package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.testing.capturePreviewEvidence
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartActions
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartScaffold
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36], qualifiers = "pt-rBR-w480dp-h900dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CurrentCartScaffoldContractUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun authenticatedIndividualCartDelegatesHeaderAndFooterCallbacks() {
        val events = mutableListOf<String>()
        show {
            CartScaffoldFixture(CartUiState(loading = false, authenticated = true), cart(), events)
        }
        compose.onNodeWithText("Carrinho de apostas").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithContentDescription(CLEAR).performClick()
        compose.onNodeWithContentDescription(FAVORITE).performClick()
        compose.onNodeWithTag("simlo.cart.0048").performClick()
        compose.onNodeWithTag("simlo.cart.0050").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(listOf("back", "clear", "favorite", "add", "pay"), events) }
        capturePreviewEvidence(compose, "cart-authenticated-individual")
    }

    @Test fun visitorAndPoolCartKeepClearButSuppressFavorite() {
        val authenticated = mutableStateOf(false)
        val current = mutableStateOf(cart())
        show { CartScaffoldFixture(CartUiState(loading = false, authenticated = authenticated.value), current.value) }
        assertActions(clear = true, favorite = false)
        capturePreviewEvidence(compose, "cart-visitor")
        compose.runOnIdle {
            authenticated.value = true
            current.value = cart(CartItemKind.POOL_SHARE)
        }
        assertActions(clear = true, favorite = false)
        capturePreviewEvidence(compose, "cart-pool")
    }

    @Test fun emptyAndAbsentCartHideActionsAndPaymentButKeepAddAndContent() {
        val current = mutableStateOf<ShoppingCart?>(cart().copy(items = emptyList(), total = BigDecimal.ZERO))
        val events = mutableListOf<String>()
        show { CartScaffoldFixture(CartUiState(loading = false, authenticated = true), current.value, events) }
        assertActions(clear = false, favorite = false)
        compose.onNodeWithTag("simlo.cart.0050").assertDoesNotExist()
        compose.onNodeWithTag(CONTENT).assertIsDisplayed()
        compose.onNodeWithTag("simlo.cart.0048").assertIsEnabled().performClick()
        capturePreviewEvidence(compose, "cart-empty")
        compose.runOnIdle { current.value = null }
        assertActions(clear = false, favorite = false)
        compose.onNodeWithTag("simlo.cart.0050").assertDoesNotExist()
        compose.onNodeWithTag(CONTENT).assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf("add"), events) }
    }

    @Test fun missingCartPrioritizesLoadingThenRetryableErrorThenContent() {
        val current = mutableStateOf(CartUiState(loading = true, errorMessage = "Falha do carrinho"))
        val events = mutableListOf<String>()
        show { CartScaffoldFixture(current.value, null, events) }
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertCountEquals(1)
        compose.onNodeWithText("Falha do carrinho").assertDoesNotExist()
        compose.onNodeWithTag(CONTENT).assertDoesNotExist()
        compose.runOnIdle { current.value = current.value.copy(loading = false) }
        compose.onNodeWithText("Falha do carrinho").assertIsDisplayed()
        compose.onNodeWithTag(CONTENT).assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle {
            assertEquals(listOf("load"), events)
            current.value = current.value.copy(errorMessage = null)
        }
        compose.onNodeWithTag(CONTENT).assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test fun existingCartKeepsContentDuringLoadingOrErrorsAndPaymentRespectsAllBlockers() {
        val current = mutableStateOf(CartUiState(loading = true, errorMessage = "Falha oculta"))
        show { CartScaffoldFixture(current.value, cart()) }
        compose.onNodeWithTag(CONTENT).assertIsDisplayed()
        compose.onNodeWithText("Falha oculta").assertDoesNotExist()
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertCountEquals(0)
        compose.onNodeWithTag("simlo.cart.0050").assertIsNotEnabled()
        compose.runOnIdle { current.value = CartUiState(loading = false, loadingParameters = true) }
        compose.onNodeWithTag("simlo.cart.0050").assertIsNotEnabled()
        compose.runOnIdle { current.value = CartUiState(loading = false, parametersUnavailable = true) }
        compose.onNodeWithTag("simlo.cart.0050").assertIsNotEnabled()
        compose.runOnIdle { current.value = CartUiState(loading = false) }
        compose.onNodeWithTag("simlo.cart.0050").assertIsEnabled()
    }

    @Test fun headerPreservesActionAndIconSizesSpacingAndFooterPadding() {
        show { CartScaffoldFixture(CartUiState(loading = false, authenticated = true), cart()) }
        val clear = compose.onNodeWithContentDescription(CLEAR).fetchSemanticsNode().boundsInRoot
        val favorite = compose.onNodeWithContentDescription(FAVORITE).fetchSemanticsNode().boundsInRoot
        val icon = compose.onNodeWithContentDescription(FAVORITE, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val content = compose.onNodeWithTag(CONTENT).fetchSemanticsNode().boundsInRoot
        val add = compose.onNodeWithTag("simlo.cart.0048").fetchSemanticsNode().boundsInRoot
        val pay = compose.onNodeWithTag("simlo.cart.0050").fetchSemanticsNode().boundsInRoot
        val density = compose.density.density
        assertEquals(EXPECTED_ACTION_SIZE_DP * density, clear.width, GEOMETRY_TOLERANCE_PX)
        assertEquals(EXPECTED_ACTION_SIZE_DP * density, favorite.width, GEOMETRY_TOLERANCE_PX)
        assertEquals(EXPECTED_ICON_SIZE_DP * density, icon.width, GEOMETRY_TOLERANCE_PX)
        assertEquals(EXPECTED_ICON_SIZE_DP * density, icon.height, GEOMETRY_TOLERANCE_PX)
        assertEquals(clear.right, favorite.left, GEOMETRY_TOLERANCE_PX)
        assertEquals(EXPECTED_HEADER_END_DP * density, root.right - favorite.right, GEOMETRY_TOLERANCE_PX)
        assertEquals(EXPECTED_FOOTER_INSET_DP * density, add.left - root.left, GEOMETRY_TOLERANCE_PX)
        assertEquals(EXPECTED_ACTION_SIZE_DP * density, add.height, GEOMETRY_TOLERANCE_PX)
        assertEquals(EXPECTED_ACTION_SIZE_DP * density, pay.height, GEOMETRY_TOLERANCE_PX)
        assertEquals(EXPECTED_FOOTER_GAP_DP * density, pay.top - add.bottom, GEOMETRY_TOLERANCE_PX)
        assertTrue(content.top >= clear.bottom)
        assertTrue(content.bottom <= add.top)
    }

    private fun assertActions(clear: Boolean, favorite: Boolean) {
        compose.onNodeWithContentDescription(CLEAR).apply { if (clear) assertExists() else assertDoesNotExist() }
        compose.onNodeWithContentDescription(FAVORITE).apply { if (favorite) assertExists() else assertDoesNotExist() }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                Box(Modifier.size(480.dp, 900.dp)) { content() }
            }
        }
    }

    @Composable
    private fun CartScaffoldFixture(state: CartUiState, cart: ShoppingCart?, events: MutableList<String> = mutableListOf()) {
        CurrentCartScaffold(
            state,
            cart,
            { events.add("back") },
            { events.add("add") },
            CurrentCartActions({ events.add("clear") }, { events.add("favorite") }, { events.add("pay") }, { events.add("load") })
        ) { padding -> Box(Modifier.fillMaxSize().padding(padding).testTag(CONTENT)) }
    }

    private fun cart(kind: CartItemKind = CartItemKind.INDIVIDUAL_BET) = ShoppingCart(
        1,
        BigDecimal.TEN,
        listOf(CartItem(1, kind, "Mega-Sena", "Aposta", BigDecimal.TEN))
    )

    private companion object {
        const val CLEAR = "Excluir todas as apostas"
        const val FAVORITE = "Salvar carrinho favorito"
        const val CONTENT = "cart-contract-content"
    }
}

private const val EXPECTED_ACTION_SIZE_DP = 44f
private const val EXPECTED_ICON_SIZE_DP = 20f
private const val EXPECTED_HEADER_END_DP = 16f
private const val EXPECTED_FOOTER_INSET_DP = 24f
private const val EXPECTED_FOOTER_GAP_DP = 12f
private const val GEOMETRY_TOLERANCE_PX = 0.5f
