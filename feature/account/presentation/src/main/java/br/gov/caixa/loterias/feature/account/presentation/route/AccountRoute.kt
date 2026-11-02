package br.gov.caixa.loterias.feature.account.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.screen.AccountTokenScreen
import br.gov.caixa.loterias.feature.account.presentation.state.AccountStatusEffect
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountStatusViewModel

import br.gov.caixa.loterias.feature.account.presentation.screen.SuccessScreen

@Composable
fun AccountRoute(viewModel: AccountStatusViewModel, onBack: () -> Unit, onComplete: () -> Unit, ssoAccountUrl: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AccountStatusEffect.Completed -> onComplete()
            }
        }
    }
    LaunchedEffect(viewModel) { viewModel.start() }
    when (viewModel.route) {
        "account/register-confirmation" -> SuccessScreen(registration = true)
        "account/personal-data-confirmation" -> SuccessScreen(registration = false)
        "account/change-login" -> ChangeLoginScreen(ssoAccountUrl, onBack)
        "account/token" -> AccountTokenScreen(modifier = Modifier.uiId("account.0008"))
        else -> error(uiStrings.getString(LocalizedR.string.ui_rota_de_conta_sem_layout_compose_1_s_19cdf9, viewModel.route))
    }
}
