package com.example.habitflow3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TituloSecao(titulo: String, subtitulo: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        if (subtitulo != null) {
            Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CampoFormulario(
    titulo: String,
    valor: String,
    aoAlterar: (String) -> Unit,
    modificador: Modifier = Modifier,
    linhaUnica: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoAlterar,
        label = { Text(titulo) },
        modifier = modificador.fillMaxWidth(),
        singleLine = linhaUnica,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CartaoInformacao(titulo: String, conteudo: String, modificador: Modifier = Modifier) {
    Card(
        modifier = modificador.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            Text(conteudo, style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun CoroutineScope.executarAcao(aoErro: (String) -> Unit, bloco: suspend () -> Unit) {
    launch {
        try {
            bloco()
        } catch (e: Exception) {
            aoErro("Não foi possível concluir a operação: ${e.localizedMessage ?: "erro inesperado"}")
        }
    }
}
