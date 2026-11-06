package com.example.habitflow3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.interpretarData
import com.example.habitflow3.data.local.MetaEntity
import com.example.habitflow3.data.repository.MetaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MetasViewModel(private val repository: MetaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MetasUiState())
    val uiState: StateFlow<MetasUiState> = _uiState.asStateFlow()
    private var consultaDetalhe: Job? = null

    init {
        viewModelScope.launch {
            repository.buscarTodos()
                .catch { erro -> registrarErro(erro) }
                .collect { lista ->
                    _uiState.update { estado -> estado.copy(metas = lista, carregando = false) }
                }
        }
    }

    fun selecionarMeta(id: Int) {
        consultaDetalhe?.cancel()
        _uiState.update { it.copy(metaSelecionada = null, carregandoDetalhe = true) }
        consultaDetalhe = viewModelScope.launch {
            repository.buscarPorId(id)
                .catch { erro -> registrarErro(erro) }
                .collect { item ->
                    _uiState.update {
                        it.copy(metaSelecionada = item, carregandoDetalhe = false)
                    }
                }
        }
    }

    // Formulário de cadastro: valores vêm do UiState.
    fun atualizarTituloNovaMeta(valor: String) = mudarNovaMeta { it.copy(titulo = valor) }
    fun atualizarDescricaoNovaMeta(valor: String) = mudarNovaMeta { it.copy(descricao = valor) }
    fun atualizarPrazoNovaMeta(valor: String) = mudarNovaMeta { it.copy(prazo = valor) }

    private fun mudarNovaMeta(mudanca: (MetaFormularioUiState) -> MetaFormularioUiState) {
        _uiState.update { it.copy(novaMeta = mudanca(it.novaMeta)) }
    }

    // Formulário de edição não interfere no cadastro.
    fun prepararEdicaoMeta(meta: MetaEntity) {
        _uiState.update {
            it.copy(edicaoMeta = MetaFormularioUiState(
                id = meta.id,
                titulo = meta.titulo,
                descricao = meta.descricao,
                prazo = if (meta.prazo.isBlank()) "" else formatarData(meta.prazo)
            ))
        }
    }

    fun cancelarEdicaoMeta() {
        _uiState.update { it.copy(edicaoMeta = MetaFormularioUiState()) }
    }

    fun atualizarTituloEdicao(valor: String) = mudarEdicao { it.copy(titulo = valor) }
    fun atualizarDescricaoEdicao(valor: String) = mudarEdicao { it.copy(descricao = valor) }
    fun atualizarPrazoEdicao(valor: String) = mudarEdicao { it.copy(prazo = valor) }

    private fun mudarEdicao(mudanca: (MetaFormularioUiState) -> MetaFormularioUiState) {
        _uiState.update { it.copy(edicaoMeta = mudanca(it.edicaoMeta)) }
    }

    fun salvarNovaMeta(aoSucesso: () -> Unit = {}) {
        salvarMeta(_uiState.value.novaMeta, nova = true, aoSucesso = aoSucesso)
    }

    fun salvarEdicaoMeta(aoSucesso: () -> Unit = {}) {
        salvarMeta(_uiState.value.edicaoMeta, nova = false, aoSucesso = aoSucesso)
    }

    private fun salvarMeta(
        formulario: MetaFormularioUiState,
        nova: Boolean,
        aoSucesso: () -> Unit
    ) {
        if (_uiState.value.salvando) return
        _uiState.update { it.copy(salvando = true, erro = null) }
        viewModelScope.launch {
            try {
                val titulo = formulario.titulo.trim()
                require(titulo.isNotBlank()) { "Informe o título da meta." }
                val prazo = if (formulario.prazo.isBlank()) "" else interpretarData(formulario.prazo)
                require(prazo != null) { "O prazo precisa estar no formato dd/MM/aaaa." }
                require(nova || formulario.id != null) { "Nenhuma meta foi selecionada para edição." }
                val existente = formulario.id?.let { id ->
                    repository.buscarPorId(id).first()
                        ?: throw IllegalStateException("Meta não encontrada para edição.")
                }
                repository.salvar(MetaEntity(
                    id = existente?.id ?: 0,
                    titulo = titulo,
                    descricao = formulario.descricao.trim(),
                    prazo = prazo,
                    concluida = existente?.concluida ?: false
                ))
                _uiState.update { estado ->
                    if (nova) estado.copy(novaMeta = MetaFormularioUiState())
                    else estado.copy(edicaoMeta = MetaFormularioUiState())
                }
                aoSucesso()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                registrarErro(e)
            } finally {
                _uiState.update { it.copy(salvando = false) }
            }
        }
    }

    fun remover(meta: MetaEntity, aoSucesso: () -> Unit) = executar(aoSucesso) {
        repository.remover(meta)
    }

    fun alterarConclusao(meta: MetaEntity, concluida: Boolean) = executar {
        repository.alterarConclusao(meta.id, concluida)
    }

    fun limparErro() { _uiState.update { it.copy(erro = null) } }

    private fun registrarErro(e: Throwable) {
        _uiState.update {
            it.copy(erro = e.localizedMessage ?: "Erro inesperado.", carregando = false, carregandoDetalhe = false)
        }
    }

    private fun executar(aoSucesso: () -> Unit = {}, acao: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                acao()
                aoSucesso()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                registrarErro(e)
            }
        }
    }
}
