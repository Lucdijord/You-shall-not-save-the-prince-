package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.model.EntradaOnda
import com.example.youshallnotsavetheprince.model.FaseConfig
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.TipoInimigo.CAVALEIRO
import com.example.youshallnotsavetheprince.model.TipoInimigo.CORREDOR
import com.example.youshallnotsavetheprince.model.TipoInimigo.ESCUDEIRO
import com.example.youshallnotsavetheprince.model.TipoInimigo.PRINCESA
import com.example.youshallnotsavetheprince.model.TipoInimigo.SOLDADO

object Fases {

    const val TOTAL_FASES = 5

    private fun caminho(vararg pares: Pair<Int, Int>): List<Posicao> =
        pares.map { Posicao(it.first, it.second) }

    private val fase1 = FaseConfig(
        numero = 1,
        linhas = 5,
        colunas = 7,
        caminho = caminho(
            2 to 0, 2 to 1, 2 to 2, 2 to 3, 2 to 4, 2 to 5, 2 to 6
        ),
        entradas = listOf(
            EntradaOnda(1, SOLDADO, 3),
            EntradaOnda(4, SOLDADO, 3),
            EntradaOnda(7, CORREDOR, 2)
        ),
        moedasIniciais = 100,
        vidaInicialCastelo = 10,
        limiteTurnos = 20
    )

    private val fase2 = FaseConfig(
        numero = 2,
        linhas = 5,
        colunas = 7,
        caminho = caminho(
            0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2, 2 to 3,
            2 to 4, 3 to 4, 4 to 4, 4 to 5, 4 to 6
        ),
        entradas = listOf(
            EntradaOnda(1, SOLDADO, 3),
            EntradaOnda(3, CORREDOR, 2),
            EntradaOnda(5, ESCUDEIRO, 2),
            EntradaOnda(8, SOLDADO, 4)
        ),
        moedasIniciais = 110,
        vidaInicialCastelo = 10,
        limiteTurnos = 28
    )

    private val fase3 = FaseConfig(
        numero = 3,
        linhas = 6,
        colunas = 8,
        caminho = caminho(
            1 to 0, 1 to 1, 1 to 2, 1 to 3, 2 to 3, 3 to 3,
            3 to 4, 3 to 5, 3 to 6, 4 to 6, 5 to 6, 5 to 7
        ),
        entradas = listOf(
            EntradaOnda(1, SOLDADO, 4),
            EntradaOnda(3, ESCUDEIRO, 2),
            EntradaOnda(5, CORREDOR, 3),
            EntradaOnda(8, ESCUDEIRO, 3),
            EntradaOnda(11, CAVALEIRO, 1)
        ),
        moedasIniciais = 120,
        vidaInicialCastelo = 12,
        limiteTurnos = 32
    )

    private val fase4 = FaseConfig(
        numero = 4,
        linhas = 6,
        colunas = 8,
        caminho = caminho(
            0 to 1, 1 to 1, 2 to 1, 2 to 2, 2 to 3, 1 to 3, 0 to 3, 0 to 4,
            0 to 5, 1 to 5, 2 to 5, 3 to 5, 4 to 5, 4 to 6, 4 to 7
        ),
        entradas = listOf(
            EntradaOnda(1, SOLDADO, 4),
            EntradaOnda(3, CORREDOR, 3),
            EntradaOnda(5, ESCUDEIRO, 3),
            EntradaOnda(8, CAVALEIRO, 2),
            EntradaOnda(11, ESCUDEIRO, 3),
            EntradaOnda(14, CAVALEIRO, 2)
        ),
        moedasIniciais = 130,
        vidaInicialCastelo = 12,
        limiteTurnos = 40
    )

    private val fase5 = FaseConfig(
        numero = 5,
        linhas = 7,
        colunas = 9,
        caminho = caminho(
            3 to 0, 3 to 1, 3 to 2, 2 to 2, 1 to 2, 1 to 3, 1 to 4, 2 to 4, 3 to 4,
            4 to 4, 5 to 4, 5 to 5, 5 to 6, 4 to 6, 3 to 6, 3 to 7, 3 to 8
        ),
        entradas = listOf(
            EntradaOnda(1, SOLDADO, 5),
            EntradaOnda(3, ESCUDEIRO, 3),
            EntradaOnda(5, CORREDOR, 4),
            EntradaOnda(8, CAVALEIRO, 2),
            EntradaOnda(11, ESCUDEIRO, 4),
            EntradaOnda(14, CAVALEIRO, 3),
            EntradaOnda(18, PRINCESA, 1)
        ),
        moedasIniciais = 150,
        vidaInicialCastelo = 15,
        limiteTurnos = 45
    )

    val todas: List<FaseConfig> = listOf(fase1, fase2, fase3, fase4, fase5)

    fun configurar(numero: Int): FaseConfig {
        require(numero in 1..TOTAL_FASES) { "Fase inexistente: $numero" }
        return todas[numero - 1]
    }
}