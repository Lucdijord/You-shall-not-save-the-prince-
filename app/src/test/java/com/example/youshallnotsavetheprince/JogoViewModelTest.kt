package com.example.youshallnotsavetheprince

import com.example.youshallnotsavetheprince.logic.MotorTurnos
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.SituacaoPartida
import com.example.youshallnotsavetheprince.model.TipoDefesa
import com.example.youshallnotsavetheprince.viewmodel.JogoViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JogoViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun criarVm(
        presets: FakeRepositorioPresets = FakeRepositorioPresets(),
        resultados: FakeRepositorioResultados = FakeRepositorioResultados(),
        bonus: FakeRepositorioBonus = FakeRepositorioBonus(),
        defesa: FakeRegrasDefesa = FakeRegrasDefesa()
    ): JogoViewModel {
        val motor = MotorTurnos(defesa, FakeRegrasInimigo(configTeste()))
        return JogoViewModel(motor, presets, resultados, bonus, relogio = { 1_000L })
    }

    @Test
    fun `fase 1 sempre liberada e vitoria libera a seguinte`() {
        val resultados = FakeRepositorioResultados()
        val vm = criarVm(resultados = resultados)
        assertEquals(listOf(1), vm.uiState.value.fasesLiberadas)

        resultados.fluxo.value = EstadoConsulta.Sucesso(
            listOf(resultadoTeste(1, fase = 1, vitoria = true, pontos = 50))
        )
        assertEquals(listOf(1, 2), vm.uiState.value.fasesLiberadas)
    }

    @Test
    fun `derrota nao libera a proxima fase e vitoria da fase 5 nao passa de 5`() {
        val resultados = FakeRepositorioResultados(
            listOf(
                resultadoTeste(1, fase = 1, vitoria = true, pontos = 10),
                resultadoTeste(2, fase = 2, vitoria = false, pontos = 5),
                resultadoTeste(3, fase = 5, vitoria = true, pontos = 90)
            )
        )
        val vm = criarVm(resultados = resultados)

        assertEquals(listOf(1, 2), vm.uiState.value.fasesLiberadas)
    }

    @Test
    fun `excluir uma vitoria bloqueia novamente a fase`() {
        val resultados = FakeRepositorioResultados(
            listOf(resultadoTeste(1, fase = 1, vitoria = true, pontos = 10))
        )
        val vm = criarVm(resultados = resultados)
        assertEquals(listOf(1, 2), vm.uiState.value.fasesLiberadas)

        vm.excluirResultado(1)

        assertEquals(listOf(1), vm.uiState.value.fasesLiberadas)
    }

    @Test
    fun `resultados ficam ordenados por pontos do maior para o menor`() {
        val resultados = FakeRepositorioResultados(
            listOf(
                resultadoTeste(1, 1, true, pontos = 10),
                resultadoTeste(2, 1, true, pontos = 50),
                resultadoTeste(3, 1, false, pontos = 30)
            )
        )
        val vm = criarVm(resultados = resultados)

        assertEquals(listOf(50, 30, 10), vm.uiState.value.resultados.map { it.pontos })
    }

    @Test
    fun `bonus online soma moedas e informa o valor`() {
        val vm = criarVm(bonus = FakeRepositorioBonus(Result.success(20)))

        vm.iniciarPartida(1, 1L, "Ana")

        val estado = vm.uiState.value
        assertEquals(120, estado.partida?.moedas)
        assertEquals(20, estado.bonusMoedasAplicado)
        assertEquals("Bônus online: +20 moedas", estado.mensagem)
    }

    @Test
    fun `falha remota aplica bonus zero e avisa o jogador`() {
        val vm = criarVm(bonus = FakeRepositorioBonus(Result.failure(RuntimeException("sem rede"))))

        vm.iniciarPartida(1, 1L, "Ana")

        val estado = vm.uiState.value
        assertEquals(100, estado.partida?.moedas)
        assertEquals(0, estado.bonusMoedasAplicado)
        assertEquals("Bônus online indisponível: usado +0", estado.mensagem)
    }

    @Test
    fun `bonus acima de 30 e limitado`() {
        val vm = criarVm(bonus = FakeRepositorioBonus(Result.success(500)))

        vm.iniciarPartida(1, 1L, "Ana")

        assertEquals(30, vm.uiState.value.bonusMoedasAplicado)
        assertEquals(130, vm.uiState.value.partida?.moedas)
    }

    @Test
    fun `nome vazio e fase bloqueada impedem o inicio da partida`() {
        val bonus = FakeRepositorioBonus()
        val vm = criarVm(bonus = bonus)

        vm.iniciarPartida(1, 1L, "   ")
        assertNull(vm.uiState.value.partida)
        assertNotNull(vm.uiState.value.mensagem)

        vm.iniciarPartida(2, 1L, "Ana")
        assertNull(vm.uiState.value.partida)
        assertEquals(0, bonus.chamadas)
    }

    @Test
    fun `resultado e salvo exatamente uma vez`() {
        val resultados = FakeRepositorioResultados()
        val vm = criarVm(
            resultados = resultados,
            defesa = FakeRegrasDefesa(aoAtacar = matarTodosInimigos)
        )

        vm.iniciarPartida(1, 1L, "Ana")
        vm.proximoTurno()
        vm.proximoTurno()
        vm.proximoTurno()

        assertEquals(1, resultados.chamadasInserir)
        val atual = vm.uiState.value.resultadoAtual
        assertNotNull(atual)
        assertTrue(atual!!.vitoria)
        assertEquals("Ana", atual.nomeJogador)
        assertEquals(SituacaoPartida.VITORIA, vm.uiState.value.partida?.situacao)
    }

    @Test
    fun `salvamento atrasado nao altera resultado de outra partida`() {
        val liberarPrimeiro = CompletableDeferred<Unit>()
        val resultados = FakeRepositorioResultados().apply {
            aguardarPrimeiroInsert = liberarPrimeiro
        }
        val vm = criarVm(resultados = resultados, defesa = FakeRegrasDefesa(aoAtacar = matarTodosInimigos))

        vm.iniciarPartida(1, 1L, "Ana")
        vm.proximoTurno()

        vm.iniciarPartida(1, 1L, "Bia")
        vm.proximoTurno()
        val resultadoNovo = vm.uiState.value.resultadoAtual
        assertNotNull(resultadoNovo)
        assertEquals("Bia", resultadoNovo!!.nomeJogador)
        assertTrue(resultadoNovo.id > 0L)

        liberarPrimeiro.complete(Unit)
        assertEquals(resultadoNovo, vm.uiState.value.resultadoAtual)
        assertEquals(2, resultados.chamadasInserir)
    }

    @Test
    fun `erro ao salvar resultado vira mensagem no UiState`() {
        val resultados = FakeRepositorioResultados().apply { falharAoInserir = true }
        val vm = criarVm(
            resultados = resultados,
            defesa = FakeRegrasDefesa(aoAtacar = matarTodosInimigos)
        )

        vm.iniciarPartida(1, 1L, "Ana")
        vm.proximoTurno()

        assertEquals("Não foi possível salvar o resultado no histórico.", vm.uiState.value.mensagem)
        assertEquals(1, resultados.chamadasInserir)
    }

    @Test
    fun `falha na leitura de presets vira mensagem`() {
        val presets = FakeRepositorioPresets(EstadoConsulta.Falha("Erro ao ler presets"))
        val vm = criarVm(presets = presets)

        assertEquals("Erro ao ler presets", vm.uiState.value.mensagem)
    }

    @Test
    fun `compra invalida mantem estado e avisa, compra valida atualiza moedas`() {
        val defesa = FakeRegrasDefesa(aoComprar = { it.copy(moedas = it.moedas - 30) })
        val vm = criarVm(bonus = FakeRepositorioBonus(Result.success(0)), defesa = defesa)
        vm.iniciarPartida(1, 1L, "Ana")

        vm.comprar(TipoDefesa.GOBLIN, Posicao(1, 1))
        assertEquals(70, vm.uiState.value.partida?.moedas)
        assertNull(vm.uiState.value.mensagem)

        val semDefesa = criarVm(bonus = FakeRepositorioBonus(Result.success(0)))
        semDefesa.iniciarPartida(1, 1L, "Ana")
        semDefesa.comprar(TipoDefesa.GOBLIN, Posicao(1, 1))
        assertEquals(100, semDefesa.uiState.value.partida?.moedas)
        assertNotNull(semDefesa.uiState.value.mensagem)
    }

    @Test
    fun `renomear ignora nome em branco e chama repositorio com nome valido`() {
        val resultados = FakeRepositorioResultados(listOf(resultadoTeste(1, 1, true, 10)))
        val vm = criarVm(resultados = resultados)

        vm.renomearResultado(1, "  ")
        assertEquals(0, resultados.chamadasAtualizarNome)

        vm.renomearResultado(1, " Bia ")
        assertEquals(1, resultados.chamadasAtualizarNome)
        assertEquals("Bia", vm.uiState.value.resultados.first().nomeJogador)
    }

    @Test
    fun `sairDaPartida limpa a partida e o resultado atual`() {
        val vm = criarVm()
        vm.iniciarPartida(1, 1L, "Ana")

        vm.sairDaPartida()

        assertNull(vm.uiState.value.partida)
        assertNull(vm.uiState.value.resultadoAtual)
    }
}
