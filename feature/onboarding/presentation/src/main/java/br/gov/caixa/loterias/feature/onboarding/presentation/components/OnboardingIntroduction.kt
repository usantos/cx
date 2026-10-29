package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoteriasLogo
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR
import br.gov.caixa.loterias.feature.onboarding.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingUiState
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.OnboardingViewModel

private data class IntroPage(
    val icon: ImageVector,
    @StringRes val iconDescription: Int,
    val title: String,
    val message: String,
    val titleFontFamily: androidx.compose.ui.text.font.FontFamily,
    val titleSize: androidx.compose.ui.unit.TextUnit,
)

private val introPages = listOf(
    IntroPage(
        CaixaDSIcons.IntroPhone,
        LocalizedR.string.intro_illustration_phone,
        messageText("uma_nova_experiencia_1b5282"),
        messageText("todas_as_apostas_na_palma_da_sua_8f3184"),
        CaixaDSFontFamily,
        20.sp
    ),
    IntroPage(
        CaixaDSIcons.IntroStar,
        LocalizedR.string.intro_illustration_star,
        "Novas funcionalidades",
        messageText("suas_apostas_ainda_mais_faceis_e_dae5cf"),
        CaixaDSFuturaFontFamily,
        18.sp
    ),
    IntroPage(
        CaixaDSIcons.IntroManageSearch,
        LocalizedR.string.intro_illustration_search,
        "Mais praticidade",
        messageText("acompanhe_e_gerencie_todas_as_su_4b243f"),
        CaixaDSFuturaFontFamily,
        18.sp
    )
)

@Composable
internal fun IntroductionScreen(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val pagerState = rememberPagerState(initialPage = state.introductionPage, pageCount = introPages::size)
    SynchronizeIntroductionPager(pagerState, viewModel)
    val density = LocalDensity.current
    val largeFont = density.fontScale > 1f
    var footerHeight by remember { mutableIntStateOf(0) }
    Box(
        Modifier.fillMaxSize().background(
            Brush.horizontalGradient(listOf(CaixaDS.colors.headerStart, CaixaDS.colors.headerEnd))
        )
    ) {
        IntroBackground()
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .padding(bottom = if (largeFont) with(density) { footerHeight.toDp() } else 0.dp).semantics {
                    contentDescription = uiStrings.getString(
                        LocalizedR.string.ui_pagina_1_s_de_2_s_ecbc1a,
                        (state.introductionPage + 1).toString(),
                        introPages.size.toString()
                    )
                }
        ) { page ->
            LegacyIntroPage(introPages[page], scrollable = largeFont)
        }
        if (largeFont) {
            LargeFontIntroductionFooter(
                pagerState.currentPage,
                state.introductionPage == introPages.lastIndex,
                viewModel::complete,
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().onSizeChanged { footerHeight = it.height }
            )
        } else {
            LegacyIntroductionFooter(
                pagerState.currentPage,
                state.introductionPage == introPages.lastIndex,
                viewModel::complete,
                Modifier.align(Alignment.BottomCenter)
            )
        }
    }
    if (state.ageConfirmationVisible) {
        AgeConfirmationDialog(
            onConfirmAdult = viewModel::confirmAdult,
            onRejectAdult = viewModel::rejectAdult
        )
    }
}

@Composable
private fun LegacyIntroPage(page: IntroPage, scrollable: Boolean = false) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        CaixaDSLoteriasLogo(
            contentDescription = uiStrings.getString(LocalizedR.string.ui_logo_loterias_caixa_0ec4dc),
            modifier = (Modifier.padding(top = 29.dp).size(120.dp)).uiId("start.0015")
        )
        if (scrollable) {
            Column(
                Modifier.fillMaxWidth().weight(1f).uiId("start.0016").verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { LegacyIntroPageBody(page) }
        } else {
            LegacyIntroPageBody(page)
        }
    }
}

@Composable
private fun LegacyIntroPageBody(page: IntroPage) {
    Spacer(Modifier.height(54.dp))
    Icon(
        imageVector = page.icon,
        contentDescription = stringResource(page.iconDescription),
        tint = CaixaDSColorTokens.White,
        modifier = (Modifier.size(width = 161.dp, height = 70.dp)).uiId("start.0017")
    )
    Spacer(Modifier.height(16.dp))
    Box(Modifier.width(125.dp).height(1.dp).background(CaixaDSColorTokens.White))
    Text(
        text = page.title,
        modifier = (Modifier.padding(top = 18.dp)).uiId("start.0018"),
        color = CaixaDSColorTokens.White,
        fontFamily = page.titleFontFamily,
        fontSize = page.titleSize,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp
    )
    Text(
        text = page.message,
        modifier = (Modifier.padding(top = 20.dp).width(320.dp)).uiId("start.0019"),
        color = CaixaDSColorTokens.White,
        fontFamily = CaixaDSRegularFontFamily,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun LargeFontIntroductionFooter(currentPage: Int, lastPage: Boolean, onComplete: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(Modifier.legacyAutomationId("introduction-page-indicators"), horizontalArrangement = Arrangement.Center) {
            introPages.indices.forEach { index ->
                Box(
                    Modifier.padding(horizontal = 3.dp).size(12.dp).border(1.dp, CaixaDSColorTokens.White, CircleShape)
                        .background(if (index == currentPage) CaixaDSColorTokens.White else CaixaDSColorTokens.Transparent, CircleShape)
                )
            }
        }
        Box(
            Modifier.fillMaxWidth().heightIn(min = 44.dp).uiId("start.0020").clickable(role = Role.Button, onClick = onComplete)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(
                    if (lastPage) LocalizedR.string.ui_acessar_o_aplicativo_58076a else LocalizedR.string.ui_pular_introducao_24a12b
                ),
                (Modifier.fillMaxWidth()).uiId("start.0021"),
                color = CaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontFamily = CaixaDSFontFamily,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LegacyIntroductionFooter(currentPage: Int, lastPage: Boolean, onComplete: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        modifier = modifier.fillMaxWidth().navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp).height(44.dp)
    ) {
        Row(
            Modifier.align(Alignment.Center).legacyAutomationId("introduction-page-indicators"),
            horizontalArrangement = Arrangement.Center
        ) {
            introPages.indices.forEach { index ->
                Box(
                    Modifier.padding(horizontal = 3.dp).size(12.dp).border(
                        width = 1.dp,
                        color = CaixaDSColorTokens.White,
                        shape = CircleShape
                    ).background(
                        if (index == currentPage) CaixaDSColorTokens.White else CaixaDSColorTokens.Transparent,
                        CircleShape
                    )
                )
            }
        }
        Box(
            Modifier.align(Alignment.CenterEnd).padding(end = 3.dp)
                .size(width = 140.dp, height = 44.dp)
                .uiId("start.0022").clickable(role = Role.Button, onClick = onComplete),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (lastPage) {
                    uiStrings.getString(
                        LocalizedR.string.ui_acessar_o_aplicativo_58076a
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_pular_introducao_24a12b)
                },
                color = CaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontFamily = CaixaDSFontFamily,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                maxLines = 2, modifier = Modifier.uiId("start.0023")
            )
        }
    }
}
