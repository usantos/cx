package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSPoolTutorialPrimary
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTutorialPreference
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialPrimaryLabel
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import kotlin.math.roundToInt

@Composable
internal fun PoolTutorialPrimary(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    finalPage: Boolean,
    leftIntroduction: Boolean,
    pagerHeight: androidx.compose.ui.unit.Dp,
    onPrimary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    tutorialPrimaryLabel(definition, state.page)?.let { label ->
        Layout(content = {
            val styled = buildAnnotatedString {
                if (finalPage) {
                    withStyle(SpanStyle(fontFamily = LoteriasCaixaDSFuturaFontFamily, fontWeight = FontWeight.Bold)) { append("Escolher") }
                    append(uiStrings.getString(DeliveryR.string.ui_meu_bolao_c690d7))
                } else if (!leftIntroduction) {
                    withStyle(SpanStyle(fontFamily = LoteriasCaixaDSFuturaFontFamily, fontWeight = FontWeight.Bold)) { append(label) }
                } else {
                    append(uiStrings.getString(DeliveryR.string.ui_vamos_comecar_b9007b))
                }
            }
            LoteriasCaixaDSPoolTutorialPrimary(
                styled,
                onPrimary,
                (Modifier.legacyAutomationId("pool-tutorial-primary")).uiId("bet.0130")
            )
            if (finalPage) {
                LoteriasCaixaDSTutorialPreference(
                    state.doNotShowAgain,
                    onToggleDoNotShowAgain,
                    (Modifier.legacyAutomationId("pool-tutorial-checkbox-row")).uiId("bet.0131"),
                    Modifier.legacyAutomationId("pool-tutorial-checkbox")
                )
            }
        }, modifier = Modifier.fillMaxSize()) { measurables, constraints ->
            val button = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
            val checkbox = measurables.getOrNull(1)?.measure(constraints.copy(minWidth = 0, minHeight = 0))
            val pagerPixels = pagerHeight.roundToPx()
            val centerTop = ((pagerPixels - 1.dp.roundToPx()) / 2f).roundToInt()
            // TutorialNovaModalidadeFragment define bottomMargin de BOLAO como 200 pixels brutos.
            val top = ((centerTop + pagerPixels - NATIVE_PRIMARY_BOTTOM_MARGIN_PIXELS - button.height) / 2f).roundToInt()
            layout(constraints.maxWidth, constraints.maxHeight) {
                button.placeRelative((constraints.maxWidth - button.width) / 2, top)
                checkbox?.placeRelative((constraints.maxWidth - checkbox.width) / 2, top + button.height + ContentGap.roundToPx())
            }
        }
    }
}

private val ContentGap = 8.dp

private const val NATIVE_PRIMARY_BOTTOM_MARGIN_PIXELS = 200
