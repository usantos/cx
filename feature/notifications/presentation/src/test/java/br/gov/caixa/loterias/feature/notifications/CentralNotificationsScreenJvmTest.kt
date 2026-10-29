package br.gov.caixa.loterias.feature.notifications

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.presentation.screen.CentralNotificationsScreen
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "pt-rBR-w360dp-h800dp-xhdpi")
class CentralNotificationsScreenJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun emptyTabsHaveSelectedStateAndDisableBulkActions() {
        val state = mutableStateOf(CentralNotificationsState())
        var backs = 0
        compose.setContent {
            CaixaDSTheme {
                CentralNotificationsScreen(state.value, { backs++ }, { state.value = state.value.copy(unreadOnly = it) }, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("Central de notificações").assertIsDisplayed()
        compose.onNodeWithText("Todas").assertIsSelected()
        compose.onNodeWithText("Ainda não há notificações.").assertIsDisplayed()
        compose.onNodeWithText("Fique de olho nas novidades e atualizações.").assertIsDisplayed()
        compose.onNodeWithText("Marcar todas como lidas").assertIsNotEnabled()
        compose.onNodeWithText("Excluir todas as notificações").assertIsNotEnabled()
        compose.onNodeWithText("Não lidas (0)").performClick().assertIsSelected()
        compose.onNodeWithText("Não há notificações não lidas.").assertIsDisplayed()
        compose.onNodeWithText("Excluir todas as notificações").assertDoesNotExist()
        compose.onNodeWithText("Todas").performClick().assertIsSelected()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun anonymousFailureOffersLoginAndRetryWithoutEnablingBulkActions() {
        var retries = 0
        compose.setContent {
            CaixaDSTheme {
                CentralNotificationsScreen(
                    CentralNotificationsState(isAnonymous = true, error = NotificationText.Plain("Falha")),
                    {},
                    {},
                    {},
                    {},
                    {},
                    { retries++ }
                )
            }
        }
        compose.onNodeWithText("Faça login para visualizar suas notificações.").assertIsDisplayed()
        compose.onNodeWithText("Marcar todas como lidas").assertIsNotEnabled()
        compose.onNodeWithText("Excluir todas as notificações").assertIsNotEnabled()
        compose.onNodeWithText("Tentar novamente").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun loadingHidesRowsAndDisablesMutations() {
        val unread = InboxNotification(UNREAD_NOTIFICATION_ID, "Nova mensagem", "Conteúdo", false)
        compose.setContent {
            CaixaDSTheme {
                CentralNotificationsScreen(
                    CentralNotificationsState(all = listOf(unread), unread = listOf(unread), loading = true),
                    {},
                    {},
                    {},
                    {},
                    {},
                    {}
                )
            }
        }
        compose.onNodeWithText(unread.title).assertDoesNotExist()
        compose.onNodeWithText("Ainda não há notificações.").assertDoesNotExist()
        compose.onNodeWithText("Marcar todas como lidas").assertIsNotEnabled()
        compose.onNodeWithText("Excluir todas as notificações").assertIsNotEnabled()
    }

    @Test fun rowsPreserveContentAndDispatchTheNotificationSelectedForRemoval() {
        val unread = InboxNotification(UNREAD_NOTIFICATION_ID, "Aviso de sorteio", "Seu concurso será realizado hoje.", false)
        val read = InboxNotification(READ_NOTIFICATION_ID, "Anterior", "Mensagem já lida", true)
        val state = mutableStateOf(CentralNotificationsState(all = listOf(unread, read), unread = listOf(unread)))
        val removed = mutableListOf<InboxNotification>()
        var marks = 0
        var clears = 0
        compose.setContent {
            CaixaDSTheme {
                CentralNotificationsScreen(
                    state.value,
                    {},
                    { state.value = state.value.copy(unreadOnly = it) },
                    { marks++ },
                    { clears++ },
                    { removed += it },
                    {}
                )
            }
        }
        compose.onNodeWithText(unread.title).assertIsDisplayed()
        compose.onNodeWithText(unread.content).assertIsDisplayed()
        compose.onNodeWithText(read.title).assertIsDisplayed()
        compose.onNodeWithText(read.content).assertIsDisplayed()
        compose.onNodeWithText("Marcar todas como lidas").performClick()
        compose.onNodeWithText("Excluir todas as notificações").performClick()
        compose.onNodeWithContentDescription("Excluir notificação: ${read.title}").performClick()
        compose.onNodeWithText("Não lidas (1)").performClick()
        compose.onNodeWithText(read.title).assertDoesNotExist()
        compose.onNodeWithText(unread.title).assertIsDisplayed()
        compose.onNodeWithText("Excluir todas as notificações").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf(read), removed)
            assertEquals(1, marks)
            assertEquals(1, clears)
        }
    }
}

private const val UNREAD_NOTIFICATION_ID = 82L
private const val READ_NOTIFICATION_ID = 31L
