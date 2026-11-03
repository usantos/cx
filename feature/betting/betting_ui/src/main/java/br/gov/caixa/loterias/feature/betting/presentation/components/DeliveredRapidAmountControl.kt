package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
internal fun RapidAmountControl(
    label: String,
    description: String,
    value: BigDecimal,
    presets: List<BigDecimal>,
    onSelect: (BigDecimal) -> Unit,
    onCustom: () -> Unit,
    enabled: Boolean,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxWidth().padding(top = if (label.contains("mínimo")) SectionGap else LabelTopGap),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            color = LoteriasCaixaDSColorTokens.FavoriteCartCard,
            fontSize = SectionGapFontSize,
            fontFamily = LoteriasCaixaDSFontFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("bet.0136")
        )
        Text(
            buildAnnotatedString {
                val word = if (label.contains("mínimo")) {
                    uiStrings.getString(
                        DeliveryR.string.ui_minimo_5aeed3
                    )
                } else {
                    uiStrings.getString(DeliveryR.string.ui_maximo_e7013b)
                }
                val start = description.indexOf(word)
                append(description.substring(0, start))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(word) }
                append(description.substring(start + word.length))
            },
            (Modifier.padding(start = CounterHeight, end = CounterHeight, top = LabelTopGap)).uiId("bet.0137"),
            fontSize = if (label.contains("mínimo")) ContentInsetFontSize else CounterTextFontSize,
            fontFamily = LoteriasCaixaDSFontFamily,
            color = LoteriasCaixaDS.colors.graphite,
            textAlign = TextAlign.Center
        )
        Row(Modifier.padding(top = LabelTopGap), horizontalArrangement = Arrangement.Center) {
            presets.forEach { amount ->
                RapidAmountPreset(amount.toInt().toString(), value.compareTo(amount) == 0, true) { onSelect(amount) }
            }
            RapidAmountPreset("...", enabled && presets.none { it.compareTo(value) == 0 }, enabled, onCustom)
        }
        Text(
            uiStrings.getString(DeliveryR.string.ui_valor_selecionado_8db7ec),
            (Modifier.padding(top = LabelTopGap)).uiId("bet.0138"),
            fontSize = CounterTextFontSize,
            fontFamily = LoteriasCaixaDSFontFamily,
            color = LoteriasCaixaDS.colors.graphite
        )
        Text(
            formatBetAmount(value),
            (Modifier.padding(top = LabelTopGap)).uiId("bet.0139"),
            color = LoteriasCaixaDSColorTokens.BurntYellow,
            fontSize = CounterTextFontSize,
            fontFamily = LoteriasCaixaDSFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun RapidAmountPreset(label: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    RapidPreset(label, selected, enabled, true, onClick)
}

private val LabelTopGap = 10.dp
private val CounterTextFontSize = 15.sp
private val ContentInsetFontSize = 16.sp
private val SectionGap = 20.dp
private val SectionGapFontSize = 20.sp
private val CounterHeight = 50.dp
