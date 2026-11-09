package com.example.youshallnotsavetheprince.contratos

import com.example.youshallnotsavetheprince.model.EstadoConsulta
import com.example.youshallnotsavetheprince.model.PresetPartida
import kotlinx.coroutines.flow.Flow

interface RepositorioPresets {
    fun observar(): Flow<EstadoConsulta<List<PresetPartida>>>
    suspend fun inserir(preset: PresetPartida): Result<Long>
    suspend fun atualizar(preset: PresetPartida): Result<Unit>
    suspend fun excluir(id: Long): Result<Unit>
}
