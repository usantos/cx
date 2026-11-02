package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialSecondaryLabel
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition

@Composable
internal fun BoxScope.PoolTutorialTopActions(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    finalPage: Boolean,
    regular: TextStyle,
    onClose: () -> Unit,
    onSecondary: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (!finalPage) {
        Box(
            Modifier.align(Alignment.TopStart).size(NavigationActionWidth, TouchTargetHeight)
                .legacyAutomationId("pool-tutorial-close").uiId("bet.0126").clickable(role = Role.Button, onClick = onClose),
            Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
                style = regular,
                fontSize = ControlTextFontSize,
                color = LoteriasCaixaDSColorTokens.TutorialGray,
                modifier = Modifier.uiId("bet.0127")
            )
        }
    }
    Box(
        Modifier.align(Alignment.TopEnd).size(NavigationActionWidth, TouchTargetHeight)
            .legacyAutomationId("pool-tutorial-skip").uiId("bet.0128").clickable(role = Role.Button, onClick = onSecondary),
        Alignment.Center
    ) {
        Text(
            tutorialSecondaryLabel(definition, state.page),
            style = regular,
            fontSize = ControlTextFontSize,
            color = LoteriasCaixaDSColorTokens.TutorialGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("bet.0129")
        )
    }
}

@Composable
internal fun BoxScope.PoolTutorialIndicators(state: BettingFlowUiState, definition: TutorialDefinition) {
    Box(
        Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(IndicatorStripHeight)
            .legacyAutomationId("pool-tutorial-indicator-strip"),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(definition.pages.size) { index ->
                val selected = index == state.page
                Box(Modifier.padding(horizontal = IndicatorGap)) {
                    Box(
                        Modifier.size(IndicatorGap).graphicsLayer {
                            scaleX = if (selected) SELECTED_INDICATOR_SCALE else 1f
                            scaleY = scaleX
                            alpha = if (selected) 1f else INACTIVE_INDICATOR_ALPHA
                        }.legacyAutomationId("pool-tutorial-dot-$index").then(
                            if (selected) {
                                Modifier.background(LoteriasCaixaDSColorTokens.TutorialIndicator, CircleShape)
                            } else {
                                Modifier.border(1.dp, LoteriasCaixaDSColorTokens.TutorialIndicatorOutline, CircleShape)
                            }
                        )
                    )
                }
            }
        }
    }
}

private val ControlTextFontSize = 14.sp
private val IndicatorStripHeight = 40.dp
private val TouchTargetHeight = 48.dp
private val IndicatorGap = 5.dp
private val NavigationActionWidth = 88.dp

private const val INACTIVE_INDICATOR_ALPHA = .5f

private const val SELECTED_INDICATOR_SCALE = 1.8f
