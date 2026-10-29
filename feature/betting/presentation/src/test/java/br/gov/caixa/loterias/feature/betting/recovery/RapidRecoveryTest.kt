// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.recovery

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidListViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
@OptIn(ExperimentalCoroutinesApi::class)
class RapidRecoveryTest {
    @Test fun `recovery reads current cart without generating a replacement batch`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var reads = 0
            val cart = ShoppingCart(1, BigDecimal.ONE, emptyList())
            val model = RapidListViewModel(
                GenerateRapidBetsUseCase { error("Recovery must never generate") },
                RemoveCartItemUseCase { error("Recovery must never remove") },
                ValidateCartUseCase { error("No validation on restore") },
                dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
                recoveryMode = true,
                getCurrentCart = GetCurrentCartUseCase {
                    reads++
                    AppResult.Success(cart)
                }
            )
            advanceUntilIdle()
            assertEquals(1, reads)
            assertEquals(cart, model.state.value.cart)
            assertFalse(model.state.value.readyForPayment)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
