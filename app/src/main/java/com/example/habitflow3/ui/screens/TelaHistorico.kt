package com.example.habitflow3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.periodoEmDias
import com.example.habitflow3.ui.HabitFlowViewModel

@Composable
fun TelaHistorico(modelo: HabitFlowViewModel, abrirDetalhe: (Int) -> Unit) {
    LaunchedEffect(Unit) { modelo.carregarHabitos() }
    val concluidos = modelo.habitos.filter { it.dataConclusao != null }

    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { TituloSecao("Histórico", "Veja os hábitos concluídos e o período de realização.") }
        if (concluidos.isEmpty()) {
            item { CartaoInformacao("Nenhum hábito concluído", "Quando você finalizar um hábito, ele aparecerá aqui.") }
        }
        items(concluidos, key = { it.id }) { habito ->
            val dataFim = habito.dataConclusao ?: ""
            Card(modifier = Modifier.fillMaxWidth().clickable { abrirDetalhe(habito.id) }) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(habito.nome, style = MaterialTheme.typography.titleMedium)
                    Text(habito.categoria, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Início: ${formatarData(habito.dataInicio)}")
                    Text("Conclusão: ${formatarData(dataFim)}")
                    Text("Período: ${periodoEmDias(habito.dataInicio, dataFim)} dias corridos", color = MaterialTheme.colorScheme.primary)
                    Text("Ver detalhes", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
