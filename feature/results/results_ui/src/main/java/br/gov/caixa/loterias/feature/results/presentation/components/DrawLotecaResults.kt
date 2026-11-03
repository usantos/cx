package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.presentation.accessibility.PortugueseSpeech
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultSportsSpokenDescription
import br.gov.caixa.loterias.feature.results.ui.R as DeliveryR
import kotlin.math.absoluteValue

/** ResultadoLotecaFragment: carrossel de partidas centralizado, três resultados e cor nativa do vencedor. */
@Composable
internal fun DrawLotecaResults(matches: List<DrawSportsMatch>, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val pager = rememberPagerState(pageCount = { matches.size })
    val expandedText = LocalDensity.current.fontScale > 1f
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val pageWidth = minOf(280.dp, maxWidth)
        HorizontalPager(
            state = pager,
            pageSize = PageSize.Fixed(pageWidth),
            contentPadding = PaddingValues(horizontal = ((maxWidth - pageWidth) / 2).coerceAtLeast(0.dp)),
            modifier = Modifier.fillMaxWidth().legacyAutomationId("result_loteca_pager")
        ) { page ->
            val match = matches[page]
            Column(
                Modifier.fillMaxWidth().padding(top = 17.dp, bottom = 20.dp)
                    .graphicsLayer {
                        val distance = ((pager.currentPage - page) + pager.currentPageOffsetFraction).absoluteValue.coerceIn(0f, 1f)
                        scaleX = 1f - .2f * distance
                        scaleY = scaleX
                    }
                    .clearAndSetSemantics {
                        if (page == pager.currentPage) {
                            contentDescription =
                                "Loteca. Partida ${PortugueseSpeech.number(page + 1)} de ${PortugueseSpeech.number(matches.size)}. " +
                                uiStrings.getString(DeliveryR.string.ui_domingo_424e93) + ". " +
                                resultSportsSpokenDescription(match, lotogol = false)
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_domingo_424e93),
                    color = color,
                    fontFamily = LoteriasCaixaDSRegularFontFamily,
                    fontSize = 14.sp,
                    modifier = Modifier.uiId("results.0001")
                )
                if (expandedText) {
                    Column(Modifier.fillMaxWidth()) {
                        LotecaOutcome(match.home, match.home.won == true, match.legendLetter, color, expandedText = true)
                        LotecaOutcome(null, match.draw == true, "", color, expandedText = true)
                        LotecaOutcome(match.away, match.away.won == true, match.legendLetter, color, expandedText = true)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.Center) {
                        LotecaOutcome(match.home, match.home.won == true, match.legendLetter, color)
                        LotecaOutcome(null, match.draw == true, "", color)
                        LotecaOutcome(match.away, match.away.won == true, match.legendLetter, color)
                    }
                }
                Text(
                    uiStrings.getString(DeliveryR.string.ui_1_s_de_2_s_ec1761, (page + 1).toString(), matches.size.toString()),
                    (Modifier.padding(top = 20.dp).heightIn(min = 20.dp)).uiId("results.0002"),
                    color = color,
                    fontFamily = LoteriasCaixaDSRegularFontFamily,
                    fontSize = 14.sp
                )
            }
        }
    }
}
