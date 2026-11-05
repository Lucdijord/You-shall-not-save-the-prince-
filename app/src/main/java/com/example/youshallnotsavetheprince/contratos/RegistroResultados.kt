package com.example.youshallnotsavetheprince.contratos

import com.example.youshallnotsavetheprince.model.ResultadoPartida

interface RegistroResultados {
    suspend fun registrar(resultado: ResultadoPartida)
}
