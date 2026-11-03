package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TeimosinhaNotice
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun BetDetailTeimosinhaDialog(notice: TeimosinhaNotice?, onConfirm: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSAlertDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = buildAnnotatedString {
            if (notice == TeimosinhaNotice.ORIGINAL_PAID) {
                append(uiStrings.getString(DeliveryR.string.ui_esta_aposta_com_teimosinha_nao_e_mai_8ffde1))
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(
                        uiStrings.getString(DeliveryR.string.ui_aposta_de_troca_f0835f)
                    )
                }
                append(uiStrings.getString(DeliveryR.string.ui_gerada_apos_o_resgate_do_premio_para_ceb8bd))
            } else {
                append(uiStrings.getString(DeliveryR.string.ui_a_conferencia_dos_jogos_da_teimosinh_86f58e))
            }
        },
        confirmText = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
        onConfirm = onConfirm,
        onDismiss = { }
    )
}
