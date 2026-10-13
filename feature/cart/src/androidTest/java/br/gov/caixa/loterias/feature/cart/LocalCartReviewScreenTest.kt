package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.account.*
import br.gov.caixa.loterias.core.domain.betting.BetDraft
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class LocalCartReviewScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun comparesPendingLocalBetsWithRemoteWithoutResendingOrShowingNewLocalBets() {
        val fixture = Fixture()
        show(fixture)
        compose.onNodeWithText("Apostas preservadas").assertIsDisplayed()
        compose.onNodeWithText("Carrinho da conta").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Aposta da conta").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Apostas locais preservadas").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Aposta preservada").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Nova aposta local").assertDoesNotExist()
        compose.onNodeWithText("Conferir carrinho da conta").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(listOf("remote", "back"), fixture.navigation)
            assertEquals(0, fixture.sends)
            assertEquals(0, fixture.discards)
            assertEquals(listOf(1L, 3L), fixture.localCart.items.map { it.id })
        }
    }

    @Test fun deletingRequiresConfirmationAndKeepsRemoteCartAndNewLocalBets() {
        val fixture = Fixture()
        show(fixture)
        deleteButton().performScrollTo().performClick()
        compose.onNodeWithText("Deseja excluir as apostas locais mostradas nesta tela? Esta ação não exclui as apostas do carrinho da sua conta.").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(0, fixture.discards) }
        deleteButton().performScrollTo().performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("As apostas preservadas foram excluídas. O carrinho da conta foi mantido.").performScrollTo().assertIsDisplayed()
        deleteButton().assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, fixture.discards)
            assertEquals(0, fixture.sends)
            assertEquals(listOf(3L), fixture.localCart.items.map { it.id })
            assertEquals(listOf(2L), fixture.remoteCart.items.map { it.id })
            assertEquals(fixture.remoteCart, fixture.contents.value!!.cart)
            assertFalse(fixture.contents.value!!.localCartReviewRequired)
        }
    }

    @Test fun failedRemoteQueryHidesDeletionUntilSuccessfulRetry() {
        val fixture = Fixture().apply { failRemote = true }
        show(fixture)
        compose.onNodeWithText("Não foi possível consultar as apostas. Entre na conta utilizada no envio e tente novamente.").performScrollTo().assertIsDisplayed()
        deleteButton().assertDoesNotExist()
        compose.runOnIdle {
            fixture.model.discard()
            assertEquals(0, fixture.discards)
            assertTrue(fixture.contents.value!!.localCartReviewRequired)
            fixture.failRemote = false
        }
        compose.onNodeWithText("Atualizar consulta").performScrollTo().performClick()
        deleteButton().performScrollTo().assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(2, fixture.queries)
            assertEquals(0, fixture.discards)
            assertEquals(0, fixture.sends)
        }
    }

    @Test fun staleDeletionShowsErrorAndCanOnlyBeConfirmedAgainAfterRefreshing() {
        val fixture = Fixture().apply { allowDiscard = false }
        show(fixture)
        deleteButton().performScrollTo().performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("As apostas ou a sessão foram alteradas. Atualize a consulta antes de excluir.").performScrollTo().assertIsDisplayed()
        deleteButton().assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf(1L, 3L), fixture.localCart.items.map { it.id })
            assertTrue(fixture.contents.value!!.localCartReviewRequired)
            fixture.allowDiscard = true
        }
        compose.onNodeWithText("Atualizar consulta").performScrollTo().performClick()
        deleteButton().performScrollTo().performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("As apostas preservadas foram excluídas. O carrinho da conta foi mantido.").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, fixture.discards); assertEquals(0, fixture.sends) }
    }

    @Test fun absentReceiptAndEmptyRemoteCartExplainWhyDeletionIsUnavailable() {
        val fixture = Fixture().apply {
            receipt = null
            remoteCart = remoteCart.copy(items = emptyList(), total = BigDecimal.ZERO)
        }
        show(fixture)
        compose.onNodeWithText("O carrinho da conta está vazio.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Não há lote pendente disponível para esta conta. Um lote de outra conta só pode ser revisado pelo usuário que iniciou o envio.").performScrollTo().assertIsDisplayed()
        deleteButton().assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, fixture.discards); assertEquals(0, fixture.sends) }
    }

    @Test fun consultationLoadingNeverOffersDeletionBeforeRemoteReply() {
        val fixture = Fixture().apply { remoteReady = CompletableDeferred() }
        show(fixture)
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        deleteButton().assertDoesNotExist()
        compose.runOnIdle { fixture.remoteReady!!.complete(Unit) }
        deleteButton().performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, fixture.discards); assertEquals(0, fixture.sends) }
    }

    private fun deleteButton() = compose.onNodeWithText("Excluir apostas preservadas")

    private fun show(fixture: Fixture) {
        compose.setContent {
            val model = remember { fixture.createModel().also { fixture.model = it } }
            CaixaDSTheme(darkTheme = false) {
                LocalCartReviewRoute(model, onBack = { fixture.navigation.add("back") }, onRemoteCart = { fixture.navigation.add("remote") })
            }
        }
    }

    private class Fixture {
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        var sends = 0
        var discards = 0
        var queries = 0
        var failRemote = false
        var allowDiscard = true
        var remoteReady: CompletableDeferred<Unit>? = null
        val navigation = mutableListOf<String>()
        private val pending = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Aposta preservada", "local", BigDecimal.ONE)
        private val newLocal = pending.copy(id = 3, title = "Nova aposta local")
        var localCart = ShoppingCart(0, BigDecimal("2"), listOf(pending, newLocal))
        var remoteCart = ShoppingCart(7, BigDecimal.TEN, listOf(pending.copy(id = 2, title = "Aposta da conta", amount = BigDecimal.TEN)))
        val contents = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(remoteCart, null, false, true))
        var receipt: LegacyCartReview? = LegacyCartReview("receipt", listOf(LegacyLocalCartBet(
            localId = 1, serverId = null, modality = LegacyCartModality.MEGA_SENA,
            contestType = LegacyCartCode("NORMAL", null), numbers = LegacyCartNumbers.Flat(listOf(1, 2, 3, 4, 5, 6)),
            clovers = null, quickPickIndicator = LegacyCartCode(0, null), amount = BigDecimal.ONE,
            targetContest = 42, repeatCount = 1, betCount = 1, mirrored = false, generateMirror = false,
            initialContest = 42, status = LegacyCartCode(0L, null), replacement = false, effectiveDate = null,
            effectiveTime = null, mirrorLink = 0, numberCount = 6, cloverCount = 0, quickPick = false,
            quickPickCount = 0, favoriteTeam = null, luckyMonth = null, loteca = emptyList(), lotogol = emptyList(),
        )))
        lateinit var model: LocalCartReviewViewModel
        private val sessions = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === this@Fixture.identity
        }
        private val content = object : AuthenticatedSessionContentRepository {
            override fun observe() = contents
            override fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean {
                if (!sessions.isCurrent(identity)) return false
                contents.value = content
                return true
            }
        }
        private val transfer = object : LegacyCartTransferRepository {
            override suspend fun prepare(ownerKey: String): LegacyCartTransferPreparation { sends++; error("Consultation must not upload") }
            override suspend fun confirmReceived(ownerKey: String, transferId: String): Boolean = error("Consultation must not confirm delivery")
            override suspend fun finishConfirmed(ownerKey: String, transferId: String): LegacyCartCleanupResult = error("Consultation must not acknowledge delivery")
            override suspend fun review(ownerKey: String) = receipt.takeIf { ownerKey == identity.ownerKey }
            override suspend fun discardReviewed(ownerKey: String, review: LegacyCartReview): Boolean {
                discards++
                if (!allowDiscard || ownerKey != identity.ownerKey || review != receipt) return false
                val remaining = localCart.items.filterNot { item -> review.bets.any { it.localId == item.id } }
                localCart = localCart.copy(items = remaining, total = remaining.sumOf { it.amount })
                receipt = null
                return true
            }
        }
        private val local = object : VisitorCartRepository {
            override suspend fun current() = localCart
            override suspend fun add(draft: BetDraft, parameters: BettingParameters): ShoppingCart = error("Consultation must not add")
            override suspend fun remove(item: CartItem): ShoppingCart = error("Deletion must use reviewed receipt")
            override suspend fun clear(): ShoppingCart = error("Consultation must not clear new local bets")
        }
        fun createModel() = LocalCartReviewViewModel(sessions, content, transfer, local, GetCurrentCartUseCase {
            queries++
            remoteReady?.await()
            if (failRemote) AppResult.Failure(AppError.Network) else AppResult.Success(remoteCart)
        }, FixedCoroutineDispatchers(Dispatchers.Main.immediate))
    }
}
