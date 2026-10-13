package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog

@Composable
internal fun PurchaseNoticeDialog(
    notice: PurchaseNoticeUiState?,
    consume: (PurchaseNoticeUiState, Boolean) -> Boolean,
    onMoreBets: () -> Unit,
    onRegistration: (PurchaseNoticeUiState) -> Unit,
) {
    notice ?: return
    CaixaDSLegacyDialog(
        title = "Atenção",
        message = notice.message,
        positiveLabel = when { notice.offerRegistration -> "Sim"; notice.offerMoreBets -> "Apostar mais"; else -> "Entendi" },
        onPositive = {
            if (notice.offerRegistration) onRegistration(notice)
            else if (consume(notice, notice.offerMoreBets) && notice.offerMoreBets) onMoreBets()
        },
        negativeLabel = if (notice.offerRegistration) "Não" else "Entendi".takeIf { notice.offerMoreBets },
        onNegative = { consume(notice, false) },
    )
}
