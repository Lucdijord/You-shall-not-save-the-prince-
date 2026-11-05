package com.example.youshallnotsavetheprince.model

data class FaseConfig(
    val numero: Int,
    val linhas: Int,
    val colunas: Int,
    val caminho: List<Posicao>,
    val entradas: List<EntradaOnda>,
    val moedasIniciais: Int = 100,
    val vidaInicialCastelo: Int = 10
)
