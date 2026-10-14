package br.gov.caixa.loterias.feature.tickets

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyBetCartIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySelectionDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLoading
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet

@Composable
fun BetDetailDataRoute(
    viewModel: BetDetailViewModel,
    onBack: () -> Unit,
    onViewResult: (ConfirmedBet) -> Unit,
    onRedeemPrize: ((ConfirmedBet) -> Unit)? = null,
    onHelp: () -> Unit = {},
    onEdit: ((ConfirmedBet) -> Unit)? = null,
    onCart: () -> Unit = {},
    onServiceRedirect: (br.gov.caixa.loterias.core.domain.service.ServiceRedirect) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val failure = state.serviceFailure
    LaunchedEffect(failure) {
        if (failure is br.gov.caixa.loterias.core.domain.service.ServiceFailure.RedirectRequired) {
            viewModel.dismissServiceFailure()
            onServiceRedirect(failure.destination)
        }
    }
    if (failure != null && failure !is br.gov.caixa.loterias.core.domain.service.ServiceFailure.RedirectRequired) {
        CaixaDSAlertDialog(title = "Atenção", message = when (failure) {
            is br.gov.caixa.loterias.core.domain.service.ServiceFailure.NoticeRequired -> failure.notice.message ?: "Não foi possível consultar esta aposta."
            is br.gov.caixa.loterias.core.domain.service.ServiceFailure.ConnectionFailed -> "Não foi possível acessar o serviço. Verifique sua conexão."
            is br.gov.caixa.loterias.core.domain.service.ServiceFailure.RegistrationRequired -> failure.message
            else -> "Não foi possível consultar esta aposta."
        }, confirmText = "Tentar novamente", onConfirm = viewModel::loadDetails,
            onDismiss = { viewModel.dismissServiceFailure(); onBack() })
    }
    if (!state.detailsReady && !state.loading) {
        br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold(title = "Detalhes da aposta", onBack = onBack) { padding ->
            br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyEmptyState(
                title = "Não foi possível consultar esta aposta", message = state.errorMessage.orEmpty(),
                actionLabel = "Tentar novamente", onAction = viewModel::loadDetails,
                modifier = Modifier.padding(padding),
            )
        }
        return
    }
    val context = LocalContext.current
    var pendingReceipt by remember { mutableStateOf<BetReceipt?>(null) }
    var savedReceipt by remember { mutableStateOf<Uri?>(null) }
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
            savedReceipt = uri
            openPdf(uri)
        }
        pendingReceipt = null
    }
    LaunchedEffect(viewModel) {
        viewModel.receipts.collect { receipt ->
            savedReceipt?.let(::openPdf) ?: run {
                pendingReceipt = receipt
                createPdf.launch(receipt.fileName)
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        BetDetailRoute(
            bet = state.bet,
            onBack = onBack,
            onViewResult = { onViewResult(state.bet) },
            onRedeemPrize = onRedeemPrize?.let { callback -> { callback(state.bet) } },
            onHelp = onHelp,
            onEdit = onEdit?.let { callback -> { callback(state.bet) } },
            onCart = onCart,
            onAddToCart = viewModel::addCurrentBetToCart,
            onDownloadReceipt = viewModel::downloadReceipt,
            favoriteExpanded = state.favoriteExpanded,
            favoriteName = state.favoriteName,
            favoriteSaved = state.favoriteSaved,
            onToggleFavorite = viewModel::toggleFavoriteEditor,
            onFavoriteNameChange = viewModel::changeFavoriteName,
            onSaveFavorite = viewModel::submitFavorite,
        )
        if (state.loading) {
            Box(
                Modifier.fillMaxSize().background(CaixaDS.colors.surface.copy(alpha = 0.88f)),
                contentAlignment = Alignment.Center,
            ) {
                CaixaDSLoading("Carregando…")
            }
        }
    }
    BetRepeatActionsOverlay(viewModel, showLoading = false)
    if (state.favoriteOverwriteConfirmation) {
        CaixaDSAlertDialog(
            title = "Atenção",
            message = "Já existe uma aposta favorita com estes dados. Deseja salvar mesmo assim?",
            confirmText = "Sim",
            onConfirm = viewModel::confirmFavoriteOverwrite,
            onDismiss = viewModel::dismissFavoriteOverwrite,
        )
    }

}

@Composable
fun BetDetailRoute(
    bet: ConfirmedBet,
    onBack: () -> Unit,
    onViewResult: () -> Unit,
    onRedeemPrize: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
    onEdit: (() -> Unit)? = null,
    onCart: () -> Unit = {},
    onAddToCart: (() -> Unit)? = null,
    onDownloadReceipt: (() -> Unit)? = null,
    favoriteExpanded: Boolean = false,
    favoriteName: String = "",
    favoriteSaved: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onFavoriteNameChange: (String) -> Unit = {},
    onSaveFavorite: () -> Unit = {},
) {
    var showAlert by remember(bet.id, bet.originalRecurringBet, bet.redeemedPrize) {
        mutableStateOf(shouldShowInvalidTeimosinhaAlert(bet.originalRecurringBet, bet.redeemedPrize))
    }
    val pending = bet.status.contains("acompanhamento", ignoreCase = true) ||
        bet.status.contains("não apurado", ignoreCase = true)
    val modalityStyle = caixaDSLotteryStyle(bet.modalityId, bet.modality)
    val modalityColor = modalityStyle.dark
    val contentColor = modalityStyle.content
    val isLoteca = bet.modalityId == 19 || bet.modality.contains("loteca", true)
    val isLotogol = bet.modalityId == 14 || bet.modality.contains("lotogol", true)
    val sportsBet = isLoteca || isLotogol || bet.lotecaSelections.isNotEmpty() || bet.lotogolSelections.isNotEmpty()
    val canEdit = onEdit != null && !bet.poolShare && !sportsBet
    val showRepeatActions = !bet.poolShare && !isLoteca
    val canFavorite = bet.numbers.isNotEmpty()
    val bodyScroll = rememberScrollState()
    CaixaDSLegacyScaffold(
        title = bet.modality.replace('-', ' ').replace('_', ' ').lowercase(),
        onBack = onBack,
        topBarNavigationWidth = 52.dp,
        gradientTopBar = false,
        topBarStartColor = modalityStyle.light,
        topBarEndColor = modalityStyle.light,
        topBarContentColor = contentColor,
        topBarAction = {
            Icon(
                CaixaDSIcons.Help,
                contentDescription = "Dúvidas",
                modifier = Modifier.size(27.dp).clickable(onClick = onHelp),
                tint = contentColor,
            )
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().height(108.dp).background(CaixaDSColorTokens.White)) {
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    LegacyBottomAction("Editar\naposta", onEdit?.takeIf { canEdit }, edit = true, enabled = canEdit)
                    LegacyBottomAction("Carrinho\nde apostas", onCart, edit = false, enabled = true)
                }
                if (showRepeatActions) {
                    val expandedHeight = if (canFavorite && favoriteExpanded) 221 else if (canFavorite) 149 else 84
                    val yOffset = if (canFavorite && favoriteExpanded) (-166).dp else if (canFavorite) (-94).dp else (-29).dp
                    Column(
                        Modifier.align(Alignment.TopCenter).requiredHeight(expandedHeight.dp).offset(y = yOffset),
                    ) {
                        if (canFavorite) {
                            LegacyFavoriteAction(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                expanded = favoriteExpanded,
                                name = favoriteName,
                                saved = favoriteSaved,
                                onToggle = onToggleFavorite,
                                onNameChange = onFavoriteNameChange,
                                onSave = onSaveFavorite,
                            )
                            Spacer(Modifier.height(14.dp))
                        }
                        LegacyGrayAction("Adicionar\nao carrinho de apostas", onAddToCart, 84)
                    }
                }
            }
        },
        containerColor = modalityColor,
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(modalityColor)
                .then(if (sportsBet || bet.receipt?.netPrize != null) Modifier.verticalScroll(bodyScroll) else Modifier)
                .padding(horizontal = 24.dp),
        ) {
            Text("Concurso", Modifier.padding(top = 8.dp), color = contentColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(bet.contest.toString(), color = contentColor, fontSize = 18.sp)
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Data aposta:", color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(bet.date, color = contentColor, fontSize = 17.sp)
                    if (bet.time.isNotBlank()) Text(bet.time, color = contentColor, fontSize = 17.sp)
                }
                Box(Modifier.width(1.dp).height(64.dp).background(contentColor))
                Text(
                    if (pending) "Concurso\nnão apurado" else bet.status,
                    Modifier.weight(1.55f).padding(start = 18.dp),
                    color = contentColor,
                    fontSize = 17.sp,
                )
            }
            ConfirmedBetSelection(bet, contentColor)
            bet.receipt?.let { receipt ->
                if (receipt.nsbi.isNotBlank()) Text("NSBI: ${receipt.nsbi}", color = contentColor, fontSize = 14.sp)
                receipt.netPrize?.let { value ->
                    Text("Prêmio líquido: R$ ${value.setScale(2).toPlainString().replace('.', ',')}",
                        color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                if (receipt.paymentDate.isNotBlank()) Text("Pagamento: ${receipt.paymentDate} ${receipt.paymentTime}", color = contentColor, fontSize = 14.sp)
                if (receipt.paymentLocation.isNotBlank()) Text(receipt.paymentLocation, color = contentColor, fontSize = 14.sp)
                if (receipt.paymentNsu.isNotBlank()) Text("NSU: ${receipt.paymentNsu}", color = contentColor, fontSize = 14.sp)
                receipt.awards.forEach { award ->
                    Text("Concurso ${award.contest ?: bet.contest} • ${award.name} • ${award.status}", color = contentColor, fontSize = 14.sp)
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 27.dp), verticalAlignment = Alignment.Top) {
                Icon(CaixaDSIcons.Time, contentDescription = null, Modifier.size(23.dp), tint = contentColor)
                Column(Modifier.padding(start = 16.dp)) {
                    Text("Prazo para recebimento de prêmio:", color = contentColor, fontSize = 14.sp)
                    Text("90 dias após a data do sorteio", color = contentColor, fontSize = 14.sp)
                }
            }
            Box(Modifier.fillMaxWidth().padding(top = 24.dp).height(1.dp).background(contentColor.copy(alpha = 0.8f)))
            Box(
                Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp).width(300.dp).height(44.dp)
                    .background(CaixaDSColorTokens.Orange500).clickable(enabled = onDownloadReceipt != null) {
                        onDownloadReceipt?.invoke()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(CaixaDSIcons.Download, null, Modifier.size(22.dp), tint = CaixaDSColorTokens.White)
                    Text("Gerar comprovante", Modifier.padding(start = 8.dp), color = CaixaDSColorTokens.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            if (bet.originalRecurringBet) {
                Text(
                    if (bet.redeemedPrize) "Acompanhamento disponível na aposta de troca" else "Aposta em acompanhamento",
                    Modifier.padding(top = 12.dp),
                    color = contentColor,
                    fontSize = 12.sp,
                )
            }
            if (sportsBet || bet.receipt?.netPrize != null) Spacer(Modifier.height(190.dp)) else Spacer(Modifier.weight(1f))
            if (!pending) {
                LegacyGrayAction("Ver resultado", onViewResult, 56)
            }
            if (bet.prize.signum() > 0 && !bet.redeemedPrize && onRedeemPrize != null) {
                LegacyGrayAction("Resgatar prêmio", onRedeemPrize, 56)
            }
        }
    }
    if (showAlert) {
        CaixaDSAlertDialog(
            title = "Atenção",
            message = buildAnnotatedString {
                append("Esta aposta com Teimosinha não é mais válida para acompanhamento. Utilize a ")
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("aposta de troca") }
                append(" gerada após o resgate do prêmio, para acompanhar os próximos sorteios.")
            },
            confirmText = "Entendi",
            onConfirm = { showAlert = false },
            onDismiss = { },
        )
    }
}

@Composable
private fun LegacyFavoriteAction(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    name: String,
    saved: Boolean,
    onToggle: () -> Unit,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    val background = if (saved) CaixaDSColorTokens.LegacyFavoriteSaved else CaixaDSColorTokens.LegacyBetAction
    Column(
        modifier.width(255.dp).background(background).clickable(onClick = onToggle)
            .padding(horizontal = 15.dp, vertical = 17.dp),
    ) {
        Text(
            if (saved) "★  Aposta salva :)" else "☆  Salve esta aposta :)",
            color = CaixaDSColorTokens.White,
            fontSize = 14.sp,
        )
        if (expanded && !saved) {
            Text("Dê um nome", Modifier.padding(top = 14.dp), color = CaixaDSColorTokens.LegacyFavoriteSaved, fontSize = 12.sp)
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSave() }),
                trailingIcon = {
                    Icon(
                        CaixaDSIcons.Forward,
                        contentDescription = "Salvar aposta",
                        modifier = Modifier.size(28.dp).clickable(onClick = onSave),
                        tint = CaixaDSColorTokens.LegacyFavoriteSaved,
                    )
                },
            )
        }
    }
}

@Composable
private fun LegacyGrayAction(
    label: String,
    onClick: (() -> Unit)?,
    height: Int,
    weight: FontWeight = FontWeight.Bold,
) {
    Box(
        Modifier.fillMaxWidth().height(height.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.width(255.dp).height(height.dp).background(CaixaDSColorTokens.LegacyBetAction)
                .clickable(enabled = onClick != null) { onClick?.invoke() },
            contentAlignment = Alignment.Center,
        ) {
            Text(label, color = CaixaDSColorTokens.White, fontSize = if (height > 56) 16.sp else 17.sp, fontWeight = weight, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun LegacyBottomAction(label: String, onClick: (() -> Unit)?, edit: Boolean, enabled: Boolean) {
    val tint = if (enabled) CaixaDSColorTokens.LegacyBetIcon else CaixaDSColorTokens.Neutral300
    Column(
        Modifier.width(64.dp).clickable(enabled = onClick != null) { onClick?.invoke() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (edit) {
            Icon(
                painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_editar_aposta),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = tint,
            )
        } else {
            Icon(CaixaDSLegacyBetCartIcon, null, Modifier.size(36.dp), tint = tint)
        }
        Text(label, Modifier.padding(top = 4.dp), color = if (enabled) CaixaDSColorTokens.LegacyBetNavigationLink else CaixaDSColorTokens.Neutral300, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontFamily = CaixaDSFontFamily)
    }
}

/** Shared list/detail repeat action dialogs; preserves the existing ViewModel decisions and callbacks. */
@Composable
fun BetRepeatActionsOverlay(viewModel: BetDetailViewModel, showLoading: Boolean = true,
    onServiceRedirect: ((br.gov.caixa.loterias.core.domain.service.ServiceRedirect) -> Unit)? = null,
    onCartChanged: (() -> Unit)? = null) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (onServiceRedirect != null) {
        val failure = state.serviceFailure
        LaunchedEffect(failure) {
            if (failure is br.gov.caixa.loterias.core.domain.service.ServiceFailure.RedirectRequired) {
                viewModel.dismissServiceFailure(); onServiceRedirect(failure.destination)
            }
        }
        if (failure != null && failure !is br.gov.caixa.loterias.core.domain.service.ServiceFailure.RedirectRequired) {
            CaixaDSAlertDialog("Atenção", when (failure) {
                is br.gov.caixa.loterias.core.domain.service.ServiceFailure.NoticeRequired -> failure.notice.message ?: "Não foi possível consultar esta aposta."
                is br.gov.caixa.loterias.core.domain.service.ServiceFailure.RegistrationRequired -> failure.message
                else -> "Não foi possível acessar o serviço. Tente novamente."
            }, "Entendi", viewModel::dismissServiceFailure)
        }
    }
    if (showLoading && state.loading) {
        Box(Modifier.fillMaxSize().background(CaixaDS.colors.surface.copy(alpha = 0.88f)), contentAlignment = Alignment.Center) {
            CaixaDSLoading("Carregando…")
        }
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissError)
    }
    if (state.cartAdditionConfirmed) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Aposta adicionada ao carrinho com sucesso!",
            positiveLabel = "Entendi",
            onPositive = { viewModel.dismissCartAddition(); onCartChanged?.invoke() },
            onDismiss = viewModel::dismissCartAddition,
        )
    }
    if (state.repeatContestOptions.isNotEmpty()) {
        CaixaDSLegacySelectionDialog(
            title = "Tipo de concurso",
            message = "Escolha o tipo de concurso",
            options = state.repeatContestOptions.map(RepeatContestOption::label),
            selectedIndex = state.selectedRepeatContestIndex,
            onSelect = viewModel::selectRepeatContest,
            onConfirm = viewModel::confirmRepeatContest,
            onDismiss = viewModel::dismissRepeatContest,
        )
    }
    state.repeatConversion?.let { conversion ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = conversion.conversionMessage,
            positiveLabel = "Sim",
            onPositive = viewModel::confirmRepeatConversion,
            negativeLabel = "Não",
            onNegative = viewModel::dismissRepeatContest,
            onDismiss = viewModel::dismissRepeatContest,
        )
    }
}
