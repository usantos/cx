package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.presentation.mapper.rapidFavoriteNumbers
import org.junit.Assert.assertEquals
import org.junit.Test

class RapidConfigurationFormattersTest {
    @Test fun singleColumnKeepsOrderAndPadsIndividualNumbers() {
        val bet = favorite(listOf(listOf(10, 1, 100)))
        assertEquals("10 - 01 - 100", rapidFavoriteNumbers(bet))
    }

    @Test fun multipleColumnsUseThreeSpacesAndKeepEmptyColumns() {
        val bet = favorite(listOf(listOf(0, 9), emptyList(), listOf(5)))
        assertEquals("00 - 09      05", rapidFavoriteNumbers(bet))
    }

    @Test fun complementsKeepTheirOrderAndBlankValuesAreOmitted() {
        val bet = favorite(emptyList()).copy(heartTeam = "CAIXA", luckyMonth = "Janeiro", clovers = listOf(2, 1))
        assertEquals("\nTime do coração: CAIXA\nMês de sorte: Janeiro\nTrevos: 2 - 1", rapidFavoriteNumbers(bet))
        assertEquals("", rapidFavoriteNumbers(bet.copy(heartTeam = "  ", luckyMonth = "", clovers = emptyList())))
    }

    private fun favorite(columns: List<List<Int>>) = FavoriteBet(1, 2, "Mega-Sena", "Minha aposta", columns)
}
