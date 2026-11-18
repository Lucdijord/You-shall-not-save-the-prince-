package com.example.youshallnotsavetheprince.viewmodel

import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import com.example.youshallnotsavetheprince.model.TipoDefesa

data class JogoUiState(
    val partida: EstadoPartida? = null,
    val presets: List<PresetPartida> = emptyList(),
    val resultados: List<ResultadoPartida> = emptyList(),
    val fasesLiberadas: List<Int> = listOf(1),
    val resultadoAtual: ResultadoPartida? = null,
    val defesaSelecionada: TipoDefesa? = null,
    val bonusMoedasAplicado: Int = 0,
    val mensagem: String? = null,
    val carregando: Boolean = false
)
