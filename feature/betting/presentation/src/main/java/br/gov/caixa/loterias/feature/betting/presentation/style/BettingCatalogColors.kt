package br.gov.caixa.loterias.feature.betting.presentation.style

import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable

@Composable
internal fun modalityColor(id: Int): androidx.compose.ui.graphics.Color {
    val style = loteriasCaixaDSLotteryStyle(id)
    return if (style != resolveLoteriasCaixaDSLotteryStyle(id, "", LoteriasCaixaDSCampaign.DEFAULT)) style.listText else style.textOnSurface
}
