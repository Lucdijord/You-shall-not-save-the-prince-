package com.example.youshallnotsavetheprince

import com.example.youshallnotsavetheprince.contratos.RegrasDefesa
import com.example.youshallnotsavetheprince.contratos.RegrasInimigo
import com.example.youshallnotsavetheprince.contratos.RepositorioBonus
import com.example.youshallnotsavetheprince.contratos.RepositorioPresets
import com.example.youshallnotsavetheprince.contratos.RepositorioResultados
import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.EntradaOnda
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.FaseConfig
import com.example.youshallnotsavetheprince.model.Inimigo
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import com.example.youshallnotsavetheprince.model.TipoDefesa
import com.example.youshallnotsavetheprince.model.TipoInimigo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

fun configTeste(
    tamanhoCaminho: Int = 3,
    entradas: List<EntradaOnda> = listOf(EntradaOnda(1, TipoInimigo.SOLDADO)),
    limiteTurnos: Int = 25,
    numero: Int = 1
) = FaseConfig(
    numero = numero,
    linhas = 3,
    colunas = 3,
    caminho = List(tamanhoCaminho) { Posicao(0, it) },
    entradas = entradas,
    limiteTurnos = limiteTurnos
)

fun resultadoTeste(id: Long, fase: Int, vitoria: Boolean, pontos: Int) = ResultadoPartida(
    id = id, nomeJogador = "Ana", fase = fase, vitoria = vitoria,
    turnos = 5, pontos = pontos, vidaRestante = 5, registradoEmMillis = 1_000L
)

val matarTodosInimigos: (EstadoPartida) -> EstadoPartida = {
    it.copy(derrotados = it.derrotados + it.inimigos.size, inimigos = emptyList())
}

class FakeRegrasDefesa(
    private val log: MutableList<String> = mutableListOf(),
    private val aoAtacar: (EstadoPartida) -> EstadoPartida = { it },
    private val aoComprar: (EstadoPartida) -> EstadoPartida = { it },
    private val aoMelhorar: (EstadoPartida) -> EstadoPartida = { it }
) : RegrasDefesa {
    override fun comprar(estado: EstadoPartida, tipo: TipoDefesa, posicao: Posicao) = aoComprar(estado)
    override fun melhorar(estado: EstadoPartida, idDefesa: Int) = aoMelhorar(estado)
    override fun atacar(estado: EstadoPartida): EstadoPartida {
        log += "atacar"
        return aoAtacar(estado)
    }
}

class FakeRegrasInimigo(
    private val config: FaseConfig,
    private val log: MutableList<String> = mutableListOf()
) : RegrasInimigo {
    override fun configurarFase(numero: Int) = config.copy(numero = numero)

    override fun gerar(estado: EstadoPartida): EstadoPartida {
        log += "gerar"
        var proximoId = estado.proximoIdInimigo
        val novos = estado.config.entradas
            .filter { it.turno == estado.turno }
            .flatMap { entrada ->
                List(entrada.quantidade) {
                    Inimigo(proximoId++, entrada.tipo, CatalogoJogo.inimigo(entrada.tipo).vidaInicial)
                }
            }
        return estado.copy(inimigos = estado.inimigos + novos, proximoIdInimigo = proximoId)
    }

    override fun movimentar(estado: EstadoPartida): EstadoPartida {
        log += "movimentar"
        return estado.copy(inimigos = estado.inimigos.map { it.copy(indiceCaminho = it.indiceCaminho + 1) })
    }
}

class FakeRepositorioPresets(
    inicial: EstadoConsulta<List<PresetPartida>> =
        EstadoConsulta.Sucesso(listOf(PresetPartida(1L, "Padrão", 100, 10)))
) : RepositorioPresets {
    val fluxo = MutableStateFlow(inicial)
    override fun observar(): Flow<EstadoConsulta<List<PresetPartida>>> = fluxo
    override suspend fun inserir(preset: PresetPartida): Result<Long> = Result.success(1L)
    override suspend fun atualizar(preset: PresetPartida): Result<Unit> = Result.success(Unit)
    override suspend fun excluir(id: Long): Result<Unit> = Result.success(Unit)
}

class FakeRepositorioResultados(inicial: List<ResultadoPartida> = emptyList()) : RepositorioResultados {
    private var lista: List<ResultadoPartida> = inicial
    private var proximoId: Long = (inicial.maxOfOrNull { it.id } ?: 0L) + 1L
    val fluxo = MutableStateFlow<EstadoConsulta<List<ResultadoPartida>>>(EstadoConsulta.Sucesso(inicial))

    var chamadasInserir = 0
    var chamadasAtualizarNome = 0
    var falharAoInserir = false
    var aguardarPrimeiroInsert: CompletableDeferred<Unit>? = null
    val inseridos = mutableListOf<ResultadoPartida>()

    private fun publicar() {
        fluxo.value = EstadoConsulta.Sucesso(lista)
    }

    override fun observar(): Flow<EstadoConsulta<List<ResultadoPartida>>> = fluxo

    override suspend fun inserir(resultado: ResultadoPartida): Result<Long> {
        chamadasInserir++
        if (chamadasInserir == 1) aguardarPrimeiroInsert?.await()
        if (falharAoInserir) return Result.failure(IllegalStateException("falha simulada"))
        val id = proximoId++
        inseridos += resultado
        lista = lista + resultado.copy(id = id)
        publicar()
        return Result.success(id)
    }

    override suspend fun atualizarNome(id: Long, novoNome: String): Result<Unit> {
        chamadasAtualizarNome++
        lista = lista.map { if (it.id == id) it.copy(nomeJogador = novoNome) else it }
        publicar()
        return Result.success(Unit)
    }

    override suspend fun excluir(id: Long): Result<Unit> {
        lista = lista.filterNot { it.id == id }
        publicar()
        return Result.success(Unit)
    }
}

class FakeRepositorioBonus(private val resultado: Result<Int> = Result.success(20)) : RepositorioBonus {
    var chamadas = 0
    override suspend fun buscarBonusMoedas(): Result<Int> {
        chamadas++
        return resultado
    }
}
