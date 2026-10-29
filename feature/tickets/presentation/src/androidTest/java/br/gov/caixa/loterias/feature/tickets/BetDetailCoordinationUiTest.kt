package br.gov.caixa.loterias.feature.tickets

import androidx.compose.ui.test.*
import org.junit.Assert.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.contestType
import br.gov.caixa.loterias.feature.tickets.presentation.route.BetDetailDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetRepeatActionsOverlay

/** Caracterização da rota pública com ViewModels reais e fronteiras locais de serviço. */
class BetDetailCoordinationUiTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<BetDetailViewModel>()
    private val bet = ConfirmedBet(41, "Mega-Sena", 2800, "14/10/2026", "Não premiada",
        listOf(1, 2, 3, 4, 5, 6), modalityId = 2, statusId = 9)

    @After fun cancelModels() { compose.runOnIdle { models.forEach { it.viewModelScope.cancel() } } }

    @Test fun failedHydrationCanRetryAndActionsReceiveTheRecoveredBet() {
        val fixture = Fixture().apply { recovery = AppResult.Failure(AppError.Network) }
        val model = fixture.model()
        val viewed = mutableListOf<Long>()
        val edited = mutableListOf<Long>()
        val redeemed = mutableListOf<Long>()
        var cart = 0
        compose.setContent { CaixaDSTheme {
            BetDetailDataRoute(model, {}, { viewed.add(it.id) },
                onRedeemPrize = { redeemed.add(it.id) }, onEdit = { edited.add(it.id) },
                onCart = { cart++ }, cartTotal = BigDecimal("35.00"), cartBetCount = 3)
        } }
        compose.onNodeWithText("Não foi possível consultar esta aposta").assertIsDisplayed()
        compose.onNodeWithTag("bet-detail-footer").assertDoesNotExist()
        compose.runOnIdle { fixture.recovery = AppResult.Success(bet.copy(id = 42, prize = BigDecimal.TEN)) }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Editar aposta").performClick()
        compose.onNodeWithContentDescription("Carrinho de apostas, 3 itens").performClick()
        compose.onNodeWithTag("bet-detail-scroll").performScrollToNode(hasText("Ver resultado"))
        compose.onNodeWithContentDescription("Ver resultado").performClick()
        compose.onNodeWithTag("bet-detail-scroll").performScrollToNode(hasText("Resgatar prêmio"))
        compose.onNodeWithContentDescription("Resgatar prêmio").performClick()
        compose.runOnIdle {
            assertEquals(2, fixture.loads)
            assertEquals(listOf(42L), viewed)
            assertEquals(listOf(42L), edited)
            assertEquals(listOf(42L), redeemed)
            assertEquals(1, cart)
        }
    }

    @Test fun favoriteEditorCanCancelAndConflictRequiresConsentBeforeSaving() {
        val fixture = Fixture().apply { validation = AppResult.Failure(AppError.Validation("Duplicada")) }
        val model = fixture.model()
        compose.setContent { CaixaDSTheme { BetDetailDataRoute(model, {}, {}) } }
        compose.onNodeWithText("Favoritar aposta").performClick()
        compose.onNodeWithContentDescription("Informe o nome da aposta").performTextInput("Minha aposta")
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertFalse(model.state.value.favoriteExpanded)
            assertTrue(fixture.saved.isEmpty())
            assertTrue(fixture.validated.isEmpty())
        }
        compose.onNodeWithText("Favoritar aposta").performClick()
        compose.onNodeWithContentDescription("Informe o nome da aposta").assertTextEquals("Minha aposta")
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("Duplicada").assertIsDisplayed()
        compose.runOnIdle {
            assertTrue(model.state.value.favoriteOverwriteConfirmation)
            assertTrue(model.state.value.favoriteExpanded)
            assertTrue(fixture.saved.isEmpty())
        }
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertTrue(fixture.saved.isEmpty()) }
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("Duplicada").assertIsDisplayed()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Aposta favorita").assertIsDisplayed()
        compose.onNodeWithContentDescription("Informe o nome da aposta").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, fixture.validated.size)
            assertEquals(1, fixture.saved.size)
            assertEquals("Minha aposta", fixture.saved.single().name)
            assertEquals(listOf(listOf(1, 2, 3, 4, 5, 6)), fixture.saved.single().numberColumns)
            assertTrue(model.state.value.favoriteSaved)
        }
    }

    @Test fun repeatSelectionUsesChosenContestAndConsumesSuccessBeforeCartCallback() {
        val fixture = Fixture().apply { games = listOf(game("NORMAL"), game("ESPECIAL")) }
        val model = fixture.model()
        var changes = 0
        compose.setContent { CaixaDSTheme {
            BetRepeatActionsOverlay(model, onCartChanged = {
                assertFalse(model.state.value.cartAdditionConfirmed)
                changes++
            })
        } }
        compose.runOnIdle { model.addCurrentBetToCart() }
        compose.onNodeWithText("Tipo de concurso").assertIsDisplayed()
        compose.onNodeWithText("Mega da Virada").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals("ESPECIAL", fixture.added.single().contestType)
            assertEquals(listOf(1, 2, 3, 4, 5, 6), fixture.added.single().numbers)
            assertEquals(1, changes)
        }
        compose.onNodeWithText("Entendi").assertDoesNotExist()
    }

    @Test fun repeatConversionCanBeDeclinedAndOnlyConsentAddsTheConvertedBet() {
        val fixture = Fixture().apply { games = listOf(game("NORMAL")) }
        val model = fixture.model(bet.copy(contestTypeId = 2))
        compose.setContent { CaixaDSTheme { BetRepeatActionsOverlay(model) } }
        compose.runOnIdle { model.addCurrentBetToCart() }
        compose.onNodeWithText("A modalidade Mega da Virada não está vigente.", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertNull(model.state.value.repeatConversion)
            assertTrue(fixture.added.isEmpty())
            model.addCurrentBetToCart()
        }
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle {
            assertEquals("NORMAL", fixture.added.single().contestType)
            assertTrue(model.state.value.cartAdditionConfirmed)
        }
    }

    @Test fun serviceRedirectIsConsumedBeforeNavigationAndDoesNotRepeatOnRecomposition() {
        val fixture = Fixture().apply { rejection = ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN) }
        val model = fixture.model()
        val count = mutableStateOf(0)
        val redirects = mutableListOf<ServiceRedirect>()
        compose.setContent { CaixaDSTheme {
            BetDetailDataRoute(model, {}, {}, onServiceRedirect = {
                assertNull(model.state.value.serviceFailure)
                redirects.add(it)
            }, cartBetCount = count.value)
        } }
        compose.runOnIdle {
            assertEquals(listOf(ServiceRedirect.LOGIN), redirects)
            assertFalse(model.state.value.detailsReady)
            count.value = 4
        }
        compose.runOnIdle { assertEquals(listOf(ServiceRedirect.LOGIN), redirects) }
        compose.onNodeWithTag("bet-detail-footer").assertDoesNotExist()
    }

    @Test fun replacementViewModelOwnsReceiptCollectionAndInvalidPdfShowsOpenFailure() {
        val oldFixture = Fixture()
        val newFixture = Fixture()
        val oldModel = oldFixture.model()
        val newModel = newFixture.model(bet.copy(id = 42))
        val current = mutableStateOf(oldModel)
        val count = mutableStateOf(0)
        compose.setContent { CaixaDSTheme { BetDetailDataRoute(current.value, {}, {}, cartBetCount = count.value) } }
        compose.waitForIdle()
        compose.runOnIdle { current.value = newModel }
        compose.onNodeWithTag("bet-detail-scroll").performScrollToNode(hasText("Gerar comprovante"))
        compose.onNodeWithContentDescription("Gerar comprovante").performClick()
        compose.waitUntil(5_000) { newModel.state.value.errorMessage?.startsWith("Não foi possível abrir o comprovante.") == true }
        compose.onNodeWithText("Não foi possível abrir o comprovante.", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(listOf(42L), newFixture.downloads)
            assertTrue(oldFixture.downloads.isEmpty())
            oldModel.downloadReceipt()
            count.value = 1
        }
        compose.runOnIdle {
            assertNull(oldModel.state.value.errorMessage)
            assertNull(newModel.state.value.errorMessage)
        }
        compose.onNodeWithText("Não foi possível abrir o comprovante.", substring = true).assertDoesNotExist()
    }

    private inner class Fixture {
        var recovery: AppResult<ConfirmedBet>? = null
        var rejection: ServiceFailure? = null
        var validation: AppResult<Unit> = AppResult.Success(Unit)
        var games: List<LotteryGameParameters> = emptyList()
        var loads = 0
        val downloads = mutableListOf<Long>()
        val validated = mutableListOf<FavoriteBetDraft>()
        val saved = mutableListOf<FavoriteBetDraft>()
        val added = mutableListOf<BetDraft>()
        fun model(initial: ConfirmedBet = bet): BetDetailViewModel = BetDetailViewModel(
            initial,
            RecoverConfirmedBetUseCase { loads++; recovery ?: AppResult.Success(it) },
            DownloadBetReceiptUseCase { downloads.add(it); AppResult.Success("invalid PDF".toByteArray()) },
            ValidateFavoriteBetUseCase { validated.add(it); validation },
            SaveFavoriteBetUseCase { saved.add(it); AppResult.Success(Unit) },
            AddBetToCartUseCase { added.add(it); AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList())) },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            getBettingParameters = GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 20, games))
            },
            loadConfirmedBet = rejection?.let { failure -> LoadConfirmedBetUseCase { ServiceResult.Rejected(failure) } },
        ).also(models::add)
    }

    private fun game(type: String) = LotteryGameParameters(
        LotteryModality(2, "Mega-Sena", "MEGA_SENA", specialName = "Mega da Virada"),
        2800, "14/10/2026", 60, 6, 20, 10, emptyList(), emptyList(), contestType = type,
    )
}
