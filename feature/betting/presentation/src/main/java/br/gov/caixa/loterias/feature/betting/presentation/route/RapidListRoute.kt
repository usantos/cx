package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenu as DropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenuItem as DropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.components.ServiceFailureOverlay
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidListViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("LongMethod")
@Composable
fun RapidListRoute(
    viewModel: RapidListViewModel,
    onBack: () -> Unit,
    onPayment: () -> Unit,
    onValidationRedirect: (ServiceRedirect) -> Unit,
    onServiceRedirect: (ServiceRedirect) -> Unit = onValidationRedirect,
    onConfiguration: () -> Unit = {},
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    ServiceFailureOverlay(
        state.serviceFailure,
        viewModel::acceptServiceNotice,
        viewModel::dismissServiceNotice,
        viewModel::consumeServiceRedirect,
        onServiceRedirect
    )
    ServiceFailureOverlay(
        state.validationFailure,
        viewModel::acceptValidationNotice,
        viewModel::dismissValidationNotice,
        viewModel::consumeValidationRedirect,
        onValidationRedirect
    )
    LaunchedEffect(state.readyForPayment) {
        if (state.readyForPayment) {
            viewModel.consumePaymentReady()
            onPayment()
        }
    }
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_rapidao_658c41),
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onConfiguration, modifier = Modifier.uiId("bet.0182")) {
                Icon(
                    painterResource(br.gov.caixa.loterias.feature.betting.R.drawable.legacy_rapid_settings),
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_configuracao_8d60e1),
                    tint = CaixaDSColorTokens.White,
                    modifier = (Modifier.size(24.dp)).uiId("bet.0183")
                )
            }
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().height(99.dp).background(CaixaDSColorTokens.BetFooter)) {
                Column(
                    Modifier.align(Alignment.CenterEnd).padding(end = 10.dp).width(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        formatBetAmount(state.cart?.total),
                        fontFamily = CaixaDSFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CaixaDS.colors.mutedText,
                        maxLines = 1,
                        modifier = Modifier.uiId("bet.0184")
                    )
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_total_c9b3c3),
                        fontFamily = CaixaDSFontFamily,
                        fontSize = 12.sp,
                        color = CaixaDS.colors.brand,
                        modifier = Modifier.uiId("bet.0185")
                    )
                }
                Box(
                    Modifier.align(Alignment.BottomCenter).offset(y = (-156).dp).width(183.dp).height(56.dp)
                        .background(CaixaDSColorTokens.White).border(1.dp, CaixaDS.colors.teal)
                        .uiId("bet.0186").clickable { viewModel.generate() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_fazer_nova_aposta_7c7b2f),
                        fontFamily = CaixaDSFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CaixaDS.colors.teal,
                        modifier = Modifier.uiId("bet.0187")
                    )
                }
                Box(
                    Modifier.align(Alignment.BottomCenter).offset(y = (-64).dp).width(184.dp).height(84.dp)
                        .background(CaixaDS.colors.teal).uiId("bet.0188").clickable(onClick = viewModel::advance),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_avancar_para_a_forma_de_pagamento_64482f),
                        (Modifier.padding(horizontal = 14.dp)).uiId("bet.0189"),
                        fontFamily = CaixaDSFontFamily,
                        fontSize = 17.sp,
                        color = CaixaDSColorTokens.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        modifier = Modifier.uiId("bet.0190")
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }

            state.cart?.items.isNullOrEmpty() -> Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_nenhuma_aposta_foi_gerada_1e46c4),
                    color = CaixaDS.colors.mutedText,
                    modifier = Modifier.uiId("bet.0191")
                )
                TextButton(
                    onClick = { viewModel.generate() },
                    modifier = Modifier
                        .uiId("bet.0192")
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                        modifier = Modifier
                            .uiId("bet.0193")
                    )
                }
            }

            else -> LazyColumn(
                (Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)).uiId("bet.0194"),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_o_jeito_mais_facil_para_apostar_1a2d05),
                        (Modifier.padding(start = 17.dp, top = 40.dp, end = 90.dp)).uiId("bet.0195"),
                        color = CaixaDS.colors.brand,
                        fontFamily = CaixaDSFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.2.sp
                    )
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_o_seu_pacote_de_apostas_prontas_para_30c098),
                        (Modifier.padding(start = 17.dp, top = 20.dp, end = 17.dp)).uiId("bet.0196"),
                        color = CaixaDS.colors.text,
                        fontFamily = CaixaDSFontFamily,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(20.dp))
                }
                items(requireNotNull(state.cart).items, key = CartItem::id) { item ->
                    RapidBetCard(item) { viewModel.remove(item) }
                }
            }
        }
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissError
        )
    }
}

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("CyclomaticComplexMethod", "LongMethod")
@Composable
private fun RapidBetCard(item: CartItem, onRemove: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = caixaDSLotteryStyle(item.modalityCode.toIntOrNull() ?: 0, item.title)
    var opened by remember(item.id) { mutableStateOf(false) }
    var dragging by remember(item.id) { mutableStateOf(false) }
    var dragOffset by remember(item.id) { mutableStateOf(0f) }
    var contextMenu by remember(item.id) { mutableStateOf(false) }
    val panelWidth = with(LocalDensity.current) { 229.dp.toPx() }
    val offset by animateFloatAsState(
        if (dragging) {
            dragOffset
        } else if (opened) {
            panelWidth
        } else {
            0f
        },
        animationSpec = if (dragging) snap() else tween(200),
        label = uiStrings.getString(LocalizedR.string.ui_detalhes_da_aposta_82fbc3)
    )
    Card(
        Modifier.fillMaxWidth().padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDSColorTokens.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).semantics {
                customActions = listOf(
                    CustomAccessibilityAction(uiStrings.getString(LocalizedR.string.ui_excluir_aposta_86e1ac)) {
                        onRemove()
                        true
                    },
                    CustomAccessibilityAction(
                        if (opened) {
                            uiStrings.getString(LocalizedR.string.ui_fechar_detalhes_91bd3b)
                        } else {
                            uiStrings.getString(
                                LocalizedR.string.ui_mostrar_detalhes_ce119f
                            )
                        }
                    ) {
                        opened = !opened
                        true
                    }
                )
            }.uiId("bet.0197").pointerInput(item.id, panelWidth) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        dragging = true
                        dragOffset = if (opened) panelWidth else 0f
                    },
                    onDragEnd = {
                        opened = dragOffset > panelWidth / 2
                        dragging = false
                    },
                    onDragCancel = { dragging = false }
                ) { change, amount ->
                    change.consume()
                    dragOffset = (dragOffset + amount).coerceIn(0f, panelWidth)
                }
            }
        ) {
            Column(
                Modifier.width(229.dp).offset { IntOffset((offset - panelWidth).toInt(), 0) }
                    .background(style.dark).padding(top = 14.dp, bottom = 14.dp)
            ) {
                Row {
                    Column(Modifier.padding(start = 16.dp)) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_voce_apostou_773116),
                            fontFamily = CaixaDSFontFamily,
                            fontSize = 14.sp,
                            color = CaixaDSColorTokens.White,
                            modifier = Modifier.uiId("bet.0198")
                        )
                        Text(
                            if (item.numberQuantity == 0) {
                                uiStrings.getString(LocalizedR.string.format_rapid_no_numbers)
                            } else {
                                uiStrings.getQuantityString(
                                    LocalizedR.plurals.format_rapid_number_quantity,
                                    item.numberQuantity,
                                    item.numberQuantity
                                )
                            },
                            fontFamily = CaixaDSFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CaixaDSColorTokens.White,
                            modifier = Modifier.uiId("bet.0199")
                        )
                    }
                    Column(Modifier.padding(start = 30.dp)) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_para_237b14),
                            fontFamily = CaixaDSFontFamily,
                            fontSize = 14.sp,
                            color = CaixaDSColorTokens.White,
                            modifier = Modifier.uiId("bet.0200")
                        )
                        Text(
                            uiStrings.getQuantityString(
                                LocalizedR.plurals.format_rapid_draw_quantity,
                                item.recurringDraws.coerceAtLeast(1),
                                item.recurringDraws.coerceAtLeast(1)
                            ),
                            fontFamily = CaixaDSFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CaixaDSColorTokens.White,
                            modifier = Modifier.uiId("bet.0201")
                        )
                    }
                }
                item.complementaryLabel?.let {
                    Text(
                        it,
                        (Modifier.padding(start = 16.dp, top = 4.dp)).uiId("bet.0202"),
                        fontFamily = CaixaDSFontFamily,
                        fontSize = 14.sp,
                        color = CaixaDSColorTokens.White
                    )
                }
            }
            Box(
                Modifier.fillMaxWidth().offset { IntOffset(offset.toInt(), 0) }
                    .background(CaixaDSColorTokens.White)
                    .uiId("bet.0203").combinedClickable(
                        onClick = {
                            opened = false
                        },
                        onLongClick = { contextMenu = true },
                        onLongClickLabel = uiStrings.getString(LocalizedR.string.ui_acoes_da_aposta_134037)
                    )
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(Modifier.width(100.dp)) {
                        Text(
                            item.title.lowercase(),
                            fontFamily = CaixaDSFuturaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.2.sp,
                            color = style.listText,
                            maxLines = 1,
                            modifier = Modifier.uiId("bet.0204")
                        )
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_conc_1_s_f4692e, item.contestNumber ?: ""),
                            fontFamily = CaixaDSFontFamily,
                            fontSize = 14.2.sp,
                            color = CaixaDS.colors.text,
                            maxLines = 1,
                            modifier = Modifier.uiId("bet.0205")
                        )
                    }
                    Column(Modifier.weight(1f).padding(start = 16.dp)) {
                        Text(
                            if (item.selectedNumbers.isNotEmpty()) {
                                item.selectedNumbers.flatten().joinToString(" - ") {
                                    if (it ==
                                        100
                                    ) {
                                        "00"
                                    } else {
                                        it.toString().padStart(2, '0')
                                    }
                                }
                            } else {
                                item.description
                            },
                            fontFamily = CaixaDSFontFamily,
                            fontSize = 14.2.sp,
                            color = CaixaDS.colors.graphite,
                            modifier = Modifier.uiId("bet.0206")
                        )
                        item.complementaryLabel?.let {
                            Text(
                                it,
                                fontFamily = CaixaDSFontFamily,
                                fontSize = 14.2.sp,
                                color = CaixaDS.colors.graphite,
                                modifier = Modifier.uiId("bet.0207")
                            )
                        }
                    }
                    Text(
                        formatBetAmount(item.amount),
                        (Modifier.width(70.dp)).uiId("bet.0208"),
                        fontFamily = CaixaDSFontFamily,
                        fontSize = 14.2.sp,
                        color = CaixaDS.colors.text,
                        textAlign = TextAlign.End
                    )
                }
                DropdownMenu(expanded = contextMenu, onDismissRequest = { contextMenu = false }, modifier = Modifier.uiId("bet.0209")) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                uiStrings.getString(LocalizedR.string.ui_excluir_aposta_86e1ac),
                                fontFamily = CaixaDSFontFamily,
                                modifier = Modifier.uiId("bet.0210")
                            )
                        },
                        onClick = {
                            contextMenu = false
                            onRemove()
                        },
                        modifier = Modifier.uiId("bet.0211")
                    )
                }
            }
        }
    }
}
