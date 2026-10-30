package com.example.habitflow3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitflow3.ui.AppNavigation
import com.example.habitflow3.ui.HabitFlowViewModel
import com.example.habitflow3.ui.theme.HabitFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitFlowTheme {
                val modelo: HabitFlowViewModel = viewModel()
                AppNavigation(modelo)
            }
        }
    }
}
