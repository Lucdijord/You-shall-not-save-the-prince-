package com.example.youshallnotsavetheprince.contratos

interface RepositorioBonus {
    suspend fun buscarBonusMoedas(): Result<Int>

    val nome: String
        get() = "Maria"
}
