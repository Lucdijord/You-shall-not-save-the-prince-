package com.example.youshallnotsavetheprince.contratos

import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.TipoDefesa

interface RegrasDefesa {
    fun comprar(estado: EstadoPartida, tipo: TipoDefesa, posicao: Posicao): EstadoPartida
    fun melhorar(estado: EstadoPartida, idDefesa: Int): EstadoPartida
    fun atacar(estado: EstadoPartida): EstadoPartida
}
