// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.presentation.components.CartRow
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteBetCard
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteCartRow
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteConfirmDialog
import br.gov.caixa.loterias.feature.cart.presentation.screen.CartError
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartContent
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartDetailContent
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartsActions
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartsContent
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class CartContentBranchesUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun favoriteCartsLoadingPrioritizesProgressThenErrorThenLoadedEmpty() {
        val state = mutableStateOf(FavoriteCartsUiState(errorMessage = "Falha ao carregar favoritos"))
        var retries = 0
        show { FavoriteCartsContent(state.value, {}, {}, true, FavoriteCartsActions({ retries++ }, {}, {}, {})) }
        compose.onNodeWithText("Falha ao carregar favoritos").assertDoesNotExist()
        compose.runOnIdle { state.value = state.value.copy(loading = false) }
        compose.onNodeWithText("Falha ao carregar favoritos").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle {
            assertEquals(1, retries)
            state.value = FavoriteCartsUiState(loading = false)
        }
        compose.onNodeWithText("Você ainda não possui carrinhos favoritos.").assertExists()
    }

    @Test fun visitorFavoriteCartsSuppressPrivateRowsAndEmptyInvitation() {
        show {
            FavoriteCartsContent(FavoriteCartsUiState(loading = false, carts = listOf(FavoriteCart(1, "Privado"))), {
            }, {}, false, FavoriteCartsActions({}, {}, {}, {}))
        }
        compose.onNodeWithText("Aqui estão todos os seus carrinhos favoritos.").assertExists()
        compose.onNodeWithText("Privado").assertDoesNotExist()
        compose.onNodeWithText("Você ainda não possui carrinhos favoritos.").assertDoesNotExist()
    }

    @Test fun favoriteCartRowsSendTheirOwnIdToAllActions() {
        val events = mutableListOf<String>()
        show {
            FavoriteCartsContent(
                FavoriteCartsUiState(loading = false, carts = listOf(FavoriteCart(42, "Meus jogos"))),
                {
                },
                { events.add("detail:$it") },
                true,
                FavoriteCartsActions({}, { events.add("toggle:$it") }, { events.add("import:$it") }, { events.add("remove:$it") })
            )
        }
        compose.onNodeWithText("Meus jogos").performClick()
        compose.onNodeWithContentDescription("Transformar carrinho").performClick()
        compose.onNodeWithContentDescription("Detalhar carrinho").performClick()
        compose.onNodeWithContentDescription("Excluir carrinho").performClick()
        compose.runOnIdle { assertEquals(listOf("toggle:42", "import:42", "detail:42", "remove:42"), events) }
    }

    @Test fun expandedFavoriteSummariesIncludeCloversAndRecurringBadge() {
        show {
            FavoriteCartRow(
                FavoriteCart(
                    1,
                    "Um nome de carrinho favorito extenso para testar o ajuste do texto"
                ),
                listOf(
                    favorite().copy(
                        modalityId = 9,
                        modality = "Mais Milionária",
                        clovers = listOf(1, 2),
                        cloverCount = 2,
                        recurringDraws = 3
                    )
                ),
                true,
                false,
                {
                },
                {},
                {},
                {}
            )
        }
        compose.onNodeWithText("+milionária").assertExists()
        compose.onNodeWithText("3 num(s) 2 trev(s)").assertExists()
        compose.onNodeWithContentDescription("Teimosinha").assertExists()
    }

    @Test fun collapsedFavoriteDoesNotExposeExpandedBets() {
        show { FavoriteCartRow(FavoriteCart(1, "Jogos"), listOf(favorite()), false, false, {}, {}, {}, {}) }
        compose.onNodeWithText("3 num(s)").assertDoesNotExist()
        compose.onNodeWithText("Jogos").assertExists()
    }

    @Test fun favoriteDetailErrorCanReloadAndOptionalHelpIsAbsent() {
        var retries = 0
        show {
            FavoriteCartDetailContent(FavoriteCartsUiState(loading = false, errorMessage = "Detalhe indisponível"), {
            }, null, { retries++ }, {})
        }
        compose.onNodeWithText("Detalhe indisponível").assertExists()
        compose.onNodeWithContentDescription("Ajuda").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun favoriteDetailRemovalKeepsBetIdentifierAndSupportsHelp() {
        val removed = mutableListOf<Long>()
        var help = 0
        show {
            FavoriteCartDetailContent(FavoriteCartsUiState(loading = false, bets = listOf(favorite().copy(id = 77))), {
            }, { help++ }, {}, removed::add)
        }
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.onNodeWithContentDescription("Excluir aposta").performClick()
        compose.runOnIdle {
            assertEquals(listOf(77L), removed)
            assertEquals(1, help)
        }
    }

    @Test fun surpriseFavoriteMasksNumbersCloversAndTeamComplement() {
        show {
            FavoriteBetCard(
                favorite().copy(
                    modalityId = 20,
                    modality = "Timemania",
                    surprise = true,
                    clovers = listOf(2, 4),
                    cloverCount = 2,
                    favoriteTeam = "Flamengo"
                )
            ) {
            }
        }
        compose.onNodeWithText("XX - XX - XX").assertExists()
        compose.onNodeWithText("Trevos:  XX - XX").assertExists()
        compose.onNodeWithText("???").assertExists()
        compose.onNodeWithText("Flamengo").assertDoesNotExist()
    }

    @Test fun superSevenFavoriteUsesColumnsAndOrdinaryFavoriteMapsHundredToZero() {
        val current = mutableStateOf(favorite().copy(modalityId = 7, numberColumns = listOf(listOf(9), listOf(8))))
        show { FavoriteBetCard(current.value) {} }
        compose.onNodeWithText("9").assertExists()
        compose.onNodeWithText("8").assertExists()
        compose.runOnIdle { current.value = favorite().copy(numbers = listOf(1, 100)) }
        compose.onNodeWithText("01 - 00").assertExists()
        compose.onNodeWithText("9").assertDoesNotExist()
    }

    @Test fun cartRowRendersSelectionsCloversBadgesQuantitiesAndCallbacks() {
        var deletes = 0
        var details = 0
        show {
            CartRow(
                item().copy(
                    selectedNumbers = listOf(listOf(1, 2)),
                    selectedClovers = listOf(3),
                    badges = CartItemBadge.entries.toSet(),
                    numberQuantity = 2,
                    recurringDraws = 3,
                    complementaryLabel = "Time: Flamengo",
                    gameRows = listOf("Flamengo x Vasco")
                ),
                now,
                {
                    deletes++
                },
                { details++ }
            )
        }
        compose.onNodeWithText("01 - 02").assertExists()
        compose.onNodeWithText("Trevos: 03").assertExists()
        compose.onNodeWithText("2 números").assertExists()
        compose.onNodeWithText("3 concursos").assertExists()
        compose.onNodeWithText("Time: Flamengo").assertExists()
        compose.onNodeWithText("Flamengo x Vasco").assertExists()
        compose.onNodeWithContentDescription("Excluir").performClick()
        compose.onNodeWithText("Ver detalhes").performClick()
        compose.runOnIdle {
            assertEquals(1, deletes)
            assertEquals(1, details)
        }
    }

    @Test fun cartRowFallsBackToDescriptionAndCanOmitMutationActions() {
        show { CartRow(item().copy(kind = CartItemKind.COMBO, comboBetCount = 1), now, null, null) }
        compose.onNodeWithText("Descrição do jogo").assertExists()
        compose.onNodeWithText("1 aposta incluída").assertExists()
        compose.onNodeWithText("Você apostou").assertDoesNotExist()
        compose.onNodeWithText("Ver detalhes").assertDoesNotExist()
        compose.onNodeWithContentDescription("Excluir").assertDoesNotExist()
    }

    @Test fun poolRowDistinguishesValidAndExpiredReservation() {
        val current =
            mutableStateOf(item().copy(kind = CartItemKind.POOL_SHARE, poolShare = CartPoolShare("ABC", 1, 10, "2026-10-22T15:10:00Z")))
        show { CartRow(current.value, now, null, null) }
        compose.onNodeWithText("Bolão ABC").assertExists()
        compose.onNodeWithText("01/10").assertExists()
        compose.onNodeWithText("Cotas").assertExists()
        compose.runOnIdle {
            current.value =
                current.value.copy(poolShare = current.value.poolShare!!.copy(expiresAt = "2026-10-22T14:00:00Z"))
        }
        compose.onNodeWithText("Cota expirada").assertExists()
        compose.onNodeWithText("Cotas").assertDoesNotExist()
    }

    @Test fun currentCartEmptyContentDoesNotLeakErrorsOrPopulatedIntro() {
        show {
            CurrentCartContent(
                CartUiState(
                    loading = false,
                    errorMessage = "Falha oculta"
                ),
                PaddingValues(),
                emptyList(),
                now.toEpochMilli(),
                {
                },
                {},
                {}
            )
        }
        compose.onNodeWithText("Seu carrinho está vazio").assertExists()
        compose.onNodeWithText("Falha oculta").assertDoesNotExist()
        compose.onNodeWithText("Confira suas apostas e siga para o pagamento").assertDoesNotExist()
    }

    @Test fun cartErrorAllowsRetryWithExplicitMessage() {
        var retries = 0
        show { CartError("Conexão perdida", PaddingValues()) { retries++ } }
        compose.onNodeWithText("Conexão perdida").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun favoriteConfirmationOffersBothCallbacks() {
        var confirm = 0
        var dismiss = 0
        show { FavoriteConfirmDialog("Excluir carrinho?", { confirm++ }, { dismiss++ }) }
        compose.onNodeWithText("Excluir carrinho?").assertExists()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertEquals(1, confirm)
            assertEquals(1, dismiss)
        }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { content() } }
    }
    private fun favorite() = FavoriteCartBet(1, "Mega-Sena", listOf(1, 2, 3), modalityId = 2)
    private fun item() = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "Descrição do jogo", BigDecimal.TEN, "megasena", 123)
    private val now = Instant.parse("2026-10-22T15:00:00Z")
}
