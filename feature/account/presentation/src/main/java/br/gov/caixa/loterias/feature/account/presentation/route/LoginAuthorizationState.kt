package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/** Estado local da integração; os tíquetes e a sessão continuam pertencendo ao ViewModel. */
internal class LoginAuthorizationState(
    offline: MutableState<Boolean>,
    browserPending: MutableState<Boolean>,
    retryPending: MutableState<Boolean>,
    switchingUser: MutableState<Boolean>,
    preferBrowserOnRetry: MutableState<Boolean>,
    cancelActivityReturn: MutableState<(() -> Unit)?>,
) {
    var offline by offline
    var browserPending by browserPending
    var retryPending by retryPending
    var switchingUser by switchingUser
    var preferBrowserOnRetry by preferBrowserOnRetry
    var cancelActivityReturn by cancelActivityReturn
}

@Composable
internal fun rememberLoginAuthorizationState(activity: Activity?): LoginAuthorizationState {
    val offline = rememberSaveable { mutableStateOf(false) }
    val browserPending = rememberSaveable { mutableStateOf(false) }
    val retryPending = rememberSaveable { mutableStateOf(false) }
    val switchingUser = rememberSaveable { mutableStateOf(false) }
    val preferBrowserOnRetry = rememberSaveable { mutableStateOf(false) }
    val cancelActivityReturn = remember(activity) { mutableStateOf<(() -> Unit)?>(null) }
    DisposableEffect(activity) {
        onDispose { cancelActivityReturn.value?.invoke() }
    }
    return LoginAuthorizationState(offline, browserPending, retryPending, switchingUser, preferBrowserOnRetry, cancelActivityReturn)
}
