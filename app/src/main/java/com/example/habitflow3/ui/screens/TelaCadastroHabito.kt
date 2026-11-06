package com.example.habitflow3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitflow3.viewmodel.HabitosViewModel

@Composable
fun TelaCadastroHabito(
    modelo: HabitosViewModel,
    modoEdicao: Boolean,
    aoSalvar: () -> Unit
) {
    val estado by modelo.uiState.collectAsStateWithLifecycle()
    val formulario = estado.formulario

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TituloSecao(
            if (modoEdicao) "Editar hábito" else "Cadastrar novo hábito",
            "Escolha um hábito e acompanhe sua evolução."
        )
        CampoFormulario("Nome do hábito *", formulario.nome, modelo::atualizarNome)
        CampoFormulario("Categoria *", formulario.categoria, modelo::atualizarCategoria)
        CampoFormulario("Horário (HH:mm) *", formulario.horario, modelo::atualizarHorario)
        CampoFormulario("Meta de dias *", formulario.metaDias, modelo::atualizarMetaDias)
        CampoFormulario("Data de início (dd/MM/aaaa) *", formulario.inicio, modelo::atualizarInicio)
        Button(
            onClick = aoSalvar,
            enabled = !estado.salvando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.salvando) "Salvando..." else if (modoEdicao) "Salvar alterações" else "Salvar hábito")
        }
    }
}
