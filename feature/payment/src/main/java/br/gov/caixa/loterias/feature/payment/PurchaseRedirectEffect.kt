package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.gov.caixa.loterias.core.presentation.NavigationResumeEffect
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect

/** Navigation may return before the outgoing composition has been disposed. */
@Composable
internal fun PurchaseRedirectResumeEffect(onResume: () -> Unit) {
    NavigationResumeEffect(onResume)
}

/** Only typed destinations may leave the feature; no response URL is sent to navigation. */
@Composable
internal fun PurchaseRedirectEffect(
    redirect: PurchaseRedirect?,
    consume: (PurchaseRedirect) -> Boolean,
    onRedirect: (PurchaseRedirect) -> Unit,
) {
    if (redirect == PurchaseRedirect.ACCEPT_TERMS) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = PURCHASE_UPDATED_TERMS_GUIDANCE,
            positiveLabel = "Entendi",
            onPositive = { if (consume(redirect)) onRedirect(redirect) },
        )
    } else {
        LaunchedEffect(redirect) {
            if (redirect != null && consume(redirect)) onRedirect(redirect)
        }
    }
}
