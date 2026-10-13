package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountStatusViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `registration confirmation redirects once after legacy delay`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = AccountStatusViewModel(
            "account/register-confirmation",
            FixedCoroutineDispatchers(mainDispatcherRule.dispatcher),
        )
        val effect = async { viewModel.effects.first() }

        viewModel.start()
        viewModel.start()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(3_000)
        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        assertEquals(AccountStatusEffect.Completed, effect.await())
    }

    @Test
    fun `personal data confirmation redirects after legacy delay`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = AccountStatusViewModel(
            "account/personal-data-confirmation",
            FixedCoroutineDispatchers(mainDispatcherRule.dispatcher),
        )
        val effect = async { viewModel.effects.first() }

        viewModel.start()
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(AccountStatusEffect.Completed, effect.await())
    }

    @Test
    fun `non confirmation routes do not schedule redirect`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = AccountStatusViewModel(
            "account/change-login",
            FixedCoroutineDispatchers(mainDispatcherRule.dispatcher),
        )

        viewModel.start()
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, mainDispatcherRule.dispatcher.scheduler.currentTime)
    }
}
