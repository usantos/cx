package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.presentation.formatting.localizedMessage as messageText

fun MarketplacePool.prognosticLabel(): AnnotatedString = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp))
    append(betQuantity.toString())
    pop()
    append(if (betQuantity == 1) messageText("aposta_de_9a14cf") else messageText("apostas_de_11b8ee"))
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp))
    append(numberQuantity.toString())
    pop()
    append(if (modality.contains("LOTECA", true)) " palpites" else " dezenas")
}

fun MarketplacePool.shareAvailability(): AnnotatedString = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
    append(availableShares.toString())
    pop()
    append(if (availableShares == 1) messageText("cota_disponivel_de_fae690") else messageText("cotas_disponiveis_de_010d84"))
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
    append(totalShares.toString())
    pop()
}

internal fun MarketplacePool.estimatedPrizeValue() = estimatedPrize.toBigDecimalOrNull()
    ?: estimatedPrize.replace(".", "").replace(',', '.').toBigDecimalOrNull()
