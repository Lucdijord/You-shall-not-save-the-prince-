package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class DefesasTest {
    private fun estado(moedas: Int = 100) = EstadoPartida(
        config = FaseConfig(
            numero = 1, linhas = 3, colunas = 4,
            caminho = listOf(Posicao(0, 0), Posicao(0, 1)), entradas = emptyList()
        ),
        moedas = moedas
    )

    @Test fun `compra desconta moedas e incrementa id`() {
        val primeiro = Defesas.comprar(estado(), TipoDefesa.GOBLIN, Posicao(1, 1))
        val segundo = Defesas.comprar(primeiro, TipoDefesa.ESQUELETO, Posicao(2, 2))
        assertEquals(20, segundo.moedas)
        assertEquals(listOf(1, 2), segundo.defesas.map { it.id })
        assertEquals(3, segundo.proximoIdDefesa)
    }

    @Test fun `compra sem dinheiro mantem estado`() {
        val atual = estado(moedas = 29)
        assertSame(atual, Defesas.comprar(atual, TipoDefesa.GOBLIN, Posicao(1, 1)))
    }

    @Test fun `compra sobre caminho ou outra torre nao e permitida`() {
        val atual = estado()
        assertSame(atual, Defesas.comprar(atual, TipoDefesa.GOBLIN, Posicao(0, 0)))
        val comDefesa = Defesas.comprar(atual, TipoDefesa.GOBLIN, Posicao(1, 1))
        assertSame(comDefesa, Defesas.comprar(comDefesa, TipoDefesa.GOBLIN, Posicao(1, 1)))
    }

    @Test fun `compra fora do mapa nao e permitida`() {
        val atual = estado()
        for (posicao in listOf(Posicao(-1, 1), Posicao(1, -1), Posicao(3, 1), Posicao(1, 4))) {
            assertSame(atual, Defesas.comprar(atual, TipoDefesa.GOBLIN, posicao))
        }
    }

    @Test fun `melhorias cobram o preco e param no nivel tres`() {
        val comprado = Defesas.comprar(estado(), TipoDefesa.GOBLIN, Posicao(1, 1))
        val nivel2 = Defesas.melhorar(comprado, 1)
        val nivel3 = Defesas.melhorar(nivel2, 1)
        assertEquals(2, nivel2.defesas.single().nivel)
        assertEquals(3, nivel3.defesas.single().nivel)
        assertEquals(25, nivel3.moedas)
        assertSame(nivel3, Defesas.melhorar(nivel3, 1))
    }

    @Test fun `melhoria sem saldo ou id valido nao altera partida`() {
        val comprado = Defesas.comprar(estado(moedas = 30), TipoDefesa.GOBLIN, Posicao(1, 1))
        assertSame(comprado, Defesas.melhorar(comprado, 1))
        assertSame(comprado, Defesas.melhorar(comprado, 9))
    }
}
