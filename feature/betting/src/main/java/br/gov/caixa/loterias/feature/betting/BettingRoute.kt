package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun BettingRoute(viewModel: BettingFlowViewModel, onBack: () -> Unit, onComplete: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BettingFlowEffect.NavigateBack -> onBack()
                BettingFlowEffect.Completed -> onComplete()
            }
        }
    }
    val definition = tutorialDefinition(state.spec.route)
    if (definition.usesLegacyOverlayControls) {
        AnimatedLotteryTutorial(
            state = state,
            definition = definition,
            onPageSelected = viewModel::selectPage,
            onClose = viewModel::back,
            onPrimary = viewModel::primaryAction,
            onSecondary = viewModel::secondaryAction,
            onToggleDoNotShowAgain = viewModel::toggleDoNotShowAgain,
        )
    } else {
        SuperSevenTutorial(
            state = state,
            definition = definition,
            onPageSelected = viewModel::selectPage,
            onBack = viewModel::back,
            onPrimary = viewModel::primaryAction,
            onSkip = viewModel::secondaryAction,
        )
    }
}

@Composable
private fun AnimatedLotteryTutorial(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    onPageSelected: (Int) -> Unit,
    onClose: () -> Unit,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = state.page, pageCount = definition.pages::size)
    val finalPage = state.page == definition.pages.lastIndex
    val actionColor = if (state.spec.route == "betting/tutorial") {
        CaixaDSColorTokens.LegacyPoolTutorialAction
    } else {
        CaixaDSColorTokens.LegacyTutorialGreen
    }

    RapidPagerSynchronization(pagerState, state.page, onPageSelected)
    if (state.spec.route == "betting/tutorial") {
        PoolTutorialLegacyLayout(state, definition, pagerState, onClose, onPrimary,
            onSecondary, onToggleDoNotShowAgain)
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(CaixaDS.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            LottieTutorialPage(definition.pages[index])
        }

        if (!finalPage) {
            TextButton(onClick = onClose, modifier = Modifier.align(Alignment.TopStart).padding(top = 8.dp)) {
                Text("Fechar", color = CaixaDSColorTokens.LegacyTutorialGray)
            }
        }
        TextButton(onClick = onSecondary, modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp)) {
            Text(tutorialSecondaryLabel(definition, state.page), color = CaixaDSColorTokens.LegacyTutorialGray)
        }

        tutorialPrimaryLabel(definition, state.page)?.let { label ->
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = onPrimary,
                    modifier = Modifier.width(164.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = actionColor),
                ) {
                    Text(label, color = CaixaDSColorTokens.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                }
                if (finalPage) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = state.doNotShowAgain, onCheckedChange = { onToggleDoNotShowAgain() })
                        Text(
                            "Não exibir novamente",
                            color = if (state.spec.route == "betting/tutorial") actionColor else CaixaDSColorTokens.White,
                        )
                    }
                }
            }
        }

        TutorialIndicator(
            pageCount = definition.pages.size,
            selectedPage = state.page,
            selectedColor = CaixaDSColorTokens.LegacyTutorialIndicator,
            unselectedColor = CaixaDSColorTokens.LegacyTutorialIndicatorOutline,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun LottieTutorialPage(page: TutorialPage) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(page.media))
    LottieAnimation(
        composition = composition,
        modifier = Modifier.fillMaxSize(),
        iterations = if (page.loops) LottieConstants.IterateForever else 1,
        speed = page.speed,
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun SuperSevenTutorial(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    onPageSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onPrimary: () -> Unit,
    onSkip: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = state.page, pageCount = definition.pages::size)
    val superSeven = CaixaDSColorTokens.LegacySuperSete
    LaunchedEffect(state.page) {
        if (pagerState.currentPage != state.page) pagerState.animateScrollToPage(state.page)
    }
    LaunchedEffect(pagerState.currentPage) { onPageSelected(pagerState.currentPage) }

    CaixaDSLegacyScaffold(
        title = "Como jogar",
        onBack = onBack,
        gradientTopBar = false,
        topBarEndColor = superSeven,
        topBarActionWidth = 72.dp,
        topBarAction = {
            TextButton(onClick = onSkip) {
                Text("Pular", color = CaixaDSColorTokens.White, fontWeight = FontWeight.Bold)
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(bottom = 124.dp),
            ) { index ->
                SuperSevenPage(definition.pages[index], superSeven)
            }
            Button(
                onClick = onPrimary,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 52.dp).width(184.dp).height(64.dp),
                shape = RoundedCornerShape(0.dp),
                colors = if (state.page == definition.pages.lastIndex) {
                    ButtonDefaults.buttonColors(containerColor = superSeven, contentColor = CaixaDSColorTokens.White)
                } else {
                    ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.White, contentColor = superSeven)
                },
                border = if (state.page == definition.pages.lastIndex) null else BorderStroke(1.dp, superSeven),
            ) {
                Text(tutorialPrimaryLabel(definition, state.page).orEmpty(), fontWeight = FontWeight.Bold)
            }
            TutorialIndicator(
                pageCount = definition.pages.size,
                selectedPage = state.page,
                selectedColor = CaixaDSColorTokens.LegacySuperSeteIndicator,
                unselectedColor = superSeven,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun SuperSevenPage(page: TutorialPage, accent: Color) {
    val context = LocalContext.current
    val drawableId = context.resources.getIdentifier(page.media, "drawable", context.packageName)
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 40.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            page.heading.orEmpty(),
            color = if (page.media == "super7_passo2") accent else CaixaDS.colors.legacyText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Text(page.description.orEmpty(), color = CaixaDS.colors.legacyText, textAlign = TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f).height(1.dp).background(CaixaDSColorTokens.LegacyTutorialDivider))
            Text("Veja como Apostar", Modifier.padding(horizontal = 5.dp), color = CaixaDSColorTokens.LegacyTutorialDivider)
            Spacer(Modifier.weight(1f).height(1.dp).background(CaixaDSColorTokens.LegacyTutorialDivider))
        }
        Spacer(Modifier.height(20.dp))
        AsyncImage(
            model = drawableId,
            contentDescription = "Demonstração animada: ${page.heading}",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
        page.footer?.let {
            Text(it, Modifier.padding(30.dp), color = CaixaDS.colors.legacyText, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TutorialIndicator(
    pageCount: Int,
    selectedPage: Int,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    unselectedColor: Color = CaixaDSColorTokens.LegacyTutorialIndicatorOutline,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pageCount) { index ->
            val selected = index == selectedPage
            Box(
                Modifier
                    .size(10.dp)
                    .then(
                        if (selected) Modifier.background(selectedColor, CircleShape)
                        else Modifier.border(1.dp, unselectedColor, CircleShape),
                    ),
            )
        }
    }
}
