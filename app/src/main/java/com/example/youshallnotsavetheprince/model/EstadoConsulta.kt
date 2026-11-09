package com.example.youshallnotsavetheprince.model

sealed interface EstadoConsulta<out T> {
    data class Sucesso<T>(val dados: T) : EstadoConsulta<T>
    data class Falha(val mensagem: String) : EstadoConsulta<Nothing>
}
