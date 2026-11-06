package com.example.habitflow3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.interpretarData
import com.example.habitflow3.data.periodoEmDias
import com.example.habitflow3.data.local.HabitoEntity
import com.example.habitflow3.data.repository.HabitoRepository
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

class HabitosViewModel(private val repository: HabitoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitosUiState())
    val uiState: StateFlow<HabitosUiState> = _uiState.asStateFlow()
    private var consultaDetalhe: Job? = null

    init {
        viewModelScope.launch {
            repository.buscarTodos()
                .catch { erro -> registrarErro(erro) }
                .collect { lista ->
                    _uiState.update { estado -> estado.copy(habitos = lista, carregando = false) }
                }
        }
    }

    fun selecionarHabito(id: Int) {
        consultaDetalhe?.cancel()
        _uiState.update { it.copy(habitoSelecionado = null, carregandoDetalhe = true) }
        consultaDetalhe = viewModelScope.launch {
            repository.buscarPorId(id)
                .catch { erro -> registrarErro(erro) }
                .collect { item ->
                    _uiState.update {
                        it.copy(habitoSelecionado = item, carregandoDetalhe = false)
                    }
                }
        }
    }

    fun prepararNovoHabito() {
        _uiState.update { it.copy(formulario = HabitoFormularioUiState()) }
    }

    fun prepararEdicaoHabito(habito: HabitoEntity) {
        _uiState.update { estado ->
            estado.copy(formulario = HabitoFormularioUiState(
                id = habito.id,
                nome = habito.nome,
                categoria = habito.categoria,
                horario = habito.horario,
                metaDias = habito.metaDias.toString(),
                inicio = formatarData(habito.dataInicio)
            ))
        }
    }

    fun atualizarNome(nome: String) = atualizarFormulario { it.copy(nome = nome) }
    fun atualizarCategoria(categoria: String) = atualizarFormulario { it.copy(categoria = categoria) }
    fun atualizarHorario(horario: String) = atualizarFormulario { it.copy(horario = horario) }
    fun atualizarMetaDias(dias: String) = atualizarFormulario { it.copy(metaDias = dias) }
    fun atualizarInicio(inicio: String) = atualizarFormulario { it.copy(inicio = inicio) }

    private fun atualizarFormulario(
        mudanca: (HabitoFormularioUiState) -> HabitoFormularioUiState
    ) {
        _uiState.update { estado -> estado.copy(formulario = mudanca(estado.formulario)) }
    }

    fun salvarFormulario(aoSucesso: () -> Unit) {
        if (_uiState.value.salvando) return
        val formulario = _uiState.value.formulario
        _uiState.update { it.copy(salvando = true, erro = null) }
        viewModelScope.launch {
            try {
                val nome = formulario.nome.trim()
                val categoria = formulario.categoria.trim()
                val dias = formulario.metaDias.toIntOrNull()
                val inicioIso = interpretarData(formulario.inicio)
                val horario = formulario.horario.trim()

                require(nome.isNotBlank() && categoria.isNotBlank()) {
                    "Preencha o nome e a categoria."
                }
                require(Regex("^(?:[01][0-9]|2[0-3]):[0-5][0-9]$").matches(horario)) {
                    "Informe um horário válido, como 08:00."
                }
                require(dias != null && dias > 0) {
                    "A meta de dias precisa ser maior que zero."
                }
                require(inicioIso != null) {
                    "Informe uma data válida no formato dd/MM/aaaa."
                }
                // Busca o registro atualizado para preservar conclusão/progresso.
                val existente = formulario.id?.let { id ->
                    repository.buscarPorId(id).first()
                        ?: throw IllegalStateException("Hábito não encontrado para edição.")
                }
                if (existente?.dataConclusao != null) {
                    require(periodoEmDias(inicioIso, existente.dataConclusao) > 0) {
                        "A data de início precisa ser anterior à conclusão."
                    }
                }
                repository.salvar(HabitoEntity(
                    id = existente?.id ?: 0,
                    nome = nome,
                    categoria = categoria,
                    horario = horario,
                    metaDias = dias,
                    dataInicio = inicioIso,
                    dataConclusao = existente?.dataConclusao,
                    diasRealizados = (existente?.diasRealizados ?: 0).coerceAtMost(dias)
                ))
                _uiState.update { it.copy(formulario = HabitoFormularioUiState()) }
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

    fun remover(habito: HabitoEntity, aoSucesso: () -> Unit) = executar(aoSucesso) {
        repository.remover(habito)
    }

    fun registrarDia(habito: HabitoEntity) = executar { repository.registrarDia(habito.id) }
    fun concluir(habito: HabitoEntity) = executar { repository.concluir(habito.id) }
    fun reabrir(habito: HabitoEntity) = executar { repository.reabrir(habito.id) }

    fun limparErro() { _uiState.update { it.copy(erro = null) } }

    private fun registrarErro(e: Throwable) {
        _uiState.update {
            it.copy(
                erro = e.localizedMessage ?: "Erro inesperado.",
                carregando = false,
                carregandoDetalhe = false
            )
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
