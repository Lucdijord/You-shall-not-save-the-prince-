package com.example.youshallnotsavetheprince.data.remote

import com.example.youshallnotsavetheprince.contratos.FonteBonusRemoto
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface ProvedorConfigRemota {
    suspend fun definirPadroes(padroes: Map<String, Any>)
    suspend fun buscarEAtivar()
    fun lerLong(chave: String): Long
}

class ProvedorFirebaseConfig(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
) : ProvedorConfigRemota {

    override suspend fun definirPadroes(padroes: Map<String, Any>) {
        remoteConfig.setDefaultsAsync(padroes).aguardar()
    }

    override suspend fun buscarEAtivar() {
        remoteConfig.fetchAndActivate().aguardar()
    }

    override fun lerLong(chave: String): Long = remoteConfig.getLong(chave)
}

private suspend fun <T> Task<T>.aguardar(): Unit = suspendCancellableCoroutine { continuacao ->
    addOnCompleteListener { tarefa ->
        when {
            tarefa.isCanceled -> continuacao.cancel()
            tarefa.isSuccessful -> continuacao.resume(Unit)
            else -> continuacao.resumeWithException(
                tarefa.exception ?: IllegalStateException("Falha na consulta ao Firebase")
            )
        }
    }
}

class FirebaseBonusRemoteDataSource(
    private val provedor: ProvedorConfigRemota = ProvedorFirebaseConfig()
) : FonteBonusRemoto {

    override suspend fun lerBonusMoedas(): Int {
        provedor.definirPadroes(mapOf(CHAVE_BONUS to BONUS_PADRAO))
        provedor.buscarEAtivar()
        return provedor.lerLong(CHAVE_BONUS).coerceIn(BONUS_MIN, BONUS_MAX).toInt()
    }

    companion object {
        const val CHAVE_BONUS = "bonus_moedas_iniciais"
        const val BONUS_PADRAO = 0L
        const val BONUS_MIN = 0L
        const val BONUS_MAX = 30L
    }
}
