package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import java.math.BigDecimal
import java.time.Instant
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class CartAdditionIntegrationTest {
    @get:Rule val compose = createComposeRule()
    private val calls = mutableListOf<String>()

    @Test fun individualConfirmationShowsActualGameAndAmountAndRoutesActions() {
        show(CartAdditionKind.BET)
        compose.onNodeWithText("mega-sena").assertIsDisplayed()
        compose.onNodeWithText("2800").assertIsDisplayed()
        compose.onNodeWithText("Lembre-se: o valor mínimo", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Aposta inserida no\ncarrinho com sucesso.").assertIsDisplayed()
        compose.onNodeWithText("5,00", substring = true, useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.onNodeWithText("Carrinho\nde apostas", useUnmergedTree = true).performClick()
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(listOf("home", "cart", "help:2", "back"), calls) }
    }

    @Test fun poolConfirmationShowsReservationAndRoundingNotice() {
        show(CartAdditionKind.POOL_SHARE)
        compose.onNodeWithText("Bolão inserido no\ncarrinho com sucesso.").assertIsDisplayed()
        compose.onNodeWithText("Conclua o pagamento de suas\napostas dentro do prazo").assertIsDisplayed()
        compose.onNodeWithText("Devido a regras de arredondamento,\no valor cobrado pela Cota deste bolão\npoderá ser menor que o exibido.").assertIsDisplayed()
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.runOnIdle { assertEquals(listOf("home"), calls) }
    }

    private fun show(kind: CartAdditionKind) {
        compose.setContent {
            val model = remember {
                CartAdditionViewModel(kind, ServerTimeSource { Instant.parse("2026-09-09T12:00:00Z") },
                    expiresAt = "2026-09-09T12:15:00Z", modality = "MEGA_SENA", contest = 2800,
                    drawDate = "10/09/2026", amount = BigDecimal("5.00"), minimumCartAmount = BigDecimal("30"))
            }
            CaixaDSTheme(darkTheme = false) {
                CartAdditionRoute(model, onBack = { calls.add("back") }, onHome = { calls.add("home") }, onOpenCart = { calls.add("cart") }, onHelp = { id, _ -> calls.add("help:$id") })
            }
        }
    }
}
