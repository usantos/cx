package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeHelpAction
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizePixActionButton
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizePixKeyChoice
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizePixKeyConfirmation
import br.gov.caixa.loterias.feature.retail.presentation.mapper.canContinuePix
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixActions
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun PrizePixDataScreen(state: PrizeRedemptionUiState, onBack: () -> Unit, onHelp: () -> Unit, actions: PrizePixActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resgate_de_premio_via_pix_42150a),
        onBack = onBack,
        topBarAction = { PrizeHelpAction(onHelp) },

        containerColor = CaixaDS.colors.paymentCanvas,
        modifier = Modifier.uiId("retail.0100")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.paymentCanvas)
                .uiId("retail.0101").verticalScroll(rememberScrollState())
        ) {
            if (state.stage == PrizeRedemptionStage.PIX_ENTRY) {
                PrizePixKeyChoice(state, actions)
            } else {
                PrizePixKeyConfirmation(requireNotNull(state.pixBeneficiary))
            }
            Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White)) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_valor_do_credito_ceefea),
                    modifier = (Modifier.padding(start = 20.dp, top = 16.dp)).uiId("retail.0102"),
                    color = CaixaDSColorTokens.PrizeText,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CaixaDSFuturaFontFamily,
                    fontSize = 14.sp
                )
                Text(
                    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(state.details?.netValue),
                    (Modifier.padding(start = 20.dp, top = 16.dp))
                        .uiId("retail.0103"),
                    fontWeight = FontWeight.Bold,
                    fontFamily = CaixaDSFuturaFontFamily,
                    fontSize = 14.sp
                )
                Row(Modifier.padding(start = 20.dp, top = 16.dp, bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_resgate_processado_via_336deb),
                        color = CaixaDSColorTokens.PrizeText,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CaixaDSFuturaFontFamily,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .uiId("retail.0104")
                    )
                    CaixaDSPixBrand((Modifier.padding(start = 16.dp).size(100.dp, 36.dp)).uiId("retail.0105"))
                }
                Row(
                    Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    PrizePixActionButton(text = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb), onClick = {
                        if (state.stage == PrizeRedemptionStage.PIX_CONFIRM) {
                            actions.cancelPixAndExit()
                        } else {
                            onBack()
                        }
                    }, modifier = (Modifier.weight(1f)).uiId("retail.0106"), outlined = true)
                    PrizePixActionButton(
                        text = if (state.stage == PrizeRedemptionStage.PIX_ENTRY) {
                            uiStrings.getString(LocalizedR.string.ui_continuar_63e2bb)
                        } else {
                            uiStrings.getString(LocalizedR.string.ui_resgatar_bf428f)
                        },
                        onClick = if (state.stage == PrizeRedemptionStage.PIX_ENTRY) {
                            ({ actions.consultPix() })
                        } else {
                            ({ actions.confirmPix() })
                        },
                        modifier = (Modifier.weight(1f)).uiId("retail.0107"),
                        enabled = state.stage == PrizeRedemptionStage.PIX_CONFIRM || state.canContinuePix()
                    )
                }
            }
        }
    }
}
