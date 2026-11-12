package com.example.youshallnotsavetheprince.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.youshallnotsavetheprince.contratos.RepositorioPresets

class AdminViewModelFactory(
    private val repositorioPresets: RepositorioPresets
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(repositorioPresets) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
    }
}
