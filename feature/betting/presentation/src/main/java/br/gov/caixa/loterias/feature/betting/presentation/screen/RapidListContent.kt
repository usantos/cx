package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidBetCard
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

@Composable
internal fun RapidListContent(state: RapidListUiState, padding: PaddingValues, onGenerate: () -> Unit, onRemove: (CartItem) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    when {
        state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = LoteriasCaixaDS.colors.brand)
        }

        state.cart?.items.isNullOrEmpty() -> Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_nenhuma_aposta_foi_gerada_1e46c4),
                color = LoteriasCaixaDS.colors.mutedText,
                modifier = Modifier.uiId("bet.0191")
            )
            TextButton(
                onClick = { onGenerate() },
                modifier = Modifier
                    .uiId("bet.0192")
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                    modifier = Modifier
                        .uiId("bet.0193")
                )
            }
        }

        else -> LazyColumn(
            (Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)).uiId("bet.0194"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                RapidListIntroduction()
            }
            items(requireNotNull(state.cart).items, key = CartItem::id) { item ->
                RapidBetCard(item) { onRemove(item) }
            }
        }
    }
}

@Composable
private fun RapidListIntroduction() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_o_jeito_mais_facil_para_apostar_1a2d05),
        (Modifier.padding(start = 17.dp, top = 40.dp, end = 90.dp)).uiId("bet.0195"),
        color = LoteriasCaixaDS.colors.brand,
        fontFamily = LoteriasCaixaDSFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.2.sp
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_o_seu_pacote_de_apostas_prontas_para_30c098),
        (Modifier.padding(start = 17.dp, top = 20.dp, end = 17.dp)).uiId("bet.0196"),
        color = LoteriasCaixaDS.colors.text,
        fontFamily = LoteriasCaixaDSFontFamily,
        fontSize = 14.sp
    )
    Spacer(Modifier.height(20.dp))
}
