package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.model.*
import kotlin.math.abs

object Combate {

    fun atacar(estado: EstadoPartida): EstadoPartida {
        if (estado.inimigos.isEmpty() || estado.defesas.isEmpty()) {
            return estado
        }

        var moedasGanhas = 0
        val inimigosMutaveis = estado.inimigos.toMutableList()

        for (defesa in estado.defesas) {
            val dadosDefesa = CatalogoJogo.defesa(defesa.tipo) ?: continue
            val alcance = dadosDefesa.alcance

            val inimigosNoAlcance = inimigosMutaveis.filter { inimigo ->
                val distancia = abs(defesa.posicao.linha - inimigo.posicaoAtual.linha) +
                                abs(defesa.posicao.coluna - inimigo.posicaoAtual.coluna)
                distancia <= alcance
            }

            if (inimigosNoAlcance.isEmpty()) continue

            val alvo = inimigosNoAlcance.sortedWith(
                compareByDescending<Inimigo> { it.indiceCaminho }
                    .thenBy { it.id }
            ).firstOrNull()

            if (alvo != null) {
                val dano = dadosDefesa.danoPorNivel.getOrElse(defesa.nivel - 1) { dadosDefesa.danoPorNivel.last() }
                val indexAlvo = inimigosMutaveis.indexOfFirst { it.id == alvo.id }

                if (indexAlvo != -1) {
                    val inimigoAtualizado = inimigosMutaveis[indexAlvo]
                    val novaVida = inimigoAtualizado.vida - dano

                    if (novaVida <= 0) {
                        val dadosInimigo = CatalogoJogo.inimigo(inimigoAtualizado.tipo)
                        if (dadosInimigo != null) {
                            moedasGanhas += dadosInimigo.recompensa
                        }
                        inimigosMutaveis.removeAt(indexAlvo)
                    } else {
                        inimigosMutaveis[indexAlvo] = inimigoAtualizado.copy(vida = novaVida)
                    }
                }
            }
        }

        return estado.copy(
            moedas = estado.moedas + moedasGanhas,
            inimigos = inimigosMutaveis
        )
    }
}
