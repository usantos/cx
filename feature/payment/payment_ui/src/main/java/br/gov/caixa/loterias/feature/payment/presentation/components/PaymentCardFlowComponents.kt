package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.payment.presentation.mapper.formatCardNumber
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.ui.R as DeliveryR

@Composable
fun CreditCardFront(state: PaymentCardUiState) = CreditCardFront(
    brand = state.brand,
    number = state.savedCard?.let { stringResource(DeliveryR.string.ui_1_s_dca085, state.lastDigits) }
        ?: formatCardNumber(state.number),
    holderName = state.savedCard?.let { "-" } ?: state.holderName,
    expiration = state.savedCard?.let { "${it.expirationMonth.toString().padStart(2, '0')}/${it.expirationYear}" }
        ?: listOf(state.month, state.year).takeIf { it.all(String::isNotBlank) }?.joinToString("/").orEmpty()
)

@Composable
fun LegacyPaymentPicker(label: String, value: String, options: List<String>, onSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardPicker(
        label,
        value,
        options,
        onSelected,
        stringResource(DeliveryR.string.ui_selecionar_1_s_f97e7d, label),
        modifier
    )
}

@Composable
fun CenteredPaymentProgress(modifier: Modifier, message: String = stringResource(DeliveryR.string.format_payment_loading)) {
    Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = LoteriasCaixaDS.colors.brand)
        Text(message, (Modifier.padding(top = 16.dp)).uiId("pay.0026"), color = LoteriasCaixaDS.colors.graphite)
    }
}

val legacyCardTextStyle = br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardTextStyle

/** Geometria literal de button_rounded_verdeazul usada nas três etapas do cartão. */
@Composable
fun LegacyCardActionButton(label: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean = true) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardActionButton(label, onClick, modifier, enabled)
}
