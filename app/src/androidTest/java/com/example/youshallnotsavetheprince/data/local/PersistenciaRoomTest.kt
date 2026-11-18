package com.example.youshallnotsavetheprince.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.youshallnotsavetheprince.data.repository.RoomPresetRepository
import com.example.youshallnotsavetheprince.data.repository.RoomResultadoRepository
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaRoomTest {
    private lateinit var banco: AppDatabase

    @Before
    fun preparar() {
        banco = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
    }

    @After
    fun fechar() {
        banco.close()
    }

    @Test
    fun presetFazCrudCompletoEPreservaOUltimo() = runBlocking {
        val repositorio = RoomPresetRepository(banco.presetDao())
        val inicial = (repositorio.observar().first() as EstadoConsulta.Sucesso).dados
        assertEquals(1, inicial.size)
        assertEquals("Padrão", inicial.single().nome)
        assertEquals(100, inicial.single().moedasIniciais)
        assertEquals(10, inicial.single().vidaInicialCastelo)

        val id = repositorio.inserir(PresetPartida(nome = "Difícil", moedasIniciais = 50, vidaInicialCastelo = 5)).getOrThrow()
        assertTrue(id > 0L)
        assertEquals(2, (repositorio.observar().first() as EstadoConsulta.Sucesso).dados.size)

        repositorio.atualizar(PresetPartida(id, "Difícil ajustado", 70, 6)).getOrThrow()
        val editado = (repositorio.observar().first() as EstadoConsulta.Sucesso).dados.first { it.id == id }
        assertEquals("Difícil ajustado", editado.nome)
        assertEquals(70, editado.moedasIniciais)

        repositorio.excluir(id).getOrThrow()
        assertEquals(1, (repositorio.observar().first() as EstadoConsulta.Sucesso).dados.size)
        assertTrue(repositorio.excluir(inicial.single().id).isFailure)
        assertEquals(1, banco.presetDao().contar())
    }

    @Test
    fun presetsInvalidosNaoEntramNoBanco() = runBlocking {
        val repositorio = RoomPresetRepository(banco.presetDao())
        assertTrue(repositorio.inserir(PresetPartida(nome = " ", moedasIniciais = 100, vidaInicialCastelo = 10)).isFailure)
        assertTrue(repositorio.inserir(PresetPartida(nome = "Invalido", moedasIniciais = 49, vidaInicialCastelo = 10)).isFailure)
        assertTrue(repositorio.inserir(PresetPartida(nome = "Invalido", moedasIniciais = 100, vidaInicialCastelo = 21)).isFailure)
        assertEquals(1, (repositorio.observar().first() as EstadoConsulta.Sucesso).dados.size)
    }

    @Test
    fun resultadosFazemCrudEAlteramSomenteONome() = runBlocking {
        val repositorio = RoomResultadoRepository(banco.resultadoDao())
        assertTrue((repositorio.observar().first() as EstadoConsulta.Sucesso).dados.isEmpty())

        val id = repositorio.inserir(ResultadoPartida(
            nomeJogador = "Jogador", fase = 2, vitoria = true,
            turnos = 8, pontos = 115, vidaRestante = 7,
            registradoEmMillis = 1770000000000L
        )).getOrThrow()
        assertEquals(1, (repositorio.observar().first() as EstadoConsulta.Sucesso).dados.size)

        repositorio.atualizarNome(id, "Novo Nome").getOrThrow()
        val atualizado = (repositorio.observar().first() as EstadoConsulta.Sucesso).dados.single()
        assertEquals("Novo Nome", atualizado.nomeJogador)
        assertEquals(115, atualizado.pontos)
        assertEquals(2, atualizado.fase)
        assertEquals(8, atualizado.turnos)

        repositorio.excluir(id).getOrThrow()
        assertTrue((repositorio.observar().first() as EstadoConsulta.Sucesso).dados.isEmpty())
    }

    @Test
    fun flowDeResultadosEmiteAposInserirEditarEExcluir() = runBlocking {
        val repositorio = RoomResultadoRepository(banco.resultadoDao())
        val eventos = Channel<List<ResultadoPartida>>(Channel.UNLIMITED)
        val coleta = launch {
            repositorio.observar().collect { estado ->
                if (estado is EstadoConsulta.Sucesso) eventos.send(estado.dados)
            }
        }
        try {
            assertEquals(0, withTimeout(5000) { eventos.receive() }.size)
            val id = repositorio.inserir(ResultadoPartida(
                nomeJogador = "Antes", fase = 1, vitoria = false,
                turnos = 4, pontos = 10, vidaRestante = 0,
                registradoEmMillis = 1770000000000L
            )).getOrThrow()
            assertEquals(1, withTimeout(5000) { eventos.receive() }.size)

            repositorio.atualizarNome(id, "Depois").getOrThrow()
            assertEquals("Depois", withTimeout(5000) { eventos.receive() }.single().nomeJogador)

            repositorio.excluir(id).getOrThrow()
            assertEquals(0, withTimeout(5000) { eventos.receive() }.size)
        } finally {
            coleta.cancel()
            eventos.close()
        }
    }

    @Test
    fun flowDePresetsEmiteAposInserirEditarEExcluir() = runBlocking {
        val repositorio = RoomPresetRepository(banco.presetDao())
        val eventos = Channel<List<PresetPartida>>(Channel.UNLIMITED)
        val coleta = launch {
            repositorio.observar().collect { estado ->
                if (estado is EstadoConsulta.Sucesso) eventos.send(estado.dados)
            }
        }
        try {
            assertEquals(1, withTimeout(5000) { eventos.receive() }.size)
            val id = repositorio.inserir(PresetPartida(nome = "Novo", moedasIniciais = 150, vidaInicialCastelo = 12)).getOrThrow()
            assertEquals(2, withTimeout(5000) { eventos.receive() }.size)
            repositorio.atualizar(PresetPartida(id, "Atualizado", 160, 13)).getOrThrow()
            assertEquals("Atualizado", withTimeout(5000) { eventos.receive() }.first { it.id == id }.nome)
            repositorio.excluir(id).getOrThrow()
            assertEquals(1, withTimeout(5000) { eventos.receive() }.size)
        } finally {
            coleta.cancel()
            eventos.close()
        }
    }
}
