package br.gov.caixa.loterias.core.domain.betting

import org.junit.Assert.*
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import java.time.Instant
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntry

class PrepareBetEntryTest {
    @Test fun `ready carries verified parameters without a second simulation request`() = runTest {
        val snapshot = BettingParameters(BigDecimal("500"), BigDecimal("17.50"), 99, listOf(game))
        parameters = AppResult.Success(snapshot)
        val decision = (useCase(2) as AppResult.Success).value as BetEntryDecision.Ready
        assertSame(snapshot, decision.parameters)
        assertEquals(BigDecimal("17.50"), decision.parameters?.minimumCartAmount)
        assertEquals(listOf("access", "parameters", "features"), calls)
    }
    @Test fun `visitor opens public simulation without account request`() = runTest {
        val visitor = PrepareBetEntry(
            VerifyAccountAccessUseCase { error("Visitor must not request account") },
            GetBettingParametersUseCase { parameters },
            GetHomeFeatureConfigurationUseCase { features },
            ServerTimeSource { now },
            isAuthenticated = { false },
        )
        assertEquals(AppResult.Success(BetEntryDecision.Ready(game, (parameters as AppResult.Success).value)), visitor(2))
    }
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 3000, "", 60, 6, 20, 10, emptyList(), emptyList())
    private var access: AppResult<AccountAccess> = AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO))
    private var parameters: AppResult<BettingParameters> = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, listOf(game)))
    private val calls = mutableListOf<String>()
    private var features: AppResult<HomeFeatureConfiguration> = AppResult.Success(HomeFeatureConfiguration(poolSalesEnabled = true))
    private val now = Instant.parse("2026-09-06T21:00:00Z")
    private val useCase = PrepareBetEntry(
        VerifyAccountAccessUseCase { calls += "access"; access },
        GetBettingParametersUseCase { calls += "parameters"; parameters },
        GetHomeFeatureConfigurationUseCase { calls += "features"; features },
        ServerTimeSource { now },
    )

    @Test fun `ready decision uses same modality and first contest without changing selection`() = runTest {
        val special = game.copy(contestType = "ESPECIAL", contestNumber = 3001)
        parameters = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, listOf(special, game)))
        assertEquals(AppResult.Success(BetEntryDecision.Ready(special, (parameters as AppResult.Success).value)), useCase(2))
        assertEquals(listOf("access", "parameters", "features"), calls)
    }

    @Test fun `suspended account cannot load parameters or proceed`() = runTest {
        access = AppResult.Success(AccountAccess(PostLoginDestination.HOME, true, BigDecimal.ZERO))
        val result = useCase(2) as AppResult.Failure
        assertTrue((result.error as AppError.Validation).reason.startsWith("Há uma suspensão temporária ativa para compras."))
        assertEquals(listOf("access"), calls)
    }

    @Test fun `account requirements are preserved before betting`() = runTest {
        PostLoginDestination.entries.filter { it != PostLoginDestination.HOME }.forEach { destination ->
            access = AppResult.Success(AccountAccess(destination, false, BigDecimal.ZERO))
            assertEquals(AppResult.Success(BetEntryDecision.CompleteAccount(destination)), useCase(2))
        }
        assertFalse(calls.contains("parameters"))
    }

    @Test fun `network failures do not grant entry`() = runTest {
        access = AppResult.Failure(AppError.Network)
        assertEquals(access, useCase(2))
        assertEquals(listOf("access"), calls)
        access = AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO))
        parameters = AppResult.Failure(AppError.Unexpected)
        assertEquals(parameters, useCase(2))
    }

    @Test fun `missing modality never substitutes another available game`() = runTest {
        assertTrue(useCase(3) is AppResult.Failure)
    }

    @Test fun `closed contest still opens its simulation outside the pool-only window like legacy`() = runTest {
        val loteca = game.copy(
            modality = LotteryModality(LOTECA_MODALITY_ID, "Loteca", "LOTECA"),
            contestNumber = LOTECA_CONTEST_NUMBER,
            open = false,
            closingDateTime = "05/09/2026 18:00:00"
        )
        val snapshot = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, listOf(game, loteca))
        parameters = AppResult.Success(snapshot)
        val request = BetEntryRequest(LOTECA_MODALITY_ID, loteca.contestType, LOTECA_CONTEST_NUMBER)
        assertEquals(AppResult.Success(BetEntryDecision.Ready(loteca, snapshot)), useCase(request))
        val visitor = PrepareBetEntry(
            VerifyAccountAccessUseCase { error("Visitor must not request account") },
            GetBettingParametersUseCase { parameters },
            GetHomeFeatureConfigurationUseCase { features },
            ServerTimeSource { now },
            isAuthenticated = { false }
        )
        assertEquals(AppResult.Success(BetEntryDecision.Ready(loteca, snapshot)), visitor(request))
    }

    @Test fun `invalid modality does not invoke backend`() = runTest {
        assertTrue(useCase(0) is AppResult.Failure)
        assertTrue(useCase(-1) is AppResult.Failure)
        assertTrue(calls.isEmpty())
    }

    @Test fun `configuration error never grants entry`() = runTest {
        features = AppResult.Failure(AppError.Network)
        assertEquals(features, useCase(2))
    }

    @Test fun `explicit carousel identity never selects another contest`() = runTest {
        val special = game.copy(contestType = "ESPECIAL", contestNumber = 3001)
        parameters = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, listOf(game, special)))
        assertEquals(
            AppResult.Success(BetEntryDecision.Ready(special, (parameters as AppResult.Success).value)),
            useCase(BetEntryRequest(2, "especial", 3001))
        )
        assertTrue(useCase(BetEntryRequest(2, "ESPECIAL", 3000)) is AppResult.Failure)
        assertTrue(useCase(BetEntryRequest(2, "NORMAL", 2999)) is AppResult.Failure)
        assertTrue(useCase(BetEntryRequest(2, "OUTRO")) is AppResult.Failure)
        assertEquals(
            AppResult.Success(BetEntryDecision.Ready(special, (parameters as AppResult.Success).value)),
            useCase(BetEntryRequest(2, contestNumber = 3001))
        )
    }

    @Test fun `invalid explicit contest fails before backend`() = runTest {
        assertTrue(useCase(BetEntryRequest(2, "")) is AppResult.Failure)
        assertTrue(useCase(BetEntryRequest(2, "NORMAL", 0)) is AppResult.Failure)
        assertTrue(useCase(BetEntryRequest(2, contestNumber = -1)) is AppResult.Failure)
        assertTrue(calls.isEmpty())
    }

    @Test fun `resale window blocks entry only while pool sales are enabled like legacy`() = runTest {
        parameters = AppResult.Success(
            BettingParameters(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                listOf(game.copy(open = false, closingDateTime = "06/09/2026 18:00:00")),
                availablePoolModalities = listOf(
                    PoolModalityAvailability(
                        modalityId = 2,
                        modalityCode = "MEGA_SENA",
                        contestType = game.contestType,
                        contestNumber = 3000,
                        resaleClosingDate = "06/09/2026",
                        resaleClosingTime = "19:00:00"
                    )
                )
            )
        )
        val error = (useCase(2) as AppResult.Failure).error as AppError.Validation
        assertEquals(
            "O registro de apostas individuais para o concurso 3000 foi encerrado. " +
                "Mas ainda dá tempo de comprar seu bolão e concorrer a esse sorteio.\n" +
                "Acesse \"Compre seu bolão\" e participe!",
            error.reason
        )
        features = AppResult.Success(HomeFeatureConfiguration(poolSalesEnabled = false))
        val snapshot = (parameters as AppResult.Success).value
        assertEquals(AppResult.Success(BetEntryDecision.Ready(snapshot.games.single(), snapshot)), useCase(2))
    }

    @Test fun `temporary resale-only card keeps closed-sales explanation without changing contest`() = runTest {
        val pool = PoolModalityAvailability(
            2,
            "MEGA_SENA",
            "NORMAL",
            2999,
            "06/09/2026",
            "19:00:00"
        )
        parameters = AppResult.Success(
            BettingParameters(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                listOf(game),
                availablePoolModalities = listOf(pool)
            )
        )
        val error = (useCase(BetEntryRequest(2, "NORMAL", 2999)) as AppResult.Failure).error as AppError.Validation
        assertTrue(error.reason.contains("concurso 2999"))
        assertTrue(error.reason.contains("Compre seu bolão"))
        features = AppResult.Success(HomeFeatureConfiguration(poolSalesEnabled = false))
        val disabled = (useCase(BetEntryRequest(2, "NORMAL", 2999)) as AppResult.Failure).error as AppError.Validation
        assertFalse(disabled.reason.contains("Compre seu bolão"))
        features = AppResult.Success(HomeFeatureConfiguration(poolSalesEnabled = true))
        parameters = AppResult.Success(
            BettingParameters(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                emptyList(),
                availablePoolModalities = listOf(pool.copy(resaleClosingTime = "17:00:00"))
            )
        )
        val expired = (useCase(BetEntryRequest(2, "NORMAL", 2999)) as AppResult.Failure).error as AppError.Validation
        assertFalse(expired.reason.contains("Compre seu bolão"))
    }
}

private const val LOTECA_MODALITY_ID = 8
private const val LOTECA_CONTEST_NUMBER = 1278
