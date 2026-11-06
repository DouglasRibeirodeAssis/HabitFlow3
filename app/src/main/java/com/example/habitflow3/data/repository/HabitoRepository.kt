package com.example.habitflow3.data.repository

import com.example.habitflow3.data.dataAtualIso
import com.example.habitflow3.data.dataNaoFutura
import com.example.habitflow3.data.local.HabitoDao
import com.example.habitflow3.data.local.HabitoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

// ÚNICO acesso ao DAO: ViewModel e telas conversam só com o Repository.
class HabitoRepository(private val dao: HabitoDao) {
    fun buscarTodos(): Flow<List<HabitoEntity>> = dao.buscarTodos()
    fun buscarPorId(id: Int): Flow<HabitoEntity?> = dao.observarPorId(id)
    fun buscarPorCategoria(categoria: String): Flow<List<HabitoEntity>> = dao.buscarPorCategoria(categoria)

    suspend fun salvar(habito: HabitoEntity) = withContext(Dispatchers.IO) {
        if (habito.id == 0) dao.inserir(habito) else dao.atualizar(habito)
    }

    suspend fun remover(habito: HabitoEntity) = withContext(Dispatchers.IO) {
        dao.remover(habito)
    }

    suspend fun registrarDia(id: Int) = withContext(Dispatchers.IO) {
        val atual = dao.buscarPorId(id) ?: return@withContext
        if (atual.dataConclusao != null) return@withContext
        val novoTotal = (atual.diasRealizados + 1).coerceAtMost(atual.metaDias)
        dao.atualizar(atual.copy(diasRealizados = novoTotal))
    }

    suspend fun concluir(id: Int) = withContext(Dispatchers.IO) {
        val atual = dao.buscarPorId(id) ?: return@withContext
        require(dataNaoFutura(atual.dataInicio)) { "A data de início não pode ser posterior ao dia de hoje." }
        dao.atualizar(atual.copy(dataConclusao = dataAtualIso()))
    }

    suspend fun reabrir(id: Int) = withContext(Dispatchers.IO) {
        val atual = dao.buscarPorId(id) ?: return@withContext
        dao.atualizar(atual.copy(dataConclusao = null))
    }
}
