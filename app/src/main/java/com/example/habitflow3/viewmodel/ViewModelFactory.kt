package com.example.habitflow3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habitflow3.data.repository.HabitoRepository
import com.example.habitflow3.data.repository.MetaRepository

// Injeção de dependências explícita: nenhum ViewModel conhece Context ou AppDatabase.
class ViewModelFactory(
    private val habitosRepository: HabitoRepository,
    private val metasRepository: MetaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(HabitosViewModel::class.java) -> HabitosViewModel(habitosRepository) as T
        modelClass.isAssignableFrom(MetasViewModel::class.java) -> MetasViewModel(metasRepository) as T
        else -> throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
