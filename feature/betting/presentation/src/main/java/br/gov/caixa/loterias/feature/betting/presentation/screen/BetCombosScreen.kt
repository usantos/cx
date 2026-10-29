package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSContentState
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.presentation.components.ComboHelpAction
import br.gov.caixa.loterias.feature.betting.presentation.components.LegacyComboCatalogCard
import br.gov.caixa.loterias.feature.betting.presentation.mapper.ComboTextSize
import br.gov.caixa.loterias.feature.betting.presentation.mapper.comboText
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import java.math.BigDecimal

@Composable
internal fun BetCombosScreen(
    state: BettingCatalogUiState,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit,
    cartTotal: BigDecimal,
    cartBetCount: Int,
    onCart: (() -> Unit)?,
    onRetry: () -> Unit,
    onHelp: () -> Unit = {},
) {
    val resources = LocalResources.current
    CaixaDSScaffold(
        title = resources.getString(R.string.combo_screen_title),
        onBack = onBack,
        gradientTopBar = false,
        topBarStartColor = CaixaDSColorTokens.Blue700,
        topBarEndColor = CaixaDSColorTokens.Blue700,
        topBarActionWidth = 50.dp,
        topBarAction = { ComboHelpAction(onHelp) },
        bottomBar = { CaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart, modifier = Modifier.uiId("bet.0224")) },
        modifier = Modifier.uiId("bet.0225")
    ) { padding ->
        CaixaDSContentState(state.loading, state.errorMessage, padding, onRetry) {
            Column(
                Modifier.fillMaxSize().padding(padding)
                    .background(Brush.linearGradient(listOf(CaixaDSColorTokens.White, CaixaDSColorTokens.ComboGradientEnd)))
            ) {
                Text(
                    resources.getString(R.string.ui_confira_nossos_combos_e_escolha_o_se_9bbe75),
                    (Modifier.align(Alignment.CenterHorizontally).padding(top = 15.dp, bottom = 7.dp)).uiId("bet.0226"),
                    style = comboText(ComboTextSize.BODY).copy(lineHeight = ComboTextSize.CATALOG_LINE_HEIGHT.sp),
                    color = CaixaDSColorTokens.ComboDescription,
                    textAlign = TextAlign.Start
                )
                LazyColumn(
                    (Modifier.weight(1f).fillMaxWidth()).uiId("bet.0227"),
                    contentPadding = PaddingValues(start = 25.dp, end = 25.dp, bottom = 10.dp)
                ) {
                    items(state.combos.chunked(2)) { row ->
                        Row(Modifier.fillMaxWidth()) {
                            row.forEach { combo ->
                                Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                                    LegacyComboCatalogCard(combo) { onSelect(combo.id) }
                                }
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                    if (state.combos.isEmpty()) {
                        item {
                            Text(
                                resources.getString(R.string.ui_nenhum_combo_disponivel_no_momento_d7e649),
                                style = comboText(ComboTextSize.LABEL),
                                modifier = Modifier.uiId("bet.0228")
                            )
                        }
                    }
                }
            }
        }
    }
}
