package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSMercadoPagoBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.components.legacyRedemptionGradient
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

@Composable
internal fun PrizeMercadoPagoSuccessScreen(
    state: PrizeRedemptionUiState,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    onReceipt: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resgate_de_premio_via_mercado_pago_a5b5dc),
        onBack = null,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0119")) {
                Icon(
                    LoteriasCaixaDSIcons.Help,
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
                    tint = LoteriasCaixaDSColorTokens.White,
                    modifier = (Modifier.size(24.dp)).uiId("retail.0120")
                )
            }
        },

        containerColor = LoteriasCaixaDSColorTokens.White,
        bottomBar = {
            PrizeSuccessFooter(onHome, onMyBets)
        },
        modifier = Modifier.uiId("retail.0123")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)
                .uiId("retail.0124").verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoteriasCaixaDSMercadoPagoBrand((Modifier.padding(top = 45.dp).size(177.dp, 46.dp)).uiId("retail.0125"))
            LegacyRedemptionSuccessBanner(
                modifier = (Modifier.padding(top = 40.dp)).uiId("retail.0126"),
                text = uiStrings.getString(LocalizedR.string.ui_solicitacao_efetuada_com_sucesso_621a55),
                fontSize = 18.2.sp
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_em_breve_seu_premio_estara_disponive_735f6c),
                (Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp)).uiId("retail.0127"),
                textAlign = TextAlign.Center,
                color = LoteriasCaixaDS.colors.mutedText,
                fontWeight = FontWeight.Bold
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_um_e_mail_foi_enviado_para_1_s_com_o_91929e, state.email),
                (Modifier.padding(start = 16.dp, end = 16.dp, top = 19.dp)).uiId("retail.0128"),
                textAlign = TextAlign.Center,
                color = LoteriasCaixaDS.colors.mutedText
            )
            Box(
                Modifier.fillMaxWidth().padding(top = 30.dp, bottom = 20.dp)
                    .background(legacyRedemptionGradient()).uiId("retail.0129").clickable(onClick = onReceipt)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_visualizar_comprovante_de_premio_3a10c0),
                    color = LoteriasCaixaDSColorTokens.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("retail.0130")
                )
            }
        }
    }
}
