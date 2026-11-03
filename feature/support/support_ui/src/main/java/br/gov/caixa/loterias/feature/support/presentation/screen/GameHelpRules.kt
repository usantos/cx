package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.feature.support.ui.R as DeliveryR

@Composable
internal fun GameHelpRules(htmlContent: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(bottom = 39.dp)
            .uiId("help.0008").verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp)
    ) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_regras_do_jogo_e96ce2),
            color = LoteriasCaixaDS.colors.onBrand,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSRegularFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.2.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            modifier = Modifier.uiId("help.0009")
        )
        Text(
            AnnotatedString.fromHtml(htmlContent),
            color = LoteriasCaixaDS.colors.onBrand,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSRegularFontFamily,
                fontSize = 14.2.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true),
                lineBreak = LineBreak.Paragraph
            ),
            modifier = (Modifier.padding(top = 14.2.dp, bottom = 39.dp)).uiId("help.0010")
        )
    }
}
