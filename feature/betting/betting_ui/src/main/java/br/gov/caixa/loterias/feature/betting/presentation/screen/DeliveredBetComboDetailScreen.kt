package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSContentState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.components.ComboDetailEntryCard
import br.gov.caixa.loterias.feature.betting.presentation.components.ComboDetailHeader
import br.gov.caixa.loterias.feature.betting.presentation.components.ComboHelpAction
import br.gov.caixa.loterias.feature.betting.presentation.components.ComboNumberPreviewOption
import br.gov.caixa.loterias.feature.betting.presentation.components.ComboPurchasePanel
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
fun BetComboDetailScreenDelivered(
    state: BettingCatalogUiState,
    onBack: () -> Unit,
    cartTotal: BigDecimal,
    cartBetCount: Int,
    onCart: (() -> Unit)?,
    onRetry: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onAddCombo: () -> Unit,
    onToggleSurprise: () -> Unit,
    onHelp: () -> Unit = {},
) {
    val resources = LocalResources.current
    LoteriasCaixaDSScaffold(
        title = resources.getString(DeliveryR.string.combo_screen_title),
        onBack = onBack,
        gradientTopBar = false,
        topBarStartColor = LoteriasCaixaDSColorTokens.Blue700,
        topBarEndColor = LoteriasCaixaDSColorTokens.Blue700,
        topBarActionWidth = 50.dp,
        topBarAction = { ComboHelpAction(onHelp) },
        bottomBar = { LoteriasCaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart, modifier = Modifier.uiId("bet.0221")) },
        modifier = Modifier.uiId("bet.0222")
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.ComboDetailCanvas)) {
            LoteriasCaixaDSContentState(state.loading, state.errorMessage, PaddingValues(0.dp), onRetry) {
                LazyColumn((Modifier.fillMaxSize().padding(bottom = 36.dp)).uiId("bet.0223")) {
                    state.selectedCombo?.let { combo ->
                        item { ComboDetailHeader(combo) }
                        items(combo.entries.chunked(2)) { entries ->
                            Row(Modifier.fillMaxWidth().padding(horizontal = 17.dp)) {
                                entries.forEach { entry ->
                                    Box(Modifier.weight(1f)) { ComboDetailEntryCard(entry) }
                                }
                            }
                        }
                        item { ComboNumberPreviewOption(state.surprise, onToggleSurprise) }
                    }
                }
            }
            ComboPurchasePanel(
                state,
                onDecreaseQuantity,
                onIncreaseQuantity,
                onAddCombo,
                Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp).fillMaxWidth()
            )
        }
    }
}
