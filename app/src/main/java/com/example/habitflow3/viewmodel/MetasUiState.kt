package com.example.habitflow3.viewmodel

import com.example.habitflow3.data.local.MetaEntity

data class MetaFormularioUiState(
    val id: Int? = null,
    val titulo: String = "",
    val descricao: String = "",
    val prazo: String = ""
)

data class MetasUiState(
    val metas: List<MetaEntity> = emptyList(),
    val metaSelecionada: MetaEntity? = null,
    // Formulários distintos para não misturar cadastro e edição.
    val novaMeta: MetaFormularioUiState = MetaFormularioUiState(),
    val edicaoMeta: MetaFormularioUiState = MetaFormularioUiState(),
    val carregando: Boolean = true,
    val carregandoDetalhe: Boolean = false,
    val salvando: Boolean = false,
    val erro: String? = null
) {
    val totalMetas: Int get() = metas.size
    val metasPendentes: Int get() = metas.count { !it.concluida }
    val metasConcluidas: Int get() = metas.count { it.concluida }
}
