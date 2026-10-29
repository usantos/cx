package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import br.gov.caixa.loterias.feature.cart.domain.model.VisitorCartSelection
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.data.mapper.guestCartRows
import br.gov.caixa.loterias.feature.cart.data.mapper.decodeLegacyCart
import br.gov.caixa.loterias.feature.cart.data.mapper.visitorCartSelection

class VisitorCartSelectionMapperTest {
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 3000, "", 60, 6, 20, 10,
        emptyList(), emptyList())
    private fun bet(draft: BetDraft) = decodeLegacyCart(guestCartRows(draft, game, BigDecimal.TEN, 1, 1)).single()

    @Test fun identityIgnoresStorageIdsPricesFlagsAndComplementaryNamesButNormalizesMissingClovers() {
        val source = bet(BetDraft(2, numbers = listOf(1, 2, 3, 4, 5, 6),
            favoriteTeam = SportsTeam(10, "Time"), luckyMonth = LuckyMonth(12, "Dezembro")))
        val altered = source.copy(localId = 99, serverId = 123, amount = BigDecimal.ONE, numberCount = 20,
            mirrored = true, mirrorLink = 999, generateMirror = true, quickPick = true, clovers = null,
            favoriteTeam = source.favoriteTeam!!.copy(name = "Updated"), luckyMonth = source.luckyMonth!!.copy(name = "Updated"))
        assertEquals(source.visitorCartSelection(), altered.visitorCartSelection())
        assertEquals(VisitorCartSelection(CartModality.MEGA_SENA, 3000, "N",
            CartNumbers.Flat(listOf(1, 2, 3, 4, 5, 6)), emptyList(), 0, 10, 12, emptyList(), emptyList()),
            source.visitorCartSelection())
    }

    @Test fun identityRetainsExactContestCodeColumnsAndSportsGraphMetadata() {
        val team = SportsTeam(10, "Time", countryNumber = 55, selectionIndicator = true)
        val match = SportsMatch(1, team, team.copy(number = 11, name = "Outro"))
        val source = bet(BetDraft(19, numbers = emptyList(), contestType = "ESPECIAL", recurringDraws = 3,
            lotecaSelections = listOf(LotecaMatchSelection(match, true, true, false))))
        val identity = source.visitorCartSelection()
        assertEquals("E", identity.contestType)
        assertEquals(3, identity.repeatCount)
        assertEquals(source.loteca, identity.loteca)
        assertEquals(55, identity.loteca.single().first.parameter!!.countryNumber)
        val columns = bet(BetDraft(7, numbers = emptyList(), superSevenColumns = List(7) { listOf(it) }))
        assertEquals(CartNumbers.Columns(List(7) { listOf(it) }), columns.visitorCartSelection().numbers)
    }
}
