package com.example.habitflow3.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MetaDao {
    @Insert
    suspend fun inserir(meta: MetaEntity): Long

    @Query("SELECT * FROM metas ORDER BY id DESC")
    suspend fun buscarTodos(): List<MetaEntity>

    @Query("SELECT * FROM metas WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): MetaEntity?

    @Query("SELECT * FROM metas WHERE concluida = :concluida ORDER BY id DESC")
    suspend fun buscarPorStatus(concluida: Boolean): List<MetaEntity>

    @Update
    suspend fun atualizar(meta: MetaEntity): Int

    @Delete
    suspend fun remover(meta: MetaEntity): Int
}
