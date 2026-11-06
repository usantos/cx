package br.gov.caixa.loterias.core.network.mock.fixture

internal object MockResponsibleGamingFixtures {
    fun assessment() = """{"retornoPerguntaResposta":[
        {"id":1,"descricaoPergunta":"Você aposta mais do que planeja?","ordemPergunta":1,"ativo":true},
        {"id":2,"descricaoPergunta":"Você já tentou apostar menos?","ordemPergunta":2,"ativo":true},
        {"id":3,"descricaoPergunta":"Apostar já causou problemas para você?","ordemPergunta":3,"ativo":true}]}"""

    fun socialTransfers() = """{"arrecadacaoAcumulada":1000000,"arrecadacaoUltimaSemana":100000,
        "dataBase":"04/09/2026","percentualProgramasSociais":48,"percentualEducacao":30,
        "percentualCultura":10,"percentualEsporte":5,"percentualSeguranca":2,
        "percentualSeguridade":1,"percentualOutros":4}"""
}
