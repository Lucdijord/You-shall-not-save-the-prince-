package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.EstadoPartida
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class InimigosTest {

    private val regras = RegrasInimigoImpl()

    @Test
    fun idsContinuamEntreOndasEUsamAtributosCongelados() {
        val inicial = EstadoPartida(config = Fases.configurar(1), turno = 1)
        val primeira = regras.gerar(inicial)
        assertEquals(listOf(1, 2, 3), primeira.inimigos.map { it.id })
        assertEquals(4, primeira.proximoIdInimigo)
        assertTrue(primeira.inimigos.all { it.vida == CatalogoJogo.inimigo(it.tipo).vidaInicial })

        val segunda = regras.gerar(primeira.copy(turno = 4))
        assertEquals(listOf(1, 2, 3, 4, 5, 6), segunda.inimigos.map { it.id })
        assertEquals(7, segunda.proximoIdInimigo)

        val terceira = regras.gerar(segunda.copy(turno = 7, inimigos = emptyList()))
        assertEquals(listOf(7, 8), terceira.inimigos.map { it.id })
        assertEquals(9, terceira.proximoIdInimigo)
    }

    @Test
    fun turnoSemOndaNaoCriaInimigosNemMudaContador() {
        val estado = EstadoPartida(config = Fases.configurar(1), turno = 2)
        assertSame(estado, regras.gerar(estado))
    }

    @Test
    fun movimentoAvancaUmaCasaSemAlterarVidaOuMoedas() {
        val inicial = EstadoPartida(config = Fases.configurar(1), turno = 1, moedas = 86, vidaCastelo = 9)
        val gerado = regras.gerar(inicial)
        val movido = regras.movimentar(gerado)
        assertTrue(movido.inimigos.all { it.indiceCaminho == 1 })
        assertEquals(gerado.inimigos.map { it.vida }, movido.inimigos.map { it.vida })
        assertEquals(86, movido.moedas)
        assertEquals(9, movido.vidaCastelo)
        assertEquals(gerado.proximoIdInimigo, movido.proximoIdInimigo)
        assertTrue(gerado.inimigos.all { it.indiceCaminho == 0 })
    }

    @Test
    fun princesaNasceComVinteECincoDeVida() {
        val estado = EstadoPartida(config = Fases.configurar(5), turno = 18)
        val gerado = regras.gerar(estado)
        assertEquals(25, gerado.inimigos.single().vida)
    }
}
