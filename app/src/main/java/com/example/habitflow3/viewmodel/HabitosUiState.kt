package com.example.habitflow3.viewmodel

import com.example.habitflow3.data.dataAtualIso
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.local.HabitoEntity

data class HabitoFormularioUiState(
    val id: Int? = null,
    val nome: String = "",
    val categoria: String = "",
    val horario: String = "08:00",
    val metaDias: String = "21",
    val inicio: String = formatarData(dataAtualIso())
)

data class HabitosUiState(
    val habitos: List<HabitoEntity> = emptyList(),
    val habitoSelecionado: HabitoEntity? = null,
    val formulario: HabitoFormularioUiState = HabitoFormularioUiState(),
    val carregando: Boolean = true,
    val carregandoDetalhe: Boolean = false,
    val salvando: Boolean = false,
    val erro: String? = null
) {
    // Dados derivados ficam na camada de estado, não nas telas.
    val habitosAtivos: List<HabitoEntity>
        get() = habitos.filter { it.dataConclusao == null }

    val habitosConcluidos: List<HabitoEntity>
        get() = habitos.filter { it.dataConclusao != null }

    val totalAtivos: Int get() = habitosAtivos.size
    val totalConcluidos: Int get() = habitosConcluidos.size
}
