package br.gov.caixa.loterias.feature.onboarding.presentation.screen

import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.feature.onboarding.R

enum class StartupStatusKind(
    @androidx.annotation.StringRes val message: Int,
    @androidx.annotation.StringRes val actionLabel: Int,
    @androidx.annotation.StringRes val detailMessage: Int? = null,
    val errorCode: String? = null,
) {
    UNAVAILABLE(R.string.startup_unavailable_message, R.string.startup_retry, R.string.startup_unavailable_detail, "LTR-503"),
    OFFLINE(R.string.startup_offline_message, R.string.startup_retry, R.string.startup_offline_detail, "LTR-001"),
    UPDATE_REQUIRED(R.string.startup_update_message, R.string.startup_update),
}

fun startupStatusForConnection(kind: StartupStatusKind, availability: ConnectionAvailability): StartupStatusKind =
    if (kind == StartupStatusKind.UPDATE_REQUIRED) kind else when (availability) {
        ConnectionAvailability.CONNECTED -> StartupStatusKind.UNAVAILABLE
        ConnectionAvailability.DISCONNECTED -> StartupStatusKind.OFFLINE
        ConnectionAvailability.UNKNOWN -> kind
    }
