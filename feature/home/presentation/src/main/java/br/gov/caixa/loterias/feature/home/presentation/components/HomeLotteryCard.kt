package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeResultContent
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import kotlin.math.roundToInt

@Composable
internal fun LotteryCard(
    lottery: HomeLottery,
    resultState: ResultsUiState,
    resultExpanded: Boolean,
    onToggleResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Card(
        modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val expansion = animateFloatAsState(
                targetValue = if (resultExpanded) 1f else 0f,
                animationSpec = tween(RESULT_EXPANSION_DURATION_MILLIS, easing = FastOutSlowInEasing),
                label = uiStrings.getString(LocalizedR.string.ui_home_result_reveal_398a5f)
            )
            val fullResultHeight = (maxHeight - 103.dp).coerceAtLeast(0.dp)
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (lottery.kind == HomeLotteryKind.LOTTERY) {
                    Box(
                        Modifier.layout { measurable, constraints ->
                            val width = (240.dp.toPx() + 40.dp.toPx() * expansion.value).roundToInt()
                                .coerceAtMost(constraints.maxWidth)
                            val header = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                            layout(width, header.height) { header.placeRelative(0, 0) }
                        }.height(32.dp).background(lottery.darkColor)
                            .uiId("home.0035").clickable(onClick = onToggleResult),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_ultimo_resultado_d527ea),
                            color = lottery.darkContent,
                            style = homeLegacyTextStyle(14.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.uiId("home.0036")
                        )
                        Icon(
                            painterResource(if (resultExpanded) R.drawable.legacy_home_ic_x_bold else R.drawable.legacy_home_ic_seta_baixo),
                            contentDescription = if (resultExpanded) {
                                uiStrings.getString(
                                    LocalizedR.string.ui_fechar_resultado_3062bc
                                )
                            } else {
                                uiStrings.getString(
                                    LocalizedR.string.ui_abrir_ultimo_resultado_4bb93c
                                )
                            },
                            modifier = (Modifier.align(Alignment.CenterEnd).padding(end = 12.dp).size(9.dp)).uiId("home.0037"),
                            tint = lottery.darkContent
                        )
                    }
                    Box(
                        Modifier.clipToBounds().background(lottery.darkColor).layout { measurable, constraints ->
                            // Mede o resultado no tamanho final; anima somente sua área de visualização recortada.
                            val fullWidth = 280.dp.roundToPx().coerceAtMost(constraints.maxWidth)
                            val fullHeight = fullResultHeight.roundToPx()
                            val content = measurable.measure(Constraints.fixed(fullWidth, fullHeight))
                            val width = (240.dp.toPx() + 40.dp.toPx() * expansion.value).roundToInt()
                                .coerceAtMost(constraints.maxWidth)
                            val height = constraints.constrainHeight((fullHeight * expansion.value).roundToInt())
                            layout(width, height) { content.placeRelative((width - fullWidth) / 2, 0) }
                        }
                    ) {
                        HomeResultContent(resultState, lottery, Modifier.fillMaxSize())
                    }
                }
                if (lottery.kind != HomeLotteryKind.LOTTERY) {
                    // O legado oculta os controles de resultado, preservando seu contêiner de 32dp.
                    Box(Modifier.width(240.dp).height(32.dp).background(lottery.darkColor))
                }
                LotteryBanner(lottery)
                CollapsedCardViewport(lottery, fullResultHeight)
            }
        }
    }
}

private const val RESULT_EXPANSION_DURATION_MILLIS = 320

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.CollapsedCardViewport(
    lottery: HomeLottery,
    fullResultHeight: androidx.compose.ui.unit.Dp,
) {
    if (LocalDensity.current.fontScale > 1f) {
        Box(Modifier.weight(1f).fillMaxWidth().clipToBounds()) { HomeLargeFontLotteryContent(lottery) }
    } else {
        Box(
            Modifier.weight(1f).fillMaxWidth().clipToBounds().layout { measurable, constraints ->
                // Desliza o conteúdo do prêmio para fora sem comprimi-lo nem reorganizá-lo a cada quadro.
                val fullHeight = fullResultHeight.roundToPx()
                val content = measurable.measure(constraints.copy(minHeight = fullHeight, maxHeight = fullHeight))
                layout(content.width, constraints.constrainHeight(fullHeight)) { content.placeRelative(0, 0) }
            }
        ) {
            CollapsedLotteryContent(lottery)
        }
    }
}
