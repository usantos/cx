package br.gov.caixa.loterias.feature.results

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import kotlin.math.roundToInt
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCustomBack
import br.gov.caixa.loterias.core.designsystem.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.caixaDSLegacyLotteryClover
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.results.DrawResult

data class ResultModality(val code: String, val name: String, val color: Color)

val officialModalities = listOf(
    ResultModality("megasena", "Mega-Sena", CaixaDSColorTokens.LegacyMegaSenaLight),
    ResultModality("lotofacil", "Lotofácil", CaixaDSColorTokens.LegacyLotofacilLight),
    ResultModality("quina", "Quina", CaixaDSColorTokens.LegacyQuinaLight),
    ResultModality("lotomania", "Lotomania", CaixaDSColorTokens.LegacyLotomaniaLight),
    ResultModality("timemania", "Timemania", CaixaDSColorTokens.LegacyTimemaniaLight),
    ResultModality("duplasena", "Dupla Sena", CaixaDSColorTokens.LegacyDuplaSenaLight),
    ResultModality("diadesorte", "Dia de Sorte", CaixaDSColorTokens.LegacyDiaDeSorteLight),
    ResultModality("supersete", "Super Sete", CaixaDSColorTokens.LegacySuperSeteLight),
    ResultModality("maismilionaria", "+Milionária", CaixaDSColorTokens.LegacyMaisMilionariaLight),
    ResultModality("loteca", "Loteca", CaixaDSColorTokens.Loteca),
    ResultModality("federal", "Federal", CaixaDSColorTokens.Blue900),
)

internal fun modalityColor(code: String?): Color =
    officialModalities.firstOrNull { it.code == code }?.color ?: CaixaDSColorTokens.MegaSena

internal fun modalityName(code: String?): String =
    officialModalities.firstOrNull { it.code == code }?.name ?: "Mega-Sena"

@Composable
fun ResultsRoute(viewModel: ResultsViewModel, onBack: () -> Unit, showDetail: Boolean = false) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(showDetail) {
        if (showDetail && state.modality == null) viewModel.selectModality("megasena")
    }
    val backToModalities = !showDetail && state.modality != null
    val navigateBack: () -> Unit = {
        if (backToModalities) viewModel.clearSelection() else onBack()
    }
    BackHandler(enabled = backToModalities, onBack = navigateBack)
    ResultsScreen(
        state = state,
        onBack = navigateBack,
        onSelectModality = viewModel::selectModality,
        onContestChange = viewModel::changeContest,
        onSearch = viewModel::searchContest,
        onRetry = viewModel::retry,
        onPreviousContest = viewModel::previousContest,
        onNextContest = viewModel::nextContest,
        onDismissInputError = viewModel::dismissInputError,
        showDetail = showDetail || state.modality != null,
    )
}

@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onBack: () -> Unit,
    onSelectModality: (String) -> Unit,
    onContestChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onPreviousContest: () -> Unit,
    onNextContest: () -> Unit,
    onDismissInputError: () -> Unit = {},
    showDetail: Boolean = false,
) {
    if (showDetail) {
        ResultDetailScreen(
            state = state,
            onBack = onBack,
            onContestChange = onContestChange,
            onSearch = onSearch,
            onRetry = onRetry,
            onPreviousContest = onPreviousContest,
            onNextContest = onNextContest,
            onDismissInputError = onDismissInputError,
        )
    } else {
        ResultModalityScreen(onBack, onSelectModality)
    }
}

private val resultModalityCodes = listOf(
    "diadesorte", "duplasena", "loteca", "lotofacil", "lotomania",
    "maismilionaria", "megasena", "quina", "supersete", "timemania",
)

@Composable
private fun ResultModalityScreen(onBack: () -> Unit, onSelectModality: (String) -> Unit) {
    CaixaDSLegacyScaffold(
        title = "Resultados",
        onBack = onBack,
        gradientTopBar = false,
        topBarNavigationWidth = 63.dp,
        topBarTitleSize = 20.sp,
        topBarTitleWeight = FontWeight.SemiBold,
        topBarNavigationContent = {
            Box(Modifier.padding(start = 16.dp).size(47.dp, 48.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Icon(CaixaDSLegacyCustomBack, contentDescription = "Voltar", tint = Color.Unspecified)
            }
        },
        containerColor = CaixaDSColorTokens.White,
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 10.dp, top = 40.dp, end = 10.dp, bottom = 50.dp,
            ),
        ) {
            item {
                Text(
                    "Modalidades", Modifier.fillMaxWidth(),
                    color = CaixaDS.colors.legacyPrimaryButton,
                    style = TextStyle(
                        fontFamily = CaixaDSFontFamily, fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                    ),
                )
                Text(
                    "Selecione a modalidade para\n ver os seus resultados",
                    Modifier.fillMaxWidth().padding(top = 20.dp),
                    color = CaixaDS.colors.legacyGraphite,
                    style = TextStyle(
                        fontFamily = CaixaDSFontFamily, fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                    ),
                )
                Spacer(Modifier.height(19.dp))
            }
            resultModalityCodes.map { code -> officialModalities.first { it.code == code } }
                .chunked(2).forEach { row ->
                    item {
                        Row(Modifier.fillMaxWidth()) {
                            row.forEach { modality ->
                                ResultModalityCard(modality, Modifier.weight(1f)) {
                                    onSelectModality(modality.code)
                                }
                            }
                        }
                    }
                }
        }
    }
}

@Composable
private fun ResultModalityCard(modality: ResultModality, modifier: Modifier, onClick: () -> Unit) {
    val style = caixaDSLotteryStyle(0, modality.name)
    Row(
        modifier.padding(4.dp).height(56.dp).background(style.light).clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            caixaDSLegacyLotteryClover(modality.code), contentDescription = null,
            modifier = Modifier.padding(start = 12.dp).size(18.dp),
        )
        Text(
            modality.name.lowercase(), Modifier.padding(start = 8.dp), color = style.content,
            style = TextStyle(
                fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                platformStyle = PlatformTextStyle(includeFontPadding = true),
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun ResultDetailScreen(
    state: ResultsUiState,
    onBack: () -> Unit,
    onContestChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onPreviousContest: () -> Unit,
    onNextContest: () -> Unit,
    onDismissInputError: () -> Unit,
) {
    val lotteryStyle = caixaDSLotteryStyle(0, modalityName(state.modality))
    val color = lotteryStyle.dark
    val currentContest = state.result?.contest
    val previousEnabled = !state.isLoading && currentContest != null && currentContest > 1
    val nextEnabled = !state.isLoading && currentContest != null && state.latestContest != null &&
        currentContest < state.latestContest
    var searchVisible by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(searchVisible) {
        if (searchVisible) searchFocusRequester.requestFocus() else keyboard?.hide()
    }
    LaunchedEffect(searchVisible, state.isLoading) {
        if (searchVisible && state.isLoading) searchVisible = false
    }
    state.inputError?.let { message ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = message,
            positiveLabel = "Entendi",
            onPositive = onDismissInputError,
            onDismiss = onDismissInputError,
        )
    }
    CaixaDSLegacyScaffold(
        title = if (searchVisible) "" else currentContest?.let { "Concurso nº $it" } ?: "Resultados",
        onBack = onBack,
        topBarTitleWeight = FontWeight.Normal,
        topBarTitleSize = 16.sp,
        topBarAction = {
            Icon(
                if (searchVisible) CaixaDSIcons.Close else CaixaDSIcons.Search,
                contentDescription = if (searchVisible) "Fechar pesquisa" else "Pesquisar concurso",
                modifier = Modifier.padding(end = 16.dp).size(24.dp).clickable {
                    searchVisible = !searchVisible
                    if (searchVisible) onContestChange("")
                },
                tint = CaixaDSColorTokens.White,
            )
        },
        topBarTitleContent = if (searchVisible) {
            {
                BasicTextField(
                    value = state.contestInput,
                    onValueChange = onContestChange,
                    modifier = Modifier.fillMaxWidth().height(36.dp).focusRequester(searchFocusRequester)
                        .background(CaixaDSColorTokens.White, RoundedCornerShape(4.dp)).padding(horizontal = 10.dp),
                    singleLine = true,
                    textStyle = CaixaDS.typography.bodyMedium.copy(color = CaixaDSColorTokens.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSearch() }),
                    decorationBox = { field ->
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            if (state.contestInput.isEmpty()) {
                                Text("Número do concurso", color = CaixaDSColorTokens.Neutral600, fontSize = 14.sp)
                            }
                            field()
                        }
                    },
                )
            }
        } else null,
        bottomBar = {
            Box(Modifier.fillMaxWidth().height(94.dp).background(CaixaDSColorTokens.LegacyBetFooter)) {
                Row(
                    Modifier.align(Alignment.CenterStart).padding(start = 5.dp)
                        .alpha(if (previousEnabled) 1f else .5f)
                        .clickable(enabled = previousEnabled, onClick = onPreviousContest),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(CaixaDSIcons.Back, contentDescription = null, modifier = Modifier.size(12.dp, 18.dp), tint = CaixaDSColorTokens.LegacyBetLink)
                    Text(
                        contestButtonLabel(currentContest?.minus(1), "anterior"),
                        Modifier.padding(start = 13.dp),
                        color = CaixaDSColorTokens.LegacyBetLink,
                        style = TextStyle(fontSize = 12.sp, fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    )
                }
                Row(
                    Modifier.align(Alignment.CenterEnd).padding(end = 5.dp)
                        .alpha(if (nextEnabled) 1f else .5f)
                        .clickable(enabled = nextEnabled, onClick = onNextContest),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        contestButtonLabel(currentContest?.plus(1), "seguinte"),
                        modifier = Modifier.padding(end = 13.dp),
                        color = CaixaDSColorTokens.LegacyBetLink,
                        style = TextStyle(fontSize = 12.sp, fontFamily = CaixaDSFontFamily, textAlign = TextAlign.End, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    )
                    Icon(CaixaDSIcons.Back, contentDescription = null, modifier = Modifier.size(12.dp, 18.dp).rotate(180f), tint = CaixaDSColorTokens.LegacyBetLink)
                }
                Box(
                    Modifier.align(Alignment.TopCenter).offset(y = (-41).dp).width(184.dp).height(84.dp)
                        .background(CaixaDSColorTokens.LegacyNotificationText).clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Voltar", color = CaixaDSColorTokens.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = color,
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).background(color)) {
            item {
                Row(
                    Modifier.fillMaxWidth().height(71.dp).background(lotteryStyle.light),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.width(90.dp).height(71.dp), contentAlignment = Alignment.CenterStart) {
                        Canvas(Modifier.fillMaxSize()) {
                            drawPath(Path().apply {
                                moveTo(0f, 0f); lineTo(size.width, 0f)
                                lineTo(size.width * .74f, size.height); lineTo(0f, size.height); close()
                            }, lotteryStyle.dark)
                        }
                        Image(caixaDSLegacyLotteryClover(state.modality ?: "megasena"), null, Modifier.padding(start = 24.dp).size(23.dp))
                    }
                    Text(
                        state.result?.let { drawResultTitle(it, modalityName(state.modality)) }?.lowercase() ?: modalityName(state.modality).lowercase(),
                        modifier = Modifier.padding(start = 8.dp),
                        color = lotteryStyle.content,
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold,
                            fontFamily = CaixaDSLegacyFuturaFontFamily,
                            platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    )
                }
            }
            when {
                state.isLoading -> item {
                    Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CaixaDSColorTokens.White)
                    }
                }
                state.errorMessage != null -> item {
                    Column(Modifier.fillMaxWidth().padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.errorMessage, color = CaixaDSColorTokens.White, textAlign = TextAlign.Center)
                        Text("Tentar novamente", Modifier.padding(top = 20.dp).clickable(onClick = onRetry), color = CaixaDSColorTokens.White, fontWeight = FontWeight.Bold)
                    }
                }
                state.result != null -> item { ResultContent(requireNotNull(state.result), state.modality ?: "megasena") }
                else -> item {
                    Text(
                        "Nenhum resultado disponível para esta modalidade.",
                        Modifier.fillMaxWidth().padding(30.dp),
                        color = CaixaDSColorTokens.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultContent(result: DrawResult, modality: String) {
    val style = caixaDSLotteryStyle(0, modalityName(modality))
    val gap16 = with(LocalDensity.current) { 16.toDp() }
    val gap18 = with(LocalDensity.current) { 18.toDp() }
    val bottom180 = with(LocalDensity.current) { 180.toDp() }
    val bodySize = with(LocalDensity.current) { 14.2.sp.toPx().roundToInt().toSp() }
    val bodyStyle = TextStyle(fontFamily = CaixaDSFontFamily, fontSize = bodySize,
        platformStyle = PlatformTextStyle(includeFontPadding = true))
    val payoutStyle = bodyStyle.copy(fontSize = 16.sp)
    Column(Modifier.fillMaxWidth().padding(top = 10.dp)) {
        if (result.lotecaMatches.isNotEmpty() || result.lotogolMatches.isNotEmpty()) {
            Column(Modifier.padding(horizontal = 30.dp)) { DrawSportsResults(result, style.darkContent) }
        } else {
            Text("Números sorteados:", Modifier.padding(start = 30.dp), color = style.darkContent, style = bodyStyle)
            if (modality == "supersete" && result.firstDrawNumberColumns.size > 1) {
                DrawNumberColumns(result.firstDrawNumberColumns, style.darkContent)
            } else NumberRows(
                if (modality == "supersete") result.firstDrawNumbers else result.firstDrawNumbers.sorted(),
                Modifier.fillMaxWidth().padding(top = 20.dp), style.light,
            )
        }
        if (result.secondDrawNumbers.isNotEmpty()) {
            Text("2º sorteio", Modifier.padding(top = 16.dp), color = style.darkContent, style = bodyStyle)
            NumberRows(result.secondDrawNumbers.sorted(), Modifier.fillMaxWidth().padding(top = 20.dp), style.light)
        }
        if (result.clovers.isNotEmpty()) {
            Text("Trevos sorteados: ${result.clovers.sorted().joinToString(" • ")}", Modifier.padding(start = 30.dp, top = 10.dp), color = style.darkContent, style = bodyStyle)
        }
        Text(
            buildAnnotatedString {
                append("Data do sorteio: ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(result.drawDate) }
            },
            Modifier.padding(start = 30.dp, top = 10.dp), color = style.darkContent, style = bodyStyle,
        )
        Box(Modifier.fillMaxWidth().padding(top = 10.dp).height(1.dp).background(style.light))
        Column(
            Modifier.fillMaxWidth().padding(start = 30.dp, top = 10.dp, end = 30.dp, bottom = 84.dp)
                .background(style.light).padding(horizontal = 16.dp).padding(bottom = 30.dp + bottom180),
        ) {
            Text(if (result.secondDrawPayouts.isNotEmpty()) "Premiação do 1º sorteio" else "Premiação", Modifier.padding(top = gap16), color = style.content, style = payoutStyle.copy(fontWeight = FontWeight.Bold))
            result.payouts.forEach { payout ->
                Text(resultPayoutDescription(modality, payout.description), Modifier.padding(top = gap16), color = style.content, style = payoutStyle.copy(fontWeight = FontWeight.Bold))
                Text(resultWinners(payout.winners), color = style.content, style = payoutStyle)
                if (payout.winners > 0) Text("Prêmio: ${resultMoney(payout.value)}", color = style.content, style = payoutStyle)
            }
            DrawResultSupplement(result, style.content)
            Text("Arrecadação total", Modifier.padding(top = gap18), color = style.content, style = payoutStyle.copy(fontWeight = FontWeight.Bold))
            Text(resultMoney(result.totalRevenue.ifBlank { "0" }), Modifier.padding(top = gap16), color = style.content, style = payoutStyle.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun NumberRows(numbers: List<Int>, modifier: Modifier = Modifier, color: Color) {
    val columns = if (numbers.size > 6) 4 else 3
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        numbers.chunked(columns).forEach { row ->
            Row(Modifier.width((columns * 56).dp).padding(bottom = 10.dp)) {
                row.forEach { number ->
                    Box(Modifier.width(56.dp).height(40.dp), contentAlignment = Alignment.Center) {
                        Box(Modifier.size(40.dp).background(CaixaDSColorTokens.White, CircleShape), contentAlignment = Alignment.Center) {
                            Text(number.toString().padStart(2, '0'), color = color,
                                style = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold,
                                    fontSize = 14.1.sp, platformStyle = PlatformTextStyle(includeFontPadding = true)))
                        }
                    }
                }
            }
        }
    }
}

private fun contestButtonLabel(contest: Int?, fallback: String) = buildAnnotatedString {
    append("Concurso\n")
    if (contest == null) append(fallback) else {
        append("nº ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(contest.toString()) }
    }
}
