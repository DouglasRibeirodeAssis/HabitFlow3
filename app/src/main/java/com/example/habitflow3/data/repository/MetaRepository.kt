package com.example.habitflow3.data.repository

import com.example.habitflow3.data.local.MetaDao
import com.example.habitflow3.data.local.MetaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MetaRepository(private val dao: MetaDao) {
    fun buscarTodos(): Flow<List<MetaEntity>> = dao.buscarTodos()
    fun buscarPorId(id: Int): Flow<MetaEntity?> = dao.observarPorId(id)
    fun buscarPorStatus(concluida: Boolean): Flow<List<MetaEntity>> = dao.buscarPorStatus(concluida)

    suspend fun salvar(meta: MetaEntity) = withContext(Dispatchers.IO) {
        if (meta.id == 0) dao.inserir(meta) else dao.atualizar(meta)
    }

    suspend fun remover(meta: MetaEntity) = withContext(Dispatchers.IO) {
        dao.remover(meta)
    }

    suspend fun alterarConclusao(id: Int, concluida: Boolean) = withContext(Dispatchers.IO) {
        val atual = dao.buscarPorId(id) ?: return@withContext
        dao.atualizar(atual.copy(concluida = concluida))
    }
}
