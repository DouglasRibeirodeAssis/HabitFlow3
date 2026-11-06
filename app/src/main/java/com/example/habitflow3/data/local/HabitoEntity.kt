package com.example.habitflow3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Nomes de tabela/campos idênticos aos do Trabalho 3: banco v1 preservado.
@Entity(tableName = "habitos")
data class HabitoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val categoria: String,
    val horario: String,
    val metaDias: Int,
    val dataInicio: String,
    val dataConclusao: String? = null,
    val diasRealizados: Int = 0
)
