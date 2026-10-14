package br.gov.caixa.loterias.feature.betting

import androidx.activity.compose.BackHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.res.painterResource
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.platform.testTag
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyHtmlDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyBottomSummary
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDivider
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyNumberGrid
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySectionHeader
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import coil3.compose.AsyncImage

@Composable
fun BettingGameRoute(
    viewModel: BettingGameViewModel,
    onBack: () -> Unit,
    onAdded: () -> Unit,
    shakeEnabled: Boolean = false,
    cartTotal: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
    fallbackModalityId: Int? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showNumberQuantity by remember { mutableStateOf(false) }
    var showRecurring by remember { mutableStateOf(false) }
    var showCloverQuantity by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var showEmptyCartMessage by remember { mutableStateOf(false) }
    val requestCart: () -> Unit = {
        if (cartBetCount > 0) onCart?.invoke() else showEmptyCartMessage = true
    }
    val returnOrConfirm: () -> Unit = {
        if (!viewModel.returnToNumbers()) {
            if (state.hasPendingEdits) showExitConfirmation = true else onBack()
        }
    }
    BackHandler(enabled = !state.loading, onBack = returnOrConfirm)
    BettingShakeEffect(shakeEnabled, state, viewModel, showExitConfirmation || showNumberQuantity || showCloverQuantity || showRecurring || state.favoriteExpanded || state.favoriteOverwriteConfirmation || state.errorMessage != null || state.warningMessage != null || state.duplicateConfirmation || state.addedToCart)
    LaunchedEffect(state.addedToCart) { if (state.addedToCart) onAdded() }

    CaixaDSLegacyScaffold(
        title = state.game?.modality?.name ?: "Simular aposta",
        containerColor = CaixaDSColorTokens.White,
        customTopBar = {
            Column {
                BettingLegacyGameHeader(state, returnOrConfirm, onHelp, fallbackModalityId)
                if (state.mode == BetEntryMode.MANUAL && state.game?.acceptsMirror == true &&
                    state.selectionStep == BetSelectionStep.NUMBERS && state.selectedNumbers.size == state.numberQuantity
                ) BettingLegacyMirror(state.generateMirror, viewModel::toggleMirror, manual = true)
            }
        },
        onBack = returnOrConfirm,
        bottomBar = {
            if (state.game != null) {
                BettingLegacyFooter(state, viewModel, cartTotal, cartBetCount, requestCart.takeIf { onCart != null })
            }
        },
    ) { padding ->
        when {
            state.loading && state.game == null -> Box(Modifier.fillMaxSize().padding(padding))
            state.game == null -> BettingGameError(
                message = state.errorMessage ?: "Aposta indisponível.",
                padding = padding,
                onRetry = viewModel::load,
            )
            else -> {
                val game = requireNotNull(state.game)
                Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, CaixaDSColorTokens.LegacySimulationToolbar, RoundedCornerShape(8.dp)).padding(1.dp)) {
                    if (game.modality.id == LOTECA_ID) {
                        BettingLegacyLoteca(state, viewModel, onQuantity = { showNumberQuantity = true }, onHelp = { showHelp = true })
                    } else if (game.modality.id == LOTOGOL_ID) {
                        LotogolBetContent(state, viewModel)
                    } else {
                        BettingModeToolbar(
                            state = state,
                            onManual = { viewModel.selectMode(BetEntryMode.MANUAL) },
                            onSurprise = { viewModel.selectMode(BetEntryMode.SURPRISE) },
                            onNumberQuantity = {
                                if (state.game?.modality?.id == MAIS_MILIONARIA_ID && state.selectionStep == BetSelectionStep.COMPLEMENT && state.mode == BetEntryMode.MANUAL) showCloverQuantity = true
                                else showNumberQuantity = true
                            },
                            onRecurring = {
                                if (state.game?.contestType.equals("ESPECIAL", ignoreCase = true)) {
                                    viewModel.warnSpecialRecurringUnavailable()
                                } else {
                                    showRecurring = true
                                }
                            },
                            onTutorial = { showHelp = true },
                        )
                        if (state.mode == BetEntryMode.MANUAL) {
                            ManualBetContent(state, viewModel)
                        } else {
                            SurpriseBetContent(state, viewModel)
                        }
                    }
                }
            }
        }
    }

    // SimularApostaModel -> AdicionarApostaCarrinhoHelper -> AlertDialogUtils
    // keeps the rendered ticket behind LoadingViewLoterias during an operation.
    if (state.loading) CaixaDSAuthenticationLoadingDialog()

    if (showExitConfirmation) BettingMessageDialog(
        title = "Atenção",
        message = "Ao voltar para a página inicial, sua aposta em edição será descartada. Deseja continuar?",
        positiveLabel = "Confirmar",
        onPositive = { showExitConfirmation = false; onBack() },
        negativeLabel = "Cancelar",
        onNegative = { showExitConfirmation = false },
    )

    if (showHelp && state.game != null) CaixaDSLegacyHtmlDialog(
        title = legacyBettingHelpTitle(state.game?.modality?.id ?: 0, state.game?.modality?.name.orEmpty()),
        html = legacyBettingHelp(state.game?.modality?.id ?: 0),
        positiveLabel = "Entendi",
        onPositive = { showHelp = false },
    )

    if (state.addedToCart) BettingMessageDialog(
        title = "Atenção", message = "Aposta adicionada ao carrinho com sucesso!",
        positiveLabel = "Entendi", onPositive = viewModel::acknowledgeCartAddition,
    )
    if (state.duplicateConfirmation) BettingMessageDialog(
        title = "Atenção", message = br.gov.caixa.loterias.core.domain.cart.VISITOR_DUPLICATE_CONFIRMATION,
        positiveLabel = "Sim", negativeLabel = "Não", onPositive = viewModel::confirmLocalDuplicate, onNegative = viewModel::cancelLocalDuplicate,
    )
    if (showNumberQuantity) {
        BettingOptionDialog(
            title = if (state.game?.modality?.id == LOTECA_ID) "Quantidade de Palpites" else "Quantidade de números",
            options = state.numberQuantityOptions,
            description = if (state.game?.modality?.id == LOTECA_ID) "Escolha em quantos palpites você quer jogar:" else null,
            selected = state.numberQuantity,
            subtitle = if (state.game?.modality?.id == MAIS_MILIONARIA_ID) "* Valores para apostas com 2 trevos" else null,
            label = { quantity ->
                if (state.game?.modality?.id == LOTECA_ID) "$quantity palpites" else {
                    val price = state.game?.let {
                        resolveBetPrice(
                            it,
                            quantity,
                            if (state.mode == BetEntryMode.SURPRISE) state.surpriseCloverQuantity else state.selectedClovers.size,
                        )
                    }
                    "$quantity números por ${formatBetAmount(price)}"
                }
            },
            onSelect = viewModel::chooseNumberQuantity,
            onDismiss = { showNumberQuantity = false },
        )
    }
    if (showCloverQuantity) {
        BettingOptionDialog(
            title = "Quantidade de trevos",
            subtitle = "Para aposta com ${state.numberQuantity} números",
            options = state.cloverQuantityOptions,
            selected = state.manualCloverQuantity,
            label = { quantity -> "$quantity trevos por ${formatBetAmount(state.game?.let { resolveBetPrice(it, state.numberQuantity, quantity) })}" },
            onSelect = viewModel::chooseCloverQuantity,
            onDismiss = { showCloverQuantity = false },
        )
    }
    if (showRecurring) {
        BettingOptionDialog(
            title = "Teimosinhas",
            description = "Escolha em quantos concursos você quer participar com este mesmo jogo:",
            options = state.recurringOptions,
            selected = state.recurringDraws,
            label = ::recurringLabel,
            onSelect = viewModel::chooseRecurringDraws,
            onDismiss = { showRecurring = false },
        )
    }
    if (state.favoriteExpanded) {
        BettingFavoriteDialog(state.favoriteName, viewModel::changeFavoriteName, viewModel::submitFavorite, viewModel::cancelFavorite)
    }
    if (state.favoriteOverwriteConfirmation) {
        BettingMessageDialog("Atenção", state.favoriteOverwriteMessage.orEmpty(), "Sim", viewModel::confirmFavoriteOverwrite,
            negativeLabel = "Não", onNegative = viewModel::dismissFavoriteOverwrite)
    }
    state.warningMessage?.let { message ->
        BettingMessageDialog("Atenção", message, "Entendi", viewModel::dismissMessage)
    }
    if (state.game != null && state.errorMessage != null) {
        BettingMessageDialog("Atenção", requireNotNull(state.errorMessage), "Entendi", viewModel::dismissMessage)
    }
    if (showEmptyCartMessage) BettingMessageDialog(
        title = "Atenção",
        message = "Seu carrinho está vazio.",
        positiveLabel = "Entendi",
        onPositive = { showEmptyCartMessage = false },
    )
}

@Composable
private fun BettingMessageDialog(
    title: String,
    message: String,
    positiveLabel: String,
    onPositive: () -> Unit,
    negativeLabel: String? = null,
    onNegative: () -> Unit = {},
) = CaixaDSLegacyDialog(
    title = title,
    message = message,
    positiveLabel = positiveLabel,
    onPositive = onPositive,
    negativeLabel = negativeLabel,
    onNegative = onNegative,
)

@Composable
private fun BettingModeToolbar(
    state: BettingGameUiState,
    onManual: () -> Unit,
    onSurprise: () -> Unit,
    onNumberQuantity: () -> Unit,
    onRecurring: () -> Unit,
    onTutorial: (() -> Unit)?,
) {
    val graphite = CaixaDSColorTokens.LegacySocialGraphite
    val modalityStyle = caixaDSLotteryStyle(state.game?.modality?.id ?: 0)
    val surprise = state.mode == BetEntryMode.SURPRISE
    // SwitchMaterial measures 48dp; its native container adds 4dp on each side.
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("betting-mode-toolbar").background(if (surprise) modalityStyle.light.copy(alpha = 26f / 255f) else CaixaDSColorTokens.LegacySimulationToolbar).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier.padding(4.dp).toggleable(
                    value = surprise,
                    role = Role.Switch,
                    onValueChange = { checked -> if (checked) onSurprise() else onManual() },
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(width = 45.dp, height = 26.dp)
                        .testTag("betting-surprise-switch-control")
                        .background(
                            color = if (surprise) modalityStyle.light else CaixaDSColorTokens.White,
                            shape = RoundedCornerShape(13.dp),
                        )
                        .border(
                            width = 1.dp,
                            color = if (surprise) modalityStyle.light else graphite,
                            shape = RoundedCornerShape(13.dp),
                        ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Box(
                        Modifier.offset(x = if (surprise) 21.dp else 0.dp)
                            .size(24.dp)
                            .background(
                                color = if (surprise) CaixaDSColorTokens.White else graphite,
                                shape = CircleShape,
                            )
                            .then(
                                if (surprise) Modifier.border(2.dp, graphite, CircleShape) else Modifier,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_surpresinha),
                            contentDescription = null,
                            tint = if (surprise) modalityStyle.light else CaixaDSColorTokens.White,
                            modifier = Modifier.size(width = 14.dp, height = 13.dp),
                        )
                    }
                }
                // The native switch occupies 48dp; the drawn track above occupies 45dp.
                Text("Surpresinha", Modifier.padding(start = 11.dp), fontFamily = CaixaDSFontFamily, fontSize = 16.sp, color = graphite, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.clickable(enabled = onTutorial != null) { onTutorial?.invoke() }.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Como jogar", Modifier.padding(end = 8.dp), fontFamily = CaixaDSFontFamily, fontSize = 16.sp, color = graphite, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                Icon(painterResource(R.drawable.ic_info), null, Modifier.size(24.dp), tint = graphite)
            }
        }
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val style = caixaDSLotteryStyle(state.game?.modality?.id ?: 0)
            val cloverSelector = state.game?.modality?.id == MAIS_MILIONARIA_ID && state.selectionStep == BetSelectionStep.COMPLEMENT && state.mode == BetEntryMode.MANUAL
            BettingDSButton(if (cloverSelector) "${state.manualCloverQuantity} trevos" else "${state.numberQuantity} números", onNumberQuantity, Modifier.width(if (cloverSelector) 100.dp else 112.dp).height(45.dp),
                color = style.light, content = style.content, stroke = style.light, textSize = 14f, corner = 12f,
                icon = R.drawable.ic_arrow_down, semiBold = !cloverSelector)
            BettingDSButton(if (state.recurringDraws == 0) "Teimosinha" else recurringLabel(state.recurringDraws), onRecurring,
                Modifier.width(112.dp).height(45.dp), color = CaixaDSColorTokens.White, content = graphite,
                stroke = CaixaDS.colors.legacyGraphite, textSize = 14f, corner = 12f, icon = R.drawable.ic_arrow_down, retainNativeAnimator = true)
            if (state.requiresManualComplement) {
                Spacer(Modifier.weight(1f))
                Text(if (state.selectionStep == BetSelectionStep.COMPLEMENT) "2/2" else "1/2", Modifier.padding(end = 16.dp).height(40.dp).wrapContentHeight(Alignment.CenterVertically), fontFamily = CaixaDSFontFamily, fontSize = 14.sp, color = graphite, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
            }
        }
    }
}

@Composable
private fun ModeButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxSize().clickable(onClick = onClick)
            .background(if (selected) CaixaDSColorTokens.White.copy(alpha = 0.2f) else Color.Transparent),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = CaixaDSColorTokens.White, textAlign = TextAlign.Center, style = CaixaDS.typography.labelMedium)
    }
}

@Composable
private fun SelectorButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    BettingDSButton(text, onClick, modifier, color = CaixaDSColorTokens.White,
        content = CaixaDSColorTokens.LegacySocialGraphite, stroke = CaixaDS.colors.legacyGraphite, textSize = 14f, corner = 12f, icon = R.drawable.ic_arrow_down)

}

/** textSelecionados has one native16dp margin; the ticket container already applies8dp. */
@Composable
private fun legacyPromptInset() = with(androidx.compose.ui.platform.LocalDensity.current) {
    (16.dp.roundToPx() - 8.dp.roundToPx()).toDp()
}

@Composable
private fun ManualBetContent(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val game = requireNotNull(state.game)
    val promptInset = legacyPromptInset()
    Column(
        Modifier.fillMaxSize().background(CaixaDS.colors.surface)
            .padding(horizontal = 8.dp),
    ) {
        if (state.selectionStep == BetSelectionStep.COMPLEMENT) {
            // CX keeps textSelecionados visible even when TREVOS sets its text to empty.
            // textSelecionados has a 16dp margin below layoutFiltros, inside the border.
            val complementPromptTop = 16.dp
            Text(when (game.modality.id) {
                TIMEMANIA_ID -> "Escolha o time do coração:"
                DIA_DE_SORTE_ID -> "Escolha o mês de sorte:"
                else -> ""
            }, Modifier.padding(start = promptInset, top = complementPromptTop, bottom = 8.dp), fontFamily = CaixaDSFontFamily, fontSize = 16.sp, color = CaixaDSColorTokens.LegacySocialGraphite, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
                when (game.modality.id) {
                    TIMEMANIA_ID -> TeamOfTheHeartContent(state, viewModel)
                    DIA_DE_SORTE_ID -> LuckyMonthContent(state, viewModel)
                    MAIS_MILIONARIA_ID -> CloverSelectionContent(state, viewModel)
                }
                Spacer(Modifier.height(16.dp))
            }
        } else if (game.modality.id == SUPER_SEVEN_ID) {
            SuperSevenMatrix(state, viewModel)
        } else {
            Text(if (state.selectedNumbers.isEmpty()) "Selecione os números:" else "Selecionados: ${state.selectedNumbers.size}/${state.numberQuantity}", Modifier.padding(start = promptInset, top = 16.dp, bottom = 8.dp), fontFamily = CaixaDSFontFamily, fontSize = 16.sp, color = CaixaDSColorTokens.LegacySocialGraphite, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
            // textSelecionados belongs to the activity; only the fragment body scrolls.
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
                // Short Lotofácil and Dia de Sorte tickets retain cltGeral's native margin.
                if (game.modality.id == 8 || game.modality.id == DIA_DE_SORTE_ID) Spacer(Modifier.height(16.dp))
                BettingLegacyNumberGrid(
                    numbers = (1..game.maximumNumber).toList(),
                    selected = state.selectedNumbers,
                    onNumberClick = viewModel::toggleNumber,
                    modifier = Modifier.fillMaxWidth(),
                    modalityId = game.modality.id,
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SurpriseBetContent(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    Column(
        Modifier.fillMaxSize().background(CaixaDS.colors.surface)
            .verticalScroll(rememberScrollState()).padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Na Surpresinha os números ficam\nmascarados até a efetivação da compra.", Modifier.padding(top = 20.dp), fontFamily = CaixaDSFontFamily,
            fontSize = 16.sp, color = CaixaDSColorTokens.LegacySocialGraphite, textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
        FlowRow(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp), horizontalArrangement = Arrangement.Center) {
            repeat(6) {
                Box(Modifier.padding(4.dp).size(40.dp).background(CaixaDSColorTokens.White, CircleShape).border(1.dp, CaixaDSColorTokens.LegacySimulationMuted, CircleShape), contentAlignment = Alignment.Center) {
                    Text("XX", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaixaDSColorTokens.LegacySimulationMuted)
                }
            }
        }
        val plusMillion = state.game?.modality?.id == MAIS_MILIONARIA_ID
        Text(if (plusMillion) "Escolha a quantidade de números" else "Você ainda pode escolher quantos jogos Surpresinha quer apostar neste concurso:", Modifier.padding(start = 45.dp, end = 45.dp, top = 20.dp), fontFamily = CaixaDSFontFamily, fontSize = 16.sp, color = CaixaDSColorTokens.LegacySocialGraphite, textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
        Row(Modifier.padding(top = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularCounterButton("-", {
                if (plusMillion) state.numberQuantityOptions.lastOrNull { it < state.numberQuantity }?.let(viewModel::chooseNumberQuantity)
                else viewModel.decreaseSurpriseQuantity()
            }, muted = if (plusMillion) state.numberQuantity == state.numberQuantityOptions.firstOrNull() else state.surpriseQuantity == 1)
            Column(Modifier.padding(horizontal = 16.dp).height(50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text((if (plusMillion) state.numberQuantity else state.surpriseQuantity).toString(), Modifier.width(40.dp), textAlign = TextAlign.Center, fontFamily = CaixaDSFontFamily, fontSize = 18.sp, color = CaixaDSColorTokens.LegacySocialGraphite)
                Box(Modifier.padding(top = 6.dp).width(40.dp).height(2.dp).background(CaixaDSColorTokens.LegacySocialGraphite))
            }
            CircularCounterButton("+", {
                if (plusMillion) state.numberQuantityOptions.firstOrNull { it > state.numberQuantity }?.let(viewModel::chooseNumberQuantity)
                else viewModel.increaseSurpriseQuantity()
            })
        }
        if (state.game?.modality?.id == TIMEMANIA_ID) {
            Spacer(Modifier.height(20.dp))
            TeamOfTheHeartContent(state, viewModel)
        }
        if (state.game?.modality?.id == DIA_DE_SORTE_ID) {
            Spacer(Modifier.height(20.dp))
            LuckyMonthContent(state, viewModel)
        }
        if (plusMillion) {
            Text("Escolha a quantidade de trevos", Modifier.fillMaxWidth().padding(start = 45.dp, end = 45.dp, top = 20.dp),
                fontFamily = CaixaDSFontFamily, fontSize = 16.sp, color = CaixaDSColorTokens.LegacySocialGraphite,
                textAlign = TextAlign.Center, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
            Row(Modifier.padding(top = 30.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularCounterButton("-", viewModel::decreaseSurpriseCloverQuantity,
                    muted = state.surpriseCloverQuantity == state.game?.minimumClovers)
                Column(Modifier.padding(horizontal = 16.dp).height(50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(state.surpriseCloverQuantity.toString(), Modifier.width(40.dp), textAlign = TextAlign.Center,
                        fontFamily = CaixaDSFontFamily, fontSize = 18.sp, color = CaixaDSColorTokens.LegacySocialGraphite)
                    Box(Modifier.padding(top = 6.dp).width(40.dp).height(2.dp).background(CaixaDSColorTokens.LegacySocialGraphite))
                }
                CircularCounterButton("+", viewModel::increaseSurpriseCloverQuantity)
            }
        }
        if (state.game?.acceptsMirror == true) {
            BettingLegacyMirror(state.generateMirror, viewModel::toggleMirror, manual = false)
        }
    }
}

@Composable
private fun CloverSelectionContent(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val game = requireNotNull(state.game)
    val metrics = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    Text("Escolha de 2 a 6 trevos \npara marcar nesta aposta", Modifier.fillMaxWidth().padding(start = 60.dp, end = 60.dp, top = 36.dp),
        fontFamily = CaixaDSFontFamily, fontSize = 16.sp, color = CaixaDSColorTokens.LegacySocialGraphite, textAlign = TextAlign.Center, style = metrics)
    Column(Modifier.fillMaxWidth().padding(top = 42.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                (1..game.maximumCloverNumber).toList().chunked(3).forEach { row ->
            Row {
                row.forEach { number ->
                    val checked = number in state.selectedClovers
                    val dark = caixaDSLotteryStyle(MAIS_MILIONARIA_ID).dark
                    Box(
                        Modifier.padding(8.dp).size(45.dp)
                            .testTag("betting-clover-$number")
                            .semantics { contentDescription = "Trevo $number"; this.selected = checked }
                            .toggleable(value = checked, role = Role.Checkbox) { viewModel.toggleClover(number) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(
                                if (checked) R.drawable.installed_betting_ic_trevo_selecionado
                                else R.drawable.installed_betting_ic_trevo,
                            ),
                            contentDescription = null,
                            tint = dark,
                            modifier = Modifier.fillMaxSize(),
                        )
                        Text(
                            number.toString(),
                            color = if (checked) CaixaDSColorTokens.White else dark,
                            fontFamily = CaixaDSFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

// GridView centers using integer division; Compose's bias alignment rounds odd remainders.
private val LegacyGridHorizontalAlignment = object : Alignment.Horizontal {
    override fun align(size: Int, space: Int, layoutDirection: androidx.compose.ui.unit.LayoutDirection): Int =
        (space - size) / 2
}

/** LinearLayout gives each successive weight its share of the remaining pixels.
 * Row.weight distributes leftover pixels from the first column, shifting the balls. */
@Composable
private fun NativeSuperSevenRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier.fillMaxWidth(), content = content) { measurables, constraints ->
        val margins = 2 * 1.dp.roundToPx()
        var remaining = constraints.maxWidth - margins * measurables.size
        val children = measurables.mapIndexed { index, measurable ->
            val share = remaining / (measurables.size - index)
            remaining -= share
            val width = share + margins
            measurable.measure(constraints.copy(minWidth = width, maxWidth = width, minHeight = 0))
        }
        layout(constraints.maxWidth, children.maxOfOrNull { it.height } ?: 0) {
            var left = 0
            children.forEach { child -> child.placeRelative(left, 0); left += child.width }
        }
    }
}

@Composable
private fun SuperSevenMatrix(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val density=androidx.compose.ui.platform.LocalDensity.current
    val promptInset=legacyPromptInset()
    val nativeBallSize=with(density) { (44.dp.toPx().toInt()).toDp() }
    val metrics=TextStyle(platformStyle=PlatformTextStyle(includeFontPadding=true))
    Column(Modifier.fillMaxSize()) {
        val selectedCount = state.superSevenColumns.sumOf(Set<Int>::size)
        Text(if (selectedCount == 0) "Selecione os números:" else "Selecionados: $selectedCount/${state.numberQuantity}",Modifier.padding(start=promptInset,top=16.dp,bottom=8.dp),fontFamily=CaixaDSFontFamily,fontSize=16.sp,color=CaixaDSColorTokens.LegacySocialGraphite,style=metrics)
        NativeSuperSevenRow {
            repeat(7) { column ->
                Box(Modifier.padding(1.dp).height(35.dp).testTag("super-sete-header-$column").background(CaixaDSColorTokens.LegacySuperSevenHeader,RoundedCornerShape(16.dp)),contentAlignment=Alignment.Center) {
                    Text((column+1).toString(),fontFamily=CaixaDSFontFamily,fontWeight=FontWeight.Bold,fontSize=14.sp,color=CaixaDSColorTokens.White,style=metrics)
                }
            }
        }
        // Native headers stay above the NestedScrollView. Its cltGeral margin scrolls
        // with the digits; the activity prompt and all seven headers stay fixed.
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(16.dp))
            NativeSuperSevenRow(Modifier.padding(bottom=16.dp)) {
                repeat(7) { column ->
                    Column(Modifier.padding(1.dp).background(CaixaDSColorTokens.LegacySuperSevenColumn),verticalArrangement=Arrangement.spacedBy(15.dp),horizontalAlignment=LegacyGridHorizontalAlignment) {
                        (0..9).forEach { digit ->
                            val checked=digit in state.superSevenColumns[column]
                            Box(
                                Modifier.size(nativeBallSize)
                                    .testTag("super-sete-$column-$digit")
                                    .semantics { contentDescription = "Coluna ${column + 1}, número $digit"; this.selected = checked }
                                    .toggleable(value = checked, role = Role.Checkbox) { viewModel.toggleSuperSevenDigit(column, digit) }
                                    .background(
                                        if (checked) CaixaDSColorTokens.LegacySuperSevenSelection else CaixaDSColorTokens.White,
                                        CircleShape,
                                    )
                                    .then(
                                        if (checked) Modifier
                                        else Modifier.border(with(density) { 3.toDp() }, CaixaDSColorTokens.LegacySuperSevenNumber, CircleShape),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    digit.toString(),
                                    color = if (checked) CaixaDSColorTokens.LegacySuperSevenSelectedNumber else CaixaDSColorTokens.LegacySuperSevenNumber,
                                    fontFamily = CaixaDSLegacyFuturaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    style = metrics,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LuckyMonthContent(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val months = state.game?.luckyMonths.orEmpty()
    Column(Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        months.chunked(2).forEach { rowMonths ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                rowMonths.forEach { month ->
                    val checked = month == state.selectedLuckyMonth
                    Box(
                        Modifier.weight(1f)
                            .testTag("lucky-month-${month.name}")
                            .clip(RoundedCornerShape(4.dp))
                            .then(
                                if (checked) Modifier.background(caixaDSLotteryStyle(DIA_DE_SORTE_ID).light)
                                else Modifier.border(1.dp, CaixaDSColorTokens.LegacySimulationMuted, RoundedCornerShape(4.dp)),
                            )
                            .selectable(selected = checked, role = Role.RadioButton) { viewModel.selectLuckyMonth(month) }
                            .semantics { contentDescription = month.name; this.selected = checked }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            month.name,
                            fontFamily = CaixaDSFontFamily,
                            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = CaixaDSColorTokens.LegacySocialGraphite,
                            textAlign = TextAlign.Center,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                        )
                    }
                }
                repeat(2 - rowMonths.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        if (months.isEmpty()) Text("Meses indisponíveis para este concurso.", color = CaixaDS.colors.legacyMutedText)
    }
}

@Composable
private fun TeamOfTheHeartContent(state: BettingGameUiState, viewModel: BettingGameViewModel) = BettingLegacyTeams(state, viewModel)

@Composable
private fun LotogolBetContent(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val game = requireNotNull(state.game)
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
    ) {
        Text(
            "Informe o placar de cada jogo",
            color = CaixaDSColorTokens.Lotogol,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
        )
        game.matches.forEach { match ->
            Text(
                "Jogo ${match.number.toString().padStart(2, '0')}",
                color = CaixaDSColorTokens.Blue700,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
            )
            val scores = state.lotogolScores[match.number] ?: (null to null)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LotogolTeamScore(
                    team = match.home,
                    selected = scores.first,
                    onScore = { viewModel.selectLotogolScore(match.number, true, it) },
                    modifier = Modifier.weight(1f),
                )
                Text("X", color = CaixaDSColorTokens.Blue700, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                LotogolTeamScore(
                    team = match.away,
                    selected = scores.second,
                    onScore = { viewModel.selectLotogolScore(match.number, false, it) },
                    modifier = Modifier.weight(1f),
                )
            }
            CaixaDSLegacyDivider()
        }
        OutlinedLegacyButton("Limpar aposta", viewModel::clearSelection)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LotogolTeamScore(
    team: SportsTeam,
    selected: Int?,
    onScore: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = team.shieldBase64?.let { if (it.startsWith("data:")) it else "data:image/png;base64,$it" },
            contentDescription = "Escudo ${team.name}",
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit,
        )
        Text(team.name, color = CaixaDSColorTokens.LegacyTutorialGray, fontSize = 14.2.sp, textAlign = TextAlign.Center)
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            (0..5).forEach { score ->
                Box(
                    Modifier.size(28.dp)
                        .background(
                            if (selected == score) CaixaDSColorTokens.Lotogol else CaixaDS.colors.background,
                            CircleShape,
                        )
                        .border(1.dp, CaixaDSColorTokens.Lotogol, CircleShape)
                        .clickable { onScore(score) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (score == 5) "5+" else score.toString(),
                        color = if (selected == score) CaixaDSColorTokens.White else CaixaDSColorTokens.Lotogol,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularCounterButton(text: String, onClick: () -> Unit, muted: Boolean = false) {
    BettingDSButton(text, onClick, Modifier.size(50.dp), color = if (muted) CaixaDS.colors.legacyLine else CaixaDSColorTokens.LegacySocialGraphite, content = CaixaDSColorTokens.White, textSize = 12f, textSizeInDp = true, retainNativeAnimator = true)

}

@Composable
private fun OutlinedLegacyButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CaixaDS.colors.brand),
        contentPadding = PaddingValues(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CaixaDS.colors.brand),
    ) { Text(text) }
}

@Composable
private fun BettingGameError(message: String, padding: PaddingValues, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = CaixaDS.colors.legacyText, textAlign = TextAlign.Center)
        TextButton(onClick = onRetry) { Text("Tentar novamente") }
    }
}

private fun recurringLabel(draws: Int): String = when (draws) {
    0 -> "Nenhum concurso"
    1 -> "1 concurso"
    else -> "$draws concursos"
}

@Composable
private fun modalityColorForGame(id: Int) = when (id) {
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

private fun numberGridHeight(maximumNumber: Int) = (((maximumNumber.coerceAtLeast(1) + 5) / 6) * 54).dp

@Composable
private fun BettingLegacyGameHeader(
    state: BettingGameUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    fallbackModalityId: Int?,
) {
    val game = state.game
    val modalityId = game?.modality?.id ?: fallbackModalityId ?: 0
    val fallbackTitle = if (modalityId == MAIS_MILIONARIA_ID) "+milionária" else game?.modality?.name ?: fallbackBettingTitle(modalityId)
    val style = caixaDSLotteryStyle(modalityId, fallbackTitle)
    val metrics = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).statusBarsPadding()) {
        val special = game?.contestType.equals("ESPECIAL", ignoreCase = true)
        val specialTitle = game?.takeIf { special }?.let { specialHeaderTitle(it.modality.id, it.modality.specialName) }
        val specialArtwork = game?.takeIf { special }?.let { specialHeaderArtwork(it.modality.id) }
        Box(Modifier.fillMaxWidth().height(48.dp).background(style.light)) {
            if (specialArtwork != null) {
                Image(painterResource(specialArtwork), null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
            }
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                BettingDSHeaderIcon(R.drawable.betting_seta, "Voltar", 12, style.content, onBack)
                Text(specialTitle ?: fallbackTitle.lowercase(), Modifier.weight(1f).padding(end = 24.dp), fontFamily = CaixaDSLegacyFuturaFontFamily,
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = style.content, style = metrics,
                    maxLines = if (specialTitle != null) 2 else 1, overflow = TextOverflow.Ellipsis)
                Column(Modifier.align(Alignment.Top).padding(top = 6.dp, end = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Concurso", fontFamily = CaixaDSFontFamily, fontSize = 14.sp, color = style.content, style = metrics, maxLines = 1)
                    Text(game?.contestNumber?.toString().orEmpty(), fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = style.content, style = metrics, maxLines = 1)
                }
                Column(Modifier.align(Alignment.Top).padding(top = 6.dp, end = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (game?.modality?.id == LOTECA_ID) "Resultado" else "Sorteio", fontFamily = CaixaDSFontFamily, fontSize = 14.sp, color = style.content, style = metrics, maxLines = 1)
                    Text(bettingHeaderDrawDate(game?.drawDate.orEmpty()), fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = style.content, style = metrics, maxLines = 1)
                }
                BettingDSHeaderIcon(R.drawable.icon_duvida2, "Dúvidas", 8, style.content, onHelp, Modifier.padding(end = 4.dp))
            }
        }
    }
}

private fun fallbackBettingTitle(modalityId: Int): String = when (modalityId) {
    2 -> "Mega-Sena"
    3 -> "Quina"
    5 -> "Lotomania"
    7 -> "Super Sete"
    8 -> "Lotofácil"
    9 -> "+Milionária"
    11 -> "Dia de Sorte"
    LOTOGOL_ID -> "Lotogol"
    18 -> "Dupla Sena"
    LOTECA_ID -> "Loteca"
    TIMEMANIA_ID -> "Timemania"
    else -> "Simular aposta"
}

private fun specialHeaderTitle(modalityId: Int, backendName: String): String = when (modalityId) {
    2 -> "mega da \nvirada"
    3 -> "quina de \nsão joão"
    8 -> "lotofácil da \nindependência"
    18 -> "dupla de \npáscoa"
    else -> backendName.ifBlank { "" }.lowercase()
}

private fun specialHeaderArtwork(modalityId: Int): Int? = when (modalityId) {
    2 -> R.drawable.betting_mega_special_header
    3 -> R.drawable.betting_quina_special_header
    8 -> R.drawable.betting_lotofacil_special_header
    18 -> R.drawable.betting_duplasena_special_header
    else -> null
}

/** Header icon drawn by Compose from the source asset, centered at its original intrinsic size. */
@Composable
private fun BettingDSHeaderIcon(resource: Int, description: String, padding: Int, tint: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.size(48.dp).clickable(onClick = onClick).semantics { contentDescription = description }) {
        Icon(
            painter = painterResource(resource),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.align(Alignment.Center).padding(padding.dp),
        )
    }
}

/** Same dd/MM display as SimulaViewModel.formatarDataSorteio, accepting the modern date-only DTO too. */
private fun bettingHeaderDrawDate(value: String): String = Regex("^\\d{2}/\\d{2}").find(value)?.value.orEmpty()

@Composable
private fun BettingLegacyNumberGrid(numbers: List<Int>, selected: Set<Int>, onNumberClick: (Int) -> Unit, modifier: Modifier, modalityId: Int) {
    val style = caixaDSLotteryStyle(modalityId)
    val accent = if (style.content == CaixaDSColorTokens.White) style.light else style.content
    Layout(modifier = modifier, content = {
        numbers.forEach { number ->
            Box(Modifier.padding(horizontal = 2.dp, vertical = 4.dp), contentAlignment = Alignment.CenterStart) {
                val checked = number in selected
                val label = if (number == 100) "00" else number.toString().padStart(2, '0')
                Box(
                    Modifier.size(45.dp)
                        .testTag("betting-number-$label")
                        .semantics { contentDescription = label; this.selected = checked }
                        .toggleable(value = checked, role = Role.Checkbox, onValueChange = { onNumberClick(number) }),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(if (checked) R.drawable.ic_item_dezena_selecionado else R.drawable.ic_item_dezena),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = if (checked) ColorFilter.tint(accent) else null,
                    )
                    Text(
                        label,
                        color = if (checked) CaixaDSColorTokens.White else CaixaDSColorTokens.LegacySocialGraphite,
                        fontFamily = CaixaDSFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    )
                }
            }
        }
    }) { measurables, constraints ->
        // GridLayoutManager.calculateItemBorders distributes the integer remainder
        // across spans; dividing a dp width rounds every column to the same pixel width.
        val spanCount = 6
        val totalWidth = constraints.maxWidth
        val borders = IntArray(spanCount + 1)
        val baseWidth = totalWidth / spanCount
        val remainder = totalWidth % spanCount
        var extra = 0
        for (span in 1..spanCount) {
            var width = baseWidth
            extra += remainder
            if (extra > 0 && spanCount - extra < remainder) {
                width++
                extra -= spanCount
            }
            borders[span] = borders[span - 1] + width
        }
        val placeables = measurables.mapIndexed { index, measurable ->
            val column = index % spanCount
            measurable.measure(Constraints(
                minWidth = borders[column + 1] - borders[column],
                maxWidth = borders[column + 1] - borders[column],
            ))
        }
        val rows = placeables.chunked(spanCount)
        val rowHeights = rows.map { row -> row.maxOf { it.height } }
        layout(totalWidth, constraints.constrainHeight(rowHeights.sum())) {
            var top = 0
            rows.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { column, child -> child.placeRelative(borders[column], top) }
                top += rowHeights[rowIndex]
            }
        }
    }
}

@Composable
internal fun BettingDSButton(text: String, onClick: () -> Unit, modifier: Modifier, color: Color,
    content: Color, stroke: Color = color, enabled: Boolean = true, textSize: Float = 13f, corner: Float = 4f,
    icon: Int? = null, semiBold: Boolean = false, textSizeInDp: Boolean = false, iconAtStart: Boolean = false, retainNativeAnimator: Boolean = false) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val fontSize = with(density) {
        val pixels = if (textSizeInDp) textSize.dp.roundToPx() else textSize.sp.roundToPx()
        (pixels.toFloat() / this.density / this.fontScale).sp
    }
    Box(
        modifier = modifier
            .testTag("betting-button-$text")
            .clip(RoundedCornerShape(corner.dp))
            .background(color)
            .border(1.dp, stroke, RoundedCornerShape(corner.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = text },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = if (icon != null && !iconAtStart) 4.dp else 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null && iconAtStart) {
                Icon(painterResource(icon), null, Modifier.size(24.dp), tint = Color.Unspecified)
                Spacer(Modifier.width(2.dp))
            }
            Text(
                text = text,
                modifier = Modifier.weight(1f, fill = false),
                color = content,
                fontFamily = CaixaDSFontFamily,
                fontWeight = if (semiBold) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
            )
            if (icon != null && !iconAtStart) {
                Spacer(Modifier.width(8.dp))
                Icon(painterResource(icon), null, Modifier.size(7.dp, 4.dp).testTag("betting-dropdown-icon"), tint = content)
            }
        }
    }
}

@Composable
private fun BettingLegacyFooter(state: BettingGameUiState, viewModel: BettingGameViewModel, cartTotal: java.math.BigDecimal, cartCount: Int, onCart: (() -> Unit)?) {
    val style = caixaDSLotteryStyle(state.game?.modality?.id ?: 0)
    val textMetrics = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    val hasSelection = if (state.selectionStep == BetSelectionStep.COMPLEMENT) when (state.game?.modality?.id) {
        MAIS_MILIONARIA_ID -> state.selectedClovers.isNotEmpty()
        DIA_DE_SORTE_ID -> state.selectedLuckyMonth != null
        TIMEMANIA_ID -> state.selectedTeam != null
        else -> false
    } else state.selectedNumbers.isNotEmpty() || state.superSevenColumns.any { it.isNotEmpty() } || state.lotecaOutcomes.values.any { it.isNotEmpty() } || state.lotogolScores.isNotEmpty()
    val randomEnabled = !state.loading && state.game?.modality?.id !in setOf(LOTECA_ID, LOTOGOL_ID) &&
        if (state.selectionStep == BetSelectionStep.COMPLEMENT) when (state.game?.modality?.id) {
            MAIS_MILIONARIA_ID -> state.selectedClovers.size < state.manualCloverQuantity
            DIA_DE_SORTE_ID -> state.selectedLuckyMonth == null
            TIMEMANIA_ID -> state.selectedTeam == null
            else -> false
        } else !state.numbersAreComplete
    val randomLabel = if (state.selectionStep == BetSelectionStep.COMPLEMENT) when (state.game?.modality?.id) {
        MAIS_MILIONARIA_ID -> if (state.selectedClovers.isEmpty()) "Trevos aleatórios" else "Completar trevos"
        DIA_DE_SORTE_ID -> "Mês aleatório"
        TIMEMANIA_ID -> "Time aleatório"
        else -> "Aposta aleatória"
    } else if (hasSelection) "Completar jogo" else "Aposta aleatória"
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).background(CaixaDSColorTokens.White)) {
        if (state.mode == BetEntryMode.MANUAL) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.canFavorite) {
                BettingDSButton(if (state.favoriteSaved) "Aposta Favoritada" else "Favoritar aposta", viewModel::requestFavorite,
                    Modifier.weight(1f).height(52.dp), CaixaDSColorTokens.White, CaixaDSColorTokens.LegacySocialGraphite,
                    stroke = CaixaDSColorTokens.LegacySocialGraphite,
                    icon = if (state.favoriteSaved) R.drawable.ic_favoritado_simula else R.drawable.ic_star_simula, iconAtStart = true)
            } else {
                BettingDSButton(randomLabel, { viewModel.fillShake(false) }, Modifier.weight(1f).height(52.dp),
                    if (randomEnabled) CaixaDSColorTokens.White else CaixaDSColorTokens.LegacyFavoriteFilterCanvas,
                    if (randomEnabled) CaixaDSColorTokens.LegacySocialGraphite else CaixaDSColorTokens.LegacySimulationMuted,
                    stroke = if (randomEnabled) CaixaDSColorTokens.LegacySocialGraphite else CaixaDSColorTokens.LegacySimulationMuted,
                    retainNativeAnimator = true, enabled = randomEnabled)
            }
            BettingDSButton("Limpar", viewModel::clearCurrentStep, Modifier.width(130.dp).height(52.dp),
                if (hasSelection) CaixaDSColorTokens.White else CaixaDSColorTokens.LegacyFavoriteFilterCanvas,
                if (hasSelection) CaixaDSColorTokens.LegacySocialGraphite else CaixaDSColorTokens.LegacySimulationMuted,
                stroke = if (hasSelection) CaixaDSColorTokens.LegacySocialGraphite else CaixaDSColorTokens.LegacySimulationMuted, enabled = hasSelection, retainNativeAnimator = true)
        }
        BettingDSButton(state.primaryActionLabel, viewModel::primaryAction, Modifier.fillMaxWidth().height(52.dp),
            if (state.canPerformPrimaryAction) style.light else CaixaDSColorTokens.LegacyFavoriteFilterCanvas,
            if (state.canPerformPrimaryAction) style.content else CaixaDSColorTokens.LegacySimulationMuted, stroke = if (state.canPerformPrimaryAction) style.light else CaixaDSColorTokens.LegacySimulationMuted, enabled = state.canPerformPrimaryAction, semiBold = state.canPerformPrimaryAction,
            textSize = if (state.selectionStep == BetSelectionStep.NUMBERS && state.requiresManualComplement) 14f else 13f)
        Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            BettingDSFooterAmount("Valor dessa aposta", formatBetAmount(state.totalPrice), Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(32.dp).background(CaixaDSColorTokens.LegacySimulationMuted))
            BettingDSFooterAmount("Total do carrinho", formatBetAmount(cartTotal), Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(32.dp).background(CaixaDSColorTokens.LegacySimulationMuted))
            BettingDSCartFooter(cartCount, onCart)
        }
    }
}

/** Carrinho e badge desenhados em Compose com dimensões e cores do CaixaDS/rodapé legado. */
@Composable
private fun BettingDSCartFooter(cartCount: Int, onCart: (() -> Unit)?) {
    Box(
        Modifier.width(56.dp).fillMaxHeight().testTag("betting-cart-footer")
            .clickable(enabled = onCart != null, role = Role.Button) { onCart?.invoke() }
            .semantics { contentDescription = "Carrinho de apostas, $cartCount itens" },
        contentAlignment = Alignment.Center,
    ) {
        Image(painterResource(R.drawable.ic_cart), contentDescription = null, modifier = Modifier.size(36.dp, 32.dp))
        Box(
            Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 8.dp)
                .size(15.dp).background(CaixaDSColorTokens.LegacySimulationBadge, CircleShape),
        )
        Box(
            Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 8.dp).size(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                cartCount.toString(),
                color = CaixaDSColorTokens.LegacySocialGraphite,
                fontFamily = CaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
            )
        }
    }
}

/** Rótulos do rodapé em tipografia e métrica do CaixaDS, com o pixel observado no XCover. */
@Composable
private fun BettingDSFooterAmount(label: String, amount: String, modifier: Modifier) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    fun roundedSp(size: Float) = with(density) { (size.sp.roundToPx().toFloat() / this.density / this.fontScale).sp }
    Column(
        modifier.wrapContentHeight().testTag("betting-footer-amount-$label")
            .semantics { contentDescription = "$label $amount" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            label,
            modifier = Modifier.offset { IntOffset(-1, 0) },
            color = CaixaDSColorTokens.LegacySocialGraphite,
            fontFamily = CaixaDSFontFamily,
            fontSize = roundedSp(11f),
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
        )
        Text(
            amount,
            color = CaixaDSColorTokens.LegacySocialGraphite,
            fontFamily = CaixaDSFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = roundedSp(14f),
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
        )
    }
}
