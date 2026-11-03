package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialPrimaryLabel
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition

@Composable
internal fun BoxScope.SuperSevenPrimaryAction(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    superSeven: Color,
    onPrimary: () -> Unit,
) {
    Button(
        onClick = onPrimary,
        modifier = (
            Modifier.align(
                Alignment.BottomCenter
            ).padding(bottom = PrimaryBottomInset).width(PrimaryActionWidth).height(PrimaryButtonHeight)
            ).uiId("bet.0173"),
        shape = RoundedCornerShape(0.dp),
        colors = if (state.page == definition.pages.lastIndex) {
            ButtonDefaults.buttonColors(containerColor = superSeven, contentColor = LoteriasCaixaDSColorTokens.White)
        } else {
            ButtonDefaults.buttonColors(containerColor = LoteriasCaixaDSColorTokens.White, contentColor = superSeven)
        },
        border = if (state.page == definition.pages.lastIndex) null else BorderStroke(1.dp, superSeven)
    ) {
        Text(
            tutorialPrimaryLabel(definition, state.page).orEmpty(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .uiId("bet.0174")
        )
    }
}

private val PrimaryActionWidth = 184.dp
private val PrimaryBottomInset = 52.dp
private val PrimaryButtonHeight = 64.dp
