package com.example.habitflow3.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DatasTest {
    @Test
    fun interpretaDatasValidas() {
        assertEquals("2026-10-29", interpretarData("29/10/2026"))
        assertEquals("29/10/2026", formatarData("2026-10-29"))
    }

    @Test
    fun rejeitaDatasImpossiveis() {
        assertNull(interpretarData("31/02/2026"))
    }

    @Test
    fun calculaDiasIncluindoInicioEFim() {
        assertEquals(5L, periodoEmDias("2026-10-01", "2026-10-05"))
    }
}
