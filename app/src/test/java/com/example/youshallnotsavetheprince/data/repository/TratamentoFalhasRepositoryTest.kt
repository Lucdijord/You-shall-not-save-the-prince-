package com.example.youshallnotsavetheprince.data.repository

import com.example.youshallnotsavetheprince.contratos.FonteBonusRemoto
import com.example.youshallnotsavetheprince.data.local.PresetDao
import com.example.youshallnotsavetheprince.data.local.PresetPartidaEntity
import com.example.youshallnotsavetheprince.data.local.ResultadoDao
import com.example.youshallnotsavetheprince.data.local.ResultadoPartidaEntity
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class TratamentoFalhasRepositoryTest {
    @Test
    fun firebaseUsaFonteCongeladaELimitaBonus() = runBlocking {
        val repo = FirebaseBonusRepository(object : FonteBonusRemoto {
            override suspend fun lerBonusMoedas() = 75
        })
        assertEquals(30, repo.buscarBonusMoedas().getOrThrow())

        val negativo = FirebaseBonusRepository(object : FonteBonusRemoto {
            override suspend fun lerBonusMoedas() = -5
        })
        assertEquals(0, negativo.buscarBonusMoedas().getOrThrow())
    }

    @Test
    fun falhaFirebaseViraResultFailure() = runBlocking {
        val repo = FirebaseBonusRepository(object : FonteBonusRemoto {
            override suspend fun lerBonusMoedas(): Int = error("segredo técnico")
        })
        val resposta = repo.buscarBonusMoedas()
        assertTrue(resposta.isFailure)
        assertEquals("Não foi possível consultar o bônus online.", resposta.exceptionOrNull()?.message)
    }

    @Test
    fun erroRoomNoPresetViraFalhaDoFlowEResultFailure() = runBlocking {
        val repo = RoomPresetRepository(object : PresetDao() {
            override fun observarTodos(): Flow<List<PresetPartidaEntity>> = flow { error("falha sqlite") }
            override suspend fun inserir(preset: PresetPartidaEntity): Long = error("falha sqlite")
            override suspend fun atualizar(preset: PresetPartidaEntity): Int = error("falha sqlite")
            override suspend fun excluir(id: Long): Int = error("falha sqlite")
            override suspend fun contar() = 1
            override suspend fun buscarId(id: Long): Long? = id
        })
        assertTrue(repo.observar().first() is EstadoConsulta.Falha)
        val resposta = repo.inserir(PresetPartida(nome = "Teste", moedasIniciais = 100, vidaInicialCastelo = 10))
        assertTrue(resposta.isFailure)
        assertEquals("Não foi possível cadastrar o preset.", resposta.exceptionOrNull()?.message)
    }

    @Test
    fun erroRoomNoResultadoViraFalhaDoFlowEResultFailure() = runBlocking {
        val repo = RoomResultadoRepository(object : ResultadoDao {
            override fun observarTodos(): Flow<List<ResultadoPartidaEntity>> = flow { error("falha sqlite") }
            override suspend fun inserir(resultado: ResultadoPartidaEntity): Long = error("falha sqlite")
            override suspend fun atualizar(id: Long, novoNome: String): Int = error("falha sqlite")
            override suspend fun excluir(id: Long): Int = error("falha sqlite")
        })
        assertTrue(repo.observar().first() is EstadoConsulta.Falha)
        val resposta = repo.inserir(ResultadoPartida(
            nomeJogador = "Teste", fase = 1, vitoria = true,
            turnos = 5, pontos = 10, vidaRestante = 5, registradoEmMillis = 1L
        ))
        assertTrue(resposta.isFailure)
        assertEquals("Não foi possível salvar o resultado.", resposta.exceptionOrNull()?.message)
    }
}
