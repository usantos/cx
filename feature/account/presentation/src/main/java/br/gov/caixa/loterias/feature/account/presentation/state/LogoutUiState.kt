package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import kotlinx.coroutines.launch

enum class LogoutNotice { SERVER_FAILURE, LOCAL_FAILURE, LOCAL_DATA_FAILURE, CANCELLED, UNCONFIRMED, SIMULATED }

data class LogoutUiState(
    val launch: EndSessionLaunch? = null,
    val waitingForBrowser: Boolean = false,
    val notice: LogoutNotice? = null,
    val clearingLocalData: Boolean = false,
    val signingOut: Boolean = false,
    val localSessionEnded: Boolean = false,
    val serverFailure: ServiceFailure? = null,
)
