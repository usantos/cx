package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.legacyPromptInset
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

// GridView centraliza por divisão inteira; o alinhamento por viés do Compose arredonda restos ímpares.
internal val LegacyGridHorizontalAlignment = object : Alignment.Horizontal {
    override fun align(size: Int, space: Int, layoutDirection: androidx.compose.ui.unit.LayoutDirection): Int = (space - size) / 2
}

/**
 * LinearLayout atribui a cada peso sucessivo sua parcela dos pixels restantes. Row.weight distribui os pixels
 * restantes a partir da primeira coluna, deslocando as bolas.
 */
@Composable
internal fun NativeSuperSevenRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier.fillMaxWidth(), content = content) { measurables, constraints ->
        val margins = 2 * 1.dp.roundToPx()
        var remaining = constraints.maxWidth - margins * measurables.size
        val children = measurables.mapIndexed { index, measurable ->
            val share = remaining / (measurables.size - index)
            remaining -= share
            val width = share + margins
            measurable.measure(constraints.copy(minWidth = width, maxWidth = width, minHeight = 0))
        }
        layout(constraints.maxWidth, children.maxOfOrNull { it.height } ?: 0) {
            var left = 0
            children.forEach { child ->
                child.placeRelative(left, 0)
                left += child.width
            }
        }
    }
}

@Composable
internal fun SuperSevenMatrix(state: BettingGameUiState, actions: BettingGameActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val promptInset = legacyPromptInset()
    val nativeBallSize = with(density) { (NumberBallSize.toPx().toInt()).toDp() }
    val metrics = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    Column(Modifier.fillMaxSize()) {
        val selectedCount = state.superSevenColumns.sumOf(Set<Int>::size)
        Text(
            if (selectedCount == 0) {
                uiStrings.getString(LocalizedR.string.ui_selecione_os_numeros_44f9f7)
            } else {
                uiStrings.getString(
                    LocalizedR.string.ui_selecionados_1_s_2_s_0a48ce,
                    selectedCount.toString(),
                    state.numberQuantity.toString()
                )
            },
            (Modifier.padding(start = promptInset, top = ContentInset, bottom = ContentGap)).uiId("bet.0271"),
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = ContentInsetFontSize,
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            style = metrics
        )
        SuperSevenColumnHeaders(metrics)
        // Cabeçalhos nativos ficam acima de NestedScrollView. A margem de cltGeral rola
        // com os números; a orientação da atividade e os sete cabeçalhos permanecem fixos.
        Column(Modifier.weight(1f).fillMaxWidth().uiId("bet.0273").verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(ContentInset))
            SuperSevenNumberColumns(state, actions, nativeBallSize, metrics)
        }
    }
}

private val ContentInset = 16.dp
private val ContentInsetFontSize = 16.sp
private val NumberBallSize = 44.dp
private val ContentGap = 8.dp
