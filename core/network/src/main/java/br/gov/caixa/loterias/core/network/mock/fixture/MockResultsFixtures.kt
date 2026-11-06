package br.gov.caixa.loterias.core.network.mock.fixture

internal object MockResultsFixtures {
    fun result() = """{"aviso":"Resultado mock","acumulou":false,"arrecadacaoTotal":"0.00",
        "numerosSorteadosPrimeiroSorteio":[5,10,23,34,42,58],"concurso":{"modalidade":"Mega-Sena",
        "numero":2800,"dataSorteio":"10/09/2026"},"premiacoesPrimeiroSorteio":[]}"""
}
