package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSPoolTutorialPrimary
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTutorialPreference
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialInstructionDescription
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialPrimaryLabel
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialSecondaryLabel
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialPage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlin.math.min
import kotlin.math.roundToInt

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("LongMethod")
@Composable
internal fun PoolTutorialLayout(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    pager: PagerState,
    onClose: () -> Unit,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
    onPageSelected: (Int) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val density = LocalDensity.current
    val enlargedFont = density.fontScale > 1f
    var navigationHeight by remember { mutableIntStateOf(0) }
    val finalPage = state.page == definition.pages.lastIndex
    var leftIntroduction by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(state.page) {
        if (state.page != 0) leftIntroduction = true
    }
    val regular = TextStyle(
        fontFamily = CaixaDSRegularFontFamily,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
    BoxWithConstraints(
        Modifier.fillMaxSize().background(CaixaDSColorTokens.White)
            .windowInsetsPadding(WindowInsets.safeDrawing).legacyAutomationId("pool-tutorial-viewport")
    ) {
        val navigationInset = if (enlargedFont) with(density) { navigationHeight.toDp() } else 0.dp
        val pagerHeight = (maxHeight - 40.dp - navigationInset).coerceAtLeast(0.dp)
        HorizontalPager(pager, Modifier.fillMaxWidth().height(pagerHeight).legacyAutomationId("pool-tutorial-pager")) { index ->
            PoolTutorialMedia(definition.pages[index], index, definition.pages.size, index == pager.currentPage)
        }
        if (!finalPage) {
            Box(
                Modifier.align(Alignment.TopStart).size(88.dp, 48.dp)
                    .legacyAutomationId("pool-tutorial-close").uiId("bet.0126").clickable(role = Role.Button, onClick = onClose),
                Alignment.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
                    style = regular,
                    fontSize = 14.sp,
                    color = CaixaDSColorTokens.TutorialGray,
                    modifier = Modifier.uiId("bet.0127")
                )
            }
        }
        Box(
            Modifier.align(Alignment.TopEnd).size(88.dp, 48.dp)
                .legacyAutomationId("pool-tutorial-skip").uiId("bet.0128").clickable(role = Role.Button, onClick = onSecondary),
            Alignment.Center
        ) {
            Text(
                tutorialSecondaryLabel(definition, state.page),
                style = regular,
                fontSize = 14.sp,
                color = CaixaDSColorTokens.TutorialGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("bet.0129")
            )
        }
        tutorialPrimaryLabel(definition, state.page)?.let { label ->
            Layout(content = {
                val styled = buildAnnotatedString {
                    if (finalPage) {
                        withStyle(SpanStyle(fontFamily = CaixaDSFuturaFontFamily, fontWeight = FontWeight.Bold)) { append("Escolher") }
                        append(uiStrings.getString(LocalizedR.string.ui_meu_bolao_c690d7))
                    } else if (!leftIntroduction) {
                        withStyle(SpanStyle(fontFamily = CaixaDSFuturaFontFamily, fontWeight = FontWeight.Bold)) { append(label) }
                    } else {
                        append(uiStrings.getString(LocalizedR.string.ui_vamos_comecar_b9007b))
                    }
                }
                CaixaDSPoolTutorialPrimary(
                    styled,
                    onPrimary,
                    (Modifier.legacyAutomationId("pool-tutorial-primary")).uiId("bet.0130")
                )
                if (finalPage) {
                    CaixaDSTutorialPreference(
                        state.doNotShowAgain,
                        onToggleDoNotShowAgain,
                        (Modifier.legacyAutomationId("pool-tutorial-checkbox-row")).uiId("bet.0131"),
                        Modifier.legacyAutomationId("pool-tutorial-checkbox")
                    )
                }
            }, modifier = Modifier.fillMaxSize()) { measurables, constraints ->
                val button = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
                val checkbox = measurables.getOrNull(1)?.measure(constraints.copy(minWidth = 0, minHeight = 0))
                val pagerPixels = pagerHeight.roundToPx()
                val centerTop = ((pagerPixels - 1.dp.roundToPx()) / 2f).roundToInt()
                // TutorialNovaModalidadeFragment define bottomMargin de BOLAO como 200 pixels brutos.
                val top = ((centerTop + pagerPixels - 200 - button.height) / 2f).roundToInt()
                layout(constraints.maxWidth, constraints.maxHeight) {
                    button.placeRelative((constraints.maxWidth - button.width) / 2, top)
                    checkbox?.placeRelative((constraints.maxWidth - checkbox.width) / 2, top + button.height + 8.dp.roundToPx())
                }
            }
        }
        if (enlargedFont) {
            AnimatedTutorialNavigation(
                state.page,
                definition.pages.lastIndex,
                onPageSelected,
                Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp).onSizeChanged { navigationHeight = it.height }
            )
        }
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(40.dp)
                .legacyAutomationId("pool-tutorial-indicator-strip"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(definition.pages.size) { index ->
                    val selected = index == state.page
                    Box(Modifier.padding(horizontal = 5.dp)) {
                        Box(
                            Modifier.size(5.dp).graphicsLayer {
                                scaleX = if (selected) 1.8f else 1f
                                scaleY = scaleX
                                alpha = if (selected) 1f else .5f
                            }.legacyAutomationId("pool-tutorial-dot-$index").then(
                                if (selected) {
                                    Modifier.background(CaixaDSColorTokens.TutorialIndicator, CircleShape)
                                } else {
                                    Modifier.border(1.dp, CaixaDSColorTokens.TutorialIndicatorOutline, CircleShape)
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PoolTutorialMedia(page: TutorialPage, index: Int, total: Int, active: Boolean) {
    BoxWithConstraints(
        Modifier.fillMaxSize().clearAndSetSemantics {
            if (active) {
                contentDescription = tutorialInstructionDescription(page, index, total)
                role = Role.Image
            }
        },
        contentAlignment = Alignment.Center
    ) {
        // centerInside do XML preserva a área original de 360 x 640 dp sem ampliá-la.
        val scale = min(1f, min(maxWidth.value / 360f, maxHeight.value / 640f))
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset(page.media))
        LottieAnimation(
            composition,
            Modifier.size((360f * scale).dp, (640f * scale).dp)
                .legacyAutomationId("pool-tutorial-media-${page.media}"),
            iterations = if (page.loops) LottieConstants.IterateForever else 1,
            speed = page.speed,
            contentScale = ContentScale.Inside
        )
    }
}
