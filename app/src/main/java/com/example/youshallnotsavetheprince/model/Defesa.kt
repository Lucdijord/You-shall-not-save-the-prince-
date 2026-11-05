package com.example.youshallnotsavetheprince.model

data class Defesa(
    val id: Int,
    val tipo: TipoDefesa,
    val posicao: Posicao,
    val nivel: Int = 1
)
