package br.gov.caixa.loterias.feature.payment.presentation.mapper

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText

internal val legacyPaymentTextStyle = br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardTextStyle

internal fun legacyProcessingText(text: String) = buildAnnotatedString {
    val emphasis = when {
        "EFETIVADA" in text -> "EFETIVADA"
        "não garante" in text -> messageText("nao_garante_a1d33e")
        "Minhas Compras" in text -> "Minhas Compras"
        else -> null
    }
    if (emphasis == null) {
        append(text)
    } else {
        val start = text.indexOf(emphasis)
        append(text.substring(0, start))
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(emphasis) }
        append(text.substring(start + emphasis.length))
    }
}
