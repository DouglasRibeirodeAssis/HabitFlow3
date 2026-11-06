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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.viewmodel.MetasViewModel

@Composable
fun TelaMetas(modelo: MetasViewModel, abrirDetalhe: (Int) -> Unit) {
    val estado by modelo.uiState.collectAsStateWithLifecycle()
    val formulario = estado.novaMeta

    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { TituloSecao("Minhas metas", "Planeje seus objetivos e marque suas conquistas.") }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Nova meta", style = MaterialTheme.typography.titleMedium)
                    CampoFormulario("Título *", formulario.titulo, modelo::atualizarTituloNovaMeta)
                    CampoFormulario("Descrição", formulario.descricao, modelo::atualizarDescricaoNovaMeta, linhaUnica = false)
                    CampoFormulario("Prazo (dd/MM/aaaa), opcional", formulario.prazo, modelo::atualizarPrazoNovaMeta)
                    Button(
                        onClick = { modelo.salvarNovaMeta() },
                        enabled = !estado.salvando,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (estado.salvando) "Salvando..." else "Adicionar meta") }
                }
            }
        }
        if (estado.metas.isEmpty()) item {
            CartaoInformacao("Sem metas cadastradas", "Adicione uma meta para começar.")
        }
        items(estado.metas, key = { it.id }) { meta ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = meta.concluida,
                        onCheckedChange = { marcado -> modelo.alterarConclusao(meta, marcado) }
                    )
                    Column(modifier = Modifier.weight(1f).clickable { abrirDetalhe(meta.id) }) {
                        Text(meta.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(if (meta.concluida) "Concluída" else "Em andamento")
                        if (meta.prazo.isNotBlank()) Text("Prazo: ${formatarData(meta.prazo)}")
                        Text(
                            "Toque para ver detalhes",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
