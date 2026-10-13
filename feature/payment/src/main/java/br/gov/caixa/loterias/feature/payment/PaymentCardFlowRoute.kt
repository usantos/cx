package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFooterButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyOutlinedButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect

@Composable
fun PaymentCardFlowRoute(
    viewModel: PaymentCardViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onReviewPurchases: () -> Unit,
    onRedirect: (PurchaseRedirect) -> Unit,
    onMoreBets: () -> Unit,
    onRegistration: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PurchaseRedirectResumeEffect(viewModel::resumeAfterRedirect)
    LaunchedEffect(state.stage) { if (state.stage == PaymentCardStage.SUCCESS) onSuccess() }
    CaixaDSLegacyScaffold(title = "Forma de pagamento", onBack = onBack) { padding ->
        when (state.stage) {
            PaymentCardStage.LOADING -> CenteredPaymentProgress(Modifier.padding(padding))
            PaymentCardStage.FORM -> CardForm(state, viewModel, onBack, Modifier.padding(padding))
            PaymentCardStage.PREVIEW -> CardPreview(state, viewModel, Modifier.padding(padding))
            PaymentCardStage.CVV -> CardCvv(state, viewModel, Modifier.padding(padding))
            PaymentCardStage.CONFIRM -> CardConfirmation(state, viewModel, Modifier.padding(padding))
            PaymentCardStage.REVIEW_REQUIRED -> CardConfirmation(state, viewModel, Modifier.padding(padding))
            PaymentCardStage.REDIRECT_REQUIRED -> CardConfirmation(state, viewModel, Modifier.padding(padding))
            PaymentCardStage.NOTICE_REQUIRED -> CardConfirmation(state, viewModel, Modifier.padding(padding))
            PaymentCardStage.PROCESSING, PaymentCardStage.SUCCESS -> CenteredPaymentProgress(Modifier.padding(padding), "Estamos processando sua compra.")
            PaymentCardStage.FAILURE -> CardFailure(state, viewModel, onBack, Modifier.padding(padding))
        }
    }
    PurchaseRedirectEffect(state.redirect, viewModel::consumeRedirect, onRedirect)
    PurchaseNoticeDialog(state.notice, viewModel::consumeNotice, onMoreBets) {
        if (viewModel.consumeRegistrationNotice(it)) onRegistration()
    }
    if (state.stage == PaymentCardStage.REVIEW_REQUIRED && !state.redirectPaused) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = PURCHASE_REVIEW_GUIDANCE,
            positiveLabel = "Entendi",
            onPositive = { if (viewModel.acknowledgePurchaseReview()) onReviewPurchases() },
        )
    }
    state.errorMessage?.takeIf { state.stage != PaymentCardStage.FAILURE }?.let {
        CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissError)
    }
}

@Composable
private fun CardForm(state: PaymentCardUiState, viewModel: PaymentCardViewModel, onBack: () -> Unit, modifier: Modifier) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(CaixaDS.colors.surface)) {
        Text("Preencha os dados de cartão de crédito de sua titularidade", Modifier.padding(start = 16.dp, top = 40.dp, end = 16.dp), color = CaixaDS.colors.brand, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.2.sp)
        CreditCardFront(state)
        ProviderHeader(state.providerName, onBack)
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text("Bandeiras aceitas", Modifier.padding(top = 8.dp), color = CaixaDS.colors.legacyGraphite, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                state.acceptedBrands.forEach { brand -> CaixaDSPaymentBrandMark(brand, Modifier.width(30.dp).height(30.dp)) }
            }
            LegacyPaymentInput("Número do cartão", formatCardNumber(state.number), viewModel::changeNumber)
            LegacyPaymentInput("Nome igual ao cartão", state.holderName, viewModel::changeHolderName)
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                LegacyPaymentPicker(
                    label = "Mês de validade",
                    value = state.month,
                    options = (1..12).map { it.toString().padStart(2, '0') },
                    onSelected = viewModel::changeMonth,
                    modifier = Modifier.weight(1f),
                )
                LegacyPaymentPicker(
                    label = "Ano de validade",
                    value = state.year,
                    options = state.expirationYears.map(Int::toString),
                    onSelected = viewModel::changeYear,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.save, onCheckedChange = { viewModel.toggleSave() })
                Text("Salvar cartão")
            }
            CaixaDSLegacyFooterButton("Continuar", viewModel::continueFromCard, Modifier.width(184.dp).align(Alignment.CenterHorizontally), state.formValid)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CardPreview(state: PaymentCardUiState, viewModel: PaymentCardViewModel, modifier: Modifier) {
    Column(modifier.fillMaxSize().background(CaixaDS.colors.legacyPaymentCanvas), horizontalAlignment = Alignment.CenterHorizontally) {
        LegacySmallHeading("Confirme os dados do cartão escolhido")
        CreditCardFront(state)
        LegacyCardActionButton("Incluir Código de Verificação", viewModel::continueFromCard, Modifier.padding(top = 20.dp).width(280.dp))
    }
}

@Composable
private fun CardCvv(state: PaymentCardUiState, viewModel: PaymentCardViewModel, modifier: Modifier) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(CaixaDS.colors.legacyPaymentCanvas), horizontalAlignment = Alignment.CenterHorizontally) {
        LegacySmallHeading("Insira o código de segurança do seu cartão de crédito")
        Text("Normalmente localizado no verso do seu cartão", Modifier.fillMaxWidth().padding(start = 32.dp, top = 8.dp, end = 32.dp), color = CaixaDS.colors.legacyGraphite, fontSize = 13.sp, style = legacyCardTextStyle)
        // activity_digitar_cvv: number and back fragment share one FrameLayout;
        // the card begins at 8dp, the number overlays it at 16dp.
        Box(Modifier.fillMaxWidth()) {
            Box(Modifier.padding(top = 8.dp)) { CreditCardBack(showMagnifier = true, brand = state.brand) }
            Text("cartão final •••• ${state.lastDigits}", Modifier.fillMaxWidth().padding(top = 16.dp, end = 32.dp), color = CaixaDS.colors.legacyGraphite, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.End, style = legacyCardTextStyle)
        }
        CvvEntry(state.cvv, state.cvvLength, viewModel::changeCvv)
        Text("Digite seu código de segurança", Modifier.padding(top = 1.dp), color = CaixaDS.colors.legacyGraphite, fontSize = 13.sp, style = legacyCardTextStyle)
        LegacyCardActionButton("Confirmar", viewModel::continueFromCvv, Modifier.padding(top = 40.dp).width(170.dp), state.cvv.length == state.cvvLength)
    }
}

@Composable
private fun CardConfirmation(state: PaymentCardUiState, viewModel: PaymentCardViewModel, modifier: Modifier) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(CaixaDS.colors.legacyPaymentCanvas), horizontalAlignment = Alignment.CenterHorizontally) {
        LegacySmallHeading("Confirme os dados digitados")
        CreditCardFront(state)
        CreditCardBack(showMagnifier = true, brand = state.brand)
        LegacyCardActionButton("Apostar", { viewModel.submit() }, Modifier.width(140.dp).padding(bottom = 16.dp))
    }
}

@Composable
private fun CreditCardFront(state: PaymentCardUiState) = CreditCardFront(
    brand = state.brand,
    number = state.savedCard?.let { "•••• •••• •••• ${state.lastDigits}" }
        ?: formatCardNumber(state.number),
    holderName = state.savedCard?.let { "-" } ?: state.holderName,
    expiration = state.savedCard?.let { "${it.expirationMonth.toString().padStart(2, '0')}/${it.expirationYear}" }
        ?: listOf(state.month, state.year).takeIf { it.all(String::isNotBlank) }?.joinToString("/").orEmpty(),
)

@Composable
private fun LegacyPaymentPicker(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier.padding(top = 14.dp)) {
        Row(
            Modifier.fillMaxWidth().height(41.dp).clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                value.ifBlank { label },
                Modifier.weight(1f),
                color = if (value.isBlank()) CaixaDS.colors.legacyPrimaryButton else CaixaDS.colors.legacyText,
                fontSize = 16.sp,
            )
            Icon(CaixaDSIcons.ChevronDown, contentDescription = "Selecionar $label", tint = CaixaDS.colors.brand)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).align(Alignment.BottomCenter).background(CaixaDS.colors.legacyLine))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CenteredPaymentProgress(modifier: Modifier, message: String = "Carregando dados do pagamento...") {
    Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = CaixaDS.colors.brand)
        Text(message, Modifier.padding(top = 16.dp), color = CaixaDS.colors.legacyGraphite)
    }
}

@Composable
private fun CardFailure(state: PaymentCardUiState, viewModel: PaymentCardViewModel, onBack: () -> Unit, modifier: Modifier) {
    Column(modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (state.trackingPurchase) "Acompanhamento da compra" else "Pagamento não aprovado", color = CaixaDS.colors.error, fontSize = 22.sp)
        Text(state.errorMessage ?: "Não foi possível concluir sua compra.", Modifier.padding(top = 12.dp))
        CaixaDSLegacyFooterButton("Tentar novamente", viewModel::retry, Modifier.width(210.dp).padding(top = 24.dp))
        CaixaDSLegacyOutlinedButton("Voltar", onBack, Modifier.width(210.dp).padding(top = 12.dp))
    }
}


private val legacyCardTextStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

/** Literal button_rounded_verdeazul geometry used by the three card stages. */
@Composable
private fun LegacyCardActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.LegacyTealAction, contentColor = CaixaDSColorTokens.White),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
    ) {
        Text(label, fontSize = 17.sp, fontWeight = FontWeight.Bold, style = legacyCardTextStyle)
    }
}
