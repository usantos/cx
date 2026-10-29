package br.gov.caixa.loterias.feature.retail

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextReplacement
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.feature.retail.presentation.route.RetailSearchRoute
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.RetailSearchViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RetailSearchRouteUiTest {
    @get:Rule val compose = createComposeRule()
    private fun retailer(id: Long, code: String, name: String, favorite: Boolean = false) =
        Retailer(id, code, name, "", "", "", "", "", favorite)
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }

    @Test fun `search loader is centered in the visible content`() {
        val response = CompletableDeferred<AppResult<List<Retailer>>>()
        val model = RetailSearchViewModel(
            SearchRetailersUseCase { response.await() },
            SetRetailerFavoriteUseCase { _, _ -> error("No writes requested") },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        )
        model.changeQuery("teste")
        model.searchNow()
        compose.setContent { CaixaDSTheme { RetailSearchRoute(model, {}) } }
        flush()
        val content = compose.onNode(androidx.compose.ui.test.hasTestTag("retail-search-content")).fetchSemanticsNode().boundsInRoot
        val loader = compose.onNode(androidx.compose.ui.test.hasTestTag("retail-search-loader")).fetchSemanticsNode().boundsInRoot
        assertEquals(content.center.x, loader.center.x, 1f)
        assertEquals(content.center.y, loader.center.y, 1f)
        compose.runOnIdle { response.complete(AppResult.Success(emptyList())) }
        flush()
        compose.onNode(androidx.compose.ui.test.hasTestTag("retail-search-loader")).assertDoesNotExist()
    }

    @Test fun `retailer search formats codes and favorite confirmation mutates only selected row`() {
        val response = CompletableDeferred<AppResult<List<Retailer>>>()
        val favoriteCalls = mutableListOf<Pair<Long, Boolean>>()
        val model =
            RetailSearchViewModel(
                SearchRetailersUseCase { response.await() },
                SetRetailerFavoriteUseCase { id, favorite ->
                    favoriteCalls +=
                        id to favorite
                    AppResult.Success(Unit)
                },
                FixedCoroutineDispatchers(Dispatchers.Main.immediate)
            )
        var back = 0
        var help = 0
        compose.setContent { CaixaDSTheme { RetailSearchRoute(model, { back++ }, { help++ }) } }
        compose.onNodeWithText("Nome ou código").assertExists()
        compose.onNodeWithText("Nenhuma Lotérica encontrada").assertExists()
        compose.onNode(hasSetTextAction()).performTextReplacement("casa")
        compose.onNode(hasSetTextAction()).performImeAction()
        flush()
        compose.runOnIdle {
            assertTrue(model.state.value.loading)
            response.complete(AppResult.Success(listOf(retailer(1, "10.000004-01", " Casa Um "), retailer(2, "22", "Casa Dois", true))))
        }
        flush()
        compose.onNodeWithText("000004 - CASA UM").assertExists()
        compose.onNodeWithText("22 - CASA DOIS").assertExists()
        compose.onNodeWithText("000004 - CASA UM").performClick()
        flush()
        compose.onNodeWithText("Favoritar lotérica").assertExists()
        compose.onNodeWithText("Deseja incluir CASA UM na sua lista de favoritas?").assertExists()
        compose.onNodeWithText("Confirmar").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(listOf(1L to true), favoriteCalls)
            assertTrue(model.state.value.retailers.first().favorite)
        }
        compose.onNodeWithText("22 - CASA DOIS").performClick()
        flush()
        compose.onNodeWithText("Remover lotérica favorita").assertExists()
        compose.onNodeWithText("Confirmar").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(listOf(1L to true, 2L to false), favoriteCalls)
            assertFalse(model.state.value.retailers[1].favorite)
        }
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, help)
            assertEquals(1, back)
        }
    }

    @Test fun `short query and service failure show dismissible errors without favorite mutation`() {
        val model =
            RetailSearchViewModel(
                SearchRetailersUseCase {
                    AppResult.Failure(AppError.Network)
                },
                SetRetailerFavoriteUseCase { _, _ -> error("No favorite allowed") },
                FixedCoroutineDispatchers(Dispatchers.Main.immediate)
            )
        compose.setContent { CaixaDSTheme { RetailSearchRoute(model, {}) } }
        compose.onNode(hasSetTextAction()).performTextReplacement("ab")
        compose.onNode(hasSetTextAction()).performImeAction()
        flush()
        compose.onNodeWithText("Informe pelo menos 3 caracteres.").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.onNodeWithText("Informe pelo menos 3 caracteres.").assertDoesNotExist()
        compose.onNode(hasSetTextAction()).performTextReplacement("unknown")
        compose.onNode(hasSetTextAction()).performImeAction()
        flush()
        compose.onNodeWithText("Não foi possível buscar as lotéricas.").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.onNodeWithText("Nenhuma Lotérica encontrada").assertExists()
    }
}
