package br.gov.caixa.loterias.apostas


import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Density
import kotlin.math.roundToInt
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.core.domain.results.DrawResult
import br.gov.caixa.loterias.core.domain.home.HomeLayoutPreferences
import br.gov.caixa.loterias.core.designsystem.CxHomeV2Colors

import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive

/** Presentation only: navigation, authentication and purchases remain with PrincipalActivity. */
internal class HomeV2Presentation(private val layoutPreferences: HomeLayoutPreferences, private val actionProvider: () -> Actions) {
    private val actions get() = actionProvider()
    interface Actions {
        fun bet(modalidade: HomeLottery)
        fun pool(modalidade: HomeLottery)
        fun results()
        fun modalityResults(modalidade: HomeV2Modality)
        fun games()
        fun favorites()
        fun menu()
        fun notifications()
        fun cart()
        fun profile()
        fun canPool(modalidade: HomeV2Modality): Boolean
    }

    private val navy = CxHomeV2Colors.CFF003B71
    private val blue = CxHomeV2Colors.CFF005CA9
    private val regular = FontFamily(Font(R.font.caixa_std_regular))
    private val bold = FontFamily(Font(R.font.caixa_std_bold))
    private val order = listOf(HomeV2Modality.MEGA_SENA, HomeV2Modality.MAIS_MILIONARIA,
        HomeV2Modality.LOTECA, HomeV2Modality.DIA_DE_SORTE, HomeV2Modality.DUPLA_SENA,
        HomeV2Modality.LOTOMANIA, HomeV2Modality.QUINA, HomeV2Modality.TIMEMANIA,
        HomeV2Modality.LOTOFACIL, HomeV2Modality.SUPER_7)


    private var cardOrder by mutableStateOf(HomeV2ModalityOrder.restore(
        layoutPreferences.loadOrder(), order.map { it.name }))

    private fun moveModality(from: HomeV2Modality, to: HomeV2Modality) {
        val updated = HomeV2ModalityOrder.move(cardOrder, from.name, to.name)
        if (updated != cardOrder) {
            cardOrder = updated
            layoutPreferences.saveOrder(updated.joinToString(","))
        }
    }

    @Composable fun Home(modalidades: List<HomeLottery>, loading: Boolean, results: Map<String, DrawResult>) {
        val scroll = rememberScrollState()
        val scope = rememberCoroutineScope()
        val cards = modalidades.filter { it.tipoModalidade in order }
            .sortedBy { order.indexOf(it.tipoModalidade) }
        var showAll by remember { mutableStateOf(false) }
        var viewport by remember { mutableStateOf(Rect.Zero) }
        var navigationHeight by remember { mutableStateOf(84.dp) }
        val density = LocalDensity.current
        Box(Modifier.fillMaxSize().background(CxHomeV2Colors.CFFF5F7FC)) {
            Column(Modifier.fillMaxSize().onGloballyPositioned {
                val bounds = it.boundsInRoot()
                viewport = Rect(bounds.left, bounds.top, bounds.right,
                    bounds.bottom - with(density) { navigationHeight.toPx() })
            }.verticalScroll(scroll).padding(bottom = navigationHeight)) {
                Header()
                if (loading) {
                    HomeShimmer()
                } else {
                    val heroCards = cards.take(4)
                    if (heroCards.isNotEmpty()) {
                        val pager = rememberPagerState(pageCount = { heroCards.size })
                        Box(Modifier.padding(top = 12.dp)) {
                            HorizontalPager(pager, contentPadding = PaddingValues(horizontal = 12.dp),
                                pageSpacing = 10.dp) { Hero(heroCards[it]) }
                        }
                        Dots(heroCards.size, pager.currentPage)
                    }
                    Column(Modifier.padding(horizontal = 13.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val poolCard = cards.firstOrNull { actions.canPool(it.tipoModalidade) }
                        if (poolCard != null) {
                            Spacer(Modifier.height(6.dp))
                            PoolBanner(poolCard)
                        }
                        Column(Modifier.padding(top = if (poolCard != null) 8.dp else 0.dp)) {
                            Section("Escolha sua modalidade") { showAll = !showAll }
                            Text("Segure e arraste para organizar", fontFamily = regular,
                                fontSize = 11.sp, color = CxHomeV2Colors.CFF647C94, modifier = Modifier.padding(top = 3.dp))
                        }
                        val arrangedCards = cards.sortedBy { cardOrder.indexOf(it.tipoModalidade.name) }
                        ReorderableModalities(if (showAll) arrangedCards else arrangedCards.take(8), scroll, viewport) { showAll = true }
                        Spacer(Modifier.height(5.dp))
                        Section("Últimos resultados", actions::results)
                        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            cards.filter { it.tipoModalidade in listOf(HomeV2Modality.MEGA_SENA,
                                HomeV2Modality.MAIS_MILIONARIA, HomeV2Modality.QUINA) }.forEach { ResultCard(it, results[it.resultCode]) }
                        }
                        Spacer(Modifier.height(4.dp))
                        Title("Mais para você", 18)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Shortcut("Meus jogos", "Acompanhe suas apostas", R.drawable.ic_home_v2_bilhete, Modifier.weight(1f), actions::games)
                            Shortcut("Resultados", "Consulte históricos", R.drawable.ic_home_v2_resultados, Modifier.weight(1f), actions::results)
                            Shortcut("Estatísticas", "Veja números e probabilidades", R.drawable.ic_home_v2_estatisticas, Modifier.weight(1f), actions::results)
                            Shortcut("Promoções", "Fique por dentro das novidades", R.drawable.ic_home_v2_presente, Modifier.weight(1f), actions::notifications)
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .onGloballyPositioned { navigationHeight = with(density) { it.size.height.toDp() } }
                .shadow(6.dp)
                .background(Brush.verticalGradient(listOf(
                    CxHomeV2Colors.White.copy(alpha = .98f), CxHomeV2Colors.White.copy(alpha = .96f))))
                .border(1.dp, CxHomeV2Colors.White.copy(alpha = .65f))
                .padding(vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Nav("Início", R.drawable.trevo_megasena_fundo_branco, true, Modifier.weight(1f)) { scope.launchScroll(scroll, 0) }
                Nav("Minhas\nApostas", R.drawable.ic_home_v2_bilhete, false, Modifier.weight(1f), action = actions::games)
                Nav("Carrinho", R.drawable.ic_carrinho2, false, Modifier.weight(1f), emphasized = true, action = actions::cart)
                Nav("Resultados", R.drawable.ic_home_v2_resultados, false, Modifier.weight(1f), action = actions::results)
                Nav("Apostas\nFavoritas", R.drawable.ic_favoritar2, false, Modifier.weight(1f), action = actions::favorites)
            }
        }
    }

    @Composable private fun HomeShimmer() {
        val transition = rememberInfiniteTransition(label = "homeLoading")
        val progress by transition.animateFloat(
            initialValue = -1f, targetValue = 2f,
            animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
            label = "shimmerProgress"
        )
        Column(Modifier.fillMaxWidth().semantics { contentDescription = "Carregando a home" }) {
            Box(Modifier.padding(12.dp)) {
                ShimmerBlock(Modifier.fillMaxWidth().height(220.dp), progress, 12)
            }
            Column(Modifier.padding(horizontal = 13.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ShimmerBlock(Modifier.fillMaxWidth().height(64.dp), progress, 13)
                ShimmerBlock(Modifier.width(210.dp).height(24.dp), progress)
                repeat(4) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(2) { ShimmerBlock(Modifier.weight(1f).height(85.dp), progress, 11) }
                    }
                }
                Spacer(Modifier.height(5.dp))
                ShimmerBlock(Modifier.width(165.dp).height(24.dp), progress)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(2) { ShimmerBlock(Modifier.weight(1f).height(95.dp), progress, 11) }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    @Composable private fun ShimmerBlock(modifier: Modifier, progress: Float, radius: Int = 6) {
        Canvas(modifier.clip(RoundedCornerShape(radius.dp))) {
            val start = size.width * progress
            drawRect(Brush.linearGradient(
                listOf(CxHomeV2Colors.CFFDCE2EC, CxHomeV2Colors.CFFF4F6FA, CxHomeV2Colors.CFFDCE2EC),
                start = Offset(start, 0f), end = Offset(start + size.width, size.height)
            ))
        }
    }

    @Composable private fun Header() {
        Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(blue, CxHomeV2Colors.CFF006BB4)))
            .padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("☰", color = CxHomeV2Colors.White, fontSize = 25.sp,
                modifier = Modifier.size(40.dp).clickable(onClickLabel = "Abrir menu", onClick = actions::menu))
            Text("Loterias CAIXA", color = CxHomeV2Colors.White, fontFamily = bold, fontSize = 20.sp,
                modifier = Modifier.weight(1f).padding(start = 6.dp))
            IconButton(onClick = actions::notifications) {
                Icon(painterResource(R.drawable.menu8_notificacao), "Notificações", tint = CxHomeV2Colors.White)
            }
        }
    }

    @Composable private fun Hero(item: HomeLottery) {
        val type = item.tipoModalidade
        Box(Modifier.fillMaxWidth().heightIn(min = 220.dp).clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(colors(type)))) {
            if (type == HomeV2Modality.MEGA_SENA) {
                Image(painterResource(R.drawable.home_v2_mega_corporativo), null,
                    Modifier.matchParentSize(), contentScale = ContentScale.Crop, alignment = Alignment.CenterEnd)
                Box(Modifier.matchParentSize().background(Brush.horizontalGradient(
                    0f to CxHomeV2Colors.CFF006B43.copy(alpha = .97f),
                    .45f to CxHomeV2Colors.CFF006B43.copy(alpha = .85f),
                    .75f to CxHomeV2Colors.Transparent, 1f to CxHomeV2Colors.Transparent)))
            }
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PRÓXIMO SORTEIO", fontFamily = bold, fontSize = 10.sp, color = CxHomeV2Colors.White,
                        modifier = Modifier.padding(vertical = 6.dp))
                    Text("Último resultado ›", color = CxHomeV2Colors.White, fontSize = 11.sp,
                        modifier = Modifier.clip(CircleShape).background(CxHomeV2Colors.C44003322).border(1.dp, CxHomeV2Colors.White.copy(alpha = .45f), CircleShape)
                            .clickable { actions.modalityResults(type) }.padding(8.dp))
                }
                Column(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(icon(type)), null, Modifier.size(26.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(name(type), fontFamily = bold, fontSize = 24.sp, color = CxHomeV2Colors.White)
                    }
                    Text("Prêmio estimado", fontFamily = bold, fontSize = 13.sp, color = CxHomeV2Colors.White)
                    Text(prize(item), fontFamily = bold, fontSize = 25.sp, color = CxHomeV2Colors.White, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(painterResource(R.drawable.ic_calendario), null, Modifier.size(17.dp), tint = CxHomeV2Colors.White)
                        Text(date(item), color = CxHomeV2Colors.White, fontFamily = regular, fontSize = 11.sp)
                        Text("Concurso ${item.contestNumber}", color = CxHomeV2Colors.White,
                            fontFamily = regular, fontSize = 11.sp)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (actions.canPool(type)) Button(onClick = { actions.pool(item) },
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                        shape = RoundedCornerShape(7.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CxHomeV2Colors.White, contentColor = navy)) {
                        Text("Compre seu bolão", fontFamily = bold, fontSize = 12.sp)
                    }
                    Button(onClick = { actions.bet(item) },
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                        shape = RoundedCornerShape(7.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CxHomeV2Colors.White, contentColor = navy)) {
                        Text("Aposte", fontFamily = bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    @Composable private fun ReorderableModalities(items: List<HomeLottery>, scroll: ScrollState, viewport: Rect, expand: () -> Unit) {
        val bounds = remember { mutableMapOf<HomeV2Modality, Rect>() }
        var origin by remember { mutableStateOf(Offset.Zero) }
        var dragged by remember { mutableStateOf<HomeV2Modality?>(null) }
        var target by remember { mutableStateOf<HomeV2Modality?>(null) }
        var pointer by remember { mutableStateOf(Offset.Zero) }
        var grabOffset by remember { mutableStateOf(Offset.Zero) }
        val currentItems by rememberUpdatedState(items)
        val currentViewport by rememberUpdatedState(viewport)
        val currentExpand by rememberUpdatedState(expand)
        val density = LocalDensity.current
        val haptic = LocalHapticFeedback.current
        LaunchedEffect(dragged) {
            if (dragged != null) {
                val edge = with(density) { 48.dp.toPx() }
                val step = with(density) { 10.dp.toPx() }
                while (isActive) {
                    withFrameNanos { }
                    val view = currentViewport
                    val amount = when {
                        pointer.y < view.top + edge -> -step
                        pointer.y > view.bottom - edge -> step
                        else -> 0f
                    }
                    if (amount != 0f) {
                        scroll.scrollBy(amount)
                        target = currentItems.firstOrNull { bounds[it.tipoModalidade]?.contains(pointer) == true }?.tipoModalidade
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().onGloballyPositioned { origin = it.positionInRoot() }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { local ->
                        pointer = origin + local
                        dragged = currentItems.firstOrNull { bounds[it.tipoModalidade]?.contains(pointer) == true }?.tipoModalidade
                        target = dragged
                        dragged?.let { type ->
                            grabOffset = pointer - bounds.getValue(type).topLeft
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentExpand()
                        }
                    },
                    onDrag = { change, amount ->
                        if (dragged != null) {
                            change.consume()
                            pointer += amount
                            target = currentItems.firstOrNull { bounds[it.tipoModalidade]?.contains(pointer) == true }?.tipoModalidade
                        }
                    },
                    onDragEnd = {
                        val from = dragged
                        val to = target
                        dragged = null
                        target = null
                        if (from != null && to != null) moveModality(from, to)
                    },
                    onDragCancel = { dragged = null; target = null }
                )
            }) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { item ->
                            key(item.tipoModalidade) {
                                val type = item.tipoModalidade
                                val index = items.indexOf(item)
                                ModalityCard(item, Modifier.weight(1f)
                                    .onGloballyPositioned { bounds[type] = it.boundsInRoot() }
                                    .alpha(if (dragged == type) .3f else 1f)
                                    .then(if (target == type && dragged != type) Modifier.border(2.dp, blue, RoundedCornerShape(11.dp)) else Modifier)
                                    .semantics {
                                        customActions = buildList {
                                            if (index > 0) add(CustomAccessibilityAction("Mover para posição anterior") {
                                                moveModality(type, items[index - 1].tipoModalidade); true
                                            })
                                            if (index < items.lastIndex) add(CustomAccessibilityAction("Mover para próxima posição") {
                                                moveModality(type, items[index + 1].tipoModalidade); true
                                            })
                                        }
                                    })
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            dragged?.let { type ->
                val item = items.firstOrNull { it.tipoModalidade == type }
                val rect = bounds[type]
                if (item != null && rect != null) {
                    ModalityCard(item, Modifier.offset {
                        val position = pointer - grabOffset - origin
                        IntOffset(position.x.roundToInt(), position.y.roundToInt())
                    }.width(with(density) { rect.width.toDp() }).zIndex(1f)
                        .shadow(8.dp, RoundedCornerShape(11.dp)))
                }
            }
        }
    }

    @Composable private fun ModalityCard(item: HomeLottery, modifier: Modifier) {
        val isDiaDeSorte = item.tipoModalidade == HomeV2Modality.DIA_DE_SORTE
        val ink = if (isDiaDeSorte) navy else CxHomeV2Colors.White
        Row(modifier.heightIn(min = 78.dp).clip(RoundedCornerShape(8.dp))
            .background(Brush.horizontalGradient(colors(item.tipoModalidade)))
            .clickable(onClickLabel = "Apostar em ${name(item.tipoModalidade)}") { actions.bet(item) }
            .padding(9.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(icon(item.tipoModalidade)), null, Modifier.size(29.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(name(item.tipoModalidade), fontFamily = bold, fontSize = 13.sp, color = ink)
                Text(prize(item), fontFamily = bold, fontSize = 13.sp, color = ink, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Row(Modifier.padding(top = 3.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painterResource(R.drawable.ic_calendario), null, Modifier.size(12.dp), tint = ink)
                    Text(date(item), fontFamily = regular, fontSize = 10.sp, color = ink, maxLines = 1)
                }
            }
            Text("›", fontSize = 24.sp, color = ink)
        }
    }

    @Composable private fun ResultCard(item: HomeLottery,
        result: DrawResult?) {
        val ink = colors(item.tipoModalidade).first()
        Column(Modifier.width(190.dp).heightIn(min = 128.dp).fillMaxHeight().shadow(2.dp, RoundedCornerShape(9.dp)).clip(RoundedCornerShape(9.dp))
            .background(CxHomeV2Colors.White).clickable { actions.modalityResults(item.tipoModalidade) }.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(icon(item.tipoModalidade)), null, Modifier.size(22.dp))
                Spacer(Modifier.width(6.dp))
                Text(name(item.tipoModalidade), color = ink, fontFamily = bold, fontSize = 12.sp)
            }
            if (result == null) {
                Text("Consultar último resultado", fontSize = 11.sp, color = navy, modifier = Modifier.padding(top = 12.dp))
            } else {
                Text("Concurso ${result.contest}", fontSize = 10.sp, color = navy)
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    result.firstDrawNumbers.take(6).forEach { number ->
                        Box(Modifier.size(25.dp).background(ink, CircleShape), contentAlignment = Alignment.Center) {
                            Text("%02d".format(number), color = CxHomeV2Colors.White, fontSize = 11.sp,
                                lineHeight = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                                maxLines = 1, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)))
                        }
                    }
                }
                result.clovers.takeIf { it.isNotEmpty() }?.let {
                    Text("Trevos: ${it.joinToString(" • ")}", fontSize = 10.sp, color = ink)
                }
            }
            Spacer(Modifier.weight(1f))
            HorizontalDivider(Modifier.padding(top = 12.dp, bottom = 7.dp), color = CxHomeV2Colors.CFFE1E8EF)
            Text("Ver resultado ›", color = blue, fontFamily = bold, fontSize = 11.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }

    @Composable private fun PoolBanner(item: HomeLottery?) {
        if (item == null) return
        Box(Modifier.fillMaxWidth().heightIn(min = 64.dp).clip(RoundedCornerShape(9.dp))
            .clickable(onClickLabel = "Compre seu bolão") { actions.pool(item) }) {
            Image(painterResource(R.drawable.home_v2_bolao_corporativo), null,
                Modifier.matchParentSize(), contentScale = ContentScale.Crop, alignment = Alignment.CenterEnd)
            Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(
                CxHomeV2Colors.CFF003B71.copy(alpha = .50f), CxHomeV2Colors.CFF003B71.copy(alpha = .12f)))))
            Column(Modifier.align(Alignment.CenterStart).fillMaxWidth(.78f).padding(horizontal = 14.dp, vertical = 14.dp)) {
                Text("Compre seu bolão", color = CxHomeV2Colors.White, fontFamily = bold,
                    fontSize = 16.sp, lineHeight = 20.sp)
                Text("Jogue com amigos e aumente suas chances de ganhar.", color = CxHomeV2Colors.White,
                    fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
    }

    @Composable private fun Shortcut(title: String, detail: String, icon: Int, modifier: Modifier, action: () -> Unit) {
        Column(modifier.heightIn(min = 72.dp).shadow(1.dp, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp))
            .background(CxHomeV2Colors.White).semantics { contentDescription = "$title. $detail" }
            .clickable(onClick = action).padding(horizontal = 3.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(painterResource(icon), null, tint = navy, modifier = Modifier.size(26.dp))
            Text(title, color = navy, fontFamily = bold, fontSize = 10.sp, lineHeight = 14.sp,
                textAlign = TextAlign.Center)
        }
    }
    @Composable private fun Section(title: String, action: () -> Unit) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { Title(title, 18) }
            Text("Ver todas  ›", color = blue, fontSize = 12.sp, modifier = Modifier.clickable(onClick = action))
        }
    }
    @Composable private fun Title(text: String, size: Int) {
        Text(text, color = navy, fontFamily = bold, fontSize = size.sp)
    }
    @Composable private fun Dots(count: Int, selected: Int) {
        Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), horizontalArrangement = Arrangement.Center) {
            repeat(count) { Box(Modifier.padding(horizontal = 4.dp).size(if (it == selected) 12.dp else 6.dp, 6.dp)
                .background(if (it == selected) blue else CxHomeV2Colors.CFFCAD5E7, CircleShape)) }
        }
    }
    @Composable private fun Nav(title: String, icon: Int, selected: Boolean,
        modifier: Modifier, emphasized: Boolean = false, originalColors: Boolean = false, action: () -> Unit) {
        val ink = if (selected || emphasized) blue else CxHomeV2Colors.CFF30445A
        Column(modifier.clickable(onClick = action).padding(horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(42.dp).then(if (emphasized) Modifier.background(CxHomeV2Colors.CFFE9F0FC, CircleShape) else Modifier),
                contentAlignment = Alignment.Center) {
                if (originalColors) {
                    Image(painterResource(icon), null, Modifier.size(28.dp), contentScale = ContentScale.Fit)
                } else {
                    Icon(painterResource(icon), null, tint = ink,
                        modifier = Modifier.size(28.dp))
                }
            }
            Text(title, color = ink, fontSize = 12.sp, lineHeight = 14.sp, fontFamily = regular,
                textAlign = TextAlign.Center, modifier = Modifier.heightIn(min = 28.dp))
        }
    }
    private fun prize(item: HomeLottery) = HomeV2Formatting.prize(item.estimatedPrize)
    private fun date(item: HomeLottery) = HomeV2Formatting.date(item.drawDate)
    private fun name(type: HomeV2Modality): String = when (type) {
        HomeV2Modality.MEGA_SENA -> "Mega-Sena"; HomeV2Modality.MAIS_MILIONARIA -> "+Milionária"
        HomeV2Modality.DIA_DE_SORTE -> "Dia de Sorte"; HomeV2Modality.DUPLA_SENA -> "Dupla Sena"
        HomeV2Modality.SUPER_7 -> "Super Sete"; HomeV2Modality.LOTOFACIL -> "Lotofácil"
        else -> type.name.lowercase().replaceFirstChar { it.titlecase() }
    }
    private fun colors(type: HomeV2Modality): List<Color> = when (type) {
        HomeV2Modality.MEGA_SENA -> listOf(CxHomeV2Colors.CFF008852, CxHomeV2Colors.CFF006B43)
        HomeV2Modality.MAIS_MILIONARIA -> listOf(CxHomeV2Colors.CFF6533C7, CxHomeV2Colors.CFF4A28A3)
        HomeV2Modality.LOTECA -> listOf(CxHomeV2Colors.CFFD32E49, CxHomeV2Colors.CFFBF2943)
        HomeV2Modality.DUPLA_SENA -> listOf(CxHomeV2Colors.CFFAF2255, CxHomeV2Colors.CFF94214C)
        HomeV2Modality.DIA_DE_SORTE -> listOf(CxHomeV2Colors.CFFFFC52B, CxHomeV2Colors.CFFF5B816)
        HomeV2Modality.LOTOMANIA -> listOf(CxHomeV2Colors.CFFFF852A, CxHomeV2Colors.CFFF06C12)
        HomeV2Modality.QUINA -> listOf(CxHomeV2Colors.CFF075AC0, CxHomeV2Colors.CFF06489D)
        HomeV2Modality.LOTOFACIL -> listOf(CxHomeV2Colors.CFF8B2387, CxHomeV2Colors.CFF731B70)
        else -> listOf(CxHomeV2Colors.CFF6A35C7, CxHomeV2Colors.CFF49279F)
    }
    private fun icon(type: HomeV2Modality): Int = when (type) {
        HomeV2Modality.MEGA_SENA -> R.drawable.trevo_megasena_fundo_branco
        HomeV2Modality.MAIS_MILIONARIA -> R.drawable.trevo_mais_milionaria
        HomeV2Modality.DIA_DE_SORTE -> R.drawable.trevo_dia_de_sorte_fundo_branco
        HomeV2Modality.LOTECA -> R.drawable.trevo_loteca_fundo_escuro
        HomeV2Modality.DUPLA_SENA -> R.drawable.trevo_dupla_sena_fundo_escuro
        HomeV2Modality.LOTOMANIA -> R.drawable.trevo_lotomania
        HomeV2Modality.QUINA -> R.drawable.trevo_quina_fundo_branco
        HomeV2Modality.TIMEMANIA -> R.drawable.trevo_timemania_fundo_escuro
        HomeV2Modality.SUPER_7 -> R.drawable.trevo_super_sete_fundo_branco
        else -> R.drawable.trevo_lotofacil_fundo_branco
    }
}

private fun kotlinx.coroutines.CoroutineScope.launchScroll(state: ScrollState, target: Int) {
    launch { state.animateScrollTo(target.coerceAtMost(state.maxValue)) }
}

internal enum class HomeV2Modality(val resultCode: String) {
    MEGA_SENA("megasena"), MAIS_MILIONARIA("maismilionaria"), LOTECA("loteca"),
    DIA_DE_SORTE("diadesorte"), DUPLA_SENA("duplasena"), LOTOMANIA("lotomania"),
    QUINA("quina"), TIMEMANIA("timemania"), LOTOFACIL("lotofacil"), SUPER_7("supersete"), OTHER("")
}
private val HomeLottery.tipoModalidade: HomeV2Modality
    get() = HomeV2Modality.entries.firstOrNull { it.resultCode == resultCode } ?: HomeV2Modality.OTHER

@Composable
internal fun HomeV2Content(
    lotteries: List<HomeLottery>, loading: Boolean, results: Map<String, DrawResult>,
    preferences: HomeLayoutPreferences, actions: HomeV2Presentation.Actions,
) {
    val currentActions by rememberUpdatedState(actions)
    val presentation = remember(preferences) { HomeV2Presentation(preferences) { currentActions } }
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
        MaterialTheme { presentation.Home(lotteries, loading, results) }
    }
}
