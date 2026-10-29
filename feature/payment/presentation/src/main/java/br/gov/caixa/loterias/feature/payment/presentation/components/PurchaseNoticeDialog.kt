package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState

@Composable
internal fun PurchaseNoticeDialog(
    notice: PurchaseNoticeUiState?,
    consume: (PurchaseNoticeUiState, Boolean) -> Boolean,
    onMoreBets: () -> Unit,
    onRegistration: (PurchaseNoticeUiState) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    notice ?: return
    CaixaDSDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = notice.message,
        positiveLabel = when {
            notice.offerRegistration -> uiStrings.getString(
                LocalizedR.string.ui_sim_931345
            )

            notice.offerMoreBets -> uiStrings.getString(
                LocalizedR.string.ui_apostar_mais_a6282e
            )

            else -> uiStrings.getString(LocalizedR.string.ui_entendi_9f9373)
        },
        onPositive = {
            if (notice.offerRegistration) {
                onRegistration(notice)
            } else if (consume(notice, notice.offerMoreBets) && notice.offerMoreBets) {
                onMoreBets()
            }
        },
        negativeLabel = if (notice.offerRegistration) {
            uiStrings.getString(LocalizedR.string.ui_nao_9bc947)
        } else {
            uiStrings.getString(
                LocalizedR.string.ui_entendi_9f9373
            ).takeIf { notice.offerMoreBets }
        },
        onNegative = { consume(notice, false) }
    )
}
