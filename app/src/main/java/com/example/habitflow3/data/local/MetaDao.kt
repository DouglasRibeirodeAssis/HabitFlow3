package com.example.habitflow3.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {
    @Insert
    suspend fun inserir(meta: MetaEntity): Long

    @Query("SELECT * FROM metas ORDER BY id DESC")
    fun buscarTodos(): Flow<List<MetaEntity>>

    @Query("SELECT * FROM metas WHERE id = :id LIMIT 1")
    fun observarPorId(id: Int): Flow<MetaEntity?>

    @Query("SELECT * FROM metas WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): MetaEntity?

    @Query("SELECT * FROM metas WHERE concluida = :concluida ORDER BY id DESC")
    fun buscarPorStatus(concluida: Boolean): Flow<List<MetaEntity>>

    @Update
    suspend fun atualizar(meta: MetaEntity): Int

    @Delete
    suspend fun remover(meta: MetaEntity): Int
}
