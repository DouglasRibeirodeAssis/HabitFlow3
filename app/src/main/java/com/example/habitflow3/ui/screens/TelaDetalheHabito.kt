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
import com.example.habitflow3.data.HabitoEntity
import com.example.habitflow3.data.dataNaoFutura
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.periodoEmDias
import com.example.habitflow3.ui.HabitFlowViewModel
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun TelaDetalheHabito(
    id: Int,
    modelo: HabitFlowViewModel,
    editar: () -> Unit,
    voltar: () -> Unit,
    aoErro: (String) -> Unit
) {
    val escopo = rememberCoroutineScope()
    var habito by remember(id) { mutableStateOf<HabitoEntity?>(null) }
    var carregado by remember(id) { mutableStateOf(false) }
    LaunchedEffect(id, modelo.habitos) {
        habito = modelo.buscarHabitoPorId(id)
        carregado = true
    }
    var confirmarExclusao by remember { mutableStateOf(false) }

    val habitoAtual = habito
    if (habitoAtual == null) {
        Column(modifier = Modifier.padding(16.dp)) { Text(if (carregado) "Hábito não encontrado." else "Carregando hábito...") }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TituloSecao(habitoAtual.nome, habitoAtual.categoria)
        CartaoInformacao("Horário", habitoAtual.horario)
        CartaoInformacao("Data de início", formatarData(habitoAtual.dataInicio))
        CartaoInformacao("Progresso", "${habitoAtual.diasRealizados} de ${habitoAtual.metaDias} dias")
        LinearProgressIndicator(
            progress = { (habitoAtual.diasRealizados.toFloat() / habitoAtual.metaDias.coerceAtLeast(1)).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth()
        )
        if (habitoAtual.dataConclusao != null) {
            CartaoInformacao("Concluído em", formatarData(habitoAtual.dataConclusao))
            CartaoInformacao("Período realizado", "${periodoEmDias(habitoAtual.dataInicio, habitoAtual.dataConclusao)} dias corridos")
            Button(onClick = {
                escopo.executarAcao(aoErro) { modelo.reabrirHabito(habitoAtual) }
            }, modifier = Modifier.fillMaxWidth()) { Text("Reabrir hábito") }
        } else {
            Button(onClick = {
                escopo.executarAcao(aoErro) { modelo.registrarDia(habitoAtual) }
            }, enabled = habitoAtual.diasRealizados < habitoAtual.metaDias, modifier = Modifier.fillMaxWidth()) {
                Text("Registrar um dia realizado")
            }
            Button(onClick = {
                if (!dataNaoFutura(habitoAtual.dataInicio)) {
                    aoErro("A data de início não pode ser posterior ao dia de hoje.")
                } else {
                    escopo.executarAcao(aoErro) { modelo.concluirHabito(habitoAtual) }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Concluir hábito") }
        }
        OutlinedButton(onClick = editar, modifier = Modifier.fillMaxWidth()) { Text("Editar hábito") }
        OutlinedButton(onClick = { confirmarExclusao = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Excluir hábito")
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir hábito?") },
            text = { Text("O hábito será removido definitivamente.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    escopo.executarAcao(aoErro) {
                        modelo.removerHabito(habitoAtual)
                        voltar()
                    }
                }) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") } }
        )
    }
}
