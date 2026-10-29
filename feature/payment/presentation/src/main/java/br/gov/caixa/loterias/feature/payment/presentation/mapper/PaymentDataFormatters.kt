package br.gov.caixa.loterias.feature.payment.presentation.mapper

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

internal fun paymentTopBarTitle(): AnnotatedString = buildAnnotatedString {
    append(messageText("forma_de_146f5e"))
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("pagamento") }
}

internal fun secondsUntil(value: String, now: Instant = Instant.now()): Long = runCatching {
    val instant = runCatching { OffsetDateTime.parse(value).toInstant() }.getOrElse {
        LocalDateTime.parse(value).atZone(ZoneId.of("America/Sao_Paulo")).toInstant()
    }
    (instant.epochSecond - now.epochSecond).coerceAtLeast(0)
}.getOrDefault(0)
