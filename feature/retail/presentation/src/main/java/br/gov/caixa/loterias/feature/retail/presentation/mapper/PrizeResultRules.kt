package br.gov.caixa.loterias.feature.retail.presentation.mapper

import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

/** Somente uma fixture explicitamente controlada pode renderizar uma rota terminal sem um resgate. */
internal fun prizeScreenState(
    actual: PrizeRedemptionUiState,
    previewChannel: br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel?,
    allowControlledSuccessPreview: Boolean = false,
): PrizeRedemptionUiState = if (
    allowControlledSuccessPreview && previewChannel != null &&
    actual.stage != PrizeRedemptionStage.LOADING && actual.stage != PrizeRedemptionStage.SUCCESS
) {
    actual.copy(stage = PrizeRedemptionStage.SUCCESS, selectedChannel = previewChannel, loading = false)
} else {
    actual
}
