package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState

@Composable
internal fun RapidListFooter(state: RapidListUiState, onGenerate: () -> Unit, onAdvance: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxWidth().height(99.dp).background(LoteriasCaixaDSColorTokens.BetFooter)) {
        Column(
            Modifier.align(Alignment.CenterEnd).padding(end = 10.dp).width(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                formatBetAmount(state.cart?.total),
                fontFamily = LoteriasCaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = LoteriasCaixaDS.colors.mutedText,
                maxLines = 1,
                modifier = Modifier.uiId("bet.0184")
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_total_c9b3c3),
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = 12.sp,
                color = LoteriasCaixaDS.colors.brand,
                modifier = Modifier.uiId("bet.0185")
            )
        }
        Box(
            Modifier.align(Alignment.BottomCenter).offset(y = (-156).dp).width(183.dp).height(56.dp)
                .background(LoteriasCaixaDSColorTokens.White).border(1.dp, LoteriasCaixaDS.colors.teal)
                .uiId("bet.0186").clickable { onGenerate() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_fazer_nova_aposta_7c7b2f),
                fontFamily = LoteriasCaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = LoteriasCaixaDS.colors.teal,
                modifier = Modifier.uiId("bet.0187")
            )
        }
        Box(
            Modifier.align(Alignment.BottomCenter).offset(y = (-64).dp).width(184.dp).height(84.dp)
                .background(LoteriasCaixaDS.colors.teal).uiId("bet.0188").clickable(onClick = onAdvance),
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_avancar_para_a_forma_de_pagamento_64482f),
                (Modifier.padding(horizontal = 14.dp)).uiId("bet.0189"),
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = 17.sp,
                color = LoteriasCaixaDSColorTokens.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
