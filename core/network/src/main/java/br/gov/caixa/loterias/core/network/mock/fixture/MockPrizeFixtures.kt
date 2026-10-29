package br.gov.caixa.loterias.core.network.mock.fixture

internal object MockPrizeFixtures {
    fun prizeDetails() = """{"aposta":{"id":100,"modalidade":"MEGA_SENA","concursoInicial":2800,"concursoAlvo":2800},"premio":{"valorBruto":1250,"valorLiquido":1250},
        "nsbi":"MOCK-NSBI","mensagemInformativaIR":"Valores sujeitos às regras fiscais vigentes.",
        "meiosResgate":[{"id":6,"descricao":"PIX"},{"id":1,"descricao":"Mercado Pago"},
        {"id":9660,"descricao":"Lotérica"},{"id":9770,"descricao":"Agência Caixa","canal":"SISPL"}]}"""

    fun lotteryRedemptionCode() = """{"nsb":"MOCK-NSB","codigo":"123456",
        "validade":15,"qrCode":"CAIXA|LOTERIAS|RESGATE|MOCK-100"}"""

    fun pixBeneficiary() = """{"cpfMascarado":"***.456.789-**","nome":"PESSOA MOCK",
        "chave":"mock@caixa.gov.br","nomeBanco":"CAIXA ECONÔMICA FEDERAL"}"""
}
