
package com.example.habitflow3.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.habitflow3.R
import com.example.habitflow3.ui.HabitFlowViewModel

@Composable
fun TelaInicial(
    modelo: HabitFlowViewModel,
    abrirCadastro: () -> Unit,
    abrirMetas: () -> Unit,
    abrirHistorico: () -> Unit
) {
    LaunchedEffect(Unit) {
        modelo.carregarTudo()
    }

    val emAndamento = modelo.habitos.count {
        it.dataConclusao == null
    }

    val terminados = modelo.habitos.count {
        it.dataConclusao != null
    }

    val metasPendentes = modelo.metas.count {
        !it.concluida
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.logo_habitflow
            ),
            contentDescription = "Logo HabitFlow",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Fit
        )

        TituloSecao(
            "Bem-vindo ao HabitFlow",
            "Organize sua rotina, acompanhe metas e comemore cada conquista."
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    "Seu resumo",
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    CartaoInformacao(
                        "Hábitos ativos",
                        "$emAndamento",
                        Modifier.weight(1f)
                    )

                    CartaoInformacao(
                        "Concluídos",
                        "$terminados",
                        Modifier.weight(1f)
                    )
                }

                Text(
                    "Metas pendentes: $metasPendentes",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Button(
            onClick = abrirCadastro,
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.AddCircle,
                contentDescription = null
            )
            Text("  Cadastrar hábito")
        }

        Button(
            onClick = abrirMetas,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Flag,
                contentDescription = null
            )
            Text("  Minhas metas")
        }

        Button(
            onClick = abrirHistorico,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null
            )
            Text("  Histórico de hábitos")
        }
    }
}
