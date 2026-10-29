package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidConfigurationViewModel
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidListViewModel
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
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
class RapidPrivateEntryTest {
    @Before fun setup() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After fun teardown() = Dispatchers.resetMain()

    @Test fun `expired rapid session asks central login without generic acknowledgement`() = runTest {
        val vm = RapidConfigurationViewModel(
            getConfiguration = GetRapidBetConfigurationUseCase {
                ServiceResult.Rejected(
                    br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired(
                        br.gov.caixa.loterias.core.domain.service.model.ServiceNotice(
                            br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason.LOGIN_REQUIRED
                        )
                    )
                )
            },
            getModalities = GetLotteryModalitiesUseCase { error("Do not continue after 401") },
            saveConfiguration = SaveRapidBetConfigurationUseCase { error("No save") },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher(testScheduler))
        )
        advanceUntilIdle()
        val failure = vm.state.value.serviceFailure
        assertNull(vm.state.value.errorMessage)
        assertNull(failure.notice)
        assertEquals(br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect.LOGIN, failure.redirect?.destination)
        assertTrue(vm.consumeServiceRedirect(failure.redirect!!))
        assertFalse(vm.consumeServiceRedirect(failure.redirect!!))
    }

    @Test fun `visitor configuration never reads or writes private data even on retry`() = runTest {
        val vm = RapidConfigurationViewModel(
            getConfiguration = GetRapidBetConfigurationUseCase { error("private configuration") },
            getModalities = GetLotteryModalitiesUseCase { error("modalities") },
            saveConfiguration = SaveRapidBetConfigurationUseCase { error("private save") },
            getFavorites = GetFavoriteBetsUseCase { error("private favorites") },
            getParameters = GetBettingParametersUseCase { error("limits") },
            verifyAccess = VerifyAccountAccessUseCase { error("private account") },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher(testScheduler)),
            autoLoad = false,
            isSessionCurrent = { false }
        )
        vm.load()
        vm.loadFavorites()
        vm.loadAmountLimits()
        vm.save()
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.availableFavorites.isEmpty())
        assertNull(vm.state.value.errorMessage)
    }

    @Test fun `rapid list expired session opens login directly and cannot navigate twice`() = runTest {
        val vm = RapidListViewModel(
            generateRapidBets = GenerateRapidBetsUseCase {
                ServiceResult.Rejected(
                    br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired(
                        br.gov.caixa.loterias.core.domain.service.model.ServiceNotice(
                            br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason.LOGIN_REQUIRED
                        )
                    )
                )
            },
            removeCartItem = RemoveCartItemUseCase { error("No remove") },
            validateCart = ValidateCartUseCase { error("No checkout") },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher(testScheduler))
        )
        advanceUntilIdle()
        assertNull(vm.state.value.errorMessage)
        assertNull(vm.state.value.serviceFailure.notice)
        val redirect = vm.state.value.serviceFailure.redirect!!
        assertEquals(br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect.LOGIN, redirect.destination)
        assertTrue(vm.consumeServiceRedirect(redirect))
        assertFalse(vm.consumeServiceRedirect(redirect))
    }

    @Test fun `visitor rapid list never generates or validates private cart`() = runTest {
        val vm = RapidListViewModel(
            generateRapidBets = GenerateRapidBetsUseCase { error("private generation") },
            removeCartItem = RemoveCartItemUseCase { error("private remove") },
            validateCart = ValidateCartUseCase { error("private cart") },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher(testScheduler)),
            autoLoad = false,
            isSessionCurrent = { false }
        )
        vm.generate()
        vm.advance()
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.cart)
        assertNull(vm.state.value.errorMessage)
    }

    @Test fun `expired generation lease cannot publish cart or payment readiness`() = runTest {
        var current = true
        val response = CompletableDeferred<ServiceResult<br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart>>()
        val vm = RapidListViewModel(
            generateRapidBets = GenerateRapidBetsUseCase { response.await() },
            removeCartItem = RemoveCartItemUseCase { error("No remove") },
            validateCart = ValidateCartUseCase { error("Expired checkout") },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher(testScheduler)),
            isSessionCurrent = { current }
        )
        current = false
        response.complete(
            ServiceResult.Success(br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart(1, BigDecimal.TEN, emptyList()))
        )
        advanceUntilIdle()
        vm.advance()
        assertNull(vm.state.value.cart)
        assertFalse(vm.state.value.readyForPayment)
    }

    @Test fun `configuration response from expired lease cannot populate screen or start next request`() = runTest {
        var current = true
        val response = CompletableDeferred<ServiceResult<RapidBetConfiguration>>()
        var modalitiesCalled = false
        val vm = RapidConfigurationViewModel(
            getConfiguration = GetRapidBetConfigurationUseCase { response.await() },
            getModalities = GetLotteryModalitiesUseCase {
                modalitiesCalled = true
                AppResult.Success(emptyList())
            },
            saveConfiguration = SaveRapidBetConfigurationUseCase { ServiceResult.Success(it) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher(testScheduler)),
            isSessionCurrent = { current }
        )
        current = false
        response.complete(
            ServiceResult.Success(
                RapidBetConfiguration(
                    BigDecimal.TEN,
                    BigDecimal.TEN,
                    BigDecimal.ZERO,
                    emptySet(),
                    emptySet(),
                    listOf(LotteryModality(2, "Mega", "MEGA"))
                )
            )
        )
        advanceUntilIdle()
        assertFalse(modalitiesCalled)
        assertEquals(BigDecimal.ZERO, vm.state.value.maximumAmount)
        assertTrue(vm.state.value.availableModalities.isEmpty())
    }
}
