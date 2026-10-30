package com.example.habitflow3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitflow3.data.MetaEntity
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.interpretarData
import com.example.habitflow3.ui.HabitFlowViewModel

@Composable
fun TelaMetas(modelo: HabitFlowViewModel, abrirDetalhe: (Int) -> Unit, aoErro: (String) -> Unit) {
    val escopo = rememberCoroutineScope()
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var prazo by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { modelo.carregarMetas() }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TituloSecao("Minhas metas", "Planeje seus objetivos e marque suas conquistas.") }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Nova meta", style = MaterialTheme.typography.titleMedium)
                    CampoFormulario("Título *", titulo, { titulo = it })
                    CampoFormulario("Descrição", descricao, { descricao = it }, linhaUnica = false)
                    CampoFormulario("Prazo (dd/MM/aaaa), opcional", prazo, { prazo = it })
                    Button(onClick = {
                        val prazoIso = if (prazo.isBlank()) "" else interpretarData(prazo)
                        when {
                            titulo.isBlank() -> aoErro("Informe o título da meta.")
                            prazoIso == null -> aoErro("O prazo precisa estar no formato dd/MM/aaaa.")
                            else -> {
                                val nova = MetaEntity(titulo = titulo.trim(), descricao = descricao.trim(), prazo = prazoIso)
                                escopo.executarAcao(aoErro) {
                                    modelo.salvarMeta(nova)
                                    titulo = ""
                                    descricao = ""
                                    prazo = ""
                                }
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Adicionar meta") }
                }
            }
        }
        if (modelo.metas.isEmpty()) {
            item { CartaoInformacao("Sem metas cadastradas", "Adicione uma meta para começar.") }
        }
        items(modelo.metas, key = { it.id }) { meta ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = meta.concluida,
                        onCheckedChange = { marcado ->
                            escopo.executarAcao(aoErro) { modelo.alterarConclusaoMeta(meta, marcado) }
                        }
                    )
                    Column(modifier = Modifier.weight(1f).clickable { abrirDetalhe(meta.id) }) {
                        Text(meta.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(if (meta.concluida) "Concluída" else "Em andamento")
                        if (meta.prazo.isNotBlank()) Text("Prazo: ${formatarData(meta.prazo)}")
                        Text("Toque para ver detalhes", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
