package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.*
import com.airbnb.lottie.compose.*
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
internal fun PoolTutorialLegacyLayout(
    state: BettingFlowUiState, definition: TutorialDefinition, pager: PagerState,
    onClose: () -> Unit, onPrimary: () -> Unit, onSecondary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    val finalPage = state.page == definition.pages.lastIndex
    var leftIntroduction by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(state.page) {
        if (state.page != 0) leftIntroduction = true
    }
    val actionColor = CaixaDSColorTokens.LegacyPoolTutorialAction
    val regular = TextStyle(fontFamily = CaixaDSLegacyRegularFontFamily,
        platformStyle = PlatformTextStyle(includeFontPadding = true))
    BoxWithConstraints(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)
        .windowInsetsPadding(WindowInsets.safeDrawing).testTag("pool-tutorial-viewport")) {
        val pagerHeight = maxHeight - 40.dp
        HorizontalPager(pager, Modifier.fillMaxWidth().height(pagerHeight).testTag("pool-tutorial-pager")) { index ->
            BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // XML centerInside preserves the authored 360 x 640 dp canvas without upscaling.
                val scale = min(1f, min(maxWidth.value / 360f, maxHeight.value / 640f))
                val page = definition.pages[index]
                val composition by rememberLottieComposition(LottieCompositionSpec.Asset(page.media))
                LottieAnimation(composition, Modifier.size((360f * scale).dp, (640f * scale).dp)
                    .testTag("pool-tutorial-media-${page.media}"),
                    iterations = if (page.loops) LottieConstants.IterateForever else 1,
                    speed = page.speed, contentScale = ContentScale.Inside)
            }
        }
        if (!finalPage) {
            Box(Modifier.align(Alignment.TopStart).size(88.dp, 48.dp)
                .testTag("pool-tutorial-close").clickable(role = Role.Button, onClick = onClose), Alignment.Center) {
                Text("Fechar", style = regular, fontSize = 14.sp, color = CaixaDSColorTokens.LegacyTutorialGray)
            }
        }
        Box(Modifier.align(Alignment.TopEnd).size(88.dp, 48.dp)
            .testTag("pool-tutorial-skip").clickable(role = Role.Button, onClick = onSecondary), Alignment.Center) {
            Text(tutorialSecondaryLabel(definition, state.page), style = regular, fontSize = 14.sp,
                color = CaixaDSColorTokens.LegacyTutorialGray, textAlign = TextAlign.Center)
        }
        tutorialPrimaryLabel(definition, state.page)?.let { label ->
            Layout(content = {
                Box(Modifier.width(164.dp).background(actionColor, RoundedCornerShape(10.dp))
                    .testTag("pool-tutorial-primary").clickable(role = Role.Button, onClick = onPrimary)
                    .padding(16.dp), contentAlignment = Alignment.Center) {
                    val styled = buildAnnotatedString {
                        if (finalPage) {
                            withStyle(SpanStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold)) { append("Escolher") }
                            append("\nmeu bolão")
                        } else if (!leftIntroduction) {
                            withStyle(SpanStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold)) { append(label) }
                        } else append("Vamos começar?")
                    }
                    BasicText(styled, style = regular.copy(fontSize = 16.sp,
                        color = CaixaDSColorTokens.White, textAlign = TextAlign.Center))
                }
                if (finalPage) {
                    Row(Modifier.testTag("pool-tutorial-checkbox-row"), verticalAlignment = Alignment.CenterVertically) {
                        Canvas(Modifier.size(13.dp).testTag("pool-tutorial-checkbox")
                            .toggleable(value = state.doNotShowAgain, role = Role.Checkbox,
                                onValueChange = { onToggleDoNotShowAgain() })) {
                            if (state.doNotShowAgain) {
                                drawRoundRect(actionColor, cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
                                val path = Path().apply { moveTo(size.width*3/13, size.height*7/13); lineTo(size.width*5/13,size.height*9/13); lineTo(size.width*10.5f/13,size.height*3/13) }
                                drawPath(path, CaixaDSColorTokens.White, style = Stroke(2.dp.toPx()))
                            } else drawRoundRect(CaixaDSColorTokens.LegacyTutorialCheckboxOutline,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()), style = Stroke(1.dp.toPx()))
                        }
                        Text("  Não exibir novamente", style = regular, fontSize = 14.sp, color = actionColor)
                    }
                }
            }, modifier = Modifier.fillMaxSize()) { measurables, constraints ->
                val button = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
                val checkbox = measurables.getOrNull(1)?.measure(constraints.copy(minWidth = 0, minHeight = 0))
                val pagerPixels = pagerHeight.roundToPx()
                val centerTop = ((pagerPixels - 1.dp.roundToPx()) / 2f).roundToInt()
                // TutorialNovaModalidadeFragment sets BOLAO bottomMargin to 200 raw pixels.
                val top = ((centerTop + pagerPixels - 200 - button.height) / 2f).roundToInt()
                layout(constraints.maxWidth, constraints.maxHeight) {
                    button.placeRelative((constraints.maxWidth - button.width) / 2, top)
                    checkbox?.placeRelative((constraints.maxWidth - checkbox.width) / 2, top + button.height + 8.dp.roundToPx())
                }
            }
        }
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(40.dp)
            .testTag("pool-tutorial-indicator-strip"), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(definition.pages.size) { index ->
                    val selected = index == state.page
                    Box(Modifier.padding(horizontal = 5.dp)) {
                        Box(Modifier.size(5.dp).graphicsLayer {
                            scaleX = if (selected) 1.8f else 1f; scaleY = scaleX
                            alpha = if (selected) 1f else .5f
                        }.testTag("pool-tutorial-dot-$index").then(if (selected)
                            Modifier.background(CaixaDSColorTokens.LegacyTutorialIndicator, CircleShape)
                        else Modifier.border(1.dp, CaixaDSColorTokens.LegacyTutorialIndicatorOutline, CircleShape)))
                    }
                }
            }
        }
    }
}
