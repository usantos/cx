package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition

@Composable
internal fun SuperSevenTutorial(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    onPageSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onPrimary: () -> Unit,
    onSkip: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val pagerState = rememberPagerState(initialPage = state.page, pageCount = definition.pages::size)
    val superSeven = LoteriasCaixaDSColorTokens.SuperSeteAccent
    LaunchedEffect(state.page) {
        if (pagerState.currentPage != state.page) pagerState.animateScrollToPage(state.page)
    }
    LaunchedEffect(pagerState.currentPage) { onPageSelected(pagerState.currentPage) }

    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_como_jogar_ceb452),
        onBack = onBack,
        gradientTopBar = false,
        topBarEndColor = superSeven,
        topBarActionWidth = ToolbarActionWidth,
        topBarAction = {
            TextButton(onClick = onSkip, modifier = Modifier.uiId("bet.0170")) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_pular_d2ad99),
                    color = LoteriasCaixaDSColorTokens.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("bet.0171")
                )
            }
        },
        modifier = Modifier.uiId("bet.0172")
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(bottom = PagerBottomInset)
            ) { index ->
                SuperSevenPage(definition.pages[index], superSeven)
            }
            SuperSevenPrimaryAction(state, definition, superSeven, onPrimary)
            TutorialIndicator(
                pageCount = definition.pages.size,
                selectedPage = state.page,
                selectedColor = LoteriasCaixaDSColorTokens.SuperSeteIndicator,
                unselectedColor = superSeven,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = ContentInset)
            )
        }
    }
}

private val PagerBottomInset = 124.dp
private val ContentInset = 16.dp
private val ToolbarActionWidth = 72.dp
