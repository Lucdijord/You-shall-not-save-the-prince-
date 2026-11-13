package com.example.youshallnotsavetheprince.data.repository

import com.example.youshallnotsavetheprince.contratos.RepositorioResultados
import com.example.youshallnotsavetheprince.data.local.ResultadoDao
import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class RoomResultadoRepository(private val dao: ResultadoDao) : RepositorioResultados {
    override fun observar(): Flow<EstadoConsulta<List<ResultadoPartida>>> =
        dao.observarTodos()
            .map { entidades ->
                val estado: EstadoConsulta<List<ResultadoPartida>> =
                    EstadoConsulta.Sucesso(entidades.map { it.paraModelo() })
                estado
            }
            .catch {
                emit(EstadoConsulta.Falha("Não foi possível carregar o histórico de partidas."))
            }

    override suspend fun inserir(resultado: ResultadoPartida): Result<Long> = try {
        require(resultado.id == 0L) { "Um novo resultado não pode ter ID definido." }
        require(resultado.nomeJogador.isNotBlank()) { "Informe o nome do jogador." }
        require(resultado.fase in 1..5) { "A fase precisa estar entre 1 e 5." }
        Result.success(dao.inserir(resultado.paraEntidade()))
    } catch (erro: CancellationException) {
        throw erro
    } catch (erro: IllegalArgumentException) {
        Result.failure(erro)
    } catch (erro: Exception) {
        Result.failure(IllegalStateException("Não foi possível salvar o resultado.", erro))
    }

    override suspend fun atualizarNome(id: Long, novoNome: String): Result<Unit> = try {
        require(id > 0L) { "Selecione um resultado válido para editar." }
        require(novoNome.isNotBlank()) { "Informe o nome do jogador." }
        require(dao.atualizar(id, novoNome.trim()) == 1) { "Resultado não encontrado." }
        Result.success(Unit)
    } catch (erro: CancellationException) {
        throw erro
    } catch (erro: IllegalArgumentException) {
        Result.failure(erro)
    } catch (erro: Exception) {
        Result.failure(IllegalStateException("Não foi possível alterar o nome do jogador.", erro))
    }

    override suspend fun excluir(id: Long): Result<Unit> = try {
        require(id > 0L) { "Selecione um resultado válido para excluir." }
        require(dao.excluir(id) == 1) { "Resultado não encontrado." }
        Result.success(Unit)
    } catch (erro: CancellationException) {
        throw erro
    } catch (erro: IllegalArgumentException) {
        Result.failure(erro)
    } catch (erro: Exception) {
        Result.failure(IllegalStateException("Não foi possível excluir o resultado.", erro))
    }
}
