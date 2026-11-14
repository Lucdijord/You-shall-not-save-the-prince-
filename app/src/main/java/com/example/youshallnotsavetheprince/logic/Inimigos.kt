package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.contratos.RegrasInimigo
import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.FaseConfig
import com.example.youshallnotsavetheprince.model.Inimigo

class RegrasInimigoImpl : RegrasInimigo {

    override fun configurarFase(numero: Int): FaseConfig = Fases.configurar(numero)

    override fun gerar(estado: EstadoPartida): EstadoPartida {
        var proximoId = estado.proximoIdInimigo
        val novos = mutableListOf<Inimigo>()

        estado.config.entradas.filter { it.turno == estado.turno }.forEach { entrada ->
            repeat(entrada.quantidade) {
                novos += Inimigo(
                    id = proximoId++,
                    tipo = entrada.tipo,
                    vida = CatalogoJogo.inimigo(entrada.tipo).vidaInicial
                )
            }
        }

        if (novos.isEmpty()) return estado
        return estado.copy(
            inimigos = estado.inimigos + novos,
            proximoIdInimigo = proximoId
        )
    }

    override fun movimentar(estado: EstadoPartida): EstadoPartida = estado.copy(
        inimigos = estado.inimigos.map { inimigo ->
            inimigo.copy(indiceCaminho = inimigo.indiceCaminho + 1)
        }
    )
}
