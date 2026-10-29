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
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSSupportTopBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.support.R as LocalizedR
import br.gov.caixa.loterias.feature.support.presentation.components.SupportTabs
import br.gov.caixa.loterias.feature.support.presentation.mapper.legacyCapitalized
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Composable
internal fun SupportContent(state: SupportUiState, onAction: (SupportAction) -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = when {
            state.selectedSectionId != null -> state.selectedSectionTitle.legacyCapitalized()
            state.acceptanceRequired || state.showTermsTabs -> uiStrings.getString(LocalizedR.string.ui_loterias_caixa_02d076)
            else -> uiStrings.getString(LocalizedR.string.ui_duvidas_10e026)
        },
        onBack = { onAction(SupportAction.Back) },
        modifier = (modifier.fillMaxSize().legacyAutomationId("support_screen")).uiId("help.0020"),
        containerColor = CaixaDSColorTokens.White,
        gradientTopBar = false,
        topBarEndColor = CaixaDSColorTokens.Blue700,
        customTopBar = if (state.selectedSectionId != null || (!state.showTermsTabs && !state.acceptanceRequired)) {
            {
                CaixaDSSupportTopBar(
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
                    color = CaixaDS.colors.error,
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
        Box(Modifier.fillMaxWidth().height(131.dp).background(CaixaDSColorTokens.White)) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.DrawerDivider))
            Row(
                modifier = Modifier.padding(start = 17.dp, top = 10.dp).uiId("help.0021").toggleable(
                    value = state.termsAccepted,
                    role = Role.Checkbox,
                    onValueChange = { onAction(SupportAction.ToggleTermsAcceptance) }
                ),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painterResource(if (state.termsAccepted) CaixaDSR.drawable.legacy_check2 else CaixaDSR.drawable.legacy_check1),
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_aceitar_termos_de_uso_eeb4ca),
                    modifier = Modifier.padding(top = 6.dp).size(20.dp)
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_declaro_ter_lido_e_concordado_com_os_44f77b),
                    (Modifier.padding(start = 20.dp)).uiId("help.0022"),
                    color = CaixaDS.colors.graphite,
                    style = TextStyle(
                        fontFamily = CaixaDSRegularFontFamily,
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
                    containerColor = CaixaDSColorTokens.YellowAction,
                    contentColor = CaixaDSColorTokens.White
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_continuar_63e2bb),
                    style = TextStyle(
                        fontFamily = CaixaDSFontFamily,
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
