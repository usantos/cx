package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSFooterButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardFront
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyPaymentInput
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyPaymentPicker
import br.gov.caixa.loterias.feature.payment.presentation.components.ProviderHeader
import br.gov.caixa.loterias.feature.payment.presentation.mapper.acceptedBrands
import br.gov.caixa.loterias.feature.payment.presentation.mapper.formatCardNumber
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.ui.R as DeliveryR

@Composable
fun CardFormDelivered(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier.fillMaxSize().uiId("pay.0066").verticalScroll(rememberScrollState()).background(LoteriasCaixaDS.colors.surface)) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_preencha_os_dados_de_cartao_de_credi_bf1aad),
            (Modifier.padding(start = 16.dp, top = 40.dp, end = 16.dp)).uiId("pay.0067"),
            color = LoteriasCaixaDS.colors.brand,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 14.2.sp
        )
        CreditCardFront(state)
        ProviderHeader(state.providerName, onBack)
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_bandeiras_aceitas_bf739a),
                (Modifier.padding(top = 8.dp)).uiId("pay.0068"),
                color = LoteriasCaixaDS.colors.graphite,
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                state.acceptedBrands.forEach { brand ->
                    LoteriasCaixaDSPaymentBrandMark(
                        brand,
                        (Modifier.width(30.dp).height(30.dp))
                            .uiId("pay.0069")
                    )
                }
            }
            LegacyPaymentInput(
                uiStrings.getString(DeliveryR.string.ui_numero_do_cartao_0c235a),
                formatCardNumber(state.number),
                actions.changeNumber
            )
            LegacyPaymentInput(
                uiStrings.getString(DeliveryR.string.ui_nome_igual_ao_cartao_27ae7e),
                state.holderName,
                actions.changeHolderName
            )
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                LegacyPaymentPicker(
                    label = uiStrings.getString(DeliveryR.string.ui_mes_de_validade_7e3813),
                    value = state.month,
                    options = (1..12).map { it.toString().padStart(2, '0') },
                    onSelected = actions.changeMonth,
                    modifier = (Modifier.weight(1f)).uiId("pay.0070")
                )
                LegacyPaymentPicker(
                    label = uiStrings.getString(DeliveryR.string.ui_ano_de_validade_954be8),
                    value = state.year,
                    options = state.expirationYears.map(Int::toString),
                    onSelected = actions.changeYear,
                    modifier = (Modifier.weight(1f)).uiId("pay.0071")
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.save, onCheckedChange = { actions.toggleSave() }, modifier = Modifier.uiId("pay.0072"))
                Text(uiStrings.getString(DeliveryR.string.ui_salvar_cartao_c1afff), modifier = Modifier.uiId("pay.0073"))
            }
            LoteriasCaixaDSFooterButton(
                stringResource(LocalizedR.string.workflow_payment_continuar),
                actions.continueFromCard,
                (Modifier.width(184.dp).align(Alignment.CenterHorizontally)).uiId("pay.0074"),
                state.formValid
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
