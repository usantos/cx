package br.gov.caixa.loterias.core.network.mock.fixture

internal object MockMarketplaceFixtures {
    // Um contrato de backend é compartilhado com o executor nativo CX de instrumentação sem alterações.
    private val marketplaceParityFixture by lazy {
        val stream = requireNotNull(MockBackendFixtures::class.java.getResourceAsStream("/marketplace-parity-fixture.json")) {
            "Shared Marketplace parity fixture missing from classpath"
        }
        stream.bufferedReader(Charsets.UTF_8).use { reader ->
            com.google.gson.JsonParser.parseReader(reader).asJsonObject
        }
    }

    fun marketplaceParityPayload(key: String): String = marketplaceParityFixture.get(key).toString()

    fun retailers() = """[{"id":101,"codigo":"000101","numeroFormatado":"0001-01",
        "nomeFantasia":"Lotérica Centro","nome":"Centro Loterias Ltda","logradouro":"SCS Quadra 1",
        "bairro":"Asa Sul","nomeMunicipio":"Brasília","nomeUF":"DF"},{"id":102,"codigo":"000102",
        "nomeFantasia":"Lotérica da Praça","logradouro":"Praça Central","bairro":"Centro",
        "nomeMunicipio":"Brasília","nomeUF":"DF"}]"""

    fun favoriteRetailers() = """[{"codigo":101,"polo":1,"dv":0,"nomeFantasia":"Lotérica Centro",
        "nomeMunicipio":"Brasília","siglaUf":"DF"}]"""

    fun marketplace() = """{"totalRegistros":1,"paginaAtual":0,"ultimaPagina":0,"cotas":[{
        "codigoBolao":"B1","modalidade":"Mega-Sena","concurso":2900,"nomeFantasia":"Loterias Centro",
        "dataSorteio":"10/09/2026","valorCota":"12.50","qtdCotaDisponivel":4,"qtdApostas":3,
        "qtdNumeros":6,"lotericaFavorita":true,"loterica":99,"municipio":{"nome":"Brasília","uf":{"sigla":"DF"}},
        "qtdCotaTotal":10,"tipoConcurso":1,"vrPremioEstimado":"1000000","vrCotaComTarifa":10.00,
        "valorTarifaServico":2.50,"vrUltimaCotaComTarifa":12.50}]}"""

    fun marketplaceDetail() = """{"qtdCotaTotal":10,"qtdCotaDisponivel":4,"loterica":99,
        "nomeFantasia":"Loterias Centro","concurso":2900,"tipoConcurso":1,"modalidade":"MEGA_SENA","dataSorteio":"10/09/2026",
        "horaSorteio":"20:00","vrPremioEstimado":"1000000","qtdApostas":1,"qtdNumeros":6,
        "vrCotaSemTarifa":"10","vrTarifaServicoCota":2.50,"vrUltimaCotaComTarifa":"12.50","vrTarifaServicoUltimaCota":"2.50",
        "apostas":[{"indicadorSurpresinha":false,"dezenas":[1,2,3,4,5,6],"trevos":[]}]}"""
}
