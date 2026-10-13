package br.gov.caixa.loterias.feature.onboarding

import br.gov.caixa.loterias.core.common.ConnectionAvailability

enum class StartupStatusKind(val message: String, val actionLabel: String, val detailMessage: String? = null) {
    UNAVAILABLE(
        "O aplicativo está\ntemporariamente indisponível",
        "Tentar novamente",
        "Pedimos desculpas pelo transtorno.\nTente novamente em breve",
    ),
    OFFLINE(
        "Parece que sua internet\nestá com alguma instabilidade",
        "Tentar novamente",
        "Verifique sua conexão\ne tente novamente",
    ),
    UPDATE_REQUIRED("O seu aplicativo está desatualizado. Para continuar será necessário efetuar a atualização.", "Atualizar"),
}

fun startupStatusVersion(versionName: String): String = "v$versionName"

fun startupStatusForConnection(kind: StartupStatusKind, availability: ConnectionAvailability): StartupStatusKind =
    if (kind == StartupStatusKind.UPDATE_REQUIRED) kind else when (availability) {
        ConnectionAvailability.CONNECTED -> StartupStatusKind.UNAVAILABLE
        ConnectionAvailability.DISCONNECTED -> StartupStatusKind.OFFLINE
        ConnectionAvailability.UNKNOWN -> kind
    }
