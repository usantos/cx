package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.tickets.TicketQuery
import br.gov.caixa.loterias.core.network.ApiEnvelope
import br.gov.caixa.loterias.core.network.AppParametersDto
import br.gov.caixa.loterias.core.network.AppParametersMapDto
import br.gov.caixa.loterias.core.network.BetsHistoryApi
import br.gov.caixa.loterias.core.network.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.LegacyBackendApi
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.lang.reflect.Proxy
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteTicketsRepositoryTest {
    @Test fun `real recurring quantity and exchange identify the original paid bet`() = runTest {
        val bets = RemoteTicketsRepository(FakeTicketsRemote(bets = json("""{"apostas":[
            {"id":41,"modalidade":"QUINA","quantidadeTeimosinhas":3,"troca":false,"situacao":{"valor":11}},
            {"id":42,"modalidade":"QUINA","quantidadeTeimosinhas":3,"troca":true,"apostaOriginal":true,"situacao":{"valor":11}}
        ]}"""))).confirmedBets()
        assertTrue(bets[0].originalRecurringBet)
        assertFalse(bets[1].originalRecurringBet)
    }

    @Test fun `history page transports actual backend even when there are no bets`() = runTest {
        val backend = br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.MICROSERVICE
        val remote = FakeTicketsRemote(backend = backend)
        val result = RemoteTicketsRepository(remote).confirmedBetsPage(mapOf("mes" to "45"))
        assertEquals(backend, result.backend)
        assertTrue(result.bets.isEmpty())
        assertEquals(mapOf("mes" to "45"), remote.filters)
    }

    @Test fun `repository maps nested bets fallbacks flags and distinct ids`() = runTest {
        val json = json(
            """{
              "payload": {"apostas": [
                {"id":1,"modalidade":{"descricao":"MEGA_SENA"},"numeroConcurso":2800,
                 "dataEfetivacao":"01/09/2026","situacao":{"descricao":"PREMIADA"},
                 "numerosSelecionados":[[1,2,3],[4,5,6]],"valorPremio":"10.50","apostaOriginal":true,
                 "houveResgate":true,"idApostaTroca":9},
                {"id":1,"modalidade":"duplicada","numeroConcurso":1},
                {"id":2,"modalidade":"DIA-DE-SORTE","concursoAlvo":{"numero":33},
                 "dataCompra":"02/09/2026","situacao":"PREMIO_RESGATADO","dezenas":[7,"x",[8]],
                 "valorPremio":"inválido","teimosinha":true},
                {"id":"inválido","modalidade":"quina"},
                {"id":3,"campo":"não é aposta"}
              ]}}""",
        )
        val remote = FakeTicketsRemote(bets = json)
        val repository = RemoteTicketsRepository(remote)

        val bets = repository.confirmedBets(mapOf("situacao" to "PREMIADA"))

        assertEquals(mapOf("situacao" to "PREMIADA"), remote.filters)
        assertEquals(2, bets.size)
        assertEquals("Mega Sena", bets[0].modality)
        assertEquals(2800, bets[0].contest)
        assertEquals(listOf(1, 2, 3, 4, 5, 6), bets[0].numbers)
        assertEquals(BigDecimal("10.50"), bets[0].prize)
        assertTrue(bets[0].originalRecurringBet)
        assertTrue(bets[0].redeemedPrize)
        assertEquals(9L, bets[0].exchangeBetId)
        assertEquals("Dia De Sorte", bets[1].modality)
        assertEquals(33, bets[1].contest)
        assertEquals("02/09/2026", bets[1].date)
        assertEquals(listOf(7, 8), bets[1].numbers)
        assertEquals(BigDecimal.ZERO, bets[1].prize)
        assertTrue(bets[1].redeemedPrize)
    }

    @Test fun `repository preserves every special bet complement without flattening its contract`() = runTest {
        val payload = json(
            """{"apostas":[
              {"id":10,"modalidade":{"valor":9,"descricao":"MAIS_MILIONARIA"},"concursoInicial":120,
               "tipoConcurso":{"valor":"E","descricao":"Especial"},"numerosSelecionados":[1,2,3,4,5,6],
               "numerosSelecionadosMaisMilionariaTrevo":[2,5],"quantidadeTeimosinhas":3,
               "quantidadeApostas":2,"horaEfetivacao":"13:45:10","espelho":true,"troca":true,"combo":true},
              {"id":11,"modalidade":"SUPER_7","concursoAlvo":700,
               "numerosSelecionados":[[1],[2,3],[4],[5],[6],[7],[8]],"surpresinha":false},
              {"id":12,"modalidade":"TIMEMANIA","numerosSelecionados":[1,2,3,4,5,6,7,8,9,10],
               "timeDoCoracao":{"numero":101,"nome":"Time Azul","uf":"DF","descricaoCurta":"AZU"}},
              {"id":13,"modalidade":"DIA_DE_SORTE","numerosSelecionados":[1,2,3,4,5,6,7],
               "mesDeSorte":{"numero":9,"nome":"Setembro","abreviacao":"SET"}},
              {"id":14,"modalidade":"LOTECA","partidasLoteca":[
                 {"numero":1,"equipe1":{"nome":"Casa","numero":1,"vitoria":true},
                  "equipe2":{"nome":"Fora","numero":2,"vitoria":false},"empate":true,
                  "descricaoLegenda":"Jogo 1","letraLegenda":"A"}]},
              {"id":15,"modalidade":"LOTOGOL","partidasLotogol":[
                 {"equipe1":{"nome":"Casa","numero":1,"placar":"2"},
                  "equipe2":{"nome":"Fora","numero":2,"placar":"5+"},
                  "indexPlacarEquipe1":2,"indexPlacarEquipe2":5}]}
            ]}""",
        )

        val bets = RemoteTicketsRepository(FakeTicketsRemote(bets = payload)).confirmedBets()

        val plus = bets.first { it.id == 10L }
        assertEquals(9, plus.modalityId)
        assertEquals(120, plus.contest)
        assertEquals(2L, plus.contestTypeId)
        assertEquals(listOf(2, 5), plus.clovers)
        assertEquals(3, plus.recurringDraws)
        assertEquals(2, plus.quantity)
        assertEquals("13:45:10", plus.time)
        assertTrue(plus.mirror)
        assertTrue(plus.exchange)
        assertTrue(plus.combo)

        val superSeven = bets.first { it.id == 11L }
        assertEquals(7, superSeven.numberColumns.size)
        assertEquals(listOf(2, 3), superSeven.numberColumns[1])
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8), superSeven.numbers)
        assertEquals(101, bets.first { it.id == 12L }.favoriteTeam?.number)
        assertEquals("Setembro", bets.first { it.id == 13L }.luckyMonth?.name)

        val loteca = bets.first { it.id == 14L }.lotecaSelections.single()
        assertEquals("Casa", loteca.match.home.name)
        assertTrue(loteca.homeWin)
        assertTrue(loteca.draw)
        assertFalse(loteca.awayWin)
        val lotogol = bets.first { it.id == 15L }.lotogolSelections.single()
        assertEquals(2, lotogol.homeScore)
        assertEquals(5, lotogol.awayScore)
    }

    @Test fun `repository maps purchases defaults and delegates detail and ticket queries`() = runTest {
        val purchases = json(
            """{"compras":[
              {"id":4,"dataInicioCompra":"03/09/2026","situacao":{"valor":8,"descricao":"PAGAMENTO_CONFIRMADO"},
               "meioPagamento":{"valor":6,"nome":"PIX"},"valorTotal":"30.00","pixCopiaECola":"000201",
               "dataHoraExpiracaoPagamento":"04/09/2026","horaFinalizacaoCompra":"13:40","e2eId":"E2E-1"},
              {"id":4,"dataInicioCompra":"duplicada","situacao":"x","valorTotal":1},
              {"id":5,"dataFinalizacaoCompra":"05/09/2026","situacao":{"valor":"FINALIZADA"},
               "meioPagamento":[],"valorTotal":null},
              {"id":6,"dataInicioCompra":"sem total"}
            ]}""",
        )
        val oneBet = json("""[{"id":8,"modalidade":"quina","numeroConcurso":9,"numerosSelecionados":[1]}]""")
        val remote = FakeTicketsRemote(purchases = purchases, purchaseBets = oneBet, ticket = oneBet)
        val repository = RemoteTicketsRepository(remote) { query -> EncodedTicketCode("encrypted:${query.code}", query.qrCode) }

        val mapped = repository.purchases(mapOf("mes" to "09"))
        assertEquals(2, mapped.size)
        assertEquals("Pagamento Confirmado", mapped[0].status)
        assertEquals("Pix", mapped[0].paymentMethod)
        assertEquals(BigDecimal("30.00"), mapped[0].total)
        assertEquals("000201", mapped[0].pixCopyPaste)
        assertEquals("04/09/2026", mapped[0].paymentExpiresAt)
        assertEquals(8L, mapped[0].statusId)
        assertEquals(6L, mapped[0].paymentMethodId)
        assertEquals("13:40", mapped[0].finalizedTime)
        assertEquals("E2E-1", mapped[0].endToEndId)
        assertEquals("05/09/2026", mapped[1].date)
        assertEquals(BigDecimal.ZERO, mapped[1].total)

        assertEquals(8L, repository.purchaseBets(10).single().id)
        assertEquals(10L, remote.purchaseId)
        assertEquals(8L, repository.purchaseDetails(mapped[0]).bets.single().id)
        assertEquals(4L, remote.purchaseId)
        repository.savePurchaseAsFavorite(10, "Favorito", false)
        assertEquals(Triple(10L, "Favorito", false), remote.favoriteRequest)
        assertEquals(8L, repository.checkTicket(TicketQuery("123", true)).single().id)
        assertEquals("encrypted:123" to true, remote.ticketRequest)
    }

    @Test fun `ticket encoder preserves legacy barcode retailer and qr payload rules`() {
        assertEquals(
            EncodedTicketCode("123456", false),
            TicketQuery("123-456").prepareLegacyTicketPayload(),
        )
        assertEquals(
            EncodedTicketCode("123456==100000001", true),
            TicketQuery("123-456", retailerCode = "10.000000-1").prepareLegacyTicketPayload(),
        )
        assertEquals(
            EncodedTicketCode("NSB==CI==2==MEGA_SENA==CI", true),
            TicketQuery("APOSTA{\"nsb\":\"NSB\",\"ci\":\"CI\",\"pro\":\"2\"}", true)
                .prepareLegacyTicketPayload(),
        )
        assertEquals(
            EncodedTicketCode("INVALIDO", false),
            TicketQuery("INVALIDO", true).prepareLegacyTicketPayload(),
        )

        val encrypted = LegacyRsaTicketCodeEncoder.encode(TicketQuery("123456"))
        assertEquals(false, encrypted.qrCode)
        assertEquals(256, java.util.Base64.getDecoder().decode(encrypted.value).size)
    }

    @Test fun `null primitive and json null payloads map to empty lists`() = runTest {
        assertTrue(null.toBets().isEmpty())
        assertTrue(json("null").toBets().isEmpty())
        assertTrue(json("12").toBets().isEmpty())
        assertTrue(json("{} ").toPurchases().isEmpty())
        assertTrue(RemoteTicketsRepository(FakeTicketsRemote()).confirmedBets(emptyMap()).isEmpty())
    }

    @Test fun `repository maps legacy bet configuration and purchase filter catalogs`() = runTest {
        val configuration = json("""{"ordenacaoPadrao":{"valor":3,"descricao":"Mais recentes"},
            "situacaoPadrao":{"valor":1,"descricao":"Todas"},
            "periodoPadrao":{"mes":{"valor":45,"descricao":"Últimos 45 dias"}},
            "situacoes":[{"valor":1,"descricao":"Todas"}],
            "ordenacoes":[{"valor":3,"descricao":"Mais recentes"}],
            "periodos":[{"mes":{"valor":45,"descricao":"Últimos 45 dias"}}],
            "listaTipoAposta":[{"valor":1,"descricao":"Todas"}],
            "listaTipoConcurso":[{"valor":2,"descricao":"Especial"}]}""")
        val months = json("""[{"mes":{"valor":9,"descricao":"Setembro"},"ano":2026}]""")
        val methods = json("""[{"id":4,"nomePagamentoPremio":"PIX"},{"id":5,"nomePagamentoCompra":"Cartão"}]""")
        val statuses = json("""[{"id":9,"descricao":"Pendente - pagamento"},{"id":9,"descricao":"duplicada"}]""")
        val repository = RemoteTicketsRepository(FakeTicketsRemote(configuration = configuration, months = months,
            methods = methods, statuses = statuses))

        val mapped = repository.confirmedBetsConfiguration()
        assertEquals(3L, mapped.defaultOrdering)
        assertEquals(1L, mapped.defaultStatus)
        assertEquals(45, mapped.defaultPeriod?.month)
        assertEquals("Especial", mapped.contestTypes.single().description)
        assertEquals("Setembro de 2026", repository.purchaseMonths().single().description)
        assertEquals(listOf("PIX", "Cartão"), repository.purchasePaymentMethods().map { it.description })
        assertEquals("Pendente-pagamento", repository.purchaseStatuses(4).single().description)
    }

    @Test fun `retrofit data source delegates all legacy endpoints and serializes ticket code`() = runTest {
        val calls = mutableListOf<String>()
        val bodies = mutableListOf<JsonElement>()
        val payload = json("[]")
        val api = Proxy.newProxyInstance(
            LegacyBackendApi::class.java.classLoader,
            arrayOf(LegacyBackendApi::class.java),
        ) { proxy, method, arguments ->
            when (method.name) {
                "confirmedBets", "purchases", "purchaseBets", "checkTicket", "checkTicketQrCode",
                "confirmedBetsConfiguration", "purchaseMonths", "paymentMethods", "purchaseStatuses",
                "savePurchaseAsFavoriteCart" -> {
                    calls += method.name
                    arguments.orEmpty().filterIsInstance<JsonElement>().forEach(bodies::add)
                    ApiEnvelope(payload)
                }
                "toString" -> "FakeLegacyBackendApi"
                "hashCode" -> System.identityHashCode(proxy)
                "equals" -> proxy === arguments?.firstOrNull()
                else -> error("Unexpected call ${method.name}")
            }
        } as LegacyBackendApi
        val provider = LegacyBackendApiProvider().apply { replace(api) }
        val source = RetrofitTicketsRemoteDataSource(provider)

        assertEquals(payload, source.confirmedBets(mapOf("f" to "v")))
        assertEquals(payload, source.purchases(emptyMap()))
        assertEquals(payload, source.purchaseBets(7))
        assertEquals(payload, source.checkTicket("A\"B", false))
        assertEquals(payload, source.checkTicket("QR", true))
        assertEquals(payload, source.confirmedBetsConfiguration())
        assertEquals(payload, source.purchaseMonths())
        assertEquals(payload, source.purchasePaymentMethods())
        assertEquals(payload, source.purchaseStatuses(3))
        source.savePurchaseAsFavorite(7, "Meu jogo", false)
        assertEquals(
            listOf("confirmedBets", "purchases", "purchaseBets", "checkTicket", "checkTicketQrCode",
                "confirmedBetsConfiguration", "purchaseMonths", "paymentMethods", "purchaseStatuses",
                "savePurchaseAsFavoriteCart"),
            calls,
        )
        assertEquals("A\"B", bodies[0].asJsonObject.get("codigo").asString)
        assertEquals("QR", bodies[1].asJsonObject.get("codigo").asString)
        assertEquals("Meu jogo", bodies[2].asJsonObject.get("nome").asString)
        assertFalse(bodies[2].asJsonObject.get("mantemSurpresinhas").asBoolean)
        assertFalse(bodies[0].toString().contains("\\\"B\"B"))
    }

    @Test fun `bet history policy reproduces nova api cpf rollout and microservice flavor rules`() {
        val parameters = AppParametersDto(
            microserviceEnabled = true,
            betsHistoryBaseUrl = "https://micro.caixa.gov.br/v2",
            productionBetsApiBaseUrl = "https://nova-prd.caixa.gov.br/base/",
            pilotBetsApiBaseUrl = "https://nova-tqs.caixa.gov.br/base",
            novaApiCpfRolloutPercentage = 35,
        )

        assertEquals(
            BetHistoryRoute(BetHistoryBackend.NOVA_API, "https://nova-prd.caixa.gov.br/base/v1"),
            BetHistoryRoutingPolicy.resolve(parameters, "123.456.789-12", production = true),
        )
        assertEquals(
            BetHistoryRoute(BetHistoryBackend.MICROSERVICE, "https://micro.caixa.gov.br/v2"),
            BetHistoryRoutingPolicy.resolve(parameters, "123.456.789-99", production = true),
        )
        assertEquals(
            "https://nova-tqs.caixa.gov.br/base/v1",
            BetHistoryRoutingPolicy.resolve(parameters.copy(novaApiCpfRolloutPercentage = 100), "", false).baseUrl,
        )
        assertEquals(
            BetHistoryBackend.SILCE,
            BetHistoryRoutingPolicy.resolve(
                parameters.copy(betsHistoryBaseUrl = "https://micro.caixa.gov.br/unsupported", novaApiCpfRolloutPercentage = 0),
                "123", production = true,
            ).backend,
        )
        assertEquals(
            BetHistoryBackend.MICROSERVICE,
            BetHistoryRoutingPolicy.resolve(
                parameters.copy(betsHistoryBaseUrl = "https://micro.caixa.gov.br/dev", novaApiCpfRolloutPercentage = 0),
                "123", production = false,
            ).backend,
        )
        assertEquals(
            BetHistoryBackend.SILCE,
            BetHistoryRoutingPolicy.resolve(null, "", production = false).backend,
        )
    }

    @Test fun `retrofit ticket source routes nova api and falls back when dynamic host is not pinned`() = runTest {
        val payload = json("[]")
        val calls = mutableListOf<Pair<String, Map<String, String>>>()
        val legacy = proxyApi(LegacyBackendApi::class.java) { method, arguments ->
            if (method == "confirmedBets") {
                @Suppress("UNCHECKED_CAST")
                calls += method to (arguments?.first() as Map<String, String>)
                ApiEnvelope(payload)
            } else error("Unexpected call $method")
        }
        val dynamic = proxyApi(BetsHistoryApi::class.java) { method, arguments ->
            @Suppress("UNCHECKED_CAST")
            calls += method to if (method == "recoverBet") emptyMap() else arguments?.first() as Map<String, String>
            ApiEnvelope(payload)
        }
        val legacyProvider = LegacyBackendApiProvider().apply { replace(legacy) }
        val historyProvider = BetsHistoryApiProvider().apply {
            replace(dynamic)
            configureDynamicClients(setOf("nova.caixa.gov.br")) { dynamic }
        }
        val configuration = object : AppConfigurationRemoteDataSource {
            override suspend fun isVersionSupported(versionName: String) = true
            override suspend fun appParameters() = AppParametersMapDto(
                AppParametersDto(
                    productionBetsApiBaseUrl = "https://nova.caixa.gov.br",
                    novaApiCpfRolloutPercentage = 50,
                ),
            )
        }
        val source = RetrofitTicketsRemoteDataSource(
            legacyProvider,
            historyProvider,
            configuration,
            CurrentUserCpfProvider { "000.000.000-12" },
            production = true,
        )

        source.confirmedBets(mapOf("combo" to "3", "mes" to "9"))
        assertEquals("novaApiConfirmedBets", calls.single().first)
        assertEquals("3", calls.single().second["comboApostas"])
        assertFalse(calls.single().second.containsKey("combo"))
        assertEquals(payload, source.recoverConfirmedBet(42))
        assertEquals("recoverBet", calls.last().first)

        calls.clear()
        val unpinnedProvider = BetsHistoryApiProvider().apply {
            replace(dynamic)
            configureDynamicClients(emptySet()) { dynamic }
        }
        RetrofitTicketsRemoteDataSource(
            legacyProvider, unpinnedProvider, configuration, CurrentUserCpfProvider { "12" }, production = true,
        ).confirmedBets(emptyMap())
        assertEquals("confirmedBets", calls.single().first)
        assertEquals(null, source.run {
            val silce = RetrofitTicketsRemoteDataSource(legacyProvider)
            silce.recoverConfirmedBet(1)
        })
    }

    private class FakeTicketsRemote(
        private val bets: JsonElement? = null,
        private val purchases: JsonElement? = null,
        private val purchaseBets: JsonElement? = null,
        private val ticket: JsonElement? = null,
        private val configuration: JsonElement? = null,
        private val months: JsonElement? = null,
        private val methods: JsonElement? = null,
        private val statuses: JsonElement? = null,
        private val backend: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend = br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.SILCE,
    ) : TicketsRemoteDataSource {
        var filters: Map<String, String>? = null
        var purchaseId: Long? = null
        var ticketRequest: Pair<String, Boolean>? = null
        var favoriteRequest: Triple<Long, String, Boolean>? = null
        override fun confirmedBetsBackend() = backend
        override suspend fun confirmedBets(filters: Map<String, String>) = bets.also { this.filters = filters }
        override suspend fun purchases(filters: Map<String, String>) = purchases.also { this.filters = filters }
        override suspend fun purchaseBets(purchaseId: Long) = purchaseBets.also { this.purchaseId = purchaseId }
        override suspend fun checkTicket(code: String, qrCode: Boolean) = ticket.also {
            ticketRequest = code to qrCode
        }
        override suspend fun confirmedBetsConfiguration() = configuration
        override suspend fun purchaseMonths() = months
        override suspend fun purchasePaymentMethods() = methods
        override suspend fun purchaseStatuses(paymentMethodId: Long?) = statuses
        override suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) {
            favoriteRequest = Triple(purchaseId, name, keepSurprises)
        }
    }

    private fun json(value: String): JsonElement = JsonParser.parseString(value)

    @Suppress("UNCHECKED_CAST")
    private fun <T> proxyApi(type: Class<T>, response: (String, Array<out Any?>?) -> Any): T =
        Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { proxy, method, arguments ->
            when (method.name) {
                "toString" -> "Fake${type.simpleName}"
                "hashCode" -> System.identityHashCode(proxy)
                "equals" -> proxy === arguments?.firstOrNull()
                else -> response(method.name, arguments)
            }
        } as T
}
