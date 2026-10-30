package com.example.habitflow3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

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
