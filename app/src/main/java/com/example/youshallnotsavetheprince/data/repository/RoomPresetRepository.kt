package com.example.youshallnotsavetheprince.data.repository

import com.example.youshallnotsavetheprince.contratos.RepositorioPresets
import com.example.youshallnotsavetheprince.data.local.PresetDao
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.PresetPartida
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RoomPresetRepository(private val dao: PresetDao) : RepositorioPresets {
    override fun observar(): Flow<EstadoConsulta<List<PresetPartida>>> = flow<EstadoConsulta<List<PresetPartida>>> {
        dao.garantirPadrao()
        emitAll(dao.observarTodos().map { entidades ->
            EstadoConsulta.Sucesso(entidades.map { it.paraModelo() })
        })
    }.catch {
        emit(EstadoConsulta.Falha("Não foi possível carregar os presets salvos."))
    }

    override suspend fun inserir(preset: PresetPartida): Result<Long> = try {
        require(preset.id == 0L) { "Um novo preset não pode ter ID definido." }
        validar(preset)
        dao.garantirPadrao()
        Result.success(dao.inserir(preset.paraEntidade()))
    } catch (erro: CancellationException) {
        throw erro
    } catch (erro: IllegalArgumentException) {
        Result.failure(erro)
    } catch (erro: Exception) {
        Result.failure(IllegalStateException("Não foi possível cadastrar o preset.", erro))
    }

    override suspend fun atualizar(preset: PresetPartida): Result<Unit> = try {
        require(preset.id > 0L) { "Selecione um preset válido para editar." }
        validar(preset)
        dao.garantirPadrao()
        require(dao.atualizar(preset.paraEntidade()) == 1) { "Preset não encontrado." }
        Result.success(Unit)
    } catch (erro: CancellationException) {
        throw erro
    } catch (erro: IllegalArgumentException) {
        Result.failure(erro)
    } catch (erro: Exception) {
        Result.failure(IllegalStateException("Não foi possível atualizar o preset.", erro))
    }

    override suspend fun excluir(id: Long): Result<Unit> = try {
        require(id > 0L) { "Selecione um preset válido para excluir." }
        dao.garantirPadrao()
        require(dao.excluirSePermitido(id) == 1) { "Preset não encontrado." }
        Result.success(Unit)
    } catch (erro: CancellationException) {
        throw erro
    } catch (erro: IllegalArgumentException) {
        Result.failure(erro)
    } catch (erro: Exception) {
        Result.failure(IllegalStateException("Não foi possível excluir o preset.", erro))
    }

    private fun validar(preset: PresetPartida) {
        require(preset.nome.isNotBlank()) { "Informe um nome para o preset." }
        require(preset.moedasIniciais in 50..250) { "As moedas iniciais devem estar entre 50 e 250." }
        require(preset.vidaInicialCastelo in 5..20) { "A vida do castelo deve estar entre 5 e 20." }
    }
}
