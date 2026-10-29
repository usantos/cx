package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
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

@Composable
internal fun AnimatedLotteryTutorial(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    onPageSelected: (Int) -> Unit,
    onClose: () -> Unit,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val pagerState = rememberPagerState(initialPage = state.page, pageCount = definition.pages::size)
    val density = LocalDensity.current
    val enlargedFont = density.fontScale > 1f
    var navigationHeight by remember { mutableIntStateOf(0) }
    val navigationInset = if (enlargedFont) with(density) { navigationHeight.toDp() } + 40.dp else 0.dp
    val finalPage = state.page == definition.pages.lastIndex
    val actionColor = if (state.spec.route == "betting/tutorial") {
        CaixaDSColorTokens.PoolTutorialAction
    } else {
        CaixaDSColorTokens.TutorialGreen
    }

    RapidPagerSynchronization(pagerState, state.page, onPageSelected)
    if (state.spec.route == "betting/tutorial") {
        PoolTutorialLayout(
            state,
            definition,
            pagerState,
            onClose,
            onPrimary,
            onSecondary,
            onToggleDoNotShowAgain,
            onPageSelected
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(CaixaDS.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize().padding(bottom = navigationInset)) { index ->
            LottieTutorialPage(definition.pages[index], index, definition.pages.size, index == pagerState.currentPage)
        }

        if (!finalPage) {
            TextButton(onClick = onClose, modifier = (Modifier.align(Alignment.TopStart).padding(top = 8.dp)).uiId("bet.0001")) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
                    color = CaixaDSColorTokens.TutorialGray,
                    modifier = Modifier
                        .uiId("bet.0002")
                )
            }
        }
        TextButton(onClick = onSecondary, modifier = (Modifier.align(Alignment.TopEnd).padding(top = 8.dp)).uiId("bet.0003")) {
            Text(
                tutorialSecondaryLabel(definition, state.page),
                color = CaixaDSColorTokens.TutorialGray,
                modifier = Modifier
                    .uiId("bet.0004")
            )
        }

        AnimatedTutorialPrimaryAction(state, definition, actionColor, onPrimary, onToggleDoNotShowAgain)
        if (enlargedFont) {
            AnimatedTutorialNavigation(
                state.page,
                definition.pages.lastIndex,
                onPageSelected,
                Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp).onSizeChanged { navigationHeight = it.height }
            )
        }

        TutorialIndicator(
            pageCount = definition.pages.size,
            selectedPage = state.page,
            selectedColor = CaixaDSColorTokens.TutorialIndicator,
            unselectedColor = CaixaDSColorTokens.TutorialIndicatorOutline,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun BoxScope.AnimatedTutorialPrimaryAction(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    actionColor: Color,
    onPrimary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val finalPage = state.page == definition.pages.lastIndex
    tutorialPrimaryLabel(definition, state.page)?.let { label ->
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onPrimary,
                modifier = (Modifier.width(164.dp)).uiId("bet.0005"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = actionColor)
            ) {
                Text(
                    label,
                    color = CaixaDSColorTokens.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("bet.0006")
                )
            }
            if (finalPage) {
                Row(
                    Modifier.uiId("bet.0007").toggleable(value = state.doNotShowAgain, role = Role.Checkbox, onValueChange = {
                        onToggleDoNotShowAgain()
                    }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = state.doNotShowAgain, onCheckedChange = null, modifier = Modifier.uiId("bet.0008"))
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_nao_exibir_novamente_f8abb0),
                        color = if (state.spec.route == "betting/tutorial") actionColor else CaixaDSColorTokens.White,
                        modifier = Modifier
                            .uiId("bet.0009")
                    )
                }
            }
        }
    }
}

@Composable
private fun LottieTutorialPage(page: TutorialPage, index: Int, total: Int, active: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(page.media))
    Box(
        Modifier.fillMaxSize().clearAndSetSemantics {
            if (active) {
                contentDescription = tutorialInstructionDescription(page, index, total)
                role = Role.Image
            }
        }
    ) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier.fillMaxSize(),
            iterations = if (page.loops) LottieConstants.IterateForever else 1,
            speed = page.speed,
            contentScale = ContentScale.Fit
        )
    }
}
