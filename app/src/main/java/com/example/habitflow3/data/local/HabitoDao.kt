package com.example.habitflow3.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitoDao {
    @Insert
    suspend fun inserir(habito: HabitoEntity): Long

    // Room emite uma nova lista automaticamente ao inserir, editar ou excluir.
    @Query("SELECT * FROM habitos ORDER BY id DESC")
    fun buscarTodos(): Flow<List<HabitoEntity>>

    @Query("SELECT * FROM habitos WHERE id = :id LIMIT 1")
    fun observarPorId(id: Int): Flow<HabitoEntity?>

    @Query("SELECT * FROM habitos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): HabitoEntity?

    @Query("SELECT * FROM habitos WHERE categoria = :categoria COLLATE NOCASE ORDER BY id DESC")
    fun buscarPorCategoria(categoria: String): Flow<List<HabitoEntity>>

    @Update
    suspend fun atualizar(habito: HabitoEntity): Int

    @Delete
    suspend fun remover(habito: HabitoEntity): Int
}
