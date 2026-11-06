package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily

/** Texto e funções de retorno fornecidos pelo fluxo responsável; sem dependência de pagamento ou domínio. */
data class LoteriasCaixaDSPixPanelText(
    val intro: String,
    val title: String,
    val singlePayment: String,
    val timeIntro: String,
    val timeOutro: String,
    val expired: String,
    val checkPayment: String,
    val purchases: String,
    val attention: String,
    val warning: String = "",
)

/** Resolve os textos padrão do painel com os recursos do contexto Compose atual. */
@Composable
fun loteriasCaixaDSPixPanelText(warning: String = ""): LoteriasCaixaDSPixPanelText = LoteriasCaixaDSPixPanelText(
    intro = stringResource(LocalizedR.string.format_pix_intro),
    title = stringResource(LocalizedR.string.format_pix_title),
    singlePayment = stringResource(LocalizedR.string.format_pix_single_payment),
    timeIntro = stringResource(LocalizedR.string.format_pix_time_intro),
    timeOutro = stringResource(LocalizedR.string.format_pix_time_outro),
    expired = stringResource(LocalizedR.string.format_pix_expired),
    checkPayment = stringResource(LocalizedR.string.format_pix_check_payment),
    purchases = stringResource(LocalizedR.string.format_pix_purchases),
    attention = stringResource(LocalizedR.string.format_pix_attention),
    warning = warning
)

internal val pixPanelStyle = TextStyle(
    fontFamily = LoteriasCaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)
