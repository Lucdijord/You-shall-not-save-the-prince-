package com.example.youshallnotsavetheprince.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PresetPartidaEntity::class, ResultadoPartidaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
    abstract fun resultadoDao(): ResultadoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obter(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "you_shall_not_save_the_prince.db"
                ).build().also { instancia = it }
            }
    }
}
