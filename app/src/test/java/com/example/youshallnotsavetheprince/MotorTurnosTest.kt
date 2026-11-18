package com.example.youshallnotsavetheprince

import com.example.youshallnotsavetheprince.logic.MotorTurnos
import com.example.youshallnotsavetheprince.model.EntradaOnda
import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.Inimigo
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.model.SituacaoPartida
import com.example.youshallnotsavetheprince.model.TipoInimigo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MotorTurnosTest {

    private val preset = PresetPartida(1L, "Padrão", 100, 10)

    private fun criarMotor(
        config: com.example.youshallnotsavetheprince.model.FaseConfig = configTeste(),
        log: MutableList<String> = mutableListOf(),
        aoAtacar: (EstadoPartida) -> EstadoPartida = { it }
    ) = MotorTurnos(
        regrasDefesa = FakeRegrasDefesa(log = log, aoAtacar = aoAtacar),
        regrasInimigo = FakeRegrasInimigo(config, log)
    )

    @Test
    fun `iniciarFase usa preset mais bonus e comeca no turno zero`() {
        val motor = criarMotor()
        val estado = motor.iniciarFase(1, preset, bonusMoedas = 20)

        assertEquals(0, estado.turno)
        assertEquals(120, estado.moedas)
        assertEquals(10, estado.vidaCastelo)
        assertTrue(estado.inimigos.isEmpty())
        assertEquals(SituacaoPartida.EM_ANDAMENTO, estado.situacao)
    }

    @Test
    fun `iniciarFase limita bonus fora da faixa 0 a 30`() {
        val motor = criarMotor()
        assertEquals(130, motor.iniciarFase(1, preset, bonusMoedas = 99).moedas)
        assertEquals(100, motor.iniciarFase(1, preset, bonusMoedas = -5).moedas)
    }

    @Test
    fun `proximoTurno executa gerar, movimentar e atacar nessa ordem`() {
        val log = mutableListOf<String>()
        val motor = criarMotor(log = log)
        val inicial = motor.iniciarFase(1, preset, 0)

        val depois = motor.proximoTurno(inicial)

        assertEquals(listOf("gerar", "movimentar", "atacar"), log)
        assertEquals(1, depois.turno)
    }

    @Test
    fun `proximoTurno nao altera partida encerrada`() {
        val log = mutableListOf<String>()
        val motor = criarMotor(log = log)
        val encerrada = motor.iniciarFase(1, preset, 0).copy(situacao = SituacaoPartida.VITORIA)

        val depois = motor.proximoTurno(encerrada)

        assertEquals(encerrada, depois)
        assertTrue(log.isEmpty())
    }

    @Test
    fun `escapados descontam vida por tipo e saem da lista`() {
        val config = configTeste(entradas = listOf(EntradaOnda(10, TipoInimigo.SOLDADO)))
        val motor = criarMotor(config)
        val estado = EstadoPartida(
            config = config,
            vidaCastelo = 10,
            inimigos = listOf(
                Inimigo(1, TipoInimigo.CAVALEIRO, 10, indiceCaminho = 2),
                Inimigo(2, TipoInimigo.SOLDADO, 4, indiceCaminho = 2),
                Inimigo(3, TipoInimigo.ESCUDEIRO, 6, indiceCaminho = 0)
            )
        )

        val depois = motor.proximoTurno(estado)

        assertEquals(7, depois.vidaCastelo)
        assertEquals(listOf(3), depois.inimigos.map { it.id })
        assertEquals(SituacaoPartida.EM_ANDAMENTO, depois.situacao)
    }

    @Test
    fun `derrota por vida quando princesa escapa`() {
        val config = configTeste(entradas = listOf(EntradaOnda(10, TipoInimigo.SOLDADO)))
        val motor = criarMotor(config)
        val estado = EstadoPartida(
            config = config,
            vidaCastelo = 2,
            inimigos = listOf(Inimigo(1, TipoInimigo.PRINCESA, 25, indiceCaminho = 2))
        )

        val depois = motor.proximoTurno(estado)

        assertEquals(-1, depois.vidaCastelo)
        assertEquals(SituacaoPartida.DERROTA, depois.situacao)
    }

    @Test
    fun `derrota por vida tem prioridade sobre vitoria`() {
        val config = configTeste(entradas = emptyList())
        val motor = criarMotor(config)
        val estado = EstadoPartida(
            config = config,
            vidaCastelo = 2,
            inimigos = listOf(Inimigo(1, TipoInimigo.CAVALEIRO, 10, indiceCaminho = 2))
        )

        val depois = motor.proximoTurno(estado)

        assertEquals(0, depois.vidaCastelo)
        assertEquals(SituacaoPartida.DERROTA, depois.situacao)
    }

    @Test
    fun `vitoria quando todas as entradas passaram e nao ha inimigos`() {
        val motor = criarMotor(aoAtacar = matarTodosInimigos)
        val depois = motor.proximoTurno(motor.iniciarFase(1, preset, 0))

        assertEquals(SituacaoPartida.VITORIA, depois.situacao)
        assertEquals(1, depois.derrotados)
    }

    @Test
    fun `sem vitoria enquanto ainda ha inimigos no mapa`() {
        val motor = criarMotor()
        val depois = motor.proximoTurno(motor.iniciarFase(1, preset, 0))

        assertEquals(1, depois.inimigos.size)
        assertEquals(SituacaoPartida.EM_ANDAMENTO, depois.situacao)
    }

    @Test
    fun `derrota por limite de turnos`() {
        val config = configTeste(entradas = listOf(EntradaOnda(10, TipoInimigo.SOLDADO)), limiteTurnos = 2)
        val motor = criarMotor(config)

        val turno1 = motor.proximoTurno(motor.iniciarFase(1, preset, 0))
        assertEquals(SituacaoPartida.EM_ANDAMENTO, turno1.situacao)

        val turno2 = motor.proximoTurno(turno1)
        assertEquals(SituacaoPartida.DERROTA, turno2.situacao)
    }

    @Test
    fun `vitoria tem prioridade sobre limite de turnos`() {
        val config = configTeste(limiteTurnos = 1)
        val motor = criarMotor(config, aoAtacar = matarTodosInimigos)

        val depois = motor.proximoTurno(motor.iniciarFase(1, preset, 0))

        assertEquals(SituacaoPartida.VITORIA, depois.situacao)
    }

    @Test
    fun `calcularPontos usa derrotados e vida nao negativa`() {
        val motor = criarMotor()
        val base = motor.iniciarFase(1, preset, 0)

        assertEquals(50, motor.calcularPontos(base.copy(derrotados = 3, vidaCastelo = 4)))
        assertEquals(30, motor.calcularPontos(base.copy(derrotados = 3, vidaCastelo = -2)))
    }

    @Test
    fun `criarResultado so existe quando a partida termina`() {
        val motor = criarMotor()
        val base = motor.iniciarFase(1, preset, 0)
        assertNull(motor.criarResultado(base, "Ana", 1_000L))

        val final = base.copy(turno = 7, derrotados = 2, vidaCastelo = 6, situacao = SituacaoPartida.VITORIA)
        val resultado = motor.criarResultado(final, "Ana", 1_000L)

        assertNotNull(resultado)
        assertEquals("Ana", resultado!!.nomeJogador)
        assertEquals(1, resultado.fase)
        assertTrue(resultado.vitoria)
        assertEquals(7, resultado.turnos)
        assertEquals(50, resultado.pontos)
        assertEquals(6, resultado.vidaRestante)
    }
}
