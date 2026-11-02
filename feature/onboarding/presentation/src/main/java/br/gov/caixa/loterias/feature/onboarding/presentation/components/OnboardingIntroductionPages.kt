package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLoteriasLogo
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR
import br.gov.caixa.loterias.feature.onboarding.presentation.formatting.localizedMessage as messageText

internal data class IntroPage(
    val icon: ImageVector,
    @StringRes val iconDescription: Int,
    val title: String,
    val message: String,
    val titleFontFamily: androidx.compose.ui.text.font.FontFamily,
    val titleSize: androidx.compose.ui.unit.TextUnit,
)

internal val introPages = listOf(
    IntroPage(
        LoteriasCaixaDSIcons.IntroPhone,
        LocalizedR.string.intro_illustration_phone,
        messageText("uma_nova_experiencia_1b5282"),
        messageText("todas_as_apostas_na_palma_da_sua_8f3184"),
        LoteriasCaixaDSFontFamily,
        20.sp
    ),
    IntroPage(
        LoteriasCaixaDSIcons.IntroStar,
        LocalizedR.string.intro_illustration_star,
        "Novas funcionalidades",
        messageText("suas_apostas_ainda_mais_faceis_e_dae5cf"),
        LoteriasCaixaDSFuturaFontFamily,
        18.sp
    ),
    IntroPage(
        LoteriasCaixaDSIcons.IntroManageSearch,
        LocalizedR.string.intro_illustration_search,
        "Mais praticidade",
        messageText("acompanhe_e_gerencie_todas_as_su_4b243f"),
        LoteriasCaixaDSFuturaFontFamily,
        18.sp
    )
)

@Composable
internal fun LegacyIntroPage(page: IntroPage, scrollable: Boolean = false) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        LoteriasCaixaDSLoteriasLogo(
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
        tint = LoteriasCaixaDSColorTokens.White,
        modifier = (Modifier.size(width = 161.dp, height = 70.dp)).uiId("start.0017")
    )
    Spacer(Modifier.height(16.dp))
    Box(Modifier.width(125.dp).height(1.dp).background(LoteriasCaixaDSColorTokens.White))
    Text(
        text = page.title,
        modifier = (Modifier.padding(top = 18.dp)).uiId("start.0018"),
        color = LoteriasCaixaDSColorTokens.White,
        fontFamily = page.titleFontFamily,
        fontSize = page.titleSize,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp
    )
    Text(
        text = page.message,
        modifier = (Modifier.padding(top = 20.dp).width(320.dp)).uiId("start.0019"),
        color = LoteriasCaixaDSColorTokens.White,
        fontFamily = LoteriasCaixaDSRegularFontFamily,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        textAlign = TextAlign.Center
    )
}
