package com.example.habitflow3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitflow3.data.local.AppDatabase
import com.example.habitflow3.data.repository.HabitoRepository
import com.example.habitflow3.data.repository.MetaRepository
import com.example.habitflow3.ui.AppNavigation
import com.example.habitflow3.ui.theme.HabitFlowTheme
import com.example.habitflow3.viewmodel.HabitosViewModel
import com.example.habitflow3.viewmodel.MetasViewModel
import com.example.habitflow3.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val banco = AppDatabase.obter(applicationContext)
        val fabrica = ViewModelFactory(
            HabitoRepository(banco.habitoDao()),
            MetaRepository(banco.metaDao())
        )
        setContent {
            HabitFlowTheme {
                val habitos: HabitosViewModel = viewModel(factory = fabrica)
                val metas: MetasViewModel = viewModel(factory = fabrica)
                AppNavigation(habitos, metas)
            }
        }
    }
}
