package br.gov.caixa.loterias.feature.tickets.presentation.state

import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.contestType

data class BetDetailUiState(
    val bet: ConfirmedBet,
    val loading: Boolean = false,
    val detailsReady: Boolean = true,
    val serviceFailure: br.gov.caixa.loterias.core.domain.service.model.ServiceFailure? = null,
    val errorMessage: String? = null,
    val favoriteExpanded: Boolean = false,
    val favoriteName: String = "",
    val favoriteSaved: Boolean = false,
    val favoriteOverwriteConfirmation: Boolean = false,
    val favoriteOverwriteMessage: String? = null,
    val repeatContestOptions: List<RepeatContestOption> = emptyList(),
    val selectedRepeatContestIndex: Int = 0,
    val repeatConversion: RepeatContestOption? = null,
    val cartAdditionConfirmed: Boolean = false,
    val drawResults: List<br.gov.caixa.loterias.feature.results.domain.model.DrawResult> = emptyList(),
)

data class RepeatContestOption(
    val contestType: String,
    val label: String,
    val conversionMessage: String = "",
)

data class BetReceipt(val bytes: ByteArray, val fileName: String)
