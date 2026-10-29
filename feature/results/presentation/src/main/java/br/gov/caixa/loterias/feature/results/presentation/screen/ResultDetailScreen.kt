package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.presentation.components.ResultContestSearchAction
import br.gov.caixa.loterias.feature.results.presentation.components.ResultContestSearchField
import br.gov.caixa.loterias.feature.results.presentation.components.drawResultTitle
import br.gov.caixa.loterias.feature.results.presentation.mapper.contestButtonLabel
import br.gov.caixa.loterias.feature.results.presentation.mapper.modalityName
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState

@Composable
internal fun ResultDetailScreen(
    state: ResultsUiState,
    onBack: () -> Unit,
    onContestChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onPreviousContest: () -> Unit,
    onNextContest: () -> Unit,
    onDismissInputError: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
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
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = onDismissInputError,
            onDismiss = onDismissInputError
        )
    }
    CaixaDSScaffold(
        title = if (searchVisible) {
            ""
        } else {
            currentContest?.let { "Concurso nº $it" } ?: uiStrings.getString(
                LocalizedR.string.ui_resultados_310561
            )
        },
        onBack = onBack,
        topBarAction = {
            ResultContestSearchAction(searchVisible) {
                searchVisible = !searchVisible
                if (searchVisible) onContestChange("")
            }
        },
        topBarTitleContent = if (searchVisible) {
            {
                ResultContestSearchField(state.contestInput, searchFocusRequester, onContestChange, onSearch)
            }
        } else {
            null
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().height(94.dp).background(CaixaDSColorTokens.BetFooter)) {
                Row(
                    Modifier.align(Alignment.CenterStart).padding(start = 5.dp)
                        .alpha(if (previousEnabled) 1f else .5f)
                        .uiId("results.0081").clickable(enabled = previousEnabled, onClick = onPreviousContest),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        CaixaDSIcons.Back,
                        contentDescription = null,
                        modifier = (Modifier.size(12.dp, 18.dp)).uiId("results.0082"),
                        tint = CaixaDSColorTokens.BetLink
                    )
                    Text(
                        contestButtonLabel(currentContest?.minus(1), "anterior"),
                        (Modifier.padding(start = 13.dp)).uiId("results.0083"),
                        color = CaixaDSColorTokens.BetLink,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = CaixaDSFontFamily,
                            platformStyle = PlatformTextStyle(includeFontPadding = true)
                        )
                    )
                }
                Row(
                    Modifier.align(Alignment.CenterEnd).padding(end = 5.dp)
                        .alpha(if (nextEnabled) 1f else .5f)
                        .uiId("results.0084").clickable(enabled = nextEnabled, onClick = onNextContest),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        contestButtonLabel(currentContest?.plus(1), "seguinte"),
                        modifier = (Modifier.padding(end = 13.dp)).uiId("results.0085"),
                        color = CaixaDSColorTokens.BetLink,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = CaixaDSFontFamily,
                            textAlign = TextAlign.End,
                            platformStyle = PlatformTextStyle(includeFontPadding = true)
                        )
                    )
                    Icon(
                        CaixaDSIcons.Back,
                        contentDescription = null,
                        modifier = (Modifier.size(12.dp, 18.dp).rotate(180f))
                            .uiId("results.0086"),
                        tint = CaixaDSColorTokens.BetLink
                    )
                }
                Box(
                    Modifier.align(Alignment.TopCenter).offset(y = (-41).dp).width(184.dp).height(84.dp)
                        .background(CaixaDSColorTokens.NotificationText).uiId("results.0087").clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_voltar_59dc92),
                        color = CaixaDSColorTokens.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.uiId("results.0088")
                    )
                }
            }
        },
        containerColor = color,
        modifier = Modifier.uiId("results.0089")
    ) { padding ->
        LazyColumn((Modifier.fillMaxSize().padding(padding).background(color)).uiId("results.0090")) {
            item {
                Box(Modifier.fillMaxWidth().height(71.dp).background(lotteryStyle.light)) {
                    val specialHeader = state.result?.takeIf { it.specialContest }?.let { result ->
                        when (state.modality) {
                            "megasena" -> R.drawable.legacy_result_mega_virada_dupla

                            "quina" -> R.drawable.legacy_result_quina_sao_joao_dupla

                            "lotofacil" -> R.drawable.legacy_result_lotofacil_indepedencia_dupla

                            "duplasena" -> R.drawable.legacy_result_dupla_pascoa_dupla

                            "loteca" -> if (result.specialName.contains("país", true) || result.specialName.contains("pais", true)) {
                                R.drawable.legacy_result_loteca_pais_dupla
                            } else {
                                R.drawable.legacy_result_loteca_copa_dupla
                            }

                            else -> null
                        }
                    }
                    specialHeader?.let { resource ->
                        Image(
                            painterResource(resource),
                            state.result?.let { drawResultTitle(it, modalityName(state.modality)) },
                            Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    Row(
                        Modifier.fillMaxWidth().height(71.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.width(90.dp).height(71.dp), contentAlignment = Alignment.CenterStart) {
                            val trapezoid = caixaDSCampaignDrawable(0, modalityName(state.modality), CaixaDSLotteryArtwork.TRAPEZE)
                            if (trapezoid != null) {
                                Image(caixaDSDrawablePainter(trapezoid), null, Modifier.fillMaxSize())
                            } else {
                                Canvas(Modifier.fillMaxSize()) {
                                    drawPath(
                                        Path().apply {
                                            moveTo(0f, 0f)
                                            lineTo(size.width, 0f)
                                            lineTo(size.width * .74f, size.height)
                                            lineTo(0f, size.height)
                                            close()
                                        },
                                        lotteryStyle.dark
                                    )
                                }
                            }
                            Image(caixaDSLotteryClover(state.modality ?: "megasena"), null, Modifier.padding(start = 24.dp).size(23.dp))
                        }
                        Text(
                            state.result?.let { drawResultTitle(it, modalityName(state.modality)) }?.lowercase()
                                ?: modalityName(state.modality).lowercase(),
                            modifier = (Modifier.padding(start = 8.dp)).uiId("results.0091"),
                            color = lotteryStyle.content,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CaixaDSFuturaFontFamily,
                                platformStyle = PlatformTextStyle(includeFontPadding = true)
                            )
                        )
                    }
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
                        Text(
                            state.errorMessage,
                            color = CaixaDSColorTokens.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .uiId("results.0092")
                        )
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                            (Modifier.padding(top = 20.dp).uiId("results.0093").clickable(onClick = onRetry)).uiId("results.0094"),
                            color = CaixaDSColorTokens.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                state.result != null -> item { ResultContent(requireNotNull(state.result), state.modality ?: "megasena") }

                else -> item {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_nenhum_resultado_disponivel_para_est_cec209),
                        (Modifier.fillMaxWidth().padding(30.dp)).uiId("results.0095"),
                        color = CaixaDSColorTokens.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
