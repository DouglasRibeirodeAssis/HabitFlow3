package com.example.habitflow3.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitoDao {
    @Insert
    suspend fun inserir(habito: HabitoEntity): Long

    @Query("SELECT * FROM habitos ORDER BY id DESC")
    suspend fun buscarTodos(): List<HabitoEntity>

    @Query("SELECT * FROM habitos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): HabitoEntity?

    @Query("SELECT * FROM habitos WHERE categoria = :categoria COLLATE NOCASE ORDER BY id DESC")
    suspend fun buscarPorCategoria(categoria: String): List<HabitoEntity>

    @Update
    suspend fun atualizar(habito: HabitoEntity): Int

    @Delete
    suspend fun remover(habito: HabitoEntity): Int
}
