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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.interpretarData
import com.example.habitflow3.ui.HabitFlowViewModel

@Composable
fun TelaDetalheMeta(id: Int, modelo: HabitFlowViewModel, voltar: () -> Unit, aoErro: (String) -> Unit) {
    val escopo = rememberCoroutineScope()
    var meta by remember(id) { mutableStateOf<com.example.habitflow3.data.MetaEntity?>(null) }
    var carregado by remember(id) { mutableStateOf(false) }
    LaunchedEffect(id, modelo.metas) {
        meta = modelo.buscarMetaPorId(id)
        carregado = true
    }
    var editando by remember { mutableStateOf(false) }
    var confirmarExclusao by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var prazo by remember { mutableStateOf("") }

    LaunchedEffect(meta?.id) {
        val metaCarregada = meta
        if (metaCarregada != null) {
            titulo = metaCarregada.titulo
            descricao = metaCarregada.descricao
            prazo = if (metaCarregada.prazo.isBlank()) "" else formatarData(metaCarregada.prazo)
        }
    }

    val metaAtual = meta
    if (metaAtual == null) {
        Text(if (carregado) "Meta não encontrada." else "Carregando meta...", modifier = Modifier.padding(16.dp))
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TituloSecao(if (editando) "Editar meta" else metaAtual.titulo)
        if (editando) {
            CampoFormulario("Título *", titulo, { titulo = it })
            CampoFormulario("Descrição", descricao, { descricao = it }, linhaUnica = false)
            CampoFormulario("Prazo (dd/MM/aaaa), opcional", prazo, { prazo = it })
            Button(onClick = {
                val prazoIso = if (prazo.isBlank()) "" else interpretarData(prazo)
                when {
                    titulo.isBlank() -> aoErro("Informe o título da meta.")
                    prazoIso == null -> aoErro("O prazo precisa estar no formato dd/MM/aaaa.")
                    else -> escopo.executarAcao(aoErro) {
                        modelo.salvarMeta(metaAtual.copy(titulo = titulo.trim(), descricao = descricao.trim(), prazo = prazoIso))
                        editando = false
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Salvar alterações") }
            OutlinedButton(onClick = { editando = false }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar edição") }
        } else {
            CartaoInformacao("Descrição", metaAtual.descricao.ifBlank { "Sem descrição" })
            CartaoInformacao("Prazo", if (metaAtual.prazo.isBlank()) "Não definido" else formatarData(metaAtual.prazo))
            Button(onClick = {
                escopo.executarAcao(aoErro) { modelo.alterarConclusaoMeta(metaAtual, !metaAtual.concluida) }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (metaAtual.concluida) "Marcar como pendente" else "Marcar como concluída")
            }
            OutlinedButton(onClick = { editando = true }, modifier = Modifier.fillMaxWidth()) { Text("Editar meta") }
            OutlinedButton(onClick = { confirmarExclusao = true }, modifier = Modifier.fillMaxWidth()) { Text("Excluir meta") }
        }
    }
    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir meta?") },
            text = { Text("A meta será removida definitivamente.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    escopo.executarAcao(aoErro) {
                        modelo.removerMeta(metaAtual)
                        voltar()
                    }
                }) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") } }
        )
    }
}
