package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalView
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

internal class LoginBiometricAuthorization(val promptHost: Activity?, val launch: () -> Unit)

@Composable
internal fun rememberLoginBiometricAuthorization(
    viewModel: LoginViewModel,
    authentication: AuthenticationGateway,
    biometricPrompt: (Activity, Intent, (Intent?) -> Unit) -> (() -> Unit),
): LoginBiometricAuthorization {
    var biometricChallenge by rememberSaveable { mutableStateOf<android.content.Intent?>(null) }
    val credentialLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val challenge = biometricChallenge
        biometricChallenge = null
        if (result.resultCode == Activity.RESULT_OK && challenge != null) viewModel.completeBiometric {
                        checkNotNull(authentication as? br.gov.caixa.loterias.core.auth.gateway.BiometricAuthenticationGateway)
                            .completeBiometricAuthorization(challenge)
                    }
        else {
            viewModel.biometricAuthorizationFailed()
        }
    }
    val promptHost = LocalView.current.context.findActivity()
    DisposableEffect(biometricChallenge, promptHost) {
        val challenge = biometricChallenge
        val cancelPrompt = if (android.os.Build.VERSION.SDK_INT >= 29 && challenge != null && promptHost != null) {
            try {
                biometricPrompt(promptHost, challenge) { result ->
                    biometricChallenge = null
                    if (result != null) viewModel.completeBiometric {
                        checkNotNull(authentication as? br.gov.caixa.loterias.core.auth.gateway.BiometricAuthenticationGateway)
                            .completeBiometricAuthorization(result)
                    }
                    else {
                        viewModel.biometricAuthorizationFailed()
                    }
                }
            } catch (_: Exception) {
                biometricChallenge = null
                viewModel.biometricAuthorizationFailed()
                null
            }
        } else null
        onDispose { cancelPrompt?.invoke() }
    }
    return LoginBiometricAuthorization(promptHost) {
        checkNotNull(promptHost)
        val challenge = (authentication as br.gov.caixa.loterias.core.auth.gateway.BiometricAuthenticationGateway)
            .createBiometricAuthorizationIntent()
        biometricChallenge = challenge
        if (android.os.Build.VERSION.SDK_INT < 29) credentialLauncher.launch(challenge)
    }
}

internal tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
