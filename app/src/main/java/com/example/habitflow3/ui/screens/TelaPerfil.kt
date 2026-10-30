package com.example.habitflow3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitflow3.ui.HabitFlowViewModel

@Composable
fun TelaPerfil(modelo: HabitFlowViewModel) {
    LaunchedEffect(Unit) { modelo.carregarTudo() }
    val ativos = modelo.habitos.count { it.dataConclusao == null }
    val concluidos = modelo.habitos.count { it.dataConclusao != null }
    val metasConcluidas = modelo.metas.count { it.concluida }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TituloSecao("Meu perfil", "Sua evolução em um só lugar.")
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Seu progresso", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CartaoInformacao("Hábitos ativos", "$ativos", Modifier.weight(1f))
                    CartaoInformacao("Finalizados", "$concluidos", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CartaoInformacao("Metas", "${modelo.metas.size}", Modifier.weight(1f))
                    CartaoInformacao("Alcançadas", "$metasConcluidas", Modifier.weight(1f))
                }
            }
        }
        Text("Continue construindo hábitos, um dia de cada vez.", style = MaterialTheme.typography.bodyLarge)
    }
}
