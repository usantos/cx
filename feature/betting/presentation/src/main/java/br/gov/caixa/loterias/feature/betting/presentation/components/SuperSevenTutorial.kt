package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialPrimaryLabel
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialPage
import coil3.compose.AsyncImage

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
    val superSeven = CaixaDSColorTokens.SuperSeteAccent
    LaunchedEffect(state.page) {
        if (pagerState.currentPage != state.page) pagerState.animateScrollToPage(state.page)
    }
    LaunchedEffect(pagerState.currentPage) { onPageSelected(pagerState.currentPage) }

    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_como_jogar_ceb452),
        onBack = onBack,
        gradientTopBar = false,
        topBarEndColor = superSeven,
        topBarActionWidth = 72.dp,
        topBarAction = {
            TextButton(onClick = onSkip, modifier = Modifier.uiId("bet.0170")) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_pular_d2ad99),
                    color = CaixaDSColorTokens.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("bet.0171")
                )
            }
        },
        modifier = Modifier.uiId("bet.0172")
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(bottom = 124.dp)
            ) { index ->
                SuperSevenPage(definition.pages[index], superSeven)
            }
            Button(
                onClick = onPrimary,
                modifier = (Modifier.align(Alignment.BottomCenter).padding(bottom = 52.dp).width(184.dp).height(64.dp)).uiId("bet.0173"),
                shape = RoundedCornerShape(0.dp),
                colors = if (state.page == definition.pages.lastIndex) {
                    ButtonDefaults.buttonColors(containerColor = superSeven, contentColor = CaixaDSColorTokens.White)
                } else {
                    ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.White, contentColor = superSeven)
                },
                border = if (state.page == definition.pages.lastIndex) null else BorderStroke(1.dp, superSeven)
            ) {
                Text(
                    tutorialPrimaryLabel(definition, state.page).orEmpty(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .uiId("bet.0174")
                )
            }
            TutorialIndicator(
                pageCount = definition.pages.size,
                selectedPage = state.page,
                selectedColor = CaixaDSColorTokens.SuperSeteIndicator,
                unselectedColor = superSeven,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun SuperSevenPage(page: TutorialPage, accent: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val context = LocalContext.current
    val drawableId = context.resources.getIdentifier(page.media, "drawable", context.packageName)
    Column(
        modifier = Modifier.fillMaxSize()
            .uiId("bet.0175").verticalScroll(rememberScrollState()).padding(horizontal = 40.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            page.heading.orEmpty(),
            color = if (page.media == "super7_passo2") accent else CaixaDS.colors.text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("bet.0176")
        )
        Spacer(Modifier.height(20.dp))
        Text(page.description.orEmpty(), color = CaixaDS.colors.text, textAlign = TextAlign.Center, modifier = Modifier.uiId("bet.0177"))
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f).height(1.dp).background(CaixaDSColorTokens.TutorialDivider))
            Text(
                uiStrings.getString(LocalizedR.string.ui_veja_como_apostar_8b34ac),
                (Modifier.padding(horizontal = 5.dp)).uiId("bet.0178"),
                color = CaixaDSColorTokens.TutorialDivider
            )
            Spacer(Modifier.weight(1f).height(1.dp).background(CaixaDSColorTokens.TutorialDivider))
        }
        Spacer(Modifier.height(20.dp))
        AsyncImage(
            model = drawableId,
            contentDescription = uiStrings.getString(LocalizedR.string.ui_demonstracao_animada_1_s_cb7005, page.heading),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        page.footer?.let {
            Text(it, (Modifier.padding(30.dp)).uiId("bet.0179"), color = CaixaDS.colors.text, textAlign = TextAlign.Center)
        }
    }
}
