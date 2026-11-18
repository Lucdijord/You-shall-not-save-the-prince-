package com.example.youshallnotsavetheprince.viewmodel

import com.example.youshallnotsavetheprince.contratos.RepositorioPresets
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.PresetPartida
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {
    private class FakePresets : RepositorioPresets {
        val fluxo = MutableStateFlow<EstadoConsulta<List<PresetPartida>>>(EstadoConsulta.Sucesso(emptyList()))
        var insercoes = 0
        var alteracoes = 0
        var exclusoes = 0
        var falha: String? = null
        val registros = mutableListOf<PresetPartida>()

        override fun observar(): Flow<EstadoConsulta<List<PresetPartida>>> = fluxo

        override suspend fun inserir(preset: PresetPartida): Result<Long> {
            insercoes++
            falha?.let { return Result.failure(IllegalStateException(it)) }
            val novo = preset.copy(id = (registros.maxOfOrNull { it.id } ?: 0L) + 1L)
            registros.add(novo)
            fluxo.value = EstadoConsulta.Sucesso(registros.toList())
            return Result.success(novo.id)
        }

        override suspend fun atualizar(preset: PresetPartida): Result<Unit> {
            alteracoes++
            falha?.let { return Result.failure(IllegalStateException(it)) }
            registros.replaceAll { if (it.id == preset.id) preset else it }
            fluxo.value = EstadoConsulta.Sucesso(registros.toList())
            return Result.success(Unit)
        }

        override suspend fun excluir(id: Long): Result<Unit> {
            exclusoes++
            falha?.let { return Result.failure(IllegalStateException(it)) }
            registros.removeAll { it.id == id }
            fluxo.value = EstadoConsulta.Sucesso(registros.toList())
            return Result.success(Unit)
        }
    }

    @Before fun iniciar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After fun finalizar() {
        Dispatchers.resetMain()
    }

    @Test fun `pin correto habilita administracao e incorreto bloqueia`() {
        val vm = AdminViewModel(FakePresets())
        vm.autenticar("0000")
        assertFalse(vm.uiState.value.adminAutenticado)
        assertNotNull(vm.uiState.value.mensagem)
        vm.autenticar("1234")
        assertTrue(vm.uiState.value.adminAutenticado)
        assertNull(vm.uiState.value.mensagem)
        vm.sair()
        assertFalse(vm.uiState.value.adminAutenticado)
    }

    @Test fun `presets sao filtrados e ordenados pelo nome`() {
        val fake = FakePresets()
        val vm = AdminViewModel(fake)
        fake.fluxo.value = EstadoConsulta.Sucesso(listOf(
            PresetPartida(1L, "zeta", 100, 10),
            PresetPartida(2L, "Alfa", 100, 10),
            PresetPartida(3L, "almeida", 100, 10)
        ))
        assertEquals(listOf("Alfa", "almeida", "zeta"), vm.uiState.value.presetsVisiveis.map { it.nome })
        vm.filtrar("AL")
        assertEquals(listOf("Alfa", "almeida"), vm.uiState.value.presetsVisiveis.map { it.nome })
        assertEquals("AL", vm.uiState.value.filtro)
    }

    @Test fun `sem login nao permite mutacoes`() {
        val fake = FakePresets()
        val vm = AdminViewModel(fake)
        vm.criarPreset("Teste", 100, 10)
        vm.atualizarPreset(PresetPartida(1L, "Teste", 100, 10))
        vm.excluirPreset(1)
        assertEquals(0, fake.insercoes + fake.alteracoes + fake.exclusoes)
        assertNotNull(vm.uiState.value.mensagem)
    }

    @Test fun `crud usa repository e atualiza estado`() {
        val fake = FakePresets()
        val vm = AdminViewModel(fake)
        vm.autenticar("1234")
        vm.criarPreset(" Beta ", 100, 10)
        assertEquals("Beta", fake.registros.single().nome)
        assertEquals("Beta", vm.uiState.value.presetsVisiveis.single().nome)
        vm.atualizarPreset(fake.registros.single().copy(nome = "Novo"))
        assertEquals("Novo", fake.registros.single().nome)
        vm.excluirPreset(1)
        assertEquals(1, fake.insercoes)
        assertEquals(1, fake.alteracoes)
        assertEquals(1, fake.exclusoes)
        assertTrue(vm.uiState.value.presetsVisiveis.isEmpty())
    }

    @Test fun `campos invalidos nao chamam repository`() {
        val fake = FakePresets()
        val vm = AdminViewModel(fake)
        vm.autenticar("1234")
        vm.criarPreset(" ", 100, 10)
        vm.criarPreset("Teste", 49, 10)
        vm.criarPreset("Teste", 100, 21)
        assertEquals(0, fake.insercoes)
        assertNotNull(vm.uiState.value.mensagem)
    }

    @Test fun `erro de CRUD e exibido no UiState`() {
        val fake = FakePresets().apply { falha = "Não é possível excluir o último preset." }
        val vm = AdminViewModel(fake)
        vm.autenticar("1234")
        vm.excluirPreset(1)
        assertEquals("Não é possível excluir o último preset.", vm.uiState.value.mensagem)
        assertFalse(vm.uiState.value.carregando)
    }

    @Test fun `erro de leitura e exibido no UiState`() {
        val fake = FakePresets()
        val vm = AdminViewModel(fake)
        fake.fluxo.value = EstadoConsulta.Falha("Erro ao ler presets")
        assertEquals("Erro ao ler presets", vm.uiState.value.mensagem)
        assertFalse(vm.uiState.value.carregando)
        vm.limparErro()
        assertNull(vm.uiState.value.mensagem)
    }
}
