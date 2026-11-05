package com.example.youshallnotsavetheprince.model

data class Inimigo(
    val id: Int,
    val tipo: TipoInimigo,
    val vida: Int,
    val indiceCaminho: Int = 0
)
