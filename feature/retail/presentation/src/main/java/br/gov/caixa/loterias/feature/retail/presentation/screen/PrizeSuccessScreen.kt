package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSMercadoPagoBrand
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.components.LegacyRedemptionActionButton
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeHelpAction
import br.gov.caixa.loterias.feature.retail.presentation.components.legacyRedemptionGradient
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

@Composable
internal fun PrizeSuccessDataScreen(
    state: PrizeRedemptionUiState,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    onReceipt: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val isPix = state.selectedChannel == PrizeRedemptionChannel.PIX
    BackHandler(onBack = if (isPix) onMyBets else onHome)
    if (isPix) {
        CaixaDSScaffold(
            title = uiStrings.getString(LocalizedR.string.ui_resgate_de_premio_via_pix_42150a),
            onBack = null,
            topBarAction = { PrizeHelpAction(onHelp) },
            containerColor = CaixaDSColorTokens.White,
            modifier = Modifier.uiId("retail.0113")
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)
                    .uiId("retail.0114").verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CaixaDSPixBrand((Modifier.padding(top = 40.dp).size(145.dp, 52.dp)).uiId("retail.0115"))
                LegacyRedemptionSuccessBanner(
                    modifier = (Modifier.padding(top = 35.dp)).uiId("retail.0116"),
                    text = uiStrings.getString(LocalizedR.string.ui_seu_resgate_foi_solicitado_com_suces_7f6a39),
                    fontSize = 20.sp
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_voce_pode_acompanhar_a_situacao_do_r_ef1451),
                    (Modifier.padding(horizontal = 16.dp, vertical = 24.dp)).uiId("retail.0117"),
                    textAlign = TextAlign.Center,
                    color = CaixaDS.colors.mutedText,
                    fontSize = 20.sp,
                    lineHeight = 25.sp
                )
                LegacyRedemptionActionButton(
                    uiStrings.getString(LocalizedR.string.ui_acesse_minhas_apostas_85c594),
                    onMyBets,
                    (Modifier.padding(top = 24.dp)).uiId("retail.0118"),
                    color = CaixaDSColorTokens.RedemptionButton,
                    cornerRadius = 4
                )
            }
        }
    } else {
        CaixaDSScaffold(
            title = uiStrings.getString(LocalizedR.string.ui_resgate_de_premio_via_mercado_pago_a5b5dc),
            onBack = null,
            topBarAction = {
                IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0119")) {
                    Icon(
                        CaixaDSIcons.Help,
                        contentDescription = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
                        tint = CaixaDSColorTokens.White,
                        modifier = (Modifier.size(24.dp)).uiId("retail.0120")
                    )
                }
            },

            containerColor = CaixaDSColorTokens.White,
            bottomBar = {
                Box(Modifier.fillMaxWidth().height(208.dp)) {
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(176.dp)
                            .background(CaixaDSColorTokens.MercadoPagoFooter)
                    )
                    LegacyRedemptionActionButton(
                        text = uiStrings.getString(LocalizedR.string.ui_voltar_ao_inicio_372787),
                        onClick = onHome,
                        modifier = (Modifier.align(Alignment.TopCenter)).uiId("retail.0121"),
                        color = CaixaDS.colors.teal
                    )
                    LegacyRedemptionActionButton(
                        text = uiStrings.getString(LocalizedR.string.ui_resgatar_novo_premio_4ed587),
                        onClick = onMyBets,
                        modifier = (Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)).uiId("retail.0122"),
                        color = CaixaDS.colors.teal
                    )
                }
            },
            modifier = Modifier.uiId("retail.0123")
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)
                    .uiId("retail.0124").verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CaixaDSMercadoPagoBrand((Modifier.padding(top = 45.dp).size(177.dp, 46.dp)).uiId("retail.0125"))
                LegacyRedemptionSuccessBanner(
                    modifier = (Modifier.padding(top = 40.dp)).uiId("retail.0126"),
                    text = uiStrings.getString(LocalizedR.string.ui_solicitacao_efetuada_com_sucesso_621a55),
                    fontSize = 18.2.sp
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_em_breve_seu_premio_estara_disponive_735f6c),
                    (Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp)).uiId("retail.0127"),
                    textAlign = TextAlign.Center,
                    color = CaixaDS.colors.mutedText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_um_e_mail_foi_enviado_para_1_s_com_o_91929e, state.email),
                    (Modifier.padding(start = 16.dp, end = 16.dp, top = 19.dp)).uiId("retail.0128"),
                    textAlign = TextAlign.Center,
                    color = CaixaDS.colors.mutedText
                )
                Box(
                    Modifier.fillMaxWidth().padding(top = 30.dp, bottom = 20.dp)
                        .background(legacyRedemptionGradient()).uiId("retail.0129").clickable(onClick = onReceipt)
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_visualizar_comprovante_de_premio_3a10c0),
                        color = CaixaDSColorTokens.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.uiId("retail.0130")
                    )
                }
            }
        }
    }
}

@Composable
private fun LegacyRedemptionSuccessBanner(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.2.sp,
) {
    Box(
        modifier.fillMaxWidth().height(80.dp).background(legacyRedemptionGradient()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = CaixaDSColorTokens.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            modifier = Modifier.uiId("retail.0131")
        )
    }
}
