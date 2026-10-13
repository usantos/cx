package br.gov.caixa.loterias.feature.payment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.LEGACY_UPDATED_TERMS
import br.gov.caixa.loterias.core.presentation.NavigationResumeEffect

@Composable
fun MercadoPagoRegistrationOverlay(
    viewModel: MercadoPagoRegistrationViewModel,
    simulateBrowser: Boolean,
    onRedirect: (ServiceRedirect) -> Unit,
    onFinished: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NavigationResumeEffect { if (viewModel.navigationReturned()) onFinished() }
    LaunchedEffect(state.stage) {
        if (viewModel.finishWithoutAction()) onFinished()
    }
    if (state.redirect == ServiceRedirect.ACCEPT_TERMS) {
        CaixaDSLegacyDialog(title = "Atenção", message = LEGACY_UPDATED_TERMS, positiveLabel = "Entendi",
            onPositive = { if (viewModel.consumeRedirect(ServiceRedirect.ACCEPT_TERMS)) onRedirect(ServiceRedirect.ACCEPT_TERMS) })
    } else {
        LaunchedEffect(state.redirect) {
            state.redirect?.let { if (viewModel.consumeRedirect(it)) onRedirect(it) }
        }
    }
    state.notice?.let { notice ->
        CaixaDSLegacyDialog(title = "Atenção", message = notice.message,
            positiveLabel = notice.positiveLabel, negativeLabel = notice.negativeLabel,
            onPositive = {
                if (notice.destination != null) viewModel.consumeNoticeAction(notice)?.let(onRedirect)
                else if (viewModel.dismissNotice(notice)) onFinished()
            }, onNegative = { if (viewModel.dismissNotice(notice)) onFinished() })
    }
    state.repeatConsent?.let { consent ->
        CaixaDSLegacyDialog(title = "Atenção", message = consent.message, positiveLabel = "Sim", negativeLabel = "Não",
            onPositive = { viewModel.repeatRegistration(consent) },
            onNegative = { if (viewModel.declineRepeatRegistration(consent)) onFinished() })
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (viewModel.browserReturned()) onFinished()
    }
    LaunchedEffect(state.request) {
        val request = state.request ?: return@LaunchedEffect
        val url = viewModel.takeBrowserRequest(request) ?: return@LaunchedEffect
        if (!simulateBrowser) {
            try {
                launcher.launch(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addCategory(Intent.CATEGORY_BROWSABLE))
            } catch (_: ActivityNotFoundException) {
                viewModel.browserUnavailable()
            } catch (_: SecurityException) {
                viewModel.browserUnavailable()
            }
        }
    }
    when (state.stage) {
        MercadoPagoRegistrationStage.LOADING -> CaixaDSAuthenticationLoadingDialog()
        MercadoPagoRegistrationStage.ERROR -> CaixaDSLegacyDialog(
            title = "Atenção", message = state.errorMessage.orEmpty(), positiveLabel = "Entendi",
            onPositive = { if (viewModel.dismissError()) onFinished() },
        )
        MercadoPagoRegistrationStage.OPENED -> if (simulateBrowser) CaixaDSLegacyDialog(
            title = "Simulação", message = "Abertura do cadastro Mercado Pago simulada. Nenhum navegador ou cadastro real foi iniciado.",
            positiveLabel = "Entendi", onPositive = { if (viewModel.browserReturned()) onFinished() },
        )
        else -> Unit
    }
}
