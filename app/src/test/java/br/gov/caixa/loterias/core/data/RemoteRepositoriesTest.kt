package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.feature.account.data.dto.AccountRegistrationDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountSessionDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.AccountSuspensionDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddBetRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddComboRequestDto
import br.gov.caixa.loterias.feature.cart.data.dto.AddFavoriteCartRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddPoolShareRequestDto
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.network.model.AppParametersMapDto
import br.gov.caixa.loterias.feature.betting.data.dto.AvailablePoolModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboDto
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboEntryDto
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboTypeDto
import br.gov.caixa.loterias.feature.betting.data.dto.BettingParametersDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartBetDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartComboDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartComboTypeDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartPoolReservationDto
import br.gov.caixa.loterias.feature.results.data.dto.DrawContestDto
import br.gov.caixa.loterias.feature.results.data.dto.DrawPayoutDto
import br.gov.caixa.loterias.feature.results.data.dto.DrawResultDto
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartBetDto
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartCloversDto
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartDto
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartModalityDto
import br.gov.caixa.loterias.feature.account.data.dto.FederativeUnitDto
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryCloverParametersDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryContestDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryGameParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryPriceDto
import br.gov.caixa.loterias.feature.betting.data.dto.LuckyMonthParameterDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplaceBetDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplaceCityDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplaceLotteryDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePageDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePoolDetailDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePoolDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplaceStateDto
import br.gov.caixa.loterias.feature.account.data.dto.MunicipalityDto
import br.gov.caixa.loterias.feature.account.data.dto.MunicipalityIdDto
import br.gov.caixa.loterias.feature.account.data.dto.NeighborhoodDto
import br.gov.caixa.loterias.feature.account.data.dto.PersonalDataUpdateDto
import br.gov.caixa.loterias.feature.cart.data.dto.SaveFavoriteCartRequestDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SelfAssessmentAnswerDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SelfAssessmentPayloadDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SelfAssessmentQuestionDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SocialTransfersDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsMatchParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsShieldTeamDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamShieldDto
import br.gov.caixa.loterias.feature.support.data.dto.SupportQuestionDto
import br.gov.caixa.loterias.feature.support.data.dto.SupportSectionDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SuspendAccountRequestDto
import br.gov.caixa.loterias.feature.support.data.dto.TermsDto
import br.gov.caixa.loterias.feature.account.data.dto.UserProfileDto
import br.gov.caixa.loterias.feature.account.data.repository.AccountRepositoryImpl
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.betting.data.repository.BettingRepositoryImpl
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.cart.data.repository.CartRepositoryImpl
import br.gov.caixa.loterias.feature.cart.data.repository.FavoriteCartRepositoryImpl
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.marketplace.data.repository.MarketplaceRepositoryImpl
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.responsiblegaming.data.repository.ResponsibleGamingRepositoryImpl
import br.gov.caixa.loterias.feature.results.data.repository.ResultsRepositoryImpl
import br.gov.caixa.loterias.feature.support.data.repository.SupportRepositoryImpl
import com.google.gson.JsonParser
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.core.data.repository.AppConfigurationRepositoryImpl
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSourceImpl
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource

class RemoteRepositoriesTest {
    @Test
    fun `configuration maps marketplace limits`() = runBlocking {
        val api = FakeApi()
        val repository = AppConfigurationRepositoryImpl(source(api))
        assertTrue(repository.isVersionSupported("1.24.0"))
        assertFalse(repository.isVersionSupported("0.0.0"))
        val parameters = repository.marketplaceFilterParameters()
        assertEquals(listOf(2, 3, 4), parameters.betOptions)
        assertEquals(parameters, repository.marketplaceFilterParameters())
        assertTrue(repository.homeFeatures().poolSalesEnabled)
        assertTrue(repository.homeFeatures().comboBettingEnabled)
        assertTrue(repository.homeFeatures().rapidBettingEnabled)
        assertEquals(1, api.parameterCalls)
    }

    @Test
    fun `configuration preserves missing limits as invalid`() = runBlocking {
        val repository = AppConfigurationRepositoryImpl(source(FakeApi(parameters = ApiEnvelope())))
        assertTrue(repository.marketplaceFilterParameters().betOptions.isEmpty())
    }

    @Test
    fun `support repository maps sections questions and terms`() = runBlocking {
        val api = FakeApi()
        val repository = SupportRepositoryImpl(source(api))

        val section = repository.sections().single()
        assertEquals(10, section.id)
        assertEquals("Geral", section.title)
        assertEquals("Pergunta", section.questions.single().title)
        assertEquals("Resposta", section.questions.single().answer)
        assertEquals(section.questions, repository.questions(10))
        assertEquals("<p>Termos</p>", repository.terms())
        repository.acceptTerms()
        assertEquals(1, api.acceptCalls)
    }

    @Test
    fun `support repository safely maps absent payload and nullable fields`() = runBlocking {
        val api = FakeApi(
            sections = ApiEnvelope(listOf(SupportSectionDto())),
            terms = ApiEnvelope(),
        )
        val repository = SupportRepositoryImpl(source(api))

        val section = repository.sections().single()
        assertEquals(0, section.id)
        assertEquals("", section.title)
        assertTrue(section.questions.isEmpty())
        assertTrue(repository.questions(9).isEmpty())
        assertEquals("", repository.terms())
    }

    @Test
    fun `short filtered municipal page automatically continues into state results`() = runBlocking {
        val api = FakeApi(marketplace = ApiEnvelope(MarketplacePageDto(
            currentPage = 1, lastPage = 1, totalRecords = 1,
            pools = listOf(MarketplacePoolDto(code = "B1")),
        )))
        val filter = MarketplaceFilter("1,00", "9,00", 2, 8, 4, 6, "", "", emptyList(), emptyList())
            .withSearchArea(br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.MUNICIPALITY)
            .withSearchLocation(br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceLocation(123, 53))
        val result = MarketplaceRepositoryImpl(source(api)).search(filter, 0)
        assertEquals(1, result.pools.size)
        assertEquals(br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.STATE, result.nextArea)
        assertTrue(result.continueAutomatically)
    }

    @Test
    fun `regional marketplace requests preserve account location and advance only after final page`() = runBlocking {
        val api = FakeApi(marketplace = ApiEnvelope(MarketplacePageDto(
            currentPage = 1, lastPage = 1, totalRecords = 0, pools = emptyList(),
        )))
        val repository = MarketplaceRepositoryImpl(source(api))
        val filter = MarketplaceFilter.empty().withSearchLocation(
            br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceLocation(123, 53))
        for (area in br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.entries) {
            val result = repository.search(filter.withSearchArea(area), 0)
            assertEquals(area.backendValue.toString(), api.marketplaceQuery["tipoConsulta"])
            assertEquals("123", api.marketplaceQuery["idMunicipio"])
            assertEquals("53", api.marketplaceQuery["idUf"])
            assertEquals("1", api.marketplaceQuery["pagina"])
            assertEquals(area.next(), result.nextArea)
            assertEquals(area.next() != null, result.canLoadMore)
        }
        repository.search(filter.withLottery("987"), 0)
        // O número da lotérica é uma restrição separada; não força searchArea RETAILER.
        assertEquals("2", api.marketplaceQuery["tipoConsulta"])
        assertEquals("987", api.marketplaceQuery["numeroLoterico"])
    }

    @Test
    fun `marketplace repository calls pool service with quantidadeAposta`() = runBlocking {
        val api = FakeApi()
        val result = MarketplaceRepositoryImpl(source(api)).search(
            MarketplaceFilter("1,00", "9,00", 2, 8, 4, 6, "", "", emptyList(), emptyList()),
            page = 3,
        )
        assertEquals("4", api.marketplaceQuery["quantidadeAposta"])
        assertEquals("6", api.marketplaceQuery["quantidadeDezena"])
        assertEquals("2", api.marketplaceQuery["tipoConsulta"])
        assertEquals("30", api.marketplaceQuery["qtdPorPagina"])
        assertEquals("4", api.marketplaceQuery["pagina"])
        assertEquals(8, result.totalRecords)
        assertEquals(3, result.currentPage)
        assertEquals(4, result.lastPage)
        assertEquals("B1", result.pools.single().code)
        assertEquals("Mega-Sena", result.pools.single().modality)
        assertEquals(2800, result.pools.single().contest)
        assertEquals("Loterias Centro", result.pools.single().lotteryName)
        assertEquals("10/09/2026", result.pools.single().drawDate)
        assertEquals("12.50", result.pools.single().sharePrice)
        assertEquals(4, result.pools.single().availableShares)
        assertEquals(3, result.pools.single().betQuantity)
        assertEquals(6, result.pools.single().numberQuantity)
        assertTrue(result.pools.single().favoriteLottery)
        assertEquals(99L, result.pools.single().lotteryId)
        assertEquals("Brasília - DF", result.pools.single().cityAndState)
        assertEquals(10, result.pools.single().totalShares)
        assertEquals(BigDecimal("10.00"), result.pools.single().regularSharePriceWithFee)
        assertEquals(BigDecimal("12.50"), result.pools.single().lastSharePriceWithFeeValue)
    }

    @Test
    fun `marketplace successful response without payload keeps the native empty page`() = runBlocking {
        val page = MarketplaceRepositoryImpl(source(FakeApi(marketplace = ApiEnvelope()))).search(MarketplaceFilter.empty(), 0)
        // A página 1 do protocolo é normalizada para o índice do domínio, iniciado em zero.
        assertEquals(0, page.currentPage)
        assertEquals(0, page.lastPage)
        assertTrue(page.pools.isEmpty())
    }

    @Test
    fun `marketplace repository supplies defaults for nullable response`() = runBlocking {
        val result = MarketplaceRepositoryImpl(source(
            FakeApi(marketplace = ApiEnvelope(MarketplacePageDto(pools = listOf(MarketplacePoolDto())))),
        ),
        ).search(MarketplaceFilter.empty(), -5)
        assertEquals(0, result.currentPage)
        assertEquals(0, result.lastPage)
        assertEquals(1, result.totalRecords)
        val pool = result.pools.single()
        assertEquals("", pool.code)
        assertEquals(0, pool.contest)
        assertEquals(0, pool.availableShares)
        assertFalse(pool.favoriteLottery)
    }

    @Test fun `marketplace detail maps actual nested Loteca teams ahead of flat metadata while preserving outer winner flags`() = runBlocking {
        val dto = com.google.gson.Gson().fromJson(
            """{"apostas":[{"partidasLoteca":[{
              "numero":6,"empate":true,
              "equipe1":{"numero":999,"nome":"Wrong flat home","indicadorSelecao":true,"vitoria":true,
                "parametroEquipe":{"numero":11,"nome":"Nested home","uf":"RJ","descricaoCurta":"NH","descricaoLonga":"Nested home long","numeroPais":55,"pais":"Brasil","siglaPais":"BR","nomeClass":"NestedClass","indicadorSelecao":false}},
              "equipe2":{"vitoria":false,"parametroEquipe":{"numero":22,"nome":"Nested away","uf":"SP","indicadorSelecao":true,"numeroPais":44,"pais":"Outro","siglaPais":"OT"}},
              "descricaoLegenda":"Nested fixture","letraLegenda":"N"
            }]}]}""", MarketplacePoolDetailDto::class.java)
        val bet = MarketplaceRepositoryImpl(source(FakeApi(detail = ApiEnvelope(dto)))).detail("NESTED").bets.single()
        val selection = bet.lotecaSelections.single()
        val home = selection.match.home
        assertEquals(11, home.number); assertEquals("Nested home", home.name); assertEquals("RJ", home.state)
        assertEquals("NH", home.shortDescription); assertEquals("Nested home long", home.longDescription)
        assertEquals(55, home.countryNumber); assertEquals("Brasil", home.country); assertEquals("BR", home.countryCode)
        assertEquals("NestedClass", home.className); assertFalse(home.selectionIndicator)
        assertEquals(22, selection.match.away.number); assertEquals("Nested away", selection.match.away.name)
        assertTrue(selection.match.away.selectionIndicator)
        assertTrue(selection.homeWin); assertTrue(selection.draw); assertFalse(selection.awayWin)
        assertEquals("Nested fixture", selection.match.legendDescription)
    }

    @Test fun `marketplace detail uses last share without fee and preserves all backend prices independently`() = runBlocking {
        val dto = com.google.gson.Gson().fromJson(
            """{"vrCotaSemTarifa":"60.47","vrUltimaCotaSemTarifa":"60.48","vrTarifaServicoUltimaCota":"21.55","vrUltimaCotaComTarifa":"82.03","vrCotaComTarifa":82.02}""",
            MarketplacePoolDetailDto::class.java)
        val result = MarketplaceRepositoryImpl(source(FakeApi(detail = ApiEnvelope(dto)))).detail("ROUNDING")
        assertEquals("60.48", result.sharePriceWithoutFee)
        assertEquals("21.55", result.lastShareFee)
        assertEquals(BigDecimal("82.03"), result.lastSharePriceWithFeeValue)
        assertEquals(BigDecimal("82.02"), result.regularSharePriceWithFee)
        assertEquals(result.sharePriceWithoutFee.toBigDecimal() + result.lastShareFee.toBigDecimal(), result.lastSharePriceWithFeeValue)
        val compatible = MarketplaceRepositoryImpl(source(FakeApi(detail = ApiEnvelope(dto.copy(lastSharePriceWithoutFee = null))))).detail("OLD")
        assertEquals("60.47", compatible.sharePriceWithoutFee)
    }

    @Test fun `marketplace parsed detail preserves distinct Super Sete column matrices instead of only flattened numbers`() = runBlocking {
        val dto = com.google.gson.Gson().fromJson(
            """{"apostas":[{"dezenas":[[1,2],[],[3]]},{"dezenas":[[],[1],[2,3]]}]}""",
            MarketplacePoolDetailDto::class.java)
        val api = FakeApi(detail = ApiEnvelope(dto))
        val result = MarketplaceRepositoryImpl(source(api)).detail("SUPER7")
        assertEquals("SUPER7", api.detailCode)
        assertEquals(listOf(1, 2, 3), result.bets[0].numbers)
        assertEquals(result.bets[0].numbers, result.bets[1].numbers)
        assertEquals(listOf(listOf(1, 2), emptyList<Int>(), listOf(3)), result.bets[0].numberColumns)
        assertEquals(listOf(emptyList<Int>(), listOf(1), listOf(2, 3)), result.bets[1].numberColumns)
    }

    @Test fun `marketplace parsed detail retains team month and Loteca match metadata and every selection flag`() = runBlocking {
        val dto = com.google.gson.Gson().fromJson(
            """{"apostas":[{
              "indicadorSurpresinha":true,"dezenas":[1,2],"trevos":[3,4],
              "timeDoCoracao":{"numero":7,"nome":"Time A","uf":"DF","numeroPais":55,"descricaoCurta":"TA","nomeClass":"Equipe","indicadorSelecao":true},
              "mesDeSorte":{"numero":10,"nome":"Outubro","abreviacao":"OUT"},
              "partidasLoteca":[
                {"numero":9,"equipe1":{"numero":11,"nome":"Casa","uf":"RJ","vitoria":true},"equipe2":{"numero":22,"nome":"Fora","uf":"SP","vitoria":false},"empate":true,"descricaoLegenda":"Rodada especial","letraLegenda":"E"},
                {"numero":10,"equipe1":{"numero":33,"nome":"Casa2","vitoria":false},"equipe2":{"numero":44,"nome":"Fora2","vitoria":true},"empate":false}
              ]
            }],"vrCotaComTarifa":9.99,"vrUltimaCotaComTarifa":"10.00"}""",
            MarketplacePoolDetailDto::class.java)
        val result = MarketplaceRepositoryImpl(source(FakeApi(detail = ApiEnvelope(dto)))).detail("SPORTS")
        val bet = result.bets.single()
        val team = requireNotNull(bet.favoriteTeam)
        assertEquals(7, team.number); assertEquals("Time A", team.name); assertEquals("DF", team.state)
        assertEquals(55, team.countryNumber); assertEquals("TA", team.shortDescription)
        assertEquals("Equipe", team.className); assertTrue(team.selectionIndicator)
        assertEquals("", team.longDescription); assertEquals("", team.country); assertEquals("", team.countryCode)
        assertEquals(br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth(10, "Outubro", "OUT"), bet.luckyMonth)
        val first = bet.lotecaSelections[0]
        assertEquals(9, first.match.number); assertEquals("Casa", first.match.home.name); assertEquals("SP", first.match.away.state)
        assertEquals("Rodada especial", first.match.legendDescription); assertEquals("E", first.match.legendLetter)
        assertTrue(first.homeWin); assertTrue(first.draw); assertFalse(first.awayWin)
        val second = bet.lotecaSelections[1]
        assertFalse(second.homeWin); assertFalse(second.draw); assertTrue(second.awayWin)
        assertTrue(bet.surprise); assertEquals(listOf(3, 4), bet.clovers)
        assertEquals(BigDecimal("9.99"), result.regularSharePriceWithFee)
    }

    @Test fun `marketplace parsed regular detail is compatible with absent optional modality data`() = runBlocking {
        val dto = com.google.gson.Gson().fromJson(
            """{"apostas":[{"dezenas":[1,2],"trevos":[4]},{"dezenas":null}]}""", MarketplacePoolDetailDto::class.java)
        val result = MarketplaceRepositoryImpl(source(FakeApi(detail = ApiEnvelope(dto)))).detail("REGULAR")
        assertEquals(listOf(1, 2), result.bets[0].numbers)
        assertEquals(listOf(4), result.bets[0].clovers)
        result.bets.forEach {
            assertTrue(it.numberColumns.isEmpty()); assertNull(it.favoriteTeam); assertNull(it.luckyMonth)
            assertTrue(it.lotecaSelections.isEmpty()); assertFalse(it.surprise)
        }
    }

    @Test
    fun `marketplace detail maps all purchase and bet fields`() = runBlocking {
        val api = FakeApi()
        val result = MarketplaceRepositoryImpl(source(api)).detail("B9")

        assertEquals("B9", api.detailCode)
        assertEquals("B9", result.code)
        assertEquals("Mega-Sena", result.modality)
        assertEquals(2801, result.contest)
        assertEquals(99L, result.lotteryId)
        assertEquals("Loterias Centro", result.lotteryName)
        assertEquals("000099-9", result.lotteryNumber)
        assertEquals("Brasília - DF", result.cityAndState)
        assertEquals("10/09/2026 às 20:00", result.drawDateAndTime)
        assertEquals(listOf(1, 2, 3, 4), result.bets.first().numbers)
        assertEquals(listOf(5, 6), result.bets.first().clovers)
        assertTrue(result.bets.first().surprise)
        assertTrue(result.bets.last().numbers.isEmpty())
        assertFalse(result.bets.last().surprise)
        assertEquals(2, result.contestType)
        assertEquals("10/09/2026", result.drawDate.trim())
        assertEquals(BigDecimal("2.50"), result.serviceFeePerShare)
        assertEquals(BigDecimal("10.00"), result.lastSharePriceWithFeeValue)

        val cart = MarketplaceRepositoryImpl(source(api)).addToCart(
            MarketplacePoolOrder(
                code = result.code,
                modality = result.modality,
                contest = result.contest,
                contestType = result.contestType,
                drawDate = result.drawDate,
                availableShares = result.availableShares,
                requestedShares = 2,
                serviceFeePerShare = result.serviceFeePerShare,
                firstSharePriceWithFee = result.lastSharePriceWithFeeValue,
            ),
        )
        assertEquals(9L, cart.id)
        assertEquals("B9", api.addedPoolShare?.poolCode)
        assertEquals(2, api.addedPoolShare?.shareQuantity)
        assertEquals("2", api.addedPoolShare?.contestType?.value)
        assertEquals(BigDecimal("20.00"), api.addedPoolShare?.amount)
        assertEquals(BigDecimal("2.50"), api.addedPoolShare?.serviceFee)
    }

    @Test
    fun `marketplace detail preserves regular share price independently of first share rounding`() = runBlocking {
        val dto = com.google.gson.Gson().fromJson(
            """{"vrUltimaCotaComTarifa":"10.00","vrCotaComTarifa":9.99}""",
            MarketplacePoolDetailDto::class.java,
        )
        val result = MarketplaceRepositoryImpl(source(FakeApi(detail = ApiEnvelope(dto)))).detail("B9")
        assertEquals(BigDecimal("10.00"), result.lastSharePriceWithFeeValue)
        assertEquals(BigDecimal("9.99"), result.regularSharePriceWithFee)
    }

    @Test
    fun `marketplace detail supplies defaults for nullable fields`() = runBlocking {
        val detail = MarketplacePoolDetailDto(
            bets = listOf(MarketplaceBetDto(numbers = JsonParser.parseString("null"))),
        )
        val result = MarketplaceRepositoryImpl(source(FakeApi(detail = ApiEnvelope(detail)))).detail("B0")
        assertEquals("", result.cityAndState)
        assertEquals("", result.drawDateAndTime)
        assertEquals(0, result.availableShares)
        assertTrue(result.bets.single().numbers.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `marketplace detail rejects blank code`() = runBlocking {
        MarketplaceRepositoryImpl(source(FakeApi())).detail(" ")
        Unit
    }

    @Test(expected = IllegalArgumentException::class)
    fun `marketplace detail rejects response without payload`() = runBlocking {
        MarketplaceRepositoryImpl(source(FakeApi(detail = ApiEnvelope()))).detail("B1")
        Unit
    }

    @Test
    fun `results repository maps latest and contest results`() = runBlocking {
        val api = FakeApi()
        val repository = ResultsRepositoryImpl(source(api).results)
        val latest = repository.latest("megasena")
        val contest = repository.byContest("quina", 99)

        assertEquals("megasena", api.latestModality)
        assertEquals("quina", api.resultModality)
        assertEquals(99, api.resultContest)
        assertEquals("Mega-Sena", latest.modality)
        assertEquals(2802, latest.contest)
        assertEquals("Brasília - DF", latest.drawLocation)
        assertEquals(listOf(1, 2, 3), latest.firstDrawNumbers)
        assertEquals(listOf(4, 5), latest.secondDrawNumbers)
        assertEquals(listOf(6, 7), latest.clovers)
        assertTrue(latest.accumulated)
        assertEquals("Sena", latest.payouts.single().description)
        assertEquals(latest, contest)
    }

    @Test
    fun `results repository supplies nullable defaults and alternate state`() = runBlocking {
        val dto = DrawResultDto(
            city = MarketplaceCityDto(name = "São Paulo"),
            state = MarketplaceStateDto("SP"),
            firstDrawNumbers = JsonParser.parseString("\"unsupported\""),
        )
        val result = ResultsRepositoryImpl(source(FakeApi(result = ApiEnvelope(dto))).results).latest("quina")
        assertEquals("São Paulo - SP", result.drawLocation)
        assertTrue(result.firstDrawNumbers.isEmpty())
        assertTrue(result.payouts.isEmpty())
        assertFalse(result.accumulated)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `results repository rejects blank modality`() = runBlocking {
        ResultsRepositoryImpl(source(FakeApi()).results).latest(" ")
        Unit
    }

    @Test(expected = IllegalArgumentException::class)
    fun `results repository rejects invalid contest`() = runBlocking {
        ResultsRepositoryImpl(source(FakeApi()).results).byContest("quina", 0)
        Unit
    }

    @Test(expected = IllegalArgumentException::class)
    fun `results repository rejects absent payload`() = runBlocking {
        ResultsRepositoryImpl(source(FakeApi(result = ApiEnvelope())).results).latest("quina")
        Unit
    }

    @Test
    fun `responsible gaming repository maps active questions answers and social transfers`() = runBlocking {
        val api = FakeApi()
        val repository = ResponsibleGamingRepositoryImpl(source(api))

        val questions = repository.assessmentQuestions()
        assertEquals(1, questions.size)
        assertEquals(8, questions.single().id)
        assertEquals("Pergunta responsável", questions.single().description)
        repository.submitAssessment(
            listOf(br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentAnswer(8, "Sim", true)),
        )
        assertEquals(listOf(SelfAssessmentAnswerDto(8, "Sim", true)), api.assessmentAnswers)

        val transfers = repository.socialTransfers()
        assertEquals(BigDecimal("1000"), transfers.accumulatedRevenue)
        assertEquals(BigDecimal("300"), transfers.amountFor(transfers.educationPercentage))
        assertEquals("04/09/2026", transfers.baseDate)

        val suspension = repository.suspendAccount(24)
        assertTrue(suspension.active)
        assertEquals("24 horas", suspension.duration)
        assertEquals(SuspendAccountRequestDto("12345678901", 24), api.suspensionRequest)
    }

    @Test
    fun `responsible gaming repository supplies defaults and rejects absent payloads`() = runBlocking {
        val repository = ResponsibleGamingRepositoryImpl(source(FakeApi(
            assessment = ApiEnvelope(SelfAssessmentPayloadDto(listOf(SelfAssessmentQuestionDto()))),
            social = ApiEnvelope(SocialTransfersDto()),
        )))
        val question = repository.assessmentQuestions().single()
        assertEquals(0, question.id)
        assertEquals("", question.description)
        assertEquals(Int.MAX_VALUE, question.order)
        assertEquals(BigDecimal.ZERO, repository.socialTransfers().otherPercentage)

        runCatching {
            ResponsibleGamingRepositoryImpl(source(FakeApi(assessment = ApiEnvelope()))).assessmentQuestions()
        }.onSuccess { error("Payload ausente deveria falhar") }
        runCatching {
            ResponsibleGamingRepositoryImpl(source(FakeApi(social = ApiEnvelope()))).socialTransfers()
        }.onSuccess { error("Payload ausente deveria falhar") }
        listOf(
            FakeApi(profile = ApiEnvelope()),
            FakeApi(profile = ApiEnvelope(UserProfileDto("inválido"))),
            FakeApi(suspension = ApiEnvelope()),
        ).forEach { api ->
            runCatching { ResponsibleGamingRepositoryImpl(source(api)).suspendAccount(24) }
                .onSuccess { error("Suspensão inválida deveria falhar") }
        }
        Unit
    }

    @Test
    fun `account repository maps profile location and update contract`() = runBlocking {
        val api = FakeApi()
        val repository = AccountRepositoryImpl(source(api))

        val profile = repository.profile()
        assertEquals("123.456.789-01", profile.cpf)
        assertEquals("Pessoa CAIXA", profile.name)
        assertEquals("pessoa@caixa.gov.br", profile.email)
        assertEquals(Gender.FEMALE, profile.gender)
        assertEquals("DF", profile.stateCode)
        assertEquals("Brasília", profile.city)
        assertEquals(BigDecimal("100"), profile.dailyLimit)

        val location = repository.locationForZipCode("70.000-000")
        assertEquals(MunicipalityId(7, 1, 0), location.municipalityId)
        assertEquals("DF", location.stateCode)
        assertEquals("Brasília", location.city)

        repository.updatePersonalData(
            PersonalDataUpdate("70.000-000", Gender.MALE, MunicipalityId(7, 1, 0), BigDecimal("90"), false),
        )
        assertEquals(
            PersonalDataUpdateDto("70000000", MunicipalityIdDto(7, 1, 0), "M", false, BigDecimal("90"), false),
            api.personalDataUpdate,
        )
        repository.register(AccountRegistration("70.000-000", Gender.FEMALE, MunicipalityId(7, 1, 0), true, true))
        assertEquals(
            AccountRegistrationDto("70000000", true, true, MunicipalityIdDto(7, 1, 0), "F"),
            api.accountRegistration,
        )
        val access = repository.verifyAccess()
        assertEquals(PostLoginDestination.HOME, access.destination)
        assertTrue(access.temporarySuspension)
        assertEquals(BigDecimal("90"), access.dailyLimit)
        assertEquals(BigDecimal("200"), repository.recalculateAuthorizedLimit())
    }

    @Test
    fun `account repository provides safe defaults and rejects absent location`() = runBlocking {
        val api = FakeApi(
            profile = ApiEnvelope(UserProfileDto(municipalityId = MunicipalityIdDto())),
            municipalities = ApiEnvelope(emptyList()),
            neighborhoods = ApiEnvelope(emptyList()),
        )
        val repository = AccountRepositoryImpl(source(api))
        val profile = repository.profile()
        assertEquals(Gender.MALE, profile.gender)
        assertEquals("", profile.stateCode)
        assertEquals(BigDecimal.ZERO, profile.authorizedDailyLimit)
        runCatching { repository.locationForZipCode("00000000") }
            .onSuccess { error("CEP ausente deveria falhar") }
            .onFailure { assertEquals("O CEP informado não foi localizado. Por favor, insira um CEP válido.", it.message) }
        runCatching { AccountRepositoryImpl(source(FakeApi(profile = ApiEnvelope()))).profile() }
            .onSuccess { error("Perfil ausente deveria falhar") }
        assertEquals(
            PostLoginDestination.REGISTRATION,
            AccountRepositoryImpl(source(FakeApi(verification = ApiEnvelope(redirect = "cadastrar-apostador"))))
                .verifyAccess().destination,
        )
        assertEquals(
            PostLoginDestination.TERMS,
            AccountRepositoryImpl(source(FakeApi(verification = ApiEnvelope(redirect = "aceitar-termo-de-uso"))))
                .verifyAccess().destination,
        )
        assertEquals(
            PostLoginDestination.SELF_ASSESSMENT,
            AccountRepositoryImpl(source(FakeApi(verification = ApiEnvelope(AccountSessionDto(requiresSelfAssessment = true)))))
                .verifyAccess().destination,
        )
        Unit
    }

    @Test
    fun `cart repository maps grouped items totals and mutation endpoints`() = runBlocking {
        val api = FakeApi()
        val repository = CartRepositoryImpl(source(api))
        val cart = repository.current()
        assertEquals(3, cart.items.size)
        assertEquals(BigDecimal("42.50"), cart.total)
        assertEquals(CartItemKind.POOL_SHARE, cart.items[0].kind)
        assertEquals("Mega Sena • Bolão B10", cart.items[0].title)
        assertEquals("Cota 4", cart.items[0].description)
        assertEquals(BigDecimal("12.50"), cart.items[0].amount)
        assertEquals("01  02  03", cart.items[1].description)
        assertEquals("Combo da Sorte", cart.items[2].title)
        assertEquals(BigDecimal("10.00"), cart.items[2].amount)
        assertTrue(cart.expiredPoolShareRemoved)

        repository.remove(cart.items[0])
        repository.remove(cart.items[2])
        repository.clear()
        repository.validate()
        repository.validateFavoriteName("Meu jogo")
        repository.saveFavorite("Meu jogo")
        assertEquals(11L, api.removedBetId)
        assertEquals(13L, api.removedComboId)
        assertEquals(1, api.clearCartCalls)
        assertEquals("Meu jogo", api.validatedFavoriteName)
        assertEquals("Meu jogo", api.savedFavoriteName)
    }

    @Test
    fun `favorite cart repository maps bets and delegates every mutation`() = runBlocking {
        val api = FakeApi()
        val repository = FavoriteCartRepositoryImpl(source(api))

        assertEquals("Meu favorito", repository.favorites().single().name)
        val bet = repository.bets(21).single()
        assertEquals(31, bet.id)
        assertEquals("Mega da Virada", bet.modality)
        assertEquals(listOf(1, 2), bet.numbers)
        assertEquals(listOf(3, 4), bet.clovers)
        assertEquals(2, bet.recurringDraws)
        assertTrue(bet.surprise)
        assertTrue(bet.special)

        repository.deleteBet(21, 31)
        repository.deleteCart(21)
        val cart = repository.addToCurrentCart(21, 2)
        assertEquals(BigDecimal("42.50"), cart.total)
        assertEquals(21L to 31L, api.deletedFavoriteBet)
        assertEquals(21L, api.deletedFavoriteCart)
        assertEquals(21L to 2, api.importedFavoriteCart)
    }

    @Test
    fun `favorite detail retains backend quantities columns and modality complements`() = runBlocking {
        val gson = com.google.gson.Gson()
        val payloads = listOf(
            """{"id":1,"modalidade":{"valor":7,"descricao":"Super Sete"},"numerosSelecionados":[[0,3],[4],[6],[7],[8],[9],[2]],"indicadorSurpresinha":"NAO_SURPRESINHA"}""",
            """{"id":2,"modalidade":{"valor":9},"quantidadeNumeros":6,"quantidadeTrevos":2,"indicadorSurpresinha":"SURPRESINHA"}""",
            """{"id":3,"modalidade":{"valor":20},"timeDoCoracao":{"nome":"FLAMENGO","uf":"RJ"}}""",
            """{"id":4,"modalidade":{"valor":11},"mesDeSorte":{"nome":"Setembro"}}""",
        ).map { gson.fromJson(it, FavoriteCartBetDto::class.java) }
        val repository = FavoriteCartRepositoryImpl(source(FakeApi(favoriteBets = ApiEnvelope(payloads))))
        val bets = repository.bets(1)
        assertEquals(listOf(listOf(0, 3), listOf(4), listOf(6), listOf(7), listOf(8), listOf(9), listOf(2)), bets[0].numberColumns)
        assertEquals(8, bets[0].numberCount)
        assertEquals(7, bets[0].modalityId)
        assertEquals(6, bets[1].numberCount)
        assertEquals(2, bets[1].cloverCount)
        assertTrue(bets[1].surprise)
        assertEquals("FLAMENGO/RJ", bets[2].favoriteTeam)
        assertEquals("Setembro", bets[3].luckyMonth)
    }

    @Test
    fun `favorite cart repository supplies safe nullable defaults`() = runBlocking {
        val repository = FavoriteCartRepositoryImpl(
            source(
                FakeApi(
                    favorites = ApiEnvelope(listOf(FavoriteCartDto())),
                    favoriteBets = ApiEnvelope(
                        listOf(
                            FavoriteCartBetDto(
                                selectedNumbers = JsonParser.parseString("{}"),
                                surpriseIndicator = "NAO_SURPRESINHA",
                            ),
                        ),
                    ),
                ),
            ),
        )
        assertEquals("Carrinho favorito", repository.favorites().single().name)
        val bet = repository.bets(1).single()
        assertEquals("", bet.modality)
        assertTrue(bet.numbers.isEmpty())
        assertTrue(bet.clovers.isEmpty())
        assertFalse(bet.surprise)
        assertFalse(bet.special)
    }

    @Test
    fun `cart repository derives total and safe descriptions and rejects absent payload`() = runBlocking {
        val individualBet = CartBetDto(
            id = 1,
            modality = JsonParser.parseString("\"FUTURE_GAME\""),
            selectedNumbers = JsonParser.parseString("\"x\""),
            betQuantity = 2
        )
        val hiddenBet = CartBetDto(id = 3, modality = null, betQuantity = 9)
        val dto = CartDto(
            individualBets = listOf(individualBet, hiddenBet),
            combos = listOf(CartComboDto(id = 2, bets = emptyList())),
            bets = listOf(individualBet, hiddenBet)
        )
        val repository = CartRepositoryImpl(source(FakeApi(cart = ApiEnvelope(dto))))
        val cart = repository.current()
        assertEquals(BigDecimal.ZERO, cart.total)
        assertEquals(listOf(1L, 2L), cart.items.map { it.id })
        assertEquals(2, cart.betCount)
        assertEquals("2 apostas", cart.items[0].description)
        assertEquals("Combo", cart.items[1].title)
        assertEquals("0 apostas", cart.items[1].description)
        runCatching { CartRepositoryImpl(source(FakeApi(cart = ApiEnvelope()))).current() }
            .onSuccess { error("Carrinho ausente deveria falhar") }
        Unit
    }

    @Test
    fun `betting repository maps catalogs parameters combos and cart mutations`() = runBlocking {
        val api = FakeApi()
        var surpriseBody: com.google.gson.JsonObject? = null
        val backend = java.lang.reflect.Proxy.newProxyInstance(
            BackendApi::class.java.classLoader,
            arrayOf(BackendApi::class.java)
        ) { _, method, args ->
            check(method.name == "addSurpriseBets")
            surpriseBody = (args!![0] as com.google.gson.JsonElement).asJsonObject
            ApiEnvelope(CartDto(id = 1))
        } as BackendApi
        val repository = BettingRepositoryImpl(source(api, backend))

        val modalities = repository.modalities()
        assertEquals("MEGA_SENA", modalities.first().code)
        assertEquals("", modalities.last().code)
        assertEquals(13, modalities.size)

        val parameters = repository.parameters()
        assertEquals(BigDecimal("200"), parameters.dailyLimit)
        assertEquals(14, parameters.games.size)
        val mega = parameters.games.first()
        assertEquals(2, mega.modality.id)
        assertEquals("Mega-Sena", mega.modality.name)
        assertEquals(60, mega.maximumNumber)
        assertEquals(listOf(2, 3), mega.recurringOptions)
        assertEquals(BigDecimal("5"), mega.prices.single().amount)
        assertEquals(2, mega.minimumClovers)
        assertTrue(mega.open)
        assertEquals("10/09/2026 20:00:00", mega.drawDate)
        assertEquals("10/09/2026 19:00:00", mega.closingDateTime)
        assertEquals(BigDecimal("35000000"), mega.estimatedPrize)
        assertEquals(2, parameters.availablePoolModalities.single().modalityId)
        assertEquals("MEGA_SENA", parameters.availablePoolModalities.single().modalityCode)

        val combo = repository.combos().first()
        assertEquals(7, combo.id)
        assertEquals("Combo da Sorte", combo.name)
        assertEquals(2, combo.entries.single().betQuantity)
        assertEquals("Combo", repository.combos().last().name)

        repository.addBet(BetDraft(2, "ESPECIAL", listOf(1, 2), listOf(3), 2, true, 4))
        assertNull(api.addedBet)
        assertEquals("MEGA_SENA", surpriseBody?.get("modalidade")?.asString)
        assertEquals("2", surpriseBody?.getAsJsonObject("tipoConcurso")?.get("valor")?.asString)
        assertEquals(2, surpriseBody?.getAsJsonObject("indicadorSurpresinha")?.get("valor")?.asInt)
        assertEquals(4, surpriseBody?.get("quantidadeSurpresinhas")?.asInt)
        repository.addBet(BetDraft(999, "NORMAL", listOf(1), surprise = false))
        assertEquals("", api.addedBet?.modality)
        assertEquals("1", api.addedBet?.contestType?.value)
        assertEquals(1, api.addedBet?.surpriseIndicator?.value)
        repository.addCombo(7, 3, false)
        assertEquals(AddComboRequestDto(7, 3, false), api.addedCombo)
    }

    @Test
    fun `betting repository supplies safe defaults and rejects absent parameters`() = runBlocking {
        val repository = BettingRepositoryImpl(
            source(
                FakeApi(
                    betting = ApiEnvelope(BettingParametersDto(parameters = listOf(LotteryParameterDto()))),
                    modalities = ApiEnvelope(listOf(LotteryModalityDto())),
                    betComboResponse = ApiEnvelope()
                )
            )
        )
        assertEquals(0, repository.modalities().single().id)
        assertTrue(repository.parameters().games.isEmpty())
        assertTrue(repository.combos().isEmpty())
        runCatching { BettingRepositoryImpl(source(FakeApi(betting = ApiEnvelope()))).parameters() }
            .onSuccess { error("Parâmetros ausentes deveriam falhar") }
        Unit
    }

    @Test
    fun `betting repository maps sports teams shields matches and request selections`() = runBlocking {
        val home = SportsTeamParameterDto(name = "Azul", number = 101, state = "DF")
        val away = SportsTeamParameterDto(name = "Verde", number = 102, state = "SP")
        val sportsParameter = LotteryGameParameterDto(
            contest = LotteryContestDto(
                modalityCode = "LOTECA",
                modality = LotteryModalityDto(19, "Loteca"),
                number = 1200
            ),
            prices = listOf(LotteryPriceDto(BigDecimal("6"), 0, 0, 1, 0)),
            teams = listOf(home, away),
            matches = listOf(SportsMatchParameterDto(1, home, away, "Jogo decisivo", "D")),
            luckyMonths = listOf(LuckyMonthParameterDto(1, "Janeiro", "JAN")),
            acceptsMirror = true
        )
        val api = FakeApi(
            betting = ApiEnvelope(BettingParametersDto(parameters = listOf(LotteryParameterDto(sportsParameter))))
        )
        val legacy = fakeLegacyApi(
            listOf(SportsTeamShieldDto(1, listOf("LOTECA"), SportsShieldTeamDto(101), "shield"))
        )
        val repository = BettingRepositoryImpl(source(api, legacy))

        val game = repository.parameters().games.single()
        assertEquals("shield", game.teams.first().shieldBase64)
        assertEquals("Janeiro", game.luckyMonths.single().name)
        assertTrue(game.acceptsMirror)
        assertEquals("Verde", game.matches.single().away.name)
        assertEquals(1, game.prices.single().doubleQuantity)

        val match = game.matches.single()
        repository.addBet(
            BetDraft(
                modalityId = 19,
                numbers = emptyList(),
                numberQuantity = 0,
                favoriteTeam = game.teams.first(),
                lotecaSelections = listOf(LotecaMatchSelection(match, true, true, false)),
                lotogolSelections = listOf(LotogolMatchSelection(match, 5, 0)),
                superSevenColumns = listOf(listOf(0), listOf(1)),
                luckyMonth = LuckyMonth(1, "Janeiro", "JAN"),
                generateMirror = true
            )
        )
        assertEquals(101, api.addedBet?.favoriteTeam?.number)
        assertTrue(requireNotNull(requireNotNull(api.addedBet).lotecaMatches).single().draw)
        assertEquals("5", requireNotNull(requireNotNull(api.addedBet).lotogolMatches).single().home.score)
        assertEquals(JsonParser.parseString("[[0],[1]]"), api.addedBet?.selectedNumbers)
        assertEquals("Janeiro", api.addedBet?.luckyMonth?.name)
        assertTrue(api.addedBet?.generateMirror == true)
    }

    private fun source(api: LoteriasApi, legacyApi: BackendApi? = null) = TestRemoteSources(
        apiProvider = LoteriasApiProvider().apply { replace(api) },
        legacyApiProvider = legacyApi?.let { BackendApiProvider().apply { replace(it) } }
    )

    /** Os dados de teste simplificam os casos de paridade; a produção injeta separadamente cada adaptador do domínio. */
    private class TestRemoteSources(apiProvider: LoteriasApiProvider, legacyApiProvider: BackendApiProvider?) :
        AppConfigurationRemoteDataSource by AppConfigurationRemoteDataSourceImpl(apiProvider),
        br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSource by
        br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSourceImpl(apiProvider),
        br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSource by
        br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSourceImpl(apiProvider, legacyApiProvider),
        br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSource by
        br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSourceImpl(apiProvider),
        br.gov.caixa.loterias.feature.cart.data.datasource.remote.FavoriteCartRemoteDataSource by
        br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSourceImpl(apiProvider),
        br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSource by
        br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSourceImpl(apiProvider),
        br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingRemoteDataSource by
        br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingRemoteDataSourceImpl(apiProvider),
        br.gov.caixa.loterias.feature.support.data.datasource.remote.SupportRemoteDataSource by
        br.gov.caixa.loterias.feature.support.data.datasource.remote.SupportRemoteDataSourceImpl(apiProvider) {
        val results = br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsRemoteDataSourceImpl(
            apiProvider,
            legacyApiProvider
        )
        private val accountSource = br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSourceImpl(apiProvider)

        override suspend fun currentUserProfile() = accountSource.currentUserProfile()
    }

    private fun fakeLegacyApi(shields: List<SportsTeamShieldDto>): BackendApi = java.lang.reflect.Proxy.newProxyInstance(
        BackendApi::class.java.classLoader,
        arrayOf(BackendApi::class.java)
    ) { _, method, _ ->
        if (method.name == "sportsTeams") {
            ApiEnvelope(shields)
        } else {
            error("Unexpected legacy call ${method.name}")
        }
    } as BackendApi

    private class FakeApi(
        private val parameters: ApiEnvelope<AppParametersMapDto> = ApiEnvelope(
            AppParametersMapDto(
                AppParametersDto(
                    minimumMarketplaceBetQuantity = 2,
                    maximumMarketplaceBetQuantity = 4,
                    poolSalesEnabled = true,
                    comboBettingEnabled = true,
                    rapidBettingEnabled = true
                )
            )
        ),
        private val sections: ApiEnvelope<List<SupportSectionDto>> = ApiEnvelope(
            listOf(
                SupportSectionDto(
                    id = 10,
                    name = " Geral ",
                    questions = listOf(SupportQuestionDto(20, " Pergunta ", " Resposta "))
                )
            )
        ),
        private val terms: ApiEnvelope<TermsDto> = ApiEnvelope(TermsDto("<p>Termos</p>")),
        private val marketplace: ApiEnvelope<MarketplacePageDto> = ApiEnvelope(
            MarketplacePageDto(
                totalRecords = 8,
                currentPage = 4,
                lastPage = 5,
                pools = listOf(
                    MarketplacePoolDto(
                        code = "B1",
                        modality = "Mega-Sena",
                        contest = 2800,
                        lotteryName = "Loterias Centro",
                        drawDate = "10/09/2026",
                        sharePrice = "12.50",
                        availableShares = 4,
                        betQuantity = 3,
                        numberQuantity = 6,
                        favoriteLottery = true,
                        lotteryId = 99,
                        city = MarketplaceCityDto("Brasília", MarketplaceStateDto("DF")),
                        totalShares = 10,
                        contestType = 1,
                        estimatedPrize = "1000000",
                        regularSharePriceWithFee = BigDecimal("10.00"),
                        serviceFeePerShare = BigDecimal("2.50"),
                        lastSharePriceWithFee = BigDecimal("12.50")
                    )
                )
            )
        ),
        private val detail: ApiEnvelope<MarketplacePoolDetailDto> = ApiEnvelope(
            MarketplacePoolDetailDto(
                totalShares = 10,
                availableShares = 4,
                lotteryId = 99,
                lotteryName = "Loterias Centro",
                city = MarketplaceCityDto(" Brasília ", MarketplaceStateDto(" DF ")),
                contest = 2801,
                contestType = 2,
                modality = "Mega-Sena",
                drawDate = " 10/09/2026 ",
                drawTime = " 20:00 ",
                estimatedPrize = "1000000.00",
                betQuantity = 2,
                numberQuantity = 6,
                sharePriceWithoutFee = "9.00",
                serviceFeePerShare = BigDecimal("2.50"),
                lastShareFee = "1.00",
                lastSharePriceWithFee = "10.00",
                formattedLottery = MarketplaceLotteryDto("000099-9"),
                bets = listOf(
                    MarketplaceBetDto(true, JsonParser.parseString("[[1,2],[3,4]]"), listOf(5, 6)),
                    MarketplaceBetDto(false, JsonParser.parseString("\"unsupported\""), null)
                )
            )
        ),
        private val result: ApiEnvelope<DrawResultDto> = ApiEnvelope(
            DrawResultDto(
                city = MarketplaceCityDto(" Brasília ", MarketplaceStateDto(" DF ")),
                notice = "Aviso",
                totalRevenue = "1000000.00",
                accumulatedNextContest = "2000000.00",
                accumulated = true,
                firstDrawNumbers = JsonParser.parseString("[[1,2],3]"),
                secondDrawNumbers = listOf(4, 5),
                clovers = listOf(6, 7),
                contest = DrawContestDto("Mega-Sena", 2802, "11/09/2026"),
                payouts = listOf(DrawPayoutDto("Sena", 1, "900000.00"))
            )
        ),
        private val assessment: ApiEnvelope<SelfAssessmentPayloadDto> = ApiEnvelope(
            SelfAssessmentPayloadDto(
                listOf(
                    SelfAssessmentQuestionDto(8, " Pergunta responsável ", 1, true),
                    SelfAssessmentQuestionDto(9, "Inativa", 2, false)
                )
            )
        ),
        private val social: ApiEnvelope<SocialTransfersDto> = ApiEnvelope(
            SocialTransfersDto(
                accumulatedRevenue = BigDecimal("1000"),
                lastWeekRevenue = BigDecimal("100"),
                baseDate = "04/09/2026",
                socialProgramsPercentage = BigDecimal("48"),
                educationPercentage = BigDecimal("30"),
                culturePercentage = BigDecimal("10"),
                sportPercentage = BigDecimal("5"),
                securityPercentage = BigDecimal("2"),
                socialSecurityPercentage = BigDecimal("1"),
                otherPercentage = BigDecimal("0")
            )
        ),
        private val profile: ApiEnvelope<UserProfileDto> = ApiEnvelope(
            UserProfileDto(
                cpf = "123.456.789-01",
                email = "pessoa@caixa.gov.br",
                name = "Pessoa CAIXA",
                zipCode = "70000000",
                acceptsNotifications = true,
                municipalityId = MunicipalityIdDto(7, 1, 0),
                gender = "F",
                minimumDailyLimit = BigDecimal("10"),
                dailyLimit = BigDecimal("100"),
                authorizedDailyLimit = BigDecimal("200")
            )
        ),
        private val municipalities: ApiEnvelope<List<MunicipalityDto>> = ApiEnvelope(
            listOf(MunicipalityDto(MunicipalityIdDto(7, 1, 0), "Brasília", FederativeUnitDto(7, "DF", "Distrito Federal")))
        ),
        private val neighborhoods: ApiEnvelope<List<NeighborhoodDto>> = ApiEnvelope(
            listOf(
                NeighborhoodDto(
                    1,
                    "Centro",
                    MunicipalityDto(MunicipalityIdDto(7, 1, 0), "Brasília", FederativeUnitDto(7, "DF", "Distrito Federal"))
                )
            )
        ),
        private val verification: ApiEnvelope<AccountSessionDto> = ApiEnvelope(
            AccountSessionDto(dailyLimit = BigDecimal("90"), temporarySuspension = true)
        ),
        private val cart: ApiEnvelope<CartDto> = ApiEnvelope(
            CartDto(
                id = 9,
                total = BigDecimal("42.50"),
                poolShares = listOf(
                    CartBetDto(
                        id = 11,
                        modality = JsonParser.parseString("\"MEGA_SENA\""),
                        poolReservation = CartPoolReservationDto("B10", 4, BigDecimal("12.50"))
                    )
                ),
                individualBets = listOf(
                    CartBetDto(
                        id = 12,
                        modality = JsonParser.parseString("\"LOTOFACIL\""),
                        selectedNumbers = JsonParser.parseString("[[1,2],3]"),
                        amount = BigDecimal("20.00"),
                        betQuantity = 1
                    )
                ),
                combos = listOf(
                    CartComboDto(
                        13,
                        CartComboTypeDto("Combo da Sorte", "2 modalidades"),
                        listOf(CartBetDto(amount = BigDecimal("4.00")), CartBetDto(amount = BigDecimal("6.00")))
                    )
                ),
                expiredPoolShareRemoved = true
            )
        ),
        private val favorites: ApiEnvelope<List<FavoriteCartDto>> = ApiEnvelope(
            listOf(FavoriteCartDto(21, "Meu favorito"))
        ),
        private val favoriteBets: ApiEnvelope<List<FavoriteCartBetDto>> = ApiEnvelope(
            listOf(
                FavoriteCartBetDto(
                    id = 31,
                    modality = FavoriteCartModalityDto(1, "Mega-Sena", "Mega da Virada"),
                    selectedNumbers = JsonParser.parseString("[1,[2]]"),
                    recurringDraws = 2,
                    mirror = true,
                    surpriseIndicator = "SURPRESINHA",
                    clovers = FavoriteCartCloversDto(JsonParser.parseString("[3,4]"))
                )
            )
        ),
        private val suspension: ApiEnvelope<AccountSuspensionDto> = ApiEnvelope(AccountSuspensionDto(true, "24 horas")),
        private val modalities: ApiEnvelope<List<LotteryModalityDto>> = ApiEnvelope(
            listOf(2, 3, 7, 8, 9, 11, 14, 16, 18, 19, 20, 1502, 999).map { id ->
                LotteryModalityDto(id, if (id == 2) "Mega-Sena" else "Modalidade $id")
            }
        ),
        private val betting: ApiEnvelope<BettingParametersDto> = ApiEnvelope(
            BettingParametersDto(
                BigDecimal("200"),
                BigDecimal("30"),
                50,
                listOf(
                    LotteryParameterDto(
                        game = LotteryGameParameterDto(
                            contest = LotteryContestDto(
                                modality = LotteryModalityDto(2, "Mega-Sena"),
                                number = 2900,
                                drawDate = "10/09/2026",
                                drawDateTime = "10/09/2026 20:00:00",
                                closingDateTime = "10/09/2026 19:00:00",
                                estimatedPrize = BigDecimal("35000000"),
                                open = true
                            ),
                            maximumNumber = 60,
                            minimumSelection = 6,
                            maximumSelection = 20,
                            maximumSurprises = 15,
                            recurringOptions = listOf(2, 3),
                            prices = listOf(LotteryPriceDto(BigDecimal("5"), 6, 1)),
                            open = true,
                            clovers = LotteryCloverParametersDto(6, 2, 6)
                        )
                    )
                ) + listOf(
                    "MEGA_SENA", "QUINA", "SUPER_7", "LOTOFACIL", "MAIS_MILIONARIA", "DIA_DE_SORTE", "LOTOGOL",
                    "LOTOMANIA", "DUPLA_SENA", "LOTECA", "TIMEMANIA", "INSTANTANEA", "UNKNOWN"
                ).map { code ->
                    LotteryParameterDto(
                        nextGame = LotteryGameParameterDto(contest = LotteryContestDto(modalityCode = code))
                    )
                } + LotteryParameterDto(),
                availablePoolModalities = listOf(
                    AvailablePoolModalityDto("MEGA_SENA", LotteryModalityDto(2, "Mega-Sena"), "NORMAL")
                )
            )
        ),
        private val betComboResponse: ApiEnvelope<List<BetComboDto>> = ApiEnvelope(
            listOf(
                BetComboDto(
                    BetComboTypeDto(7, "Combo da Sorte", "8 apostas", true),
                    listOf(BetComboEntryDto(LotteryModalityDto(2, "Mega-Sena"), "NORMAL", 2, 3, 6, 0, 2900)),
                    BigDecimal("20")
                ),
                BetComboDto()
            )
        ),
    ) : LoteriasApi {
        var acceptCalls = 0
        var parameterCalls = 0
        var marketplaceQuery: Map<String, String> = emptyMap()
        var detailCode: String = ""
        var latestModality: String = ""
        var resultModality: String = ""
        var resultContest: Int = 0
        var assessmentAnswers: List<SelfAssessmentAnswerDto> = emptyList()
        var suspensionRequest: SuspendAccountRequestDto? = null
        var personalDataUpdate: PersonalDataUpdateDto? = null
        var accountRegistration: AccountRegistrationDto? = null
        var verificationRequest: AccountVerificationRequestDto? = null
        var removedBetId: Long? = null
        var removedComboId: Long? = null
        var clearCartCalls = 0
        var validatedFavoriteName: String? = null
        var savedFavoriteName: String? = null
        var deletedFavoriteCart: Long? = null
        var deletedFavoriteBet: Pair<Long, Long>? = null
        var importedFavoriteCart: Pair<Long, Int>? = null
        var addedBet: AddBetRequestDto? = null
        var addedCombo: AddComboRequestDto? = null
        var addedPoolShare: AddPoolShareRequestDto? = null

        override suspend fun isVersionSupported(version: String) = ApiEnvelope(version == "1.24.0")
        override suspend fun appParameters(): ApiEnvelope<AppParametersMapDto> {
            parameterCalls++
            return parameters
        }
        override suspend fun lotteryModalities() = modalities
        override suspend fun bettingParameters() = betting
        override suspend fun betCombos() = betComboResponse
        override suspend fun addBetToCart(request: AddBetRequestDto): ApiEnvelope<CartDto> {
            addedBet = request
            return cart
        }
        override suspend fun addPoolShareToCart(request: AddPoolShareRequestDto): ApiEnvelope<CartDto> {
            addedPoolShare = request
            return cart
        }
        override suspend fun addComboToCart(request: AddComboRequestDto): ApiEnvelope<CartDto> {
            addedCombo = request
            return cart
        }
        override suspend fun supportSections() = sections
        override suspend fun supportQuestions(sectionId: Long) = if (sectionId == 10L) sections else ApiEnvelope(emptyList())
        override suspend fun currentTerms() = terms
        override suspend fun acceptCurrentTerms(): ApiEnvelope<Unit> {
            acceptCalls++
            return ApiEnvelope(Unit)
        }
        override suspend fun marketplacePools(filters: Map<String, String>): ApiEnvelope<MarketplacePageDto> {
            marketplaceQuery = filters
            return marketplace
        }
        override suspend fun marketplacePoolDetail(poolCode: String): ApiEnvelope<MarketplacePoolDetailDto> {
            detailCode = poolCode
            return detail
        }
        override suspend fun latestResult(modality: String): ApiEnvelope<DrawResultDto> {
            latestModality = modality
            return result
        }
        override suspend fun resultByContest(modality: String, contest: Int): ApiEnvelope<DrawResultDto> {
            resultModality = modality
            resultContest = contest
            return result
        }
        override suspend fun selfAssessmentQuestions() = assessment
        override suspend fun submitSelfAssessment(answers: List<SelfAssessmentAnswerDto>): ApiEnvelope<SelfAssessmentPayloadDto> {
            assessmentAnswers = answers
            return assessment
        }
        override suspend fun socialTransfers() = social
        override suspend fun currentUserProfile() = profile
        override suspend fun municipalities(stateId: Long) = municipalities
        override suspend fun neighborhoodsByZipCode(zipCode: String) = neighborhoods
        override suspend fun updatePersonalData(request: PersonalDataUpdateDto): ApiEnvelope<Unit> {
            personalDataUpdate = request
            return ApiEnvelope(Unit)
        }
        override suspend fun registerAccount(request: AccountRegistrationDto): ApiEnvelope<UserProfileDto> {
            accountRegistration = request
            return profile
        }
        override suspend fun verifyAccount(request: AccountVerificationRequestDto): ApiEnvelope<AccountSessionDto> {
            verificationRequest = request
            return verification
        }
        override suspend fun recalculateAuthorizedLimit() = profile
        override suspend fun currentCart() = cart
        override suspend fun removeBoundCartBet(
            id: Long,
            session: br.gov.caixa.loterias.core.network.api.AccountVerificationSession,
        ): ApiEnvelope<CartDto> = removeCartBet(id)

        override suspend fun removeCartBet(id: Long): ApiEnvelope<CartDto> {
            removedBetId = id
            return cart
        }
        override suspend fun removeCartCombo(id: Long): ApiEnvelope<CartDto> {
            removedComboId = id
            return cart
        }
        override suspend fun clearCart(): ApiEnvelope<CartDto> {
            clearCartCalls++
            return cart
        }
        override suspend fun validateCart() = ApiEnvelope(JsonParser.parseString("{}"))
        override suspend fun validateFavoriteCartName(request: SaveFavoriteCartRequestDto) {
            validatedFavoriteName = request.name
        }
        override suspend fun saveCurrentCartFavorite(request: SaveFavoriteCartRequestDto) {
            savedFavoriteName = request.name
        }
        override suspend fun favoriteCarts() = favorites
        override suspend fun favoriteCartBets(cartId: Long) = favoriteBets
        override suspend fun deleteFavoriteCart(cartId: Long): ApiEnvelope<List<FavoriteCartDto>> {
            deletedFavoriteCart = cartId
            return favorites
        }
        override suspend fun deleteFavoriteCartBet(cartId: Long, betId: Long): ApiEnvelope<List<FavoriteCartBetDto>> {
            deletedFavoriteBet = cartId to betId
            return favoriteBets
        }
        override suspend fun addFavoriteCartToCurrent(cartId: Long, request: AddFavoriteCartRequestDto): ApiEnvelope<CartDto> {
            importedFavoriteCart = cartId to request.specialSelection
            return cart
        }
        override suspend fun suspendAccount(request: SuspendAccountRequestDto): ApiEnvelope<AccountSuspensionDto> {
            suspensionRequest = request
            return suspension
        }
    }
}
