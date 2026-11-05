package com.example.youshallnotsavetheprince.model

data class EntradaOnda(
    val turno: Int,
    val tipo: TipoInimigo,
    val quantidade: Int = 1
)
