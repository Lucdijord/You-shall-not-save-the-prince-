package com.example.youshallnotsavetheprince.contratos

interface RepositorioBonus {
    suspend fun buscarBonusMoedas(): Result<Int>
}
