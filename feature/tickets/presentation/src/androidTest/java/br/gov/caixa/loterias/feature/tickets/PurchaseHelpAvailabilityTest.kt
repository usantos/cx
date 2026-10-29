package br.gov.caixa.loterias.feature.tickets

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailContent
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PurchaseHelpAvailabilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun helpIsHiddenWithoutDestinationAndInvokesAvailableDestination() {
        val purchase = Purchase(1, "22/10/2026", "Confirmada", "Pix", BigDecimal.TEN)
        val help = mutableStateOf<(() -> Unit)?>(null)
        var clicks = 0
        compose.setContent {
            CaixaDSTheme {
                PurchaseDetailContent(
                    state = TicketsUiState(selectedPurchase = purchase),
                    purchase = purchase,
                    onBack = {},
                    actions = PurchaseDetailActions(
                        openPurchaseBetFilters = {},
                        loadPurchase = {},
                        openFavoriteDialog = {},
                        pendingPixSecondsRemaining = { 0 },
                        checkPendingPixStatus = {},
                        clearPurchaseBetFilters = {},
                        openHelp = help.value
                    )
                )
            }
        }
        compose.onNodeWithContentDescription("Ajuda").assertDoesNotExist()
        compose.runOnIdle { help.value = { clicks++ } }
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.runOnIdle { assertEquals(1, clicks) }
    }
}
