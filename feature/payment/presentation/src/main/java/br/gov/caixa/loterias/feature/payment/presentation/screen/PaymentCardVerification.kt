package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardBack
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardFront
import br.gov.caixa.loterias.feature.payment.presentation.components.CvvEntry
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyCardActionButton
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacySmallHeading
import br.gov.caixa.loterias.feature.payment.presentation.components.legacyCardTextStyle
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState

@Composable
internal fun CardPreview(state: PaymentCardUiState, actions: PaymentCardActions, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(LoteriasCaixaDS.colors.paymentCanvas),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LegacySmallHeading(uiStrings.getString(LocalizedR.string.ui_confirme_os_dados_do_cartao_escolhid_890bbf))
        CreditCardFront(state)
        LegacyCardActionButton(
            uiStrings.getString(LocalizedR.string.ui_incluir_codigo_de_verificacao_89a5fe),
            actions.continueFromCard,
            (Modifier.padding(top = 20.dp).width(280.dp)).uiId("pay.0075")
        )
    }
}

@Composable
internal fun CardCvv(state: PaymentCardUiState, actions: PaymentCardActions, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxSize()
            .uiId("pay.0076").verticalScroll(rememberScrollState()).background(LoteriasCaixaDS.colors.paymentCanvas),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LegacySmallHeading(uiStrings.getString(LocalizedR.string.ui_insira_o_codigo_de_seguranca_do_seu_37c0d3))
        Text(
            uiStrings.getString(LocalizedR.string.ui_normalmente_localizado_no_verso_do_s_cf883d),
            (Modifier.fillMaxWidth().padding(start = 32.dp, top = 8.dp, end = 32.dp)).uiId("pay.0077"),
            color = LoteriasCaixaDS.colors.graphite,
            fontSize = 13.sp,
            style = legacyCardTextStyle
        )
        // activity_digitar_cvv: o número e o fragmento traseiro compartilham um FrameLayout;
        // o cartão começa em 8dp e o número se sobrepõe a ele em 16dp.
        Box(Modifier.fillMaxWidth()) {
            Box(Modifier.padding(top = 8.dp)) { CreditCardBack(showMagnifier = true, brand = state.brand) }
            Text(
                uiStrings.getString(LocalizedR.string.ui_cartao_final_1_s_3647b7, state.lastDigits),
                (Modifier.fillMaxWidth().padding(top = 16.dp, end = 32.dp)).uiId("pay.0078"),
                color = LoteriasCaixaDS.colors.graphite,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                style = legacyCardTextStyle
            )
        }
        CvvEntry(state.cvv, state.cvvLength, actions.changeCvv)
        Text(
            uiStrings.getString(LocalizedR.string.ui_digite_seu_codigo_de_seguranca_9e6237),
            (Modifier.padding(top = 1.dp)).uiId("pay.0079"),
            color = LoteriasCaixaDS.colors.graphite,
            fontSize = 13.sp,
            style = legacyCardTextStyle
        )
        LegacyCardActionButton(
            stringResource(LocalizedR.string.ui_confirmar_717bed),
            actions.continueFromCvv,
            (Modifier.padding(top = 40.dp).width(170.dp)).uiId("pay.0080"),
            state.cvv.length == state.cvvLength
        )
    }
}
