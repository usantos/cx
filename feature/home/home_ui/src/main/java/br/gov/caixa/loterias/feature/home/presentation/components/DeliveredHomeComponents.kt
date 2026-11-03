package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR
import kotlin.math.roundToInt

@Composable
fun RenderHomeLoadingState(loading: Boolean, errorMessage: String?, onRetry: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (loading) {
            CircularProgressIndicator(color = LoteriasCaixaDS.colors.brand)
        } else {
            Text(
                errorMessage ?: uiStrings.getString(DeliveryR.string.ui_nenhuma_modalidade_disponivel_no_mom_8d67af),
                color = LoteriasCaixaDS.colors.text,
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("home.0022")
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                (Modifier.padding(top = 20.dp).uiId("home.0023").clickable(onClick = onRetry)).uiId("home.0024"),
                color = LoteriasCaixaDS.colors.brand,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** textSize de TextView no XML é lido como dimensionPixelSize; setTextSize em Java preserva pixels fracionários. */
@Composable
internal fun homeLegacyTextStyle(size: TextUnit, weight: FontWeight = FontWeight.Normal, xmlTextSize: Boolean = true): TextStyle {
    val renderedSize = if (xmlTextSize) with(LocalDensity.current) { size.toPx().roundToInt().toSp() } else size
    return TextStyle(
        fontFamily = LoteriasCaixaDSFontFamily,
        fontSize = renderedSize,
        fontWeight = weight,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
}
