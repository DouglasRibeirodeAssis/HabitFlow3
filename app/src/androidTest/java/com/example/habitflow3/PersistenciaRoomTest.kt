package com.example.habitflow3

import androidx.room.Room
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habitflow3.data.local.AppDatabase
import com.example.habitflow3.data.local.HabitoEntity
import com.example.habitflow3.data.local.MetaEntity
import com.example.habitflow3.data.repository.HabitoRepository
import com.example.habitflow3.data.repository.MetaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaRoomTest {
    private lateinit var banco: AppDatabase

    @Before
    fun iniciar() {
        banco = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(), AppDatabase::class.java
        ).build()
    }

    @After
    fun finalizar() { banco.close() }

    @Test
    fun habitos_crud_reageAoFlow() = runBlocking {
        val repo = HabitoRepository(banco.habitoDao())
        assertTrue(repo.buscarTodos().first().isEmpty())
        repo.salvar(HabitoEntity(nome="Ler", categoria="Estudo", horario="08:00", metaDias=7, dataInicio="2026-01-01"))
        val novo = repo.buscarTodos().first().single()
        assertEquals("Ler", novo.nome)
        assertNotNull(repo.buscarPorId(novo.id).first())
        repo.registrarDia(novo.id)
        assertEquals(1, repo.buscarTodos().first().single().diasRealizados)
        repo.salvar(repo.buscarTodos().first().single().copy(nome="Ler todos os dias"))
        assertEquals("Ler todos os dias", repo.buscarTodos().first().single().nome)
        repo.remover(repo.buscarTodos().first().single())
        assertTrue(repo.buscarTodos().first().isEmpty())
        assertNull(repo.buscarPorId(novo.id).first())
    }

    @Test
    fun metas_crud_reageAoFlow() = runBlocking {
        val repo = MetaRepository(banco.metaDao())
        repo.salvar(MetaEntity(titulo="Estudar", descricao="20 minutos", prazo="2026-12-31"))
        val meta = repo.buscarTodos().first().single()
        assertFalse(meta.concluida)
        repo.alterarConclusao(meta.id, true)
        assertTrue(repo.buscarTodos().first().single().concluida)
        assertEquals(1, repo.buscarPorStatus(true).first().size)
        repo.salvar(repo.buscarTodos().first().single().copy(descricao="30 minutos"))
        assertEquals("30 minutos", repo.buscarPorId(meta.id).first()?.descricao)
        repo.remover(repo.buscarTodos().first().single())
        assertTrue(repo.buscarTodos().first().isEmpty())
    }
}
