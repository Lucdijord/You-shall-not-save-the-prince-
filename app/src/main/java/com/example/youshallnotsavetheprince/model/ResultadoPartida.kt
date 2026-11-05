package com.example.youshallnotsavetheprince.model

data class ResultadoPartida(
    val fase: Int,
    val vitoria: Boolean,
    val turnos: Int,
    val pontos: Int,
    val vidaRestante: Int
)
