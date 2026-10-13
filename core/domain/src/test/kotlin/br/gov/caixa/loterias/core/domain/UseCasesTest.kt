package br.gov.caixa.loterias.core.domain

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.GetMarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.configuration.IsVersionSupported
import br.gov.caixa.loterias.core.domain.marketplace.GetMarketplaceDetail
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceDetailRepository
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePage
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolDetail
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceRepository
import br.gov.caixa.loterias.core.domain.marketplace.SearchMarketplace
import br.gov.caixa.loterias.core.domain.results.DrawResult
import br.gov.caixa.loterias.core.domain.results.GetLatestResult
import br.gov.caixa.loterias.core.domain.results.GetResultByContest
import br.gov.caixa.loterias.core.domain.results.ResultsRepository
import br.gov.caixa.loterias.core.domain.support.AcceptTerms
import br.gov.caixa.loterias.core.domain.support.GetSupportQuestions
import br.gov.caixa.loterias.core.domain.support.GetSupportSections
import br.gov.caixa.loterias.core.domain.support.GetTerms
import br.gov.caixa.loterias.core.domain.support.SupportQuestion
import br.gov.caixa.loterias.core.domain.support.SupportRepository
import br.gov.caixa.loterias.core.domain.support.SupportSection
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UseCasesTest {
    @Test
    fun `configuration use cases delegate to repository`() = runTest {
        val parameters = MarketplaceFilterParameters(2, 3)
        val repository = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String) = versionName == "1"
            override suspend fun marketplaceFilterParameters() = parameters
        }

        assertEquals(AppResult.Success(true), IsVersionSupported(repository)("1"))
        assertEquals(AppResult.Success(parameters), GetMarketplaceFilterParameters(repository)())
    }

    @Test
    fun `marketplace use cases delegate to narrow contracts`() = runTest {
        val page = MarketplacePage(emptyList(), 0, 0, 0)
        val detail = marketplaceDetail()
        val search = SearchMarketplace(MarketplaceRepository { _, _ -> page })
        val getDetail = GetMarketplaceDetail(MarketplaceDetailRepository { detail })

        assertEquals(AppResult.Success(page), search(MarketplaceFilter.empty(), 0))
        assertEquals(AppResult.Success(detail), getDetail("B1"))
    }

    @Test
    fun `results and support use cases delegate to repositories`() = runTest {
        val draw = DrawResult("Mega", 1, "", "", emptyList(), emptyList(), emptyList(), false, "", "", "", emptyList())
        val results = object : ResultsRepository {
            override suspend fun latest(modality: String) = draw
            override suspend fun byContest(modality: String, contest: Int) = draw
        }
        val question = SupportQuestion(2, "Q", "A")
        val section = SupportSection(1, "S", listOf(question))
        var accepted = false
        val support = object : SupportRepository {
            override suspend fun sections() = listOf(section)
            override suspend fun questions(sectionId: Long) = listOf(question)
            override suspend fun terms() = "terms"
            override suspend fun acceptTerms() { accepted = true }
        }

        assertEquals(AppResult.Success(draw), GetLatestResult(results)("mega"))
        assertEquals(AppResult.Success(draw), GetResultByContest(results)("mega", 1))
        assertEquals(AppResult.Success(listOf(section)), GetSupportSections(support)())
        assertEquals(AppResult.Success(listOf(question)), GetSupportQuestions(support)(1))
        assertEquals(AppResult.Success("terms"), GetTerms(support)())
        assertEquals(AppResult.Success(Unit), AcceptTerms(support)())
        assertEquals(true, accepted)
    }

    private fun marketplaceDetail() = MarketplacePoolDetail(
        code = "B1",
        modality = "Mega",
        contest = 1,
        lotteryId = 1,
        lotteryName = "Lottery",
        lotteryNumber = "1",
        cityAndState = "",
        drawDateAndTime = "",
        estimatedPrize = "",
        availableShares = 1,
        totalShares = 1,
        betQuantity = 1,
        numberQuantity = 6,
        sharePriceWithoutFee = "1",
        lastShareFee = "0",
        lastSharePriceWithFee = "1",
        bets = emptyList(),
    )
}
