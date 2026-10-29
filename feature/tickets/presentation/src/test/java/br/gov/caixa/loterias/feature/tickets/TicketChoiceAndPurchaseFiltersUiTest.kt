// Literais numéricos descrevem seleções de loteria, identificadores e dados de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.route.TicketGameChoiceRoute
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class TicketChoiceAndPurchaseFiltersUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun ticketChoiceSelectsEveryLegacyModalityAndSavesExactSelection() {
        val modalities =
            listOf(
                LotteryModality(
                    2,
                    "Mega-Sena",
                    "MEGA_SENA"
                ),
                LotteryModality(
                    8,
                    "Lotofácil",
                    "LOTOFACIL"
                ),
                LotteryModality(
                    3,
                    "Quina",
                    "QUINA"
                ),
                LotteryModality(
                    16,
                    "Lotomania",
                    "LOTOMANIA"
                ),
                LotteryModality(
                    20,
                    "Timemania",
                    "TIMEMANIA"
                ),
                LotteryModality(
                    18,
                    "Dupla Sena",
                    "DUPLA_SENA"
                ),
                LotteryModality(
                    19,
                    "Loteca",
                    "LOTECA"
                ),
                LotteryModality(14, "Lotogol", "LOTOGOL"), LotteryModality(7, "Super Sete", "SUPER_SETE")
            )
        val saved = mutableListOf<Set<Int>>()
        val vm = model(GetLotteryModalitiesUseCase { AppResult.Success(modalities) })
        show { TicketGameChoiceRoute(vm, {}, {}, saved::add) }
        compose.onNodeWithText("Salvar").assertIsNotEnabled()
        compose.onNodeWithText("Super Sete").assertDoesNotExist()
        modalities.dropLast(1).forEach { compose.onNodeWithText(it.name).performScrollTo().performClick() }
        compose.onNodeWithText("Salvar").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(listOf(modalities.dropLast(1).map { it.id }.toSet()), saved) }
        compose.onNodeWithText("Quina").performScrollTo().performClick()
        compose.onNodeWithText("Salvar").performClick()
        compose.runOnIdle { assertEquals(modalities.dropLast(1).map { it.id }.toSet() - 3, saved.last()) }
    }

    @Test fun ticketChoiceFailureCanRetryWithoutPublishingStaleSelection() {
        var calls = 0
        var helps = 0
        var backs = 0
        val vm = model(
            GetLotteryModalitiesUseCase {
                calls++
                if (calls ==
                    1
                ) {
                    AppResult.Failure(AppError.Network)
                } else {
                    AppResult.Success(listOf(LotteryModality(2, "Mega-Sena", "MEGA_SENA")))
                }
            }
        )
        show { TicketGameChoiceRoute(vm, { backs++ }, { helps++ }, {}) }
        compose.onNodeWithText("Não foi possível carregar as modalidades.").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Mega-Sena").assertExists()
        compose.onNodeWithText("Salvar").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Termos de Uso").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(2, calls)
            assertEquals(1, helps)
            assertEquals(1, backs)
        }
    }

    @Test fun emptyTicketCatalogDoesNotEnableSaving() {
        val vm = model(GetLotteryModalitiesUseCase { AppResult.Success(emptyList()) })
        show { TicketGameChoiceRoute(vm, {}, {}, {}) }
        compose.onNodeWithText("Selecione a aposta realizada").assertExists()
        compose.onNodeWithText("Salvar").assertIsNotEnabled()
    }

    @Test fun purchaseFiltersRouteSpecificAndAllOptionsAndActionCallbacks() {
        val methods = mutableListOf<Long?>()
        val statuses = mutableListOf<Long?>()
        var applies = 0
        var clears = 0
        var closes = 0
        show {
            PurchaseFiltersDialog(
                TicketsUiState(
                    paymentMethods = listOf(TicketFilterOption(6, "Pix")),
                    purchaseStatuses = listOf(TicketFilterOption(8, "Finalizada"))
                ),
                PurchaseFiltersActions({
                    closes++
                }, methods::add, statuses::add, { applies++ }, { clears++ })
            )
        }
        compose.onNodeWithText("Pix").performClick()
        compose.onNodeWithText("Todos").performClick()
        compose.onNodeWithText("Finalizada").performClick()
        compose.onNodeWithText("Todas").performClick()
        compose.onNodeWithText("Limpar").performClick()
        compose.onNodeWithText("Aplicar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(6L, null), methods)
            assertEquals(listOf(8L, null), statuses)
            assertEquals(1, applies)
            assertEquals(1, clears)
            assertEquals(1, closes)
        }
    }

    @Test fun purchaseBetFiltersDeduplicateAndPreserveOptionIdentifiers() {
        val contest = mutableListOf<Int?>()
        val status = mutableListOf<Long?>()
        val modality = mutableListOf<Int?>()
        var apply = 0
        var clear = 0
        var close = 0
        val bet = ConfirmedBet(1, "Mega-Sena", 123, "", "", emptyList(), modalityId = 2, statusId = 9)
        show {
            PurchaseBetFiltersDialog(
                TicketsUiState(bets = listOf(bet, bet.copy(id = 2), bet.copy(id = 3, contest = 0))),
                PurchaseBetFiltersActions({
                    close++
                }, contest::add, status::add, modality::add, { apply++ }, { clear++ })
            )
        }
        compose.onAllNodesWithText("123").assertCountEquals(1)
        compose.onNodeWithText("0").assertDoesNotExist()
        compose.onNodeWithText("123").performClick()
        compose.onNodeWithText("Não informada").performClick()
        compose.onNodeWithText("Mega-Sena").performClick()
        compose.onNodeWithText("Todos").performClick()
        compose.onAllNodesWithText("Todas")[0].performClick()
        compose.onAllNodesWithText("Todas")[1].performClick()
        compose.onNodeWithText("Limpar").performClick()
        compose.onNodeWithText("Aplicar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(123, null), contest)
            assertEquals(listOf(9L, null), status)
            assertEquals(listOf(2, null), modality)
            assertEquals(1, apply)
            assertEquals(1, clear)
            assertEquals(1, close)
        }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { content() } }
    }
    private fun model(modalities: GetLotteryModalitiesUseCase) = TicketsViewModel(
        GetConfirmedBetsUseCase {
            AppResult.Success(emptyList())
        },
        GetPurchasesUseCase {
            AppResult.Success(emptyList())
        },
        GetPurchaseBetsUseCase {
            AppResult.Success(emptyList())
        },
        CheckTicketUseCase {
            AppResult.Success(emptyList())
        },
        FixedCoroutineDispatchers(Dispatchers.Main.immediate),
        getModalities = modalities
    )
}
