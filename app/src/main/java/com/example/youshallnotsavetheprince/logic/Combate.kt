package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.Inimigo
import kotlin.math.abs

object Combate {
    fun atacar(estado: EstadoPartida): EstadoPartida {
        if (estado.defesas.isEmpty() || estado.inimigos.isEmpty()) return estado

        val inimigos = estado.inimigos.toMutableList()
        var moedasGanhas = 0
        var derrotados = 0

        for (defesa in estado.defesas) {
            val atributos = CatalogoJogo.defesa(defesa.tipo)
            val alvo = inimigos
                .filter { inimigo ->
                    val posicao = estado.config.caminho.getOrNull(inimigo.indiceCaminho)
                    posicao != null &&
                        abs(defesa.posicao.linha - posicao.linha) +
                        abs(defesa.posicao.coluna - posicao.coluna) <= atributos.alcance
                }
                .sortedWith(compareByDescending<Inimigo> { it.indiceCaminho }.thenBy { it.id })
                .firstOrNull() ?: continue

            val dano = atributos.danosPorNivel.getOrElse(defesa.nivel - 1) {
                atributos.danosPorNivel.last()
            }
            val indice = inimigos.indexOfFirst { it.id == alvo.id }
            if (indice == -1) continue

            val novaVida = alvo.vida - dano
            if (novaVida <= 0) {
                moedasGanhas += CatalogoJogo.inimigo(alvo.tipo).recompensa
                derrotados++
                inimigos.removeAt(indice)
            } else {
                inimigos[indice] = alvo.copy(vida = novaVida)
            }
        }

        return estado.copy(
            moedas = estado.moedas + moedasGanhas,
            derrotados = estado.derrotados + derrotados,
            inimigos = inimigos
        )
    }
}
