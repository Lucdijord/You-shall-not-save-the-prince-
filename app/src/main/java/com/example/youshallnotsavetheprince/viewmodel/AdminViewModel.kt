package com.example.youshallnotsavetheprince.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youshallnotsavetheprince.contratos.RepositorioPresets
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.PresetPartida
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repositorioPresets: RepositorioPresets
) : ViewModel() {

    companion object {
        private const val PIN_ADMIN_SIMULADO = "1234"
    }

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private var todosPresets: List<PresetPartida> = emptyList()

    init {
        observarPresets()
    }

    private fun observarPresets() {
        viewModelScope.launch {
            repositorioPresets.observar().collect { consulta ->
                when (consulta) {
                    is EstadoConsulta.Carregando -> {
                        _uiState.update { it.copy(carregando = true, erro = null) }
                    }
                    is EstadoConsulta.Sucesso -> {
                        todosPresets = consulta.dados
                        aplicarFiltroEOrdenacao()
                    }
                    is EstadoConsulta.Falha -> {
                        _uiState.update {
                            it.copy(
                                carregando = false,
                                erro = consulta.mensagem
                            )
                        }
                    }
                }
            }
        }
    }

    fun autenticar(pin: String) {
        if (pin == PIN_ADMIN_SIMULADO) {
            _uiState.update {
                it.copy(
                    adminAutenticado = true,
                    erro = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    adminAutenticado = false,
                    erro = "PIN administrativo inválido."
                )
            }
        }
    }

    fun sair() {
        _uiState.update {
            it.copy(
                adminAutenticado = false,
                erro = null
            )
        }
    }

    fun filtrar(termo: String) {
        _uiState.update { it.copy(filtroTexto = termo) }
        aplicarFiltroEOrdenacao()
    }

    private fun aplicarFiltroEOrdenacao() {
        val termo = _uiState.value.filtroTexto.trim().lowercase()
        val listaProcessada = todosPresets
            .filter { preset ->
                termo.isEmpty() || preset.nome.lowercase().contains(termo)
            }
            .sortedBy { it.nome.lowercase() }

        _uiState.update {
            it.copy(
                presets = listaProcessada,
                carregando = false,
                erro = null
            )
        }
    }

    fun criarPreset(nome: String, moedasIniciais: Int, vidaInicialCastelo: Int) {
        if (!validarCampos(nome, moedasIniciais, vidaInicialCastelo)) return

        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, erro = null) }
            val novoPreset = PresetPartida(
                id = 0L,
                nome = nome.trim(),
                moedasIniciais = moedasIniciais,
                vidaInicialCastelo = vidaInicialCastelo
            )
            repositorioPresets.inserir(novoPreset)
                .onSuccess {
                    _uiState.update { it.copy(carregando = false, erro = null) }
                }
                .onFailure { falha ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            erro = falha.message ?: "Erro ao criar preset."
                        )
                    }
                }
        }
    }

    fun atualizarPreset(preset: PresetPartida) {
        if (!validarCampos(preset.nome, preset.moedasIniciais, preset.vidaInicialCastelo)) return

        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, erro = null) }
            val presetLimpo = preset.copy(nome = preset.nome.trim())
            repositorioPresets.atualizar(presetLimpo)
                .onSuccess {
                    _uiState.update { it.copy(carregando = false, erro = null) }
                }
                .onFailure { falha ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            erro = falha.message ?: "Erro ao atualizar preset."
                        )
                    }
                }
        }
    }

    fun excluirPreset(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, erro = null) }
            repositorioPresets.excluir(id)
                .onSuccess {
                    _uiState.update { it.copy(carregando = false, erro = null) }
                }
                .onFailure { falha ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            erro = falha.message ?: "Não foi possível excluir o preset."
                        )
                    }
                }
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }

    private fun validarCampos(nome: String, moedas: Int, vida: Int): Boolean {
        if (nome.isBlank()) {
            _uiState.update { it.copy(erro = "O nome do preset não pode ser vazio.") }
            return false
        }
        if (moedas !in 50..250) {
            _uiState.update { it.copy(erro = "As moedas iniciais devem estar entre 50 e 250.") }
            return false
        }
        if (vida !in 5..20) {
            _uiState.update { it.copy(erro = "A vida do castelo deve estar entre 5 e 20.") }
            return false
        }
        return true
    }
}
