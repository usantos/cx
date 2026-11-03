package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSFooterButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardBack
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardFront
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyCardActionButton
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacySmallHeading
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.ui.R as DeliveryR

@Composable
fun CardConfirmationDelivered(state: PaymentCardUiState, actions: PaymentCardActions, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxSize()
            .uiId("pay.0081").verticalScroll(rememberScrollState()).background(LoteriasCaixaDS.colors.paymentCanvas),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LegacySmallHeading(uiStrings.getString(DeliveryR.string.ui_confirme_os_dados_digitados_90c25b))
        CreditCardFront(state)
        CreditCardBack(showMagnifier = true, brand = state.brand)
        LegacyCardActionButton("Apostar", { actions.submit() }, (Modifier.width(140.dp).padding(bottom = 16.dp)).uiId("pay.0082"))
    }
}

@Composable
fun CardFailureDelivered(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (state.trackingPurchase) {
                uiStrings.getString(
                    DeliveryR.string.ui_acompanhamento_da_compra_feea00
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_pagamento_nao_aprovado_b8c8b6)
            },
            color = LoteriasCaixaDS.colors.error,
            fontSize = 22.sp,
            modifier = Modifier.uiId("pay.0083")
        )
        Text(
            state.errorMessage ?: uiStrings.getString(DeliveryR.string.ui_nao_foi_possivel_concluir_sua_compra_f96541),
            (Modifier.padding(top = 12.dp)).uiId("pay.0084")
        )
        LoteriasCaixaDSFooterButton(
            uiStrings.getString(DeliveryR.string.ui_tentar_novamente_b9e106),
            actions.retry,
            (Modifier.width(210.dp).padding(top = 24.dp)).uiId("pay.0085")
        )
        LoteriasCaixaDSOutlinedButton(
            stringResource(DeliveryR.string.ui_voltar_59dc92),
            onBack,
            (Modifier.width(210.dp).padding(top = 12.dp))
                .uiId("pay.0086")
        )
    }
}
