package br.gov.caixa.loterias.apostas.view.homev2

import android.content.Context
import android.widget.FrameLayout
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
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
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP
import br.gov.caixa.loterias.apostas.model.bean.Modalidade
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive

/** Presentation only: navigation, authentication and purchases remain with PrincipalActivity. */
class HomeV2View(context: Context, private val actions: Actions) : FrameLayout(context) {
    interface Actions {
        fun bet(modalidade: Modalidade)
        fun pool(modalidade: Modalidade)
        fun results()
        fun modalityResults(modalidade: ModalidadeEnum)
        fun games()
        fun favorites()
        fun menu()
        fun notifications()
        fun cart()
        fun profile()
        fun canPool(modalidade: ModalidadeEnum): Boolean
    }

    private var modalidades by mutableStateOf<List<Modalidade>>(emptyList(), neverEqualPolicy())
    private var loading by mutableStateOf(true)
    fun startLoading() { loading = true }
    fun bind(items: List<Modalidade>) {
        modalidades = items.toList()
        loading = false
    }

    init {
        addView(ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val deviceDensity = LocalDensity.current
                // Keep Home text sizes fixed while preserving the device's pixel density.
                CompositionLocalProvider(LocalDensity provides Density(deviceDensity.density, fontScale = 1f)) {
                    MaterialTheme { Home() }
                }
            }
        }, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private val navy = Color(0xFF081970)
    private val blue = Color(0xFF0752CA)
    private val regular = FontFamily(Font(R.font.caixa_std_regular))
    private val bold = FontFamily(Font(R.font.caixa_std_bold))
    private val order = listOf(ModalidadeEnum.MEGA_SENA, ModalidadeEnum.MAIS_MILIONARIA,
        ModalidadeEnum.LOTECA, ModalidadeEnum.DIA_DE_SORTE, ModalidadeEnum.DUPLA_SENA,
        ModalidadeEnum.LOTOMANIA, ModalidadeEnum.QUINA, ModalidadeEnum.TIMEMANIA,
        ModalidadeEnum.LOTOFACIL, ModalidadeEnum.SUPER_7)

    private val orderPreferences = context.getSharedPreferences("home_v2_layout", Context.MODE_PRIVATE)
    private var cardOrder by mutableStateOf(HomeV2ModalityOrder.restore(
        orderPreferences.getString("modality_order", null), order.map { it.name }))

    private fun moveModality(from: ModalidadeEnum, to: ModalidadeEnum) {
        val updated = HomeV2ModalityOrder.move(cardOrder, from.name, to.name)
        if (updated != cardOrder) {
            cardOrder = updated
            orderPreferences.edit().putString("modality_order", updated.joinToString(",")).apply()
        }
    }

    @Composable private fun Home() {
        val scroll = rememberScrollState()
        val scope = rememberCoroutineScope()
        val cards = modalidades.filter { it.tipoModalidade in order }
            .sortedBy { order.indexOf(it.tipoModalidade) }
        var showAll by remember { mutableStateOf(false) }
        var viewport by remember { mutableStateOf(Rect.Zero) }
        var navigationHeight by remember { mutableStateOf(84.dp) }
        val density = LocalDensity.current
        Box(Modifier.fillMaxSize().background(Color(0xFFF5F7FC))) {
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
                        Box(Modifier.background(Brush.verticalGradient(listOf(blue, Color(0xFFF5F7FC)))).padding(top = 12.dp)) {
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
                        Box(Modifier.padding(top = if (poolCard != null) 8.dp else 0.dp)) {
                            Section("Escolha sua modalidade") { showAll = !showAll }
                        }
                        val arrangedCards = cards.sortedBy { cardOrder.indexOf(it.tipoModalidade.name) }
                        ReorderableModalities(if (showAll) arrangedCards else arrangedCards.take(8), scroll, viewport) { showAll = true }
                        Spacer(Modifier.height(5.dp))
                        Section("Últimos resultados", actions::results)
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            cards.filter { it.tipoModalidade in listOf(ModalidadeEnum.MEGA_SENA,
                                ModalidadeEnum.MAIS_MILIONARIA, ModalidadeEnum.QUINA) }.forEach { ResultCard(it, it.resultadoConcursoDTO) }
                        }
                        Spacer(Modifier.height(4.dp))
                        Title("Mais para você", 16)
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            Shortcut("Meus jogos", "Acompanhe suas apostas", R.drawable.ic_menu_minha_area, actions::games)
                            Shortcut("Resultados", "Consulte históricos", R.drawable.ic_menu_resultados, actions::results)
                            Shortcut("Estatísticas", "Veja números e probabilidades", R.drawable.ic_menu_resultados, actions::results)
                            Shortcut("Promoções", "Fique por dentro das novidades", R.drawable.menu8_notificacao, actions::notifications)
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .onGloballyPositioned { navigationHeight = with(density) { it.size.height.toDp() } }
                .shadow(6.dp)
                .background(Brush.verticalGradient(listOf(
                    Color.White.copy(alpha = .94f), Color.White.copy(alpha = .90f))))
                .border(1.dp, Color.White.copy(alpha = .65f))
                .padding(vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Nav("Início", R.drawable.ic_loterias_caixa, true, Modifier.weight(1f)) { scope.launchScroll(scroll, 0) }
                Nav("Minhas\nApostas", R.drawable.ic_menu_conferir_bilhete, false, Modifier.weight(1f), action = actions::games)
                Nav("Carrinho", R.drawable.ic_carrinho2, false, Modifier.weight(1f), emphasized = true, action = actions::cart)
                Nav("Resultados", R.drawable.ic_menu_resultados, false, Modifier.weight(1f), action = actions::results)
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
            Box(Modifier.background(Brush.verticalGradient(listOf(blue, Color(0xFFF5F7FC)))).padding(12.dp)) {
                ShimmerBlock(Modifier.fillMaxWidth().height(275.dp), progress, 14)
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
                listOf(Color(0xFFDCE2EC), Color(0xFFF4F6FA), Color(0xFFDCE2EC)),
                start = Offset(start, 0f), end = Offset(start + size.width, size.height)
            ))
        }
    }

    @Composable private fun Header() {
        Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF06429E), blue)))
            .padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("☰", color = Color.White, fontSize = 25.sp,
                modifier = Modifier.size(40.dp).clickable(onClickLabel = "Abrir menu", onClick = actions::menu))
            Text("Loterias CAIXA", color = Color.White, fontFamily = bold, fontSize = 18.sp,
                modifier = Modifier.weight(1f).padding(start = 6.dp))
            IconButton(onClick = actions::notifications) {
                Icon(painterResource(R.drawable.menu8_notificacao), "Notificações", tint = Color.White)
            }
        }
    }

    @Composable private fun Hero(item: Modalidade) {
        val type = item.tipoModalidade
        Box(Modifier.fillMaxWidth().heightIn(min = 275.dp).clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(colors(type)))) {
            if (type == ModalidadeEnum.MEGA_SENA) Image(painterResource(R.drawable.home_v2_mega), null,
                Modifier.matchParentSize(), contentScale = ContentScale.Crop)
            Column(Modifier.fillMaxWidth().padding(17.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PRÓXIMO SORTEIO", fontFamily = bold, fontSize = 10.sp, color = Color.White,
                        modifier = Modifier.clip(CircleShape).background(Color(0xAA008E45)).padding(8.dp, 4.dp))
                    Text("Último resultado ›", color = Color.White, fontSize = 11.sp,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xAA003522))
                            .clickable { actions.modalityResults(type) }.padding(8.dp))
                }
                Column(Modifier.fillMaxWidth(.78f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(icon(type)), null, Modifier.size(26.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(name(type), fontFamily = bold, fontSize = 23.sp, color = Color.White)
                    }
                    Text("Prêmio estimado", fontFamily = bold, fontSize = 13.sp, color = Color.White)
                    Text(prize(item), fontFamily = bold, fontSize = 25.sp, color = Color.White, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    Text("${date(item)}\nConcurso ${item.concurso?.numero ?: "—"}", color = Color.White,
                        fontFamily = regular, fontSize = 12.sp)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (actions.canPool(type)) Button(onClick = { actions.pool(item) },
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = navy)) {
                        Text("Compre seu bolão", fontFamily = bold, fontSize = 12.sp)
                    }
                    Button(onClick = { actions.bet(item) },
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = navy)) {
                        Text("Aposte", fontFamily = bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    @Composable private fun ReorderableModalities(items: List<Modalidade>, scroll: ScrollState, viewport: Rect, expand: () -> Unit) {
        val bounds = remember { mutableMapOf<ModalidadeEnum, Rect>() }
        var origin by remember { mutableStateOf(Offset.Zero) }
        var dragged by remember { mutableStateOf<ModalidadeEnum?>(null) }
        var target by remember { mutableStateOf<ModalidadeEnum?>(null) }
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

    @Composable private fun ModalityCard(item: Modalidade, modifier: Modifier) {
        val style = remember(item.tipoModalidade) { EstiloModalidadeMKP(item.tipoModalidade) }
        val isDiaDeSorte = item.tipoModalidade == ModalidadeEnum.DIA_DE_SORTE
        val ink = if (isDiaDeSorte) Color.White else colorResource(style.corFonteFundoClaro)
        val background = if (isDiaDeSorte) Color(0xFFEACB3D) else colorResource(style.corClara)
        Row(modifier.heightIn(min = 67.dp).clip(RoundedCornerShape(11.dp))
            .background(background)
            .clickable(onClickLabel = "Apostar em ${name(item.tipoModalidade)}") { actions.bet(item) }
            .padding(9.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(icon(item.tipoModalidade)), null, Modifier.size(29.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(name(item.tipoModalidade), fontFamily = bold, fontSize = 12.sp, color = ink)
                Text(prize(item), fontFamily = bold, fontSize = 12.sp, color = ink, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Text(date(item), fontFamily = regular, fontSize = 10.sp, color = ink, maxLines = 1)
            }
            Text("›", fontSize = 24.sp, color = ink)
        }
    }

    @Composable private fun ResultCard(item: Modalidade,
        result: br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO<*>?) {
        val ink = colors(item.tipoModalidade).first()
        Column(Modifier.width(190.dp).heightIn(min = 95.dp).clip(RoundedCornerShape(11.dp))
            .background(Color.White).clickable { actions.modalityResults(item.tipoModalidade) }.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(icon(item.tipoModalidade)), null, Modifier.size(22.dp))
                Spacer(Modifier.width(6.dp))
                Text(name(item.tipoModalidade), color = ink, fontFamily = bold, fontSize = 12.sp)
            }
            if (result == null) {
                Text("Consultar último resultado ›", fontSize = 11.sp, color = navy, modifier = Modifier.padding(top = 12.dp))
            } else {
                Text("Concurso ${result.concurso?.numero ?: "—"}", fontSize = 10.sp, color = navy)
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    result.listaSorteadosPrimeiroSorteio?.take(6)?.forEach { number ->
                        Box(Modifier.size(25.dp).background(ink, CircleShape), contentAlignment = Alignment.Center) {
                            Text("%02d".format(number), color = Color.White, fontSize = 11.sp,
                                lineHeight = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                                maxLines = 1, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)))
                        }
                    }
                }
                result.trevosSorteadosPrimeiroSorteio?.takeIf { it.isNotEmpty() }?.let {
                    Text("Trevos: ${it.joinToString(" • ")}", fontSize = 10.sp, color = ink)
                }
            }
        }
    }

    @Composable private fun PoolBanner(item: Modalidade?) {
        if (item == null) return
        Box(Modifier.fillMaxWidth().heightIn(min = 64.dp).clip(RoundedCornerShape(13.dp))
            .clickable(onClickLabel = "Compre seu bolão") { actions.pool(item) }) {
            Image(painterResource(R.drawable.home_v2_bolao), null,
                Modifier.matchParentSize(), contentScale = ContentScale.Crop)
            Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(
                Color(0xFF031C55).copy(alpha = .88f),
                Color(0xFF031C55).copy(alpha = .68f),
                Color(0xFF031C55).copy(alpha = .30f)
            ))))
            Column(Modifier.align(Alignment.CenterStart).fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp)) {
                Text("Compre seu bolão", color = Color.White, fontFamily = bold,
                    fontSize = 16.sp, lineHeight = 20.sp)
                Text("Jogue com amigos e aumente suas chances de ganhar.", color = Color.White,
                    fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
    }

    @Composable private fun Shortcut(title: String, detail: String, icon: Int, action: () -> Unit) {
        Row(Modifier.width(150.dp).heightIn(min = 80.dp).clip(RoundedCornerShape(9.dp))
            .background(Color.White).clickable(onClick = action).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(icon), null, tint = blue, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = navy, fontFamily = bold, fontSize = 11.sp, lineHeight = 16.sp)
                Text(detail, color = navy, fontFamily = regular, fontSize = 10.sp, lineHeight = 14.sp)
            }
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
                .background(if (it == selected) blue else Color(0xFFCAD5E7), CircleShape)) }
        }
    }
    @Composable private fun Nav(title: String, icon: Int, selected: Boolean,
        modifier: Modifier, emphasized: Boolean = false, originalColors: Boolean = false, action: () -> Unit) {
        val ink = if (selected || emphasized) blue else Color(0xFF30445A)
        Column(modifier.clickable(onClick = action).padding(horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(42.dp).then(if (emphasized) Modifier.background(Color(0xFFE9F0FC), CircleShape) else Modifier),
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
    private fun prize(item: Modalidade) = HomeV2Formatting.prize(item.concurso?.estimativa)
    private fun date(item: Modalidade) = HomeV2Formatting.date(item.concurso?.dataHoraSorteio ?: item.concurso?.dataSorteio)
    private fun name(type: ModalidadeEnum): String = when (type) {
        ModalidadeEnum.MEGA_SENA -> "Mega-Sena"; ModalidadeEnum.MAIS_MILIONARIA -> "+Milionária"
        ModalidadeEnum.DIA_DE_SORTE -> "Dia de Sorte"; ModalidadeEnum.DUPLA_SENA -> "Dupla Sena"
        ModalidadeEnum.SUPER_7 -> "Super Sete"; ModalidadeEnum.LOTOFACIL -> "Lotofácil"
        else -> type.name.lowercase().replaceFirstChar { it.titlecase() }
    }
    private fun colors(type: ModalidadeEnum): List<Color> = when (type) {
        ModalidadeEnum.MEGA_SENA -> listOf(Color(0xFF008B48), Color(0xFF11C99A))
        ModalidadeEnum.MAIS_MILIONARIA -> listOf(Color(0xFF5D0CCD), Color(0xFFAA41FE))
        ModalidadeEnum.LOTECA, ModalidadeEnum.DUPLA_SENA -> listOf(Color(0xFFEF3560), Color(0xFFFF6595))
        ModalidadeEnum.DIA_DE_SORTE -> listOf(Color(0xFFFFCD24), Color(0xFFFFE66B))
        ModalidadeEnum.LOTOMANIA -> listOf(Color(0xFFFF6420), Color(0xFFFFA160))
        ModalidadeEnum.QUINA -> listOf(Color(0xFF0054CD), Color(0xFF198FFA))
        else -> listOf(Color(0xFF7622D7), Color(0xFFBC6CEF))
    }
    private fun icon(type: ModalidadeEnum): Int = when (type) {
        ModalidadeEnum.MEGA_SENA -> R.drawable.trevo_megasena_fundo_branco
        ModalidadeEnum.MAIS_MILIONARIA -> R.drawable.trevo_mais_milionaria
        ModalidadeEnum.DIA_DE_SORTE -> R.drawable.trevo_dia_de_sorte_fundo_branco
        ModalidadeEnum.LOTECA -> R.drawable.trevo_loteca_fundo_escuro
        ModalidadeEnum.DUPLA_SENA -> R.drawable.trevo_dupla_sena_fundo_escuro
        ModalidadeEnum.LOTOMANIA -> R.drawable.trevo_lotomania
        ModalidadeEnum.QUINA -> R.drawable.trevo_quina_fundo_branco
        ModalidadeEnum.TIMEMANIA -> R.drawable.trevo_timemania_fundo_escuro
        ModalidadeEnum.SUPER_7 -> R.drawable.trevo_super_sete_fundo_branco
        else -> R.drawable.trevo_lotofacil_fundo_branco
    }
}

private fun kotlinx.coroutines.CoroutineScope.launchScroll(state: ScrollState, target: Int) {
    launch { state.animateScrollTo(target.coerceAtMost(state.maxValue)) }
}
