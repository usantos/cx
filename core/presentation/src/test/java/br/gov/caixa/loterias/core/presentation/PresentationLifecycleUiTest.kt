// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.presentation

import android.os.SystemClock
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignActivation
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings
import br.gov.caixa.loterias.core.domain.configuration.usecase.CampaignConfigurationUseCases
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.campaign.theme.ApplicationCampaignTheme
import br.gov.caixa.loterias.core.presentation.campaign.viewmodel.CampaignViewModel
import br.gov.caixa.loterias.core.presentation.components.ServiceFailureOverlay
import br.gov.caixa.loterias.core.presentation.effect.NavigationResumeEffect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.core.presentation.time.elapsedRealtimeMillis
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PresentationLifecycleUiTest {
    @get:Rule val compose = createComposeRule()
    private class Owner : LifecycleOwner {
        override val lifecycle = LifecycleRegistry(this).apply { currentState = Lifecycle.State.STARTED }
    }

    @Test fun `resume effect invokes latest callback and removes observer when destination leaves composition`() {
        val owner = Owner()
        val callbackRevision = mutableStateOf(1)
        val visible = mutableStateOf(true)
        val calls = mutableListOf<Int>()
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                val revision = callbackRevision.value
                if (visible.value) NavigationResumeEffect { calls += revision }
            }
        }
        compose.runOnIdle {
            assertTrue(calls.isEmpty())
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
        }
        compose.runOnIdle {
            assertEquals(listOf(1), calls)
            owner.lifecycle.currentState = Lifecycle.State.STARTED
            callbackRevision.value =
                2
        }
        compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.RESUMED }
        compose.runOnIdle {
            assertEquals(listOf(1, 2), calls)
            visible.value = false
        }
        compose.runOnIdle {
            owner.lifecycle.currentState = Lifecycle.State.STARTED
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
            assertEquals(listOf(1, 2), calls)
        }
    }

    @Test fun `service notice delivers exact accept and dismiss decisions without repeating service work`() {
        val notice = ServiceNoticeUiState("Aposte mais para continuar", ServiceRedirect.HOME)
        val decisions = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                ServiceFailureOverlay(
                    ServiceFailurePresentation(notice),
                    {
                        assertSame(notice, it)
                        decisions += "accept"
                    },
                    {
                        assertSame(notice, it)
                        decisions += "dismiss"
                    },
                    { error("notice has no redirect") },
                    { error("notice has no redirect") }
                )
            }
        }
        compose.onNodeWithText("Apostar mais").performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(listOf("accept", "dismiss"), decisions) }
    }

    @Test fun `redirect waits for resumed lifecycle and consumed event is not replayed`() {
        val owner = Owner()
        val event = ServiceRedirectEvent(ServiceRedirect.LOGIN)
        var consumed = false
        val redirects = mutableListOf<ServiceRedirect>()
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                ServiceFailureOverlay(
                    ServiceFailurePresentation(redirect = event),
                    {},
                    {},
                    {
                        assertSame(event, it)
                        if (consumed) {
                            false
                        } else {
                            consumed = true
                            true
                        }
                    },
                    { redirects += it }
                )
            }
        }
        compose.runOnIdle {
            assertTrue(redirects.isEmpty())
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
        }
        compose.runOnIdle {
            assertEquals(listOf(ServiceRedirect.LOGIN), redirects)
            owner.lifecycle.currentState = Lifecycle.State.STARTED
        }
        compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.RESUMED }
        compose.runOnIdle { assertEquals(listOf(ServiceRedirect.LOGIN), redirects) }
    }

    @Test fun `campaign first resume loads shared cache and later resumes explicitly refresh`() {
        var loads = 0
        var refreshes = 0
        val model = CampaignViewModel(
            object : CampaignConfigurationUseCases {
                override val settings = MutableStateFlow(CommemorativeCampaignSettings())
                override suspend fun load() {
                    loads++
                }
                override suspend fun refresh() {
                    refreshes++
                }
            },
            FixedCoroutineDispatchers(Dispatchers.Unconfined)
        )
        model.resumed(true)
        model.resumed(true)
        assertEquals(1, loads)
        assertEquals(0, refreshes)
        model.resumed(false)
        model.resumed(true)
        assertEquals(1, loads)
        assertEquals(1, refreshes)
    }

    @Test fun `campaign theme refreshes only while resumed and maps home and game scopes independently`() {
        val owner = Owner()
        val refreshes = AtomicInteger()
        val settings = MutableStateFlow(
            CommemorativeCampaignSettings(
                listOf(
                    CampaignActivation(CommemorativeCampaign.OUTUBRO_ROSA, setOf(CampaignArea.HOME)),
                    CampaignActivation(CommemorativeCampaign.SETEMBRO_AZUL, setOf(CampaignArea.MEGA_SENA))
                )
            )
        )
        val model = CampaignViewModel(
            object : CampaignConfigurationUseCases {
                override val settings = settings
                override suspend fun refresh() {
                    refreshes.incrementAndGet()
                }
            },
            FixedCoroutineDispatchers(Dispatchers.Unconfined)
        )
        var home = CaixaDSCampaign.DEFAULT
        var game = CaixaDSCampaign.DEFAULT
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                ApplicationCampaignTheme(model) {
                    home = LocalCaixaDSCampaign.current
                    game = LocalCaixaDSMegaSenaCampaign.current
                }
            }
        }
        compose.runOnIdle {
            assertEquals(0, refreshes.get())
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
        }
        compose.waitUntil(5000) { home == CaixaDSCampaign.OCTOBER_ROSE && game == CaixaDSCampaign.SEPTEMBER_BLUE && refreshes.get() == 1 }
        compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.STARTED }
        compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.RESUMED }
        compose.waitUntil(5000) { refreshes.get() == 2 }
        val before = elapsedRealtimeMillis()
        SystemClock.sleep(10)
        assertTrue(elapsedRealtimeMillis() >= before + 10)
    }
}
