package br.gov.caixa.loterias.feature.tickets

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPurchaseFilterIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPurchaseHelpIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPurchaseFavoriteIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPixPanel
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyEmptyState
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyOutlinedButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySectionHeader
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.Purchase
import br.gov.caixa.loterias.core.domain.tickets.TicketFilterOption
import java.text.NumberFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun ConfirmedBetsDataRoute(viewModel: TicketsViewModel, onBack: () -> Unit, onOpen: (ConfirmedBet) -> Unit,
    onAddToCart: ((ConfirmedBet) -> Unit)? = null, onBetNow: (() -> Unit)? = null,
    onHelp: (() -> Unit)? = null) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { if (!state.loaded) viewModel.loadBets() }
    CaixaDSLegacyScaffold(title = "Minhas apostas", onBack = onBack,
        topBarTitleWeight = FontWeight.Normal,
        topBarAnnotatedTitle = buildAnnotatedString {
            append("Minhas ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("apostas") }
        },
        // Scaffold reserves 48dp by default; both legacy actions need their complete widths.
        topBarActionWidth = if (onHelp != null) 93.dp else 48.dp,
        topBarAction = { Row {
            LegacyTicketsFilterAction(state.betFilters.activeCount, viewModel::openBetFilters)
            onHelp?.let { LegacyTicketsHelpAction(it) }
        } },
        containerColor = CaixaDSColorTokens.White) { padding ->
        androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item { LegacyConfirmedBetsIntro(state.historyBackend) }
            item { LegacyTicketPeriodSelector(
                "Selecione o período",
                state.betFilters.period?.let { listOfNotNull(it.description, it.year?.toString()).joinToString(" de ") }.orEmpty(),
                state.betConfiguration?.periods.orEmpty().map { listOfNotNull(it.description, it.year?.toString()).joinToString(" de ") },
                Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp),
            ) { index -> state.betConfiguration?.periods?.getOrNull(index)?.let { viewModel.selectBetPeriod(it); viewModel.applyBetFilters() } } }
            item { Spacer(Modifier.height(16.dp)) }
            when {
                state.isLoading -> item { LoadingData(Modifier.height(100.dp)) }
                state.errorMessage != null -> item { ErrorData(state.errorMessage.orEmpty(), viewModel::retryBets) }
                state.loaded && state.bets.isEmpty() -> item { LegacyEmptyBets(onBetNow) }
                else -> items(state.bets.size, key = { state.bets[it].id }) { index ->
                    val bet = state.bets[index]
                    LegacyConfirmedBetRow(bet, { onOpen(bet) }, onAddToCart?.let { { it(bet) } })
                }
            }
            item { Spacer(Modifier.height(120.dp)) }
        }
    }
    if (state.showBetFilters) BetFiltersDialog(state, viewModel)
}

@Composable
fun PurchasesDataRoute(viewModel: TicketsViewModel, onBack: () -> Unit, onOpen: (Purchase) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { if (!state.loaded) viewModel.loadPurchases() }
    CaixaDSLegacyScaffold(title = "Minhas Compras", onBack = onBack,
        topBarAction = { LegacyTicketsFilterAction(state.purchaseFilters.activeCount, viewModel::openPurchaseFilters) },
        containerColor = CaixaDSColorTokens.White) { padding ->
        androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item { LegacyPurchaseIntro() }
            item { LegacyTicketPeriodSelector(
                "Selecione o mês e ano", state.purchaseFilters.month?.description.orEmpty(),
                state.purchaseMonths.map { it.description },
                Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp),
            ) { index -> state.purchaseMonths.getOrNull(index)?.let(viewModel::selectPurchaseMonth) } }
            item { Box(Modifier.fillMaxWidth().padding(horizontal = 17.dp).height(1.dp).background(CaixaDSColorTokens.LegacyTicketDisabledBackground)) }
            when {
                state.isLoading -> item { LoadingData(Modifier.height(100.dp)) }
                state.errorMessage != null -> item { ErrorData(state.errorMessage.orEmpty(), viewModel::retryPurchases) }
                state.loaded && state.purchases.isEmpty() -> item { LegacyEmptyPurchases() }
                else -> items(state.purchases.size, key = { state.purchases[it].id }) { index ->
                    val purchase = state.purchases[index]
                    LegacyPurchaseRow(purchase) { onOpen(purchase) }
                }
            }
            if (state.canLoadMore || state.isLoadingMore) {
                item {
                    LaunchedEffect(state.purchases.size) { viewModel.loadMorePurchases() }
                    if (state.isLoadingMore) CircularProgressIndicator(Modifier.padding(5.dp))
                }
            }
        }
    }
    if (state.showPurchaseFilters) PurchaseFiltersDialog(state, viewModel)
}

@Composable
private fun BetFiltersDialog(state: TicketsUiState, viewModel: TicketsViewModel) {
    val configuration = state.betConfiguration ?: return
    AlertDialog(
        onDismissRequest = viewModel::closeBetFilters,
        title = { Text("Filtrar apostas") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                FilterSection("Período") {
                    configuration.periods.forEach { period ->
                        FilterRadio(
                            listOfNotNull(period.description, period.year?.toString()).joinToString(" de "),
                            state.betFilters.period == period,
                        ) { viewModel.selectBetPeriod(period) }
                    }
                }
                FilterOptions("Situação", configuration.statuses, state.betFilters.status, viewModel::selectBetStatus)
                FilterOptions("Ordenar por", configuration.orderings, state.betFilters.ordering, viewModel::selectBetOrdering)
                FilterOptions("Tipo de aposta", configuration.betTypes, state.betFilters.betType, viewModel::selectBetType)
                FilterOptions("Tipo de concurso", configuration.contestTypes, state.betFilters.contestType, viewModel::selectContestType)
                FilterSection("Modalidade") {
                    FilterRadio("Todas", state.betFilters.modality == 0) { viewModel.selectModality(0) }
                    state.modalities.forEach { modality ->
                        FilterRadio(modality.name, state.betFilters.modality == modality.id) {
                            viewModel.selectModality(modality.id)
                        }
                    }
                }
                FilterCheck("Surpresinha", state.betFilters.surprise, viewModel::toggleSurprise)
                FilterCheck("Teimosinha", state.betFilters.recurring, viewModel::toggleRecurring)
                FilterCheck("Combos", state.betFilters.combo, viewModel::toggleCombo)
            }
        },
        confirmButton = { TextButton(onClick = viewModel::applyBetFilters) { Text("Aplicar") } },
        dismissButton = {
            Row {
                TextButton(onClick = viewModel::clearBetFilters) { Text("Limpar") }
                TextButton(onClick = viewModel::closeBetFilters) { Text("Cancelar") }
            }
        },
    )
}

@Composable
private fun PurchaseFiltersDialog(state: TicketsUiState, viewModel: TicketsViewModel) {
    AlertDialog(
        onDismissRequest = viewModel::closePurchaseFilters,
        title = { Text("Filtrar compras") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                FilterSection("Meio de pagamento") {
                    FilterRadio("Todos", state.purchaseFilters.paymentMethodId == null) {
                        viewModel.selectPaymentMethod(null)
                    }
                    state.paymentMethods.forEach { option ->
                        FilterRadio(option.description, state.purchaseFilters.paymentMethodId == option.value) {
                            viewModel.selectPaymentMethod(option.value)
                        }
                    }
                }
                FilterSection("Situação") {
                    FilterRadio("Todas", state.purchaseFilters.statusId == null) {
                        viewModel.selectPurchaseStatus(null)
                    }
                    state.purchaseStatuses.forEach { option ->
                        FilterRadio(option.description, state.purchaseFilters.statusId == option.value) {
                            viewModel.selectPurchaseStatus(option.value)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = viewModel::applyPurchaseFilters) { Text("Aplicar") } },
        dismissButton = {
            Row {
                TextButton(onClick = viewModel::clearPurchaseFilters) { Text("Limpar") }
                TextButton(onClick = viewModel::closePurchaseFilters) { Text("Cancelar") }
            }
        },
    )
}

@Composable
private fun FilterOptions(
    title: String,
    options: List<TicketFilterOption>,
    selected: Long,
    onSelect: (Long) -> Unit,
) {
    if (options.isEmpty()) return
    FilterSection(title) {
        options.forEach { option ->
            FilterRadio(option.description, selected == option.value) { onSelect(option.value) }
        }
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, color = CaixaDS.colors.brand, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
private fun FilterRadio(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
    }
}

@Composable
private fun FilterCheck(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = selected, onCheckedChange = { onClick() })
        Text(label)
    }
}

@Composable
fun PurchaseDetailDataRoute(
    viewModel: TicketsViewModel,
    purchase: Purchase,
    onBack: () -> Unit,
    onOpen: (String) -> Unit = {},
    onPixRedirect: (br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect) -> Unit = {},
    onPixRegistration: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    br.gov.caixa.loterias.core.presentation.NavigationResumeEffect(viewModel::resumePixPurchaseAfterRedirect)
    LaunchedEffect(state.pixPurchaseRedirect) {
        state.pixPurchaseRedirect?.let { if (viewModel.consumePixPurchaseRedirect(it)) onPixRedirect(it) }
    }
    state.pixPurchaseNotice?.let { notice ->
        val registration = notice.offerRegistration && onPixRegistration != null
        CaixaDSLegacyDialog(title = "Atenção", message = notice.message,
            positiveLabel = when { registration -> "Sim"; notice.offerMoreBets -> "Apostar mais"; else -> "Entendi" },
            onPositive = {
                if (viewModel.consumePixPurchaseNotice(notice, notice.offerMoreBets || registration)) {
                    if (registration) onPixRegistration?.invoke()
                    else if (notice.offerMoreBets) onOpen("home")
                }
            },
            negativeLabel = when { registration -> "Não"; notice.offerMoreBets -> "Entendi"; else -> null },
            onNegative = { viewModel.consumePixPurchaseNotice(notice) })
    }
    LaunchedEffect(purchase.id) { viewModel.loadPurchase(purchase) }
    CaixaDSLegacyScaffold(
        title = "Apostas da Compra",
        contentUsesTruncatedToolbarGuideline = true,
        topBarTitleSize = 20.sp,
        onBack = onBack,
        containerColor = CaixaDSColorTokens.White,
        topBarActionWidth = 96.dp,
        topBarAction = {
            Row {
                Box {
                    IconButton(onClick = viewModel::openPurchaseBetFilters) {
                        Icon(CaixaDSLegacyPurchaseFilterIcon, contentDescription = "Filtrar", tint = CaixaDSColorTokens.White)
                    }
                    if (state.purchaseBetFilters.activeCount > 0) {
                        Box(
                            Modifier.align(Alignment.TopEnd).size(16.dp)
                                .background(CaixaDS.colors.error, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                state.purchaseBetFilters.activeCount.toString(),
                                color = CaixaDSColorTokens.White,
                                style = CaixaDS.typography.labelSmall,
                            )
                        }
                    }
                }
                IconButton(onClick = { onOpen("support/terms") }) {
                    CaixaDSLegacyPurchaseHelpIcon(Modifier.size(24.dp), contentDescription = "Ajuda")
                }
            }
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingData(Modifier.padding(padding))
            state.errorMessage != null -> ErrorData(state.errorMessage.orEmpty(), { viewModel.loadPurchase(purchase) }, Modifier.padding(padding))
            else -> androidx.compose.foundation.lazy.LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(0.dp),
            ) {
                val detail = state.selectedPurchase ?: purchase
                val displayedBets = state.bets.filter(state.purchaseBetFilters::matches)
                item {
                    PurchaseLegacyHeader(
                        purchase = detail,
                        canFavorite = state.bets.none(ConfirmedBet::poolShare),
                        onFavorite = viewModel::openFavoriteDialog,
                    )
                }
                if (detail.isPendingPix()) item { PendingPurchasePix(detail, state.checkingPixPurchase, viewModel::checkPendingPixStatus) }
                if (displayedBets.isEmpty()) {
                    item {
                        CaixaDSLegacyEmptyState(
                            title = "Nenhuma aposta encontrada",
                            message = "Revise os filtros aplicados.",
                            actionLabel = "Limpar filtros",
                            onAction = viewModel::clearPurchaseBetFilters,
                        )
                    }
                } else {
                    items(displayedBets.size, key = { displayedBets[it].id }) { index ->
                        val bet = displayedBets[index]
                        if (bet.numbers.isNotEmpty() && bet.numberColumns.size <= 1 &&
                            bet.clovers.isEmpty() && !bet.poolShare && bet.lotecaSelections.isEmpty() &&
                            bet.lotogolSelections.isEmpty() && bet.favoriteTeam == null && bet.luckyMonth == null &&
                            !bet.combo && !bet.mirror && !bet.exchange && bet.exchangeBetId == null && !bet.surprise
                        ) LegacyNumericPurchaseBetRow(bet)
                        else BetDataCard(bet) {}
                    }
                }
            }
        }
    }
    if (state.showPurchaseBetFilters) PurchaseBetFiltersDialog(state, viewModel)
    state.purchaseStatusDialog?.let { dialog ->
        PurchaseStatusDialogView(dialog) { action ->
            when (action) {
                PurchaseDialogAction.SAVE_FAVORITE -> viewModel.openFavoriteDialog()
                PurchaseDialogAction.HELP -> {
                    viewModel.dismissPurchaseStatusDialog()
                    onOpen("support/faq")
                }
                PurchaseDialogAction.DISMISS -> viewModel.dismissPurchaseStatusDialog()
                PurchaseDialogAction.NEW_PIX -> {
                    viewModel.dismissPurchaseStatusDialog()
                    onOpen("payment/pix-guidance")
                }
                PurchaseDialogAction.CHANGE_PAYMENT -> {
                    viewModel.dismissPurchaseStatusDialog()
                    onOpen("payment/method")
                }
                PurchaseDialogAction.HOME -> {
                    viewModel.dismissPurchaseStatusDialog()
                    onOpen("home")
                }
            }
        }
    }
    if (state.showFavoriteDialog) FavoritePurchaseDialog(state, viewModel)
    if (state.favoriteOverwriteConfirmation) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Já existe um carrinho favorito com este nome. Deseja substituí-lo?",
            positiveLabel = "Sim",
            onPositive = viewModel::confirmFavoriteOverwrite,
            negativeLabel = "Não",
            onNegative = viewModel::cancelFavoriteOverwrite,
        )
    }
    if (state.favoriteSaved) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Carrinho favorito salvo com sucesso.",
            positiveLabel = "Entendi",
            onPositive = {
                viewModel.dismissFavoriteSaved()
                onOpen("cart/favorites")
            },
        )
    }
    state.damMessage?.let { message ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = message,
            positiveLabel = "Entendi",
            onPositive = viewModel::dismissDamMessage,
        )
    }
}

@Composable
private fun PurchaseLegacyHeader(purchase: Purchase, canFavorite: Boolean, onFavorite: () -> Unit) {
    LegacyPurchaseDetailHeader(purchase, purchaseStatusColor(purchase), canFavorite, onFavorite)
}

@Composable
private fun PurchaseBetFiltersDialog(state: TicketsUiState, viewModel: TicketsViewModel) {
    AlertDialog(
        onDismissRequest = viewModel::closePurchaseBetFilters,
        title = { Text("Filtrar apostas") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("Concurso", fontWeight = FontWeight.Bold)
                FilterRadio("Todos", state.purchaseBetFilters.contest == null) {
                    viewModel.selectPurchaseBetContest(null)
                }
                state.bets.map(ConfirmedBet::contest).filter { it > 0 }.distinct().sorted().forEach { contest ->
                    FilterRadio(contest.toString(), state.purchaseBetFilters.contest == contest) {
                        viewModel.selectPurchaseBetContest(contest)
                    }
                }
                Text("Situação", fontWeight = FontWeight.Bold)
                FilterRadio("Todas", state.purchaseBetFilters.statusId == null) {
                    viewModel.selectPurchaseBetStatus(null)
                }
                state.bets.distinctBy { it.statusId to it.status }.forEach { bet ->
                    FilterRadio(bet.status.ifBlank { "Não informada" }, state.purchaseBetFilters.statusId == bet.statusId) {
                        viewModel.selectPurchaseBetStatus(bet.statusId)
                    }
                }
                Text("Modalidade", fontWeight = FontWeight.Bold)
                FilterRadio("Todas", state.purchaseBetFilters.modalityId == null) {
                    viewModel.selectPurchaseBetModality(null)
                }
                state.bets.distinctBy { it.modalityId to it.modality }.forEach { bet ->
                    FilterRadio(bet.modality, state.purchaseBetFilters.modalityId == bet.modalityId) {
                        viewModel.selectPurchaseBetModality(bet.modalityId)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = viewModel::applyPurchaseBetFilters) { Text("Aplicar") } },
        dismissButton = {
            Row {
                TextButton(onClick = viewModel::clearPurchaseBetFilters) { Text("Limpar") }
                TextButton(onClick = viewModel::closePurchaseBetFilters) { Text("Cancelar") }
            }
        },
    )
}

@Composable
private fun PurchaseStatusDialogView(dialog: PurchaseStatusDialog, onAction: (PurchaseDialogAction) -> Unit) {
    val positive = dialog.actions.getOrNull(0) ?: return
    val negative = dialog.actions.getOrNull(1)
    val neutral = dialog.actions.getOrNull(2)
    CaixaDSLegacyDialog(
        title = dialog.title,
        message = dialog.message,
        positiveLabel = positive.second,
        onPositive = { onAction(positive.first) },
        negativeLabel = negative?.second,
        onNegative = { negative?.let { onAction(it.first) } },
        neutralLabel = neutral?.second,
        onNeutral = { neutral?.let { onAction(it.first) } },
        onDismiss = { onAction(PurchaseDialogAction.DISMISS) },
    )
}

@Composable
private fun FavoritePurchaseDialog(state: TicketsUiState, viewModel: TicketsViewModel) {
    AlertDialog(
        onDismissRequest = viewModel::closeFavoriteDialog,
        title = { Text("Carrinho Favorito") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BasicTextField(
                    value = state.favoriteName,
                    onValueChange = viewModel::changeFavoriteName,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                        .border(1.dp, CaixaDS.colors.brand, RoundedCornerShape(4.dp)).padding(14.dp),
                    singleLine = true,
                    decorationBox = { field ->
                        Box {
                            if (state.favoriteName.isBlank()) Text("Nome do carrinho", color = CaixaDS.colors.legacyMutedText)
                            field()
                        }
                    },
                )
                if (state.bets.any(ConfirmedBet::surprise)) {
                    FilterCheck("Manter surpresinhas", state.keepSurprises, viewModel::toggleKeepSurprises)
                }
                state.errorMessage?.let { Text(it, color = CaixaDS.colors.error) }
            }
        },
        confirmButton = { TextButton(onClick = viewModel::validateAndSaveFavorite) { Text("OK") } },
        dismissButton = { TextButton(onClick = viewModel::closeFavoriteDialog) { Text("Cancelar") } },
    )
}

@Composable
private fun PendingPurchasePix(purchase: Purchase, checking: Boolean, onCheckPayment: () -> Unit) {
    var remaining by remember(purchase.paymentExpiresAt) { mutableLongStateOf(purchase.paymentSecondsRemaining()) }
    var copied by remember(purchase.id) { androidx.compose.runtime.mutableStateOf(false) }
    LaunchedEffect(purchase.paymentExpiresAt) {
        while (remaining > 0) { delay(1_000); remaining = purchase.paymentSecondsRemaining() }
    }
    LaunchedEffect(purchase.id, copied) {
        if (copied) while (remaining > 0) {
            delay(20_000)
            if (remaining > 0) onCheckPayment()
        }
    }
    CaixaDSLegacyPixPanel(
        purchaseId = purchase.id, copyPaste = purchase.pixCopyPaste, remainingSeconds = remaining,
        checking = checking, onCheckPayment = onCheckPayment, onCopied = { copied = true }, onPurchases = {},
        showIntro = false, showHeader = false, showRecoveryGuidance = false,
    )
}

@Composable
private fun purchaseStatusColor(purchase: Purchase) = when (purchase.statusId) {
    6L, 8L, 13L, 14L -> CaixaDSColorTokens.LegacyPurchaseStatus
    3L, 4L, 5L, 11L, 12L -> CaixaDSColorTokens.LegacyNotificationDeleteAction
    else -> CaixaDSColorTokens.LegacyReservationWarning
}

internal fun Purchase.isPendingPix(): Boolean =
    (paymentMethodId == 6L || paymentMethod.contains("pix", true)) &&
        (statusId == 10L || status.contains("aguardando", true)) && pixCopyPaste.isNotBlank()

internal fun Purchase.paymentSecondsRemaining(now: LocalDateTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))): Long {
    val serverZone = ZoneId.of("America/Sao_Paulo")
    val expiration = runCatching { OffsetDateTime.parse(paymentExpiresAt).toInstant() }
        .recoverCatching { LocalDateTime.parse(paymentExpiresAt).atZone(serverZone).toInstant() }
        .getOrNull() ?: return 0
    return Duration.between(now.atZone(serverZone).toInstant(), expiration).seconds.coerceAtLeast(0)
}

@Composable
fun ManualTicketDataRoute(
    viewModel: TicketsViewModel,
    onBack: () -> Unit,
    onResult: (List<ConfirmedBet>) -> Unit,
    onHelp: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.loaded, state.bets, state.ticketResult) {
        if (state.loaded && state.errorMessage == null && (state.ticketResult != null || state.bets.isNotEmpty())) onResult(state.bets)
    }
    val manualCodeValid = state.ticketCode.length >= TicketsViewModel.MINIMUM_MANUAL_TICKET_LENGTH
    CaixaDSLegacyScaffold(
        title = "Conferir bilhetes",
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp) {
                Icon(CaixaDSIcons.Help, contentDescription = "Termos de uso", tint = CaixaDSColorTokens.White)
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Text(
                "Por favor, informe o número do seu bilhete.",
                modifier = Modifier.padding(top = 40.dp),
                color = CaixaDS.colors.brand,
                fontSize = 14.2.sp,
                fontWeight = FontWeight.Bold,
            )
            LegacyTicketUnderlineField(
                value = state.ticketCode.legacyNsbMask(),
                onValueChange = viewModel::changeTicketCode,
                hint = "NSB",
                valid = manualCodeValid,
                modifier = Modifier.padding(top = 40.dp),
            )
            LegacyTicketUnderlineField(
                value = state.ticketRetailerCode.legacyRetailerMask(),
                onValueChange = viewModel::changeTicketRetailerCode,
                hint = "Lotérico",
                valid = state.ticketRetailerCode.length == 9,
                modifier = Modifier.padding(top = 20.dp),
            )
            Button(
                onClick = { viewModel.checkTicket() },
                enabled = manualCodeValid,
                modifier = Modifier.fillMaxWidth().padding(top = 49.dp).height(55.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CaixaDS.colors.legacyTeal,
                    contentColor = CaixaDSColorTokens.White,
                    disabledContainerColor = CaixaDSColorTokens.LegacyTicketDisabledBackground,
                    disabledContentColor = CaixaDSColorTokens.LegacyTicketDisabledContent,
                ),
            ) {
                Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                state.errorMessage != null -> Text(state.errorMessage.orEmpty(), color = CaixaDS.colors.error)
                state.loaded && state.bets.isEmpty() -> Text("Nenhuma aposta encontrada neste bilhete.", color = CaixaDS.colors.legacyMutedText)
                else -> state.bets.forEach { BetDataCard(it) {} }
            }
        }
    }
}

@Composable
private fun LegacyTicketUnderlineField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    valid: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().height(52.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().weight(1f),
            singleLine = true,
            textStyle = CaixaDS.typography.bodyLarge.copy(color = CaixaDS.colors.legacyText, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            decorationBox = { field ->
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        if (value.isBlank()) Text(hint, color = CaixaDSColorTokens.LegacyTealAction, fontSize = 14.sp)
                        field()
                    }
                    if (valid) Icon(CaixaDSIcons.Confirm, null, tint = CaixaDS.colors.legacyTeal, modifier = Modifier.size(24.dp))
                }
            },
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.legacyLine))
    }
}

internal fun String.legacyNsbMask(): String = filter(Char::isLetterOrDigit).uppercase().take(23).let { raw ->
    buildString {
        append(raw.take(4))
        if (raw.length > 4) append('-').append(raw.substring(4, minOf(raw.length, 21)))
        if (raw.length > 21) append('-').append(raw.substring(21))
    }
}

internal fun String.legacyRetailerMask(): String = filter(Char::isLetterOrDigit).uppercase().take(9).let { raw ->
    buildString {
        append(raw.take(2))
        if (raw.length > 2) append('.').append(raw.substring(2, minOf(raw.length, 8)))
        if (raw.length > 8) append('-').append(raw.substring(8))
    }
}

@Composable
fun TicketResultDataRoute(bets: List<ConfirmedBet>, onBack: () -> Unit, onSave: () -> Unit,
    visualData: TicketResultVisualData? = null, onBetNow: (() -> Unit)? = null) {
    CaixaDSLegacyScaffold(title = "Resultado da aposta", onBack = onBack, containerColor = CaixaDSColorTokens.White) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LegacyTicketResultContent(bets, visualData, onBetNow, onSave)
        }
    }
}

@Composable
private fun DataListState(
    state: TicketsUiState,
    padding: PaddingValues,
    emptyTitle: String,
    retry: () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    when {
        state.isLoading -> LoadingData(Modifier.padding(padding))
        state.errorMessage != null -> ErrorData(state.errorMessage, retry, Modifier.padding(padding))
        state.loaded && state.bets.isEmpty() && state.purchases.isEmpty() -> CaixaDSLegacyEmptyState(
            title = emptyTitle,
            message = "Altere os filtros ou tente novamente mais tarde.",
            modifier = Modifier.padding(padding),
        )
        else -> androidx.compose.foundation.lazy.LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}

@Composable
private fun LoadingData(modifier: Modifier = Modifier) = Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

@Composable
private fun ErrorData(message: String, retry: () -> Unit, modifier: Modifier = Modifier) = CaixaDSLegacyEmptyState(
    title = "Não foi possível carregar",
    message = message,
    actionLabel = "Tentar novamente",
    onAction = retry,
    modifier = modifier,
)

@Composable
private fun BetDataCard(bet: ConfirmedBet, onOpen: () -> Unit) {
    CaixaDSLegacyCard(onClick = onOpen) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text("${bet.modality.ifBlank { "Aposta" }} • Concurso ${bet.contest}", color = CaixaDS.colors.brand, fontWeight = FontWeight.Bold)
            if (bet.numbers.isNotEmpty()) Text(bet.numbers.joinToString("  ") { it.toString().padStart(2, '0') })
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(bet.date, color = CaixaDS.colors.legacyMutedText)
                Text(bet.status.ifBlank { "Em acompanhamento" }, color = if (bet.prize.signum() > 0) CaixaDS.colors.success else CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PurchaseDataCard(purchase: Purchase, onOpen: (Purchase) -> Unit) {
    CaixaDSLegacyCard(onClick = { onOpen(purchase) }) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Compra ${purchase.date}", fontWeight = FontWeight.Bold)
                Text(purchase.status.ifBlank { "Situação não informada" }, color = CaixaDS.colors.legacyMutedText)
            }
            Text(purchase.total.currency(), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DataFilterChip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.height(40.dp).background(if (selected) CaixaDS.colors.brand else CaixaDS.colors.surface, RoundedCornerShape(20.dp))
            .border(1.dp, CaixaDS.colors.brand, RoundedCornerShape(20.dp)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(text, color = if (selected) CaixaDSColorTokens.White else CaixaDS.colors.brand, textAlign = TextAlign.Center) }
}

@Composable
private fun DetailDataLine(label: String, value: String, valueColor: Color = CaixaDS.colors.legacyText) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = CaixaDS.colors.legacyMutedText)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

private fun java.math.BigDecimal.currency(): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(this)
