package br.gov.caixa.loterias.feature.retail

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizePixOtherKeyField
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizePixTab
import br.gov.caixa.loterias.feature.retail.presentation.route.RetailSearchRoute
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.RetailSearchViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class RetailAccessibilityTest {
    @get:Rule val compose = createComposeRule()
    private fun role(value: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, value)

    @Test fun pixSourceExposesTabRoleAndSelectedValue() {
        val selected = mutableStateOf(true)
        var calls = 0
        compose.setContent {
            CaixaDSTheme {
                PrizePixTab("CPF", selected.value, {
                    calls++
                    selected.value = !selected.value
                }, Modifier.height(56.dp))
            }
        }
        compose.onNode(role(Role.Tab) and hasText("CPF")).assertIsSelected().performClick().assertIsNotSelected()
        compose.runOnIdle { assertEquals(1, calls) }
        val tab = compose.onNode(role(Role.Tab) and hasText("CPF"))
        tab.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        tab.performKeyInput { pressKey(Key.Enter) }
        tab.assertIsSelected()
        compose.runOnIdle { assertEquals(2, calls) }
    }

    @Test fun invalidFilledPixKeyHasPersistentNameAndAttachedError() {
        compose.setContent { CaixaDSTheme { PrizePixOtherKeyField("invalid", {}, true) } }
        compose.onNode(hasSetTextAction() and hasContentDescription("Chave Pix"))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Chave inválida!"))
    }

    @Test fun filledRetailerSearchRetainsNameAndEditingAction() {
        val viewModel =
            RetailSearchViewModel(
                SearchRetailersUseCase {
                    AppResult.Success(emptyList())
                },
                SetRetailerFavoriteUseCase { _, _ -> AppResult.Success(Unit) }
            )
        viewModel.changeQuery("AB")
        compose.setContent { CaixaDSTheme { RetailSearchRoute(viewModel, {}) } }
        compose.onNode(hasSetTextAction() and hasContentDescription("Nome ou código")).assertExists()
    }
}
