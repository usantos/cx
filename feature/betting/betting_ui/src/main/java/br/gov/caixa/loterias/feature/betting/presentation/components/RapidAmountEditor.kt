package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSRapidAmountDialog
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
fun RapidAmountEditDialog(
    minimum: Boolean,
    input: String,
    floor: BigDecimal?,
    ceiling: BigDecimal?,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSRapidAmountDialog(
        title = if (minimum) {
            uiStrings.getString(
                DeliveryR.string.ui_valor_minimo_da_aposta_bcf5d5
            )
        } else {
            uiStrings.getString(DeliveryR.string.ui_valor_maximo_da_aposta_349559)
        },
        value = input,
        hint = if (minimum) {
            uiStrings.getString(
                DeliveryR.string.ui_digite_o_valor_minimo_25e16d
            )
        } else {
            uiStrings.getString(DeliveryR.string.ui_digite_o_valor_maximo_e1efe1)
        },
        limits = if (minimum) {
            uiStrings.getString(
                DeliveryR.string.ui_minimo_de_1_s_e_maximo_de_2_s_6f07c8,
                formatBetAmount(floor!!),
                formatBetAmount(ceiling!!)
            )
        } else {
            uiStrings.getString(DeliveryR.string.ui_o_limite_e_de_1_s_82167e, formatBetAmount(ceiling!!))
        },
        errorMessage = null,
        onValueChange = onValueChange,
        onSave = onSave,
        onDismiss = onDismiss
    )
}

@Composable
fun RapidAmountInputFeedback(message: String?, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    message?.let { message ->
        br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            onDismiss
        )
    }
}
