package br.gov.caixa.loterias.core.navigation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/** Somente continuações de navegação; credenciais e validação de concessões permanecem no provedor de sessão. */
class AppSessionState(
    pendingRestoredRouteState: MutableState<String?> = mutableStateOf(null),
    visitorLoginRequestState: MutableState<String?> = mutableStateOf(null),
    visitorReturnRouteState: MutableState<String?> = mutableStateOf(null),
    visitorReturnOwnerKeyState: MutableState<String?> = mutableStateOf(null),
    resumedHomeActionState: MutableState<String?> = mutableStateOf(null),
    accountCompletionRequiredState: MutableState<Boolean> = mutableStateOf(false),
    foregroundExpiryMarkerState: MutableState<String?> = mutableStateOf(null),
    prizeChannelHostState: MutableState<Boolean> = mutableStateOf(false),
) {
    var pendingRestoredRoute by pendingRestoredRouteState
    var visitorLoginRequest by visitorLoginRequestState
    var visitorReturnRoute by visitorReturnRouteState
    var visitorReturnOwnerKey by visitorReturnOwnerKeyState
    var resumedHomeAction by resumedHomeActionState
    var accountCompletionRequired by accountCompletionRequiredState
    var foregroundExpiryMarker by foregroundExpiryMarkerState
    var prizeChannelHost by prizeChannelHostState

    /** Resolve a continuação antes de reconstruir a pilha com a conta autenticada. */
    fun authenticated(destination: String, owner: String?, details: LoggedDetailContext): String {
        details.reconcile(owner)
        if (destination == "home" && pendingRestoredRoute != null) {
            val restored = pendingRestoredRoute
            pendingRestoredRoute = null
            visitorReturnRoute = if (details.restorationOutcome == DetailRestorationOutcome.Restored) restored else null
            visitorReturnOwnerKey = owner
        }
        visitorReturnRoute = authenticatedContinuation(visitorReturnRoute, visitorReturnOwnerKey, owner)
        accountCompletionRequired = destination != "home"
        val requested = visitorReturnRoute
        if (destination == "home") {
            visitorReturnRoute = null
            visitorReturnOwnerKey = null
        }
        val target = if (destination == "home" && requested != null && !requested.startsWith("home/")) requested else destination
        if (destination == "home" && requested?.startsWith("home/") == true) resumedHomeAction = requested
        return target
    }

    fun clearForVisitor(details: LoggedDetailContext) {
        details.clear()
        pendingRestoredRoute = null
        visitorReturnRoute = null
        visitorReturnOwnerKey = null
        resumedHomeAction = null
        accountCompletionRequired = false
    }
}

@Composable
fun rememberAppSessionState(): AppSessionState = AppSessionState(
    pendingRestoredRouteState = remember { mutableStateOf(null) },
    visitorLoginRequestState = remember { mutableStateOf(null) },
    visitorReturnRouteState = remember { mutableStateOf(null) },
    visitorReturnOwnerKeyState = remember { mutableStateOf(null) },
    resumedHomeActionState = remember { mutableStateOf(null) },
    accountCompletionRequiredState = remember { mutableStateOf(false) },
    foregroundExpiryMarkerState = remember { mutableStateOf(null) },
    prizeChannelHostState = remember { mutableStateOf(false) }
)
