package br.gov.caixa.loterias.feature.results.presentation.mapper

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import br.gov.caixa.loterias.feature.results.presentation.components.officialModalities

internal fun modalityName(code: String?): String =
    officialModalities.firstOrNull { it.code == code }?.name ?: "Mega-Sena"

internal fun contestButtonLabel(contest: Int?, fallback: String) = buildAnnotatedString {
    append("Concurso\n")
    if (contest == null) append(fallback) else {
        append("nº ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(contest.toString()) }
    }
}
