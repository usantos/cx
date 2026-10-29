package br.gov.caixa.loterias.feature.notifications

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.presentation.screen.CentralNotificationsScreen
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CentralNotificationsScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun emptyAndUnreadTabsDisableBulkOperations() {
        val state = mutableStateOf(CentralNotificationsState())
        compose.setContent {
            CaixaDSTheme {
                CentralNotificationsScreen(state.value, {}, { state.value = state.value.copy(unreadOnly = it) }, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("Central de notificações").assertIsDisplayed()
        compose.onNodeWithText("Ainda não há notificações.").assertIsDisplayed()
        compose.onNodeWithText("Marcar todas como lidas").assertIsNotEnabled()
        compose.onNodeWithText("Excluir todas as notificações").assertIsNotEnabled()
        compose.onNodeWithText("Não lidas (0)").performClick()
        compose.onNodeWithText("Não há notificações não lidas.").assertIsDisplayed()
        compose.onNodeWithText("Excluir todas as notificações").assertDoesNotExist()
    }

    @Test fun allRowsKeepFullTextAndDispatchRealIds() {
        val unread =
            InboxNotification(
                UNREAD_ID,
                "Título completo de uma notificação longa",
                "Conteúdo integral da mensagem para o apostador.",
                false
            )
        val read = InboxNotification(READ_ID, "Anterior", "Já lida", true)
        var removed: Long? = null
        var marked = 0
        compose.setContent {
            CaixaDSTheme {
                CentralNotificationsScreen(
                    CentralNotificationsState(all = listOf(unread, read), unread = listOf(unread)),
                    {},
                    {},
                    { marked++ },
                    {},
                    { removed = it.id },
                    {}
                )
            }
        }
        compose.onNodeWithText(unread.title).assertIsDisplayed()
        compose.onNodeWithText(unread.content).assertIsDisplayed()
        compose.onNodeWithText("Não lidas (1)").assertIsDisplayed()
        compose.onNodeWithText("Marcar todas como lidas").performClick()
        compose.onNodeWithContentDescription("Excluir notificação: ${unread.title}").performClick()
        compose.runOnIdle {
            assertEquals(UNREAD_ID, removed)
            assertEquals(1, marked)
        }
    }
    private companion object {
        const val UNREAD_ID = 82L
        const val READ_ID = 31L
    }
}
