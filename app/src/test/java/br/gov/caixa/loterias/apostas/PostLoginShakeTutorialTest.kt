package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.apostas.navigation.PostLoginShakeTutorial
import br.gov.caixa.loterias.apostas.navigation.PostLoginTutorialRequest
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PostLoginShakeTutorialTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val lease = AuthenticatedSessionIdentity("a".repeat(64))
    private var identity by mutableStateOf<AuthenticatedSessionIdentity?>(lease)
    private val destinations = mutableListOf<String>()
    private var cancellations = 0
    private val settings = Settings()

    @Test fun skipWaitsForPersistenceThenResumesOriginalFeatureOnce() {
        settings.write = CompletableDeferred()
        show()
        compose.onNodeWithText("Pular tutorial").performClick()
        compose.runOnIdle {
            assertTrue(destinations.isEmpty())
            settings.write!!.complete(Unit)
        }
        compose.waitUntil { destinations.isNotEmpty() }
        compose.runOnIdle {
            assertEquals(listOf("home"), destinations)
            assertEquals(listOf(lease.ownerKey), settings.completedOwners)
        }
    }

    @Test fun closeOnLastPageResumesWithoutHome() {
        show("tickets/my-bets")
        repeat(TUTORIAL_ADVANCE_COUNT) { compose.onNodeWithContentDescription("Próxima etapa").performClick() }
        compose.onNodeWithText("Fechar tutorial").performClick()
        compose.waitUntil { destinations.isNotEmpty() }
        compose.runOnIdle { assertEquals(listOf("tickets/my-bets"), destinations) }
    }

    @Test fun finalArrowUsesSameContinuation() {
        show("cart/current")
        repeat(TUTORIAL_ADVANCE_COUNT) { compose.onNodeWithContentDescription("Próxima etapa").performClick() }
        compose.onNodeWithContentDescription("Fechar tutorial").performClick()
        compose.waitUntil { destinations.isNotEmpty() }
        compose.runOnIdle { assertEquals(listOf("cart/current"), destinations) }
    }

    @Test fun completedAccountTutorialDoesNotRepeat() {
        settings.completed = true
        show("tickets/my-bets")
        compose.waitUntil { destinations.isNotEmpty() }
        compose.onNodeWithText("Pular tutorial").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf("tickets/my-bets"), destinations)
            assertTrue(settings.completedOwners.isEmpty())
        }
    }

    @Test fun logoutWhileSavingCannotResumePrivateFeature() {
        settings.write = CompletableDeferred()
        show()
        compose.onNodeWithText("Pular tutorial").performClick()
        compose.runOnIdle { identity = null }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, cancellations) }
        compose.runOnIdle { settings.write!!.complete(Unit) }
        compose.waitForIdle()
        compose.runOnIdle { assertTrue(destinations.isEmpty()) }
    }

    @Test fun failedSaveKeepsTutorialAndRetryKeepsDestination() {
        settings.fail = true
        show("cart/current")
        compose.onNodeWithText("Pular tutorial").performClick()
        compose.onNodeWithText("Tentar novamente").assertExists()
        compose.runOnIdle {
            assertTrue(destinations.isEmpty())
            settings.fail = false
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.waitUntil { destinations.isNotEmpty() }
        compose.runOnIdle { assertEquals(listOf("cart/current"), destinations) }
    }

    private fun show(destination: String = "home") {
        val provider = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = this@PostLoginShakeTutorialTest.identity === identity
        }
        val preferences = HomePreferencesUseCases(
            object : HomeLayoutRepository {
                override suspend fun loadOrder(): String? = null
                override suspend fun saveOrder(order: String) = Unit
            },
            settings
        )
        val request = PostLoginTutorialRequest(destination, lease)
        compose.setContent {
            CaixaDSTheme {
                PostLoginShakeTutorial(
                    request,
                    identity,
                    provider,
                    preferences,
                    FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                    onReady = { destinations.add(it) },
                    onCancelled = { cancellations++ }
                )
            }
        }
        compose.waitForIdle()
    }

    private class Settings : ShakeSettingsRepository {
        var completed = false
        var fail = false
        var write: CompletableDeferred<Unit>? = null
        val completedOwners = mutableListOf<String?>()
        override suspend fun enabled() = false
        override suspend fun setEnabled(enabled: Boolean) = Unit
        override suspend fun warningHidden() = false
        override suspend fun setWarningHidden(hidden: Boolean) = Unit
        override suspend fun tutorialCompleted(ownerKey: String?) = completed
        override suspend fun completeTutorial(ownerKey: String?) {
            write?.await()
            if (fail) error("Storage unavailable")
            completedOwners.add(ownerKey)
            completed = true
        }
        override suspend fun tooltipPending(ownerKey: String?) = false
        override suspend fun consumeTooltip(ownerKey: String?) = Unit
    }
}

private const val TUTORIAL_ADVANCE_COUNT = 5
