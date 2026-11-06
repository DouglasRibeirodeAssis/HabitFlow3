package com.example.habitflow3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.periodoEmDias
import com.example.habitflow3.viewmodel.HabitosViewModel

@Composable
fun TelaDetalheHabito(id: Int, modelo: HabitosViewModel, editar: () -> Unit, voltar: () -> Unit) {
    val estado by modelo.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(id) { modelo.selecionarHabito(id) }
    var confirmarExclusao by remember(id) { mutableStateOf(false) }
    val habito = estado.habitoSelecionado?.takeIf { it.id == id }
    if (habito == null) {
        Text(if (estado.carregandoDetalhe) "Carregando hábito..." else "Hábito não encontrado.", modifier = Modifier.padding(16.dp))
        return
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TituloSecao(habito.nome, habito.categoria)
        CartaoInformacao("Horário", habito.horario)
        CartaoInformacao("Data de início", formatarData(habito.dataInicio))
        CartaoInformacao("Progresso", "${habito.diasRealizados} de ${habito.metaDias} dias")
        LinearProgressIndicator(
            progress = { (habito.diasRealizados.toFloat() / habito.metaDias.coerceAtLeast(1)).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth()
        )
        if (habito.dataConclusao != null) {
            CartaoInformacao("Concluído em", formatarData(habito.dataConclusao))
            CartaoInformacao("Período realizado", "${periodoEmDias(habito.dataInicio, habito.dataConclusao)} dias corridos")
            Button(onClick = { modelo.reabrir(habito) }, modifier = Modifier.fillMaxWidth()) { Text("Reabrir hábito") }
        } else {
            Button(
                onClick = { modelo.registrarDia(habito) },
                enabled = habito.diasRealizados < habito.metaDias,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrar um dia realizado") }
            Button(onClick = { modelo.concluir(habito) }, modifier = Modifier.fillMaxWidth()) { Text("Concluir hábito") }
        }
        OutlinedButton(onClick = editar, modifier = Modifier.fillMaxWidth()) { Text("Editar hábito") }
        OutlinedButton(onClick = { confirmarExclusao = true }, modifier = Modifier.fillMaxWidth()) { Text("Excluir hábito") }
    }
    if (confirmarExclusao) AlertDialog(
        onDismissRequest = { confirmarExclusao = false },
        title = { Text("Excluir hábito?") },
        text = { Text("O hábito será removido definitivamente.") },
        confirmButton = {
            TextButton(onClick = { confirmarExclusao = false; modelo.remover(habito, voltar) }) { Text("Excluir") }
        },
        dismissButton = { TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") } }
    )
}
