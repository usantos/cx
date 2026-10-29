package br.gov.caixa.loterias.core.domain.betting

import org.junit.Assert.*
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingAction

class PrepareHomeBettingActionTest {
    private var access: AppResult<AccountAccess> = AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO))
    private var config: AppResult<HomeFeatureConfiguration> = AppResult.Success(HomeFeatureConfiguration(comboBettingEnabled = true, rapidBettingEnabled = true))
    private var featureReads = 0
    private var preferenceReads = 0
    private var rapidCompleted = false
    private val onboarding = object : OnboardingRepository {
        override suspend fun isIntroductionCompleted(): Boolean = error("Unexpected introduction query")
        override suspend fun completeIntroduction(): Unit = error("Unexpected preference write")
        override suspend fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean): Unit =
            error("Entry must not change the legacy flag")
        override suspend fun isTutorialDismissed(tutorial: BettingTutorial): Boolean {
            assertEquals(BettingTutorial.RAPID, tutorial)
            preferenceReads++
            return rapidCompleted
        }
    }
    private val prepare = PrepareHomeBettingAction(
        VerifyAccountAccessUseCase { access },
        GetHomeFeatureConfigurationUseCase {
            featureReads++
            config
        },
        onboarding
    )

    @Test fun `visitor instant lottery does not request private account flags or preferences`() = runTest {
        val visitor = PrepareHomeBettingAction(
            VerifyAccountAccessUseCase { error("Private account request") },
            GetHomeFeatureConfigurationUseCase { error("Unexpected flags") },
            onboarding,
            isAuthenticated = { false }
        )
        assertEquals(AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.INSTANT)), visitor(HomeBettingAction.INSTANT))
    }

    @Test fun `visitor rapid respects tutorial without requesting private account flags`() = runTest {
        val visitor = PrepareHomeBettingAction(
            VerifyAccountAccessUseCase { error("Private account request") },
            GetHomeFeatureConfigurationUseCase { AppResult.Success(HomeFeatureConfiguration(rapidBettingEnabled = true)) },
            onboarding,
            isAuthenticated = { false }
        )
        assertEquals(AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.RAPID_CONFIGURATION)), visitor(HomeBettingAction.RAPID))
        rapidCompleted = true
        assertEquals(AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.RAPID_LIST)), visitor(HomeBettingAction.RAPID))
    }

    @Test fun `suspension and account failure block every action before flags or preferences`() = runTest {
        HomeBettingAction.entries.forEach {
            access = AppResult.Failure(AppError.Network)
            assertEquals(access, prepare(it))
            access = AppResult.Success(AccountAccess(PostLoginDestination.HOME, true, BigDecimal.ZERO))
            val error = (prepare(it) as AppResult.Failure).error as AppError.Validation
            assertTrue(error.reason.startsWith("Há uma suspensão temporária ativa para compras."))
        }
        assertEquals(0, featureReads)
        assertEquals(0, preferenceReads)
    }

    @Test fun `account requirements take precedence over external or internal navigation`() = runTest {
        PostLoginDestination.entries.filter { it != PostLoginDestination.HOME }.forEach { destination ->
            access = AppResult.Success(AccountAccess(destination, false, BigDecimal.ZERO))
            HomeBettingAction.entries.forEach {
                assertEquals(AppResult.Success(HomeBettingDecision.CompleteAccount(destination)), prepare(it))
            }
        }
        assertEquals(0, featureReads)
    }

    @Test fun `instant does not depend on rapid or combo flags and combo does not read tutorial`() = runTest {
        config = AppResult.Failure(AppError.Network)
        assertEquals(AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.INSTANT)), prepare(HomeBettingAction.INSTANT))
        assertEquals(0, featureReads)
        assertEquals(config, prepare(HomeBettingAction.COMBO))
        config = AppResult.Success(HomeFeatureConfiguration(comboBettingEnabled = true))
        assertEquals(AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.COMBOS)), prepare(HomeBettingAction.COMBO))
        assertEquals(0, preferenceReads)
    }

    @Test fun `disabled actions cannot navigate even through stale entry points`() = runTest {
        config = AppResult.Success(HomeFeatureConfiguration())
        assertTrue(prepare(HomeBettingAction.COMBO) is AppResult.Failure)
        assertTrue(prepare(HomeBettingAction.RAPID) is AppResult.Failure)
        assertEquals(0, preferenceReads)
    }

    @Test fun `rapid legacy flag selects configuration or existing list without writing it`() = runTest {
        assertEquals(AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.RAPID_CONFIGURATION)), prepare(HomeBettingAction.RAPID))
        rapidCompleted = true
        assertEquals(AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.RAPID_LIST)), prepare(HomeBettingAction.RAPID))
        assertEquals(2, preferenceReads)
    }
}
