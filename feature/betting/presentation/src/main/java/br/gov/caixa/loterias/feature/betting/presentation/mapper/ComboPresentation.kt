package br.gov.caixa.loterias.feature.betting.presentation.mapper

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryKey
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetComboEntry
import java.util.Locale

internal fun comboText(size: Int, weight: FontWeight = FontWeight.Normal, family: FontFamily = CaixaDSFontFamily) = TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = size.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

internal fun BetCombo.specialEntry(): BetComboEntry? = entries.firstOrNull()?.takeIf { id == 1 }
internal fun BetCombo.catalogName(): String = specialEntry()?.modality?.specialName?.takeIf(String::isNotBlank) ?: name
internal fun BetCombo.detailName(): String = specialEntry()?.modality?.specialName?.takeIf(String::isNotBlank)
    ?.let { "$name $it" } ?: name

internal fun comboEntryName(entry: BetComboEntry): AnnotatedString {
    val modality = entry.modality
    val normal = modality.managerName.ifBlank { modality.name }
    val special = entry.contestType.equals("ESPECIAL", true) || entry.contestType.equals("E", true)
    if (special) return AnnotatedString(modality.specialName.ifBlank { normal }.lowercase(Locale.forLanguageTag("pt-BR")))
    return if (caixaDSLotteryKey(modality.id, modality.code) == "LOTOMANIA") {
        buildAnnotatedString {
            append(normal)
            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) { append(" + espelho") }
        }
    } else {
        AnnotatedString(normal.lowercase(Locale.forLanguageTag("pt-BR")))
    }
}

internal object ComboTextSize {
    const val CAPTION = 12
    const val BODY = 13
    const val LABEL = 14
    const val HEADING = 16
    const val QUANTITY = 17
    const val AMOUNT = 18
    const val CATALOG_LINE_HEIGHT = 19
}
