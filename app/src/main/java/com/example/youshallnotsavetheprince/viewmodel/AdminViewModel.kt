package com.example.youshallnotsavetheprince.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youshallnotsavetheprince.contratos.RepositorioPresets
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.PresetPartida
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(private val repositorioPresets: RepositorioPresets) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private var todosPresets = emptyList<PresetPartida>()

    init {
        observarPresets()
    }

    private fun observarPresets() {
        viewModelScope.launch {
            try {
                repositorioPresets.observar().collect { consulta ->
                    when (consulta) {
                        is EstadoConsulta.Sucesso -> {
                            todosPresets = consulta.dados
                            aplicarFiltro()
                        }
                        is EstadoConsulta.Falha -> {
                            _uiState.update {
                                it.copy(carregando = false, mensagem = consulta.mensagem)
                            }
                        }
                    }
                }
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: Exception) {
                _uiState.update {
                    it.copy(carregando = false, mensagem = "Não foi possível carregar os presets.")
                }
            }
        }
    }

    fun autenticar(pin: String) {
        val autenticado = pin == "1234"
        _uiState.update {
            it.copy(
                adminAutenticado = autenticado,
                mensagem = if (autenticado) null else "PIN administrativo inválido."
            )
        }
    }

    fun sair() {
        _uiState.update { it.copy(adminAutenticado = false, mensagem = null) }
    }

    fun filtrar(termo: String) {
        _uiState.update { it.copy(filtro = termo) }
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val termo = _uiState.value.filtro.trim()
        val filtrados = todosPresets
            .filter { it.nome.contains(termo, ignoreCase = true) }
            .sortedBy { it.nome.lowercase() }
        _uiState.update { it.copy(presetsVisiveis = filtrados, carregando = false) }
    }

    fun criarPreset(nome: String, moedasIniciais: Int, vidaInicialCastelo: Int) {
        if (!autorizar() || !validarCampos(nome, moedasIniciais, vidaInicialCastelo)) return
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, mensagem = null) }
            val preset = PresetPartida(
                nome = nome.trim(),
                moedasIniciais = moedasIniciais,
                vidaInicialCastelo = vidaInicialCastelo
            )
            concluirOperacao(repositorioPresets.inserir(preset), "Não foi possível criar o preset.")
        }
    }

    fun atualizarPreset(preset: PresetPartida) {
        if (!autorizar() || !validarCampos(preset.nome, preset.moedasIniciais, preset.vidaInicialCastelo)) return
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, mensagem = null) }
            concluirOperacao(
                repositorioPresets.atualizar(preset.copy(nome = preset.nome.trim())),
                "Não foi possível atualizar o preset."
            )
        }
    }

    fun excluirPreset(id: Long) {
        if (!autorizar()) return
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, mensagem = null) }
            concluirOperacao(repositorioPresets.excluir(id), "Não foi possível excluir o preset.")
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(mensagem = null) }
    }

    private fun autorizar(): Boolean {
        if (_uiState.value.adminAutenticado) return true
        _uiState.update { it.copy(mensagem = "Entre como administrador para gerenciar presets.") }
        return false
    }

    private fun validarCampos(nome: String, moedas: Int, vida: Int): Boolean {
        val mensagem = when {
            nome.isBlank() -> "O nome do preset não pode ser vazio."
            moedas !in 50..250 -> "As moedas iniciais devem estar entre 50 e 250."
            vida !in 5..20 -> "A vida do castelo deve estar entre 5 e 20."
            else -> return true
        }
        _uiState.update { it.copy(mensagem = mensagem, carregando = false) }
        return false
    }

    private fun concluirOperacao(resultado: Result<*>, mensagemPadrao: String) {
        _uiState.update {
            it.copy(
                carregando = false,
                mensagem = resultado.exceptionOrNull()?.message ?: if (resultado.isFailure) mensagemPadrao else null
            )
        }
    }
}
