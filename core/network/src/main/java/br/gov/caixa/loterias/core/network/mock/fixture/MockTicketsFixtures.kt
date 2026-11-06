package br.gov.caixa.loterias.core.network.mock.fixture

internal object MockTicketsFixtures {
    fun betReceipt(path: String): String {
        val id = path.split('/').firstOrNull { it.toLongOrNull() != null }?.toLongOrNull() ?: 100L
        val paid = path.endsWith("/comprovante-premio")
        return """{"nsbi":"MOCK-$id","aposta":{"id":$id,"modalidade":"MEGA_SENA",
            "concursoInicial":2900,"situacao":{"valor":${if (paid) 11 else 100},"descricao":"${if (paid) "PAGA" else "PREMIADA"}"},
            "dataEfetivacao":"13/10/2026","numerosSelecionados":[[5,10,23,34,42,58]]},
            "premio":{"valorLiquido":1250,"dataPagamento":"${if (paid) "13/10/2026" else ""}","localPagamento":"${if (paid) "Lotérica" else ""}"}}"""
    }

    fun confirmedBets() = """{"totalRegistros":1,"resultado":[{"id":100,"modalidade":"MEGA_SENA",
        "numeroConcurso":2900,"situacao":{"valor":11,"descricao":"PAGA"},"teimosinha":false}]}"""

    fun confirmedBetsConfiguration() = """{
        "ordenacaoPadrao":{"valor":3,"descricao":"Mais recentes"},
        "situacaoPadrao":{"valor":1,"descricao":"Todas"},
        "periodoPadrao":{"mes":{"valor":45,"descricao":"Últimos 45 dias"}},
        "situacoes":[{"valor":1,"descricao":"Todas"},{"valor":2,"descricao":"Pagas"},{"valor":3,"descricao":"Prescritas"}],
        "ordenacoes":[{"valor":3,"descricao":"Mais recentes"},{"valor":2,"descricao":"Mais antigas"}],
        "periodos":[{"mes":{"valor":45,"descricao":"Últimos 45 dias"}}],
        "listaTipoAposta":[{"valor":1,"descricao":"Todas"},{"valor":2,"descricao":"Simples"}],
        "listaTipoConcurso":[{"valor":1,"descricao":"Regular"},{"valor":2,"descricao":"Especial"}]
    }""".trimIndent()

    fun purchaseMonths() = """[
        {"mes":{"valor":9,"descricao":"Setembro"},"ano":2026},
        {"mes":{"valor":8,"descricao":"Agosto"},"ano":2026}
    ]""".trimIndent()

    fun purchaseStatuses() = """[
        {"id":2,"descricao":"Pagamento confirmado"},
        {"id":9,"descricao":"Pendente - pagamento"},
        {"id":12,"descricao":"Pagamento - cancelado"}
    ]""".trimIndent()

    fun purchases() = """{"lista":[{
        "id":99,"dataInicioCompra":"04/09/2026","dataFinalizacaoCompra":"04/09/2026",
        "horaFinalizacaoCompra":"14:35","situacao":{"valor":8,"descricao":"Finalizada"},
        "meioPagamento":{"valor":6,"nome":"PIX"},"valorTotal":35.00,"e2eId":"E29001101202609041435MOCK"
    }]}""".trimIndent()

    fun purchaseDetails() = """{"dataHoraServidor":"2026-09-04T14:35:00.000","compra":{
        "id":99,"dataInicioCompra":"04/09/2026","dataFinalizacaoCompra":"04/09/2026",
        "horaFinalizacaoCompra":"14:35","situacao":{"valor":8,"descricao":"Finalizada"},
        "meioPagamento":{"valor":6,"nome":"PIX"},"valorTotal":35.00,
        "e2eId":"E29001101202609041435MOCK"},"apostas":[
        {"id":100,"modalidade":{"valor":2,"descricao":"Mega-Sena"},"numeroConcurso":2900,
         "tipoConcurso":{"valor":1,"descricao":"Regular"},"situacao":{"valor":2,"descricao":"Paga"},
         "dataEfetivacao":"04/09/2026","numerosSelecionados":[1,7,13,22,35,49],
         "indicadorCotaBolao":false,"indicadorSurpresinha":"NAO_SURPRESINHA"}
    ]}""".trimIndent()

    fun teimosinhaBet() = """{"totalRegistros":1,"resultado":[{"id":100,"modalidade":"MEGA_SENA",
        "numeroConcurso":2900,"situacao":"RESGATADA","teimosinha":true,"apostaOriginal":true,
        "houveResgate":true,"idApostaTroca":101}]}"""

    fun prizeBet() = """{"totalRegistros":1,"resultado":[{"id":100,"modalidade":"MEGA_SENA",
        "numeroConcurso":2900,"situacao":"PREMIADA","valorPremio":1000}]}"""
}
