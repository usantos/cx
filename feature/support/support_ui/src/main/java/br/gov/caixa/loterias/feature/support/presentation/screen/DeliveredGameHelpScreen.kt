package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Composable
internal fun RenderGameHelpScreen(state: SupportUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    if (state.isLoading) br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog()
    val modalityId = state.gameHelpModalityId
    val modalityName = state.gameHelpModalityName
    val lotteryStyle = loteriasCaixaDSLotteryStyle(modalityId, modalityName)
    val headerDark = when (modalityId) {
        HELP_LOTOFACIL_ID -> LoteriasCaixaDSColorTokens.LotomaniaDark

        // AjudaActivity usa esta cor para Lotofácil.
        HELP_LOTOGOL_ID -> LoteriasCaixaDSColorTokens.LotogolDark

        else -> lotteryStyle.dark
    }
    val headerLight = when (modalityId) {
        HELP_LOTOMANIA_ID -> LoteriasCaixaDSColorTokens.HelpLotomania
        HELP_TIMEMANIA_ID -> LoteriasCaixaDSColorTokens.HelpTimemania
        HELP_LOTECA_ID -> LoteriasCaixaDSColorTokens.HelpLoteca
        HELP_LOTOGOL_ID -> LoteriasCaixaDSColorTokens.HelpLotogol
        else -> lotteryStyle.light
    }
    val title = modalityName.ifBlank { "Mega-Sena" }.lowercase()
    Column(
        modifier.fillMaxSize().background(headerDark).statusBarsPadding()
            .legacyAutomationId("support_game_help")
    ) {
        GameHelpTitle(modalityId, modalityName, title, headerLight, lotteryStyle.content, onBack)
        Column(
            Modifier
                .fillMaxSize()
                .background(LoteriasCaixaDS.colors.graphite)
        ) {
            GameHelpMetadata(state)
            GameHelpRules(state.htmlContent)
        }
    }
}

private const val HELP_LOTOFACIL_ID = 8
private const val HELP_LOTOGOL_ID = 14
private const val HELP_LOTOMANIA_ID = 16
private const val HELP_TIMEMANIA_ID = 20
private const val HELP_LOTECA_ID = 19
