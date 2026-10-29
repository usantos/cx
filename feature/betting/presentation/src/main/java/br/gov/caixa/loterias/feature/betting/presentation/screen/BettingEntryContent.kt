package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingLegacyNumberGrid
import br.gov.caixa.loterias.feature.betting.presentation.components.legacyPromptInset
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID

@Composable
internal fun ManualBetContent(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val game = requireNotNull(state.game)
    val promptInset = legacyPromptInset()
    Column(
        Modifier.fillMaxSize().background(CaixaDS.colors.surface)
            .padding(horizontal = 8.dp)
    ) {
        if (state.selectionStep == BetSelectionStep.COMPLEMENT) {
            // A CX mantém textSelecionados visível mesmo quando TREVOS define seu texto como vazio.
            // textSelecionados possui margem de 16dp abaixo de layoutFiltros, dentro da borda.
            val complementPromptTop = 16.dp
            Text(
                when (game.modality.id) {
                    TIMEMANIA_ID -> uiStrings.getString(LocalizedR.string.ui_escolha_o_time_do_coracao_d62c54)
                    DIA_DE_SORTE_ID -> uiStrings.getString(LocalizedR.string.ui_escolha_o_mes_de_sorte_73d774)
                    else -> ""
                },
                (Modifier.padding(start = promptInset, top = complementPromptTop, bottom = 8.dp))
                    .uiId("bet.0233"),
                fontFamily = CaixaDSFontFamily,
                fontSize = 16.sp,
                color = CaixaDSColorTokens.SocialGraphite,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
            Column(Modifier.weight(1f).fillMaxWidth().uiId("bet.0234").verticalScroll(rememberScrollState())) {
                when (game.modality.id) {
                    TIMEMANIA_ID -> teamContent()
                    DIA_DE_SORTE_ID -> LuckyMonthContent(state, actions)
                    MAIS_MILIONARIA_ID -> CloverSelectionContent(state, actions)
                }
                Spacer(Modifier.height(16.dp))
            }
        } else if (game.modality.id == SUPER_SEVEN_ID) {
            SuperSevenMatrix(state, actions)
        } else {
            Text(
                if (state.selectedNumbers.isEmpty()) {
                    uiStrings.getString(
                        LocalizedR.string.ui_selecione_os_numeros_44f9f7
                    )
                } else {
                    uiStrings.getString(
                        LocalizedR.string.ui_selecionados_1_s_2_s_0a48ce,
                        state.selectedNumbers.size.toString(),
                        state.numberQuantity.toString()
                    )
                },
                (Modifier.padding(start = promptInset, top = 16.dp, bottom = 8.dp)).uiId("bet.0235"),
                fontFamily = CaixaDSFontFamily,
                fontSize = 16.sp,
                color = CaixaDSColorTokens.SocialGraphite,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
            // textSelecionados pertence à atividade; somente o corpo do fragmento rola.
            Column(Modifier.weight(1f).fillMaxWidth().uiId("bet.0236").verticalScroll(rememberScrollState())) {
                // Bilhetes curtos da Lotofácil e do Dia de Sorte mantêm a margem nativa de cltGeral.
                if (game.modality.id == 8 || game.modality.id == DIA_DE_SORTE_ID) Spacer(Modifier.height(16.dp))
                BettingLegacyNumberGrid(
                    numbers = (1..game.maximumNumber).toList(),
                    selected = state.selectedNumbers,
                    onNumberClick = actions.toggleNumber,
                    modifier = (Modifier.fillMaxWidth()).uiId("bet.0237"),
                    modalityId = game.modality.id
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
internal fun SurpriseBetContent(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().background(CaixaDS.colors.surface)
            .uiId("bet.0238").verticalScroll(rememberScrollState()).padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_na_surpresinha_os_numeros_ficam_masc_e7ef11),
            (Modifier.padding(top = 20.dp)).uiId("bet.0239"),
            fontFamily = CaixaDSFontFamily,
            fontSize = 16.sp,
            color = CaixaDSColorTokens.SocialGraphite,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        FlowRow(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp), horizontalArrangement = Arrangement.Center) {
            repeat(6) {
                Box(
                    Modifier.padding(
                        4.dp
                    ).size(
                        40.dp
                    ).background(CaixaDSColorTokens.White, CircleShape).border(1.dp, CaixaDSColorTokens.SimulationMuted, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_xx_ecc76c),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CaixaDSColorTokens.SimulationMuted,
                        modifier = Modifier.uiId("bet.0240")
                    )
                }
            }
        }
        val plusMillion = state.game?.modality?.id == MAIS_MILIONARIA_ID
        Text(
            if (plusMillion) {
                uiStrings.getString(LocalizedR.string.ui_escolha_a_quantidade_de_numeros_f01f9e)
            } else {
                uiStrings.getString(
                    LocalizedR.string.ui_voce_ainda_pode_escolher_quantos_jog_b63430
                )
            },
            (Modifier.padding(start = 45.dp, end = 45.dp, top = 20.dp)).uiId("bet.0241"),
            fontFamily = CaixaDSFontFamily,
            fontSize = 16.sp,
            color = CaixaDSColorTokens.SocialGraphite,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        SurpriseNumberQuantity(state, actions, plusMillion)
        if (state.game?.modality?.id == TIMEMANIA_ID) {
            Spacer(Modifier.height(20.dp))
            teamContent()
        }
        if (state.game?.modality?.id == DIA_DE_SORTE_ID) {
            Spacer(Modifier.height(20.dp))
            LuckyMonthContent(state, actions)
        }
        if (plusMillion) {
            SurpriseCloverQuantity(state, actions, state.game.minimumClovers)
        }
        if (state.game?.acceptsMirror == true) {
            BettingLegacyMirror(state.generateMirror, actions.toggleMirror, manual = false)
        }
    }
}

@Composable
private fun CircularCounterButton(text: String, onClick: () -> Unit, muted: Boolean = false) {
    CaixaDSBettingButton(
        text, onClick,
        Modifier.size(
            50.dp
        ),
        color = if (muted) CaixaDS.colors.line else CaixaDSColorTokens.SocialGraphite,
        content = CaixaDSColorTokens.White,
        textSize = 12f,
        textSizeInDp = true,
        retainNativeAnimator = true,
        verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
    )
}

@Composable
private fun SurpriseNumberQuantity(state: BettingGameUiState, actions: BettingGameActions, plusMillion: Boolean) {
    Row(Modifier.padding(top = 30.dp), verticalAlignment = Alignment.CenterVertically) {
        CircularCounterButton("-", {
            if (plusMillion) {
                state.numberQuantityOptions.lastOrNull { it < state.numberQuantity }?.let(actions.chooseNumberQuantity)
            } else {
                actions.decreaseSurpriseQuantity()
            }
        }, muted = if (plusMillion) state.numberQuantity == state.numberQuantityOptions.firstOrNull() else state.surpriseQuantity == 1)
        Column(
            Modifier.padding(horizontal = 16.dp).height(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                (if (plusMillion) state.numberQuantity else state.surpriseQuantity).toString(),
                (Modifier.width(40.dp))
                    .uiId("bet.0242"),
                textAlign = TextAlign.Center,
                fontFamily = CaixaDSFontFamily,
                fontSize = 18.sp,
                color = CaixaDSColorTokens.SocialGraphite
            )
            Box(Modifier.padding(top = 6.dp).width(40.dp).height(2.dp).background(CaixaDSColorTokens.SocialGraphite))
        }
        CircularCounterButton("+", {
            if (plusMillion) {
                state.numberQuantityOptions.firstOrNull { it > state.numberQuantity }?.let(actions.chooseNumberQuantity)
            } else {
                actions.increaseSurpriseQuantity()
            }
        })
    }
}

@Composable
private fun SurpriseCloverQuantity(state: BettingGameUiState, actions: BettingGameActions, minimumClovers: Int) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_escolha_a_quantidade_de_trevos_ff6a2c),
        (Modifier.fillMaxWidth().padding(start = 45.dp, end = 45.dp, top = 20.dp)).uiId("bet.0243"),
        fontFamily = CaixaDSFontFamily,
        fontSize = 16.sp,
        color = CaixaDSColorTokens.SocialGraphite,
        textAlign = TextAlign.Center,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    )
    Row(Modifier.padding(top = 30.dp), verticalAlignment = Alignment.CenterVertically) {
        CircularCounterButton(
            "-",
            actions.decreaseSurpriseCloverQuantity,
            muted = state.surpriseCloverQuantity == minimumClovers
        )
        Column(
            Modifier.padding(horizontal = 16.dp).height(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                state.surpriseCloverQuantity.toString(),
                (Modifier.width(40.dp)).uiId("bet.0244"),
                textAlign = TextAlign.Center,
                fontFamily = CaixaDSFontFamily,
                fontSize = 18.sp,
                color = CaixaDSColorTokens.SocialGraphite
            )
            Box(Modifier.padding(top = 6.dp).width(40.dp).height(2.dp).background(CaixaDSColorTokens.SocialGraphite))
        }
        CircularCounterButton("+", actions.increaseSurpriseCloverQuantity)
    }
}
