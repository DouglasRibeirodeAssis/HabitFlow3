package com.example.habitflow3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitflow3.data.HabitoEntity
import com.example.habitflow3.data.dataAtualIso
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.interpretarData
import com.example.habitflow3.data.periodoEmDias

@Composable
fun TelaCadastroHabito(
    existente: HabitoEntity?,
    modoEdicao: Boolean,
    aoSalvar: (HabitoEntity) -> Unit,
    aoErro: (String) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("08:00") }
    var metaDias by remember { mutableStateOf("21") }
    var inicio by remember { mutableStateOf(formatarData(dataAtualIso())) }

    LaunchedEffect(existente?.id) {
        if (existente != null) {
            nome = existente.nome
            categoria = existente.categoria
            horario = existente.horario
            metaDias = existente.metaDias.toString()
            inicio = formatarData(existente.dataInicio)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TituloSecao(
            if (modoEdicao) "Editar hábito" else "Cadastrar novo hábito",
            "Escolha um hábito e acompanhe sua evolução."
        )
        CampoFormulario("Nome do hábito *", nome, { nome = it })
        CampoFormulario("Categoria *", categoria, { categoria = it })
        CampoFormulario("Horário (HH:mm) *", horario, { horario = it })
        CampoFormulario("Meta de dias *", metaDias, { metaDias = it })
        CampoFormulario("Data de início (dd/MM/aaaa) *", inicio, { inicio = it })
        Button(
            onClick = {
                val dias = metaDias.toIntOrNull()
                val dataIso = interpretarData(inicio)
                val horaValida = Regex("^(?:[01][0-9]|2[0-3]):[0-5][0-9]$").matches(horario)
                when {
                    nome.isBlank() || categoria.isBlank() -> aoErro("Preencha o nome e a categoria.")
                    !horaValida -> aoErro("Informe um horário válido, como 08:00.")
                    dias == null || dias <= 0 -> aoErro("A meta de dias precisa ser maior que zero.")
                    dataIso == null -> aoErro("Informe uma data válida no formato dd/MM/aaaa.")
                    existente?.dataConclusao != null && periodoEmDias(dataIso, existente.dataConclusao) <= 0 ->
                        aoErro("A data de início precisa ser anterior à conclusão.")
                    else -> aoSalvar(
                        HabitoEntity(
                            id = existente?.id ?: 0,
                            nome = nome.trim(),
                            categoria = categoria.trim(),
                            horario = horario,
                            metaDias = dias,
                            dataInicio = dataIso,
                            dataConclusao = existente?.dataConclusao,
                            diasRealizados = (existente?.diasRealizados ?: 0).coerceAtMost(dias)
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (modoEdicao) "Salvar alterações" else "Salvar hábito")
        }
    }
}
