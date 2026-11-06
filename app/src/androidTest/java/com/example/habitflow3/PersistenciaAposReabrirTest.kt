package com.example.habitflow3

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import com.example.habitflow3.data.local.AppDatabase
import com.example.habitflow3.data.local.HabitoEntity
import com.example.habitflow3.data.local.MetaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaAposReabrirTest {
    @Test
    fun registrosContinuamNoBancoDepoisDeFecharEReabrir() = runBlocking {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        val nome = "habitflow3_mvvm_teste.db"
        contexto.deleteDatabase(nome)
        try {
            var bd = Room.databaseBuilder(contexto, AppDatabase::class.java, nome).build()
            bd.habitoDao().inserir(HabitoEntity(nome="Caminhar", categoria="Saúde", horario="07:00", metaDias=10, dataInicio="2026-01-01"))
            bd.metaDao().inserir(MetaEntity(titulo="Terminar curso", descricao="", prazo=""))
            bd.close()

            bd = Room.databaseBuilder(contexto, AppDatabase::class.java, nome).build()
            try {
                assertEquals("Caminhar", bd.habitoDao().buscarTodos().first().single().nome)
                assertEquals("Terminar curso", bd.metaDao().buscarTodos().first().single().titulo)
            } finally { bd.close() }
        } finally { contexto.deleteDatabase(nome) }
    }
}
