package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.feature.support.presentation.components.gameHelpDate
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import br.gov.caixa.loterias.feature.support.ui.R as DeliveryR

@Composable
internal fun GameHelpMetadata(state: SupportUiState) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(157.dp)
            .background(LoteriasCaixaDS.colors.gameHelpHeader)
    ) {
        HelpMetadata(
            stringResource(DeliveryR.string.format_concurso),
            state.gameHelpContestNumber?.let { "Nº $it" } ?: uiStrings.getString(DeliveryR.string.ui_nao_informado_01af1e),
            "Abertura",
            uiStrings.getString(DeliveryR.string.ui_nao_informada_c6f460),
            (Modifier.weight(1f).padding(start = 38.dp, top = 40.dp)).uiId("help.0006")
        )
        HelpMetadata(
            uiStrings.getString(DeliveryR.string.ui_proximo_sorteio_a5035e),
            gameHelpDate(state.gameHelpDrawDate),
            "Encerramento",
            gameHelpDate(state.gameHelpClosingDateTime, includeTime = true),
            (Modifier.weight(1f).padding(start = 38.dp, top = 40.dp)).uiId("help.0007")
        )
    }
}

@Composable
internal fun HelpMetadata(firstLabel: String, firstValue: String, secondLabel: String, secondValue: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        val style = TextStyle(
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontSize = 14.2.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
        Text(
            firstLabel,
            color = LoteriasCaixaDS.colors.onBrand,
            style = style.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .uiId("help.0011")
        )
        Text(firstValue, color = LoteriasCaixaDS.colors.onBrand, style = style, modifier = Modifier.uiId("help.0012"))
        Text(
            secondLabel,
            color = LoteriasCaixaDS.colors.onBrand,
            style = style.copy(fontWeight = FontWeight.Bold),
            modifier = (Modifier.padding(top = 21.dp)).uiId("help.0013")
        )
        Text(secondValue, color = LoteriasCaixaDS.colors.onBrand, style = style, modifier = Modifier.uiId("help.0014"))
    }
}
