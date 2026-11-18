package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.contratos.RegrasDefesa
import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.Defesa
import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.TipoDefesa

object Defesas : RegrasDefesa {
    override fun comprar(estado: EstadoPartida, tipo: TipoDefesa, posicao: Posicao): EstadoPartida {
        val atributos = CatalogoJogo.defesa(tipo)
        if (estado.moedas < atributos.preco) return estado
        if (posicao.linha !in 0 until estado.config.linhas ||
            posicao.coluna !in 0 until estado.config.colunas) return estado
        if (posicao in estado.config.caminho) return estado
        if (estado.defesas.any { it.posicao == posicao }) return estado

        val novaDefesa = Defesa(
            id = estado.proximoIdDefesa,
            tipo = tipo,
            posicao = posicao
        )
        return estado.copy(
            moedas = estado.moedas - atributos.preco,
            defesas = estado.defesas + novaDefesa,
            proximoIdDefesa = estado.proximoIdDefesa + 1
        )
    }

    override fun melhorar(estado: EstadoPartida, idDefesa: Int): EstadoPartida {
        val indice = estado.defesas.indexOfFirst { it.id == idDefesa }
        if (indice == -1) return estado

        val defesa = estado.defesas[indice]
        if (defesa.nivel !in 1..2) return estado
        val atributos = CatalogoJogo.defesa(defesa.tipo)
        val custo = atributos.custosMelhoria[defesa.nivel - 1]
        if (estado.moedas < custo) return estado

        val atualizadas = estado.defesas.toMutableList()
        atualizadas[indice] = defesa.copy(nivel = defesa.nivel + 1)
        return estado.copy(moedas = estado.moedas - custo, defesas = atualizadas)
    }

    override fun atacar(estado: EstadoPartida): EstadoPartida = Combate.atacar(estado)
}
