package com.example.youshallnotsavetheprince.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.youshallnotsavetheprince.contratos.RepositorioBonus
import com.example.youshallnotsavetheprince.contratos.RepositorioPresets
import com.example.youshallnotsavetheprince.contratos.RepositorioResultados
import com.example.youshallnotsavetheprince.logic.MotorTurnos

class JogoViewModelFactory(
    private val motor: MotorTurnos,
    private val repositorioPresets: RepositorioPresets,
    private val repositorioResultados: RepositorioResultados,
    private val repositorioBonus: RepositorioBonus
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JogoViewModel::class.java)) {
            return JogoViewModel(
                motor = motor,
                repositorioPresets = repositorioPresets,
                repositorioResultados = repositorioResultados,
                repositorioBonus = repositorioBonus
            ) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
    }
}
