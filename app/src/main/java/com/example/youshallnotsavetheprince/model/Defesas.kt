package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.model.*

object Defesas {

    fun comprar(estado: EstadoPartida, tipo: TipoDefesa, posicao: Posicao): EstadoPartida {
        val dadosDefesa = CatalogoJogo.defesa(tipo) ?: return estado

        if (estado.moedas < dadosDefesa.preco) {
            return estado
        }

        val posicaoOcupada = estado.defesas.any { it.posicao == posicao }
        if (posicaoOcupada) {
            return estado
        }

        val estaNoCaminho = estado.faseAtual.caminho.contains(posicao)
        if (estaNoCaminho) {
            return estado
        }

        if (posicao.linha < 0 || posicao.coluna < 0) {
            return estado
        }

        val novaDefesa = Defesa(
            id = estado.proximoIdDefesa,
            tipo = tipo,
            posicao = posicao,
            nivel = 1
        )

        return estado.copy(
            moedas = estado.moedas - dadosDefesa.preco,
            defesas = estado.defesas + novaDefesa,
            proximoIdDefesa = estado.proximoIdDefesa + 1
        )
    }

    fun melhorar(estado: EstadoPartida, idDefesa: Long): EstadoPartida {
        val defesaIndex = estado.defesas.indexOfFirst { it.id == idDefesa }
        if (defesaIndex == -1) return estado

        val defesa = estado.defesas[defesaIndex]

        if (defesa.nivel >= 3) return estado

        val dadosDefesa = CatalogoJogo.defesa(defesa.tipo) ?: return estado

        val custoUpgrade = when (defesa.nivel) {
            1 -> dadosDefesa.custoUpgrade1Para2
            2 -> dadosDefesa.custoUpgrade2Para3
            else -> return estado
        }

        if (estado.moedas < custoUpgrade) {
            return estado
        }

        val defesaAtualizada = defesa.copy(nivel = defesa.nivel + 1)
        val novasDefesas = estado.defesas.toMutableList().apply {
            set(defesaIndex, defesaAtualizada)
        }

        return estado.copy(
            moedas = estado.moedas - custoUpgrade,
            defesas = novasDefesas
        )
    }
}
