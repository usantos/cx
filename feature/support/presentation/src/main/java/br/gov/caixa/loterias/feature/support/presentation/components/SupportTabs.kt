package br.gov.caixa.loterias.feature.support.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.support.R as LocalizedR
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab

@Composable
internal fun SupportTabs(selected: SupportTab, onAction: (SupportAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = TextStyle(
        fontFamily = CaixaDSFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val termsWidth = with(density) {
        measurer.measure(
            uiStrings.getString(LocalizedR.string.ui_termo_de_uso_d57d7b),
            style
        ).size.width.toDp()
    }
    val faqWidth = with(density) { measurer.measure(uiStrings.getString(LocalizedR.string.ui_duvidas_10e026), style).size.width.toDp() }
    // RadioButtons do XML usam wrap_content + pesos iguais: distribuem o espaço restante igualmente.
    BoxWithConstraints(Modifier.fillMaxWidth().height(56.dp)) {
        val availableWidth = maxWidth
        val firstWidth = (availableWidth + termsWidth - faqWidth) / 2
        Row(Modifier.fillMaxSize()) {
            listOf(
                SupportTab.TERMS to uiStrings.getString(LocalizedR.string.ui_termo_de_uso_d57d7b),
                SupportTab.FAQ to uiStrings.getString(LocalizedR.string.ui_duvidas_10e026)
            ).forEach { (
                tab,
                label,
            ),
                ->
                val isSelected = selected == tab
                Box(
                    Modifier.width(if (tab == SupportTab.TERMS) firstWidth else availableWidth - firstWidth)
                        .fillMaxSize()
                        .background(if (isSelected) CaixaDSMarketplaceStyle.tab else CaixaDSMarketplaceStyle.inactiveTab)
                        .uiId("help.0001").clickable { onAction(SupportAction.SelectTab(tab)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Image(
                            painterResource(CaixaDSR.drawable.legacy_tab_seta),
                            null,
                            Modifier.align(Alignment.BottomCenter)
                        )
                    }
                    Text(
                        label,
                        color = if (isSelected) CaixaDSColorTokens.White else CaixaDSMarketplaceStyle.tab,
                        style = style,
                        modifier = Modifier.uiId("help.0002")
                    )
                }
            }
        }
    }
}
