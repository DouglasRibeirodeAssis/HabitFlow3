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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.viewmodel.MetasViewModel

@Composable
fun TelaDetalheMeta(id: Int, modelo: MetasViewModel, voltar: () -> Unit) {
    val estado by modelo.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(id) { modelo.selecionarMeta(id) }
    val meta = estado.metaSelecionada?.takeIf { it.id == id }
    // Apenas estados de interface visual ficam locais.
    var editando by remember(id) { mutableStateOf(false) }
    var confirmarExclusao by remember(id) { mutableStateOf(false) }

    if (meta == null) {
        Text(
            if (estado.carregandoDetalhe) "Carregando meta..." else "Meta não encontrada.",
            modifier = Modifier.padding(16.dp)
        )
        return
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TituloSecao(if (editando) "Editar meta" else meta.titulo)
        if (editando) {
            val formulario = estado.edicaoMeta
            CampoFormulario("Título *", formulario.titulo, modelo::atualizarTituloEdicao)
            CampoFormulario("Descrição", formulario.descricao, modelo::atualizarDescricaoEdicao, linhaUnica = false)
            CampoFormulario("Prazo (dd/MM/aaaa), opcional", formulario.prazo, modelo::atualizarPrazoEdicao)
            Button(
                onClick = { modelo.salvarEdicaoMeta { editando = false } },
                enabled = !estado.salvando,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (estado.salvando) "Salvando..." else "Salvar alterações") }
            OutlinedButton(
                onClick = { modelo.cancelarEdicaoMeta(); editando = false },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cancelar edição") }
        } else {
            CartaoInformacao("Descrição", meta.descricao.ifBlank { "Sem descrição" })
            CartaoInformacao("Prazo", if (meta.prazo.isBlank()) "Não definido" else formatarData(meta.prazo))
            Button(
                onClick = { modelo.alterarConclusao(meta, !meta.concluida) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (meta.concluida) "Marcar como pendente" else "Marcar como concluída") }
            OutlinedButton(
                onClick = { modelo.prepararEdicaoMeta(meta); editando = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Editar meta") }
            OutlinedButton(
                onClick = { confirmarExclusao = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Excluir meta") }
        }
    }
    if (confirmarExclusao) AlertDialog(
        onDismissRequest = { confirmarExclusao = false },
        title = { Text("Excluir meta?") },
        text = { Text("A meta será removida definitivamente.") },
        confirmButton = {
            TextButton(onClick = { confirmarExclusao = false; modelo.remover(meta, voltar) }) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
        }
    )
}
