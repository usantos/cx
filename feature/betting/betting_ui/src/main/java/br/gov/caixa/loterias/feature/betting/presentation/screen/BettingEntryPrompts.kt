package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun SurpriseMaskedNumbersDelivered() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(DeliveryR.string.ui_na_surpresinha_os_numeros_ficam_masc_e7ef11),
        (Modifier.padding(top = SectionGap)).uiId("bet.0239"),
        fontFamily = LoteriasCaixaDSFontFamily,
        fontSize = ContentInsetFontSize,
        color = LoteriasCaixaDSColorTokens.SocialGraphite,
        textAlign = TextAlign.Center,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    )
    FlowRow(Modifier.fillMaxWidth().padding(horizontal = ContentInset, vertical = IconSize), horizontalArrangement = Arrangement.Center) {
        repeat(MASKED_NUMBER_COUNT) {
            Box(
                Modifier.padding(
                    ControlPadding
                ).size(
                    ControlSize
                ).background(
                    LoteriasCaixaDSColorTokens.White,
                    CircleShape
                ).border(1.dp, LoteriasCaixaDSColorTokens.SimulationMuted, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_xx_ecc76c),
                    fontWeight = FontWeight.Bold,
                    fontSize = ControlTextFontSize,
                    color = LoteriasCaixaDSColorTokens.SimulationMuted,
                    modifier = Modifier.uiId("bet.0240")
                )
            }
        }
    }
}

@Composable
fun ManualNumberPromptDelivered(state: BettingGameUiState, promptInset: androidx.compose.ui.unit.Dp) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        if (state.selectedNumbers.isEmpty()) {
            uiStrings.getString(
                DeliveryR.string.ui_selecione_os_numeros_44f9f7
            )
        } else {
            uiStrings.getString(
                DeliveryR.string.ui_selecionados_1_s_2_s_0a48ce,
                state.selectedNumbers.size.toString(),
                state.numberQuantity.toString()
            )
        },
        (Modifier.padding(start = promptInset, top = ContentInset, bottom = ContentGap)).uiId("bet.0235"),
        fontFamily = LoteriasCaixaDSFontFamily,
        fontSize = ContentInsetFontSize,
        color = LoteriasCaixaDSColorTokens.SocialGraphite,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    )
}

private val ControlTextFontSize = 14.sp
private val ContentInset = 16.dp
private val ContentInsetFontSize = 16.sp
private val SectionGap = 20.dp
private val IconSize = 24.dp
private val ControlPadding = 4.dp
private val ControlSize = 40.dp
private val ContentGap = 8.dp

private const val MASKED_NUMBER_COUNT = 6
