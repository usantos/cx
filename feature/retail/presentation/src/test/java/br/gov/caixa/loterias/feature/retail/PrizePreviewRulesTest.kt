package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeScreenState

class PrizePreviewRulesTest {
    @Test fun `navigation to a confirmation alias never fabricates successful redemption by default`() {
        val actual = PrizeRedemptionUiState(stage = PrizeRedemptionStage.OPTIONS, loading = false)
        assertSame(actual, prizeScreenState(actual, PrizeRedemptionChannel.PIX))
        assertSame(actual, prizeScreenState(actual, PrizeRedemptionChannel.MERCADO_PAGO))
    }

    @Test fun `only an explicitly controlled fixture may preview a success screen`() {
        val actual = PrizeRedemptionUiState(stage = PrizeRedemptionStage.OPTIONS, loading = false)
        val preview = prizeScreenState(actual, PrizeRedemptionChannel.PIX, allowControlledSuccessPreview = true)
        assertEquals(PrizeRedemptionStage.SUCCESS, preview.stage)
        assertEquals(PrizeRedemptionChannel.PIX, preview.selectedChannel)
        assertEquals(PrizeRedemptionStage.OPTIONS, actual.stage)
    }

    @Test fun `a controlled preview cannot override a loading or real completed state`() {
        val loading = PrizeRedemptionUiState()
        assertSame(loading, prizeScreenState(loading, PrizeRedemptionChannel.PIX, true))
        val actual = PrizeRedemptionUiState(stage = PrizeRedemptionStage.SUCCESS,
            selectedChannel = PrizeRedemptionChannel.MERCADO_PAGO, loading = false)
        assertSame(actual, prizeScreenState(actual, PrizeRedemptionChannel.PIX, true))
        assertSame(actual, prizeScreenState(actual, null, true))
    }
}
