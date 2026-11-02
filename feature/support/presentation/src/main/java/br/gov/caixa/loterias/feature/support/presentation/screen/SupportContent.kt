package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LoteriasCaixaDSR
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSSupportTopBar
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.support.R as LocalizedR
import br.gov.caixa.loterias.feature.support.presentation.components.SupportTabs
import br.gov.caixa.loterias.feature.support.presentation.mapper.legacyCapitalized
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Composable
internal fun SupportContent(state: SupportUiState, onAction: (SupportAction) -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = when {
            state.selectedSectionId != null -> state.selectedSectionTitle.legacyCapitalized()
            state.acceptanceRequired || state.showTermsTabs -> uiStrings.getString(LocalizedR.string.ui_loterias_caixa_02d076)
            else -> uiStrings.getString(LocalizedR.string.ui_duvidas_10e026)
        },
        onBack = { onAction(SupportAction.Back) },
        modifier = (modifier.fillMaxSize().legacyAutomationId("support_screen")).uiId("help.0020"),
        containerColor = LoteriasCaixaDSColorTokens.White,
        gradientTopBar = false,
        topBarEndColor = LoteriasCaixaDSColorTokens.Blue700,
        customTopBar = if (state.selectedSectionId != null || (!state.showTermsTabs && !state.acceptanceRequired)) {
            {
                LoteriasCaixaDSSupportTopBar(
                    if (
                        state.selectedSectionId != null
                    ) {
                        state.selectedSectionTitle.legacyCapitalized()
                    } else {
                        uiStrings.getString(
                            LocalizedR.string.ui_duvidas_10e026
                        )
                    },
                    { onAction(SupportAction.Back) }
                )
            }
        } else {
            null
        },
        bottomBar = {
            SupportTermsFooter(state, onAction)
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (state.showTermsTabs && state.selectedSectionId == null) {
                SupportTabs(state.selectedTab, onAction)
            }
            when {
                state.errorMessage != null -> Text(
                    text = state.errorMessage,
                    color = LoteriasCaixaDS.colors.error,
                    modifier = (Modifier.padding(16.dp)).uiId("help.0025")
                )

                state.selectedTab == SupportTab.FAQ -> FaqContent(state, onAction)

                else -> HtmlContent(state.htmlContent)
            }
        }
    }
}

@Composable
private fun SupportTermsFooter(state: SupportUiState, onAction: (SupportAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.acceptanceRequired && state.selectedTab == SupportTab.TERMS) {
        Box(Modifier.fillMaxWidth().height(131.dp).background(LoteriasCaixaDSColorTokens.White)) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.DrawerDivider))
            Row(
                modifier = Modifier.padding(start = 17.dp, top = 10.dp).uiId("help.0021").toggleable(
                    value = state.termsAccepted,
                    role = Role.Checkbox,
                    onValueChange = { onAction(SupportAction.ToggleTermsAcceptance) }
                ),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painterResource(
                        if (state.termsAccepted) LoteriasCaixaDSR.drawable.legacy_check2 else LoteriasCaixaDSR.drawable.legacy_check1
                    ),
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_aceitar_termos_de_uso_eeb4ca),
                    modifier = Modifier.padding(top = 6.dp).size(20.dp)
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_declaro_ter_lido_e_concordado_com_os_44f77b),
                    (Modifier.padding(start = 20.dp)).uiId("help.0022"),
                    color = LoteriasCaixaDS.colors.graphite,
                    style = TextStyle(
                        fontFamily = LoteriasCaixaDSRegularFontFamily,
                        fontSize = 15.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    )
                )
            }
            Button(
                onClick = { onAction(SupportAction.AcceptTerms) },
                enabled = state.termsAccepted,
                modifier = (Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 55.dp).height(56.dp))
                    .uiId("help.0023"),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoteriasCaixaDSColorTokens.YellowAction,
                    contentColor = LoteriasCaixaDSColorTokens.White
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_continuar_63e2bb),
                    style = TextStyle(
                        fontFamily = LoteriasCaixaDSFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    ),
                    modifier = Modifier
                        .uiId("help.0024")
                )
            }
        }
    }
}
