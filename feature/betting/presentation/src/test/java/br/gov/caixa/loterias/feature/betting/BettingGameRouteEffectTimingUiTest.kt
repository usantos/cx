package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRouteEffects
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36], qualifiers = "w480dp-h900dp")
class BettingGameRouteEffectTimingUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun addedConfirmationConsumedBeforeEffectExecutionDoesNotNavigate() {
        val viewModel = preparedModel()
        val observed = mutableStateOf(viewModel.state.value.copy(addedToCart = true))
        var navigations = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                val state by observed
                BettingGameRouteEffects(
                    state = state,
                    viewModel = viewModel,
                    shakeEnabled = false,
                    shakeBlocked = false,
                    onBack = {},
                    onAdded = { navigations++ },
                    currentState = { state }
                )
                // O efeito deve ler a confirmação atual quando executar, após o consumo durante o apply.
                SideEffect {
                    if (state.addedToCart) observed.value = state.copy(addedToCart = false)
                }
            }
        }

        compose.runOnIdle {
            assertEquals(false, observed.value.addedToCart)
            assertEquals(0, navigations)
        }
    }

    private fun preparedModel(): BettingGameViewModel {
        val game = LotteryGameParameters(
            LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
            1, "18/10/2026", MAXIMUM_NUMBER, NUMBER_QUANTITY, NUMBER_QUANTITY, 1, listOf(0),
            listOf(BetPrice(NUMBER_QUANTITY, 0, BigDecimal.ONE))
        )
        return BettingGameViewModel(
            2,
            GetBettingParametersUseCase { error("Os parâmetros preparados não devem ser recarregados") },
            AddBetToCartUseCase { error("Este teste não permite inclusão no carrinho") },
            initialParameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 1, listOf(game))
        )
    }

    private companion object {
        const val NUMBER_QUANTITY = 6
        const val MAXIMUM_NUMBER = 60
    }
}
