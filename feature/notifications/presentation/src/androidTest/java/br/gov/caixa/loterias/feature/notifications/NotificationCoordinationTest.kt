package br.gov.caixa.loterias.feature.notifications

import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.presentation.route.NotificationsRoute
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.NotificationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotificationCoordinationTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<NotificationsViewModel>()
    private val intents = mutableListOf<Intent>()

    @After fun cleanup() {
        models.forEach { it.viewModelScope.cancel() }
    }

    @Test fun searchMatchesBodyAndUnreadTabKeepsItsEmptyStateAndBackCallback() {
        val fixture = Fixture(
            listOf(
                message(1, NotificationCategory.LUCK_REMINDERS),
                message(2, NotificationCategory.SECURITY_ALERTS, "Segurança", "Reserva confirmada", true)
            )
        )
        var back = 0
        show(fixture, onBack = { back++ })
        compose.onNode(hasSetTextAction()).performTextReplacement("RESERVA")
        compose.onNodeWithText("Mensagem").assertDoesNotExist()
        compose.onNodeWithText("Segurança").assertIsDisplayed()
        compose.onNodeWithText("Não Lidas").performClick()
        compose.onNodeWithText("Você não possui mensagens não lidas").assertIsDisplayed()
        compose.onNodeWithText("Todas").performClick()
        compose.onNodeWithText("Segurança").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performTextReplacement("")
        compose.onNodeWithText("Mensagem").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals("", fixture.model.state.value.query)
            assertEquals(1, back)
        }
    }

    @Test fun swipingRequiresConsentAndFirstRowTapClosesActionsBeforeExpanding() {
        val fixture = Fixture()
        show(fixture)
        swipeMessage("Mensagem")
        compose.onNodeWithContentDescription("Marcar mensagem como lida").assertIsDisplayed().performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertFalse(fixture.model.state.value.messages.single().isRead) }
        compose.onNodeWithText("Mensagem").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertNull(fixture.model.state.value.expandedMessageId) }
        compose.onNodeWithContentDescription("Excluir mensagem").assertIsNotDisplayed()
        compose.onNodeWithText("Mensagem").performClick()
        compose.runOnIdle {
            assertEquals(1L, fixture.model.state.value.expandedMessageId)
            assertTrue(fixture.model.state.value.messages.single().isRead)
        }
        compose.onNodeWithText("Mensagem").performClick()
        swipeMessage("Mensagem")
        compose.onNodeWithContentDescription("Excluir mensagem").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(1, fixture.model.state.value.messages.size) }
        compose.onNodeWithContentDescription("Excluir mensagem").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Você não possui mensagens").assertIsDisplayed()
    }

    @Test fun replacingViewModelUpdatesMessagesDialogsAndIndependentPreferenceCallbacks() {
        val first = Fixture()
        val second = Fixture()
        val replace = mutableStateOf(false)
        val route = mutableStateOf("notifications/messages")
        var help = 0
        var back = 0
        compose.setContent {
            val old = remember { first.create() }
            val current = remember { second.create() }
            Render(if (replace.value) current else old, route.value, { back++ }, { help++ })
        }
        compose.onNodeWithContentDescription("Excluir todas as mensagens").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { replace.value = true }
        compose.waitForIdle()
        compose.onNode(hasSetTextAction()).performTextReplacement("Conteúdo")
        compose.onNodeWithContentDescription("Marcar todas as mensagens como lidas").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle {
            assertEquals("", first.model.state.value.query)
            assertFalse(first.model.state.value.messages.single().isRead)
            assertEquals("Conteúdo", second.model.state.value.query)
            assertTrue(second.model.state.value.messages.single().isRead)
            route.value = "notifications/preferences"
        }
        compose.onAllNodes(isToggleable())[0].performClick()
        compose.runOnIdle {
            assertFalse(first.model.state.value.preferences.first().enabled)
            assertTrue(second.model.state.value.preferences.first().enabled)
            assertFalse(second.model.state.value.preferences.last().enabled)
        }
        compose.onAllNodes(isToggleable())[1].performClick()
        compose.runOnIdle { assertTrue(second.model.state.value.preferences.all { it.enabled }) }
        compose.onNodeWithContentDescription("Termos de Uso").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, help)
            assertEquals(1, back)
        }
    }

    @Test fun replacingViewModelCancelsOldShareCollectorAndKeepsChooserPayload() {
        val first = Fixture()
        val second = Fixture(listOf(message(2, NotificationCategory.LUCK_REMINDERS, "Segunda", "Outro conteúdo")))
        val replace = mutableStateOf(false)
        compose.setContent {
            val old = remember { first.create() }
            val current = remember { second.create() }
            Render(if (replace.value) current else old)
        }
        compose.onNodeWithText("Mensagem").performClick()
        compose.onNodeWithContentDescription("Compartilhar mensagem").performClick()
        compose.runOnIdle {
            assertChooser(intents.single(), "Mensagem <br>Conteúdo inicial")
            replace.value = true
        }
        compose.waitForIdle()
        compose.runOnIdle { first.model.share(first.model.state.value.messages.single()) }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, intents.size) }
        compose.onNodeWithText("Segunda").performClick()
        compose.onNodeWithContentDescription("Compartilhar mensagem").performClick()
        compose.runOnIdle {
            assertEquals(2, intents.size)
            assertChooser(intents.last(), "Segunda <br>Outro conteúdo")
        }
    }

    @Test fun categoriesKeepTheirPrimaryActionsAndShareVisibility() {
        val categories = listOf(
            Triple(NotificationCategory.PAYMENTS_REDEMPTIONS, null, false),
            Triple(NotificationCategory.LUCK_REMINDERS, "Apostar agora", true),
            Triple(NotificationCategory.RESULTS_AVAILABLE, "Conferir agora", false),
            Triple(NotificationCategory.TIPS_NEWS, null, true),
            Triple(NotificationCategory.SECURITY_ALERTS, null, false)
        )
        val fixture = Fixture(categories.mapIndexed { index, (category, _, _) -> message(index + 1L, category, "Categoria $index") })
        show(fixture)
        categories.forEachIndexed { index, (_, action, share) ->
            compose.onNode(hasSetTextAction()).performTextReplacement("Categoria $index")
            compose.onNode(hasText("Categoria $index") and !hasSetTextAction()).performClick()
            compose.onAllNodesWithText("Conteúdo inicial").assertCountEquals(2)
            if (share) {
                compose.onNodeWithContentDescription("Compartilhar mensagem").assertIsDisplayed().performClick()
            } else {
                compose.onNodeWithContentDescription("Compartilhar mensagem").assertDoesNotExist()
            }
            if (action != null) {
                compose.onNodeWithText(action).performClick()
                compose.onNodeWithText("Realiza ação da categoria").assertIsDisplayed()
                compose.onNodeWithText("Entendi").performClick()
            } else {
                compose.onNodeWithText("Apostar agora").assertDoesNotExist()
                compose.onNodeWithText("Conferir agora").assertDoesNotExist()
            }
        }
        compose.runOnIdle { assertEquals(2, intents.size) }
    }

    private fun swipeMessage(title: String) {
        val y = compose.onNodeWithText(title).fetchSemanticsNode().boundsInRoot.center.y
        compose.onRoot().performTouchInput {
            swipe(Offset(width * 0.85f, y), Offset(width * 0.15f, y), durationMillis = 400)
        }
        compose.waitForIdle()
    }

    private fun show(fixture: Fixture, onBack: () -> Unit = {}) {
        compose.setContent {
            val model = remember { fixture.create() }
            Render(model, onBack = onBack)
        }
    }

    @Composable private fun Render(
        model: NotificationsViewModel,
        route: String = "notifications/messages",
        onBack: () -> Unit = {},
        onHelp: () -> Unit = {},
    ) {
        val base = LocalContext.current
        val capture = remember(base) {
            object : ContextWrapper(base) {
                override fun startActivity(intent: Intent) {
                    intents.add(intent)
                }
            }
        }
        CompositionLocalProvider(LocalContext provides capture) {
            CaixaDSTheme(darkTheme = false) { NotificationsRoute(route, model, onBack, onHelp) }
        }
    }

    @Suppress("DEPRECATION")
    private fun assertChooser(chooser: Intent, text: String) {
        assertEquals(Intent.ACTION_CHOOSER, chooser.action)
        assertEquals("Compartilhar mensagem", chooser.getCharSequenceExtra(Intent.EXTRA_TITLE))
        val send = chooser.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)!!
        assertEquals(Intent.ACTION_SEND, send.action)
        assertEquals("text/plain", send.type)
        assertEquals(text, send.getStringExtra(Intent.EXTRA_TEXT))
    }

    private inner class Fixture(var messages: List<AppNotification> = listOf(message(1, NotificationCategory.LUCK_REMINDERS))) {
        private var preferences = listOf(
            NotificationPreference(null, "Todas as notificações", "", false),
            NotificationPreference(NotificationCategory.TIPS_NEWS, "Dicas e Novidades", "Novos produtos", false)
        )
        lateinit var model: NotificationsViewModel
        fun create() = NotificationsViewModel(
            { AppResult.Success(messages) },
            { id ->
                messages = messages.map { if (it.id == id) it.copy(isRead = true) else it }
                AppResult.Success(Unit)
            },
            { id ->
                messages = messages.filterNot { it.id == id }
                AppResult.Success(Unit)
            },
            {
                messages = emptyList()
                AppResult.Success(Unit)
            },
            {
                messages = messages.map { it.copy(isRead = true) }
                AppResult.Success(Unit)
            },
            { AppResult.Success(preferences) },
            { category, enabled ->
                preferences = preferences.map {
                    if (it.category ==
                        category
                    ) {
                        it.copy(enabled = enabled)
                    } else {
                        it
                    }
                }
                AppResult.Success(Unit)
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        ).also {
            model = it
            models.add(it)
        }
    }

    companion object {
        private fun message(
            id: Long,
            category: NotificationCategory,
            title: String = "Mensagem",
            content: String = "Conteúdo inicial",
            read: Boolean = false,
        ) = AppNotification(id, category, title, content, "hoje", read)
    }
}
