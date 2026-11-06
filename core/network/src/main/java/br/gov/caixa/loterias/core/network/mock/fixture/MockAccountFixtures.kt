package br.gov.caixa.loterias.core.network.mock.fixture

internal object MockAccountFixtures {
    fun profile() = """{"cpf":"12345678901","email":"mock@caixa.gov.br","nome":"Pessoa Mock",
        "dataNascimento":"1990-01-01","telefone":{"ddd":"61","numero":"999999999"},
        "cep":"70000000","aceitaReceberNoticias":true,"municipioId":{"idUF":7,"numero":1,
        "digitoVerificador":0},"sexo":"F","limiteMinimoDiario":10,"limiteDiario":100,
        "limiteDiarioAutorizado":200}"""

    fun municipalities() = """[{"id":{"idUF":7,"numero":1,"digitoVerificador":0},
        "nome":"Brasília","uf":{"id":7,"sigla":"DF","nome":"Distrito Federal"}}]"""

    fun neighborhoods() = """[{"id":1,"nome":"Centro","municipio":{"id":{"idUF":7,
        "numero":1,"digitoVerificador":0},"nome":"Brasília","uf":{"id":7,"sigla":"DF"}}}]"""

    fun session() = """{"nome":"Pessoa Mock","email":"mock@caixa.gov.br","primeiroNome":"Pessoa",
        "limiteDiario":100,"suspensaoTemporariaApostador":false,"responderAutoavaliacao":false}"""
}
