package com.example.habitflow3.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.time.temporal.ChronoUnit

private val formato = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)

fun dataAtualIso(): String = LocalDate.now().toString()

fun formatarData(iso: String): String = runCatching {
    LocalDate.parse(iso).format(formato)
}.getOrDefault(iso)

fun interpretarData(valor: String): String? = runCatching {
    LocalDate.parse(valor.trim(), formato).toString()
}.getOrNull()

fun periodoEmDias(inicioIso: String, fimIso: String): Long = runCatching {
    ChronoUnit.DAYS.between(LocalDate.parse(inicioIso), LocalDate.parse(fimIso)) + 1L
}.getOrDefault(0L)

fun dataNaoFutura(iso: String): Boolean = runCatching {
    !LocalDate.parse(iso).isAfter(LocalDate.now())
}.getOrDefault(false)
