package br.gov.caixa.loterias.feature.account.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import br.gov.caixa.loterias.feature.account.navigation.AccountEnvironmentDependencies

@Composable
fun AccountLoginEnvironmentSelector(
    enabled: Boolean,
    environment: AccountEnvironmentDependencies,
) {
    if (!environment.selectorVisible) return
    val activity = LocalContext.current.findActivity()
    LoginEnvironmentPicker(
        selected = environment.selectedEnvironment,
        enabled = enabled,
        onChange = environment.changeEnvironment::invoke,
        onClose = {
            activity?.finishAffinity()
            if (activity != null) android.os.Process.killProcess(android.os.Process.myPid())
        },
    )
}
