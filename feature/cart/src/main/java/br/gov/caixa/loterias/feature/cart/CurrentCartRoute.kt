package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSTextInputDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSCartActions
import br.gov.caixa.loterias.core.designsystem.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.CaixaDSCartSummaryTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPurchaseFavoriteIcon
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemBadge
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.CartPaymentAccountDecision
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.ServiceFailureOverlay
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun CurrentCartRoute(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onContinueBetting: () -> Unit,
    onPayment: () -> Unit,
    onLogin: () -> Unit,
    onAccountRequired: (PostLoginDestination) -> Unit,
    onLocationBlocked: () -> Unit,
    onValidationRedirect: (ServiceRedirect) -> Unit,
    onPoolDetail: (String) -> Unit = {},
    onComboDetail: (Long) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cart = state.cart
    ServiceFailureOverlay(state.validationFailure, viewModel::acceptValidationNotice,
        viewModel::dismissValidationNotice, viewModel::consumeValidationRedirect, onValidationRedirect)
    CartLocationDialogs(state, viewModel, onLocationBlocked)
    LaunchedEffect(state.paymentAccountDecision) {
        val decision = state.paymentAccountDecision
        if (decision is CartPaymentAccountDecision.CompleteAccount) {
            viewModel.consumePaymentAccountDecision()
            onAccountRequired(decision.destination)
        }
    }
    LaunchedEffect(state.paymentReady) {
        if (state.paymentReady) {
            viewModel.consumePaymentReady()
            onPayment()
        }
    }
    val reservedPoolCodes = cart?.items.orEmpty().mapNotNull { it.poolShare?.code }.sorted()
    LaunchedEffect(reservedPoolCodes) {
        while (reservedPoolCodes.isNotEmpty()) {
            viewModel.refreshExpiredReservations()
            delay(60_000)
        }
    }
    CaixaDSLegacyScaffold(
        title = "Carrinho de apostas",
        topBarTitleSize = 20.sp,
        topBarTitleWeight = FontWeight.SemiBold,
        onBack = onBack,
        containerColor = CaixaDSColorTokens.White,
        topBarActionWidth = 96.dp,
        topBarAction = {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                if (canClearCart(cart)) {
                    IconButton(onClick = viewModel::requestClear, modifier = Modifier.size(48.dp)) {
                        Icon(painterResource(CaixaDSR.drawable.legacy_cart_toolbar_delete), "Excluir todas as apostas", Modifier.size(19.dp, 25.dp), tint = CaixaDSColorTokens.White)
                    }
                } else {
                    Spacer(Modifier.size(48.dp))
                }
                if (canFavoriteCart(cart, state.authenticated)) {
                    IconButton(onClick = viewModel::requestFavorite, modifier = Modifier.size(48.dp)) {
                        Icon(CaixaDSLegacyPurchaseFavoriteIcon, "Salvar carrinho favorito", Modifier.size(24.dp), tint = CaixaDSColorTokens.White)
                    }
                }
            }
        },
        bottomBar = {
            Column(
                Modifier.fillMaxWidth().background(CaixaDS.colors.surface),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CaixaDSCartActions(
                    onContinueBetting = onContinueBetting,
                    onPayment = viewModel::requestPayment,
                    paymentEnabled = cart != null && !state.loading && !state.loadingParameters && !state.parametersUnavailable,
                )
                // The legacy FrameLayout retains its slot when the fragment is GONE.
                Box(Modifier.fillMaxWidth().height(CaixaDSCartSummaryTokens.Height)) {
                    CaixaDSCartSummary(cart?.total ?: BigDecimal.ZERO, cart?.betCount ?: 0)
                }
            }
        },
    ) { padding ->
        when {
            state.loading && cart == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.legacyTeal)
            }
            state.errorMessage != null && cart == null -> CartError(
                requireNotNull(state.errorMessage),
                padding,
                viewModel::load,
            )
            else -> CartContent(state, padding, viewModel, onPoolDetail, onComboDetail)
        }
    }
    if (state.recentPurchaseConfirmation) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Existe compra efetivada, em processamento ou aguardando efetivação da aposta com o mesmo valor do carrinho atual nas últimas 24 horas. Você gostaria de revisar o carrinho atual?",
            positiveLabel = "Sim",
            onPositive = viewModel::reviewRecentPurchase,
            negativeLabel = "Não",
            onNegative = viewModel::continueRecentPurchase,
            onDismiss = viewModel::reviewRecentPurchase,
        )
    }
    when (state.paymentAccountDecision) {
        CartPaymentAccountDecision.LoginRequired -> CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Para continuar utilizando a funcionalidade, você precisa efetuar o login no app Loterias Caixa. Deseja efetuar o login?",
            positiveLabel = "Sim",
            onPositive = { viewModel.consumePaymentAccountDecision(); onLogin() },
            negativeLabel = "Não",
            onNegative = viewModel::consumePaymentAccountDecision,
            onDismiss = viewModel::consumePaymentAccountDecision,
        )
        CartPaymentAccountDecision.Suspended -> CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Há uma suspensão temporária ativa para compras. Novas apostas poderão ser realizadas no Portal Loterias Online e no Aplicativo Loterias CAIXA após o término do período selecionado.",
            positiveLabel = "Entendi",
            onPositive = viewModel::consumePaymentAccountDecision,
            onDismiss = viewModel::consumePaymentAccountDecision,
        )
        else -> Unit
    }
    state.pendingRemoval?.let {
        ConfirmCartDialog(
            message = "Deseja excluir esta aposta do carrinho?",
            onConfirm = viewModel::confirmRemoval,
            onDismiss = viewModel::dismissConfirmation,
        )
    }
    if (state.clearConfirmation) {
        ConfirmCartDialog(
            message = "Deseja excluir todas as apostas do carrinho?",
            onConfirm = viewModel::confirmClear,
            onDismiss = viewModel::dismissConfirmation,
        )
    }
    if (state.favoriteNameDialog) {
        FavoriteNameDialog(state, viewModel)
    }
    if (state.favoriteOverwriteConfirmation) {
        ConfirmCartDialog(
            message = "Já existe um carrinho favorito com este nome. Deseja substituí-lo?",
            onConfirm = viewModel::confirmFavoriteOverwrite,
            onDismiss = viewModel::dismissConfirmation,
        )
    }
    if (state.favoriteSaved) {
        CaixaDSLegacyDialog(
            title = "Sucesso",
            message = "Carrinho salvo nos favoritos.",
            positiveLabel = "Entendi",
            onPositive = viewModel::dismissConfirmation,
        )
    }
}

@Composable
private fun CartContent(
    state: CartUiState,
    padding: PaddingValues,
    viewModel: CartViewModel,
    onPoolDetail: (String) -> Unit,
    onComboDetail: (Long) -> Unit,
) {
    val items = state.cart?.items.orEmpty()
    var nowMillis by remember(viewModel) { mutableLongStateOf(viewModel.reservationNow().toEpochMilli()) }
    val hasExpiringShare = items.any { it.poolShare?.expiresAt?.isNotBlank() == true }
    LaunchedEffect(hasExpiringShare) {
        while (hasExpiringShare) {
            nowMillis = viewModel.reservationNow().toEpochMilli()
            delay(1_000)
        }
    }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Text(
                "Confirme suas apostas",
                Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp),
                color = CaixaDS.colors.legacyPrimaryButton,
                fontSize = 14.2.sp,
                fontWeight = FontWeight.Bold,
            )
            if (state.minimumCartAmount > BigDecimal.ZERO) {
                Text(
                    buildAnnotatedString {
                        append("Lembre-se que o valor mínimo do total\nde compras nesse aplicativo é ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            // ViewUtils.setMoedaFormatHtml(value, view, prefix) joined symbol and amount.
                            append(formatCartAmount(state.minimumCartAmount).replace(" ", "").replace("\u00a0", ""))
                        }
                    },
                    Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp),
                    color = CaixaDSColorTokens.Black,
                    fontSize = 14.2.sp,
                )
            }
            state.errorMessage?.let {
                Text(it, Modifier.padding(horizontal = 20.dp), color = CaixaDS.colors.error)
            }
            if (state.cart?.expiredPoolShareRemoved == true) {
                Text(
                    "A(s) cota(s) expirada(s) foram excluídas do seu carrinho.",
                    Modifier.padding(horizontal = 20.dp),
                    color = CaixaDS.colors.error,
                )
            }
        }
        visibleCartSections(items).forEach { section ->
            item { CartSection(section.kind.sectionTitle) }
            items(section.items, key = { "${it.kind}-${it.id}" }) { item ->
                CartRow(
                    item,
                    now = Instant.ofEpochMilli(nowMillis),
                    onDelete = { viewModel.requestRemoval(item) },
                    onDetail = when (item.kind) {
                        CartItemKind.POOL_SHARE -> ({ item.poolShare?.code?.let(onPoolDetail) })
                        CartItemKind.COMBO -> ({ onComboDetail(item.id) })
                        CartItemKind.INDIVIDUAL_BET -> null
                    },
                )
            }
        }
    }
}

@Composable
private fun CartSection(title: String) {
    Text(title, Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, bottom = 5.dp), color = CaixaDS.colors.legacyCartDark)
    Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.legacyLine))
}

@Composable
internal fun CartRow(item: CartItem, now: Instant, onDelete: (() -> Unit)?, onDetail: (() -> Unit)?) {
    val expiry = item.poolShare?.let { poolExpiry(it.expiresAt, now) }
    val expired = expiry?.state == PoolExpiryState.EXPIRED
    val modalityColor = if (expired) CaixaDSColorTokens.LegacyReservationExpired else cartModalityColor(item.modalityCode)
    Column(
        Modifier.fillMaxWidth().background(CaixaDS.colors.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(item.title, color = modalityColor, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                item.contestNumber?.let {
                    Text("Concurso $it", color = if (expired) CaixaDSColorTokens.LegacyReservationExpired else CaixaDS.colors.legacyMutedText, fontSize = 12.sp)
                }
            }
            Text(formatCartAmount(item.amount), color = if (expired) CaixaDSColorTokens.LegacyReservationExpired else CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold)
            if (onDelete != null) IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(CaixaDSIcons.Delete, "Excluir", Modifier.size(20.dp), tint = CaixaDS.colors.error)
            }
        }
        if (item.badges.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item.badges.forEach { CartBadge(it, modalityColor) }
            }
        }
        val rows = item.selectedNumbers.filter(List<Int>::isNotEmpty)
        if (rows.isNotEmpty()) {
            rows.forEach { numbers ->
                Text(
                    numbers.joinToString(" - ") { it.toString().padStart(2, '0') },
                    Modifier.padding(top = 8.dp),
                    color = modalityColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else if (item.description.isNotBlank()) {
            Text(item.description, Modifier.padding(top = 7.dp), color = CaixaDS.colors.legacyMutedText)
        }
        if (item.selectedClovers.isNotEmpty()) {
            Text(
                "Trevos: ${item.selectedClovers.joinToString(" - ") { it.toString().padStart(2, '0') }}",
                Modifier.padding(top = 5.dp),
                color = modalityColor,
                fontWeight = FontWeight.Bold,
            )
        }
        if (item.kind == CartItemKind.INDIVIDUAL_BET) {
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                CartQuantity("Você apostou", quantityLabel(item.numberQuantity, "número", "números"))
                CartQuantity("Para", quantityLabel(item.recurringDraws, "concurso", "concursos"))
            }
        }
        item.poolShare?.let { share ->
            val expiry = poolExpiry(share.expiresAt, now)
            val timerColor = when (expiry.state) {
                PoolExpiryState.EXPIRED -> CaixaDSColorTokens.LegacyReservationExpired
                PoolExpiryState.WARNING -> CaixaDSColorTokens.LegacyReservationWarning
                else -> CaixaDS.colors.legacyMutedText
            }
            Text("Bolão ${share.code}", Modifier.padding(top = 10.dp), color = CaixaDS.colors.legacyText)
            Text(
                "%02d/%02d".format(Locale.ROOT, share.reservedShare, share.totalShares),
                color = if (expired) CaixaDSColorTokens.LegacyReservationExpired else CaixaDS.colors.legacyText,
                fontWeight = FontWeight.Bold,
            )
            Text(
                if (expired) "Cota expirada" else "Cotas",
                color = if (expired) CaixaDSColorTokens.LegacyReservationCancelled else timerColor,
            )
            Row(Modifier.padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    CaixaDSIcons.Time,
                    null,
                    Modifier.size(18.dp),
                    tint = timerColor,
                )
                Text(
                    expiry.remainingLabel,
                    Modifier.padding(start = 5.dp),
                    color = timerColor,
                )
            }
        }
        if (item.comboBetCount > 0) {
            Text(
                quantityLabel(item.comboBetCount, "aposta incluída", "apostas incluídas"),
                Modifier.padding(top = 8.dp),
                color = modalityColor,
                fontWeight = FontWeight.Bold,
            )
        }
        item.complementaryLabel?.takeIf(String::isNotBlank)?.let {
            Text(it, Modifier.padding(top = 7.dp), color = CaixaDS.colors.legacyMutedText)
        }
        item.gameRows.forEach {
            Text(it, Modifier.padding(top = 5.dp), color = CaixaDS.colors.legacyText)
        }
        if (onDetail != null) {
            Row(
                Modifier.align(Alignment.End).clickable(onClick = onDetail).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Ver detalhes", color = CaixaDS.colors.brand, fontWeight = FontWeight.Bold)
                Icon(CaixaDSIcons.Forward, null, Modifier.size(18.dp), tint = CaixaDS.colors.brand)
            }
        }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.legacyLine))
}

@Composable
private fun CartQuantity(label: String, value: String) {
    Column {
        Text(label, color = CaixaDS.colors.legacyMutedText, fontSize = 12.sp)
        Text(value, color = CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun CartBadge(badge: CartItemBadge, color: Color) {
    Surface(color = color, shape = RoundedCornerShape(2.dp)) {
        Text(
            badge.label,
            Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            color = CaixaDSColorTokens.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun FavoriteNameDialog(state: CartUiState, viewModel: CartViewModel) {
    CaixaDSTextInputDialog(
        title = "Carrinho Favorito",
        value = state.favoriteName,
        hint = "Nome do carrinho favorito",
        onValueChange = viewModel::updateFavoriteName,
        onSave = viewModel::submitFavoriteName,
        onDismiss = viewModel::dismissConfirmation,
        enabled = !state.loading,
        errorMessage = state.errorMessage,
    )
}

@Composable
private fun ConfirmCartDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    CaixaDSLegacyDialog(
        title = "Atenção",
        message = message,
        positiveLabel = "Sim",
        onPositive = onConfirm,
        negativeLabel = "Não",
        onNegative = onDismiss,
    )
}

@Composable
private fun CartError(message: String, padding: PaddingValues, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(padding).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = CaixaDS.colors.error)
        Text("Tentar novamente", Modifier.clickable(onClick = onRetry).padding(20.dp), color = CaixaDS.colors.brand)
    }
}

private val CartItemKind.sectionTitle: String
    get() = when (this) {
        CartItemKind.POOL_SHARE -> "Cotas de bolão"
        CartItemKind.INDIVIDUAL_BET -> "Apostas individuais"
        CartItemKind.COMBO -> "Combos"
    }

private val CartItemBadge.label: String
    get() = when (this) {
        CartItemBadge.EXCHANGE -> "TROCA"
        CartItemBadge.POOL -> "BOLÃO"
        CartItemBadge.COMBO -> "COMBO"
        CartItemBadge.MIRROR -> "ESPELHO"
        CartItemBadge.SURPRISE -> "SURPRESINHA"
        CartItemBadge.RECURRING -> "TEIMOSINHA"
    }

@Composable
private fun cartModalityColor(code: String): Color = when (code.uppercase(Locale.ROOT)) {
    "MEGA_SENA", "MEGASENA", "MEGA-SENA" -> CaixaDSColorTokens.MegaSena
    "LOTOFACIL", "LOTOFÁCIL" -> CaixaDSColorTokens.Lotofacil
    "QUINA" -> CaixaDSColorTokens.Quina
    "LOTOMANIA" -> CaixaDSColorTokens.Lotomania
    "TIMEMANIA" -> CaixaDSColorTokens.Timemania
    "DUPLA_SENA", "DUPLASENA", "DUPLA-SENA" -> CaixaDSColorTokens.DuplaSena
    "DIA_DE_SORTE", "DIADESORTE", "DIA-DE-SORTE" -> CaixaDSColorTokens.DiaDeSorte
    "SUPER_SETE", "SUPERSETE", "SUPER-SETE" -> CaixaDSColorTokens.SuperSete
    "MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA" -> CaixaDSColorTokens.MaisMilionaria
    else -> CaixaDS.colors.brand
}

internal fun formatCartAmount(value: BigDecimal?): String = NumberFormat
    .getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    .format(value ?: BigDecimal.ZERO)
    .replace('\u00A0', ' ')
    .replace('\u202F', ' ')
