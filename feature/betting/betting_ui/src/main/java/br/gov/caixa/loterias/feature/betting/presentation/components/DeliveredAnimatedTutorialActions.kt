package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialPrimaryLabel
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialSecondaryLabel
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
internal fun BoxScope.AnimatedTutorialPrimaryAction(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    actionColor: Color,
    onPrimary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val finalPage = state.page == definition.pages.lastIndex
    tutorialPrimaryLabel(definition, state.page)?.let { label ->
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onPrimary,
                modifier = (Modifier.width(PrimaryActionWidth)).uiId("bet.0005"),
                shape = RoundedCornerShape(LabelTopGap),
                colors = ButtonDefaults.buttonColors(containerColor = actionColor)
            ) {
                Text(
                    label,
                    color = LoteriasCaixaDSColorTokens.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("bet.0006")
                )
            }
            if (finalPage) {
                Row(
                    Modifier.uiId("bet.0007").toggleable(value = state.doNotShowAgain, role = Role.Checkbox, onValueChange = {
                        onToggleDoNotShowAgain()
                    }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = state.doNotShowAgain, onCheckedChange = null, modifier = Modifier.uiId("bet.0008"))
                    Text(
                        uiStrings.getString(DeliveryR.string.ui_nao_exibir_novamente_f8abb0),
                        color = if (state.spec.route == "betting/tutorial") actionColor else LoteriasCaixaDSColorTokens.White,
                        modifier = Modifier
                            .uiId("bet.0009")
                    )
                }
            }
        }
    }
}

@Composable
internal fun BoxScope.AnimatedTutorialTopActions(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    onClose: () -> Unit,
    onSecondary: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val finalPage = state.page == definition.pages.lastIndex
    if (!finalPage) {
        TextButton(onClick = onClose, modifier = (Modifier.align(Alignment.TopStart).padding(top = ContentGap)).uiId("bet.0001")) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_fechar_0f2bd8),
                color = LoteriasCaixaDSColorTokens.TutorialGray,
                modifier = Modifier
                    .uiId("bet.0002")
            )
        }
    }
    TextButton(onClick = onSecondary, modifier = (Modifier.align(Alignment.TopEnd).padding(top = ContentGap)).uiId("bet.0003")) {
        Text(
            tutorialSecondaryLabel(definition, state.page),
            color = LoteriasCaixaDSColorTokens.TutorialGray,
            modifier = Modifier
                .uiId("bet.0004")
        )
    }
}

private val LabelTopGap = 10.dp
private val PrimaryActionWidth = 164.dp
private val ContentGap = 8.dp
