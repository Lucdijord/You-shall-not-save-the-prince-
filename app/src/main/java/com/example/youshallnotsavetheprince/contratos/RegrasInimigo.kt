package com.example.youshallnotsavetheprince.contratos

import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.FaseConfig

interface RegrasInimigo {
    fun configurarFase(numero: Int): FaseConfig
    fun gerar(estado: EstadoPartida): EstadoPartida
    fun movimentar(estado: EstadoPartida): EstadoPartida
}
