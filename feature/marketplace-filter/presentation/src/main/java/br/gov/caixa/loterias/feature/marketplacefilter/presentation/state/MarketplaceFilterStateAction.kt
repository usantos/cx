package br.gov.caixa.loterias.feature.marketplacefilter.presentation.state
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.allModalities
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi

internal sealed interface MarketplaceFilterStateAction {
    data class Cleared(val defaults: MarketplaceFilterUiState, val allModality: MarketplaceFilterModalityUi = allModalities) :
        MarketplaceFilterSelectionAction
    data class ModalitySelected(val modality: MarketplaceFilterModalityUi) : MarketplaceFilterSelectionAction
    data class RetailerSelected(
        val retailer: br.gov.caixa.loterias.feature.retail.domain.model.Retailer,
        val allModality: MarketplaceFilterModalityUi = allModalities,
    ) : MarketplaceFilterSelectionAction
    data class MinimumValueChanged(val value: String) : MarketplaceFilterInputsAction
    data class MaximumValueChanged(val value: String) : MarketplaceFilterInputsAction
    data class MinimumSharesChanged(val value: String, val messages: ShareValidationMessages = captureShareValidationMessages()) :
        MarketplaceFilterInputsAction
    data class MaximumSharesChanged(val value: String, val messages: ShareValidationMessages = captureShareValidationMessages()) :
        MarketplaceFilterInputsAction
    data class BetQuantitySelected(val quantity: Int) : MarketplaceFilterInputsAction
    data class NumberQuantitySelected(val quantity: Int?) : MarketplaceFilterInputsAction
    data class WantedNumbersChanged(val numbers: List<Int>) : MarketplaceFilterInputsAction
    data class UnwantedNumbersChanged(val numbers: List<Int>) : MarketplaceFilterInputsAction
    data class ParametersReceived(
        val betOptions: List<Int>,
        val modalities: List<MarketplaceFilterModalityUi>,
        val configurationError: String?,
        val allModality: MarketplaceFilterModalityUi = allModalities,
    ) : MarketplaceFilterLoadingAction
    data class LotteryChanged(val value: String, val lastIndividualModality: MarketplaceFilterModalityUi?) : MarketplaceFilterLoadingAction
    data class RetailersReceived(
        val query: String,
        val retailers: List<br.gov.caixa.loterias.feature.retail.domain.model.Retailer>,
        val emptyMessage: String = messageText("sua_busca_nao_retornou_resultado_d069d5"),
    ) : MarketplaceFilterLoadingAction
    data class RetailersFailed(val query: String) : MarketplaceFilterLoadingAction
    data class SharesOverflowed(val message: String, val messages: ShareValidationMessages = captureShareValidationMessages()) :
        MarketplaceFilterInputsAction
    data class ValidationFailed(val message: String?) : MarketplaceFilterInputsAction
}

internal data class ShareValidationMessages(
    val minimumTooSmall: String,
    val minimumTooLarge: String,
    val maximumTooSmall: String,
    val maximumTooLarge: String,
)

private fun captureShareValidationMessages() = ShareValidationMessages(
    messageText("quantidade_minima_nao_pode_ser_m_536325"),
    messageText("quantidade_minima_nao_pode_ser_m_d73775"),
    messageText("quantidade_maxima_nao_pode_ser_m_6f7be5"),
    messageText("quantidade_maxima_nao_pode_ser_m_1d14f1")
)
