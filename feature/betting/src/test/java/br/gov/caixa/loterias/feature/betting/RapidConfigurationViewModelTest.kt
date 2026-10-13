package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.AccountAccess
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.FavoriteBet
import br.gov.caixa.loterias.core.domain.betting.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.core.domain.betting.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.RapidBetConfiguration
import br.gov.caixa.loterias.core.domain.betting.SaveRapidBetConfigurationUseCase
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
class RapidConfigurationViewModelTest {
    private val mega = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val quina = LotteryModality(3, "Quina", "QUINA")
    private val configuration = RapidBetConfiguration(
        BigDecimal("30"), BigDecimal("100"), BigDecimal.ZERO,
        setOf(1), setOf(2), listOf(mega), setOf(10),
    )
    private var configurationResult: AppResult<RapidBetConfiguration> = AppResult.Success(configuration)
    private var modalitiesResult: AppResult<List<LotteryModality>> = AppResult.Success(listOf(mega, quina, LotteryModality(0, "", "")))
    private var saveResult: AppResult<RapidBetConfiguration> = AppResult.Success(configuration)
    private var saved: RapidBetConfiguration? = null

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `loads configuration and filters invalid modalities`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        with(viewModel.state.value) {
            assertFalse(loading)
            assertEquals(BigDecimal("30"), minimumAmount)
            assertEquals(setOf(1), requiredNumbers)
            assertEquals(setOf(2), excludedNumbers)
            assertEquals(listOf(2, 3), availableModalities.map { it.id })
            assertEquals(setOf(2), selectedModalityIds)
            assertEquals(setOf(10L), favoriteBetIds)
            assertTrue(canSave)
        }
    }

    @Test fun `selects every available modality when server selection is empty`() = runTest {
        configurationResult = AppResult.Success(configuration.copy(modalities = emptyList()))
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals(setOf(2, 3), viewModel.state.value.selectedModalityIds)
    }

    @Test fun `page and amount controls clamp invalid values`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.setPage(-2)
        assertEquals(0, viewModel.state.value.page)
        viewModel.setPage(99)
        assertEquals(6, viewModel.state.value.page)
        viewModel.setMinimumPrize(BigDecimal("-1"))
        assertEquals(BigDecimal.ZERO, viewModel.state.value.minimumPrize)
        viewModel.setMinimumAmount(BigDecimal("-1"))
        assertEquals(BigDecimal.ZERO, viewModel.state.value.minimumAmount)
        viewModel.setMinimumAmount(BigDecimal("200"))
        assertEquals(BigDecimal("100"), viewModel.state.value.minimumAmount)
        viewModel.setMaximumAmount(BigDecimal("10"))
        assertEquals(BigDecimal("100"), viewModel.state.value.maximumAmount)
        viewModel.setMaximumAmount(BigDecimal("120"))
        assertEquals(BigDecimal("120"), viewModel.state.value.maximumAmount)
    }

    @Test fun `modality and number selections enforce available and opposite sets`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.toggleModality(999)
        assertEquals(setOf(2), viewModel.state.value.selectedModalityIds)
        viewModel.toggleModality(2)
        assertTrue(viewModel.state.value.selectedModalityIds.isEmpty())
        viewModel.toggleModality(3)
        assertEquals(setOf(3), viewModel.state.value.selectedModalityIds)

        viewModel.toggleRequiredNumber(0)
        viewModel.toggleRequiredNumber(3)
        viewModel.toggleRequiredNumber(3)
        assertEquals(setOf(1), viewModel.state.value.requiredNumbers)
        viewModel.toggleRequiredNumber(2)
        assertTrue(viewModel.state.value.warningMessage?.isNotBlank() == true)
        viewModel.dismissMessage()
        assertNull(viewModel.state.value.warningMessage)

        viewModel.toggleExcludedNumber(3)
        viewModel.toggleExcludedNumber(3)
        viewModel.toggleExcludedNumber(1)
        assertTrue(viewModel.state.value.warningMessage?.isNotBlank() == true)
        viewModel.clearRequiredNumbers()
        viewModel.clearExcludedNumbers()
        assertTrue(viewModel.state.value.requiredNumbers.isEmpty())
        assertTrue(viewModel.state.value.excludedNumbers.isEmpty())
    }

    @Test fun `invalid and valid saves expose expected state`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.toggleModality(2)
        viewModel.save()
        assertEquals("Revise a configuração do Rapidão antes de salvar.", viewModel.state.value.errorMessage)
        viewModel.dismissMessage()
        viewModel.toggleModality(3)
        viewModel.save()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.saved)
        assertEquals(listOf(3), saved?.modalities?.map { it.id })
    }

    @Test fun `load and save failures map every app error`() = runTest {
        configurationResult = AppResult.Failure(AppError.Network)
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals("Não foi possível carregar o Rapidão. Verifique sua internet.", viewModel.state.value.errorMessage)

        configurationResult = AppResult.Success(configuration)
        modalitiesResult = AppResult.Failure(AppError.Unexpected)
        viewModel.load()
        advanceUntilIdle()
        assertEquals("Ocorreu um erro inesperado no Rapidão.", viewModel.state.value.errorMessage)

        modalitiesResult = AppResult.Success(listOf(mega))
        viewModel.load()
        advanceUntilIdle()
        saveResult = AppResult.Failure(AppError.Validation(""))
        viewModel.save()
        advanceUntilIdle()
        assertEquals("Não foi possível salvar o Rapidão.", viewModel.state.value.errorMessage)

        saveResult = AppResult.Failure(AppError.Validation("Configuração recusada"))
        viewModel.save()
        advanceUntilIdle()
        assertEquals("Configuração recusada", viewModel.state.value.errorMessage)
    }

    @Test fun `favorite selection loads without clearing saved ids and persists toggles`() = runTest {
        val bet = FavoriteBet(20, 2, "Mega-Sena", "Favorita", listOf(listOf(1, 2, 3, 4, 5, 6)))
        val viewModel = viewModel(GetFavoriteBetsUseCase { AppResult.Success(listOf(bet, bet, bet.copy(id = 0))) })
        advanceUntilIdle()
        assertEquals(listOf(bet), viewModel.state.value.availableFavorites)
        assertEquals(setOf(10L), viewModel.state.value.favoriteBetIds)
        viewModel.toggleFavorite(999)
        assertEquals(setOf(10L), viewModel.state.value.favoriteBetIds)
        viewModel.toggleFavorite(20)
        viewModel.save()
        advanceUntilIdle()
        assertEquals(setOf(10L, 20L), saved?.favoriteBetIds)
        viewModel.toggleFavorite(20)
        assertEquals(setOf(10L), viewModel.state.value.favoriteBetIds)
    }

    @Test fun `favorite failure preserves config and saved selection then supports retry`() = runTest {
        var favorites: AppResult<List<FavoriteBet>> = AppResult.Failure(AppError.Network)
        val viewModel = viewModel(GetFavoriteBetsUseCase { favorites })
        advanceUntilIdle()
        assertFalse(viewModel.state.value.loading)
        assertFalse(viewModel.state.value.favoritesLoading)
        assertTrue(viewModel.state.value.favoritesError?.isNotBlank() == true)
        assertEquals(setOf(10L), viewModel.state.value.favoriteBetIds)
        assertTrue(viewModel.state.value.canSave)
        favorites = AppResult.Success(emptyList())
        viewModel.loadFavorites()
        advanceUntilIdle()
        assertNull(viewModel.state.value.favoritesError)
        assertEquals(setOf(10L), viewModel.state.value.favoriteBetIds)
    }

    @Test fun `amount controls use minimum cart and verified session ceiling`() = runTest {
        val viewModel = viewModel(getParameters = GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal("999"), BigDecimal("20"), 100, emptyList()))
        }, verifyAccess = VerifyAccountAccessUseCase { AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal("200"))) })
        advanceUntilIdle()
        assertEquals(BigDecimal("20"), viewModel.state.value.minimumAllowedAmount)
        assertEquals(BigDecimal("200"), viewModel.state.value.maximumAllowedAmount)
        viewModel.setMinimumAmount(BigDecimal("120"))
        assertEquals(BigDecimal("120"), viewModel.state.value.minimumAmount)
        assertEquals(BigDecimal("125"), viewModel.state.value.maximumAmount)
        viewModel.setMaximumAmount(BigDecimal("1000"))
        assertEquals(BigDecimal("200"), viewModel.state.value.maximumAmount)
        assertFalse(viewModel.setCustomAmount(BigDecimal("201"), false))
        assertFalse(viewModel.setCustomAmount(BigDecimal("19"), true))
        assertFalse(viewModel.setCustomAmount(null, true))
        assertTrue(viewModel.setCustomAmount(BigDecimal("20"), false))
        assertEquals(BigDecimal("20"), viewModel.state.value.minimumAmount)
        assertEquals(BigDecimal("25"), viewModel.state.value.maximumAmount)
        assertTrue(viewModel.state.value.warningMessage?.contains("R$ 25,00") == true)
    }

    @Test fun `failed limit lookup preserves actual values and retries without invented ceiling`() = runTest {
        var result: AppResult<AccountAccess> = AppResult.Failure(AppError.Network)
        val viewModel = viewModel(getParameters = GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal("999"), BigDecimal("20"), 100, emptyList()))
        }, verifyAccess = VerifyAccountAccessUseCase { result })
        advanceUntilIdle()
        assertNull(viewModel.state.value.maximumAllowedAmount)
        assertEquals(BigDecimal("30"), viewModel.state.value.minimumAmount)
        assertEquals(BigDecimal("100"), viewModel.state.value.maximumAmount)
        assertTrue(viewModel.state.value.amountLimitsError?.isNotBlank() == true)
        result = AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal("200")))
        viewModel.loadAmountLimits()
        advanceUntilIdle()
        assertEquals(BigDecimal("200"), viewModel.state.value.maximumAllowedAmount)
        assertNull(viewModel.state.value.amountLimitsError)
    }

    private fun viewModel(getFavorites: GetFavoriteBetsUseCase? = null, getParameters: GetBettingParametersUseCase? = null, verifyAccess: VerifyAccountAccessUseCase? = null): RapidConfigurationViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return RapidConfigurationViewModel(
            GetRapidBetConfigurationUseCase { configurationResult },
            GetLotteryModalitiesUseCase { modalitiesResult },
            SaveRapidBetConfigurationUseCase { value -> saved = value; saveResult },
            FixedCoroutineDispatchers(dispatcher),
            getFavorites,
            getParameters,
            verifyAccess,
        )
    }
}
