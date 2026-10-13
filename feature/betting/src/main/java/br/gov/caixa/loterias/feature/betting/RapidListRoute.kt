package br.gov.caixa.loterias.feature.betting

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDivider
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.ServiceFailureOverlay

@Composable
fun RapidListRoute(viewModel: RapidListViewModel, onBack: () -> Unit, onPayment: () -> Unit,
    onValidationRedirect: (ServiceRedirect) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ServiceFailureOverlay(state.validationFailure, viewModel::acceptValidationNotice,
        viewModel::dismissValidationNotice, viewModel::consumeValidationRedirect, onValidationRedirect)
    LaunchedEffect(state.readyForPayment) {
        if (state.readyForPayment) { viewModel.consumePaymentReady(); onPayment() }
    }
    CaixaDSLegacyScaffold(
        title = "Rapidão",
        onBack = onBack,
        bottomBar = {
            Box(Modifier.fillMaxWidth().height(99.dp).background(CaixaDSColorTokens.LegacyBetFooter)) {
                Column(Modifier.align(Alignment.CenterEnd).padding(end = 10.dp).width(80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(formatBetAmount(state.cart?.total), fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CaixaDS.colors.legacyMutedText, maxLines = 1)
                    Text("Total", fontFamily = CaixaDSFontFamily, fontSize = 12.sp, color = CaixaDS.colors.brand)
                }
                Box(Modifier.align(Alignment.BottomCenter).offset(y = (-156).dp).width(183.dp).height(56.dp)
                    .background(CaixaDSColorTokens.White).border(1.dp, CaixaDS.colors.legacyTeal)
                    .clickable { viewModel.generate() }, contentAlignment = Alignment.Center) {
                    Text("Fazer nova aposta", fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CaixaDS.colors.legacyTeal)
                }
                Box(Modifier.align(Alignment.BottomCenter).offset(y = (-64).dp).width(184.dp).height(84.dp)
                    .background(CaixaDS.colors.legacyTeal).clickable(onClick = viewModel::advance), contentAlignment = Alignment.Center) {
                    Text("Avançar\npara a forma de pagamento", Modifier.padding(horizontal = 14.dp), fontFamily = CaixaDSFontFamily, fontSize = 17.sp, color = CaixaDSColorTokens.White, textAlign = TextAlign.Center)
                }
            }
        },
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
            state.cart?.items.isNullOrEmpty() -> Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Nenhuma aposta foi gerada.", color = CaixaDS.colors.legacyMutedText)
                TextButton(onClick = { viewModel.generate() }) { Text("Tentar novamente") }
            }
            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White),
                contentPadding = PaddingValues(bottom = 120.dp),
            ) {
                item {
                    Text("O jeito mais fácil para apostar", Modifier.padding(start = 17.dp, top = 40.dp, end = 90.dp), color = CaixaDS.colors.brand, fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.2.sp)
                    Text("O seu pacote de apostas prontas para serem adquiridas em um toque", Modifier.padding(start = 17.dp, top = 20.dp, end = 17.dp), color = CaixaDS.colors.legacyText, fontFamily = CaixaDSFontFamily, fontSize = 14.sp)
                    Spacer(Modifier.height(20.dp))
                }
                items(requireNotNull(state.cart).items, key = CartItem::id) { item ->
                    RapidBetCard(item) { viewModel.remove(item) }
                }
            }
        }
    }
    state.errorMessage?.let { CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissError) }
}

@Composable
private fun RapidBetCard(item: CartItem, onRemove: () -> Unit) {
    val style = caixaDSLotteryStyle(item.modalityCode.toIntOrNull() ?: 0, item.title)
    var opened by remember(item.id) { mutableStateOf(false) }
    var dragging by remember(item.id) { mutableStateOf(false) }
    var dragOffset by remember(item.id) { mutableStateOf(0f) }
    var contextMenu by remember(item.id) { mutableStateOf(false) }
    val panelWidth = with(LocalDensity.current) { 229.dp.toPx() }
    val offset by animateFloatAsState(if (dragging) dragOffset else if (opened) panelWidth else 0f,
        animationSpec = if (dragging) snap() else tween(200), label = "Detalhes da aposta")
    Card(Modifier.fillMaxWidth().padding(10.dp), shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDSColorTokens.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).semantics {
            customActions = listOf(
                CustomAccessibilityAction("Excluir aposta") { onRemove(); true },
                CustomAccessibilityAction(if (opened) "Fechar detalhes" else "Mostrar detalhes") { opened = !opened; true },
            )
        }.pointerInput(item.id, panelWidth) {
            detectHorizontalDragGestures(
                onDragStart = { dragging = true; dragOffset = if (opened) panelWidth else 0f },
                onDragEnd = { opened = dragOffset > panelWidth / 2; dragging = false },
                onDragCancel = { dragging = false },
            ) { change, amount ->
                change.consume()
                dragOffset = (dragOffset + amount).coerceIn(0f, panelWidth)
            }
        }) {
            Column(Modifier.width(229.dp).offset { IntOffset((offset - panelWidth).toInt(), 0) }
                .background(style.dark).padding(top = 14.dp, bottom = 14.dp)) {
                Row {
                    Column(Modifier.padding(start = 16.dp)) {
                        Text("Você apostou", fontFamily = CaixaDSFontFamily, fontSize = 14.sp, color = CaixaDSColorTokens.White)
                        Text("${item.numberQuantity} ${if (item.numberQuantity == 1) "número" else "números"}", fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaixaDSColorTokens.White)
                    }
                    Column(Modifier.padding(start = 30.dp)) {
                        Text("Para", fontFamily = CaixaDSFontFamily, fontSize = 14.sp, color = CaixaDSColorTokens.White)
                        Text("${item.recurringDraws.coerceAtLeast(1)} ${if (item.recurringDraws <= 1) "sorteio" else "sorteios"}", fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaixaDSColorTokens.White)
                    }
                }
                item.complementaryLabel?.let { Text(it, Modifier.padding(start = 16.dp, top = 4.dp), fontFamily = CaixaDSFontFamily, fontSize = 14.sp, color = CaixaDSColorTokens.White) }
            }
            Box(Modifier.fillMaxWidth().offset { IntOffset(offset.toInt(), 0) }
                .background(CaixaDSColorTokens.White)
                .combinedClickable(onClick = { opened = false }, onLongClick = { contextMenu = true }, onLongClickLabel = "Ações da aposta")) {
        Row(Modifier.fillMaxWidth().padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 14.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.width(100.dp)) {
                Text(item.title.lowercase(), fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.2.sp, color = style.content, maxLines = 1)
                Text("Conc. ${item.contestNumber ?: ""}", fontFamily = CaixaDSFontFamily, fontSize = 14.2.sp, color = CaixaDS.colors.legacyText, maxLines = 1)
            }
            Column(Modifier.weight(1f).padding(start = 16.dp)) {
                Text(if (item.selectedNumbers.isNotEmpty()) item.selectedNumbers.flatten().joinToString(" - ") { if (it == 100) "00" else it.toString().padStart(2, '0') } else item.description,
                    fontFamily = CaixaDSFontFamily, fontSize = 14.2.sp, color = CaixaDS.colors.legacyGraphite)
                item.complementaryLabel?.let { Text(it, fontFamily = CaixaDSFontFamily, fontSize = 14.2.sp, color = CaixaDS.colors.legacyGraphite) }
            }
            Text(formatBetAmount(item.amount), Modifier.width(70.dp), fontFamily = CaixaDSFontFamily, fontSize = 14.2.sp, color = CaixaDS.colors.legacyText, textAlign = TextAlign.End)
        }
                DropdownMenu(expanded = contextMenu, onDismissRequest = { contextMenu = false }) {
                    DropdownMenuItem(text = { Text("Excluir aposta", fontFamily = CaixaDSFontFamily) }, onClick = { contextMenu = false; onRemove() })
                }
            }
        }
    }
}
