package com.example.youshallnotsavetheprince.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultadoDao {
    @Query("SELECT * FROM resultados_partida ORDER BY registradoEmMillis DESC, id DESC")
    fun observarTodos(): Flow<List<ResultadoPartidaEntity>>

    @Insert
    suspend fun inserir(resultado: ResultadoPartidaEntity): Long

    @Query("UPDATE resultados_partida SET nomeJogador = :novoNome WHERE id = :id")
    suspend fun atualizar(id: Long, novoNome: String): Int

    @Query("DELETE FROM resultados_partida WHERE id = :id")
    suspend fun excluir(id: Long): Int
}
