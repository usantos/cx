package br.gov.caixa.loterias.feature.betting.domain

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.rule.BetSelectionEligibility
import br.gov.caixa.loterias.feature.betting.domain.rule.BetSelectionStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BetSelectionEligibilityTest {
    private val selection = BetSelectionStatus(
        false, 6, 6, 0, 0, 0, 1, false, false,
        List(SUPER_SEVEN_COLUMNS) { emptySet() }, emptyMap(), emptyMap()
    )
    private val baseGame = LotteryGameParameters(
        LotteryModality(2, "Fixture", "FIXTURE"),
        1, "", 60, 6, 20, 10, emptyList(), emptyList()
    )
    private fun game(modality: Int) = baseGame.copy(modality = baseGame.modality.copy(id = modality))

    @Test fun numericSelectionRequiresCompleteCountAndOpenGame() {
        assertTrue(BetSelectionEligibility.canRegister(game(2), selection))
        assertFalse(BetSelectionEligibility.canRegister(game(2).copy(open = false), selection))
        assertFalse(BetSelectionEligibility.canRegister(game(2), selection.copy(numberCount = INCOMPLETE_NUMBER_COUNT)))
    }

    @Test fun timemaniaAndDiaDeSorteRequireTheirComplements() {
        assertFalse(BetSelectionEligibility.canRegister(game(TIMEMANIA), selection))
        assertTrue(BetSelectionEligibility.canRegister(game(TIMEMANIA), selection.copy(teamSelected = true)))
        assertFalse(BetSelectionEligibility.canRegister(game(DIA_DE_SORTE), selection))
        assertTrue(BetSelectionEligibility.canRegister(game(DIA_DE_SORTE), selection.copy(monthSelected = true)))
    }

    @Test fun superSevenRequiresSevenNonemptyColumnsAndExactQuantity() {
        assertTrue(
            BetSelectionEligibility.canRegister(
                game(SUPER_SEVEN),
                selection.copy(
                    numberQuantity = SUPER_SEVEN_COLUMNS,
                    superSevenColumns = List(SUPER_SEVEN_COLUMNS) { setOf(1) }
                )
            )
        )
        assertFalse(
            BetSelectionEligibility.canRegister(
                game(SUPER_SEVEN),
                selection.copy(
                    numberQuantity = SUPER_SEVEN_COLUMNS,
                    superSevenColumns = List(INCOMPLETE_COLUMN_COUNT) { setOf(1) }
                )
            )
        )
    }

    @Test fun surprisePreservesQuantityAndCloverBounds() {
        val million = game(MAIS_MILIONARIA).copy(minimumClovers = 2, maximumClovers = 6)
        assertFalse(BetSelectionEligibility.canRegister(million, selection.copy(surprise = true, surpriseCloverQuantity = 1)))
        assertTrue(BetSelectionEligibility.canRegister(million, selection.copy(surprise = true, surpriseCloverQuantity = 2)))
        assertFalse(
            BetSelectionEligibility.canRegister(
                million,
                selection.copy(surprise = true, surpriseCloverQuantity = 2, surpriseQuantity = 0)
            )
        )
    }
}

private const val TIMEMANIA = 20
private const val DIA_DE_SORTE = 11
private const val SUPER_SEVEN = 7
private const val MAIS_MILIONARIA = 9
private const val SUPER_SEVEN_COLUMNS = 7
private const val INCOMPLETE_COLUMN_COUNT = 6
private const val INCOMPLETE_NUMBER_COUNT = 5
