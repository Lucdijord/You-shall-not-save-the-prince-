package com.example.youshallnotsavetheprince.contratos

import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import kotlinx.coroutines.flow.Flow

interface RepositorioResultados {
    fun observar(): Flow<EstadoConsulta<List<ResultadoPartida>>>
    suspend fun inserir(resultado: ResultadoPartida): Result<Long>
    suspend fun atualizarNome(id: Long, novoNome: String): Result<Unit>
    suspend fun excluir(id: Long): Result<Unit>
}
