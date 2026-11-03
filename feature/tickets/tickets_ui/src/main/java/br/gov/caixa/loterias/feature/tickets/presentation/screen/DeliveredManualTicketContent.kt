package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDataCard
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun ManualTicketContentDelivered(
    state: TicketsUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    manualCodeValid: Boolean,
    actions: ManualTicketActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_conferir_bilhetes_1465ae),
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("tickets.0169")) {
                Icon(
                    LoteriasCaixaDSIcons.Help,
                    contentDescription = uiStrings.getString(DeliveryR.string.ui_termos_de_uso_a6f04f),
                    tint = LoteriasCaixaDSColorTokens.White,
                    modifier = Modifier.uiId("tickets.0170")
                )
            }
        },
        modifier = Modifier.uiId("tickets.0171")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
                .uiId("tickets.0172").verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            ManualTicketFieldsDelivered(state, manualCodeValid, actions)
            Button(
                onClick = { actions.checkTicket() },
                enabled = manualCodeValid,
                modifier = (Modifier.fillMaxWidth().padding(top = 49.dp).height(55.dp)).uiId("tickets.0176"),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoteriasCaixaDS.colors.teal,
                    contentColor = LoteriasCaixaDSColorTokens.White,
                    disabledContainerColor = LoteriasCaixaDSColorTokens.TicketDisabledBackground,
                    disabledContentColor = LoteriasCaixaDSColorTokens.TicketDisabledContent
                )
            ) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_continuar_63e2bb),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("tickets.0177")
                )
            }
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))

                state.errorMessage != null -> Text(
                    state.errorMessage.orEmpty(),
                    color = LoteriasCaixaDS.colors.error,
                    modifier = Modifier
                        .uiId("tickets.0178")
                )

                state.loaded && state.bets.isEmpty() -> Text(
                    uiStrings.getString(
                        DeliveryR.string.ui_nenhuma_aposta_encontrada_neste_bilh_97edde
                    ),
                    color = LoteriasCaixaDS.colors.mutedText,
                    modifier = Modifier.uiId("tickets.0179")
                )

                else -> state.bets.forEach { BetDataCard(it) {} }
            }
        }
    }
}
