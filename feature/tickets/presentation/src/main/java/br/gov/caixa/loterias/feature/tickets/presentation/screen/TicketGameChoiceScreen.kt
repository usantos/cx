package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun TicketGameChoiceScreen(
    state: TicketsUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onSave: (Set<Int>) -> Unit,
    onToggleModality: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_conferir_aposta_c2b3fa),
        onBack = onBack,
        topBarAction = {
            Icon(
                LoteriasCaixaDSIcons.Help,
                contentDescription = uiStrings.getString(LocalizedR.string.ui_termos_de_uso_e259be),
                modifier = (Modifier.fillMaxSize().uiId("tickets.0141").clickable(onClick = onHelp).padding(12.dp)).uiId("tickets.0142"),
                tint = LoteriasCaixaDSColorTokens.White
            )
        },
        bottomBar = {
            TicketChoiceSave(state.selectedTicketModalityIds, onSave)
        },
        modifier = Modifier.uiId("tickets.0145")
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LoteriasCaixaDS.colors.teal)
            }

            else -> Column(Modifier.fillMaxSize().padding(padding)) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_conferir_aposta_c2b3fa),
                    (Modifier.fillMaxWidth().padding(top = 40.dp)).uiId("tickets.0146"),
                    color = LoteriasCaixaDS.colors.teal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_selecione_a_aposta_realizada_a33b3d),
                    (Modifier.width(130.dp).padding(top = 24.dp).align(Alignment.CenterHorizontally)).uiId("tickets.0147"),
                    color = LoteriasCaixaDS.colors.graphite,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(top = 19.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(17.dp)
                ) {
                    items(state.ticketModalities, key = LotteryModality::id) { modality ->
                        TicketModalityChoice(
                            modality = modality,
                            selected = modality.id in state.selectedTicketModalityIds,
                            onClick = { onToggleModality(modality.id) }
                        )
                    }
                }
            }
        }
    }
    state.errorMessage?.let {
        LoteriasCaixaDSAlertDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = it,
            confirmText = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onConfirm = onRetry
        )
    }
}
