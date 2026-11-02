package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingGameError
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingLegacyFooter
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingLegacyGameHeader
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
internal fun BettingGameScreen(
    state: BettingGameUiState,
    actions: BettingGameActions,
    cartTotal: java.math.BigDecimal,
    cartBetCount: Int,
    onCart: (() -> Unit)?,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    fallbackModalityId: Int?,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = state.game?.modality?.name ?: uiStrings.getString(LocalizedR.string.ui_simular_aposta_507413),
        containerColor = LoteriasCaixaDSColorTokens.White,
        customTopBar = {
            Column {
                BettingLegacyGameHeader(state, onBack, onHelp, fallbackModalityId)
                if (state.mode == BetEntryMode.MANUAL && state.game?.acceptsMirror == true &&
                    state.selectionStep == BetSelectionStep.NUMBERS && state.selectedNumbers.size == state.numberQuantity
                ) {
                    BettingLegacyMirror(state.generateMirror, actions.toggleMirror, manual = true)
                }
            }
        },
        onBack = onBack,
        bottomBar = {
            if (state.game != null) {
                BettingLegacyFooter(state, actions, cartTotal, cartBetCount, onCart, onClear = onClear)
            }
        },
        modifier = Modifier.uiId("bet.0180")
    ) { padding ->
        when {
            state.loading && state.game == null -> Box(Modifier.fillMaxSize().padding(padding))

            state.game == null -> BettingGameError(
                message = state.errorMessage ?: uiStrings.getString(LocalizedR.string.ui_aposta_indisponivel_8fce95),
                padding = padding,
                onRetry = onRetry,
                onBack = onBack,
                review = state.recoveryReview
            )

            else -> {
                Column(
                    Modifier.fillMaxSize().padding(
                        padding
                    ).padding(
                        16.dp
                    ).clip(
                        RoundedCornerShape(8.dp)
                    ).border(1.dp, LoteriasCaixaDSColorTokens.SimulationToolbar, RoundedCornerShape(8.dp)).padding(1.dp)
                ) {
                    content()
                }
            }
        }
    }
}
