package br.gov.caixa.loterias.feature.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.cart.*
import br.gov.caixa.loterias.core.domain.payment.*
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentPixRestorationTest {
    private val dispatcher = StandardTestDispatcher()
    private val owner = "a".repeat(64)
    private val charge = PixCharge("synthetic-code", "synthetic-tx", "large-qr-not-for-saved-state",
        "2026-10-05T12:00:00", "2026-10-05T12:30:00", 17, BigDecimal("25.50"))
    private var posts = 0
    private var queries = 0
    private var generation: suspend () -> AppResult<PixCharge> = { AppResult.Success(charge) }

    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun teardown() = Dispatchers.resetMain()

    @Test fun `recreated PIX restores primitive charge without generating another purchase`() = runTest(dispatcher) {
        val handle = SavedStateHandle()
        val original = viewModel(handle)
        advanceUntilIdle()
        assertEquals(1, posts)
        assertEquals(charge, original.state.value.pix)
        val saved = snapshot(handle)
        assertTrue(saved.isNotEmpty())
        assertTrue(saved.values.all { it == null || it is String || it is Long || it is Boolean })
        assertFalse(saved.values.contains(charge.qrCodeBase64))
        original.viewModelScope.cancel()
        val restored = viewModel(SavedStateHandle(saved))
        try {
            advanceUntilIdle(); restored.load(); advanceUntilIdle()
            assertEquals(1, posts)
            assertEquals(charge.copy(qrCodeBase64 = ""), restored.state.value.pix)
            assertFalse(restored.state.value.loading)
            assertFalse(restored.state.value.pixRecoveryRequired)
            restored.checkPixStatus(); advanceUntilIdle()
            assertEquals(1, queries)
            assertEquals(1, posts)
        } finally { restored.viewModelScope.cancel() }
    }

    @Test fun `attempt is persisted before POST and interrupted attempt restores recovery without POST`() = runTest(dispatcher) {
        val handle = SavedStateHandle()
        val gate = CompletableDeferred<AppResult<PixCharge>>()
        generation = {
            assertTrue(snapshot(handle).values.contains(true))
            gate.await()
        }
        val original = viewModel(handle)
        advanceUntilIdle(); assertEquals(1, posts)
        val saved = snapshot(handle)
        original.viewModelScope.cancel()
        generation = { AppResult.Success(charge) }
        val restored = viewModel(SavedStateHandle(saved))
        try {
            advanceUntilIdle(); restored.load(); restored.checkPixStatus(); advanceUntilIdle()
            assertEquals(1, posts)
            assertEquals(0, queries)
            assertNull(restored.state.value.pix)
            assertTrue(restored.state.value.pixRecoveryRequired)
            assertFalse(restored.state.value.pixSessionMismatch)
            assertFalse(restored.state.value.loading)
        } finally { restored.viewModelScope.cancel() }
    }

    @Test fun `different owner cannot expose saved PIX or automatically POST`() = runTest(dispatcher) {
        val handle = SavedStateHandle()
        val original = viewModel(handle)
        advanceUntilIdle(); original.viewModelScope.cancel()
        val restored = viewModel(SavedStateHandle(snapshot(handle)), "b".repeat(64))
        try {
            advanceUntilIdle(); restored.load(); restored.checkPixStatus(); advanceUntilIdle()
            assertEquals(1, posts)
            assertEquals(0, queries)
            assertNull(restored.state.value.pix)
            assertTrue(restored.state.value.pixSessionMismatch)
            assertFalse(restored.state.value.loading)
        } finally { restored.viewModelScope.cancel() }
    }

    @Test fun `saved host cannot repeat POST after a lost generation response`() = runTest(dispatcher) {
        generation = { AppResult.Failure(AppError.Network) }
        val vm = viewModel(SavedStateHandle())
        try {
            advanceUntilIdle()
            assertEquals(1, posts)
            assertTrue(vm.state.value.pixRecoveryRequired)
            assertNull(vm.state.value.pix)
            vm.dismissError(); vm.load(); vm.load(); advanceUntilIdle()
            assertEquals(1, posts)
            assertTrue(vm.state.value.pixRecoveryRequired)
        } finally { vm.viewModelScope.cancel() }
    }

    @Test fun `invalid restored purchase or PIX code requires recovery without POST`() = runTest(dispatcher) {
        for (invalid in listOf(charge.copy(purchaseId = 0), charge.copy(copyPaste = " "),
            charge.copy(transactionId = ""), charge.copy(createdAt = " "), charge.copy(expiresAt = ""),
            charge.copy(amount = BigDecimal.ZERO), charge.copy(amount = BigDecimal("-1")))) {
            val handle = SavedStateHandle()
            generation = { AppResult.Success(invalid) }
            val original = viewModel(handle)
            advanceUntilIdle(); original.viewModelScope.cancel()
            val before = posts
            val restored = viewModel(SavedStateHandle(snapshot(handle)))
            try {
                advanceUntilIdle(); restored.load(); advanceUntilIdle()
                assertEquals(before, posts)
                assertNull(restored.state.value.pix)
                assertTrue(restored.state.value.pixRecoveryRequired)
            } finally { restored.viewModelScope.cancel() }
        }
    }

    @Test fun `incomplete saved charge requires recovery instead of a replacement POST`() = runTest(dispatcher) {
        val handle = SavedStateHandle()
        val original = viewModel(handle)
        advanceUntilIdle(); original.viewModelScope.cancel()
        val incomplete = snapshot(handle).filterValues { it != charge.copyPaste }
        val restored = viewModel(SavedStateHandle(incomplete))
        try {
            advanceUntilIdle(); restored.load(); advanceUntilIdle()
            assertEquals(1, posts)
            assertNull(restored.state.value.pix)
            assertTrue(restored.state.value.pixRecoveryRequired)
        } finally { restored.viewModelScope.cancel() }
    }

    @Test fun `saved-state host without authenticated owner never automatically POSTs`() = runTest(dispatcher) {
        val vm = viewModel(SavedStateHandle(), null)
        try {
            advanceUntilIdle(); vm.load(); advanceUntilIdle()
            assertEquals(0, posts)
            assertNull(vm.state.value.pix)
            assertTrue(vm.state.value.pixSessionMismatch)
        } finally { vm.viewModelScope.cancel() }
    }

    @Test fun `fresh saved state still creates exactly one charge across repeated loads`() = runTest(dispatcher) {
        val vm = viewModel(SavedStateHandle())
        try {
            vm.load(); vm.load(); advanceUntilIdle(); vm.load(); advanceUntilIdle()
            assertEquals(1, posts)
            assertEquals(charge, vm.state.value.pix)
        } finally { vm.viewModelScope.cancel() }
    }

    private fun snapshot(handle: SavedStateHandle): Map<String, Any?> = handle.keys().associateWith { handle.get<Any?>(it) }

    private fun viewModel(handle: SavedStateHandle?, accountOwner: String? = owner) = PaymentViewModel(
        mode = PaymentMode.PIX,
        getMethods = GetPaymentMethodsUseCase { AppResult.Success(emptyList()) },
        getCards = GetSavedCardsUseCase { AppResult.Success(emptyList()) },
        deleteCard = DeleteSavedCardUseCase { _, _ -> AppResult.Success(emptyList()) },
        generatePixCharge = GeneratePixChargeUseCase { posts++; generation() },
        getPurchaseProgress = GetPurchaseProgressUseCase { queries++; AppResult.Success(PurchaseProgress(charge.purchaseId, 1)) },
        validateDam = ValidateDamUseCase { AppResult.Success(DamValidation(false, "")) },
        getCurrentCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList())) },
        recordRecentPurchase = RecordRecentPurchaseUseCase { fail("No purchase completed"); AppResult.Success(Unit) },
        clock = Clock.fixed(Instant.parse("2026-10-05T15:00:00Z"), ZoneOffset.UTC),
        dispatchers = FixedCoroutineDispatchers(dispatcher), ownerKey = accountOwner, savedStateHandle = handle,
    )
}
