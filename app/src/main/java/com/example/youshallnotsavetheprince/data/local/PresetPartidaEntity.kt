package com.example.youshallnotsavetheprince.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets_partida")
data class PresetPartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nome: String,
    val moedasIniciais: Int,
    val vidaInicialCastelo: Int
)
