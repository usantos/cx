package br.gov.caixa.loterias.feature.betting.presentation.style

import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable

@Composable
internal fun modalityColor(id: Int): androidx.compose.ui.graphics.Color {
    val style = caixaDSLotteryStyle(id)
    return if (style != resolveCaixaDSLotteryStyle(id, "", CaixaDSCampaign.DEFAULT)) style.listText else style.textOnSurface
}
