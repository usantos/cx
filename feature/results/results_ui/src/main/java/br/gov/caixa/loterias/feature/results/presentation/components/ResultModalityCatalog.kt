package br.gov.caixa.loterias.feature.results.presentation.components

import br.gov.caixa.loterias.feature.results.presentation.formatting.localizedMessage as messageText

data class ResultModality(val code: String, val name: String)

val officialModalities = listOf(
    ResultModality("megasena", "Mega-Sena"),
    ResultModality("lotofacil", messageText("lotofacil_d3ed7c")),
    ResultModality("quina", "Quina"),
    ResultModality("lotomania", "Lotomania"),
    ResultModality("timemania", "Timemania"),
    ResultModality("duplasena", "Dupla Sena"),
    ResultModality("diadesorte", messageText("dia_de_sorte_b5c7a5")),
    ResultModality("supersete", "Super Sete"),
    ResultModality("maismilionaria", messageText("milionaria_11be79")),
    ResultModality("loteca", "Loteca"),
    ResultModality("federal", "Federal")
)
