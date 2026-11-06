package com.example.habitflow3

import com.example.habitflow3.data.formatarData
import com.example.habitflow3.data.interpretarData
import com.example.habitflow3.data.periodoEmDias
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DatasTest {
    @Test fun converteDatasValidas() {
        assertEquals("2026-11-05", interpretarData("05/11/2026"))
        assertEquals("05/11/2026", formatarData("2026-11-05"))
    }
    @Test fun rejeitaDatasInvalidas() { assertNull(interpretarData("31/02/2026")) }
    @Test fun contaDiasCorridosComInicioEFinalInclusivos() {
        assertEquals(3L, periodoEmDias("2026-01-01", "2026-01-03"))
    }
}
