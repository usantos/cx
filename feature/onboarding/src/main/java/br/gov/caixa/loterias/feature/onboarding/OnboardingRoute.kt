package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLoteriasLogo
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyBrandTopBar
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyTutorialCheckbox
import br.gov.caixa.loterias.core.designsystem.R
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onUnderage: () -> Unit,
    onResponsibleGaming: () -> Unit,
    onStartBettingTutorial: () -> Unit,
    onSkipBettingTutorial: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.Completed -> onComplete()
                OnboardingEffect.Underage -> onUnderage()
                OnboardingEffect.StartBettingTutorial -> onStartBettingTutorial()
                OnboardingEffect.SkipBettingTutorial -> onSkipBettingTutorial()
            }
        }
    }
    when (state.route) {
        "onboarding/introduction" -> IntroductionScreen(state, viewModel)
        "onboarding/tutorial" -> TutorialScreen(
            checked = state.tutorialDoNotShowAgain,
            onToggle = viewModel::toggleTutorialDoNotShowAgain,
            onStart = viewModel::startBettingTutorial,
            onSkip = viewModel::skipBettingTutorial,
        )
        "onboarding/age" -> AgeScreen(onBack, onResponsibleGaming)
        "onboarding/splash" -> LegacySplashScreen()
        else -> error("Rota inicial sem layout Compose: ${state.route}")
    }
}

private data class IntroPage(
    val icon: ImageVector,
    val title: String,
    val message: String,
    val titleFontFamily: androidx.compose.ui.text.font.FontFamily,
    val titleSize: androidx.compose.ui.unit.TextUnit,
)

private val introPages = listOf(
    IntroPage(
        CaixaDSIcons.IntroPhone,
        "Uma nova experiência",
        "Todas as apostas na palma da sua mão, o dia inteiro, todos os dias. ;)",
        CaixaDSFontFamily,
        20.sp,
    ),
    IntroPage(
        CaixaDSIcons.IntroStar,
        "Novas funcionalidades",
        "Suas apostas ainda mais fáceis e muito mais rápidas.",
        CaixaDSLegacyFuturaFontFamily,
        18.sp,
    ),
    IntroPage(
        CaixaDSIcons.IntroManageSearch,
        "Mais praticidade",
        "Acompanhe e gerencie todas as suas apostas.",
        CaixaDSLegacyFuturaFontFamily,
        18.sp,
    ),
)

@Composable
private fun IntroductionScreen(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val pagerState = rememberPagerState(initialPage = state.introductionPage, pageCount = introPages::size)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect(viewModel::pageChanged)
    }
    Box(
        Modifier.fillMaxSize().background(
            Brush.horizontalGradient(listOf(CaixaDS.colors.legacyHeaderStart, CaixaDS.colors.legacyHeaderEnd)),
        ),
    ) {
        LegacyIntroBackground()
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().semantics {
                contentDescription = "Página ${state.introductionPage + 1} de ${introPages.size}"
            },
        ) { page ->
            LegacyIntroPage(introPages[page])
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(140.dp))
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                introPages.indices.forEach { index ->
                    Box(
                        Modifier.padding(horizontal = 3.dp).size(12.dp).border(
                            width = 1.dp,
                            color = CaixaDSColorTokens.White,
                            shape = CircleShape,
                        ).background(
                            if (index == pagerState.currentPage) CaixaDSColorTokens.White else CaixaDSColorTokens.Transparent,
                            CircleShape,
                        ),
                    )
                }
            }
            Box(
                Modifier.size(width = 140.dp, height = 44.dp).clickable(onClick = viewModel::complete),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (state.introductionPage == introPages.lastIndex) "Acessar o aplicativo" else "Pular introdução",
                    color = CaixaDSColorTokens.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
    }
    if (state.ageConfirmationVisible) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Você tem mais de 18 anos?",
            positiveLabel = "Sim",
            onPositive = viewModel::confirmAdult,
            negativeLabel = "Não",
            onNegative = viewModel::rejectAdult,
        )
    }
}

@Composable
private fun LegacyIntroPage(page: IntroPage) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        CaixaDSLoteriasLogo(
            contentDescription = "Logo Loterias CAIXA",
            modifier = Modifier.padding(top = 29.dp).size(120.dp),
        )
        Spacer(Modifier.height(54.dp))
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            tint = CaixaDSColorTokens.White,
            modifier = Modifier.size(width = 161.dp, height = 70.dp),
        )
        Spacer(Modifier.height(16.dp))
        Box(Modifier.width(125.dp).height(1.dp).background(CaixaDSColorTokens.White))
        Text(
            text = page.title,
            modifier = Modifier.padding(top = 18.dp),
            color = CaixaDSColorTokens.White,
            fontFamily = page.titleFontFamily,
            fontSize = page.titleSize,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
        )
        Text(
            text = page.message,
            modifier = Modifier.padding(top = 20.dp).width(320.dp),
            color = CaixaDSColorTokens.White,
            fontFamily = CaixaDSLegacyRegularFontFamily,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TutorialScreen(
    checked: Boolean,
    onToggle: () -> Unit,
    onStart: () -> Unit,
    onSkip: () -> Unit,
) {
    val checkboxInteraction = remember { MutableInteractionSource() }
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
      Box(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
            .background(CaixaDSColorTokens.LegacyTutorialIndicator),
      ) {
        Text(
            "Chegou a +milionária",
            modifier = Modifier.align(Alignment.Center).offset(y = (-110).dp),
            color = CaixaDSColorTokens.White,
            fontFamily = CaixaDSLegacyFuturaFontFamily,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            "Conheça o novo jogo +milionária.\nVamos explicar como funciona\nnessa nova modalidade.",
            modifier = Modifier.align(Alignment.Center).offset(y = (-42).dp),
            color = CaixaDSColorTokens.White,
            fontFamily = CaixaDSLegacyFuturaFontFamily,
            fontSize = 18.sp,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center,
        )
        LegacyTutorialButton(
            text = "Começar\ntutorial",
            color = CaixaDSColorTokens.LegacyTutorialGreenButton,
            modifier = Modifier.align(Alignment.Center).offset(y = 151.dp).size(width = 180.dp, height = 84.dp),
            onClick = onStart,
        )
        Row(
            modifier = Modifier.align(Alignment.Center).offset(x = 2.dp, y = 221.dp).clickable(
                interactionSource = checkboxInteraction,
                indication = null,
                onClick = onToggle,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CaixaDSLegacyTutorialCheckbox(checked = checked, onClick = onToggle)
            Text("Não mostrar mais esse tutorial", color = CaixaDSColorTokens.White, fontSize = 14.sp)
        }
        LegacyTutorialButton(
            text = "Pular\napresentação",
            color = CaixaDSColorTokens.LegacyTutorialGray,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp).size(width = 180.dp, height = 64.dp),
            onClick = onSkip,
        )
      }
    }
}

@Composable
private fun AgeScreen(onBack: () -> Unit, onResponsibleGaming: () -> Unit) {
    Scaffold(
        containerColor = CaixaDS.colors.surface,
        topBar = { CaixaDSLegacyBrandTopBar() },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LegacyResponsibleGamingIcon(
                Modifier.align(Alignment.Center).offset(y = (-50).dp).size(100.dp),
            )
            Box(
                Modifier.align(Alignment.Center).offset(y = 24.dp).width(125.dp).height(1.dp)
                    .background(CaixaDS.colors.brand),
            )
            Text(
                "O uso do aplicativo é restrito a maiores de 18 anos.",
                modifier = Modifier.align(Alignment.Center).offset(y = 65.dp).width(347.dp).height(54.dp),
                color = CaixaDS.colors.brand,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                "Saiba mais sobre o Jogo Responsável.",
                modifier = Modifier.align(Alignment.Center).offset(y = 135.dp).width(348.dp)
                    .clickable(onClick = onResponsibleGaming),
                color = CaixaDS.colors.brand,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
            )
            LegacyFlatAction(
                text = "Entendi",
                modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth().height(56.dp),
                color = CaixaDS.colors.brand,
                onClick = onBack,
            )
        }
    }
}

@Composable
private fun LegacyFlatAction(
    text: String,
    modifier: Modifier,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = CaixaDSColorTokens.White),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LegacyTutorialButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val lines = text.split('\n', limit = 2)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier.background(color).clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                lines.first(),
                color = CaixaDSColorTokens.White,
                fontFamily = CaixaDSLegacyFuturaFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp,
            )
            if (lines.size > 1) {
                Text(
                    lines[1],
                    color = CaixaDSColorTokens.White,
                    fontFamily = CaixaDSLegacyFuturaFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}
