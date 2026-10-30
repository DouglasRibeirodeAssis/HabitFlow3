package com.example.habitflow3.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.habitflow3.data.AppDatabase
import com.example.habitflow3.data.HabitoEntity
import com.example.habitflow3.data.MetaEntity
import com.example.habitflow3.data.dataAtualIso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitFlowViewModel(application: Application) : AndroidViewModel(application) {
    private val banco = AppDatabase.obter(application)
    private val habitoDao = banco.habitoDao()
    private val metaDao = banco.metaDao()

    var habitos by mutableStateOf<List<HabitoEntity>>(emptyList())
        private set

    var metas by mutableStateOf<List<MetaEntity>>(emptyList())
        private set

    suspend fun carregarHabitos() {
        habitos = withContext(Dispatchers.IO) { habitoDao.buscarTodos() }
    }

    suspend fun carregarMetas() {
        metas = withContext(Dispatchers.IO) { metaDao.buscarTodos() }
    }

    suspend fun carregarTudo() {
        carregarHabitos()
        carregarMetas()
    }

    suspend fun buscarHabitoPorId(id: Int): HabitoEntity? = withContext(Dispatchers.IO) {
        habitoDao.buscarPorId(id)
    }

    suspend fun buscarMetaPorId(id: Int): MetaEntity? = withContext(Dispatchers.IO) {
        metaDao.buscarPorId(id)
    }

    suspend fun salvarHabito(habito: HabitoEntity) {
        withContext(Dispatchers.IO) {
            if (habito.id == 0) habitoDao.inserir(habito) else habitoDao.atualizar(habito)
        }
        carregarHabitos()
    }

    suspend fun removerHabito(habito: HabitoEntity) {
        withContext(Dispatchers.IO) { habitoDao.remover(habito) }
        carregarHabitos()
    }

    suspend fun registrarDia(habito: HabitoEntity) {
        if (habito.dataConclusao != null) return
        val novoTotal = (habito.diasRealizados + 1).coerceAtMost(habito.metaDias)
        salvarHabito(habito.copy(diasRealizados = novoTotal))
    }

    suspend fun concluirHabito(habito: HabitoEntity) {
        salvarHabito(habito.copy(dataConclusao = dataAtualIso()))
    }

    suspend fun reabrirHabito(habito: HabitoEntity) {
        salvarHabito(habito.copy(dataConclusao = null))
    }

    suspend fun salvarMeta(meta: MetaEntity) {
        withContext(Dispatchers.IO) {
            if (meta.id == 0) metaDao.inserir(meta) else metaDao.atualizar(meta)
        }
        carregarMetas()
    }

    suspend fun removerMeta(meta: MetaEntity) {
        withContext(Dispatchers.IO) { metaDao.remover(meta) }
        carregarMetas()
    }

    suspend fun alterarConclusaoMeta(meta: MetaEntity, concluida: Boolean) {
        salvarMeta(meta.copy(concluida = concluida))
    }
}
