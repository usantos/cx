package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSMercadoPagoBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMercadoPagoPrizePanel
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseHelpIcon
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.components.LegacyRedemptionActionButton
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.ui.R as DeliveryR
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun PrizeMercadoPagoDataScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onConfirm: () -> Unit,
    onEmailChange: (String) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = LocalDensity.current.fontScale > 1f
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_resgate_de_premio_via_mercado_pago_a5b5dc),
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0081")) {
                LoteriasCaixaDSPurchaseHelpIcon((Modifier.size(24.dp)).uiId("retail.0082"))
            }
        },

        containerColor = LoteriasCaixaDSColorTokens.White,
        bottomBar = {
            Box(Modifier.fillMaxWidth().height(104.dp)) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(LoteriasCaixaDSColorTokens.MercadoPagoFooter)
                )
                LegacyRedemptionActionButton(
                    text = uiStrings.getString(DeliveryR.string.ui_resgatar_premio_e68bcd),
                    compactContent = true,
                    onClick = onConfirm,
                    modifier = (Modifier.align(Alignment.TopCenter)).uiId("retail.0083"),
                    color = LoteriasCaixaDS.colors.teal
                )
            }
        },
        modifier = Modifier.uiId("retail.0084")
    ) { padding ->
        PrizeMercadoPagoBiasedBody(
            (Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)).uiId("retail.0085")
        ) {
            LoteriasCaixaDSMercadoPagoBrand((Modifier.size(177.dp, 46.dp)).uiId("retail.0086"))
            LoteriasCaixaDSMercadoPagoPrizePanel(
                amount = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(state.details?.netValue),
                modifier = (Modifier.fillMaxWidth().padding(top = 40.dp)).uiId("retail.0087")
            )
            Column(Modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 252.dp) else Modifier.height(252.dp))) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_para_receber_seu_premio_por_favor_co_95e3f1),
                    modifier = (Modifier.fillMaxWidth().padding(start = 16.dp, top = 30.dp)).uiId("retail.0088"),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    fontSize = 18.199982.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    uiStrings.getString(DeliveryR.string.ui_e_mail_2550d9),
                    modifier = (Modifier.padding(start = 16.dp, top = 20.dp)).uiId("retail.0089"),
                    color = LoteriasCaixaDSColorTokens.FavoriteBlue,
                    fontSize = 15.2.sp
                )
                PrizeMercadoPagoEmailField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    modifier = (Modifier.fillMaxWidth().padding(horizontal = 16.dp)).uiId("retail.0090")
                )
            }
        }
    }
}
