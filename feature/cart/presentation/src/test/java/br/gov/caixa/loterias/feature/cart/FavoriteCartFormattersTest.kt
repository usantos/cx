package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteBetClovers
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteBetComplement
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteBetNumbers
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteBetSummaryText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteDetailModalityName
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteSummaryModalityName

class FavoriteCartFormattersTest {
    private val bet = FavoriteCartBet(1, "Mega-Sena", listOf(10, 1, 100), modalityId = 2)

    @Test fun summaryKeepsLowercaseAndMilionariaLabel() {
        assertEquals("mega-sena", favoriteSummaryModalityName(bet))
        assertEquals("+milionária", favoriteSummaryModalityName(bet.copy(modalityId = 9)))
        assertEquals("timemania", favoriteSummaryModalityName(bet.copy(modalityId = 20, modality = "Timemania")))
    }

    @Test fun detailPreservesTimemaniaCaseWhileOtherLabelsUseTheirOriginalRules() {
        assertEquals("mega-sena", favoriteDetailModalityName(bet))
        assertEquals("Timemania", favoriteDetailModalityName(bet.copy(modalityId = 20, modality = "Timemania")))
        assertEquals("+milionária", favoriteDetailModalityName(bet.copy(modalityId = 9)))
        assertEquals("mega-sena", favoriteDetailModalityName(bet.copy(modalityId = null)))
    }

    @Test fun summaryUsesBackendCountsAndOnlyPositiveCloverCount() {
        assertEquals("8 num(s)", favoriteBetSummaryText(bet.copy(numberCount = 8)))
        assertEquals("8 num(s) 2 trev(s)", favoriteBetSummaryText(bet.copy(numberCount = 8, cloverCount = 2)))
        assertEquals("3 num(s)", favoriteBetSummaryText(bet.copy(cloverCount = -1)))
    }

    @Test fun complementsKeepNullAndEmptyValuesAndHideSurpriseTeamAndMonth() {
        assertNull(favoriteBetComplement(bet))
        assertNull(favoriteBetComplement(bet.copy(modalityId = 20)))
        assertEquals("", favoriteBetComplement(bet.copy(modalityId = 11, luckyMonth = "")))
        assertEquals("CAIXA/DF", favoriteBetComplement(bet.copy(modalityId = 20, favoriteTeam = "CAIXA/DF")))
        assertEquals("Setembro", favoriteBetComplement(bet.copy(modalityId = 11, luckyMonth = "Setembro")))
        assertEquals("???", favoriteBetComplement(bet.copy(modalityId = 20, surprise = true)))
        assertEquals("???", favoriteBetComplement(bet.copy(modalityId = 11, surprise = true)))
        assertNull(favoriteBetComplement(bet.copy(surprise = true)))
    }

    @Test fun numbersPreserveOrderMapHundredToZeroAndUseSurpriseCount() {
        assertEquals("10 - 01 - 00", favoriteBetNumbers(bet))
        assertEquals("XX - XX", favoriteBetNumbers(bet.copy(surprise = true, numberCount = 2)))
        assertEquals("", favoriteBetNumbers(bet.copy(surprise = true, numberCount = 0)))
        assertEquals("", favoriteBetNumbers(bet.copy(numbers = emptyList())))
    }

    @Test fun cloversKeepTwoSpacesAndActualOrderOrSurpriseCount() {
        assertEquals("Trevos:  4 - 2", favoriteBetClovers(bet.copy(clovers = listOf(4, 2))))
        assertEquals("Trevos:  XX - XX", favoriteBetClovers(bet.copy(surprise = true, cloverCount = 2)))
        assertEquals("Trevos:  ", favoriteBetClovers(bet.copy(clovers = emptyList())))
    }
}
