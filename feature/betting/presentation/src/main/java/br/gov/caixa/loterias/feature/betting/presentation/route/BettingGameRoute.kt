package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSHtmlDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingFavoriteDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingGameError
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingLegacyFooter
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingLegacyGameHeader
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingMessageDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingModeToolbar
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.mapper.legacyBettingHelp
import br.gov.caixa.loterias.feature.betting.presentation.mapper.legacyBettingHelpTitle
import br.gov.caixa.loterias.feature.betting.presentation.mapper.recurringLabel
import br.gov.caixa.loterias.feature.betting.presentation.mapper.resolveBetPrice
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyLoteca
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyMirror
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyTeams
import br.gov.caixa.loterias.feature.betting.presentation.screen.LotogolBetContent
import br.gov.caixa.loterias.feature.betting.presentation.screen.ManualBetContent
import br.gov.caixa.loterias.feature.betting.presentation.screen.SurpriseBetContent
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

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
    onFavoriteLoginRequired: (() -> Unit)? = null,
    isAuthenticated: Boolean = false,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    androidx.compose.runtime.SideEffect { viewModel.updateCartContext(isAuthenticated, cartTotal) }
    var showNumberQuantity by remember { mutableStateOf(false) }
    var lotecaDescriptionBindingKey by remember { mutableStateOf(0) }
    var showRecurring by remember { mutableStateOf(false) }
    var showCloverQuantity by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var showEmptyCartMessage by remember { mutableStateOf(false) }
    val requestCart: () -> Unit = {
        if (cartBetCount > 0) viewModel.checkpointNavigation { onCart?.invoke() } else showEmptyCartMessage = true
    }
    val returnOrConfirm: () -> Unit = {
        viewModel.requestBack {
            if (viewModel.state.value.hasPendingEdits) showExitConfirmation = true else viewModel.checkpointNavigation(onBack)
        }
    }
    BackHandler(enabled = !state.loading, onBack = returnOrConfirm)
    BettingShakeEffect(
        shakeEnabled,
        state,
        viewModel,
        showExitConfirmation || showNumberQuantity || showCloverQuantity || showRecurring ||
            state.favoriteExpanded || state.favoriteOverwriteConfirmation || state.errorMessage != null ||
            state.warningMessage != null || state.duplicateConfirmation || state.dailyLimitConfirmation || state.addedToCart
    )
    LaunchedEffect(state.addedToCart) { if (state.addedToCart) onAdded() }

    val actions = BettingGameActions(
        toggleNumber = viewModel::toggleNumber,
        chooseNumberQuantity = viewModel::chooseNumberQuantity,
        decreaseSurpriseQuantity = viewModel::decreaseSurpriseQuantity,
        increaseSurpriseQuantity = viewModel::increaseSurpriseQuantity,
        decreaseSurpriseCloverQuantity = viewModel::decreaseSurpriseCloverQuantity,
        increaseSurpriseCloverQuantity = viewModel::increaseSurpriseCloverQuantity,
        toggleMirror = viewModel::toggleMirror,
        toggleClover = viewModel::toggleClover,
        toggleSuperSevenDigit = viewModel::toggleSuperSevenDigit,
        selectLuckyMonth = viewModel::selectLuckyMonth,
        selectLotogolScore = viewModel::selectLotogolScore,
        clearSelection = viewModel::clearSelection,
        requestFavorite = { onFavoriteLoginRequired?.invoke() ?: viewModel.requestFavorite() },
        fillShake = viewModel::fillShake,
        primaryAction = viewModel::primaryAction
    )

    CaixaDSScaffold(
        title = state.game?.modality?.name ?: uiStrings.getString(LocalizedR.string.ui_simular_aposta_507413),
        containerColor = CaixaDSColorTokens.White,
        customTopBar = {
            Column {
                BettingLegacyGameHeader(state, returnOrConfirm, { viewModel.checkpointNavigation(onHelp) }, fallbackModalityId)
                if (state.mode == BetEntryMode.MANUAL && state.game?.acceptsMirror == true &&
                    state.selectionStep == BetSelectionStep.NUMBERS && state.selectedNumbers.size == state.numberQuantity
                ) {
                    BettingLegacyMirror(state.generateMirror, viewModel::toggleMirror, manual = true)
                }
            }
        },
        onBack = returnOrConfirm,
        bottomBar = {
            if (state.game != null) {
                BettingLegacyFooter(state, actions, cartTotal, cartBetCount, requestCart.takeIf { onCart != null }, onClear = {
                    viewModel.clearCurrentStep()
                    if (state.game?.modality?.id == LOTECA_ID) lotecaDescriptionBindingKey++
                })
            }
        },
        modifier = Modifier.uiId("bet.0180")
    ) { padding ->
        when {
            state.loading && state.game == null -> Box(Modifier.fillMaxSize().padding(padding))

            state.game == null -> BettingGameError(
                message = state.errorMessage ?: uiStrings.getString(LocalizedR.string.ui_aposta_indisponivel_8fce95),
                padding = padding,
                onRetry = viewModel::load,
                review = state.recoveryReview
            )

            else -> {
                val game = requireNotNull(state.game)
                Column(
                    Modifier.fillMaxSize().padding(
                        padding
                    ).padding(
                        16.dp
                    ).clip(
                        RoundedCornerShape(8.dp)
                    ).border(1.dp, CaixaDSColorTokens.SimulationToolbar, RoundedCornerShape(8.dp)).padding(1.dp)
                ) {
                    if (game.modality.id == LOTECA_ID) {
                        BettingLegacyLoteca(state, viewModel, onQuantity = {
                            showNumberQuantity = true
                        }, onHelp = { showHelp = true }, descriptionBindingKey = lotecaDescriptionBindingKey)
                    } else if (game.modality.id == LOTOGOL_ID) {
                        LotogolBetContent(state, actions)
                    } else {
                        BettingModeToolbar(
                            state = state,
                            onManual = { viewModel.selectMode(BetEntryMode.MANUAL) },
                            onSurprise = { viewModel.selectMode(BetEntryMode.SURPRISE) },
                            onNumberQuantity = {
                                if (state.game?.modality?.id == MAIS_MILIONARIA_ID && state.selectionStep == BetSelectionStep.COMPLEMENT &&
                                    state.mode == BetEntryMode.MANUAL
                                ) {
                                    showCloverQuantity = true
                                } else {
                                    showNumberQuantity = true
                                }
                            },
                            onRecurring = {
                                if (state.game?.contestType.equals("ESPECIAL", ignoreCase = true)) {
                                    viewModel.warnSpecialRecurringUnavailable()
                                } else {
                                    showRecurring = true
                                }
                            },
                            onTutorial = { showHelp = true }
                        )
                        if (state.mode == BetEntryMode.MANUAL) {
                            ManualBetContent(state, actions, teamContent = { TeamOfTheHeartContent(state, viewModel) })
                        } else {
                            SurpriseBetContent(state, actions, teamContent = { TeamOfTheHeartContent(state, viewModel) })
                        }
                    }
                }
            }
        }
    }

    // SimularApostaModel -> AdicionarApostaCarrinhoHelper -> AlertDialogUtils
    // mantém o bilhete renderizado atrás de LoadingViewLoterias durante a operação.
    if (state.loading) CaixaDSAuthenticationLoadingDialog()

    if (showExitConfirmation) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_ao_voltar_para_a_pagina_inicial_sua_128dc3),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
            onPositive = {
                showExitConfirmation = false
                viewModel.checkpointNavigation(onBack)
            },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = { showExitConfirmation = false }
        )
    }

    if (showHelp && state.game != null) {
        CaixaDSHtmlDialog(
            title = legacyBettingHelpTitle(state.game?.modality?.id ?: 0, state.game?.modality?.name.orEmpty()),
            html = legacyBettingHelp(state.game?.modality?.id ?: 0),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { showHelp = false }
        )
    }

    if (state.addedToCart) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_aposta_adicionada_ao_carrinho_com_su_d9668a),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = viewModel::acknowledgeCartAddition
        )
    }
    if (state.dailyLimitConfirmation) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_o_valor_maximo_de_apostas_permitida_0e6c55) +
                uiStrings.getString(LocalizedR.string.ui_deseja_prosseguir_com_a_inclusao_da_3e386b),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = viewModel::confirmDailyLimit,
            onNegative = viewModel::cancelDailyLimit
        )
    }
    if (state.duplicateConfirmation) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = br.gov.caixa.loterias.feature.cart.domain.model.VISITOR_DUPLICATE_CONFIRMATION,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = viewModel::confirmLocalDuplicate,
            onNegative = viewModel::cancelLocalDuplicate
        )
    }
    if (showNumberQuantity) {
        CaixaDSOptionDialog(
            title = if (state.game?.modality?.id == LOTECA_ID) {
                uiStrings.getString(
                    LocalizedR.string.ui_quantidade_de_palpites_ef7895
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_quantidade_de_numeros_ca55cf)
            },
            options = state.numberQuantityOptions,
            description = if (state.game?.modality?.id == LOTECA_ID) {
                uiStrings.getString(
                    LocalizedR.string.ui_escolha_em_quantos_palpites_voce_que_4d2dce
                )
            } else {
                null
            },
            selected = state.numberQuantity,
            subtitle = if (state.game?.modality?.id == MAIS_MILIONARIA_ID) {
                uiStrings.getString(
                    LocalizedR.string.ui_valores_para_apostas_com_2_trevos_d4655c
                )
            } else {
                null
            },
            label = { quantity ->
                if (state.game?.modality?.id == LOTECA_ID) {
                    uiStrings.getString(LocalizedR.string.ui_1_s_palpites_b6b418, quantity.toString())
                } else {
                    val price = state.game?.let {
                        resolveBetPrice(
                            it,
                            quantity,
                            if (state.mode == BetEntryMode.SURPRISE) state.surpriseCloverQuantity else state.selectedClovers.size
                        )
                    }
                    uiStrings.getString(LocalizedR.string.ui_1_s_numeros_por_2_s_daf587, quantity.toString(), formatBetAmount(price))
                }
            },
            onSelect = { quantity ->
                viewModel.chooseNumberQuantity(quantity)
                if (state.game?.modality?.id == LOTECA_ID && quantity < state.numberQuantity) lotecaDescriptionBindingKey++
            },
            onDismiss = { showNumberQuantity = false }
        )
    }
    if (showCloverQuantity) {
        CaixaDSOptionDialog(
            title = uiStrings.getString(LocalizedR.string.ui_quantidade_de_trevos_5dc723),
            subtitle = uiStrings.getString(LocalizedR.string.ui_para_aposta_com_1_s_numeros_aa05ed, state.numberQuantity.toString()),
            options = state.cloverQuantityOptions,
            selected = state.manualCloverQuantity,
            label = { quantity ->
                uiStrings.getString(
                    LocalizedR.string.ui_1_s_trevos_por_2_s_427198,
                    quantity.toString(),
                    formatBetAmount(state.game?.let { resolveBetPrice(it, state.numberQuantity, quantity) })
                )
            },
            onSelect = viewModel::chooseCloverQuantity,
            onDismiss = { showCloverQuantity = false }
        )
    }
    if (showRecurring) {
        CaixaDSOptionDialog(
            title = uiStrings.getString(LocalizedR.string.ui_teimosinhas_7217a4),
            description = uiStrings.getString(LocalizedR.string.ui_escolha_em_quantos_concursos_voce_qu_3736e6),
            options = state.recurringOptions,
            selected = state.recurringDraws,
            label = ::recurringLabel,
            onSelect = viewModel::chooseRecurringDraws,
            onDismiss = { showRecurring = false }
        )
    }
    if (state.favoriteExpanded) {
        BettingFavoriteDialog(state.favoriteName, viewModel::changeFavoriteName, viewModel::submitFavorite, viewModel::cancelFavorite)
    }
    if (state.favoriteOverwriteConfirmation) {
        BettingMessageDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            state.favoriteOverwriteMessage.orEmpty(),
            stringResource(LocalizedR.string.betting_recovery_yes),
            viewModel::confirmFavoriteOverwrite,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::dismissFavoriteOverwrite
        )
    }
    state.warningMessage?.let { message ->
        BettingMessageDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissMessage
        )
    }
    if (state.game != null && state.errorMessage != null) {
        BettingMessageDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            requireNotNull(state.errorMessage),
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissMessage
        )
    }
    if (showEmptyCartMessage) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_seu_carrinho_esta_vazio_6e7dbb),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { showEmptyCartMessage = false }
        )
    }
}

@Composable
private fun TeamOfTheHeartContent(state: BettingGameUiState, viewModel: BettingGameViewModel) = BettingLegacyTeams(state, viewModel)
