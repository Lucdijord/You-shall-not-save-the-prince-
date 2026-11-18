package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.model.TipoInimigo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FasesTest {

    @Test
    fun existemCincoFasesComCaminhosValidos() {
        assertEquals(5, Fases.todas.size)
        Fases.todas.forEachIndexed { indice, fase ->
            assertEquals(indice + 1, fase.numero)
            assertEquals(fase, Fases.configurar(fase.numero))
            assertTrue(fase.caminho.isNotEmpty())
            assertEquals(fase.caminho.size, fase.caminho.distinct().size)
            assertTrue(fase.entradas.isNotEmpty())
            assertTrue(fase.entradas.all { it.turno > 0 && it.quantidade > 0 })
            assertTrue(fase.entradas.all { it.turno <= fase.limiteTurnos })
            assertTrue(fase.caminho.all { it.linha in 0 until fase.linhas && it.coluna in 0 until fase.colunas })
            fase.caminho.zipWithNext().forEach { (atual, seguinte) ->
                val distancia = kotlin.math.abs(atual.linha - seguinte.linha) +
                    kotlin.math.abs(atual.coluna - seguinte.coluna)
                assertEquals(1, distancia)
            }
        }
    }

    @Test
    fun princesaSoApareceNaUltimaFase() {
        Fases.todas.take(4).forEach { fase ->
            assertFalse(fase.entradas.any { it.tipo == TipoInimigo.PRINCESA })
        }
        val chefes = Fases.configurar(5).entradas.filter { it.tipo == TipoInimigo.PRINCESA }
        assertEquals(1, chefes.size)
        assertEquals(1, chefes.single().quantidade)
    }

    @Test(expected = IllegalArgumentException::class)
    fun faseInexistenteNaoEPermitida() {
        Fases.configurar(6)
    }
}
