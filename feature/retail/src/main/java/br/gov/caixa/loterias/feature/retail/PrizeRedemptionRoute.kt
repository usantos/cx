package br.gov.caixa.loterias.feature.retail

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.PlatformTextStyle
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSPixMark
import br.gov.caixa.loterias.core.designsystem.CaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSBetTypeItem
import br.gov.caixa.loterias.core.designsystem.CaixaDSBetTypeStrip
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrizeChevron
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrizeMessage
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyMercadoPagoPrizePanel
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPurchaseHelpIcon
import br.gov.caixa.loterias.core.designsystem.caixaDSLegacyLotteryClover
import br.gov.caixa.loterias.core.designsystem.CaixaDSPrizeLotteryMark
import br.gov.caixa.loterias.core.designsystem.CaixaDSPrizeMercadoPagoMark
import br.gov.caixa.loterias.core.designsystem.CaixaDSMercadoPagoBrand
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.retail.PixBeneficiary
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionChannel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PrizeRedemptionRoute(
    viewModel: PrizeRedemptionViewModel,
    initialChannel: PrizeRedemptionChannel? = null,
    previewSuccessChannel: PrizeRedemptionChannel? = null,
    allowControlledSuccessPreview: Boolean = false,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    onRegistration: (String) -> Unit,
    onRedirect: (br.gov.caixa.loterias.core.domain.retail.PrizeRedirect) -> Unit,
    onBetEntry: (br.gov.caixa.loterias.core.domain.betting.BetEntryDecision) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    br.gov.caixa.loterias.core.presentation.NavigationResumeEffect(viewModel::resumeAfterRedirect)
    val redirect = state.redirect
    if (redirect == br.gov.caixa.loterias.core.domain.retail.PrizeRedirect.ACCEPT_TERMS) {
        br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog(
            title = "Atenção", message = br.gov.caixa.loterias.core.presentation.LEGACY_UPDATED_TERMS,
            positiveLabel = "Entendi", onPositive = { if (viewModel.consumeRedirect(redirect)) onRedirect(redirect) },
        )
    } else {
        LaunchedEffect(redirect) {
            if (redirect != null && viewModel.consumeRedirect(redirect)) onRedirect(redirect)
        }
    }
    LaunchedEffect(state.betEntryDecision) {
        state.betEntryDecision?.let {
            viewModel.consumeBetEntryDecision()
            onBetEntry(it)
        }
    }
    var initialSelectionApplied by remember(viewModel, initialChannel) { mutableStateOf(false) }
    val controlledPreviewChannel = previewSuccessChannel.takeIf { allowControlledSuccessPreview }
    LaunchedEffect(state.stage, initialChannel, controlledPreviewChannel) {
        if (
            !initialSelectionApplied &&
            controlledPreviewChannel == null &&
            initialChannel != null &&
            state.stage == PrizeRedemptionStage.OPTIONS
        ) {
            initialSelectionApplied = true
            viewModel.select(initialChannel)
        }
    }
    val uiState = prizeScreenState(state, controlledPreviewChannel, allowControlledSuccessPreview)
    val context = LocalContext.current
    var pendingReceipt by remember { mutableStateOf<PrizeReceipt?>(null) }
    val savedReceipts = remember { mutableStateMapOf<String, Uri>() }
    fun openPdf(uri: Uri) {
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, "application/pdf")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
            )
        }
    }
    val createPdf = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val receipt = pendingReceipt
        if (uri != null && receipt != null) {
            context.contentResolver.openOutputStream(uri)?.use { it.write(receipt.bytes) }
            savedReceipts[receipt.fileName] = uri
            openPdf(uri)
        }
        pendingReceipt = null
    }
    LaunchedEffect(viewModel) {
        viewModel.receipts.collect { receipt ->
            savedReceipts[receipt.fileName]?.let(::openPdf) ?: run {
                pendingReceipt = receipt
                createPdf.launch(receipt.fileName)
            }
        }
    }
    val handleBack = { if (viewModel.back()) onBack() }
    BackHandler(enabled = uiState.stage != PrizeRedemptionStage.SUCCESS, onBack = handleBack)

    // Packed prize TextViews retain Android font padding; do not inherit Material3's
    // body line height when measuring the literal 14/16/25sp text.
    ProvideTextStyle(LocalTextStyle.current.copy(
        fontFamily = CaixaDSFontFamily,
        platformStyle = PlatformTextStyle(includeFontPadding = true),
        lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
    )) {
    when (uiState.stage) {
        PrizeRedemptionStage.LOADING -> PrizeLoadingScreen(handleBack)
        PrizeRedemptionStage.OPTIONS -> PrizeOptionsScreen(uiState, handleBack, onHelp,
            viewModel::select, viewModel::betNow, viewModel::loadCurrentContest)
        PrizeRedemptionStage.LOTTERY -> PrizeLotteryDataScreen(uiState, handleBack, onHelp, viewModel::refreshLotteryCode)
        PrizeRedemptionStage.AGENCY -> PrizeAgencyDataScreen(
            state = uiState,
            onBack = handleBack,
            onHelp = onHelp,
            onLottery = { viewModel.select(PrizeRedemptionChannel.LOTTERY) },
            onDownload = {
                val fileName = "comprovante-aposta-${uiState.details?.betId}.pdf"
                savedReceipts[fileName]?.let(::openPdf) ?: viewModel.downloadBetReceipt()
            },
        )
        PrizeRedemptionStage.PIX_ENTRY, PrizeRedemptionStage.PIX_CONFIRM -> PrizePixDataScreen(uiState, handleBack, onHelp, viewModel)
        PrizeRedemptionStage.MERCADO_PAGO -> PrizeMercadoPagoDataScreen(
            state = uiState,
            onBack = handleBack,
            onHelp = onHelp,
            viewModel = viewModel,
        )
        PrizeRedemptionStage.SUCCESS -> PrizeSuccessDataScreen(
            state = uiState,
            onHelp = onHelp,
            onHome = onHome,
            onMyBets = onMyBets,
            onReceipt = {
                val fileName = "comprovante-premio-${uiState.details?.betId}.pdf"
                savedReceipts[fileName]?.let(::openPdf) ?: viewModel.downloadPrizeReceipt()
            },
        )
    }
    }
    if (uiState.loading && uiState.stage != PrizeRedemptionStage.LOADING) {
        if (uiState.stage == PrizeRedemptionStage.MERCADO_PAGO) {
            CaixaDSAuthenticationLoadingDialog()
        } else {
            Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.Black.copy(alpha = .28f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
        }
    }
    state.errorMessage?.let { CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissError) }
    state.notice?.let { notice ->
        br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog(
            title = "Atenção", message = notice.message,
            positiveLabel = notice.positiveLabel, negativeLabel = notice.negativeLabel,
            onPositive = {
                if (notice.destination == null) viewModel.dismissNotice(notice)
                else viewModel.consumeNoticeAction(notice)?.let(onRedirect)
            },
            onNegative = { viewModel.dismissNotice(notice) },
        )
    }
    state.registrationNotice?.let { notice ->
        br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog(
            title = "Atenção", message = notice.message, positiveLabel = "Sim", negativeLabel = "Não",
            onPositive = { viewModel.consumeRegistrationNotice(notice)?.let(onRegistration) },
            onNegative = { viewModel.dismissRegistrationNotice(notice) },
        )
    }
    state.successNoticeMessage?.let {
        CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissSuccessNotice)
    }
}

@Composable
private fun PrizeLoadingScreen(onBack: () -> Unit) {
    CaixaDSLegacyScaffold("Resgatar prêmio", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.brand)
        }
    }
}

@Composable
private fun PrizeOptionsScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onSelect: (PrizeRedemptionChannel) -> Unit,
    onBetNow: () -> Unit,
    onRetryContest: () -> Unit,
) {
    val details = state.details
    val style = caixaDSLotteryStyle(details?.modalityId ?: 2, details?.modalityName.orEmpty())
    CaixaDSLegacyScaffold(
        title = "Resultado da aposta",
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp) {
                CaixaDSLegacyPurchaseHelpIcon(Modifier.size(24.dp))
            }
        },
        topBarTitleSize = 20.sp,
        containerColor = CaixaDSColorTokens.White,
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            PrizeResultHeader(state, style)
            details?.let {
                CaixaDSBetTypeStrip(
                    items = prizeBetTypes(it).map { type ->
                        CaixaDSBetTypeItem(type.label, when (type) {
                            PrizeBetType.EXCHANGE -> CaixaDSBetTypeIcons.Exchange
                            PrizeBetType.POOL -> CaixaDSBetTypeIcons.Pool
                            PrizeBetType.COMBO -> CaixaDSBetTypeIcons.Combo
                            PrizeBetType.MIRROR -> CaixaDSBetTypeIcons.Mirror
                            PrizeBetType.SURPRISE -> CaixaDSBetTypeIcons.Surprise
                            PrizeBetType.RECURRING -> CaixaDSBetTypeIcons.Recurring
                        })
                    },
                    backgroundColor = style.dark,
                    contentColor = style.darkContent,
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }
            Text(
                "Escolha como resgatar seu prêmio",
                Modifier.fillMaxWidth().padding(top = 16.dp),
                color = CaixaDSColorTokens.LegacyNotificationIntro,
                fontFamily = CaixaDSLegacyFuturaFontFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
            state.details?.options.orEmpty().forEach { option ->
                PrizeOptionRow(option, onSelect)
            }
            state.details?.incomeTaxMessage?.takeIf(String::isNotBlank)?.let {
                Text(it, Modifier.padding(24.dp), color = CaixaDS.colors.legacyMutedText, style = CaixaDS.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PrizeCurrentContest(state: PrizeRedemptionUiState, onBetNow: () -> Unit, onRetry: () -> Unit) {
    val contest = state.currentContest?.takeIf { it.hasNextContest }
    val textColor = CaixaDS.colors.legacyMutedText
    Text("Informações do concurso atual", Modifier.fillMaxWidth().padding(vertical = 20.dp), color = textColor,
        fontSize = 14.2.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    Row(Modifier.fillMaxWidth().heightIn(min = 115.dp)) {
        Column(Modifier.width(195.dp).padding(start = 16.dp, top = 10.dp)) {
            Text("Estimativa de prêmio", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(contest?.estimatedPrize?.let { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(it) }.orEmpty(),
                Modifier.padding(top = 5.dp), color = textColor, fontSize = 14.sp)
        }
        Column(Modifier.weight(1f).padding(end = 16.dp, top = 10.dp)) {
            Text("Encerramento das apostas", color = textColor, fontSize = 14.2.sp, fontWeight = FontWeight.Bold)
            Text(contest?.closingDateTime?.let(::formatPrizeClosingDate).orEmpty(),
                Modifier.padding(top = 5.dp), color = textColor, fontSize = 14.2.sp)
            Button(onClick = onBetNow, enabled = !state.loading,
                modifier = Modifier.fillMaxWidth().padding(top = 15.dp).heightIn(min = 36.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.LegacyPrizeBetNow,
                    contentColor = CaixaDSColorTokens.White)) {
                Text(androidx.compose.ui.text.buildAnnotatedString {
                    append("Aposte ")
                    pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                    append("agora")
                    pop()
                }, fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 14.2.sp)
            }
        }
    }
    if (state.currentContestLoading) {
        Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(Modifier.size(20.dp), color = CaixaDS.colors.brand)
        }
    }
    state.currentContestError?.let { message ->
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = textColor, style = CaixaDS.typography.bodySmall)
            androidx.compose.material3.TextButton(onClick = onRetry) { Text("Tentar novamente") }
        }
    }
}

@Composable
private fun PrizeResultHeader(
    state: PrizeRedemptionUiState,
    style: br.gov.caixa.loterias.core.designsystem.CaixaDSLotteryStyle,
) {
    val details = state.details
    val awarded = details?.let(::prizeAwardedRows).orEmpty()
    Row(
        Modifier.fillMaxWidth().height(56.dp).background(style.light),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(90.dp)
                .fillMaxHeight()
                .background(
                    style.dark,
                    GenericShape { size, _ ->
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width * .68f, size.height)
                        lineTo(0f, size.height)
                        close()
                    },
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(caixaDSLegacyLotteryClover(details?.modalityId ?: 2), contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.padding(start = 16.dp).size(with(LocalDensity.current) { 22.sp.toDp() }, 22.dp))
        }
        Text(
            details?.modalityName.orEmpty().lowercase(Locale.forLanguageTag("pt-BR")),
            Modifier.weight(1f),
            color = style.content,
            fontSize = 16.sp,
            fontFamily = CaixaDSLegacyFuturaFontFamily,
            fontWeight = FontWeight.Bold,
        )
        Column(Modifier.padding(end = 24.dp), horizontalAlignment = Alignment.End) {
            val range = details != null && (details.recurringDraws > 0 || details.initialContest != details.targetContest)
            Text(if (range) "Concursos" else "Concurso", color = style.content,
                fontSize = 14.sp, fontWeight = FontWeight.Bold)
            val contestText = when {
                details == null -> ""
                details.targetContest > 0 && details.initialContest != details.targetContest -> "${details.initialContest} - ${details.targetContest}"
                details.recurringDraws > 0 && awarded.isNotEmpty() -> "${awarded.first().contest} - ${awarded.last().contest}"
                else -> details.targetContest.takeIf { it > 0 }?.toString().orEmpty()
            }
            Text(contestText,
                color = style.content, fontSize = 16.sp, fontFamily = CaixaDSLegacyFuturaFontFamily)
        }
    }
    Column(
        Modifier.fillMaxWidth().background(style.light),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val amount = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
            .format(details?.netValue ?: java.math.BigDecimal.ZERO)
        CaixaDSLegacyPrizeMessage(
            message = if (details?.netValue?.signum() == 0 || details == null) {
                "você foi premiado!"
            } else {
                "você foi premiado com total de \n$amount"
            },
            contentColor = style.content,
            modifier = Modifier.fillMaxWidth(),
        )
        if (details != null) PrizeAwardTable(details, style.content)
    }
}

@Composable
private fun PrizeAwardTable(
    details: br.gov.caixa.loterias.core.domain.retail.PrizeDetails,
    contentColor: Color,
) {
    val money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    Column(Modifier.fillMaxWidth().padding(16.dp).heightIn(min = 45.dp, max = 180.dp)
        .verticalScroll(rememberScrollState())) {
        prizeAwardedRows(details).groupBy { it.contest }.forEach { (contest, awards) ->
            if (details.recurringDraws > 0) {
                Text("Concurso: $contest", Modifier.padding(horizontal = 32.dp).padding(top = 8.dp),
                    color = contentColor, fontFamily = CaixaDSLegacyFuturaFontFamily,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            awards.forEach { award ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp).padding(top = 8.dp, bottom = 10.dp)) {
                    Text(award.description, Modifier.weight(1f), color = contentColor,
                        fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 14.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(award.amount?.let(money::format).orEmpty(), color = contentColor,
                        fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(1.dp).background(contentColor))
        }
    }
}

@Composable
private fun PrizeOptionRow(
    option: br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionOption,
    onSelect: (PrizeRedemptionChannel) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp)
            .border(BorderStroke(2.dp, CaixaDSColorTokens.LegacyDrawerDivider), RoundedCornerShape(4.dp))
            .clickable { onSelect(option.channel) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).background(CaixaDSColorTokens.LegacyComboDark, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            when (option.channel) {
                PrizeRedemptionChannel.PIX -> CaixaDSPixMark(Modifier.size(20.dp), CaixaDSColorTokens.White)
                PrizeRedemptionChannel.MERCADO_PAGO -> CaixaDSPrizeMercadoPagoMark(Modifier.size(18.dp, 12.dp))
                PrizeRedemptionChannel.LOTTERY -> CaixaDSPrizeLotteryMark(Modifier.size(20.dp))
                PrizeRedemptionChannel.AGENCY -> Icon(
                    imageVector = CaixaDSIcons.Caixa,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.Unspecified,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        PrizeOptionText(option.title, option.description, Modifier.weight(1f).padding(start = 13.dp))
        CaixaDSLegacyPrizeChevron(Modifier.size(12.dp, 18.dp))
    }
}

@Composable
private fun PrizeOptionText(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            title,
            color = CaixaDSColorTokens.LegacySocialGraphite,
            fontFamily = CaixaDSLegacyRegularFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
        )
        Text(
            subtitle,
            color = CaixaDSColorTokens.LegacySocialGraphite,
            fontFamily = CaixaDSLegacyRegularFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
        )
    }
}

@Composable
private fun PrizeLotteryDataScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onRefresh: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val urgency = lotteryQrUrgency(state.remainingSeconds)
    val pulseTransition = rememberInfiniteTransition(label = "qr-expiration-pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (urgency == LotteryQrUrgency.PULSE || urgency == LotteryQrUrgency.VIBRATE) 1.14f else 1f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "qr-expiration-scale",
    )
    LaunchedEffect(state.remainingSeconds) {
        if (urgency == LotteryQrUrgency.VIBRATE) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    CaixaDSLegacyScaffold(
        title = "Lotérica",
        onBack = onBack,
        topBarAction = { PrizeHelpAction(onHelp) },
        topBarTitleSize = 20.sp,
        containerColor = CaixaDSColorTokens.White,
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth().height(300.dp).background(CaixaDSColorTokens.FavoriteBlue), contentAlignment = Alignment.Center) {
                state.lotteryCode?.qrCode?.takeIf(String::isNotBlank)?.let { QrCodeImage(it) }
            }
            val valid = urgency != LotteryQrUrgency.EXPIRED
            Text(
                if (valid) "O QR Code é válido por ${state.lotteryCode?.validityMinutes ?: 15} minutos\nacompanhe o contador:"
                else "O QR Code expirou",
                Modifier.fillMaxWidth().padding(top = 10.dp),
                textAlign = TextAlign.Center,
                color = if (valid) CaixaDS.colors.legacyText else CaixaDSColorTokens.LegacyPrizeUrgent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            if (valid) Text(
                formatRemainingTime(state.remainingSeconds),
                Modifier.fillMaxWidth().padding(top = 15.dp).background(CaixaDSColorTokens.White)
                    .graphicsLayer { scaleX = pulse; scaleY = pulse },
                color = if (urgency == LotteryQrUrgency.NORMAL) {
                    CaixaDSColorTokens.LegacyPrizeGreen
                } else {
                    CaixaDSColorTokens.LegacyPrizeUrgent
                },
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            if (!valid) {
                Box(
                    Modifier.padding(top = 15.dp).width(183.dp).height(56.dp)
                        .background(CaixaDSColorTokens.White)
                        .border(1.dp, CaixaDS.colors.legacyTeal, RectangleShape)
                        .clickable(onClick = onRefresh),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Gerar\nnovamente",
                        color = CaixaDS.colors.legacyTeal,
                        fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Box(
                Modifier.fillMaxWidth().padding(top = 15.dp).height(1.dp)
                    .background(CaixaDSColorTokens.LegacyPrizeDivider),
            )
            PrizeLotteryInstruction(
                number = 1,
                text = "Escolha uma Lotérica mais próxima",
                modifier = Modifier.padding(top = 39.dp),
            )
            PrizeLotteryInstruction(
                number = 2,
                text = "Mostre o QR Code ao atendente",
                modifier = Modifier.padding(top = 35.dp),
            )
            PrizeLotteryInstruction(
                number = 3,
                text = "Aproveite seu prêmio!",
                highlightedText = "prêmio",
                modifier = Modifier.padding(top = 39.dp, bottom = 30.dp),
            )
        }
    }
}

@Composable
private fun QrCodeImage(value: String) {
    val bitmap = remember(value) {
        val matrix = QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, 640, 640)
        Bitmap.createBitmap(640, 640, Bitmap.Config.ARGB_8888).apply {
            for (y in 0 until 640) for (x in 0 until 640) {
                setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }.asImageBitmap()
    }
    Image(bitmap, "QR Code de resgate", Modifier.size(200.dp))
}

@Composable
private fun PrizeLotteryInstruction(
    number: Int,
    text: String,
    modifier: Modifier = Modifier,
    highlightedText: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(40.dp).background(CaixaDSColorTokens.White, CircleShape)
                .border(1.dp, CaixaDS.colors.legacyLine, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                number.toString(),
                color = CaixaDS.colors.brand,
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        val instruction = buildAnnotatedString {
            if (highlightedText == null || highlightedText !in text) {
                append(text)
            } else {
                val start = text.indexOf(highlightedText)
                append(text.substring(0, start))
                withStyle(SpanStyle(color = CaixaDSColorTokens.LegacyPrizeGreen, fontWeight = FontWeight.Bold)) {
                    append(highlightedText)
                }
                append(text.substring(start + highlightedText.length))
            }
        }
        Text(
            text = instruction,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            color = CaixaDS.colors.legacyGraphite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PrizeAgencyDataScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onLottery: () -> Unit,
    onDownload: () -> Unit,
) {
    CaixaDSLegacyScaffold(
        "Agência",
        onBack,
        topBarAction = {
            IconButton(onClick = onHelp) {
                Icon(
                    CaixaDSIcons.Help,
                    contentDescription = "Ajuda",
                    tint = CaixaDSColorTokens.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        topBarTitleSize = 20.sp,
        containerColor = CaixaDSColorTokens.White,
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            if (state.details?.options.orEmpty().any { it.channel == PrizeRedemptionChannel.LOTTERY }) {
                // A ActionBar do legado sobrepunha o bloco de 70 dp e deixava somente
                // esta faixa clicável visível abaixo da toolbar.
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(CaixaDS.colors.legacyGraphite)
                        .clickable(onClick = onLottery),
                )
            }
            PrizeAgencyInstruction(
                number = 1,
                text = "Emita o seu comprovante da aposta",
                modifier = Modifier.padding(top = 39.dp),
            )
            PrizeAgencyInstruction(
                number = 2,
                text = "Dirija-se a uma agência de sua preferência",
                modifier = Modifier.padding(top = 35.dp),
            )
            PrizeAgencyInstruction(
                number = 3,
                text = "Apresente seus documentos pessoais (RG, CPF, Comprovante de Endereço)",
                modifier = Modifier.padding(top = 39.dp, bottom = 16.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, top = 36.dp, end = 50.dp)
                    .height(56.dp)
                    .background(CaixaDSColorTokens.White, RectangleShape)
                    .border(1.dp, CaixaDS.colors.legacyTeal, RectangleShape)
                    .clickable(onClick = onDownload),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Gerar PDF",
                    color = CaixaDS.colors.legacyTeal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun PrizeAgencyInstruction(number: Int, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(CaixaDSColorTokens.White, CircleShape)
                .border(1.dp, CaixaDS.colors.legacyLine, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                number.toString(),
                color = CaixaDS.colors.brand,
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            color = CaixaDS.colors.legacyGraphite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PrizePixDataScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    viewModel: PrizeRedemptionViewModel,
) {
    CaixaDSLegacyScaffold(
        title = "Resgate de Prêmio via Pix",
        onBack = onBack,
        topBarAction = { PrizeHelpAction(onHelp) },
        topBarTitleSize = 16.sp,
        containerColor = CaixaDS.colors.legacyPaymentCanvas,
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.legacyPaymentCanvas)
                .verticalScroll(rememberScrollState()),
        ) {
            if (state.stage == PrizeRedemptionStage.PIX_ENTRY) {
                PrizePixKeyChoice(state, viewModel)
            } else {
                PrizePixKeyConfirmation(requireNotNull(state.pixBeneficiary))
            }
            Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White)) {
                Text(
                    "Valor do Crédito",
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp),
                    color = CaixaDSColorTokens.LegacyPrizeText,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CaixaDSLegacyFuturaFontFamily,
                    fontSize = 14.sp,
                )
                Text(
                    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(state.details?.netValue),
                    Modifier.padding(start = 20.dp, top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontFamily = CaixaDSLegacyFuturaFontFamily,
                    fontSize = 14.sp,
                )
                Row(
                    Modifier.padding(start = 20.dp, top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Resgate processado via",
                        color = CaixaDSColorTokens.LegacyPrizeText,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CaixaDSLegacyFuturaFontFamily,
                        fontSize = 14.sp,
                    )
                    CaixaDSPixBrand(Modifier.padding(start = 16.dp).size(100.dp, 36.dp))
                }
                Row(
                    Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    PrizePixActionButton(
                        text = "Cancelar",
                        onClick = {
                            if (state.stage == PrizeRedemptionStage.PIX_CONFIRM) {
                                viewModel.cancelPixAndExit()
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        outlined = true,
                    )
                    PrizePixActionButton(
                        text = if (state.stage == PrizeRedemptionStage.PIX_ENTRY) "Continuar" else "Resgatar",
                        onClick = if (state.stage == PrizeRedemptionStage.PIX_ENTRY) {
                            ({ viewModel.consultPix() })
                        } else {
                            ({ viewModel.confirmPix() })
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.stage == PrizeRedemptionStage.PIX_CONFIRM || state.canContinuePix(),
                    )
                }
            }
        }
    }
}

@Composable
private fun PrizePixKeyChoice(state: PrizeRedemptionUiState, viewModel: PrizeRedemptionViewModel) {
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).padding(top = 8.dp)) {
        Text(
            "Escolha a chave Pix que deseja receber o resgate do prêmio.",
            Modifier.padding(24.dp),
            color = CaixaDS.colors.brand,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(Modifier.fillMaxWidth().padding(top = 16.dp).height(56.dp)) {
            PrizePixTab("CPF", state.pixKeySource == PrizePixKeySource.CPF, viewModel::selectCpfPixKey, Modifier.weight(1f))
            PrizePixTab("Outra Chave", state.pixKeySource == PrizePixKeySource.OTHER, viewModel::selectOtherPixKey, Modifier.weight(1f))
        }
        Text(
            "Chave",
            Modifier.padding(24.dp),
            color = CaixaDSColorTokens.LegacyPrizeText,
            fontWeight = FontWeight.Bold,
        )
        if (state.pixKeySource == PrizePixKeySource.CPF) {
            Text(state.cpf, Modifier.padding(start = 24.dp, top = 16.dp), fontWeight = FontWeight.Bold)
        } else {
            Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
                PrizePixOtherKeyField(
                    value = state.pixKey,
                    onValueChange = viewModel::changePixKey,
                    error = state.pixKey.isNotBlank() && !isValidPixKey(state.pixKey),
                )
                Row(
                    Modifier.fillMaxWidth().padding(top = 16.dp).clickable(onClick = viewModel::togglePixOwnership),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(state.pixOwnershipAccepted, onCheckedChange = { viewModel.togglePixOwnership() })
                    Text(
                        "Estou ciente que o resgate de prêmio só poderá ser realizado para uma conta de titularidade do apostador.",
                        Modifier.weight(1f),
                        color = CaixaDS.colors.brand,
                    )
                }
            }
        }
    }
}

@Composable
private fun PrizePixTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxHeight().background(
            if (selected) CaixaDS.colors.legacyTeal else CaixaDSColorTokens.LegacyTicketDisabledBackground,
        )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (selected) CaixaDSColorTokens.White else CaixaDS.colors.legacyTeal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        if (selected) {
            Box(
                Modifier.align(Alignment.BottomCenter).size(30.dp, 12.dp).background(
                    CaixaDSColorTokens.White,
                    GenericShape { size, _ ->
                        moveTo(0f, size.height)
                        lineTo(size.width / 2f, 0f)
                        lineTo(size.width, size.height)
                        close()
                    },
                ),
            )
        }
    }
}

@Composable
private fun PrizePixOtherKeyField(
    value: String,
    onValueChange: (String) -> Unit,
    error: Boolean,
) {
    val lineColor = if (error) CaixaDSColorTokens.LegacyPrizeUrgent else CaixaDS.colors.legacyLine
    Column(Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(54.dp).drawBehind {
                drawLine(
                    color = lineColor,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height - 1.dp.toPx()),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            },
            textStyle = CaixaDS.typography.bodyLarge.copy(color = CaixaDS.colors.legacyText, fontSize = 16.sp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            "Celular / e-mail / Chave aleatória",
                            color = CaixaDS.colors.legacyMutedText,
                            fontSize = 16.sp,
                        )
                    }
                    innerTextField()
                }
            },
        )
        if (error) {
            Text(
                "Chave inválida!",
                modifier = Modifier.padding(top = 4.dp),
                color = CaixaDSColorTokens.LegacyPrizeUrgent,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun PrizePixActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    outlined: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(55.dp).graphicsLayer { alpha = if (enabled) 1f else .3f },
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        border = if (outlined) BorderStroke(1.dp, CaixaDSColorTokens.LegacyRedemptionButton) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (outlined) CaixaDSColorTokens.White else CaixaDSColorTokens.LegacyRedemptionButton,
            contentColor = if (outlined) CaixaDSColorTokens.LegacyRedemptionButton else CaixaDSColorTokens.White,
            disabledContainerColor = CaixaDSColorTokens.LegacyRedemptionButton,
            disabledContentColor = CaixaDSColorTokens.White,
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp),
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PrizePixKeyConfirmation(beneficiary: PixBeneficiary) {
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).padding(top = 8.dp)) {
        Text("Confirmar a chave Pix para o resgate.", Modifier.padding(start = 16.dp, top = 24.dp, end = 24.dp, bottom = 24.dp), color = CaixaDS.colors.brand, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Dados do titular da chave:", Modifier.padding(horizontal = 24.dp), color = CaixaDS.colors.brand, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(beneficiary.name, Modifier.padding(start = 24.dp, top = 16.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(beneficiary.maskedCpf, Modifier.padding(start = 24.dp, top = 4.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Chave: ${beneficiary.key}", Modifier.padding(start = 24.dp, top = 4.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun PrizeMercadoPagoDataScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    viewModel: PrizeRedemptionViewModel,
) {
    CaixaDSLegacyScaffold(
        title = "Resgate de Prêmio via Mercado Pago",
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp) {
                CaixaDSLegacyPurchaseHelpIcon(Modifier.size(24.dp))
            }
        },
        topBarTitleSize = 20.sp,
        topBarTitleContent = {
            // Reproduz o ellipsis do título nativo: sem o espaço que o overflow
            // automático do Compose acrescenta antes do caractere de reticências.
            Text("Resgate de Prêmio via…", modifier = Modifier.fillMaxWidth().padding(end = 16.dp), color = CaixaDSColorTokens.White,
                fontFamily = CaixaDSFontFamily, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Clip,
                style = LocalTextStyle.current.copy(platformStyle = PlatformTextStyle(includeFontPadding = true)))
        },
        containerColor = CaixaDSColorTokens.White,
        bottomBar = {
            Box(Modifier.fillMaxWidth().height(104.dp)) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(CaixaDSColorTokens.LegacyMercadoPagoFooter),
                )
                LegacyRedemptionActionButton(
                    text = "Resgatar prêmio",
                    compactContent = true,
                    onClick = viewModel::confirmMercadoPago,
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = CaixaDS.colors.legacyTeal,
                )
            }
        },
    ) { padding ->
        PrizeMercadoPagoBiasedBody(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)) {
            CaixaDSMercadoPagoBrand(Modifier.size(177.dp, 46.dp))
            CaixaDSLegacyMercadoPagoPrizePanel(
                amount = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(state.details?.netValue),
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
            )
            Column(Modifier.fillMaxWidth().height(252.dp)) {
                Text(
                    "Para receber seu prêmio,\npor favor confirme seu e-mail:",
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 30.dp),
                    color = CaixaDSColorTokens.LegacyBetAction,
                    fontSize = with(LocalDensity.current) { 18.199982.dp.toSp() },
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "E-mail",
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp),
                    color = CaixaDSColorTokens.FavoriteBlue,
                    fontSize = 15.2.sp,
                )
                PrizeMercadoPagoEmailField(
                    value = state.email,
                    onValueChange = viewModel::changeEmail,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }
        }
    }
}

/** Packed MP ConstraintLayout: measured header,20dp margins,vertical bias0.1. */
@Composable
private fun PrizeMercadoPagoBiasedBody(modifier: Modifier, content: @Composable () -> Unit) {
    BoxWithConstraints(modifier) {
        val viewportHeight = with(LocalDensity.current) { maxHeight.roundToPx() }
        Layout(content = { Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { content() } },
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) { measurables, constraints ->
            val child = measurables.single().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
            val margin = 20.dp.roundToPx()
            val height = maxOf(viewportHeight, child.height + margin * 2)
            val top = margin + ((height - child.height - margin * 2) * .1f).roundToInt()
            layout(constraints.maxWidth, height) { child.placeRelative(0, top) }
        }
    }
}

@Composable
private fun PrizeMercadoPagoEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = CaixaDS.colors.legacyLine
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(54.dp)
            .drawBehind {
                drawLine(
                    color = lineColor,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height - 1.dp.toPx()),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            },
        textStyle = CaixaDS.typography.bodyLarge.copy(
            color = CaixaDSColorTokens.LegacyBetAction,
            fontSize = 20.sp,
            lineHeight = 24.sp,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                innerTextField()
            }
        },
    )
}

@Composable
private fun PrizeSuccessDataScreen(
    state: PrizeRedemptionUiState,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    onReceipt: () -> Unit,
) {
    val isPix = state.selectedChannel == PrizeRedemptionChannel.PIX
    BackHandler(onBack = if (isPix) onMyBets else onHome)
    if (isPix) {
        CaixaDSLegacyScaffold(
            title = "Resgate de Prêmio via Pix",
            onBack = null,
            topBarAction = { PrizeHelpAction(onHelp) },
            containerColor = CaixaDSColorTokens.White,
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CaixaDSPixBrand(Modifier.padding(top = 40.dp).size(145.dp, 52.dp))
                LegacyRedemptionSuccessBanner(
                    modifier = Modifier.padding(top = 35.dp),
                    text = "Seu resgate foi solicitado com sucesso!",
                    fontSize = 20.sp,
                )
                Text(
                    "Você pode acompanhar a situação do resgate pelo menu Minhas apostas enquanto ele é processado.",
                    Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    textAlign = TextAlign.Center,
                    color = CaixaDS.colors.legacyMutedText,
                    fontSize = 20.sp,
                    lineHeight = 25.sp,
                )
                LegacyRedemptionActionButton(
                    "Acesse \nMinhas apostas",
                    onMyBets,
                    Modifier.padding(top = 24.dp),
                    color = CaixaDSColorTokens.LegacyRedemptionButton,
                    cornerRadius = 4,
                )
            }
        }
    } else {
        CaixaDSLegacyScaffold(
            title = "Resgate de Prêmio via Mercado Pago",
            onBack = null,
            topBarAction = {
                IconButton(onClick = onHelp) {
                    Icon(
                        CaixaDSIcons.Help,
                        contentDescription = "Ajuda",
                        tint = CaixaDSColorTokens.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
            topBarNavigationWidth = 0.dp,
            topBarTitleSize = 20.sp,
            containerColor = CaixaDSColorTokens.White,
            bottomBar = {
                Box(Modifier.fillMaxWidth().height(208.dp)) {
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(176.dp)
                            .background(CaixaDSColorTokens.LegacyMercadoPagoFooter),
                    )
                    LegacyRedemptionActionButton(
                        text = "Voltar ao início",
                        onClick = onHome,
                        modifier = Modifier.align(Alignment.TopCenter),
                        color = CaixaDS.colors.legacyTeal,
                    )
                    LegacyRedemptionActionButton(
                        text = "Resgatar novo prêmio",
                        onClick = onMyBets,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                        color = CaixaDS.colors.legacyTeal,
                    )
                }
            },
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CaixaDSMercadoPagoBrand(Modifier.padding(top = 45.dp).size(177.dp, 46.dp))
                LegacyRedemptionSuccessBanner(
                    modifier = Modifier.padding(top = 40.dp),
                    text = "Solicitação efetuada com sucesso!",
                    fontSize = 18.2.sp,
                )
                Text(
                    "Em breve seu prêmio estará disponível  em sua conta no mercado pago",
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp),
                    textAlign = TextAlign.Center,
                    color = CaixaDS.colors.legacyMutedText,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Um e-mail foi enviado para ${state.email} com os dados de recebimento do seu prêmio.",
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 19.dp),
                    textAlign = TextAlign.Center,
                    color = CaixaDS.colors.legacyMutedText,
                )
                Box(
                    Modifier.fillMaxWidth().padding(top = 30.dp, bottom = 20.dp)
                        .background(legacyRedemptionGradient()).clickable(onClick = onReceipt)
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Visualizar comprovante de prêmio", color = CaixaDSColorTokens.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LegacyRedemptionSuccessBanner(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.2.sp,
) {
    Box(
        modifier.fillMaxWidth().height(80.dp).background(legacyRedemptionGradient()),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = CaixaDSColorTokens.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
        )
    }
}

@Composable
private fun LegacyRedemptionActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color,
    cornerRadius: Int = 10,
    compactContent: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(184.dp, 84.dp),
        shape = RoundedCornerShape(cornerRadius.dp),
        contentPadding = if (compactContent) PaddingValues(0.dp) else ButtonDefaults.ContentPadding,
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = CaixaDSColorTokens.White),
    ) {
        Text(text, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 17.7.sp, maxLines = if (compactContent) 1 else 2)
    }
}

@Composable
private fun PrizeHelpAction(onHelp: () -> Unit) {
    IconButton(onClick = onHelp) {
        Icon(CaixaDSIcons.Help, "Ajuda", tint = CaixaDSColorTokens.White, modifier = Modifier.size(24.dp))
    }
}

private fun legacyRedemptionGradient() = Brush.horizontalGradient(
    listOf(CaixaDSColorTokens.LegacyRedemptionButton, CaixaDSColorTokens.Blue700),
)
