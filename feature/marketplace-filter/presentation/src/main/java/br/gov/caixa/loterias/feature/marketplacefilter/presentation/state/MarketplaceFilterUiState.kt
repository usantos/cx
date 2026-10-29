package br.gov.caixa.loterias.feature.marketplacefilter.presentation.state

import br.gov.caixa.loterias.feature.marketplacefilter.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

data class MarketplaceFilterModalityUi(
    val id: Int?,
    val name: String,
    val baseName: String = name,
    val contestType: Int?,
    val numberOptions: List<Int>,
    val maximumNumber: Int,
) {
    val isAll: Boolean get() = id == null

    /** `ModalidadeEnum.getDescricaoEspecialDuasLinhas`, apenas para apresentação do cartão. */
    val cardLabel: String
        get() = if (contestType != 2) {
            name
        } else {
            when (name.lowercase(java.util.Locale.ROOT)) {
                "quina de são joão" -> messageText("quina_de_sao_joao_4893ab")
                "lotofácil da independência" -> messageText("lotofacil_da_independencia_2d7ac9")
                "mega da virada" -> messageText("mega_da_virada_2ccce6")
                "dupla de páscoa" -> messageText("dupla_de_pascoa_2b9126")
                "loteca especial" -> "loteca\nespecial"
                else -> name
            }
        }
    val hidesWantedNumbers: Boolean
        get() = id == 19 || id == 7 || baseName.equals("Loteca", ignoreCase = true) ||
            baseName.equals("Super Sete", ignoreCase = true)
}

data class MarketplaceFilterUiState(
    val modalities: List<MarketplaceFilterModalityUi> = emptyList(),
    val selectedModality: MarketplaceFilterModalityUi? = null,
    val lottery: String = "",
    val selectedRetailer: Retailer? = null,
    val restoredRetailerId: String = "",
    val allModalitiesHighlighted: Boolean = false,
    val cachedRetailers: List<Retailer> = emptyList(),
    val retailerSuggestions: List<Retailer> = emptyList(),
    val retailerLoading: Boolean = false,
    val retailerMessage: String? = null,
    val minimumValue: String = "",
    val maximumValue: String = "",
    val minimumShares: String = "",
    val maximumShares: String = "",
    val betOptions: List<Int> = emptyList(),
    val selectedBetQuantity: Int? = null,
    val numberOptions: List<Int> = emptyList(),
    val selectedNumberQuantity: Int? = null,
    val wantedNumbers: List<Int> = emptyList(),
    val unwantedNumbers: List<Int> = emptyList(),
    val lotteryEnabled: Boolean = true,
    val configurationError: String? = null,
    val minimumSharesError: String? = null,
    val maximumSharesError: String? = null,
    val validationMessage: String? = null,
) {
    val canApply: Boolean
        get() = configurationError == null && minimumSharesError == null && maximumSharesError == null
}

sealed interface MarketplaceFilterAction {
    data class SelectModality(val value: MarketplaceFilterModalityUi) : MarketplaceFilterAction
    data class ChangeLottery(val value: String) : MarketplaceFilterAction
    data class SelectRetailer(val value: Retailer) : MarketplaceFilterAction
    data class ChangeMinimumValue(val value: String) : MarketplaceFilterAction
    data class ChangeMaximumValue(val value: String) : MarketplaceFilterAction
    data class ChangeMinimumShares(val value: String) : MarketplaceFilterAction
    data class ChangeMaximumShares(val value: String) : MarketplaceFilterAction
    data class SelectBetQuantity(val value: Int) : MarketplaceFilterAction
    data class SelectNumberQuantity(val value: Int?) : MarketplaceFilterAction
    data class ChangeWantedNumbers(val value: List<Int>) : MarketplaceFilterAction
    data class ChangeUnwantedNumbers(val value: List<Int>) : MarketplaceFilterAction
    data object Clear : MarketplaceFilterAction
    data object Apply : MarketplaceFilterAction
    data object Back : MarketplaceFilterAction
}
