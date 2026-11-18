package com.example.youshallnotsavetheprince.model

data class ResultadoPartida(
    val id: Long = 0L,
    val nomeJogador: String,
    val fase: Int,
    val vitoria: Boolean,
    val turnos: Int,
    val pontos: Int,
    val vidaRestante: Int,
    val registradoEmMillis: Long
)
