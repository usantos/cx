package br.gov.caixa.loterias.apostas

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.cart.*
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.retail.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.core.domain.tickets.*
import br.gov.caixa.loterias.feature.tickets.BetDetailViewModel
import br.gov.caixa.loterias.feature.tickets.BetRepeatActionsOverlay
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Real GetCurrentCart/BetDetailViewModel, synthetic repository, no navigation or backend. */
class ActiveCartSummaryUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private class Repository : CartRepository {
        @Volatile var snapshot = ShoppingCart(1, BigDecimal("39.00"), emptyList(), totalBetCount = 9)
        val reads = AtomicInteger()
        override suspend fun current(): ShoppingCart { reads.incrementAndGet(); return snapshot }
        override suspend fun remove(item: CartItem): ShoppingCart = error("Unused")
        override suspend fun clear(): ShoppingCart = error("Unused")
        override suspend fun validate(): CartValidationResult = error("Unused")
        override suspend fun validateFavoriteName(name: String) = error("Unused")
        override suspend fun saveFavorite(name: String) = error("Unused")
    }

    @Test fun simulatorAdditionRefreshAndReturnAfterDeletionReadCurrentCart() {
        val repository = Repository()
        val getCart = GetCurrentCart(repository)
        var revision by mutableIntStateOf(0)
        compose.setContent {
            CaixaDSTheme {
                val snapshot = activeResumingCartSummary(getCart, revision)
                CaixaDSCartSummary(snapshot?.total ?: BigDecimal.ZERO, snapshot?.betCount ?: 0)
            }
        }
        compose.waitUntil { repository.reads.get() >= 1 }
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        val previousReads = repository.reads.get()
        repository.snapshot = ShoppingCart(1, BigDecimal("57.00"), emptyList(), totalBetCount = 10)
        compose.runOnIdle { revision++ }
        compose.waitUntil { repository.reads.get() > previousReads }
        compose.onNodeWithText("R$ 57,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("10", useUnmergedTree = true).assertIsDisplayed()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.STARTED)
        repository.snapshot = ShoppingCart(1, BigDecimal.ZERO, emptyList(), totalBetCount = 0)
        val readsBeforeReturn = repository.reads.get()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
        compose.waitUntil { repository.reads.get() > readsBeforeReturn }
        // The shared summary intentionally disappears when the cart is empty.
        compose.waitUntil { compose.onAllNodesWithText("R$ 57,00", useUnmergedTree = true).fetchSemanticsNodes().isEmpty() }
        compose.onNodeWithText("R$ 57,00", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test fun returningToResumedMarketplaceReloadsCurrentCart() {
        val repository = Repository()
        val getCart = GetCurrentCart(repository)
        compose.setContent {
            CaixaDSTheme {
                val snapshot = activeResumingCartSummary(getCart)
                CaixaDSCartSummary(snapshot?.total ?: BigDecimal.ZERO, snapshot?.betCount ?: 0)
            }
        }
        compose.waitUntil { repository.reads.get() >= 1 }
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.STARTED)
        repository.snapshot = ShoppingCart(1, BigDecimal("45.00"), emptyList(), totalBetCount = 10)
        val previousReads = repository.reads.get()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
        compose.waitUntil { repository.reads.get() > previousReads }
        compose.onNodeWithText("R$ 45,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("10", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test fun refreshKeyUpdatesExistingDestinationAmountAndCount() {
        val repository = Repository()
        val getCart = GetCurrentCart(repository)
        var refresh by mutableIntStateOf(0)
        compose.setContent {
            CaixaDSTheme {
                val snapshot = activeCartSummary(getCart, refresh)
                Box {
                    CaixaDSCartSummary(snapshot?.total ?: BigDecimal.ZERO, snapshot?.betCount ?: 0)
                    TextButton(onClick = { refresh++ }) { Text("Atualizar carrinho") }
                }
            }
        }
        compose.waitUntil { repository.reads.get() == 1 }
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        repository.snapshot = ShoppingCart(1, BigDecimal("45.00"), emptyList(), totalBetCount = 10)
        compose.onNodeWithText("Atualizar carrinho").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("R$ 45,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("10", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, repository.reads.get()) }
    }

    @Test fun successfulRepeatInvalidatesSummaryOnlyAfterAcknowledgment() {
        val repository = Repository()
        var changes = 0
        val bet = ConfirmedBet(0, "Mega-Sena", 3070, "", "", listOf(1,2,3,4,5,6), modalityId = 2)
        val model = BetDetailViewModel(bet,
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            AddBetToCartUseCase {
                repository.snapshot = ShoppingCart(1, BigDecimal("45.00"), emptyList(), totalBetCount = 10)
                AppResult.Success(repository.snapshot)
            }, FixedCoroutineDispatchers(Dispatchers.Main.immediate))
        val getCart = GetCurrentCart(repository)
        var refresh by mutableIntStateOf(0)
        compose.setContent {
            CaixaDSTheme {
                val snapshot = activeCartSummary(getCart, refresh)
                Box {
                    CaixaDSCartSummary(snapshot?.total ?: BigDecimal.ZERO, snapshot?.betCount ?: 0)
                    TextButton(onClick = { model.addCurrentBetToCart() }) { Text("Repetir aposta") }
                    BetRepeatActionsOverlay(model, onCartChanged = { changes++; refresh++ })
                }
            }
        }
        compose.waitUntil { repository.reads.get() == 1 }
        compose.onNodeWithText("Repetir aposta").performClick()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, changes); assertEquals(1, repository.reads.get()) }
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertFalse(model.state.value.cartAdditionConfirmed); assertEquals(1, changes) }
        compose.waitForIdle()
        compose.onNodeWithText("R$ 45,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("10", useUnmergedTree = true).assertIsDisplayed()
    }
}
