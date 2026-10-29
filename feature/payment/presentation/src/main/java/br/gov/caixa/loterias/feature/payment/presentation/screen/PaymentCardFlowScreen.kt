package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSFooterButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.components.CenteredPaymentProgress
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardBack
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardFront
import br.gov.caixa.loterias.feature.payment.presentation.components.CvvEntry
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyCardActionButton
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyPaymentInput
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyPaymentPicker
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacySmallHeading
import br.gov.caixa.loterias.feature.payment.presentation.components.ProviderHeader
import br.gov.caixa.loterias.feature.payment.presentation.components.legacyCardTextStyle
import br.gov.caixa.loterias.feature.payment.presentation.mapper.acceptedBrands
import br.gov.caixa.loterias.feature.payment.presentation.mapper.formatCardNumber
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState

internal data class PaymentCardActions(
    val changeNumber: (String) -> Unit,
    val changeHolderName: (String) -> Unit,
    val changeMonth: (String) -> Unit,
    val changeYear: (String) -> Unit,
    val toggleSave: () -> Unit,
    val continueFromCard: () -> Unit,
    val changeCvv: (String) -> Unit,
    val continueFromCvv: () -> Unit,
    val submit: () -> Unit,
    val retry: () -> Unit,
)

@Composable
internal fun PaymentCardFlowScreen(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_forma_de_pagamento_faf708),
        onBack = onBack,
        modifier = Modifier
            .uiId("pay.0055")
    ) { padding ->
        when (state.stage) {
            PaymentCardStage.LOADING -> CenteredPaymentProgress((Modifier.padding(padding)).uiId("pay.0056"))

            PaymentCardStage.FORM -> CardForm(state, actions, onBack, (Modifier.padding(padding)).uiId("pay.0057"))

            PaymentCardStage.PREVIEW -> CardPreview(state, actions, (Modifier.padding(padding)).uiId("pay.0058"))

            PaymentCardStage.CVV -> CardCvv(state, actions, (Modifier.padding(padding)).uiId("pay.0059"))

            PaymentCardStage.CONFIRM -> CardConfirmation(state, actions, (Modifier.padding(padding)).uiId("pay.0060"))

            PaymentCardStage.REVIEW_REQUIRED -> CardConfirmation(state, actions, (Modifier.padding(padding)).uiId("pay.0061"))

            PaymentCardStage.REDIRECT_REQUIRED -> CardConfirmation(state, actions, (Modifier.padding(padding)).uiId("pay.0062"))

            PaymentCardStage.NOTICE_REQUIRED -> CardConfirmation(state, actions, (Modifier.padding(padding)).uiId("pay.0063"))

            PaymentCardStage.PROCESSING,
            PaymentCardStage.SUCCESS,
            -> CenteredPaymentProgress(
                (Modifier.padding(padding)).uiId("pay.0064"),
                uiStrings.getString(LocalizedR.string.ui_estamos_processando_sua_compra_952655)
            )

            PaymentCardStage.FAILURE -> CardFailure(state, actions, onBack, (Modifier.padding(padding)).uiId("pay.0065"))
        }
    }
}

@Composable
private fun CardForm(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier.fillMaxSize().uiId("pay.0066").verticalScroll(rememberScrollState()).background(CaixaDS.colors.surface)) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_preencha_os_dados_de_cartao_de_credi_bf1aad),
            (Modifier.padding(start = 16.dp, top = 40.dp, end = 16.dp)).uiId("pay.0067"),
            color = CaixaDS.colors.brand,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 14.2.sp
        )
        CreditCardFront(state)
        ProviderHeader(state.providerName, onBack)
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_bandeiras_aceitas_bf739a),
                (Modifier.padding(top = 8.dp)).uiId("pay.0068"),
                color = CaixaDS.colors.graphite,
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                state.acceptedBrands.forEach { brand ->
                    CaixaDSPaymentBrandMark(
                        brand,
                        (Modifier.width(30.dp).height(30.dp))
                            .uiId("pay.0069")
                    )
                }
            }
            LegacyPaymentInput(
                uiStrings.getString(LocalizedR.string.ui_numero_do_cartao_0c235a),
                formatCardNumber(state.number),
                actions.changeNumber
            )
            LegacyPaymentInput(
                uiStrings.getString(LocalizedR.string.ui_nome_igual_ao_cartao_27ae7e),
                state.holderName,
                actions.changeHolderName
            )
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                LegacyPaymentPicker(
                    label = uiStrings.getString(LocalizedR.string.ui_mes_de_validade_7e3813),
                    value = state.month,
                    options = (1..12).map { it.toString().padStart(2, '0') },
                    onSelected = actions.changeMonth,
                    modifier = (Modifier.weight(1f)).uiId("pay.0070")
                )
                LegacyPaymentPicker(
                    label = uiStrings.getString(LocalizedR.string.ui_ano_de_validade_954be8),
                    value = state.year,
                    options = state.expirationYears.map(Int::toString),
                    onSelected = actions.changeYear,
                    modifier = (Modifier.weight(1f)).uiId("pay.0071")
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.save, onCheckedChange = { actions.toggleSave() }, modifier = Modifier.uiId("pay.0072"))
                Text(uiStrings.getString(LocalizedR.string.ui_salvar_cartao_c1afff), modifier = Modifier.uiId("pay.0073"))
            }
            CaixaDSFooterButton(
                stringResource(LocalizedR.string.workflow_payment_continuar),
                actions.continueFromCard,
                (Modifier.width(184.dp).align(Alignment.CenterHorizontally)).uiId("pay.0074"),
                state.formValid
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CardPreview(state: PaymentCardUiState, actions: PaymentCardActions, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier.fillMaxSize().background(CaixaDS.colors.paymentCanvas), horizontalAlignment = Alignment.CenterHorizontally) {
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
private fun CardCvv(state: PaymentCardUiState, actions: PaymentCardActions, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxSize()
            .uiId("pay.0076").verticalScroll(rememberScrollState()).background(CaixaDS.colors.paymentCanvas),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LegacySmallHeading(uiStrings.getString(LocalizedR.string.ui_insira_o_codigo_de_seguranca_do_seu_37c0d3))
        Text(
            uiStrings.getString(LocalizedR.string.ui_normalmente_localizado_no_verso_do_s_cf883d),
            (Modifier.fillMaxWidth().padding(start = 32.dp, top = 8.dp, end = 32.dp)).uiId("pay.0077"),
            color = CaixaDS.colors.graphite,
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
                color = CaixaDS.colors.graphite,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                style = legacyCardTextStyle
            )
        }
        CvvEntry(state.cvv, state.cvvLength, actions.changeCvv)
        Text(
            uiStrings.getString(LocalizedR.string.ui_digite_seu_codigo_de_seguranca_9e6237),
            (Modifier.padding(top = 1.dp)).uiId("pay.0079"),
            color = CaixaDS.colors.graphite,
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

@Composable
private fun CardConfirmation(state: PaymentCardUiState, actions: PaymentCardActions, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxSize()
            .uiId("pay.0081").verticalScroll(rememberScrollState()).background(CaixaDS.colors.paymentCanvas),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LegacySmallHeading(uiStrings.getString(LocalizedR.string.ui_confirme_os_dados_digitados_90c25b))
        CreditCardFront(state)
        CreditCardBack(showMagnifier = true, brand = state.brand)
        LegacyCardActionButton("Apostar", { actions.submit() }, (Modifier.width(140.dp).padding(bottom = 16.dp)).uiId("pay.0082"))
    }
}

@Composable
private fun CardFailure(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (state.trackingPurchase) {
                uiStrings.getString(
                    LocalizedR.string.ui_acompanhamento_da_compra_feea00
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_pagamento_nao_aprovado_b8c8b6)
            },
            color = CaixaDS.colors.error,
            fontSize = 22.sp,
            modifier = Modifier.uiId("pay.0083")
        )
        Text(
            state.errorMessage ?: uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_concluir_sua_compra_f96541),
            (Modifier.padding(top = 12.dp)).uiId("pay.0084")
        )
        CaixaDSFooterButton(
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            actions.retry,
            (Modifier.width(210.dp).padding(top = 24.dp)).uiId("pay.0085")
        )
        CaixaDSOutlinedButton(
            stringResource(LocalizedR.string.ui_voltar_59dc92),
            onBack,
            (Modifier.width(210.dp).padding(top = 12.dp))
                .uiId("pay.0086")
        )
    }
}
