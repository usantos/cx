package br.gov.caixa.loterias.feature.cart

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartSpokenDescription
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartRow
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xhdpi")
class CartWholeBlockSpeechTest {
    @get:Rule val compose = createComposeRule()

    @Test fun individualBetIsOneCompletePortugueseReadoutAndDeleteRemainsIndependent() {
        var deletions = 0
        val item = CartItem(
            1,
            CartItemKind.INDIVIDUAL_BET,
            "Lotofácil",
            "",
            BigDecimal("3.50"),
            modalityCode = "LOTOFACIL",
            contestNumber = CONTEST,
            selectedNumbers = listOf(listOf(2, SELECTED_NUMBER))
        )
        compose.setContent { CaixaDSTheme { CurrentCartRow(item, Instant.EPOCH, { deletions++ }, null) } }
        compose.onNodeWithContentDescription(
            "lotofácil. Concurso três mil e vinte e seis. Números: dois, três. Valor: três reais e cinquenta centavos."
        ).assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir").performClick()
        compose.runOnIdle { assertEquals(1, deletions) }
    }

    @Test fun surpriseReadoutDoesNotRevealMaskedNumbersOrCloversThroughComplementaryText() {
        val item = CartItem(
            1, CartItemKind.INDIVIDUAL_BET, "+Milionária", "", BigDecimal.ONE,
            modalityCode = "MAIS_MILIONARIA", selectedNumbers = listOf(listOf(HIDDEN_NUMBER)),
            selectedClovers = listOf(HIDDEN_CLOVER), badges = setOf(CartItemBadge.SURPRISE),
            complementaryLabel = "Trevos: 05"
        )
        val spoken = cartSpokenDescription(item)
        assertEquals(
            "mais milionária. Números da Surpresinha não exibidos. Trevos da Surpresinha não exibidos. surpresinha. Valor: um real.",
            spoken
        )
        assertFalse(spoken.contains("dezoito"))
        assertFalse(spoken.contains("cinco"))
    }

    @Test fun superSevenKeepsZeroAndItsColumnAssociationInTheWholeBlock() {
        val item = CartItem(
            1,
            CartItemKind.INDIVIDUAL_BET,
            "Super Sete",
            "",
            BigDecimal.ONE,
            modalityCode = "SUPER_SETE",
            selectedNumbers = List(SUPER_SEVEN_COLUMNS) { listOf(0, 2) }
        )
        compose.setContent { CaixaDSTheme { CurrentCartRow(item, Instant.EPOCH, {}, null) } }
        val columns = listOf("um", "dois", "três", "quatro", "cinco", "seis", "sete").joinToString(". ") { "Coluna $it: zero, dois" }
        compose.onNodeWithContentDescription("super sete. $columns. Valor: um real.").assertIsDisplayed()
    }

    @Test fun poolReadoutPreservesCodeZerosQuotaExpiryAndCentavosWithSeparateDetail() {
        var details = 0
        val item = CartItem(
            1,
            CartItemKind.POOL_SHARE,
            "Mega-Sena",
            "",
            BigDecimal("1.01"),
            modalityCode = "MEGA_SENA",
            poolShare = CartPoolShare("A001", 1, TOTAL_SHARES, "1970-01-01T00:01:02Z")
        )
        compose.setContent { CaixaDSTheme { CurrentCartRow(item, Instant.EPOCH, {}, { details++ }) } }
        compose.onNodeWithContentDescription(
            "mega-sena. Código do bolão: A zero zero um. Cota reservada: uma de cem. " +
                "Expira em um minuto e dois segundos. Valor: um real e um centavo."
        ).assertIsDisplayed()
        compose.onNodeWithText("Mais detalhes", ignoreCase = true).performClick()
        compose.runOnIdle { assertEquals(1, details) }
    }

    @Test fun comboReadoutIncludesQuantityAndMoneyWhileDetailRemainsIndependent() {
        var details = 0
        val item = CartItem(1, CartItemKind.COMBO, "Combo de apostas", "", BigDecimal("7.00"), comboBetCount = 2)
        compose.setContent { CaixaDSTheme { CurrentCartRow(item, Instant.EPOCH, {}, { details++ }) } }
        compose.onNodeWithContentDescription("combo de apostas. Duas apostas incluídas. Valor: sete reais.").assertIsDisplayed()
        compose.onNodeWithText("Mais detalhes", ignoreCase = true).performClick()
        compose.runOnIdle { assertEquals(1, details) }
    }
}

private const val CONTEST = 3026

private const val HIDDEN_NUMBER = 18
private const val HIDDEN_CLOVER = 5
private const val SUPER_SEVEN_COLUMNS = 7
private const val TOTAL_SHARES = 100

private const val SELECTED_NUMBER = 3
