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
internal fun GameHelpScreen(state: SupportUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val modalityId = state.gameHelpModalityId
    val modalityName = state.gameHelpModalityName
    val lotteryStyle = loteriasCaixaDSLotteryStyle(modalityId, modalityName)
    val headerDark = when (modalityId) {
        8 -> LoteriasCaixaDSColorTokens.LotomaniaDark

        // AjudaActivity usa esta cor para Lotofácil.
        14 -> LoteriasCaixaDSColorTokens.LotogolDark

        else -> lotteryStyle.dark
    }
    val headerLight = when (modalityId) {
        16 -> LoteriasCaixaDSColorTokens.HelpLotomania
        20 -> LoteriasCaixaDSColorTokens.HelpTimemania
        19 -> LoteriasCaixaDSColorTokens.HelpLoteca
        14 -> LoteriasCaixaDSColorTokens.HelpLotogol
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
