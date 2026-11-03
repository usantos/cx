package br.gov.caixa.loterias.feature.betting.presentation.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle

@Composable
fun modalityColor(id: Int): androidx.compose.ui.graphics.Color {
    val style = loteriasCaixaDSLotteryStyle(id)
    return if (style != resolveLoteriasCaixaDSLotteryStyle(id, "", LoteriasCaixaDSCampaign.DEFAULT)) style.listText else style.textOnSurface
}
