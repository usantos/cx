package br.gov.caixa.loterias.feature.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFooterButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyOutlinedButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

private val legacyPaymentTextStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

private val pixInstructions = listOf(
    "Copie o código gerado e pague dentro do prazo que será apresentado;",
    "Entre no aplicativo da sua instituição financeira e \nacesse o ambiente Pix;",
    "Escolha a opção Pix Copia e Cola;",
    "Cole o código Pix;",
    "Confirme as informações e realize o pagamento.",
)

/**
 * UI local apenas para a orientação Pix e estados terminais. Dados e regras de cartão/Pix ficam
 * nos fluxos [PaymentCardFlowRoute] e [PaymentDataRoute], ligados aos respectivos casos de uso.
 */
@Composable
fun PaymentTerminalRoute(
    viewModel: PaymentTerminalViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onNavigate: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PaymentTerminalEffect.NavigateBack -> onBack()
                PaymentTerminalEffect.Completed -> onComplete()
            }
        }
    }
    when (state.spec.route) {
        "payment/confirmation", "payment/success" -> ProcessingScreen(state.spec, onNavigate)
        "payment/failure" -> PaymentFailureScreen(state.spec, onNavigate)
        "payment/pix-guidance" -> PixGuidanceScreen(state.spec, state, viewModel)
        else -> error("Rota terminal de pagamento desconhecida: ${state.spec.route}")
    }
}

@Composable
internal fun ProviderHeader(providerName: String = "Mercado Pago", onChange: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp).height(57.dp)
            .background(CaixaDS.colors.legacyPaymentCard),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(5.dp).fillMaxSize().background(CaixaDS.colors.legacySavedCardAccent))
        Icon(
            CaixaDSIcons.Back,
            "Voltar",
            tint = CaixaDS.colors.legacyCartDark,
            modifier = Modifier.padding(start = 16.dp)
                .then(if (onChange != null) Modifier.clickable(onClick = onChange) else Modifier),
        )
        Box(Modifier.padding(start = 16.dp).weight(1f)) {
            LegacyCardProviderLogo(providerName.contains("recarga", ignoreCase = true))
        }
        Text(
            "Alterar",
            Modifier.padding(end = 28.dp)
                .then(if (onChange != null) Modifier.clickable(onClick = onChange) else Modifier),
            color = CaixaDS.colors.legacyCartDark,
        )
    }
}

@Composable
internal fun LegacyPaymentInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 14.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            singleLine = true,
            textStyle = CaixaDS.typography.bodyLarge.copy(color = CaixaDS.colors.legacyText),
            decorationBox = { inner ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    if (value.isBlank()) {
                        Text(label, color = CaixaDS.colors.legacyPrimaryButton, fontSize = 16.sp)
                    }
                    inner()
                }
            },
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.legacyLine))
    }
}

@Composable
internal fun CvvEntry(value: String, length: Int = 3, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.padding(top = 8.dp),
        textStyle = CaixaDS.typography.bodyLarge.copy(color = CaixaDSColorTokens.Transparent),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                repeat(length) { index ->
                    Box(
                        Modifier.width(40.dp).height(60.dp)
                            .border(1.dp, CaixaDS.colors.legacyTeal),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            value.getOrNull(index)?.toString().orEmpty(),
                            color = CaixaDS.colors.legacyCartDark,
                            fontSize = 40.sp,
                            style = legacyPaymentTextStyle,
                        )
                    }
                }
            }
        },
    )
}

@Composable
internal fun CreditCardFront(
    brand: String = "VISA",
    number: String = "•••• •••• •••• 4589",
    holderName: String = "NOME DO TITULAR",
    expiration: String = "12/2029",
) {
    Card(
        // fragment_cartao_credito.xml: 200 dp body plus 32 dp outer margins.
        Modifier.fillMaxWidth().height(264.dp).padding(32.dp),
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.legacyCreditCard),
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier.fillMaxSize().padding(start = 190.dp)
                    .background(CaixaDSColorTokens.White, RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp)),
            )
            CaixaDSPaymentBrandMark(brand, Modifier.align(Alignment.TopStart).padding(start = 20.dp).offset(y = (-5).dp).size(80.dp))
            Column(Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 81.dp)) {
                Text("Número", color = CaixaDSColorTokens.Black, fontSize = 12.sp, style = legacyPaymentTextStyle)
                Text(number, Modifier.padding(top = 6.dp), color = CaixaDSColorTokens.Black, fontSize = 12.sp, style = legacyPaymentTextStyle)
            }
            Column(Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 10.dp)) {
                Text("Nome", Modifier.padding(bottom = 6.dp), color = CaixaDSColorTokens.Black, fontSize = 12.sp, style = legacyPaymentTextStyle)
                Text(holderName, color = CaixaDSColorTokens.Black, fontSize = 12.sp, style = legacyPaymentTextStyle)
            }
            Column(Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 10.dp), horizontalAlignment = Alignment.End) {
                Text("Validade", Modifier.padding(end = 4.dp, bottom = 12.dp), color = CaixaDSColorTokens.Black, fontSize = 12.sp, style = legacyPaymentTextStyle)
                Text(expiration, color = CaixaDSColorTokens.Black, fontSize = 12.sp, style = legacyPaymentTextStyle)
            }
        }
    }
}

@Composable
internal fun CreditCardBack(showMagnifier: Boolean, brand: String = "VISA") {
    Box(Modifier.fillMaxWidth().height(264.dp)) {
        Card(
            Modifier.fillMaxSize().padding(32.dp),
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.legacyCreditCard),
        ) {
            Box(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().padding(start = 190.dp)
                    .background(CaixaDSColorTokens.White, RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp)))
                Box(Modifier.fillMaxWidth().padding(top = 24.dp).height(40.dp)
                    .background(CaixaDS.colors.legacyMagneticStripe))
                Box(Modifier.fillMaxWidth().padding(top = 95.dp).height(48.dp)
                    .background(CaixaDS.colors.legacySignatureStripe))
                CaixaDSPaymentBrandMark(brand,
                    Modifier.align(Alignment.BottomStart).padding(start = 20.dp).width(80.dp).height(65.dp))
            }
        }
        if (showMagnifier) {
            Box(
                Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = 56.dp).size(100.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(painterResource(R.drawable.legacy_card_magnifier), null, Modifier.fillMaxSize())
                Text("XXX", Modifier.offset(x = (-4).dp, y = (-5).dp), color = CaixaDSColorTokens.Black,
                    fontSize = 20.sp, style = legacyPaymentTextStyle)
            }
        }
    }
}

@Composable
internal fun LegacySmallHeading(text: String) {
    Text(
        text,
        Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp),
        color = CaixaDS.colors.legacyCartDark,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        style = legacyPaymentTextStyle,
    )
}

@Composable
private fun PixGuidanceScreen(
    spec: WorkflowSpec,
    state: PaymentTerminalUiState,
    viewModel: PaymentTerminalViewModel,
) {
    CaixaDSLegacyScaffold(title = spec.title, onBack = viewModel::back) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.surface)
                .verticalScroll(rememberScrollState()).padding(top = 16.dp, bottom = 64.dp),
        ) {
            Text(
                spec.description,
                Modifier.fillMaxWidth().padding(start = 17.dp, top = 9.dp, end = 17.dp, bottom = 8.dp),
                color = CaixaDS.colors.legacyCartDark,
                fontSize = 14.sp,
                style = legacyPaymentTextStyle,
            )
            Text(
                "Como pagar com pix",
                Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                color = CaixaDS.colors.legacyCartDark,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                style = legacyPaymentTextStyle,
            )
            pixInstructions.forEachIndexed { index, instruction ->
                Row(Modifier.fillMaxWidth().padding(start = 24.dp)) {
                    Text("${index + 1}. ", fontSize = 14.sp, style = legacyPaymentTextStyle, color = CaixaDS.colors.legacyCartDark)
                    Text(instruction, fontSize = 14.sp, style = legacyPaymentTextStyle, color = CaixaDS.colors.legacyCartDark)
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp)
                    .clickable { viewModel.togglePixTerms() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(state.pixTermsAccepted, onCheckedChange = { viewModel.togglePixTerms() })
                Text(
                    "Estou ciente que somente será aceito o pagamento realizado em conta de titularidade do apostador.",
                    color = CaixaDS.colors.legacyCartDark,
                    fontSize = 14.sp,
                    style = legacyPaymentTextStyle,
                    lineHeight = 18.sp,
                )
            }
            Button(
                onClick = viewModel::submit,
                enabled = state.pixTermsAccepted,
                modifier = Modifier.padding(top = 16.dp).width(177.dp).height(48.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CaixaDS.colors.legacyPix,
                    contentColor = CaixaDSColorTokens.White,
                    disabledContainerColor = CaixaDS.colors.legacyPixDisabled,
                    disabledContentColor = CaixaDSColorTokens.White,
                ),
            ) { Text("Pagar agora!", fontSize = 14.sp, fontWeight = FontWeight.Bold, style = legacyPaymentTextStyle) }
        }
    }
}

@Composable
private fun PaymentFailureScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit) {
    CaixaDSLegacyScaffold(title = spec.title, onBack = { onNavigate("home") }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                Modifier.size(76.dp).border(3.dp, CaixaDS.colors.error, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    CaixaDSIcons.Close,
                    contentDescription = null,
                    tint = CaixaDS.colors.error,
                    modifier = Modifier.size(38.dp),
                )
            }
            Text(
                spec.description,
                Modifier.padding(top = 24.dp),
                color = CaixaDS.colors.legacyCartDark,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                "Revise os dados do pagamento ou escolha outra forma para tentar novamente.",
                Modifier.padding(top = 12.dp),
                color = CaixaDS.colors.legacyGraphite,
                textAlign = TextAlign.Center,
            )
            CaixaDSLegacyFooterButton(
                spec.primaryAction,
                { onNavigate("payment/method") },
                Modifier.width(220.dp).padding(top = 32.dp),
            )
            CaixaDSLegacyOutlinedButton(
                spec.secondaryAction.orEmpty(),
                { onNavigate("home") },
                Modifier.width(220.dp).padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun ProcessingScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit) {
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
      Box(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().background(
          Brush.verticalGradient(
            listOf(
              CaixaDS.colors.legacySuccessGradientStart,
              CaixaDS.colors.legacySuccessGradientEnd,
            ),
          ),
        ),
      ) {
        Row(
          Modifier.align(Alignment.TopCenter).fillMaxWidth().height(64.dp)
            .background(CaixaDS.colors.brand),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            "Confirmação",
            Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = CaixaDSColorTokens.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
          )
          LegacyHelpIcon { onNavigate("support/terms") }
        }
        Column(
          Modifier.fillMaxSize().padding(start = 40.dp, top = 80.dp, end = 40.dp, bottom = 152.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
            Image(
                imageVector = CaixaDSIcons.PaymentProcessing,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )
            Text(
                PURCHASE_PROCESSING_TITLE,
                Modifier.padding(top = 10.dp),
                color = CaixaDSColorTokens.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Box(
                Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp)
                    .background(CaixaDS.colors.legacySuccessBadge),
            )
            Column(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                purchaseProcessingMessages.forEachIndexed { index, text ->
                    Row(Modifier.fillMaxWidth()) {
                        Text("•", color = CaixaDSColorTokens.White)
                        Text(
                            legacyProcessingText(text),
                            Modifier.padding(start = 8.dp),
                            color = CaixaDSColorTokens.White,
                            fontSize = 16.sp,
                        )
                    }
                    if (index == 1) Spacer(Modifier.height(8.dp))
                }
            }
        }
        Box(
          Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(41.dp)
            .background(CaixaDS.colors.surface),
        )
        ProcessingActionButton(
          text = spec.primaryAction,
          primary = true,
          modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 84.dp),
        ) { onNavigate(purchaseProcessingDestination()) }
        spec.secondaryAction?.let { text ->
          ProcessingActionButton(
            text = text,
            primary = false,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
          ) { onNavigate("home") }
        }
      }
    }
}

@Composable
private fun ProcessingActionButton(
    text: String,
    primary: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.width(274.dp).height(52.dp),
        shape = RoundedCornerShape(if (primary) 5.dp else 4.dp),
        border = if (primary) null else BorderStroke(1.dp, CaixaDS.colors.legacyFilterBlue),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) CaixaDS.colors.actionHighlight else CaixaDS.colors.surface,
            contentColor = if (primary) CaixaDSColorTokens.White else CaixaDS.colors.brand,
        ),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    }
}

private fun legacyProcessingText(text: String) = buildAnnotatedString {
    val emphasis = when {
        "EFETIVADA" in text -> "EFETIVADA"
        "não garante" in text -> "não garante"
        "Minhas Compras" in text -> "Minhas Compras"
        else -> null
    }
    if (emphasis == null) {
        append(text)
    } else {
        val start = text.indexOf(emphasis)
        append(text.substring(0, start))
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(emphasis) }
        append(text.substring(start + emphasis.length))
    }
}

@Composable
private fun LegacyHelpIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(CaixaDSIcons.Help, contentDescription = "Ajuda", tint = CaixaDSColorTokens.White)
    }
}
