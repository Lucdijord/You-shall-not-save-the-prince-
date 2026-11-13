package com.example.youshallnotsavetheprince.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PresetDao {
    @Query("SELECT * FROM presets_partida ORDER BY id ASC")
    abstract fun observarTodos(): Flow<List<PresetPartidaEntity>>

    @Insert
    abstract suspend fun inserir(preset: PresetPartidaEntity): Long

    @Update
    abstract suspend fun atualizar(preset: PresetPartidaEntity): Int

    @Query("DELETE FROM presets_partida WHERE id = :id")
    abstract suspend fun excluir(id: Long): Int

    @Query("SELECT COUNT(*) FROM presets_partida")
    abstract suspend fun contar(): Int

    @Query("SELECT id FROM presets_partida WHERE id = :id LIMIT 1")
    abstract suspend fun buscarId(id: Long): Long?

    @Transaction
    open suspend fun garantirPadrao() {
        if (contar() == 0) {
            inserir(
                PresetPartidaEntity(
                    nome = "Padrão",
                    moedasIniciais = 100,
                    vidaInicialCastelo = 10
                )
            )
        }
    }

    @Transaction
    open suspend fun excluirSePermitido(id: Long): Int {
        if (buscarId(id) == null) return 0
        require(contar() > 1) { "É necessário manter pelo menos um preset." }
        return excluir(id)
    }
}
