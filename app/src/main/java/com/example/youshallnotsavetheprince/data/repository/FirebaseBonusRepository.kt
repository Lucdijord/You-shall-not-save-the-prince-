package com.example.youshallnotsavetheprince.data.repository

import com.example.youshallnotsavetheprince.contratos.FonteBonusRemoto
import com.example.youshallnotsavetheprince.contratos.RepositorioBonus
import kotlinx.coroutines.CancellationException

class FirebaseBonusRepository(private val fonte: FonteBonusRemoto) : RepositorioBonus {
    override suspend fun buscarBonusMoedas(): Result<Int> = try {
        Result.success(fonte.lerBonusMoedas().coerceIn(0, 30))
    } catch (erro: CancellationException) {
        throw erro
    } catch (erro: Exception) {
        Result.failure(IllegalStateException("Não foi possível consultar o bônus online.", erro))
    }
}
