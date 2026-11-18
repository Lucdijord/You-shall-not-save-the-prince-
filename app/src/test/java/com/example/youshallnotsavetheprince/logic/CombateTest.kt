package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CombateTest {
    private fun partida(defesas: List<Defesa>, inimigos: List<Inimigo>) = EstadoPartida(
        config = FaseConfig(
            1, 4, 5,
            listOf(Posicao(0, 0), Posicao(0, 1), Posicao(1, 0), Posicao(1, 1)),
            emptyList()
        ),
        defesas = defesas,
        inimigos = inimigos
    )

    @Test fun `distancia manhattan impede ataque diagonal fora do alcance`() {
        val estado = partida(
            listOf(Defesa(1, TipoDefesa.GOBLIN, Posicao(0, 0))),
            listOf(Inimigo(1, TipoInimigo.SOLDADO, 4, indiceCaminho = 3))
        )
        assertEquals(estado, Combate.atacar(estado))
    }

    @Test fun `ataque escolhe inimigo mais avancado`() {
        val estado = partida(
            listOf(Defesa(1, TipoDefesa.ESQUELETO, Posicao(0, 0))),
            listOf(
                Inimigo(1, TipoInimigo.SOLDADO, 4, indiceCaminho = 1),
                Inimigo(2, TipoInimigo.SOLDADO, 4, indiceCaminho = 2)
            )
        )
        val final = Combate.atacar(estado)
        assertEquals(4, final.inimigos.first { it.id == 1 }.vida)
        assertEquals(1, final.inimigos.first { it.id == 2 }.vida)
    }

    @Test fun `empate prioriza menor id`() {
        val estado = partida(
            listOf(Defesa(1, TipoDefesa.GOBLIN, Posicao(1, 0))),
            listOf(
                Inimigo(5, TipoInimigo.SOLDADO, 4, indiceCaminho = 2),
                Inimigo(2, TipoInimigo.SOLDADO, 4, indiceCaminho = 2)
            )
        )
        val final = Combate.atacar(estado)
        assertEquals(4, final.inimigos.first { it.id == 5 }.vida)
        assertEquals(2, final.inimigos.first { it.id == 2 }.vida)
    }

    @Test fun `recompensa e pontos contam derrotado apenas uma vez`() {
        val estado = partida(
            listOf(
                Defesa(1, TipoDefesa.OGRO, Posicao(0, 0)),
                Defesa(2, TipoDefesa.OGRO, Posicao(0, 2))
            ),
            listOf(Inimigo(1, TipoInimigo.SOLDADO, 4, indiceCaminho = 1))
        )
        val final = Combate.atacar(estado)
        assertEquals(110, final.moedas)
        assertEquals(1, final.derrotados)
        assertEquals(0, final.inimigos.size)
    }

    @Test fun `nivel tres usa dano correto e segunda torre pode atacar outro alvo`() {
        val estado = partida(
            listOf(
                Defesa(1, TipoDefesa.GOBLIN, Posicao(0, 0), nivel = 3),
                Defesa(2, TipoDefesa.GOBLIN, Posicao(1, 1), nivel = 2)
            ),
            listOf(
                Inimigo(1, TipoInimigo.SOLDADO, 4, indiceCaminho = 1),
                Inimigo(2, TipoInimigo.SOLDADO, 4, indiceCaminho = 2)
            )
        )
        val final = Combate.atacar(estado)
        assertEquals(1, final.inimigos.size)
        assertEquals(1, final.inimigos.single().vida)
        assertEquals(1, final.derrotados)
    }
}
