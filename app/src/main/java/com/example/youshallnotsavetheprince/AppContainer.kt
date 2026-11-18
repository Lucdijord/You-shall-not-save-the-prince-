package com.example.youshallnotsavetheprince

import android.content.Context
import com.example.youshallnotsavetheprince.data.local.AppDatabase
import com.example.youshallnotsavetheprince.data.remote.FirebaseBonusRemoteDataSource
import com.example.youshallnotsavetheprince.data.repository.FirebaseBonusRepository
import com.example.youshallnotsavetheprince.data.repository.RoomPresetRepository
import com.example.youshallnotsavetheprince.data.repository.RoomResultadoRepository
import com.example.youshallnotsavetheprince.logic.Defesas
import com.example.youshallnotsavetheprince.logic.MotorTurnos
import com.example.youshallnotsavetheprince.logic.RegrasInimigoImpl
import com.example.youshallnotsavetheprince.viewmodel.AdminViewModelFactory
import com.example.youshallnotsavetheprince.viewmodel.JogoViewModelFactory

class AppContainer(context: Context) {
    private val banco = AppDatabase.obter(context)
    private val repositorioPresets = RoomPresetRepository(banco.presetDao())
    private val repositorioResultados = RoomResultadoRepository(banco.resultadoDao())
    private val repositorioBonus = FirebaseBonusRepository(FirebaseBonusRemoteDataSource())
    private val motor = MotorTurnos(Defesas, RegrasInimigoImpl())

    val jogoFactory = JogoViewModelFactory(
        motor,
        repositorioPresets,
        repositorioResultados,
        repositorioBonus
    )

    val adminFactory = AdminViewModelFactory(repositorioPresets)
}
