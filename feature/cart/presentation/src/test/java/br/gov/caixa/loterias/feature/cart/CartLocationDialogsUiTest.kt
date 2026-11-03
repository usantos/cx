// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase
import br.gov.caixa.loterias.feature.cart.presentation.components.CartLocationDialogs
import br.gov.caixa.loterias.feature.cart.presentation.state.CartLocationAction
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

/** Captura encaminhamentos Android localmente; não abre configurações do sistema, geocodificador ou serviço de pagamento. */
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36])
class CartLocationDialogsUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()
    private var status = CheckoutLocationStatus.PERMISSION_REQUIRED
    private var checks = 0
    private var histories = 0
    private var validations = 0
    private var requests = 0
    private var blockedHome = 0
    private val registry = object : ActivityResultRegistry(), ActivityResultRegistryOwner {
        override val activityResultRegistry: ActivityResultRegistry get() = this
        var code = 0
        var fail = false
        val inputs = mutableListOf<Any?>()
        override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
            code = requestCode
            inputs += input
            if (fail) throw IllegalStateException("Synthetic Android launch failure")
        }
    }
    private fun show(): CartViewModel {
        val snapshot = ShoppingCart(5, BigDecimal.TEN, listOf(CartItem(7, CartItemKind.INDIVIDUAL_BET, "Synthetic", "", BigDecimal.TEN)))
        val model =
            CartViewModel(
                GetCurrentCartUseCase {
                    AppResult.Success(snapshot)
                },
                RemoveCartItemUseCase { error("No removal") }, ClearCartUseCase { error("No clearing") },
                ServerTimeSource {
                    Instant.parse("2026-10-22T10:00:00Z")
                },
                VerifyCartPaymentAccountUseCase { AppResult.Success(CartPaymentAccountDecision.Allowed) },
                ValidateCartUseCase {
                    validations++
                    AppResult.Success(br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult.Accepted)
                },
                HasRecentPurchaseUseCase {
                    histories++
                    AppResult.Success(false)
                },
                CheckCheckoutLocationUseCase {
                    checks++
                    AppResult.Success(status)
                },
                RecordCheckoutLocationRequestUseCase {
                    requests++
                    AppResult.Success(Unit)
                },
                dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate), authenticated = true,
                networkAvailability = NetworkAvailability {
                    ConnectionAvailability.CONNECTED
                }
            )
        compose.setFeatureContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registry) {
                LoteriasCaixaDSTheme {
                    val state by model.state.collectAsState()
                    CartLocationDialogs(state, model) { blockedHome++ }
                }
            }
        }
        compose.runOnIdle { model.requestPayment() }
        flush()
        return model
    }
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }

    @Test fun `permission denial opens own application settings and return rechecks country before blocking`() {
        val model = show()
        compose.onNodeWithText("Sim").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(
                setOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                (registry.inputs.single() as Array<*>).toSet()
            )
            assertEquals(1, requests)
            registry.dispatchResult(
                registry.code,
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to false,
                    Manifest.permission.ACCESS_COARSE_LOCATION to false
                )
            )
        }
        flush()
        compose.runOnIdle {
            val settings = registry.inputs.last() as Intent
            assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, settings.action)
            assertEquals(compose.activity.packageName, settings.data!!.schemeSpecificPart)
            assertEquals(CartLocationAction.PERMISSION_SETTINGS, model.state.value.awaitingLocationAction)
            status = CheckoutLocationStatus.OUTSIDE_BRAZIL
            registry.dispatchResult(registry.code, Activity.RESULT_OK, null)
        }
        flush()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(1, blockedHome)
            assertEquals(2, checks)
            assertFalse(model.state.value.paymentReady)
            assertNull(model.state.value.locationPrompt)
        }
    }

    @Test fun `localizacao indisponivel permite repetir e cancelar sem perder carrinho nem liberar pagamento`() {
        status = CheckoutLocationStatus.UNAVAILABLE
        val model = show()
        compose.onNodeWithText(
            listOf(
                "Não foi possível verificar sua localização. Suas apostas continuam no carrinho.",
                "Tente novamente para confirmar que você está no Brasil."
            ).joinToString(" ")
        ).assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(2, checks)
            assertEquals(0, histories)
            assertEquals(0, validations)
            assertEquals(5L, model.state.value.cart!!.id)
            assertEquals(1, model.state.value.cart!!.items.size)
            assertFalse(model.state.value.paymentReady)
            assertEquals(0, blockedHome)
        }
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertNull(model.state.value.locationPrompt)
            assertEquals(0, blockedHome)
            assertEquals(5L, model.state.value.cart!!.id)
        }
    }

    @Test fun `nova tentativa somente libera checkout depois de confirmar Brasil`() {
        status = CheckoutLocationStatus.UNAVAILABLE
        val model = show()
        compose.runOnIdle { status = CheckoutLocationStatus.IN_BRAZIL }
        compose.onNodeWithText("Tentar novamente").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(2, checks)
            assertEquals(1, histories)
            assertEquals(1, validations)
            assertTrue(model.state.value.paymentReady)
            assertEquals(5L, model.state.value.cart!!.id)
            assertEquals(1, model.state.value.cart!!.items.size)
            assertEquals(0, blockedHome)
        }
    }

    @Test fun `provider settings consent sends exact settings action and unavailable return blocks checkout`() {
        status = CheckoutLocationStatus.PROVIDER_DISABLED
        val model = show()
        compose.onNodeWithText("Sim").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(Settings.ACTION_LOCATION_SOURCE_SETTINGS, (registry.inputs.single() as Intent).action)
            assertEquals(0, requests)
            status = CheckoutLocationStatus.UNAVAILABLE
            registry.dispatchResult(registry.code, Activity.RESULT_OK, null)
        }
        flush()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(0, blockedHome)
            assertEquals(2, checks)
            assertFalse(model.state.value.paymentReady)
        }
    }

    @Test fun `permission launcher failure preserves cart and requires block acknowledgment`() {
        registry.fail = true
        val model = show()
        compose.onNodeWithText("Sim").performClick()
        flush()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(1, requests)
            assertEquals(0, blockedHome)
            assertEquals(1, registry.inputs.size)
            assertEquals(5L, model.state.value.cart!!.id)
            assertFalse(model.state.value.paymentReady)
        }
    }

    @Test fun `declined consent never launches settings or permission request`() {
        val model = show()
        compose.onNodeWithText("Não").performClick()
        flush()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertTrue(registry.inputs.isEmpty())
            assertEquals(0, requests)
            assertEquals(0, blockedHome)
            assertFalse(model.state.value.paymentReady)
        }
    }
}
