package br.gov.caixa.loterias.feature.payment

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPixPanel
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPixPanelText
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDeleteIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.CaixaDSPaymentProvider
import br.gov.caixa.loterias.core.designsystem.CaixaDSPaymentProviderMark
import br.gov.caixa.loterias.core.designsystem.CaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.CaixaDSPixCopyIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSPixTimerIcon
import br.gov.caixa.loterias.core.domain.payment.PaymentMethod
import br.gov.caixa.loterias.core.domain.payment.PixCharge
import br.gov.caixa.loterias.core.domain.payment.SavedPaymentCard
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Locale
import kotlinx.coroutines.delay

data class PaymentVisualAssets(
    val recargaPayLogoResourceId: Int = 0,
)

@Composable
fun PaymentDataRoute(
    mode: PaymentMode,
    viewModel: PaymentViewModel,
    visualAssets: PaymentVisualAssets = PaymentVisualAssets(),
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onPix: () -> Unit,
    onAddCard: (Long) -> Unit,
    onOpenCard: (SavedPaymentCard) -> Unit,
    onApproved: () -> Unit,
    onHome: () -> Unit,
    onPurchases: () -> Unit,
    onRedirect: (br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect) -> Unit,
    onRegistration: () -> Unit,
    privateDataEnabled: Boolean = true,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PurchaseRedirectResumeEffect(viewModel::resumeAfterRedirect)
    PurchaseRedirectEffect(state.redirect, viewModel::consumeRedirect, onRedirect)
    PurchaseNoticeDialog(state.notice, viewModel::consumeNotice, onHome) {
        if (viewModel.consumeRegistrationNotice(it)) onRegistration()
    }
    var showPixExitConfirmation by remember { mutableStateOf(false) }
    val handleBack: () -> Unit = {
        if (mode != PaymentMode.PIX) {
            onBack()
        } else if (state.pix?.let { secondsUntil(it.expiresAt) > 0 } == true) {
            showPixExitConfirmation = true
        } else {
            onHome()
        }
    }
    BackHandler(enabled = mode == PaymentMode.PIX, onBack = handleBack)
    LaunchedEffect(state.pixApproved) { if (state.pixApproved) onApproved() }
    CaixaDSLegacyScaffold(
        title = if (mode == PaymentMode.CARDS) "Meus Cartões" else "Forma de pagamento",
        topBarTitleSize = if (mode == PaymentMode.CARDS) 20.sp else 16.sp,
        topBarAnnotatedTitle = if (mode == PaymentMode.PIX) paymentTopBarTitle() else null,
        topBarTitleWeight = if (mode == PaymentMode.PIX || mode == PaymentMode.CARDS) FontWeight.Normal else FontWeight.Bold,
        onBack = handleBack,
        topBarAction = if (mode != PaymentMode.CARDS) ({
            Icon(
                CaixaDSIcons.Help,
                contentDescription = "Ajuda",
                tint = CaixaDSColorTokens.White,
                modifier = Modifier.fillMaxSize().clickable(onClick = onHelp).padding(12.dp),
            )
        }) else null,
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
            state.pixRecoveryRequired || state.pixSessionMismatch -> Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Não foi possível recuperar o código Pix. Confira suas compras antes de gerar outro código.",
                    color = CaixaDS.colors.legacyGraphite, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                Button(onClick = onPurchases) { Text("Minhas Compras") }
            }
            mode == PaymentMode.METHODS -> PaymentMethodsContent(
                state,
                Modifier.padding(padding),
                visualAssets,
                onPix,
                onOpenCard,
                viewModel,
            )
            mode == PaymentMode.CARDS -> SavedCardsContent(state, Modifier.padding(padding), viewModel, onOpenCard, privateDataEnabled)
            else -> PixContent(state.pix, state.checkingPix, Modifier.padding(padding), viewModel, onPurchases)
        }
    }
    if (state.showPrePurchaseConfirmation) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = prePurchaseMessage(formatPaymentAmount(state.cartTotal)),
            positiveLabel = "Confirmar",
            onPositive = viewModel::acceptPrePurchase,
            negativeLabel = "Cancelar",
            onNegative = onBack,
            onDismiss = onBack,
        )
    }
    state.deleteCandidate?.let { card ->
        CaixaDSLegacyDialog(
            title = "Excluir cartão?",
            message = "Você tem certeza de que deseja excluir o cartão ${card.brand} final ${card.lastDigits} dos cartões salvos?",
            positiveLabel = "Sim",
            onPositive = viewModel::confirmDelete,
            negativeLabel = "Não",
            onNegative = viewModel::cancelDelete,
        )
    }
    state.pendingAddMethodId?.let { methodId ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "O cartão de crédito adicionado deve ser de sua titularidade",
            positiveLabel = "Entendi",
            onPositive = {
                viewModel.cancelAddCard()
                onAddCard(methodId)
            },
        )
    }
    if (state.pixRejected) {
        CaixaDSAlertDialog("Pagamento não aprovado", "Não foi possível confirmar o pagamento.", "Entendi", viewModel::dismissPixStatus)
    }
    if (showPixExitConfirmation) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = PIX_PENDING_EXIT_MESSAGE,
            positiveLabel = "Sim",
            onPositive = { showPixExitConfirmation = false },
            negativeLabel = "Voltar ao início",
            onNegative = onHome,
        )
    }
    state.damMessage?.let { CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissDamMessage) }
    state.errorMessage?.let { CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissError) }
}

@Composable
private fun PaymentMethodsContent(
    state: PaymentUiState,
    modifier: Modifier,
    visualAssets: PaymentVisualAssets,
    onPix: () -> Unit,
    onOpenCard: (SavedPaymentCard) -> Unit,
    viewModel: PaymentViewModel,
) {
    var expandedProviders by remember { mutableStateOf(emptySet<Long>()) }
    LazyColumn(modifier.fillMaxSize().background(CaixaDS.colors.legacyPaymentCanvas)) {
        val pix = state.methods.firstOrNull(PaymentMethod::isPix)
        if (pix != null) {
            item {
                PaymentDataHeading("Pix", "Gere um código Pix Copia e Cola para pagar suas apostas.")
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 35.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CaixaDSPixBrand(Modifier.padding(vertical = 16.dp).width(200.dp).height(90.dp))
                        Button(
                            onClick = onPix,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CaixaDS.colors.legacyPix),
                        ) { Text("Gerar código Pix Copia e cola", color = CaixaDSColorTokens.White) }
                    }
                }
            }
        }
        val cardMethods = state.methods.filter(PaymentMethod::isSupportedCard)
        if (cardMethods.isNotEmpty()) {
            item { PaymentDataHeading("Cartão de Crédito", PAYMENT_CARD_PROCESSOR_GUIDANCE) }
            items(cardMethods, key = PaymentMethod::id) { method ->
                val cards = state.cards[method.id].orEmpty()
                val expanded = method.id in expandedProviders
                val visibleCards = if (expanded) cards else cards.take(2)
                Card(
                    Modifier.fillMaxWidth().padding(start = 35.dp, top = 16.dp, end = 35.dp, bottom = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                ) {
                    Column {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(92.dp)
                                .padding(start = 16.dp, top = 8.dp, end = 16.dp),
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Pagar via:",
                                    color = CaixaDS.colors.legacyGraphite,
                                    fontSize = 13.sp,
                                    lineHeight = 16.sp,
                                )
                                CaixaDSPaymentProviderMark(
                                    provider = method.toCaixaDSProvider(),
                                    recargaPayResourceId = visualAssets.recargaPayLogoResourceId,
                                    modifier = if (method.id == PaymentMethod.MERCADO_PAGO_ID) {
                                        Modifier.padding(top = 16.dp).width(107.dp).height(26.dp)
                                    } else {
                                        Modifier.padding(top = 16.dp).width(129.dp).height(43.dp)
                                    },
                                )
                            }
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    "Bandeiras aceitas",
                                    color = CaixaDS.colors.legacyGraphite,
                                    fontSize = 13.sp,
                                    lineHeight = 16.sp,
                                )
                                AcceptedBrandMarks(method.id, Modifier.padding(top = 4.dp))
                            }
                        }
                        if (cards.isEmpty()) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .background(CaixaDS.colors.legacyPaymentCard),
                            ) {
                                Text(
                                    if (method.id == PaymentMethod.MERCADO_PAGO_ID) {
                                        "Você ainda não possui cartão salvo no processador Mercado Pago"
                                    } else {
                                        "Você ainda não possui cartão salvo no processador Recarga Pay"
                                    },
                                    Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterStart)
                                        .padding(start = 16.dp, end = 32.dp),
                                    color = CaixaDS.colors.legacyGraphite,
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                )
                                Text(
                                    "Novo Cartão de Crédito",
                                    Modifier
                                        .align(Alignment.BottomStart)
                                        .clickable { viewModel.requestAddCard(method.id) }
                                        .padding(start = 16.dp, bottom = 18.dp),
                                    color = CaixaDS.colors.brand,
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                    textDecoration = TextDecoration.Underline,
                                )
                            }
                        } else {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .background(CaixaDS.colors.legacyPaymentCard)
                                    .padding(top = 16.dp, bottom = 10.dp),
                            ) {
                                visibleCards.forEach { card ->
                                    InlinePaymentCard(
                                        card = card,
                                        onOpen = { onOpenCard(card) },
                                        onDelete = { viewModel.requestDelete(card) },
                                    )
                                }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        "Adicionar Cartão",
                                        Modifier
                                            .clickable { viewModel.requestAddCard(method.id) }
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        color = CaixaDS.colors.brand,
                                        fontSize = 16.sp,
                                        lineHeight = 20.sp,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                    if (cards.size > 2) {
                                        Text(
                                            if (expanded) "Listar Menos" else "Listar Mais",
                                            Modifier
                                                .clickable {
                                                    expandedProviders = if (expanded) {
                                                        expandedProviders - method.id
                                                    } else {
                                                        expandedProviders + method.id
                                                    }
                                                }
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            color = CaixaDS.colors.brand,
                                            fontSize = 16.sp,
                                            lineHeight = 20.sp,
                                            textDecoration = TextDecoration.Underline,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InlinePaymentCard(card: SavedPaymentCard, onOpen: () -> Unit, onDelete: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically) {
        Card(
            Modifier.weight(1f).padding(start = 16.dp, end = 8.dp, bottom = 8.dp).height(50.dp).clickable(onClick = onOpen),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
        ) {
            Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                CaixaDSPaymentBrandMark(card.brand, Modifier.size(50.dp))
                Spacer(Modifier.weight(1f))
                Text("**** ${card.lastDigits}", color = CaixaDS.colors.legacyText)
            }
        }
        Icon(
            CaixaDSLegacyDeleteIcon,
            contentDescription = "Excluir cartão final ${card.lastDigits}",
            modifier = Modifier.size(42.dp).clickable(onClick = onDelete).padding(9.dp),
            tint = CaixaDS.colors.brand,
        )
    }
}

@Composable
private fun AcceptedBrandMarks(paymentMethodId: Long, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.End) {
        acceptedBrands(paymentMethodId).chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { brand -> CaixaDSPaymentBrandMark(brand, Modifier.size(30.dp)) }
            }
        }
    }
}

private fun PaymentMethod.toCaixaDSProvider(): CaixaDSPaymentProvider =
    if (id == PaymentMethod.RECARGA_PAY_ID) CaixaDSPaymentProvider.RECARGA_PAY else CaixaDSPaymentProvider.MERCADO_PAGO

@Composable
private fun PaymentDataHeading(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 32.dp)) {
        Text(
            title,
            color = CaixaDS.colors.legacyCartDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 20.sp,
        )
        Text(
            subtitle,
            Modifier.padding(start = 16.dp, top = 8.dp),
            color = CaixaDS.colors.legacyGraphite,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun SavedCardsContent(
    state: PaymentUiState,
    modifier: Modifier,
    viewModel: PaymentViewModel,
    onOpenCard: (SavedPaymentCard) -> Unit,
    privateDataEnabled: Boolean = true,
) {
    val total = state.cards.values.sumOf { it.size }
    if (privateDataEnabled && total == 0) {
        Box(modifier.fillMaxSize().background(CaixaDSColorTokens.White), contentAlignment = Alignment.Center) {
            Text(
                "Você ainda não possui um cartão de crédito cadastrado. A inclusão de um ou mais cartões de crédito deve ser feita ao realizar uma compra.",
                Modifier.fillMaxWidth().padding(30.dp),
                color = CaixaDSColorTokens.Blue700, fontSize = 16.2.sp, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, style = savedCardTextStyle,
            )
        }
        return
    }
    LazyColumn(modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
        item {
            Text("Cartões cadastrados", Modifier.padding(start = 16.dp, top = 40.dp, end = 16.dp, bottom = 16.dp),
                color = CaixaDSColorTokens.Blue700, fontSize = 14.2.sp, fontWeight = FontWeight.Bold, style = savedCardTextStyle)
        }
        state.methods.filter(PaymentMethod::isSupportedCard).forEach { method ->
            val cards = state.cards[method.id].orEmpty()
            if (cards.isNotEmpty()) {
                item {
                    val isRecarga = method.id == PaymentMethod.RECARGA_PAY_ID
                    val accent = if (isRecarga) CaixaDSColorTokens.LegacyRecargaAccent else CaixaDSColorTokens.LegacyMercadoPagoAccent
                    Row(Modifier.fillMaxWidth().height(57.dp).border(1.dp, CaixaDSColorTokens.LegacyCardOutline), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.width(8.dp).fillMaxHeight().background(accent))
                        LegacyCardProviderLogo(isRecarga, Modifier.padding(start = 42.dp))
                    }
                    Box(Modifier.fillMaxWidth().height(270.dp).background(accent.copy(alpha = 20f / 255f))) {
                        Box(Modifier.width(8.dp).fillMaxHeight().background(accent))
                        Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Text(if (isRecarga) "Confira aqui sua lista de cartões salvos no RecargaPay" else "Confira aqui sua lista de cartões salvos no Mercado Pago",
                                modifier = Modifier.fillMaxWidth().offset(x = 16.dp), color = CaixaDSColorTokens.LegacyNotificationIntro,
                                style = savedCardTextStyle, fontSize = 14.sp)
                            LazyRow(Modifier.fillMaxWidth().padding(start = 8.dp, top = 16.dp).wrapContentHeight(Alignment.Top, unbounded = true)) {
                                items(cards, key = { card -> savedCardListKey(method.id, card.id) }) { card ->
                                    SavedPaymentCardItem(card, { onOpenCard(card) }, { viewModel.requestDelete(card) })
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
internal fun LegacyCardProviderLogo(recarga: Boolean, modifier: Modifier = Modifier) {
    Image(
        painterResource(if (recarga) R.drawable.legacy_card_provider_recarga else R.drawable.legacy_card_provider_mp),
        if (recarga) "RecargaPay" else "Mercado Pago",
        modifier.then(if (recarga) Modifier.width(129.dp).height(43.dp) else Modifier.width(107.dp).height(26.dp)),
    )
}

private val savedCardTextStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

@Composable
private fun SavedPaymentCardItem(card: SavedPaymentCard, onOpen: () -> Unit, onDelete: () -> Unit) {
    Box(Modifier.padding(30.dp).width(300.dp).height(165.dp)
        .background(Brush.verticalGradient(listOf(CaixaDSColorTokens.LegacySavedCardGradientStart, CaixaDSColorTokens.LegacySavedCardGradientEnd)), RoundedCornerShape(10.dp))
        .clickable(onClick = onOpen)) {
        Box(Modifier.align(Alignment.TopEnd).size(40.dp).clickable(onClick = onDelete), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.legacy_saved_card_delete), "Remover cartão", Modifier.width(23.dp).height(22.dp), tint = CaixaDSColorTokens.White)
        }
        SavedCardLegacyBrandMark(card.brand, Modifier.align(Alignment.BottomEnd).padding(end = 17.dp, bottom = 17.dp).width(70.dp).height(56.dp))
        Column(Modifier.align(Alignment.TopStart).padding(start = 17.dp, top = 92.dp, end = 87.dp)) {
            Text("**** **** ****  ${card.lastDigits}", color = CaixaDSColorTokens.White, fontSize = 16.sp, style = savedCardTextStyle)
            Text("Venc.:${card.expirationMonth}/${card.expirationYear}", color = CaixaDSColorTokens.White, fontSize = 13.sp, style = savedCardTextStyle)
        }
    }
}

@Composable
private fun SavedCardLegacyBrandMark(brand: String, modifier: Modifier) {
    val resource = when (brand.lowercase()) {
        "visa" -> R.drawable.legacy_saved_ic_visa
        "master", "mastercard" -> R.drawable.legacy_saved_ic_master_card
        "hipercard" -> R.drawable.legacy_saved_ic_hipercard
        "diners" -> R.drawable.legacy_saved_ic_diners
        "elo" -> R.drawable.legacy_saved_elo_fundo_escuro
        "amex", "american express" -> R.drawable.legacy_saved_ic_american
        else -> R.drawable.legacy_saved_card_cvc
    }
    Image(painterResource(resource), brand, modifier)
}

@Composable
private fun PixContent(
    pix: PixCharge?,
    checking: Boolean,
    modifier: Modifier,
    viewModel: PaymentViewModel,
    onPurchases: () -> Unit,
) {
    if (pix == null) return
    var remaining by remember(pix.expiresAt) { mutableLongStateOf(secondsUntil(pix.expiresAt)) }
    var copied by remember(pix.purchaseId) { mutableStateOf(false) }
    LaunchedEffect(pix.expiresAt) {
        while (remaining > 0) { delay(1_000); remaining = secondsUntil(pix.expiresAt) }
    }
    LaunchedEffect(pix.purchaseId, copied) {
        if (copied) while (remaining > 0) {
            delay(20_000)
            if (remaining > 0) viewModel.checkPixStatus()
        }
    }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(CaixaDS.colors.legacyCanvas)) {
        CaixaDSLegacyPixPanel(
            purchaseId = pix.purchaseId, copyPaste = pix.copyPaste, remainingSeconds = remaining,
            formattedAmount = formatPaymentAmount(pix.amount), checking = checking,
            onCheckPayment = viewModel::checkPixStatus, onCopied = { copied = true }, onPurchases = onPurchases,
            texts = CaixaDSLegacyPixPanelText(warning = if (remaining <= 0) PIX_EXPIRED_GUIDANCE else PIX_RECOVERY_GUIDANCE),
        )
    }
}

private fun paymentTopBarTitle(): AnnotatedString = buildAnnotatedString {
    append("Forma de ")
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("pagamento") }
}


private fun formatPaymentAmount(value: BigDecimal): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)

internal fun secondsUntil(value: String, now: Instant = Instant.now()): Long = runCatching {
    val instant = runCatching { OffsetDateTime.parse(value).toInstant() }.getOrElse {
        LocalDateTime.parse(value).atZone(ZoneId.of("America/Sao_Paulo")).toInstant()
    }
    (instant.epochSecond - now.epochSecond).coerceAtLeast(0)
}.getOrDefault(0)
