package br.gov.caixa.loterias.feature.home

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_CONNECTION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OFFLINE
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OPERATION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_RESPONSE_TIMEOUT
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_UPDATED_TERMS
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.feature.account.domain.model.NotificationReadResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.home.presentation.route.notificationBrowserLink
import br.gov.caixa.loterias.feature.home.presentation.state.HomeNotificationRedirect
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val unreadIdentity = br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity("a".repeat(64))
    private var currentUnreadIdentity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity? = unreadIdentity
    private val unreadSessions = object : br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = currentUnreadIdentity
        override fun isCurrent(identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity) =
            identity === currentUnreadIdentity
    }
    private fun unread(source: suspend () -> Int) = br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase(
        br.gov.caixa.loterias.feature.account.domain.repository.UnreadNotificationCountRepository {
            source()
        },
        unreadSessions
    )

    @Test fun `unread badge uses independent count and failure hides it`() = runTest(dispatcher) {
        val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, null, false))
        var fail = false
        val vm = viewModel(
            AppResult.Success(parameters),
            sessionContent = { content },
            unreadCount = unread {
                if (fail) throw java.io.IOException()
                2
            }
        ) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            assertEquals(2, vm.state.value.unreadNotificationCount)
            assertNull(vm.state.value.latestNotification)
            fail = true
            vm.refreshUnreadNotificationCount()
            advanceUntilIdle()
            assertEquals(0, vm.state.value.unreadNotificationCount)
            assertNull(vm.state.value.notificationNotice)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `late unread count cannot replace newer zero in same session`() = runTest(dispatcher) {
        val pending = kotlinx.coroutines.CompletableDeferred<Int>()
        var calls = 0
        val vm = viewModel(
            AppResult.Success(parameters),
            unreadCount = unread {
                when (++calls) {
                    1 -> 2
                    2 -> pending.await()
                    else -> 0
                }
            }
        ) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            assertEquals(2, vm.state.value.unreadNotificationCount)
            vm.refreshUnreadNotificationCount()
            advanceUntilIdle()
            vm.refreshUnreadNotificationCount()
            advanceUntilIdle()
            assertEquals(0, vm.state.value.unreadNotificationCount)
            pending.complete(2)
            advanceUntilIdle()
            assertEquals(0, vm.state.value.unreadNotificationCount)
            assertEquals(3, calls)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `logout and null content clear unread count and discard pending response`() = runTest(dispatcher) {
        val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, null, false))
        val pending = kotlinx.coroutines.CompletableDeferred<Int>()
        var calls = 0
        val vm = viewModel(
            AppResult.Success(parameters),
            sessionContent = { content },
            unreadCount = unread {
                if (++calls == 1) 2 else pending.await()
            }
        ) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            assertEquals(2, vm.state.value.unreadNotificationCount)
            vm.refreshUnreadNotificationCount()
            advanceUntilIdle()
            currentUnreadIdentity = null
            content.value = null
            advanceUntilIdle()
            assertEquals(0, vm.state.value.unreadNotificationCount)
            pending.complete(2)
            advanceUntilIdle()
            assertEquals(0, vm.state.value.unreadNotificationCount)
            assertNull(vm.state.value.latestNotification)
            assertEquals(2, calls)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `replacement content invalidates pending count before refreshing`() = runTest(dispatcher) {
        val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, null, false))
        val pending = kotlinx.coroutines.CompletableDeferred<Int>()
        var calls = 0
        val vm = viewModel(
            AppResult.Success(parameters),
            sessionContent = { content },
            unreadCount = unread {
                when (++calls) {
                    1 -> 2
                    2 -> pending.await()
                    else -> 0
                }
            }
        ) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            assertEquals(2, vm.state.value.unreadNotificationCount)
            vm.refreshUnreadNotificationCount()
            advanceUntilIdle()
            content.value = AuthenticatedSessionContent(null, AccountNotification(7, null, null, null, null), false)
            advanceUntilIdle()
            assertEquals(0, vm.state.value.unreadNotificationCount)
            pending.complete(2)
            advanceUntilIdle()
            assertEquals(0, vm.state.value.unreadNotificationCount)
            assertEquals(7L, vm.state.value.latestNotification!!.id)
            assertEquals(3, calls)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `notification link never reads or closes and invalid browser schemes remain inside the popup`() = runTest(dispatcher) {
        val notification = AccountNotification(4, "Title", "Text", null, " https://example.invalid/info ")
        val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, notification, false))
        var reads = 0
        val vm = viewModel(
            AppResult.Success(parameters),
            sessionContent = { content },
            readNotification = {
                reads++
                AppResult.Success(NotificationReadResult.Confirmed)
            }
        ) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            assertEquals("https://example.invalid/info", vm.notificationLink())
            assertFalse(vm.state.value.notificationDismissed)
            assertEquals(0, reads)
            for (link in listOf(
                null, "", " ", "javascript:alert(1)", "file:///tmp/private", "intent://app", "data:text/plain,foo",
                "https://user:pass@example.invalid/", "https://", "https://ex ample.invalid/", "/relative"
            )) {
                content.value = AuthenticatedSessionContent(null, notification.copy(link = link), false)
                advanceUntilIdle()
                assertNull(vm.notificationLink())
                assertTrue(vm.state.value.notificationReadError!!.contains("abrir o link"))
                vm.dismissNotificationError()
                assertNull(vm.state.value.notificationReadError)
            }
            assertEquals("http://example.invalid/", notificationBrowserLink("http://example.invalid/"))
            vm.notificationLinkFailed()
            assertTrue(vm.state.value.notificationReadError != null)
            assertEquals(0, reads)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `notification remains closed after read failure acknowledgment without another put`() = runTest(dispatcher) {
        val notification = AccountNotification(4, null, null, null, null)
        val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, notification, false))
        var reads = 0
        val vm = viewModel(
            AppResult.Success(parameters),
            sessionContent = { content },
            readNotification = {
                reads++
                AppResult.Failure(AppError.Network)
            }
        ) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            vm.closeNotification()
            vm.closeNotification()
            assertNull(vm.notificationLink())
            advanceUntilIdle()
            assertEquals(1, reads)
            assertTrue(vm.state.value.notificationDismissed)
            assertEquals(LEGACY_OPERATION_FAILED, vm.state.value.notificationNotice!!.message)
            vm.acceptNotificationNotice(vm.state.value.notificationNotice!!)
            advanceUntilIdle()
            assertEquals(1, reads)
            assertTrue(vm.state.value.notificationDismissed)
            vm.closeNotification()
            advanceUntilIdle()
            assertEquals(1, reads)
            content.value = AuthenticatedSessionContent(null, notification, true)
            advanceUntilIdle()
            vm.closeNotification()
            assertNull(vm.notificationLink())
            assertEquals(1, reads)
            content.value = null
            advanceUntilIdle()
            vm.closeNotification()
            assertNull(vm.notificationLink())
            assertEquals(1, reads)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `late read failure does not affect a replacement notification or a new session`() = runTest(dispatcher) {
        val notification = AccountNotification(4, null, null, null, null)
        val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, notification, false))
        val result = kotlinx.coroutines.CompletableDeferred<AppResult<NotificationReadResult>>()
        val vm =
            viewModel(AppResult.Success(parameters), sessionContent = {
                content
            }, readNotification = { result.await() }) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            vm.closeNotification()
            advanceUntilIdle()
            content.value = AuthenticatedSessionContent(null, notification.copy(id = 5), false)
            advanceUntilIdle()
            result.complete(AppResult.Failure(AppError.Network))
            advanceUntilIdle()
            assertNull(vm.state.value.notificationReadError)
            assertFalse(vm.state.value.notificationDismissed)
            assertEquals(5L, vm.state.value.latestNotification!!.id)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `read failure notices preserve text choices connectivity and do not reopen or retry`() = runTest(dispatcher) {
        val cases = listOf(
            ServiceFailure.ConnectionFailed(ServiceConnectionFailure.TIMEOUT) to LEGACY_RESPONSE_TIMEOUT,
            ServiceFailure.ConnectionFailed(ServiceConnectionFailure.CONNECTION) to LEGACY_CONNECTION_FAILED,
            ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Mensagem", "E1")) to
                "Mensagem\n\nE1\n07/09/2026 12:00:00",
            ServiceFailure.RegistrationRequired("Unsupported in Home") to LEGACY_OPERATION_FAILED
        )
        for ((failure, message) in cases) {
            val content =
                MutableStateFlow<AuthenticatedSessionContent?>(
                    AuthenticatedSessionContent(null, AccountNotification(4, null, null, null, null), false)
                )
            var calls = 0
            val vm = viewModel(
                AppResult.Success(parameters),
                sessionContent = { content },
                readNotification = {
                    calls++
                    AppResult.Success(NotificationReadResult.Rejected(failure))
                }
            ) { AppResult.Success(social) }
            try {
                advanceUntilIdle()
                vm.closeNotification()
                advanceUntilIdle()
                val notice = vm.state.value.notificationNotice!!
                assertEquals(message, notice.message)
                assertEquals("Entendi", notice.positiveLabel)
                vm.dismissNotificationNotice(notice.copy())
                assertEquals(notice, vm.state.value.notificationNotice)
                vm.acceptNotificationNotice(notice.copy())
                assertEquals(notice, vm.state.value.notificationNotice)
                vm.dismissNotificationNotice(notice)
                assertNull(vm.state.value.notificationNotice)
                assertTrue(vm.state.value.notificationDismissed)
                vm.closeNotification()
                advanceUntilIdle()
                assertEquals(1, calls)
            } finally {
                vm.viewModelScope.cancel()
            }
        }
    }

    @Test fun `terms require acknowledgment and login consent rechecks offline state at the action`() = runTest(dispatcher) {
        for (destination in listOf(ServiceRedirect.ACCEPT_TERMS, ServiceRedirect.LOGIN, ServiceRedirect.HOME)) {
            val failure = when (destination) {
                ServiceRedirect.LOGIN -> ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED))
                ServiceRedirect.HOME -> ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.MORE_BETS_REQUIRED, "Mais apostas"))
                else -> ServiceFailure.RedirectRequired(destination)
            }
            val content =
                MutableStateFlow<AuthenticatedSessionContent?>(
                    AuthenticatedSessionContent(null, AccountNotification(4, null, null, null, null), false)
                )
            var availability = ConnectionAvailability.CONNECTED
            val vm = viewModel(
                AppResult.Success(parameters),
                sessionContent = { content },
                network = NetworkAvailability { availability },
                readNotification = { AppResult.Success(NotificationReadResult.Rejected(failure)) }
            ) { AppResult.Success(social) }
            try {
                advanceUntilIdle()
                vm.closeNotification()
                advanceUntilIdle()
                val notice = vm.state.value.notificationNotice!!
                assertNull(vm.state.value.notificationRedirect)
                assertEquals(destination, notice.destination)
                if (destination == ServiceRedirect.ACCEPT_TERMS) assertEquals(LEGACY_UPDATED_TERMS, notice.message)
                if (destination == ServiceRedirect.LOGIN) {
                    assertEquals("Entendi", notice.positiveLabel)
                    assertNull(notice.negativeLabel)
                    availability = ConnectionAvailability.DISCONNECTED
                    vm.acceptNotificationNotice(notice)
                    assertEquals(LEGACY_OFFLINE, vm.state.value.notificationNotice!!.message)
                    assertNull(vm.state.value.notificationRedirect)
                } else {
                    vm.acceptNotificationNotice(notice)
                    val event = vm.state.value.notificationRedirect!!
                    assertEquals(destination, event.destination)
                    assertFalse(vm.consumeNotificationRedirect(HomeNotificationRedirect(destination)))
                    assertTrue(vm.consumeNotificationRedirect(event))
                    assertFalse(vm.consumeNotificationRedirect(event))
                }
                assertTrue(vm.state.value.notificationDismissed)
            } finally {
                vm.viewModelScope.cancel()
            }
        }
    }

    @Test fun `direct redirects are single consumption and old session effects cannot navigate`() = runTest(dispatcher) {
        for (destination in listOf(
            ServiceRedirect.LOGIN,
            ServiceRedirect.REGISTER_ACCOUNT,
            ServiceRedirect.UNAVAILABLE,
            ServiceRedirect.CART,
            ServiceRedirect.FAVORITE_CARTS
        )) {
            val content =
                MutableStateFlow<AuthenticatedSessionContent?>(
                    AuthenticatedSessionContent(null, AccountNotification(4, null, null, null, null), false)
                )
            val vm = viewModel(
                AppResult.Success(parameters),
                sessionContent = { content },
                readNotification = {
                    AppResult.Success(NotificationReadResult.Rejected(ServiceFailure.RedirectRequired(destination)))
                }
            ) { AppResult.Success(social) }
            try {
                advanceUntilIdle()
                vm.closeNotification()
                advanceUntilIdle()
                val event = vm.state.value.notificationRedirect!!
                assertEquals(destination, event.destination)
                content.value = null
                advanceUntilIdle()
                assertFalse(vm.consumeNotificationRedirect(event))
                assertNull(vm.state.value.notificationRedirect)
            } finally {
                vm.viewModelScope.cancel()
            }
        }
    }

    @Test fun `offline failure and direct login redirect show offline notice without navigation`() = runTest(dispatcher) {
        for (failure in listOf(
            ServiceFailure.ConnectionFailed(ServiceConnectionFailure.TIMEOUT),
            ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN)
        )) {
            val content =
                MutableStateFlow<AuthenticatedSessionContent?>(
                    AuthenticatedSessionContent(null, AccountNotification(4, null, null, null, null), false)
                )
            val vm = viewModel(
                AppResult.Success(parameters),
                sessionContent = { content },
                network = NetworkAvailability { ConnectionAvailability.DISCONNECTED },
                readNotification = { AppResult.Success(NotificationReadResult.Rejected(failure)) }
            ) { AppResult.Success(social) }
            try {
                advanceUntilIdle()
                vm.closeNotification()
                advanceUntilIdle()
                assertEquals(LEGACY_OFFLINE, vm.state.value.notificationNotice!!.message)
                assertNull(vm.state.value.notificationRedirect)
                vm.acceptNotificationNotice(vm.state.value.notificationNotice!!)
                assertNull(vm.state.value.notificationNotice)
                assertNull(vm.state.value.notificationRedirect)
                assertTrue(vm.state.value.notificationDismissed)
            } finally {
                vm.viewModelScope.cancel()
            }
        }
    }

    @Test fun `typed redirect returned after logout cannot publish an effect or clear newer content`() = runTest(dispatcher) {
        val content =
            MutableStateFlow<AuthenticatedSessionContent?>(
                AuthenticatedSessionContent(null, AccountNotification(4, null, null, null, null), false)
            )
        val response = kotlinx.coroutines.CompletableDeferred<AppResult<NotificationReadResult>>()
        val vm =
            viewModel(AppResult.Success(parameters), sessionContent = {
                content
            }, readNotification = { response.await() }) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            vm.closeNotification()
            advanceUntilIdle()
            content.value = null
            advanceUntilIdle()
            response.complete(AppResult.Success(NotificationReadResult.Rejected(ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN))))
            advanceUntilIdle()
            assertNull(vm.state.value.notificationNotice)
            assertNull(vm.state.value.notificationRedirect)
            assertNull(vm.state.value.latestNotification)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `confirmed and deliberately ignored responses stay closed without fabricated alerts`() = runTest(dispatcher) {
        for (outcome in listOf(NotificationReadResult.Confirmed, NotificationReadResult.Rejected(ServiceFailure.Unchanged))) {
            val content =
                MutableStateFlow<AuthenticatedSessionContent?>(
                    AuthenticatedSessionContent(null, AccountNotification(4, null, null, null, null), false)
                )
            val vm =
                viewModel(AppResult.Success(parameters), sessionContent = {
                    content
                }, readNotification = { AppResult.Success(outcome) }) { AppResult.Success(social) }
            try {
                advanceUntilIdle()
                vm.closeNotification()
                advanceUntilIdle()
                assertTrue(vm.state.value.notificationDismissed)
                assertNull(vm.state.value.notificationNotice)
                assertNull(vm.state.value.notificationRedirect)
            } finally {
                vm.viewModelScope.cancel()
            }
        }
    }

    @Test fun `cart response from before session change cannot restore previous account header`() = runTest(dispatcher) {
        for (failure in listOf(false, true)) {
            val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, null, false))
            val pending = kotlinx.coroutines.CompletableDeferred<AppResult<ShoppingCart>>()
            val vm = HomeViewModel(
                { AppResult.Success(parameters) }, { AppResult.Success(HomeFeatureConfiguration()) },
                { AppResult.Success(emptyList()) }, { AppResult.Success(social) }, { pending.await() },
                prepareEntry, preparePools, prepareOther, {
                    content
                }, { AppResult.Success(NotificationReadResult.Confirmed) }, FixedCoroutineDispatchers(dispatcher),
                selectHomeResaleOffers = br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
                    br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.parse("2026-10-21T18:00:00Z") }
                )
            )
            try {
                advanceUntilIdle()
                vm.refreshCart()
                advanceUntilIdle()
                assertTrue(vm.state.value.loadingCart)
                content.value = null
                advanceUntilIdle()
                assertFalse(vm.state.value.loadingCart)
                pending.complete(
                    if (failure) {
                        AppResult.Failure(AppError.Network)
                    } else {
                        AppResult.Success(ShoppingCart(99, BigDecimal.TEN, emptyList()))
                    }
                )
                advanceUntilIdle()
                assertEquals(BigDecimal.ZERO, vm.state.value.cartTotal)
                assertFalse(vm.state.value.loadingCart)
            } finally {
                vm.viewModelScope.cancel()
            }
        }
    }

    @Test fun `post login content restores cart header and payment notice while retaining the lower priority notification`() =
        runTest(dispatcher) {
            val notification = AccountNotification(5, "Título", "Mensagem", null, null)
            val content = MutableStateFlow<AuthenticatedSessionContent?>(
                AuthenticatedSessionContent(
                    ShoppingCart(8, BigDecimal("70"), listOf(CartItem(10, CartItemKind.INDIVIDUAL_BET, "Mega", "", BigDecimal("70")))),
                    notification,
                    true
                )
            )
            val vm = viewModel(AppResult.Success(parameters), sessionContent = { content }) { AppResult.Success(social) }
            try {
                advanceUntilIdle()
                assertEquals(BigDecimal("70"), vm.state.value.cartTotal)
                assertEquals(1, vm.state.value.cartItemCount)
                assertEquals(notification, vm.state.value.latestNotification)
                assertTrue(vm.state.value.unidentifiedPayment)
                assertFalse(vm.state.value.paymentNoticeAcknowledged)
                assertTrue(vm.acknowledgeUnidentifiedPayment())
                assertFalse(vm.acknowledgeUnidentifiedPayment())
                // Mantém o indicador do backend: confirmar não torna elegível uma notificação simultânea.
                assertTrue(vm.state.value.unidentifiedPayment)
                assertEquals(notification, vm.state.value.latestNotification)
                content.value = null
                advanceUntilIdle()
                assertFalse(vm.state.value.unidentifiedPayment)
                assertFalse(vm.state.value.paymentNoticeAcknowledged)
                assertNull(vm.state.value.latestNotification)
                assertEquals(BigDecimal.ZERO, vm.state.value.cartTotal)
                assertEquals(0, vm.state.value.cartItemCount)
            } finally {
                vm.viewModelScope.cancel()
            }
        }

    @Test fun `cart header uses flat count after login and counts combo bets rather than cards on refresh`() = runTest(dispatcher) {
        val combo = CartItem(8, CartItemKind.COMBO, "Combo", "", BigDecimal("9.25"), comboBetCount = 2)
        val initial = ShoppingCart(21, BigDecimal("100"), listOf(combo), totalBetCount = 1001)
        val refreshed = ShoppingCart(
            21,
            BigDecimal("12.75"),
            listOf(
                combo,
                CartItem(9, CartItemKind.INDIVIDUAL_BET, "Mega", "", BigDecimal("3.50"))
            )
        )
        val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(initial, null, false))
        val vm = HomeViewModel(
            { AppResult.Success(parameters) }, { AppResult.Success(HomeFeatureConfiguration()) },
            { AppResult.Success(emptyList()) }, { AppResult.Success(social) }, { AppResult.Success(refreshed) },
            prepareEntry, preparePools, prepareOther, {
                content
            }, { AppResult.Success(NotificationReadResult.Confirmed) }, FixedCoroutineDispatchers(dispatcher),
            selectHomeResaleOffers = br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
                br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.parse("2026-10-21T18:00:00Z") }
            )
        )
        try {
            advanceUntilIdle()
            assertEquals(1001, vm.state.value.cartItemCount)
            assertEquals(BigDecimal("100"), vm.state.value.cartTotal)
            vm.refreshCart()
            advanceUntilIdle()
            assertEquals(3, vm.state.value.cartItemCount)
            assertEquals(BigDecimal("12.75"), vm.state.value.cartTotal)
            content.value = null
            advanceUntilIdle()
            assertEquals(0, vm.state.value.cartItemCount)
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    @Test fun `ordinary notification does not fabricate payment warning and new session resets acknowledgment`() = runTest(dispatcher) {
        val content = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, null, false))
        val vm = viewModel(AppResult.Success(parameters), sessionContent = { content }) { AppResult.Success(social) }
        try {
            advanceUntilIdle()
            assertFalse(vm.acknowledgeUnidentifiedPayment())
            content.value = AuthenticatedSessionContent(null, null, true)
            advanceUntilIdle()
            assertTrue(vm.acknowledgeUnidentifiedPayment())
            content.value = null
            advanceUntilIdle()
            content.value = AuthenticatedSessionContent(null, null, true)
            advanceUntilIdle()
            assertFalse(vm.state.value.paymentNoticeAcknowledged)
            assertTrue(vm.acknowledgeUnidentifiedPayment())
        } finally {
            vm.viewModelScope.cancel()
        }
    }

    private val dispatcher = StandardTestDispatcher()
    private val parameters = BettingParameters(
        dailyLimit = BigDecimal("500"),
        minimumCartAmount = BigDecimal("30"),
        maximumCartBets = 50,
        games = listOf(
            LotteryGameParameters(
                LotteryModality(
                    2,
                    "Mega-Sena",
                    "MEGA_SENA"
                ),
                2900, "10/09/2026", 60, 6, 20, 15, emptyList(), emptyList()
            )
        )
    )
    private val social = SocialTransfers(
        BigDecimal("8787475955"), BigDecimal.TEN, "03/09/2026",
        BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN,
        BigDecimal.TEN, BigDecimal.TEN, BigDecimal("40")
    )
    private var entryResult: AppResult<BetEntryDecision> = AppResult.Success(BetEntryDecision.Ready(parameters.games.single()))
    private val entryRequests = mutableListOf<BetEntryRequest>()
    private val prepareEntry = PrepareBetEntryUseCase {
        entryRequests += it
        entryResult
    }
    private var poolResult: AppResult<PoolEntryDecision> = AppResult.Success(PoolEntryDecision.Ready(PoolEntryRequest(2, "ESPECIAL"), true))
    private val poolRequests = mutableListOf<PoolEntryRequest>()
    private val preparePools = PreparePoolEntryUseCase {
        poolRequests += it
        poolResult
    }
    private var otherResult: AppResult<HomeBettingDecision> = AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.INSTANT))
    private val otherRequests = mutableListOf<HomeBettingAction>()
    private val prepareOther = PrepareHomeBettingActionUseCase {
        otherRequests += it
        otherResult
    }

    @Test fun `loads betting parameters and social transfers once`() = runTest(dispatcher) {
        var socialCalls = 0
        val viewModel = viewModel(
            parametersResult = AppResult.Success(parameters),
            socialResult = {
                socialCalls++
                AppResult.Success(social)
            }
        )
        advanceUntilIdle()
        assertEquals(parameters, viewModel.state.value.parameters)
        assertFalse(viewModel.state.value.loading)
        assertNull(viewModel.state.value.errorMessage)
        assertEquals(listOf(7), viewModel.state.value.combos.map(BetCombo::id))

        viewModel.loadSocialTransfers()
        viewModel.loadSocialTransfers()
        advanceUntilIdle()
        assertEquals(social, viewModel.state.value.socialTransfers)
        assertEquals(1, socialCalls)
        assertFalse(viewModel.state.value.loadingSocialTransfers)
        viewModel.loadSocialTransfers()
        assertEquals(1, socialCalls)

        viewModel.refreshCart()
        viewModel.refreshCart()
        advanceUntilIdle()
        assertEquals(BigDecimal("25"), viewModel.state.value.cartTotal)
        assertEquals(1, viewModel.state.value.cartItemCount)
        assertFalse(viewModel.state.value.loadingCart)
    }

    @Test fun `resale-only response opens home instead of recovery`() = runTest(dispatcher) {
        val poolsOnly = parameters.copy(
            games = emptyList(),
            availablePoolModalities = listOf(
                br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability(
                    2,
                    "MEGA_SENA",
                    "NORMAL",
                    2900,
                    "10/09/2099",
                    "19:00:00",
                    modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
                )
            )
        )
        val vm = viewModel(AppResult.Success(poolsOnly), socialResult = { AppResult.Failure(AppError.Network) })
        advanceUntilIdle()
        assertFalse(vm.state.value.parametersUnavailable)
        assertNull(vm.state.value.errorMessage)
        assertEquals(poolsOnly, vm.state.value.parameters)
        vm.viewModelScope.cancel()
    }

    @Test fun `surfaces empty and failed parameters and permits retry`() = runTest(dispatcher) {
        var result: AppResult<BettingParameters> = AppResult.Success(parameters.copy(games = emptyList()))
        val viewModel = HomeViewModel(
            GetBettingParametersUseCase { result },
            GetHomeFeatureConfigurationUseCase { AppResult.Failure(AppError.Network) },
            GetBetCombosUseCase { AppResult.Failure(AppError.Network) },
            GetSocialTransfersUseCase { AppResult.Failure(AppError.Network) },
            GetCurrentCartUseCase { AppResult.Failure(AppError.Network) },
            prepareEntry,
            preparePools,
            prepareOther,
            { kotlinx.coroutines.flow.flowOf(null) },
            { AppResult.Success(NotificationReadResult.Confirmed) },
            FixedCoroutineDispatchers(dispatcher),
            selectHomeResaleOffers = br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
                br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.parse("2026-10-21T18:00:00Z") }
            )
        )
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("Nenhuma"))

        result = AppResult.Failure(AppError.Network)
        viewModel.load()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("Não foi possível"))
        assertFalse(viewModel.state.value.loading)

        viewModel.loadSocialTransfers()
        viewModel.refreshCart()
        advanceUntilIdle()
        assertNull(viewModel.state.value.socialTransfers)
        assertFalse(viewModel.state.value.loadingSocialTransfers)
        assertEquals(BigDecimal.ZERO, viewModel.state.value.cartTotal)
        assertFalse(viewModel.state.value.loadingCart)
    }

    @Test fun `pending parameter load suppresses concurrent calls and releases the guard after completion`() = runTest(dispatcher) {
        val response = CompletableDeferred<AppResult<BettingParameters>>()
        var requests = 0
        val vm = viewModel(
            AppResult.Success(parameters),
            parametersSource = {
                requests++
                response.await()
            },
            socialResult = { AppResult.Success(social) }
        )
        vm.load()
        vm.load()
        runCurrent()
        assertEquals(1, requests)
        assertTrue(vm.state.value.loading)
        assertNull(vm.state.value.parameters)
        response.complete(AppResult.Success(parameters))
        advanceUntilIdle()
        assertEquals(parameters, vm.state.value.parameters)
        assertFalse(vm.state.value.loading)
        vm.load()
        advanceUntilIdle()
        assertEquals(2, requests)
        assertFalse(vm.state.value.loading)
        vm.viewModelScope.cancel()
    }

    @Test fun `failed social transfers allow one retry and successful data prevents more requests`() = runTest(dispatcher) {
        var requests = 0
        var response: AppResult<SocialTransfers> = AppResult.Failure(AppError.Network)
        val vm = viewModel(AppResult.Success(parameters), socialResult = {
            requests++
            response
        })
        advanceUntilIdle()
        vm.loadSocialTransfers()
        vm.loadSocialTransfers()
        advanceUntilIdle()
        assertEquals(1, requests)
        assertNull(vm.state.value.socialTransfers)
        assertFalse(vm.state.value.loadingSocialTransfers)
        response = AppResult.Success(social)
        vm.loadSocialTransfers()
        vm.loadSocialTransfers()
        advanceUntilIdle()
        assertEquals(2, requests)
        assertEquals(social, vm.state.value.socialTransfers)
        assertFalse(vm.state.value.loadingSocialTransfers)
        vm.loadSocialTransfers()
        advanceUntilIdle()
        assertEquals(2, requests)
        vm.viewModelScope.cancel()
    }

    @Test fun `clearing viewmodel cancels pending parameters and stops later session content`() = runTest(dispatcher) {
        val response = CompletableDeferred<AppResult<BettingParameters>>()
        val sessions = MutableStateFlow<AuthenticatedSessionContent?>(null)
        var loadFinished = false
        val vm = viewModel(
            AppResult.Success(parameters),
            parametersSource = {
                try {
                    response.await()
                } finally {
                    loadFinished = true
                }
            },
            sessionContent = { sessions },
            socialResult = { AppResult.Success(social) }
        )
        val store = ViewModelStore().apply { put("home", vm) }
        runCurrent()
        assertTrue(vm.state.value.loading)
        assertFalse(loadFinished)
        store.clear()
        runCurrent()
        assertTrue(loadFinished)
        assertFalse(vm.state.value.loading)
        val afterClear = vm.state.value
        sessions.value = AuthenticatedSessionContent(null, AccountNotification(77L, "new", "later", null, null), true)
        response.complete(AppResult.Success(parameters))
        runCurrent()
        assertEquals(afterClear, vm.state.value)
        assertNull(vm.state.value.parameters)
    }

    private fun viewModel(
        parametersResult: AppResult<BettingParameters>,
        parametersSource: suspend () -> AppResult<BettingParameters> = { parametersResult },
        sessionContent: br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase = {
            kotlinx.coroutines.flow.flowOf(null)
        },
        readNotification: br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase = {
            AppResult.Success(NotificationReadResult.Confirmed)
        },
        network: NetworkAvailability = NetworkAvailability { ConnectionAvailability.UNKNOWN },
        unreadCount: br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase? = null,
        socialResult: suspend () -> AppResult<SocialTransfers>,
    ) = HomeViewModel(
        GetBettingParametersUseCase { parametersSource() },
        GetHomeFeatureConfigurationUseCase {
            AppResult.Success(
                HomeFeatureConfiguration(
                    poolSalesEnabled = true,
                    comboBettingEnabled = true,
                    rapidBettingEnabled = true
                )
            )
        },
        GetBetCombosUseCase {
            AppResult.Success(
                listOf(
                    BetCombo(0, "Inválido", "", false, BigDecimal.ZERO, emptyList()),
                    BetCombo(7, "Combo", "", false, BigDecimal.TEN, emptyList())
                )
            )
        },
        GetSocialTransfersUseCase { socialResult() },
        GetCurrentCartUseCase {
            AppResult.Success(
                ShoppingCart(
                    id = 1,
                    total = BigDecimal("25"),
                    items = listOf(CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal("25")))
                )
            )
        },
        prepareEntry,
        preparePools,
        prepareOther,
        sessionContent,
        readNotification,
        FixedCoroutineDispatchers(dispatcher),
        network,
        java.time.Clock.fixed(java.time.Instant.parse("2026-09-07T12:00:00Z"), java.time.ZoneOffset.UTC),
        unreadCount,
        selectHomeResaleOffers = br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
            br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.parse("2026-10-21T18:00:00Z") }
        )
    )

    @Test fun `header reveal survives refresh and parameter recovery but starts hidden for a new home`() = runTest(dispatcher) {
        var result: AppResult<BettingParameters> = AppResult.Success(parameters)
        val vm = viewModel(result, parametersSource = { result }) { AppResult.Success(social) }
        var nextHome: HomeViewModel? = null
        try {
            assertFalse(vm.state.value.headerActionsRevealed)
            advanceUntilIdle()
            // Concluir apenas a requisição ainda exige aguardar o primeiro quadro de conteúdo.
            assertFalse(vm.state.value.headerActionsRevealed)
            vm.revealHeaderActions()
            assertTrue(vm.state.value.headerActionsRevealed)

            result = AppResult.Failure(AppError.Network)
            vm.load()
            assertTrue(vm.state.value.loading)
            assertTrue(vm.state.value.headerActionsRevealed)
            advanceUntilIdle()
            assertTrue(vm.state.value.parametersUnavailable)
            assertTrue(vm.state.value.headerActionsRevealed)

            result = AppResult.Success(parameters)
            vm.load()
            advanceUntilIdle()
            assertFalse(vm.state.value.parametersUnavailable)
            assertTrue(vm.state.value.headerActionsRevealed)

            nextHome = viewModel(result) { AppResult.Success(social) }
            advanceUntilIdle()
            assertFalse(nextHome.state.value.headerActionsRevealed)
        } finally {
            vm.viewModelScope.cancel()
            nextHome?.viewModelScope?.cancel()
        }
    }

    @Test fun `parameter recovery retains home and cart data and waits for nonempty response`() = runTest(dispatcher) {
        var calls = 0
        var result: AppResult<BettingParameters> = AppResult.Success(parameters)
        val vm = viewModel(result, parametersSource = {
            calls++
            result
        }) { AppResult.Success(social) }
        vm.load()
        advanceUntilIdle()
        assertEquals(1, calls)
        vm.refreshCart()
        advanceUntilIdle()

        result = AppResult.Success(parameters.copy(games = emptyList()))
        vm.load()
        advanceUntilIdle()
        assertTrue(vm.state.value.parametersUnavailable)
        assertEquals(parameters, vm.state.value.parameters)
        assertEquals(BigDecimal("25"), vm.state.value.cartTotal)
        assertNull(vm.state.value.parameterRetryMessage)
        vm.startBet(2, "NORMAL", 2900)
        assertTrue(entryRequests.isEmpty())

        vm.load()
        vm.load()
        vm.dismissParametersUnavailable()
        assertTrue(vm.state.value.parametersUnavailable)
        assertTrue(vm.state.value.loading)
        advanceUntilIdle()
        assertEquals(3, calls)
        assertEquals("Não foi possível concluir esta operação. Por favor, tente novamente.", vm.state.value.parameterRetryMessage)
        vm.dismissParameterRetryMessage()
        assertNull(vm.state.value.parameterRetryMessage)

        result = AppResult.Failure(AppError.Network)
        vm.load()
        advanceUntilIdle()
        assertTrue(vm.state.value.parametersUnavailable)
        assertEquals(parameters, vm.state.value.parameters)
        assertTrue(vm.state.value.parameterRetryMessage != null)

        result = AppResult.Success(parameters.copy(minimumCartAmount = BigDecimal("40")))
        vm.load()
        advanceUntilIdle()
        assertFalse(vm.state.value.parametersUnavailable)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.parameterRetryMessage)
        assertEquals(BigDecimal("40"), vm.state.value.parameters?.minimumCartAmount)
        assertEquals(BigDecimal("25"), vm.state.value.cartTotal)
        result = AppResult.Failure(AppError.Network)
        vm.load()
        advanceUntilIdle()
        vm.dismissParametersUnavailable()
        assertFalse(vm.state.value.parametersUnavailable)
    }

    @Test fun `carousel entry preserves contest and suppresses concurrent and unconsumed clicks`() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Success(parameters)) { AppResult.Success(social) }
        vm.startBet(2, "ESPECIAL", 3000)
        assertTrue(entryRequests.isEmpty()) // Os parâmetros iniciais ainda estão carregando.
        advanceUntilIdle()
        vm.startBet(2, "ESPECIAL", 3000)
        vm.startBet(2, "NORMAL", 2900)
        assertTrue(vm.state.value.preparingBet)
        assertNull(vm.state.value.betEntryDecision)
        advanceUntilIdle()
        assertEquals(listOf(BetEntryRequest(2, "ESPECIAL", 3000)), entryRequests)
        assertFalse(vm.state.value.preparingBet)
        assertEquals((entryResult as AppResult.Success).value, vm.state.value.betEntryDecision)
        vm.startBet(2, "NORMAL", 2900)
        assertEquals(1, entryRequests.size)
        vm.consumeBetEntryDecision()
        assertNull(vm.state.value.betEntryDecision)
        entryResult = AppResult.Success(BetEntryDecision.CompleteAccount(PostLoginDestination.TERMS))
        vm.startBet(2, "NORMAL", 2900)
        advanceUntilIdle()
        assertEquals(BetEntryDecision.CompleteAccount(PostLoginDestination.TERMS), vm.state.value.betEntryDecision)
    }

    @Test fun `blocked or failed entry stays in home with dismissible alert and retry`() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Success(parameters)) { AppResult.Success(social) }
        advanceUntilIdle()
        listOf(
            AppError.Validation("Suspensão temporária ativa") to "Suspensão temporária ativa",
            AppError.Validation("") to "Não foi possível concluir a operação.",
            AppError.Network to "Não foi possível acessar o serviço. Verifique sua conexão.",
            AppError.Unexpected to "Não foi possível concluir a operação. Tente novamente."
        ).forEach { (error, message) ->
            entryResult = AppResult.Failure(error)
            vm.startBet(2, "NORMAL", 2900)
            assertNull(vm.state.value.betEntryError)
            advanceUntilIdle()
            assertEquals(message, vm.state.value.betEntryError)
            assertNull(vm.state.value.betEntryDecision)
            assertFalse(vm.state.value.preparingBet)
            assertEquals(parameters, vm.state.value.parameters)
            vm.dismissBetEntryError()
            assertNull(vm.state.value.betEntryError)
        }
    }

    @Test fun `pool and individual actions share a single entry lock`() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Success(parameters)) { AppResult.Success(social) }
        vm.startPools(2, "ESPECIAL")
        assertTrue(poolRequests.isEmpty())
        advanceUntilIdle()
        vm.startPools(2, "ESPECIAL")
        vm.startPools(2, "NORMAL")
        vm.startBet(2, "NORMAL", 2900)
        assertTrue(vm.state.value.preparingBet)
        advanceUntilIdle()
        assertEquals(listOf(PoolEntryRequest(2, "ESPECIAL")), poolRequests)
        assertTrue(entryRequests.isEmpty())
        assertEquals((poolResult as AppResult.Success).value, vm.state.value.poolEntryDecision)
        vm.startBet(2, "NORMAL", 2900)
        vm.startPools(2, "NORMAL")
        assertEquals(1, poolRequests.size)
        vm.consumePoolEntryDecision()
        assertNull(vm.state.value.poolEntryDecision)
        vm.startBet(2, "NORMAL", 2900)
        advanceUntilIdle()
        vm.startPools(2, "NORMAL")
        advanceUntilIdle()
        assertEquals(1, poolRequests.size)
    }

    @Test fun `pool failure preserves home and retry can request account completion`() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Success(parameters)) { AppResult.Success(social) }
        advanceUntilIdle()
        poolResult = AppResult.Failure(AppError.Validation("Suspensão ativa"))
        vm.startPools(2, "ESPECIAL")
        advanceUntilIdle()
        assertEquals("Suspensão ativa", vm.state.value.betEntryError)
        assertFalse(vm.state.value.preparingBet)
        assertNull(vm.state.value.poolEntryDecision)
        assertEquals(parameters, vm.state.value.parameters)
        poolResult = AppResult.Success(PoolEntryDecision.CompleteAccount(PostLoginDestination.REGISTRATION))
        vm.startPools(2, "ESPECIAL")
        advanceUntilIdle()
        assertNull(vm.state.value.betEntryError)
        assertEquals((poolResult as AppResult.Success).value, vm.state.value.poolEntryDecision)
    }

    @Test fun `other betting actions share lock and decisions must be consumed`() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Success(parameters)) { AppResult.Success(social) }
        vm.startOtherBetting(HomeBettingAction.INSTANT)
        assertTrue(otherRequests.isEmpty())
        advanceUntilIdle()
        vm.startOtherBetting(HomeBettingAction.INSTANT)
        vm.startOtherBetting(HomeBettingAction.COMBO)
        vm.startPools(2, "NORMAL")
        vm.startBet(2, "NORMAL", 2900)
        assertTrue(vm.state.value.preparingBet)
        advanceUntilIdle()
        assertEquals(listOf(HomeBettingAction.INSTANT), otherRequests)
        assertTrue(poolRequests.isEmpty())
        assertTrue(entryRequests.isEmpty())
        assertEquals((otherResult as AppResult.Success).value, vm.state.value.otherBettingDecision)
        vm.startOtherBetting(HomeBettingAction.RAPID)
        advanceUntilIdle()
        assertEquals(1, otherRequests.size)
        vm.consumeOtherBettingDecision()
        assertNull(vm.state.value.otherBettingDecision)
        vm.externalBettingFailed()
        assertEquals("Não foi possível abrir o navegador. Tente novamente.", vm.state.value.betEntryError)
        vm.dismissBetEntryError()
        assertNull(vm.state.value.betEntryError)
    }

    @Test fun `other betting failure permits retry and account completion`() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Success(parameters)) { AppResult.Success(social) }
        advanceUntilIdle()
        otherResult = AppResult.Failure(AppError.Validation("Suspensão ativa"))
        vm.startOtherBetting(HomeBettingAction.RAPID)
        advanceUntilIdle()
        assertFalse(vm.state.value.preparingBet)
        assertNull(vm.state.value.otherBettingDecision)
        assertEquals("Suspensão ativa", vm.state.value.betEntryError)
        otherResult = AppResult.Success(HomeBettingDecision.CompleteAccount(PostLoginDestination.TERMS))
        vm.startOtherBetting(HomeBettingAction.RAPID)
        advanceUntilIdle()
        assertNull(vm.state.value.betEntryError)
        assertEquals((otherResult as AppResult.Success).value, vm.state.value.otherBettingDecision)
    }
}
