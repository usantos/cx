package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.ErrorState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.LoadingState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.SocialBlock
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.SocialCategoryData
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.SocialCategoryPair
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.currentSocialYear
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.formatCurrency
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.socialAmount
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.socialTransferredAmount
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun SocialTransfersScreen(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit, onOpenTerms: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_repasses_sociais_f44285),
        onBack = { onAction(ResponsibleGamingAction.Back) },
        gradientTopBar = false,
        topBarEndColor = CaixaDSColorTokens.Blue700,

        topBarActionWidth = 65.dp,
        topBarAction = {
            Image(
                painter = painterResource(R.drawable.icon_duvida2),
                contentDescription = uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                modifier = Modifier.size(25.dp).uiId("rg.0048").clickable(onClick = onOpenTerms),
                contentScale = ContentScale.Fit
            )
        },
        modifier = Modifier.uiId("rg.0049")
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding)) { LoadingState() }

            state.errorMessage != null -> Box(Modifier.fillMaxSize().padding(padding)) {
                ErrorState(state.errorMessage) { onAction(ResponsibleGamingAction.Retry) }
            }

            else -> SocialTransfersContent(state, padding) {
                onAction(ResponsibleGamingAction.Back)
            }
        }
    }
}

@Composable
private fun SocialTransfersContent(
    state: ResponsibleGamingUiState,
    padding: androidx.compose.foundation.layout.PaddingValues,
    onBack: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val transfers = state.socialTransfers
    var transferredAmount by remember(transfers) { mutableStateOf(socialTransferredAmount(transfers)) }
    LaunchedEffect(transfers) {
        while (isActive) {
            transferredAmount = socialTransferredAmount(transfers)
            delay(1_000)
        }
    }
    Column(
        Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)
            .uiId("rg.0050").verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(listOf(CaixaDS.colors.headerStart, CaixaDS.colors.headerEnd))
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_em_1_s_as_loterias_caixa_ja_destinar_fce192, currentSocialYear().toString()),
                modifier = (Modifier.padding(top = 40.dp)).uiId("rg.0051"),
                color = CaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                formatCurrency(transferredAmount),
                (Modifier.padding(top = 15.dp)).uiId("rg.0052"),
                color = CaixaDSColorTokens.SocialLime,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_para_melhorar_o_pais_85a746),
                (Modifier.padding(top = 18.dp, bottom = 40.dp)).uiId("rg.0053"),
                color = CaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            // Iguala a coluna mais alta como em cardsContainer legado com wrap_content.
            Modifier.fillMaxWidth().heightIn(min = 130.dp).height(IntrinsicSize.Min).background(
                Brush.horizontalGradient(listOf(CaixaDS.colors.headerStart, CaixaDS.colors.headerEnd))
            )
        ) {
            SocialBlock(
                first = uiStrings.getString(LocalizedR.string.ui_para_cada_e80c97),
                value = "R$1,00",
                description = uiStrings.getString(LocalizedR.string.ui_apostados_nas_loterias_caixa_2ebefb),
                background = CaixaDSColorTokens.SocialPanelBlue,
                foreground = CaixaDSColorTokens.White,
                valueColor = CaixaDSColorTokens.SocialPanelLime,
                backgroundResource = R.drawable.repassequadro1,
                modifier = (Modifier.weight(1f)).uiId("rg.0054")
            )
            SocialBlock(
                first = "",
                value = "R$0,48*",
                description = uiStrings.getString(LocalizedR.string.ui_sao_destinados_a_programas_sociais_25cae5),
                background = CaixaDSColorTokens.SocialPanelLime,
                foreground = CaixaDS.colors.text,
                valueColor = CaixaDSColorTokens.SocialPanelBlue,
                backgroundResource = R.drawable.repassequadro2,
                modifier = (Modifier.weight(1f)).uiId("rg.0055")
            )
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_o_valor_inclui_a_parcela_corresponde_707993),
            (Modifier.fillMaxWidth().background(CaixaDS.colors.filterLine).padding(16.dp)).uiId("rg.0056"),
            color = CaixaDSColorTokens.SocialGraphite,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Image(
            painterResource(R.drawable.ilustra_repasses),
            null,
            Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp).size(50.dp),
            contentScale = ContentScale.Fit
        )
        Box(
            Modifier.align(Alignment.CenterHorizontally).padding(top = 15.dp).width(230.dp).height(2.dp).background(CaixaDS.colors.line)
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_entenda_como_2905aa),
            (Modifier.fillMaxWidth().padding(top = 17.dp)).uiId("rg.0057"),
            color = CaixaDS.colors.brand,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_esse_dinheiro_e_distribuido_cbd1af),
            (Modifier.fillMaxWidth()).uiId("rg.0058"),
            color = CaixaDS.colors.brand,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        SocialCategoryPair(
            left = SocialCategoryData(
                uiStrings.getString(LocalizedR.string.ui_educacao_620915),
                transfers?.let { socialAmount(it.amountFor(it.educationPercentage)) },
                CaixaDSColorTokens.SocialEducation
            ),
            right = SocialCategoryData(
                "Cultura",
                transfers?.let {
                    socialAmount(it.amountFor(it.culturePercentage))
                },
                CaixaDSColorTokens.SocialCulture
            ),
            modifier = (Modifier.padding(top = 23.dp)).uiId("rg.0059")
        )
        SocialCategoryPair(
            left = SocialCategoryData(
                "Esporte",
                transfers?.let {
                    socialAmount(it.amountFor(it.sportPercentage))
                },
                CaixaDSColorTokens.SocialSport
            ),
            right = SocialCategoryData(
                uiStrings.getString(LocalizedR.string.ui_seguranca_d5668a),
                transfers?.let { socialAmount(it.amountFor(it.securityPercentage)) },
                CaixaDSColorTokens.SocialSecurity
            ),
            modifier = Modifier.uiId("rg.0060")
        )
        SocialCategoryPair(
            left = SocialCategoryData(
                uiStrings.getString(LocalizedR.string.ui_seguridade_social_6f2d6c),
                transfers?.let { socialAmount(it.amountFor(it.socialSecurityPercentage)) },
                CaixaDSColorTokens.SocialSecuritySystem
            ),
            right = SocialCategoryData(
                "Outros",
                transfers?.let {
                    socialAmount(it.amountFor(it.otherPercentage))
                },
                CaixaDSColorTokens.SocialOther
            ),
            modifier = Modifier.uiId("rg.0061")
        )
        Box(
            Modifier.fillMaxWidth().height(180.dp).background(CaixaDSColorTokens.White),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(Modifier.fillMaxWidth().height(100.dp).background(CaixaDSColorTokens.SocialCanvas))
            Button(
                onClick = onBack,
                modifier = (Modifier.padding(bottom = 43.dp).width(206.dp).heightIn(min = 52.dp)).uiId("rg.0062"),
                shape = RoundedCornerShape(4.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CaixaDSColorTokens.SocialGraphite,
                    contentColor = CaixaDSColorTokens.White
                )
            ) {
                Text(
                    androidx.compose.ui.res.stringResource(
                        br.gov.caixa.loterias.feature.responsiblegaming.R.string.social_transfers_bet_action
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.uiId("rg.0063")
                )
            }
        }
    }
}
