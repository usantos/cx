package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.WasFavoriteCartAddedUseCase
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.FavoriteCartsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteCartsViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test
    fun `list mode loads favorites and deletes selected cart`() = runTest(dispatcherRule.dispatcher) {
        var deleted = 0L
        val viewModel = viewModel(cartId = null, deleteCart = {
            deleted = it
            AppResult.Success(emptyList())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(carts(), viewModel.state.value.carts)

        viewModel.requestCartRemoval(1)
        viewModel.confirmCartRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1L, deleted)
        assertTrue(viewModel.state.value.carts.isEmpty())
        assertNull(viewModel.state.value.pendingCartRemoval)
    }

    @Test
    fun `list card loads bets once and toggles expansion`() = runTest(dispatcherRule.dispatcher) {
        var loads = 0
        val viewModel = viewModel(cartId = null, loadBets = {
            loads++
            AppResult.Success(bets())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, loads)
        assertEquals(1L, viewModel.state.value.expandedCartId)
        assertEquals(bets(), viewModel.state.value.expandedBets)

        viewModel.toggleCart(1)
        assertNull(viewModel.state.value.expandedCartId)
        assertTrue(viewModel.state.value.expandedBets.isEmpty())
        viewModel.toggleCart(1)
        assertEquals(1, loads)
        assertEquals(1L, viewModel.state.value.expandedCartId)
    }

    @Test
    fun `list transform fetches bets and preserves legacy contest selection`() = runTest(dispatcherRule.dispatcher) {
        var imported: Pair<Long, Int>? = null
        val viewModel = viewModel(
            cartId = null,
            loadBets = { AppResult.Success(bets()) },
            import = { id, selection ->
                imported = id to selection
                AppResult.Success(cart())
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.requestImport(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.specialSelectionRequired)
        assertEquals("Mega-Sena", viewModel.state.value.regularModalityName)
        assertEquals("Mega da Virada", viewModel.state.value.specialModalityName)

        viewModel.selectSpecialMode(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1L to 1, imported)
        assertTrue(viewModel.state.value.imported)
        viewModel.consumeImported()
        assertFalse(viewModel.state.value.imported)
    }

    @Test
    fun `transforming same favorite again asks for legacy confirmation`() = runTest(dispatcherRule.dispatcher) {
        var imports = 0
        var marked = 0L
        val viewModel = viewModel(
            cartId = null,
            loadBets = { AppResult.Success(listOf(normalBet())) },
            import = { _, _ ->
                imports++
                AppResult.Success(cart())
            },
            markAdded = { marked = it }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestImport(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.consumeImported()

        viewModel.requestImport(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.existingCartConfirmationRequired)
        assertEquals(1, imports)
        viewModel.confirmExistingCartImport()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1L, marked)
        assertEquals(2, imports)
        assertFalse(viewModel.state.value.existingCartConfirmationRequired)
    }

    @Test
    fun `persisted favorite usage asks before fetching or importing`() = runTest(dispatcherRule.dispatcher) {
        var loads = 0
        val viewModel = viewModel(
            cartId = null,
            loadBets = {
                loads++
                AppResult.Success(listOf(normalBet()))
            },
            wasAdded = { it == 1L }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestImport(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.existingCartConfirmationRequired)
        assertEquals(0, loads)
    }

    @Test
    fun `returning from detail invalidates cached bets before the next expansion`() = runTest(dispatcherRule.dispatcher) {
        var bets = listOf(normalBet())
        val viewModel = viewModel(cartId = null, loadBets = { AppResult.Success(bets) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(bets, viewModel.state.value.expandedBets)
        bets = emptyList()
        viewModel.refreshAfterDetail()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.state.value.expandedCartId)
        viewModel.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.expandedBets.isEmpty())
    }

    @Test
    fun `deleting expanded favorite clears expanded data`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(cartId = null)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestCartRemoval(1)
        viewModel.confirmCartRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.state.value.expandedCartId)
        assertTrue(viewModel.state.value.expandedBets.isEmpty())
    }

    @Test
    fun `list bet fetch failures clear item loading and expose message`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(
            cartId = null,
            loadBets = { AppResult.Failure(AppError.Network) }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.state.value.loadingCartId)
        assertTrue(viewModel.state.value.errorMessage!!.contains("internet"))

        viewModel.load()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestImport(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.state.value.loadingCartId)
        assertTrue(viewModel.state.value.errorMessage!!.contains("internet"))
    }

    @Test
    fun `detail mode loads bets and deletes one bet`() = runTest(dispatcherRule.dispatcher) {
        var deleted: Pair<Long, Long>? = null
        val viewModel = viewModel(deleteBet = { cart, bet ->
            deleted = cart to bet
            AppResult.Success(listOf(normalBet()))
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.state.value.bets.size)

        viewModel.requestBetRemoval(3)
        viewModel.confirmBetRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1L to 3L, deleted)
        assertEquals(listOf(normalBet()), viewModel.state.value.bets)
    }

    @Test
    fun `deleting the last bet deletes its favorite cart`() = runTest(dispatcherRule.dispatcher) {
        var deletedCart = 0L
        val viewModel = viewModel(
            loadBets = { AppResult.Success(listOf(normalBet())) },
            deleteCart = {
                deletedCart = it
                AppResult.Success(emptyList())
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestBetRemoval(2)
        viewModel.confirmBetRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1L, deletedCart)
        assertTrue(viewModel.state.value.cartDeleted)
        assertTrue(viewModel.state.value.bets.isEmpty())
    }

    @Test
    fun `mixed regular and special bets require selection before import`() = runTest(dispatcherRule.dispatcher) {
        var imported: Pair<Long, Int>? = null
        val viewModel = viewModel(import = { id, selection ->
            imported = id to selection
            AppResult.Success(cart())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestImport()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.specialSelectionRequired)
        assertNull(imported)

        viewModel.selectSpecialMode(2)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1L to 2, imported)
        assertTrue(viewModel.state.value.imported)
        assertFalse(viewModel.state.value.specialSelectionRequired)
    }

    @Test
    fun `single contest kind imports both without asking`() = runTest(dispatcherRule.dispatcher) {
        var selection = -1
        val viewModel = viewModel(
            loadBets = { AppResult.Success(listOf(normalBet())) },
            import = { _, value ->
                selection = value
                AppResult.Success(cart())
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestImport()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, selection)
        assertTrue(viewModel.state.value.imported)
    }

    @Test
    fun `dismiss clears all confirmations and guarded commands do nothing`() = runTest(dispatcherRule.dispatcher) {
        var deletes = 0
        var imports = 0
        val viewModel = viewModel(
            cartId = null,
            deleteCart = {
                deletes++
                AppResult.Success(emptyList())
            },
            import = { _, _ ->
                imports++
                AppResult.Success(cart())
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestCartRemoval(1)
        viewModel.requestBetRemoval(2)
        viewModel.dismissConfirmation()
        viewModel.confirmCartRemoval()
        viewModel.confirmBetRemoval()
        viewModel.requestImport()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, deletes)
        assertEquals(0, imports)
        assertNull(viewModel.state.value.pendingCartRemoval)
        assertNull(viewModel.state.value.pendingBetRemoval)
    }

    @Test
    fun `maps all load errors and supports retry`() = runTest(dispatcherRule.dispatcher) {
        val cases = listOf(
            AppError.Network to "Não foi possível carregar os carrinhos favoritos. Verifique sua internet.",
            AppError.Validation("") to "Não foi possível atualizar o carrinho favorito.",
            AppError.Validation("Expirado") to "Expirado",
            AppError.Unexpected to "Ocorreu um erro inesperado ao atualizar o carrinho favorito."
        )
        cases.forEach { (error, message) ->
            var fail = true
            val viewModel = viewModel(loadCarts = {
                if (fail) AppResult.Failure(error) else AppResult.Success(carts())
            }, cartId = null)
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(message, viewModel.state.value.errorMessage)
            fail = false
            viewModel.load()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(carts(), viewModel.state.value.carts)
            assertNull(viewModel.state.value.errorMessage)
        }
    }

    @Test
    fun `maps deletion and import failures while preserving loaded bets`() = runTest(dispatcherRule.dispatcher) {
        var lastBet = false
        val viewModel = viewModel(
            deleteBet = { _, _ -> AppResult.Failure(AppError.Network) },
            deleteCart = { AppResult.Failure(AppError.Unexpected) },
            import = { _, _ -> AppResult.Failure(AppError.Validation("Carrinho atual ocupado")) }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestBetRemoval(2)
        viewModel.confirmBetRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.startsWith("Não foi possível carregar"))
        assertEquals(2, viewModel.state.value.bets.size)

        viewModel.requestImport()
        viewModel.selectSpecialMode(0)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Carrinho atual ocupado", viewModel.state.value.errorMessage)

        val lastViewModel = viewModel(
            loadBets = { AppResult.Success(listOf(normalBet())) },
            deleteCart = {
                lastBet = true
                AppResult.Failure(AppError.Unexpected)
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        lastViewModel.requestBetRemoval(2)
        lastViewModel.confirmBetRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(lastBet)
        assertFalse(lastViewModel.state.value.cartDeleted)
    }

    @Test fun `deferred visitor carts do not read private lists or details`() {
        var reads = 0
        val vm = viewModel(
            cartId = null,
            autoLoad = false,
            loadCarts = {
                reads++
                AppResult.Success(carts())
            },
            loadBets = { error("Visitor must not load details") }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, reads)
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.carts.isEmpty())
        vm.load()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, reads)
        assertEquals(carts(), vm.state.value.carts)
    }

    @Test fun `expired lease prevents queued cart reads`() {
        var reads = 0
        val vm = viewModel(cartId = null, isSessionCurrent = { false }, loadCarts = {
            reads++
            AppResult.Success(carts())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, reads)
        assertTrue(vm.state.value.carts.isEmpty())
    }

    @Test fun `expired lease ignores pending carts response and resume refresh`() {
        var current = true
        var reads = 0
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCart>>>()
        val vm = viewModel(cartId = null, isSessionCurrent = { current }, loadCarts = {
            reads++
            pending.await()
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        current = false
        pending.complete(AppResult.Success(carts()))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.carts.isEmpty())
        vm.refreshAfterDetail()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, reads)
    }

    @Test fun `secure favorite read failure blocks loading and remote import with a recoverable error`() = runTest(
        dispatcherRule.dispatcher
    ) {
        var loads = 0
        var imports = 0
        val vm = viewModel(
            cartId = null,
            wasAdded = { throw java.io.IOException("controlled secure read failure") },
            loadBets = {
                loads++
                AppResult.Success(listOf(normalBet()))
            },
            import = { _, _ ->
                imports++
                AppResult.Success(cart())
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestImport(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, loads)
        assertEquals(0, imports)
        assertFalse(vm.state.value.imported)
        assertNotNull(vm.state.value.errorMessage)
    }

    @Test fun `durable favorite marker commits before remote request and failed marker prevents it`() = runTest(dispatcherRule.dispatcher) {
        val commands = mutableListOf<String>()
        var rejectWrite = true
        val vm = viewModel(
            loadBets = { AppResult.Success(listOf(normalBet())) },
            markAdded = {
                commands += "persist"
                if (rejectWrite) throw java.io.IOException("controlled secure write failure")
            },
            import = { _, _ ->
                commands += "remote"
                AppResult.Success(cart())
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestImport()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("persist"), commands)
        assertFalse(vm.state.value.loading)
        assertFalse(vm.state.value.imported)
        assertNotNull(vm.state.value.errorMessage)
        rejectWrite = false
        vm.requestImport()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("persist", "persist", "remote"), commands)
        assertTrue(vm.state.value.imported)
    }

    @Test fun `two rapid favorite imports serialize durable intent and require confirmation for the second`() =
        runTest(dispatcherRule.dispatcher) {
            var imports = 0
            val vm = viewModel(
                loadBets = { AppResult.Success(listOf(normalBet())) },
                markAdded = { kotlinx.coroutines.yield() },
                import = { _, _ ->
                    imports++
                    AppResult.Success(cart())
                }
            )
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestImport()
            vm.requestImport()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, imports)
            assertTrue(vm.state.value.existingCartConfirmationRequired)
            vm.confirmExistingCartImport()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(2, imports)
        }

    private fun viewModel(
        autoLoad: Boolean = true,
        isSessionCurrent: () -> Boolean = { true },
        cartId: Long? = 1,
        loadCarts: suspend () -> AppResult<List<FavoriteCart>> = { AppResult.Success(carts()) },
        loadBets: suspend (Long) -> AppResult<List<FavoriteCartBet>> = { AppResult.Success(bets()) },
        deleteCart: suspend (Long) -> AppResult<List<FavoriteCart>> = { AppResult.Success(emptyList()) },
        deleteBet: suspend (Long, Long) -> AppResult<List<FavoriteCartBet>> = { _, _ -> AppResult.Success(emptyList()) },
        import: suspend (Long, Int) -> AppResult<ShoppingCart> = { _, _ -> AppResult.Success(cart()) },
        wasAdded: suspend (Long) -> Boolean = { false },
        markAdded: suspend (Long) -> Unit = { },
    ) = FavoriteCartsViewModel(
        cartId,
        GetFavoriteCartsUseCase { loadCarts() },
        GetFavoriteCartBetsUseCase { loadBets(it) },
        DeleteFavoriteCartUseCase { deleteCart(it) },
        DeleteFavoriteCartBetUseCase { cart, bet -> deleteBet(cart, bet) },
        AddFavoriteCartToCurrentUseCase { id, selection -> import(id, selection) },
        WasFavoriteCartAddedUseCase(wasAdded),
        MarkFavoriteCartAddedUseCase(markAdded),
        dispatchers = FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        autoLoad = autoLoad,
        isSessionCurrent = isSessionCurrent
    )

    companion object {
        private fun carts() = listOf(FavoriteCart(1, "Favorito"))
        private fun normalBet() = FavoriteCartBet(2, "Mega-Sena", listOf(1, 2))
        private fun specialBet() = FavoriteCartBet(3, "Mega da Virada", listOf(3, 4), special = true)
        private fun bets() = listOf(normalBet(), specialBet())
        private fun cart() = ShoppingCart(9, BigDecimal.TEN, emptyList())
    }
}
