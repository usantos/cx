package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingLegacyNumberGrid
import br.gov.caixa.loterias.feature.betting.presentation.components.legacyPromptInset
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun ManualBetContentDelivered(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val game = requireNotNull(state.game)
    val promptInset = legacyPromptInset()
    Column(
        Modifier.fillMaxSize().background(LoteriasCaixaDS.colors.surface)
            .padding(horizontal = ContentGap)
    ) {
        if (state.selectionStep == BetSelectionStep.COMPLEMENT) {
            // A CX mantém textSelecionados visível mesmo quando TREVOS define seu texto como vazio.
            // textSelecionados possui margem de 16dp abaixo de layoutFiltros, dentro da borda.
            val complementPromptTop = ContentInset
            Text(
                when (game.modality.id) {
                    TIMEMANIA_ID -> uiStrings.getString(DeliveryR.string.ui_escolha_o_time_do_coracao_d62c54)
                    DIA_DE_SORTE_ID -> uiStrings.getString(DeliveryR.string.ui_escolha_o_mes_de_sorte_73d774)
                    else -> ""
                },
                (Modifier.padding(start = promptInset, top = complementPromptTop, bottom = ContentGap))
                    .uiId("bet.0233"),
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = ContentInsetFontSize,
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
            Column(Modifier.weight(1f).fillMaxWidth().uiId("bet.0234").verticalScroll(rememberScrollState())) {
                when (game.modality.id) {
                    TIMEMANIA_ID -> teamContent()
                    DIA_DE_SORTE_ID -> LuckyMonthContentDelivered(state, actions)
                    MAIS_MILIONARIA_ID -> CloverSelectionContentDelivered(state, actions)
                }
                Spacer(Modifier.height(ContentInset))
            }
        } else if (game.modality.id == SUPER_SEVEN_ID) {
            SuperSevenMatrixDelivered(state, actions)
        } else {
            ManualNumberPromptDelivered(state, promptInset)
            // textSelecionados pertence à atividade; somente o corpo do fragmento rola.
            Column(Modifier.weight(1f).fillMaxWidth().uiId("bet.0236").verticalScroll(rememberScrollState())) {
                // Bilhetes curtos da Lotofácil e do Dia de Sorte mantêm a margem nativa de cltGeral.
                if (game.modality.id == LOTOFACIL_MODALITY_ID || game.modality.id == DIA_DE_SORTE_ID) Spacer(Modifier.height(ContentInset))
                BettingLegacyNumberGrid(
                    numbers = (1..game.maximumNumber).toList(),
                    selected = state.selectedNumbers,
                    onNumberClick = actions.toggleNumber,
                    modifier = (Modifier.fillMaxWidth()).uiId("bet.0237"),
                    modalityId = game.modality.id
                )
                Spacer(Modifier.height(ContentInset))
            }
        }
    }
}

@Composable
fun SurpriseBetContentDelivered(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().background(LoteriasCaixaDS.colors.surface)
            .uiId("bet.0238").verticalScroll(rememberScrollState()).padding(bottom = LabelTopGap),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SurpriseMaskedNumbersDelivered()
        val plusMillion = state.game?.modality?.id == MAIS_MILIONARIA_ID
        Text(
            if (plusMillion) {
                uiStrings.getString(DeliveryR.string.ui_escolha_a_quantidade_de_numeros_f01f9e)
            } else {
                uiStrings.getString(
                    DeliveryR.string.ui_voce_ainda_pode_escolher_quantos_jog_b63430
                )
            },
            (Modifier.padding(start = PromptHorizontalInset, end = PromptHorizontalInset, top = SectionGap)).uiId("bet.0241"),
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = ContentInsetFontSize,
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        SurpriseNumberQuantityDelivered(state, actions, plusMillion)
        if (state.game?.modality?.id == TIMEMANIA_ID) {
            Spacer(Modifier.height(SectionGap))
            teamContent()
        }
        if (state.game?.modality?.id == DIA_DE_SORTE_ID) {
            Spacer(Modifier.height(SectionGap))
            LuckyMonthContentDelivered(state, actions)
        }
        if (plusMillion) {
            state.game?.let { game -> SurpriseCloverQuantityDelivered(state, actions, game.minimumClovers) }
        }
        if (state.game?.acceptsMirror == true) {
            BettingLegacyMirrorDelivered(state.generateMirror, actions.toggleMirror, manual = false)
        }
    }
}

private val LabelTopGap = 10.dp
private val ContentInset = 16.dp
private val ContentInsetFontSize = 16.sp
private val SectionGap = 20.dp
private val PromptHorizontalInset = 45.dp
private val ContentGap = 8.dp

private const val LOTOFACIL_MODALITY_ID = 8
