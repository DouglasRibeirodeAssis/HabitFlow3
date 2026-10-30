package com.example.habitflow3

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.habitflow3.data.AppDatabase
import com.example.habitflow3.data.HabitoEntity
import com.example.habitflow3.data.MetaEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaRoomTest {
    @Test
    fun registrosPermanecemDepoisDeReabrirOBanco() = runBlocking {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val nomeBanco = "habitflow3_teste.db"
        contexto.deleteDatabase(nomeBanco)
        var banco = Room.databaseBuilder(contexto, AppDatabase::class.java, nomeBanco).build()
        val idHabito = banco.habitoDao().inserir(
            HabitoEntity(nome = "Leitura", categoria = "Estudos", horario = "19:30", metaDias = 30, dataInicio = "2026-10-29")
        ).toInt()
        val idMeta = banco.metaDao().inserir(
            MetaEntity(titulo = "Ler 5 livros", descricao = "Até dezembro", prazo = "2026-12-31")
        ).toInt()
        banco.close()

        banco = Room.databaseBuilder(contexto, AppDatabase::class.java, nomeBanco).build()
        val habito = banco.habitoDao().buscarPorId(idHabito)
        val meta = banco.metaDao().buscarPorId(idMeta)
        assertNotNull(habito)
        assertNotNull(meta)
        assertEquals("Leitura", habito?.nome)
        assertEquals("Ler 5 livros", meta?.titulo)

        banco.habitoDao().atualizar(habito!!.copy(diasRealizados = 3, dataConclusao = "2026-11-01"))
        banco.metaDao().atualizar(meta!!.copy(concluida = true))
        assertEquals(3, banco.habitoDao().buscarPorId(idHabito)?.diasRealizados)
        assertTrue(banco.metaDao().buscarPorId(idMeta)?.concluida == true)
        banco.habitoDao().remover(banco.habitoDao().buscarPorId(idHabito)!!)
        banco.metaDao().remover(banco.metaDao().buscarPorId(idMeta)!!)
        assertNull(banco.habitoDao().buscarPorId(idHabito))
        assertNull(banco.metaDao().buscarPorId(idMeta))
        banco.close()
        contexto.deleteDatabase(nomeBanco)
    }
}
