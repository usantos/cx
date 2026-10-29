package br.gov.caixa.loterias.feature.cart.domain

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.cart.domain.rule.VisitorCartPolicy
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class VisitorPriceContractTest {
    private val game = LotteryGameParameters(
        LotteryModality(9, "Mais Milionária", "MAISMILIONARIA"),
        1, "", 50, 6, 12, 10, emptyList(), listOf(BetPrice(6, 2, BigDecimal.TEN))
    )

    private val recurringDraft = BetDraft(9, numbers = listOf(1, 2, 3, 4, 5, 6), cloverQuantity = 3, recurringDraws = 2)
    private val unpricedDraft = BetDraft(9, numbers = listOf(1))

    @Test fun visitorUsesSharedNumericFallbackAndRecurringAmount() {
        assertEquals(BigDecimal("20"), VisitorCartPolicy.unitAmount(recurringDraft, game))
    }

    @Test fun visitorRejectsMissingPriceInsteadOfUsingPresentationZero() {
        assertThrows(IllegalArgumentException::class.java) {
            VisitorCartPolicy.unitAmount(unpricedDraft, game)
        }
    }
}
