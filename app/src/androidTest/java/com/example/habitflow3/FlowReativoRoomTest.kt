package com.example.habitflow3

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habitflow3.data.local.AppDatabase
import com.example.habitflow3.data.local.HabitoEntity
import com.example.habitflow3.data.local.MetaEntity
import com.example.habitflow3.data.repository.HabitoRepository
import com.example.habitflow3.data.repository.MetaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlowReativoRoomTest {
    private lateinit var banco: AppDatabase

    @Before
    fun preparar() {
        banco = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(), AppDatabase::class.java
        ).build()
    }

    @After
    fun finalizar() { banco.close() }

    @Test
    fun habitos_reagemInsercaoEdicaoExclusaoNaMesmaColeta() = runBlocking {
        val repository = HabitoRepository(banco.habitoDao())
        val eventos = Channel<List<HabitoEntity>>(Channel.UNLIMITED)
        val trabalho = launch(Dispatchers.Default) {
            repository.buscarTodos().collect { eventos.send(it) }
        }
        try {
            assertTrue(withTimeout(5_000) { eventos.receive() }.isEmpty())
            repository.salvar(HabitoEntity(
                nome = "Caminhar", categoria = "Saúde", horario = "07:00",
                metaDias = 10, dataInicio = "2026-01-01"
            ))
            val inserido = withTimeout(5_000) { eventos.receive() }.single()
            assertEquals("Caminhar", inserido.nome)
            repository.salvar(inserido.copy(nome = "Caminhar cedo"))
            assertEquals("Caminhar cedo", withTimeout(5_000) { eventos.receive() }.single().nome)
            repository.remover(inserido)
            assertTrue(withTimeout(5_000) { eventos.receive() }.isEmpty())
        } finally {
            trabalho.cancelAndJoin()
            eventos.close()
        }
    }

    @Test
    fun metas_reagemCadastroStatusExclusaoNaMesmaColeta() = runBlocking {
        val repository = MetaRepository(banco.metaDao())
        val eventos = Channel<List<MetaEntity>>(Channel.UNLIMITED)
        val trabalho = launch(Dispatchers.Default) {
            repository.buscarTodos().collect { eventos.send(it) }
        }
        try {
            assertTrue(withTimeout(5_000) { eventos.receive() }.isEmpty())
            repository.salvar(MetaEntity(titulo = "Estudar", descricao = "Kotlin", prazo = ""))
            val inserida = withTimeout(5_000) { eventos.receive() }.single()
            assertEquals("Estudar", inserida.titulo)
            repository.alterarConclusao(inserida.id, true)
            assertTrue(withTimeout(5_000) { eventos.receive() }.single().concluida)
            repository.remover(inserida)
            assertTrue(withTimeout(5_000) { eventos.receive() }.isEmpty())
        } finally {
            trabalho.cancelAndJoin()
            eventos.close()
        }
    }
}
