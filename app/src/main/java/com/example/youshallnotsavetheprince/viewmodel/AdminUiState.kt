package com.example.youshallnotsavetheprince.viewmodel

import com.example.youshallnotsavetheprince.model.PresetPartida

data class AdminUiState(
    val adminAutenticado: Boolean = false,
    val presetsVisiveis: List<PresetPartida> = emptyList(),
    val filtro: String = "",
    val carregando: Boolean = false,
    val mensagem: String? = null
)
