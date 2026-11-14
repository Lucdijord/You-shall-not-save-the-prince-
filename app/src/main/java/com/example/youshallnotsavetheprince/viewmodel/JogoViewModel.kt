package com.example.youshallnotsavetheprince.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youshallnotsavetheprince.contratos.RepositorioBonus
import com.example.youshallnotsavetheprince.contratos.RepositorioPresets
import com.example.youshallnotsavetheprince.contratos.RepositorioResultados
import com.example.youshallnotsavetheprince.logic.MotorTurnos
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import com.example.youshallnotsavetheprince.model.SituacaoPartida
import com.example.youshallnotsavetheprince.model.TipoDefesa
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JogoViewModel(
    private val motor: MotorTurnos,
    private val repositorioPresets: RepositorioPresets,
    private val repositorioResultados: RepositorioResultados,
    private val repositorioBonus: RepositorioBonus,
    private val relogio: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    private val _uiState = MutableStateFlow(JogoUiState(carregando = true))
    val uiState: StateFlow<JogoUiState> = _uiState.asStateFlow()

    private var nomeJogador: String = ""
    private var resultadoRegistrado = false
    private var iniciandoPartida = false

    init {
        observarPresets()
        observarResultados()
    }

    private fun observarPresets() {
        viewModelScope.launch {
            repositorioPresets.observar().collect { consulta ->
                when (consulta) {
                    is EstadoConsulta.Sucesso -> _uiState.update {
                        it.copy(presets = consulta.dados, carregando = false)
                    }
                    is EstadoConsulta.Falha -> _uiState.update {
                        it.copy(carregando = false, mensagem = consulta.mensagem)
                    }
                }
            }
        }
    }

    private fun observarResultados() {
        viewModelScope.launch {
            repositorioResultados.observar().collect { consulta ->
                when (consulta) {
                    is EstadoConsulta.Sucesso -> {
                        val historico = consulta.dados
                        _uiState.update {
                            it.copy(
                                resultados = montarRanking(historico),
                                fasesLiberadas = calcularFasesLiberadas(historico),
                                carregando = false
                            )
                        }
                    }
                    is EstadoConsulta.Falha -> _uiState.update {
                        it.copy(carregando = false, mensagem = consulta.mensagem)
                    }
                }
            }
        }
    }


    private fun calcularFasesLiberadas(historico: List<ResultadoPartida>): List<Int> {
        val liberadasPorVitoria = historico
            .filter { it.vitoria }
            .map { it.fase + 1 }
        return (listOf(PRIMEIRA_FASE) + liberadasPorVitoria)
            .filter { it in PRIMEIRA_FASE..TOTAL_FASES }
            .distinct()
            .sorted()
    }


    private fun montarRanking(historico: List<ResultadoPartida>): List<ResultadoPartida> =
        historico
            .map { it }
            .sortedBy { -it.pontos }


    fun iniciarPartida(fase: Int, presetId: Long, nomeJogador: String) {
        if (iniciandoPartida) return
        val nome = nomeJogador.trim()
        val atual = _uiState.value

        if (nome.isEmpty()) {
            exibirMensagem(MSG_NOME_VAZIO)
            return
        }
        if (fase !in atual.fasesLiberadas) {
            exibirMensagem(MSG_FASE_BLOQUEADA)
            return
        }
        val preset = atual.presets.firstOrNull { it.id == presetId } ?: atual.presets.firstOrNull()
        if (preset == null) {
            exibirMensagem(MSG_SEM_PRESET)
            return
        }

        iniciandoPartida = true
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(carregando = true, mensagem = null) }
                val bonus = carregarBonus()
                val partida = motor.iniciarFase(fase, preset, bonus.moedas)
                this@JogoViewModel.nomeJogador = nome
                resultadoRegistrado = false
                _uiState.update {
                    it.copy(
                        partida = partida,
                        resultadoAtual = null,
                        defesaSelecionada = null,
                        bonusMoedasAplicado = bonus.moedas,
                        mensagem = bonus.mensagem,
                        carregando = false
                    )
                }
            } finally {
                iniciandoPartida = false
            }
        }
    }


    private suspend fun carregarBonus(): BonusCarregado {
        val resultado = try {
            repositorioBonus.buscarBonusMoedas()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
        return resultado.fold(
            onSuccess = { valor ->
                val bonus = valor.coerceIn(MotorTurnos.BONUS_MINIMO, MotorTurnos.BONUS_MAXIMO)
                BonusCarregado(bonus, "Bônus online: +$bonus moedas")
            },
            onFailure = { BonusCarregado(0, "Sem conexão: bônus padrão +0") }
        )
    }

    fun selecionarDefesa(tipo: TipoDefesa?) {
        _uiState.update { it.copy(defesaSelecionada = tipo) }
    }

    fun comprar(tipo: TipoDefesa, posicao: Posicao) {
        val atual = partidaEmAndamento() ?: return
        val nova = motor.comprar(atual, tipo, posicao)
        _uiState.update {
            it.copy(
                partida = nova,
                mensagem = if (nova == atual) MSG_COMPRA_INVALIDA else null
            )
        }
    }

    fun melhorar(idDefesa: Int) {
        val atual = partidaEmAndamento() ?: return
        val nova = motor.melhorar(atual, idDefesa)
        _uiState.update {
            it.copy(
                partida = nova,
                mensagem = if (nova == atual) MSG_MELHORIA_INVALIDA else null
            )
        }
    }

    fun proximoTurno() {
        val atual = partidaEmAndamento() ?: return
        val nova = motor.proximoTurno(atual)
        _uiState.update { it.copy(partida = nova) }
        if (nova.situacao != SituacaoPartida.EM_ANDAMENTO) {
            registrarResultado(nova)
        }
    }


    private fun registrarResultado(finalizada: EstadoPartida) {
        if (resultadoRegistrado) return
        resultadoRegistrado = true

        val resultado = motor.criarResultado(finalizada, nomeJogador, relogio()) ?: return
        _uiState.update { it.copy(resultadoAtual = resultado) }

        viewModelScope.launch {
            repositorioResultados.inserir(resultado)
                .onSuccess { idGerado ->
                    _uiState.update { atual ->
                        if (atual.resultadoAtual === resultado) {
                            atual.copy(resultadoAtual = resultado.copy(id = idGerado))
                        } else {
                            atual
                        }
                    }
                }
                .onFailure { exibirMensagem(MSG_ERRO_SALVAR) }
        }
    }


    fun sairDaPartida() {
        _uiState.update {
            it.copy(
                partida = null,
                resultadoAtual = null,
                defesaSelecionada = null,
                bonusMoedasAplicado = 0,
                mensagem = null
            )
        }
    }

    fun renomearResultado(id: Long, novoNome: String) {
        val nome = novoNome.trim()
        if (nome.isEmpty()) {
            exibirMensagem(MSG_NOME_VAZIO)
            return
        }
        viewModelScope.launch {
            repositorioResultados.atualizarNome(id, nome)
                .onFailure { exibirMensagem(MSG_ERRO_RENOMEAR) }
        }
    }

    fun excluirResultado(id: Long) {
        viewModelScope.launch {
            repositorioResultados.excluir(id)
                .onFailure { exibirMensagem(MSG_ERRO_EXCLUIR) }
        }
    }

    fun limparMensagem() {
        _uiState.update { it.copy(mensagem = null) }
    }

    private fun partidaEmAndamento(): EstadoPartida? =
        _uiState.value.partida?.takeIf { it.situacao == SituacaoPartida.EM_ANDAMENTO }

    private fun exibirMensagem(texto: String) {
        _uiState.update { it.copy(mensagem = texto) }
    }

    private data class BonusCarregado(val moedas: Int, val mensagem: String)

    private companion object {
        const val PRIMEIRA_FASE = 1
        const val TOTAL_FASES = 5
        const val MSG_NOME_VAZIO = "Informe um nome para continuar."
        const val MSG_FASE_BLOQUEADA = "Esta fase ainda está bloqueada. Vença a fase anterior."
        const val MSG_SEM_PRESET = "Nenhum preset disponível no momento. Tente novamente."
        const val MSG_COMPRA_INVALIDA = "Não foi possível comprar: verifique moedas e posição."
        const val MSG_MELHORIA_INVALIDA = "Não foi possível melhorar esta defesa."
        const val MSG_ERRO_SALVAR = "Não foi possível salvar o resultado no histórico."
        const val MSG_ERRO_RENOMEAR = "Não foi possível renomear o resultado."
        const val MSG_ERRO_EXCLUIR = "Não foi possível excluir o resultado."
    }
}
