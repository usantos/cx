package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.RemoveCartItemUseCase
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.domain.cart.ValidateCartUseCase
import br.gov.caixa.loterias.core.domain.cart.CartValidationResult
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RapidListViewModelTest {
    private val item = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "Aposta", BigDecimal("5"))
    private val cart = ShoppingCart(2, BigDecimal("5"), listOf(item))
    private var generateResult: AppResult<ShoppingCart> = AppResult.Success(cart)
    private var removeResult: AppResult<ShoppingCart> = AppResult.Success(cart.copy(items = emptyList()))
    private var validationResult: AppResult<CartValidationResult> = AppResult.Success(CartValidationResult.Accepted)
    private var quantity: Int? = null
    private var removed: CartItem? = null

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `initial generation and refresh use server default or explicit quantity`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        assertFalse(viewModel.state.value.loading)
        assertEquals(cart, viewModel.state.value.cart)
        assertEquals(0, quantity)
        viewModel.generate(4)
        advanceUntilIdle()
        assertEquals(4, quantity)
    }

    @Test fun `removes generated bet and validates non empty cart`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.remove(item)
        advanceUntilIdle()
        assertEquals(item, removed)
        assertTrue(viewModel.state.value.cart?.items.isNullOrEmpty())

        generateResult = AppResult.Success(cart)
        viewModel.generate()
        advanceUntilIdle()
        viewModel.advance()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.readyForPayment)
    }

    @Test fun `empty cart cannot advance and message can be dismissed`() = runTest {
        generateResult = AppResult.Success(cart.copy(items = emptyList()))
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.advance()
        assertEquals("Seu carrinho está vazio.", viewModel.state.value.errorMessage)
        viewModel.dismissError()
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test fun `all operation failures use shared error mapping`() = runTest {
        generateResult = AppResult.Failure(AppError.Network)
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals("Não foi possível atualizar as apostas do Rapidão. Verifique sua internet.", viewModel.state.value.errorMessage)

        generateResult = AppResult.Success(cart)
        viewModel.generate()
        advanceUntilIdle()
        removeResult = AppResult.Failure(AppError.Validation(""))
        viewModel.remove(item)
        advanceUntilIdle()
        assertEquals("Não foi possível atualizar o Rapidão.", viewModel.state.value.errorMessage)

        validationResult = AppResult.Failure(AppError.Unexpected)
        viewModel.advance()
        advanceUntilIdle()
        assertEquals("Ocorreu um erro inesperado no Rapidão.", viewModel.state.value.errorMessage)

        validationResult = AppResult.Failure(AppError.Validation("Carrinho recusado"))
        viewModel.advance()
        advanceUntilIdle()
        assertEquals("Carrinho recusado", viewModel.state.value.errorMessage)
    }

    @Test fun `rejected validation cannot be interpreted as successful payment and notices are acknowledged without retry`() = runTest {
        validationResult = AppResult.Success(CartValidationResult.Rejected(br.gov.caixa.loterias.core.domain.service.ServiceFailure.NoticeRequired(
            br.gov.caixa.loterias.core.domain.service.ServiceNotice(br.gov.caixa.loterias.core.domain.service.ServiceNoticeReason.MORE_BETS_REQUIRED, "Aposte mais"))))
        val vm = viewModel(); advanceUntilIdle()
        vm.advance(); advanceUntilIdle()
        val notice = vm.state.value.validationFailure.notice!!
        assertFalse(vm.state.value.readyForPayment)
        vm.advance(); advanceUntilIdle()
        assertTrue(vm.state.value.validationFailure.notice === notice)
        vm.dismissValidationNotice(notice.copy())
        vm.acceptValidationNotice(notice.copy())
        assertTrue(vm.state.value.validationFailure.notice === notice)
        vm.acceptValidationNotice(notice)
        val event = vm.state.value.validationFailure.redirect!!
        assertEquals(br.gov.caixa.loterias.core.domain.service.ServiceRedirect.HOME, event.destination)
        assertFalse(vm.consumeValidationRedirect(br.gov.caixa.loterias.core.presentation.ServiceRedirectEvent(event.destination)))
        assertTrue(vm.consumeValidationRedirect(event)); assertFalse(vm.consumeValidationRedirect(event))
        assertFalse(vm.state.value.readyForPayment)
        vm.advance(); advanceUntilIdle()
        vm.dismissValidationNotice(vm.state.value.validationFailure.notice!!)
        validationResult = AppResult.Success(CartValidationResult.Accepted)
        vm.advance(); advanceUntilIdle()
        assertTrue(vm.state.value.readyForPayment)
        vm.consumePaymentReady()
        assertFalse(vm.state.value.readyForPayment)
    }

    private fun viewModel(): RapidListViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return RapidListViewModel(
            GenerateRapidBetsUseCase { value -> quantity = value; generateResult },
            RemoveCartItemUseCase { value -> removed = value; removeResult },
            ValidateCartUseCase { validationResult },
            FixedCoroutineDispatchers(dispatcher),
        )
    }
}
