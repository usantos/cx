package br.gov.caixa.loterias.feature.payment.presentation.components

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.effect.NavigationResumeEffect
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_UPDATED_TERMS
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.state.MercadoPagoRegistrationStage
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.MercadoPagoRegistrationViewModel

@Composable
fun MercadoPagoRegistrationOverlay(
    viewModel: MercadoPagoRegistrationViewModel,
    simulateBrowser: Boolean,
    onRedirect: (ServiceRedirect) -> Unit,
    onFinished: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    NavigationResumeEffect { if (viewModel.navigationReturned()) onFinished() }
    LaunchedEffect(state.stage) {
        if (viewModel.finishWithoutAction()) onFinished()
    }
    if (state.redirect == ServiceRedirect.ACCEPT_TERMS) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = LEGACY_UPDATED_TERMS,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { if (viewModel.consumeRedirect(ServiceRedirect.ACCEPT_TERMS)) onRedirect(ServiceRedirect.ACCEPT_TERMS) }
        )
    } else {
        LaunchedEffect(state.redirect) {
            state.redirect?.let { if (viewModel.consumeRedirect(it)) onRedirect(it) }
        }
    }
    state.notice?.let { notice ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = notice.positiveLabel,
            negativeLabel = notice.negativeLabel,
            onPositive = {
                if (notice.destination != null) {
                    viewModel.consumeNoticeAction(notice)?.let(onRedirect)
                } else if (viewModel.dismissNotice(notice)) {
                    onFinished()
                }
            },
            onNegative = { if (viewModel.dismissNotice(notice)) onFinished() }
        )
    }
    state.repeatConsent?.let { consent ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = consent.message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = { viewModel.repeatRegistration(consent) },
            onNegative = { if (viewModel.declineRepeatRegistration(consent)) onFinished() }
        )
    }
    MercadoPagoBrowser(viewModel, state, simulateBrowser, onFinished)
    when (state.stage) {
        MercadoPagoRegistrationStage.LOADING -> CaixaDSAuthenticationLoadingDialog()

        MercadoPagoRegistrationStage.ERROR -> CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = state.errorMessage.orEmpty(),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { if (viewModel.dismissError()) onFinished() }
        )

        MercadoPagoRegistrationStage.OPENED -> if (simulateBrowser) {
            CaixaDSDialog(
                title = uiStrings.getString(LocalizedR.string.ui_simulacao_5d2570),
                message = uiStrings.getString(LocalizedR.string.ui_abertura_do_cadastro_mercado_pago_si_164ef3),
                positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
                onPositive = { if (viewModel.browserReturned()) onFinished() }
            )
        }

        else -> Unit
    }
}

@Composable
private fun MercadoPagoBrowser(
    viewModel: MercadoPagoRegistrationViewModel,
    state: br.gov.caixa.loterias.feature.payment.presentation.state.MercadoPagoRegistrationUiState,
    simulateBrowser: Boolean,
    onFinished: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (viewModel.browserReturned()) onFinished()
    }
    LaunchedEffect(state.request) {
        val request = state.request ?: return@LaunchedEffect
        val url = viewModel.takeBrowserRequest(request) ?: return@LaunchedEffect
        if (!simulateBrowser) {
            try {
                launcher.launch(Intent(Intent.ACTION_VIEW, url.toUri()).addCategory(Intent.CATEGORY_BROWSABLE))
            } catch (_: ActivityNotFoundException) {
                viewModel.browserUnavailable()
            } catch (_: SecurityException) {
                viewModel.browserUnavailable()
            }
        }
    }
}
