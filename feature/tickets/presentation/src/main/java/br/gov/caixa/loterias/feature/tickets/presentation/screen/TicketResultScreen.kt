package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketResultContent
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData

@Composable
internal fun TicketResultScreen(
    bets: List<ConfirmedBet>,
    onBack: () -> Unit,
    visualData: TicketResultVisualData? = null,
    onBetNow: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resultado_da_aposta_4e6334),
        onBack = onBack,
        containerColor = LoteriasCaixaDSColorTokens.White,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("tickets.0152")) {
                Image(
                    painterResource(br.gov.caixa.loterias.feature.tickets.R.drawable.legacy_tickets_icon_duvida2),
                    uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                    Modifier.size(24.dp)
                )
            }
        },
        modifier = Modifier.uiId("tickets.0153")
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LegacyTicketResultContent(bets, visualData, onBetNow)
        }
    }
}
