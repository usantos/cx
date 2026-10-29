package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.RapidListRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidListViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import java.math.BigDecimal
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class RapidListConfigurationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun rapidListRetainsTheLegacyConfigurationActionWhenEmpty() {
        var configurationRequests = 0
        val cart = ShoppingCart(1, BigDecimal.ZERO, emptyList())
        val viewModel = RapidListViewModel(
            GenerateRapidBetsUseCase { ServiceResult.Success(cart) },
            RemoveCartItemUseCase { AppResult.Success(cart) },
            ValidateCartUseCase { AppResult.Success(CartValidationResult.Accepted) },
            autoLoad = false,
        )
        compose.setContent {
            CaixaDSTheme {
                RapidListRoute(viewModel, onBack = {}, onPayment = {}, onValidationRedirect = {}, onConfiguration = { configurationRequests++ })
            }
        }
        compose.onNodeWithContentDescription("Configuração").assertIsDisplayed().assertHasClickAction().performClick()
        compose.runOnIdle { assertEquals(1, configurationRequests) }
    }
}
