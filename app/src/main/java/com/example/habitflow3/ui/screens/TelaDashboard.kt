package com.example.habitflow3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitflow3.ui.HabitFlowViewModel

@Composable
fun TelaDashboard(modelo: HabitFlowViewModel, abrirCadastro: () -> Unit, abrirDetalhe: (Int) -> Unit) {
    LaunchedEffect(Unit) { modelo.carregarHabitos() }
    val ativos = modelo.habitos.filter { it.dataConclusao == null }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TituloSecao("Meus hábitos", "Acompanhe seus hábitos em andamento.")
        }
        item {
            Button(onClick = abrirCadastro, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("  Novo hábito")
            }
        }
        if (ativos.isEmpty()) {
            item { CartaoInformacao("Tudo pronto para começar", "Cadastre seu primeiro hábito para acompanhar sua rotina.") }
        }
        items(ativos, key = { it.id }) { habito ->
            Card(modifier = Modifier.fillMaxWidth().clickable { abrirDetalhe(habito.id) }) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(habito.nome, style = MaterialTheme.typography.titleMedium)
                    Text("${habito.categoria} • ${habito.horario}")
                    val progresso = (habito.diasRealizados.toFloat() / habito.metaDias.coerceAtLeast(1)).coerceIn(0f, 1f)
                    LinearProgressIndicator(progress = { progresso }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("${habito.diasRealizados} de ${habito.metaDias} dias", style = MaterialTheme.typography.bodySmall)
                        Text("Ver detalhes", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
