package com.example.youshallnotsavetheprince.data.remote

import com.example.youshallnotsavetheprince.data.repository.FirebaseBonusRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseBonusRemoteDataSourceTest {

    private class ProvedorFalso(var valor: Long) : ProvedorConfigRemota {
        var padroes: Map<String, Any> = emptyMap()
        var chamadas = 0
        var falhar = false

        override suspend fun definirPadroes(padroes: Map<String, Any>) {
            this.padroes = padroes
        }

        override suspend fun buscarEAtivar() {
            chamadas++
            if (falhar) error("sem rede")
        }

        override fun lerLong(chave: String): Long {
            assertEquals("bonus_moedas_iniciais", chave)
            return valor
        }
    }

    @Test
    fun consultaChaveCorretaEConfiguraPadrao() = runTest {
        val provedor = ProvedorFalso(10)
        val fonte = FirebaseBonusRemoteDataSource(provedor)
        assertEquals(10, fonte.lerBonusMoedas())
        assertEquals(mapOf("bonus_moedas_iniciais" to 0L), provedor.padroes)
        assertEquals(1, provedor.chamadas)
    }

    @Test
    fun bonusFicaEntreZeroETrinta() = runTest {
        assertEquals(0, FirebaseBonusRemoteDataSource(ProvedorFalso(-9)).lerBonusMoedas())
        assertEquals(30, FirebaseBonusRemoteDataSource(ProvedorFalso(90)).lerBonusMoedas())
    }

    @Test
    fun conversaComRepositoryDaPessoaCincoEPropagaFalhas() = runTest {
        val provedor = ProvedorFalso(18)
        val repositorio = FirebaseBonusRepository(FirebaseBonusRemoteDataSource(provedor))
        assertEquals(18, repositorio.buscarBonusMoedas().getOrThrow())

        provedor.falhar = true
        val falha = repositorio.buscarBonusMoedas()
        assertTrue(falha.isFailure)
        assertEquals("Não foi possível consultar o bônus online.", falha.exceptionOrNull()?.message)
    }
}
