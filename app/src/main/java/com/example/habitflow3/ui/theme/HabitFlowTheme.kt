package com.example.habitflow3.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val azul = Color(0xFF1769E8)
private val azulEscuro = Color(0xFF1548A5)
private val azulClaro = Color(0xFFDDEBFF)

private val claro = lightColorScheme(
    primary = azul,
    onPrimary = Color.White,
    primaryContainer = azulClaro,
    onPrimaryContainer = Color(0xFF002C67),
    background = Color(0xFFF7FAFF),
    surface = Color.White,
    secondary = azulEscuro,
    surfaceVariant = Color(0xFFEAF1FB)
)

private val escuro = darkColorScheme(
    primary = Color(0xFF99BEFF),
    primaryContainer = azulEscuro,
    secondary = Color(0xFFBDD5FF),
    background = Color(0xFF0E1729),
    surface = Color(0xFF15233B)
)

@Composable
fun HabitFlowTheme(conteudo: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) escuro else claro, content = conteudo)
}
