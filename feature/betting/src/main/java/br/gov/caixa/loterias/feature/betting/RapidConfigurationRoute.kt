package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.caixaDSMoneyInputAmount
import br.gov.caixa.loterias.core.designsystem.CaixaDSRapidAmountDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRapidEditIcon
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.caixaDSLegacyLotteryClover
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRapidIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDivider
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyNumberGrid
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySectionHeader
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyWheel
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import java.math.BigDecimal
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun RapidConfigurationRoute(
    viewModel: RapidConfigurationViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pager = rememberPagerState(initialPage = state.page, pageCount = { RAPID_PAGE_COUNT })
    var summaryVisited by remember { mutableStateOf(false) }
    LaunchedEffect(state.page) { if (state.page == RAPID_PAGE_COUNT - 1) summaryVisited = true }
    RapidPagerSynchronization(pager, state.page, viewModel::setPage)
    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    CaixaDSLegacyScaffold(
        title = "Rapidão",
        onBack = onBack,
        topBarActionWidth = 80.dp,
        topBarAction = {
            if (summaryVisited) TextButton(onClick = viewModel::save, enabled = !state.loading && state.canSave) {
                Text("Salvar", color = CaixaDSColorTokens.White, fontSize = 16.sp, fontFamily = CaixaDSFontFamily)
            }
        },
        bottomBar = { if (!state.loading) RapidPageIndicator(state.page, viewModel::setPage) },
    ) { padding ->
        if (state.loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.surface)) {
                HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { page ->
                    when (page) {
                        0 -> RapidWelcomePage()
                        1 -> RapidPrizePage(state.minimumPrize, viewModel::setMinimumPrize)
                        2 -> RapidAmountPage(state, viewModel::setMinimumAmount, viewModel::setMaximumAmount, viewModel::setCustomAmount, { viewModel.loadAmountLimits() })
                        3 -> RapidModalitiesPage(state.availableModalities, state.selectedModalityIds, viewModel::toggleModality)
                        4 -> RapidNumbersPage(
                            title = "Números obrigatórios",
                            description = "Selecione os números que deverão aparecer em suas apostas.",
                            selected = state.requiredNumbers,
                            onToggle = viewModel::toggleRequiredNumber,
                            onClear = viewModel::clearRequiredNumbers,
                        )
                        5 -> RapidNumbersPage(
                            title = "Números proibidos",
                            description = "Selecione os números que não deverão aparecer em suas apostas.",
                            selected = state.excludedNumbers,
                            onToggle = viewModel::toggleExcludedNumber,
                            onClear = viewModel::clearExcludedNumbers,
                            danger = true,
                        )
                        else -> RapidSummaryPage(state, viewModel::setPage)
                    }
                }
            }
        }
    }
    state.warningMessage?.let {
        CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissMessage)
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissMessage)
    }
}

@Composable
internal fun RapidPageIndicator(page: Int, onPage: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(40.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(RAPID_PAGE_COUNT) { index ->
            Box(
                Modifier.width(15.dp).fillMaxHeight()
                    .semantics { contentDescription = "Etapa ${index + 1} de $RAPID_PAGE_COUNT"; selected = index == page }
                    .clickable { onPage(index) },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.size(5.dp)
                        .background(if (index == page) CaixaDS.colors.legacyTeal else CaixaDS.colors.surface, CircleShape)
                        .border(1.dp, CaixaDS.colors.legacyTeal, CircleShape),
                )
            }
        }
    }
}

/** Observe only completed swipes; intermediate animation pages must never replace a requested dot target. */
@Composable
internal fun RapidPagerSynchronization(pager: PagerState, requestedPage: Int, onSettled: (Int) -> Unit) {
    var navigating by remember(pager) { mutableStateOf(false) }
    val latestOnSettled by rememberUpdatedState(onSettled)
    LaunchedEffect(pager, requestedPage) {
        if (pager.settledPage != requestedPage || pager.currentPageOffsetFraction != 0f) {
            navigating = true
            try {
                pager.animateScrollToPage(requestedPage)
            } finally {
                navigating = false
            }
        }
    }
    LaunchedEffect(pager) {
        snapshotFlow { pager.isScrollInProgress to pager.settledPage }
            .distinctUntilChanged().collect { (scrolling, page) ->
                if (!scrolling && !navigating) latestOnSettled(page)
            }
    }
}

@Composable
private fun RapidWelcomePage() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        // The legacy guideline fixes the divider at 45% of the pager height.
        val dividerTop = maxHeight * 0.45f
        Icon(CaixaDSLegacyRapidIcon, contentDescription = null, tint = Color.Unspecified,
            modifier = Modifier.align(Alignment.TopCenter).offset(y = dividerTop - 110.dp).size(100.dp))
        Column(Modifier.fillMaxWidth().offset(y = dividerTop), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.width(150.dp).height(1.dp).background(CaixaDS.colors.legacyTeal))
            Text("Bem vindo ao", Modifier.padding(top = 5.dp), color = CaixaDS.colors.legacyTeal, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily)
            Text("Rapidão", Modifier.padding(top = 2.dp), color = CaixaDSColorTokens.LegacyBurntYellow, fontSize = 25.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily)
            Text("Selecione os limites da sua compra e suas modalidades favoritas.",
                Modifier.padding(top = 15.dp, start = 30.dp, end = 30.dp),
                color = CaixaDS.colors.legacyText, fontSize = 15.sp, fontFamily = CaixaDSFontFamily, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun RapidPrizePage(value: BigDecimal, onSelect: (BigDecimal) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Prêmio mínimo", Modifier.padding(top = 20.dp), color = CaixaDSColorTokens.LegacyFavoriteCartCard, fontSize = 25.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily)
        val description = buildAnnotatedString {
            append("O ")
            withStyle(SpanStyle(color = CaixaDSColorTokens.LegacyBurntYellow, fontWeight = FontWeight.Bold)) { append("Rapidão") }
            append(" irá escolher apenas concursos com premiação ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("maior") }
            append(" ou ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("igual") }
            append(" ao valor selecionado.")
        }
        Text(description, Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp), fontSize = 15.sp, textAlign = TextAlign.Center, fontFamily = CaixaDSFontFamily, color = CaixaDS.colors.legacyGraphite)
        Box(Modifier.padding(top = 16.dp).width(150.dp).height(1.dp).background(CaixaDSColorTokens.LegacyRegistrationSegment))
        RapidPrizeWheel(value, onSelect)
        Box(Modifier.padding(top = 10.dp).width(150.dp).height(1.dp).background(CaixaDSColorTokens.LegacyRegistrationSegment))
        Text("Nossas sugestões:", Modifier.padding(top = 20.dp), color = CaixaDS.colors.legacyGraphite, fontSize = 15.sp, fontFamily = CaixaDSFontFamily)
        FlowRow(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Center) {
            listOf(0, 20, 40, 60, 80, 100).forEach { millions ->
                val amount = BigDecimal.valueOf(millions * 1_000_000L)
                RapidPreset(if (millions == 0) "Qualquer valor" else "$millions Mi", value.compareTo(amount) == 0, true, false) { onSelect(amount) }
            }
        }
    }
}

@Composable
private fun RapidPrizeWheel(value: BigDecimal, onSelect: (BigDecimal) -> Unit) {
    val selected = value.divideToIntegralValue(BigDecimal("1000000")).toInt().coerceIn(0, 100)
    CaixaDSLegacyWheel(
        options = (0..100).map { if (it == 0) "Qualquer valor" else "R$ $it.000.000" },
        selectedIndex = selected,
        onSelectedIndex = { onSelect(BigDecimal.valueOf(it * 1_000_000L)) },
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp).height(180.dp),
        itemHeight = 40.dp,
        fontSize = 18.sp,
        wrap = true,
    )
}

@Composable
private fun RapidAmountPage(
    state: RapidConfigurationUiState,
    onMinimum: (BigDecimal) -> Unit,
    onMaximum: (BigDecimal) -> Unit,
    onCustom: (BigDecimal?, Boolean) -> Boolean,
    onRetry: () -> Unit,
) {
    var editingMinimum by remember { mutableStateOf<Boolean?>(null) }
    var input by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    val floor = state.minimumAllowedAmount
    val ceiling = state.maximumAllowedAmount
    val available = floor != null && ceiling != null && !state.amountLimitsLoading && state.amountLimitsError == null
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        RapidAmountControl("Valor máximo da aposta", "Escolha  o valor máximo em reais da sua aposta", state.maximumAmount,
            if (available) listOf(state.minimumAmount * BigDecimal("3"), state.minimumAmount * BigDecimal("4"), ceiling!!).filter { it >= floor!! && it <= ceiling } else emptyList(),
            onMaximum, { editingMinimum = false; input = ""; inputError = null }, available)
        Box(Modifier.padding(top = 20.dp).width(150.dp).height(1.dp).background(CaixaDS.colors.legacyLine))
        RapidAmountControl("Valor mínimo da aposta", "Escolha  o valor mínimo em reais da sua aposta", state.minimumAmount,
            if (available) listOf(state.minimumAmount, state.minimumAmount * BigDecimal("2"), state.minimumAmount * BigDecimal("3")).filter { it >= floor!! && it <= ceiling!! } else emptyList(),
            onMinimum, { editingMinimum = true; input = ""; inputError = null }, available)
        if (state.amountLimitsLoading) CircularProgressIndicator(Modifier.padding(20.dp), color = CaixaDS.colors.legacyTeal)
        if (state.amountLimitsError != null || !available && !state.amountLimitsLoading) {
            Text(state.amountLimitsError ?: "Não foi possível carregar os limites da aposta. Tente novamente.", Modifier.padding(16.dp), color = CaixaDS.colors.legacyGraphite, fontFamily = CaixaDSFontFamily, textAlign = TextAlign.Center)
            TextButton(onClick = onRetry) { Text("Tentar novamente") }
        }
    }
    editingMinimum?.let { minimum ->
        CaixaDSRapidAmountDialog(
            title = if (minimum) "Valor mínimo da aposta" else "Valor máximo da aposta",
            value = input, hint = if (minimum) "Digite o valor mínimo" else "Digite o valor máximo",
            limits = if (minimum) "Mínimo de ${formatBetAmount(floor!!)} e máximo de ${formatBetAmount(ceiling!!)}." else "O limite é de ${formatBetAmount(ceiling!!)}.",
            errorMessage = inputError,
            onValueChange = { input = it; inputError = null },
            onSave = {
                val amount = input.takeIf(String::isNotBlank)?.let(::caixaDSMoneyInputAmount)
                if (onCustom(amount, minimum)) editingMinimum = null
                else inputError = "O valor selecionado tem que estar entre ${formatBetAmount(floor!!)} e ${formatBetAmount(ceiling!!)}"
            },
            onDismiss = { editingMinimum = null },
        )
    }
}

@Composable
private fun RapidAmountControl(label: String, description: String, value: BigDecimal, presets: List<BigDecimal>, onSelect: (BigDecimal) -> Unit, onCustom: () -> Unit, enabled: Boolean) {
    Column(Modifier.fillMaxWidth().padding(top = if (label.contains("mínimo")) 20.dp else 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = CaixaDSColorTokens.LegacyFavoriteCartCard, fontSize = 20.sp, fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold)
        Text(buildAnnotatedString {
            val word = if (label.contains("mínimo")) "mínimo" else "máximo"
            val start = description.indexOf(word)
            append(description.substring(0, start))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(word) }
            append(description.substring(start + word.length))
        }, Modifier.padding(start = 50.dp, end = 50.dp, top = 10.dp), fontSize = if (label.contains("mínimo")) 16.sp else 15.sp, fontFamily = CaixaDSFontFamily, color = CaixaDS.colors.legacyGraphite, textAlign = TextAlign.Center)
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.Center) {
            presets.forEach { amount ->
                RapidAmountPreset(amount.toInt().toString(), value.compareTo(amount) == 0, true) { onSelect(amount) }
            }
            RapidAmountPreset("...", enabled && presets.none { it.compareTo(value) == 0 }, enabled, onCustom)
        }
        Text("Valor selecionado", Modifier.padding(top = 10.dp), fontSize = 15.sp, fontFamily = CaixaDSFontFamily, color = CaixaDS.colors.legacyGraphite)
        Text(formatBetAmount(value), Modifier.padding(top = 10.dp), color = CaixaDSColorTokens.LegacyBurntYellow, fontSize = 15.sp, fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RapidAmountPreset(label: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    RapidPreset(label, selected, enabled, true, onClick)
}

@Composable
private fun RapidPreset(label: String, selected: Boolean, enabled: Boolean, circular: Boolean, onClick: () -> Unit) {
    val outline = if (circular) CaixaDSColorTokens.LegacyRapidWheel else CaixaDSColorTokens.LegacyMegaSenaDark
    val shape = if (circular) CircleShape else RoundedCornerShape(if (selected) 10.dp else 4.dp)
    Box(
        Modifier.padding(if (circular) 10.dp else 5.dp)
            .then(if (circular) Modifier.size(60.dp) else Modifier.height(48.dp))
            .background(if (selected) CaixaDSColorTokens.LegacyRapidWheel else CaixaDSColorTokens.White, shape)
            .border(if (selected) 0.dp else 1.dp, outline, shape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            Modifier.padding(horizontal = if (circular) 0.dp else 10.dp),
            color = if (selected) CaixaDSColorTokens.White else CaixaDSColorTokens.LegacyRapidWheel,
            fontFamily = if (circular) CaixaDSFontFamily else br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily,
            fontWeight = if (circular) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (circular) 14.sp else 17.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RapidModalitiesPage(modalities: List<LotteryModality>, selected: Set<Int>, onToggle: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        RapidSectionHeader("Modalidades", "Selecione as opções que você quer contemplar no Rapidão.", titleTop = 40.dp, descriptionTop = 20.dp, descriptionPadding = 20.dp)
        Spacer(Modifier.height(19.dp))
        modalities.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                row.forEach { modality ->
                    val checked = modality.id in selected
                    val style = caixaDSLotteryStyle(modality.id, modality.name)
                    Row(
                        Modifier.weight(1f).padding(4.dp).height(56.dp)
                            .background(if (checked) style.light else CaixaDS.colors.surface)
                            .then(if (checked) Modifier else Modifier.border(1.dp, CaixaDS.colors.legacyGraphite, RoundedCornerShape(4.dp)))
                            .clickable { onToggle(modality.id) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (modality.id == 14) {
                            Image(painterResource(CaixaDSR.drawable.legacy_trevo_lotogol), null, Modifier.padding(start = 12.dp).size(18.dp))
                        } else {
                            Image(caixaDSLegacyLotteryClover(modality.id), null, Modifier.padding(start = 12.dp).size(18.dp))
                        }
                        Text(modality.name.lowercase(), Modifier.padding(start = 8.dp), color = if (checked) style.content else CaixaDS.colors.legacyMutedText, fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RapidNumbersPage(
    title: String,
    description: String,
    selected: Set<Int>,
    onToggle: (Int) -> Unit,
    onClear: () -> Unit,
    danger: Boolean = false,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        RapidSectionHeader(title, description, titleTop = 40.dp, descriptionTop = 0.dp, descriptionPadding = 20.dp)
        if (selected.isNotEmpty()) {
            TextButton(onClick = onClear, modifier = Modifier.align(Alignment.CenterHorizontally).width(184.dp).height(56.dp).border(1.dp, CaixaDS.colors.legacyGraphite, RoundedCornerShape(4.dp))) {
                Text("Limpar seleção", color = CaixaDS.colors.legacyGraphite, fontSize = 14.2.sp, fontFamily = CaixaDSFontFamily)
            }
        }
        RapidNumberCells((1..100).toList(), selected, danger, onToggle, Modifier.padding(horizontal = 8.dp, vertical = 19.dp))
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun RapidNumberCells(numbers: List<Int>, selected: Set<Int>, danger: Boolean, onToggle: ((Int) -> Unit)?, modifier: Modifier = Modifier) {
    val color = if (danger) CaixaDSColorTokens.LegacyRapidExcluded else CaixaDS.colors.legacyTeal
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columns = (maxWidth.value / 56).toInt().coerceAtLeast(1)
        val cellWidth = maxWidth / columns
        FlowRow(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(17.dp)) {
            numbers.forEach { number ->
                Box(Modifier.width(cellWidth), contentAlignment = Alignment.Center) {
                    val filled = number in selected
                    Box(
                        Modifier.size(40.dp).background(if (filled) color else CaixaDS.colors.surface, CircleShape)
                            .border(1.dp, color, CircleShape)
                            .then(if (onToggle != null) Modifier.clickable { onToggle(number) } else Modifier),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(number.toString().padStart(2, '0'), color = if (filled) CaixaDSColorTokens.White else color, fontSize = 14.1.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily)
                    }
                }
            }
        }
    }
}

@Composable
private fun RapidSectionHeader(
    title: String,
    description: String,
    titleTop: androidx.compose.ui.unit.Dp = 16.dp,
    descriptionTop: androidx.compose.ui.unit.Dp = 16.dp,
    descriptionPadding: androidx.compose.ui.unit.Dp = 0.dp,
    descriptionWidth: androidx.compose.ui.unit.Dp? = null,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, Modifier.padding(top = titleTop, start = 8.dp, end = 8.dp), color = CaixaDSColorTokens.LegacyExtendedToolbarLine, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily, textAlign = TextAlign.Center)
        Text(description, Modifier.padding(top = descriptionTop)
            .then(if (descriptionWidth != null) Modifier.width(descriptionWidth) else Modifier.fillMaxWidth())
            .padding(descriptionPadding), color = CaixaDS.colors.legacyGraphite, fontSize = 14.sp, fontFamily = CaixaDSFontFamily, textAlign = TextAlign.Center)
    }
}

@Composable
private fun RapidFavoritePage(state: RapidConfigurationUiState, onToggle: (Long) -> Unit, onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        RapidSectionHeader("Apostas favoritas", "Selecione as opções que você quer contemplar no Rapidão.", descriptionWidth = 200.dp)
        Spacer(Modifier.height(8.dp))
        when {
            state.favoritesLoading -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CaixaDS.colors.legacyTeal) }
            state.favoritesError != null -> {
                Text(state.favoritesError, Modifier.padding(16.dp), color = CaixaDS.colors.legacyGraphite, textAlign = TextAlign.Center)
                TextButton(onClick = onRetry, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Tentar novamente") }
            }
            state.availableFavorites.isEmpty() -> Text("Você ainda não possui apostas favoritas. É muito fácil! Simule uma aposta e adicione como favorita.", Modifier.padding(16.dp), color = CaixaDS.colors.brand, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily, textAlign = TextAlign.Center)
            else -> state.availableFavorites.forEach { bet ->
                Row(Modifier.fillMaxWidth().clickable { onToggle(bet.id) }.padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(bet.id in state.favoriteBetIds, onCheckedChange = { onToggle(bet.id) }, colors = CheckboxDefaults.colors(checkedColor = CaixaDS.colors.legacyTeal))
                    Text(bet.modalityName, Modifier.padding(start = 8.dp).width(90.dp), color = modalityColorForRapid(bet.modalityId), fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.2.sp)
                    Text(rapidFavoriteNumbers(bet), Modifier.weight(1f).padding(start = 8.dp), color = CaixaDSColorTokens.LegacyRapidFavoriteText, fontFamily = CaixaDSFontFamily, fontSize = 14.2.sp)
                }
            }
        }
    }
}

private fun rapidFavoriteNumbers(bet: br.gov.caixa.loterias.core.domain.betting.FavoriteBet): String = buildString {
    append(bet.numberColumns.joinToString(if (bet.isSuperSeven) "   " else " - ") { column -> column.joinToString(" - ") { it.toString().padStart(2, '0') } })
    if (bet.heartTeam.isNotBlank()) append("\nTime do coração: ${bet.heartTeam}")
    if (bet.luckyMonth.isNotBlank()) append("\nMês de sorte: ${bet.luckyMonth}")
    if (bet.clovers.isNotEmpty()) append("\nTrevos: ${bet.clovers.joinToString(" - ")}")
}

@Composable
private fun RapidSummaryPage(state: RapidConfigurationUiState, onEdit: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        RapidSectionHeader("Resumo", "Verifique e confirme suas opções de Rapidão.", descriptionTop = 8.dp)
        RapidSummaryCard("Prêmio mínimo", if (state.minimumPrize.signum() == 0) "Qualquer valor" else formatBetAmount(state.minimumPrize)) { onEdit(1) }
        RapidSummaryCard("Valor máximo da aposta", formatBetAmount(state.maximumAmount)) { onEdit(2) }
        RapidSummaryCard("Valor mínimo da aposta", formatBetAmount(state.minimumAmount)) { onEdit(2) }
        RapidSummaryCard("Modalidades", state.availableModalities.filter { it.id in state.selectedModalityIds }.joinToString { it.name }) { onEdit(3) }
        RapidSummaryNumbers("Números obrigatórios", state.requiredNumbers, false, { onEdit(4) })
        RapidSummaryNumbers("Números proibidos", state.excludedNumbers, true, { onEdit(5) })
    }
}

@Composable
private fun RapidSummaryCard(label: String, value: String, onEdit: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(15.dp).height(100.dp),
        shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = CaixaDSColorTokens.LegacyRapidSummaryBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center).padding(horizontal = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(label, color = CaixaDS.colors.legacyGraphite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily, textAlign = TextAlign.Center)
                Text(value, color = CaixaDSColorTokens.LegacyFavoriteCartCard, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily, textAlign = TextAlign.Center)
            }
            IconButton(onClick = onEdit, modifier = Modifier.align(Alignment.TopEnd).size(40.dp)) { Icon(CaixaDSLegacyRapidEditIcon, "Editar $label", tint = Color.Unspecified, modifier = Modifier.size(24.dp)) }
        }
    }
}

@Composable
private fun RapidSummaryNumbers(label: String, numbers: Set<Int>, danger: Boolean, onEdit: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 24.dp)) {
        Row(Modifier.padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = CaixaDS.colors.legacyGraphite, fontWeight = FontWeight.Bold, fontSize = 14.2.sp, fontFamily = CaixaDSFontFamily)
            IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) { Icon(CaixaDSLegacyRapidEditIcon, "Editar $label", tint = Color.Unspecified) }
        }
        RapidNumberCells(numbers.sorted(), numbers, danger, null, Modifier.padding(horizontal = 10.dp, vertical = 19.dp))
        CaixaDSLegacyDivider()
    }
}

@Composable
private fun modalityColorForRapid(id: Int) = when (id) {
    2 -> CaixaDSColorTokens.MegaSena
    3 -> CaixaDSColorTokens.Quina
    7 -> CaixaDSColorTokens.SuperSete
    8 -> CaixaDSColorTokens.Lotofacil
    9 -> CaixaDSColorTokens.MaisMilionaria
    11 -> CaixaDSColorTokens.DiaDeSorte
    16 -> CaixaDSColorTokens.Lotomania
    18 -> CaixaDSColorTokens.DuplaSena
    20 -> CaixaDSColorTokens.Timemania
    else -> CaixaDS.colors.brand
}

private const val RAPID_PAGE_COUNT = 7
