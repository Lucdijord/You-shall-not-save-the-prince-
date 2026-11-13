package com.example.youshallnotsavetheprince.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resultados_partida")
data class ResultadoPartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nomeJogador: String,
    val fase: Int,
    val vitoria: Boolean,
    val turnos: Int,
    val pontos: Int,
    val vidaRestante: Int,
    val registradoEmMillis: Long
)
