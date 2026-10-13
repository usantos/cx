package br.gov.caixa.loterias.feature.cart

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRecurringIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDeleteIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySelectionDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSMotion
import br.gov.caixa.loterias.core.domain.cart.FavoriteCart
import br.gov.caixa.loterias.core.domain.cart.FavoriteCartBet

@Composable
fun FavoriteCartsRoute(
    viewModel: FavoriteCartsViewModel,
    onBack: () -> Unit,
    onOpen: (Long) -> Unit,
    onCartReady: () -> Unit,
    onHelp: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.imported) {
        if (state.imported) {
            viewModel.consumeImported()
            onCartReady()
        }
    }
    CaixaDSLegacyScaffold(
        title = "Carrinhos Favoritos",
        topBarTitleSize = 16.sp,
        topBarTitleWeight = FontWeight.Bold,
        onBack = onBack,
        containerColor = CaixaDSColorTokens.White,
    ) { padding ->
        FavoriteContentState(state.loading, state.errorMessage, padding, viewModel::load) {
            if (state.carts.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding)) {
                    Column(Modifier.fillMaxWidth()) {
                        FavoriteRouteHeading(
                            "Aqui estão todos os seus carrinhos favoritos.",
                            "Você pode excluir, detalhar e transformar no seu carrinho atual.",
                        )
                    }
                    Text(
                        "Você ainda não possui carrinhos favoritos.",
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).align(Alignment.Center),
                        color = CaixaDSColorTokens.LegacyFavoriteCartText,
                        style = favoriteTextStyle.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 20.dp),
                ) {
                    item {
                        FavoriteRouteHeading(
                            "Aqui estão todos os seus carrinhos favoritos.",
                            "Você pode excluir, detalhar e transformar no seu carrinho atual.",
                        )
                    }
                    items(state.carts, key = { it.id }) { favorite ->
                        FavoriteCartRow(
                            favorite = favorite,
                            bets = if (state.expandedCartId == favorite.id) state.expandedBets else emptyList(),
                            expanded = state.expandedCartId == favorite.id,
                            loading = state.loadingCartId == favorite.id,
                            onToggle = { viewModel.toggleCart(favorite.id) },
                            onTransform = { viewModel.requestImport(favorite.id) },
                            onDetail = { onOpen(favorite.id) },
                            onDelete = { viewModel.requestCartRemoval(favorite.id) },
                        )
                    }
                }
            }
        }
    }
    if (state.pendingCartRemoval != null) {
        FavoriteConfirmDialog(
            "Deseja realmente excluir este carrinho favorito?",
            viewModel::confirmCartRemoval,
            viewModel::dismissConfirmation,
        )
    }
    if (state.existingCartConfirmationRequired) {
        FavoriteConfirmDialog(
            "Este carrinho favorito já foi adicionado ao carrinho atual. Deseja adicioná-lo novamente?",
            viewModel::confirmExistingCartImport,
            viewModel::dismissConfirmation,
        )
    }
    if (state.specialSelectionRequired) {
        SpecialSelectionDialog(
            regularName = state.regularModalityName,
            specialName = state.specialModalityName,
            onConfirm = viewModel::selectSpecialMode,
            onDismiss = viewModel::dismissConfirmation,
        )
    }
}

/** XML uniform auto-size: 150dp single line, 8sp minimum, 1sp steps, 14dp maximum. */
@Composable
private fun favoriteNameSize(name: String): androidx.compose.ui.unit.TextUnit {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val maximum = with(density) { 14.dp.toSp().value }
    val width = with(density) { 150.dp.roundToPx() }
    return remember(name, density, measurer) {
        var size = maximum
        while (size > 8f && measurer.measure(name, favoriteTextStyle.copy(fontSize = size.sp),
                maxLines = 1, softWrap = false).size.width > width) size -= 1f
        size.coerceAtLeast(8f).sp
    }
}

@Composable
private fun FavoriteCartRow(
    favorite: FavoriteCart,
    bets: List<FavoriteCartBet>,
    expanded: Boolean,
    loading: Boolean,
    onToggle: () -> Unit,
    onTransform: () -> Unit,
    onDetail: () -> Unit,
    onDelete: () -> Unit,
) {
    val nameSize = favoriteNameSize(favorite.name)
    val measurer = rememberTextMeasurer()
    val nameHeight = with(LocalDensity.current) {
        measurer.measure(favorite.name, favoriteTextStyle.copy(fontSize = nameSize),
            maxLines = 1, softWrap = false).size.height.toDp()
    }
    val summaryTop = (70.dp + nameHeight) / 2 + 10.dp
    val background by animateColorAsState(
        if (expanded) CaixaDSColorTokens.LegacyFavoriteCartExpanded else CaixaDSColorTokens.LegacyFavoriteCartCard,
        animationSpec = tween(100),
        label = "favorite-cart-background",
    )
    Surface(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        color = background,
        shape = RoundedCornerShape(10.dp),
    ) {
        Box(Modifier.fillMaxWidth().clickable(onClick = onToggle)) {
            Row(
                modifier = Modifier.fillMaxWidth().height(70.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = favorite.name,
                    modifier = Modifier.padding(horizontal = 10.dp).width(150.dp),
                    color = CaixaDSColorTokens.White,
                    style = favoriteTextStyle.copy(fontSize = nameSize),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.weight(1f))
                FavoriteCartRasterAction(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_carrinho, "Transformar carrinho", onTransform)
                Spacer(Modifier.width(10.dp))
                FavoriteCartRasterAction(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_ic_lupa_carrinho_fav, "Detalhar carrinho", onDetail)
                FavoriteCartRasterAction(R.drawable.legacy_favorite_ic_icone_lixeira, "Excluir carrinho", onDelete)
            }
            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier.padding(top = summaryTop),
                enter = fadeIn(tween(100)),
                exit = fadeOut(tween(100)),
            ) {
                Column(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
                    bets.forEach { FavoriteCartBetSummary(it) }
                }
            }
            if (loading) {
                Box(Modifier.fillMaxWidth().padding(top = 70.dp).height(30.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = CaixaDSColorTokens.White,
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteCartRasterAction(resource: Int, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.width(40.dp).height(70.dp).clip(RoundedCornerShape(4.dp)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(resource),
            description, Modifier.size(20.dp), tint = CaixaDSColorTokens.White,
        )
    }
}

@Composable
private fun FavoriteCartAction(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.width(40.dp).height(70.dp).clip(RoundedCornerShape(4.dp)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, Modifier.size(20.dp), tint = CaixaDSColorTokens.White)
    }
}

@Composable
private fun FavoriteCartBetSummary(bet: FavoriteCartBet) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            if (bet.modalityId == 9) "+milionária" else bet.modality.lowercase(),
            modifier = Modifier.weight(1f),
            color = CaixaDSColorTokens.White,
            style = favoriteTextStyle.copy(fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(Modifier.padding(end = 5.dp).size(20.dp)) {
            if (bet.recurringDraws > 0) Icon(CaixaDSLegacyRecurringIcon, "Teimosinha", Modifier.fillMaxSize(), tint = CaixaDSColorTokens.White)
        }
        Text(
            buildString {
                append(bet.numberCount)
                append(" num(s)")
                if (bet.cloverCount > 0) append(" ${bet.cloverCount} trev(s)")
            },
            color = CaixaDSColorTokens.White,
            style = favoriteTextStyle,
            modifier = Modifier.padding(5.dp),
            maxLines = 1,
        )
        Spacer(Modifier.width(50.dp))
    }
}

@Composable
fun FavoriteCartDetailRoute(
    viewModel: FavoriteCartsViewModel,
    onBack: () -> Unit,
    onHelp: (() -> Unit)? = null,
) {
    BackHandler(onBack = onBack)
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.cartDeleted) { if (state.cartDeleted) onBack() }
    CaixaDSLegacyScaffold(
        title = "Favoritas",
        onBack = onBack,
        topBarTitleSize = 20.sp,
        topBarTitleWeight = FontWeight.Normal,
        topBarActionWidth = 50.dp,
        topBarAction = onHelp?.let { action -> {
            IconButton(onClick = action) {
                androidx.compose.foundation.Image(
                    painterResource(R.drawable.legacy_favorite_duvidas_toolbar_icon),
                    "Ajuda",
                    Modifier.size(32.dp),
                )
            }
        } },
        containerColor = CaixaDSColorTokens.White,
    ) { padding ->
        FavoriteContentState(state.loading, state.errorMessage, padding, viewModel::load) {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 20.dp),
            ) {
                item {
                    Text("Apostas do carrinho favorito escolhido", Modifier.padding(20.dp),
                        color = CaixaDSColorTokens.LegacyFavoriteCartText, style = favoriteTextStyle.copy(fontWeight = FontWeight.Bold))
                    Text("Nesta área estão todas as suas apostas da sorte.\nÉ só escolher e apostar. Boa sorte!",
                        Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        color = CaixaDSColorTokens.LegacySecondaryText, style = favoriteTextStyle)
                    HorizontalDivider(color = CaixaDSColorTokens.LegacyFavoriteDivider)
                }
                items(state.bets, key = { it.id }) { bet -> FavoriteBetCard(bet) { viewModel.requestBetRemoval(bet.id) } }
            }
        }
    }
    if (state.pendingBetRemoval != null) {
        FavoriteConfirmDialog(
            "Deseja realmente excluir esta aposta?",
            viewModel::confirmBetRemoval,
            viewModel::dismissConfirmation,
        )
    }
}

private val favoriteTextStyle = TextStyle(fontFamily = CaixaDSFontFamily, fontSize = 14.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = true))

@Composable
private fun FavoriteBetCard(bet: FavoriteCartBet, onDelete: () -> Unit) {
    val style = caixaDSLotteryStyle(bet.modalityId ?: 0, bet.modality)
    val color = if (bet.modalityId in listOf(7, 11, 20)) style.content else style.light
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().padding(10.dp)) {
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(top = 5.dp, end = 30.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (bet.modalityId == 9) "+milionária" else if (bet.modalityId == 20) bet.modality else bet.modality.lowercase(),
                        color = color, style = favoriteTextStyle.copy(fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold),
                    )
                    if (bet.recurringDraws > 0) {
                        Icon(CaixaDSLegacyRecurringIcon,
                            "Teimosinha", Modifier.padding(start = 5.dp).size(20.dp), tint = CaixaDSColorTokens.LegacyFavoriteCartCard)
                    }
                }
                val complement = when (bet.modalityId) {
                    20 -> if (bet.surprise) "???" else bet.favoriteTeam
                    11 -> if (bet.surprise) "???" else bet.luckyMonth
                    else -> null
                }
                if (complement != null) Text(complement, color = color,
                    style = favoriteTextStyle.copy(fontWeight = FontWeight.Bold))
        if (bet.modalityId == 7 && bet.numberColumns.isNotEmpty() && !bet.surprise) {
            Row(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                bet.numberColumns.forEachIndexed { index, numbers ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(25.dp).background(style.light, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                            Text("${index + 1}", color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        numbers.forEach { number ->
                            Box(Modifier.size(30.dp).border(1.dp, color, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                                Text(number.toString(), color = color, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                if (bet.surprise) List(bet.numberCount) { "XX" }.joinToString(" - ")
                else bet.numbers.joinToString(" - ") { if (it == 100) "00" else it.toString().padStart(2, '0') },
                Modifier.padding(top = 10.dp, end = 30.dp), color = CaixaDSColorTokens.LegacySecondaryText,
                style = favoriteTextStyle,
            )
        }
        if (bet.cloverCount > 0) {
            Text(
                "Trevos:  " + if (bet.surprise) List(bet.cloverCount) { "XX" }.joinToString(" - ")
                else bet.clovers.joinToString(" - "),
                Modifier.padding(top = 10.dp), color = CaixaDSColorTokens.LegacySecondaryText,
                style = favoriteTextStyle,
            )
        }
            }
            Box(Modifier.align(Alignment.TopEnd).size(30.dp).clickable(onClick = onDelete), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.legacy_favorite_ic_icone_lixeira_verde),
                    "Excluir aposta", Modifier.size(17.dp, 20.dp), tint = CaixaDSColorTokens.LegacyFavoriteCartCard)
            }
        }
        HorizontalDivider(color = CaixaDSColorTokens.LegacyFavoriteDivider)
    }
}

@Composable
private fun FavoriteRouteHeading(title: String, description: String) {
    Text(
        title,
        Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp),
        color = CaixaDSColorTokens.LegacyFavoriteCartText,
        style = favoriteTextStyle.copy(fontWeight = FontWeight.Bold),
    )
    Text(
        description,
        Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
        color = CaixaDSColorTokens.LegacySecondaryText,
        style = favoriteTextStyle,
    )
}

@Composable
private fun FavoriteContentState(
    loading: Boolean,
    error: String?,
    padding: PaddingValues,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
) {
    when {
        loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.legacyTeal)
        }
        error != null -> Column(
            Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(error, color = CaixaDS.colors.error)
            Text("Tentar novamente", Modifier.clickable(onClick = onRetry).padding(20.dp), color = CaixaDS.colors.brand)
        }
        else -> content()
    }
}

@Composable
private fun FavoriteConfirmDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    CaixaDSLegacyDialog(
        title = "Atenção",
        message = message,
        positiveLabel = "Sim",
        onPositive = onConfirm,
        negativeLabel = "Não",
        onNegative = onDismiss,
        onDismiss = onDismiss,
    )
}

@Composable
private fun SpecialSelectionDialog(
    regularName: String,
    specialName: String,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selected by remember { mutableIntStateOf(-1) }
    val options = listOf("Ambas", regularName, specialName)
    CaixaDSLegacySelectionDialog(
        title = "Atenção",
        message = "Existem 2 concursos abertos para a $regularName.\nDeseja incluir no carrinho qual concurso?",
        options = options,
        selectedIndex = selected,
        onSelect = { selected = it },
        onConfirm = { if (selected >= 0) onConfirm(selected) },
        onDismiss = onDismiss,
    )
}
