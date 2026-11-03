package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialPage
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import coil3.compose.AsyncImage

@Composable
internal fun SuperSevenPage(page: TutorialPage, accent: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val mediaAsset = "file:///android_asset/${page.media}.gif"
    Column(
        modifier = Modifier.fillMaxSize()
            .uiId("bet.0175").verticalScroll(rememberScrollState()).padding(horizontal = ControlSize, vertical = SectionGap),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            page.heading.orEmpty(),
            color = if (page.media == "super7_passo2") accent else LoteriasCaixaDS.colors.text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("bet.0176")
        )
        Spacer(Modifier.height(SectionGap))
        Text(
            page.description.orEmpty(),
            color = LoteriasCaixaDS.colors.text,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("bet.0177")
        )
        Spacer(Modifier.height(SectionGap))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f).height(1.dp).background(LoteriasCaixaDSColorTokens.TutorialDivider))
            Text(
                uiStrings.getString(DeliveryR.string.ui_veja_como_apostar_8b34ac),
                (Modifier.padding(horizontal = IndicatorGap)).uiId("bet.0178"),
                color = LoteriasCaixaDSColorTokens.TutorialDivider
            )
            Spacer(Modifier.weight(1f).height(1.dp).background(LoteriasCaixaDSColorTokens.TutorialDivider))
        }
        Spacer(Modifier.height(SectionGap))
        AsyncImage(
            model = mediaAsset,
            contentDescription = uiStrings.getString(DeliveryR.string.ui_demonstracao_animada_1_s_cb7005, page.heading),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        page.footer?.let {
            Text(it, (Modifier.padding(QuantityTopGap)).uiId("bet.0179"), color = LoteriasCaixaDS.colors.text, textAlign = TextAlign.Center)
        }
    }
}

private val SectionGap = 20.dp
private val QuantityTopGap = 30.dp
private val ControlSize = 40.dp
private val IndicatorGap = 5.dp
