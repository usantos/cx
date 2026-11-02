package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.components.LegacyRedemptionActionButton
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeHelpAction

@Composable
internal fun PrizePixSuccessScreen(onHelp: () -> Unit, onMyBets: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resgate_de_premio_via_pix_42150a),
        onBack = null,
        topBarAction = { PrizeHelpAction(onHelp) },
        containerColor = LoteriasCaixaDSColorTokens.White,
        modifier = Modifier.uiId("retail.0113")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)
                .uiId("retail.0114").verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoteriasCaixaDSPixBrand((Modifier.padding(top = 40.dp).size(145.dp, 52.dp)).uiId("retail.0115"))
            LegacyRedemptionSuccessBanner(
                modifier = (Modifier.padding(top = 35.dp)).uiId("retail.0116"),
                text = uiStrings.getString(LocalizedR.string.ui_seu_resgate_foi_solicitado_com_suces_7f6a39),
                fontSize = 20.sp
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_voce_pode_acompanhar_a_situacao_do_r_ef1451),
                (Modifier.padding(horizontal = 16.dp, vertical = 24.dp)).uiId("retail.0117"),
                textAlign = TextAlign.Center,
                color = LoteriasCaixaDS.colors.mutedText,
                fontSize = 20.sp,
                lineHeight = 25.sp
            )
            LegacyRedemptionActionButton(
                uiStrings.getString(LocalizedR.string.ui_acesse_minhas_apostas_85c594),
                onMyBets,
                (Modifier.padding(top = 24.dp)).uiId("retail.0118"),
                color = LoteriasCaixaDSColorTokens.RedemptionButton,
                cornerRadius = PIX_SUCCESS_BUTTON_RADIUS
            )
        }
    }
}

private const val PIX_SUCCESS_BUTTON_RADIUS = 4
