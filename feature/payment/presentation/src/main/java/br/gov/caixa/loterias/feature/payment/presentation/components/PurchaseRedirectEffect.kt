package br.gov.caixa.loterias.feature.payment.presentation.components

import br.gov.caixa.loterias.feature.payment.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.gov.caixa.loterias.core.presentation.effect.NavigationResumeEffect
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_UPDATED_TERMS_GUIDANCE

/** A navegação pode retornar antes que a composição de saída seja descartada. */
@Composable
internal fun PurchaseRedirectResumeEffect(onResume: () -> Unit) {
    NavigationResumeEffect(onResume)
}

/** Somente destinos tipados podem sair da funcionalidade; nenhuma URL da resposta é enviada à navegação. */
@Composable
internal fun PurchaseRedirectEffect(
    redirect: PurchaseRedirect?,
    consume: (PurchaseRedirect) -> Boolean,
    onRedirect: (PurchaseRedirect) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (redirect == PurchaseRedirect.ACCEPT_TERMS) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = PURCHASE_UPDATED_TERMS_GUIDANCE,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { if (consume(redirect)) onRedirect(redirect) }
        )
    } else {
        LaunchedEffect(redirect) {
            if (redirect != null && consume(redirect)) onRedirect(redirect)
        }
    }
}
