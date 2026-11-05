package com.example.youshallnotsavetheprince.viewmodel

import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.TipoDefesa

data class JogoUiState(
    val partida: EstadoPartida? = null,
    val defesaSelecionada: TipoDefesa? = null,
    val mensagem: String? = null,
    val carregando: Boolean = false
)
