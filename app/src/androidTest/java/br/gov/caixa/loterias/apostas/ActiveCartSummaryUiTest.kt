package br.gov.caixa.loterias.apostas

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.state.activeCartSummary
import br.gov.caixa.loterias.core.navigation.state.activeResumingCartSummary
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.navigation.BettingSimulationDestination
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCart
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetRepeatActionsOverlay
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** GetCurrentCart/BetDetailViewModel reais, repositório simulado, sem navegação nem acesso ao servidor. */
class ActiveCartSummaryUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private class Repository : CartRepository {
        @Volatile var snapshot = ShoppingCart(1, BigDecimal("39.00"), emptyList(), totalBetCount = 9)
        val reads = AtomicInteger()
        override suspend fun current(): ShoppingCart {
            reads.incrementAndGet()
            return snapshot
        }
        override suspend fun remove(item: CartItem): ShoppingCart = error("Unused")
        override suspend fun clear(): ShoppingCart = error("Unused")
        override suspend fun validate(): CartValidationResult = error("Unused")
        override suspend fun validateFavoriteName(name: String) = error("Unused")
        override suspend fun saveFavorite(name: String) = error("Unused")
    }

    @Test fun editSuccessClearsGlobalIntentButRetainsConsumerModelContestAndAcknowledgement() {
        val normal = LotteryGameParameters(
            LotteryModality(3, "Quina", "QUINA", specialName = "Quina de São João"),
            contestNumber = 7000, drawDate = "10/09/2026", maximumNumber = 80,
            minimumSelection = 5, maximumSelection = 5, maximumSurprises = 3,
            recurringOptions = listOf(2), prices = listOf(BetPrice(5, 0, BigDecimal("3.00")))
        )
        val special = normal.copy(contestNumber = 7325, contestType = "ESPECIAL")
        val parameters = BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(normal, special))
        var pendingEdit by mutableStateOf<ConfirmedBet?>(
            ConfirmedBet(
                41,
                "Quina",
                7325,
                "",
                "",
                listOf(1, 2, 3, 4, 5),
                modalityId = 3,
                contestTypeId = 2
            )
        )
        val identity = Any()
        var consumerVisible by mutableStateOf(true)
        val created = mutableListOf<BettingGameViewModel>()
        val submitted = mutableListOf<BetDraft>()
        val repository = Repository()
        val getCart = GetCurrentCart(repository)
        compose.setContent {
            CaixaDSTheme {
                if (consumerVisible) {
                    BettingSimulationDestination(
                        destinationIdentity = identity, modalityId = 3, viewModelKey = "edit-regression",
                        pendingEdit = pendingEdit,
                        createViewModel = { seed ->
                            BettingGameViewModel(
                                3,
                                GetBettingParametersUseCase { AppResult.Success(parameters) },
                                AddBetToCartUseCase {
                                    submitted.add(it)
                                    AppResult.Success(repository.snapshot)
                                },
                                FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                                initialNumbers = seed?.numbers.orEmpty(),
                                initialContestTypeId = seed?.contestTypeId ?: 0
                            ).also { created.add(it) }
                        },
                        getCurrentCart = getCart, clearPendingEdit = { pendingEdit = null },
                        onBack = {}, onHelp = {}, onCart = {}
                    )
                }
            }
        }
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            created.size ==
                1 &&
                !created.first().state.value.loading
        }
        compose.onNodeWithText("7325").assertIsDisplayed()
        compose.runOnIdle { created.first().addToCart() }
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            pendingEdit ==
                null
        }
        compose.waitForIdle()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.onNodeWithText("7325").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, created.size)
            assertTrue(created.single().state.value.selectedNumbers.isEmpty())
            assertEquals("ESPECIAL", submitted.single().contestType)
        }
        compose.onNodeWithText("Entendi").performClick()
        assertRetainedConsumerAfterReentry(created, submitted) { consumerVisible = it }
    }

    private fun assertRetainedConsumerAfterReentry(
        created: List<BettingGameViewModel>,
        submitted: List<BetDraft>,
        setVisible: (Boolean) -> Unit,
    ) {
        // Sair da composição e entrar novamente deve preservar o modelo existente do proprietário do destino.
        compose.runOnIdle { setVisible(false) }
        compose.waitForIdle()
        compose.runOnIdle { setVisible(true) }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, created.size)
            val model = created.single()
            assertFalse(model.state.value.addedToCart)
            model.selectMode(BetEntryMode.SURPRISE)
            model.addToCart()
        }
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            submitted.size ==
                2
        }
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.onNodeWithText("7325").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, created.size)
            assertEquals("ESPECIAL", submitted.last().contestType)
            assertTrue(submitted.last().surprise)
            assertTrue(submitted.last().numbers.isEmpty())
        }
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
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            repository.reads.get() >=
                1
        }
        awaitCartSummary("R$ 39,00", "9")
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        val previousReads = repository.reads.get()
        repository.snapshot = ShoppingCart(1, BigDecimal("57.00"), emptyList(), totalBetCount = 10)
        compose.runOnIdle { revision++ }
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            repository.reads.get() >
                previousReads
        }
        awaitCartSummary("R$ 57,00", "10")
        compose.onNodeWithText("R$ 57,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("10", useUnmergedTree = true).assertIsDisplayed()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.STARTED)
        repository.snapshot = ShoppingCart(1, BigDecimal.ZERO, emptyList(), totalBetCount = 0)
        val readsBeforeReturn = repository.reads.get()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            repository.reads.get() >
                readsBeforeReturn
        }
        // O resumo compartilhado desaparece intencionalmente quando o carrinho está vazio.
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            compose.onAllNodesWithText("R$ 57,00", useUnmergedTree = true).fetchSemanticsNodes().isEmpty()
        }
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
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            repository.reads.get() >=
                1
        }
        awaitCartSummary("R$ 39,00", "9")
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.STARTED)
        repository.snapshot = ShoppingCart(1, BigDecimal("45.00"), emptyList(), totalBetCount = 10)
        val previousReads = repository.reads.get()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            repository.reads.get() >
                previousReads
        }
        awaitCartSummary("R$ 45,00", "10")
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
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            repository.reads.get() ==
                1
        }
        awaitCartSummary("R$ 39,00", "9")
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        repository.snapshot = ShoppingCart(1, BigDecimal("45.00"), emptyList(), totalBetCount = 10)
        compose.onNodeWithText("Atualizar carrinho").performClick()
        awaitCartSummary("R$ 45,00", "10")
        compose.onNodeWithText("R$ 45,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("10", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, repository.reads.get()) }
    }

    @Test fun successfulRepeatInvalidatesSummaryOnlyAfterAcknowledgment() {
        val repository = Repository()
        var changes = 0
        val bet = ConfirmedBet(0, "Mega-Sena", 3070, "", "", listOf(1, 2, 3, 4, 5, 6), modalityId = 2)
        val model = BetDetailViewModel(
            bet,
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            AddBetToCartUseCase {
                repository.snapshot = ShoppingCart(1, BigDecimal("45.00"), emptyList(), totalBetCount = 10)
                AppResult.Success(repository.snapshot)
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        )
        val getCart = GetCurrentCart(repository)
        var refresh by mutableIntStateOf(0)
        compose.setContent {
            CaixaDSTheme {
                val snapshot = activeCartSummary(getCart, refresh)
                Box {
                    CaixaDSCartSummary(snapshot?.total ?: BigDecimal.ZERO, snapshot?.betCount ?: 0)
                    TextButton(onClick = { model.addCurrentBetToCart() }) { Text("Repetir aposta") }
                    BetRepeatActionsOverlay(model, onCartChanged = {
                        changes++
                        refresh++
                    })
                }
            }
        }
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            repository.reads.get() ==
                1
        }
        compose.onNodeWithText("Repetir aposta").performClick()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, changes)
            assertEquals(1, repository.reads.get())
        }
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertFalse(model.state.value.cartAdditionConfirmed)
            assertEquals(1, changes)
        }
        awaitCartSummary("R$ 45,00", "10")
        compose.onNodeWithText("R$ 45,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("10", useUnmergedTree = true).assertIsDisplayed()
    }

    // A leitura ocorre em IO; a ociosidade do Compose e a contagem de leituras não aguardam a publicação em Main.
    private fun awaitCartSummary(amount: String, count: String) {
        compose.waitUntil {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            compose.onAllNodesWithText(amount, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() &&
                compose.onAllNodesWithText(count, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
