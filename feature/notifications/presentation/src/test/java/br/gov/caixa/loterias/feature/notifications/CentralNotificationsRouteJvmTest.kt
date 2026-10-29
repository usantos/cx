package br.gov.caixa.loterias.feature.notifications

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationInbox
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationInboxRepository
import br.gov.caixa.loterias.feature.notifications.domain.usecase.NotificationInboxActions
import br.gov.caixa.loterias.feature.notifications.presentation.route.CentralNotificationsRoute
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.CentralNotificationsViewModel
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "pt-rBR")
class CentralNotificationsRouteJvmTest {
    @get:Rule val compose = createComposeRule()
    private val repository = InboxRepository()
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private var currentIdentity: AuthenticatedSessionIdentity? = identity
    private val sessions = object : AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = currentIdentity
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === currentIdentity
    }
    private var backs = 0

    @Test fun markAllDialogCanBeCancelledAndConfirmedWithoutMarkingReadRowsAgain() {
        show()
        compose.onNodeWithText("Marcar todas como lidas").performClick()
        compose.onNodeWithText("Deseja marcar todas as notificações como lidas?").assertIsDisplayed()
        compose.runOnIdle { assertTrue(repository.marked.isEmpty()) }
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Deseja marcar todas as notificações como lidas?").assertDoesNotExist()
        compose.runOnIdle { assertTrue(repository.marked.isEmpty()) }
        compose.onNodeWithText("Marcar todas como lidas").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Não lidas (0)").assertIsDisplayed()
        compose.onNodeWithText("Marcar todas como lidas").assertIsNotEnabled()
        compose.runOnIdle {
            assertEquals(listOf(UNREAD_NOTIFICATION_ID), repository.marked)
            assertTrue(repository.rows.all { it.read })
        }
    }

    @Test fun removeDialogKeepsOtherRowsAndClearDialogDeletesOnlyAfterConfirmation() {
        show()
        compose.onNodeWithContentDescription("Excluir notificação: Aviso de sorteio").performClick()
        compose.onNodeWithText("Deseja excluir a notificação \"Aviso de sorteio\"?").assertIsDisplayed()
        compose.runOnIdle { assertTrue(repository.removed.isEmpty()) }
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Aviso de sorteio").assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir notificação: Aviso de sorteio").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Aviso de sorteio").assertDoesNotExist()
        compose.onNodeWithText("Anterior").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(UNREAD_NOTIFICATION_ID), repository.removed) }
        compose.onNodeWithText("Excluir todas as notificações").performClick()
        compose.onNodeWithText("Deseja excluir todas as notificações?").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(0, repository.clears) }
        compose.onNodeWithText("Anterior").assertIsDisplayed()
        compose.onNodeWithText("Excluir todas as notificações").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Ainda não há notificações.").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, repository.clears)
            assertTrue(repository.rows.isEmpty())
        }
    }

    @Test fun failedLoadCanBeDismissedAndRefreshedOnResumeAndBackReachesTheHost() {
        repository.failLoad = true
        show()
        compose.onNodeWithText("Não foi possível carregar as notificações. Tente novamente.").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Não foi possível carregar as notificações. Tente novamente.").assertDoesNotExist()
        // O retorno ao primeiro plano solicita outra carga após o reconhecimento do erro.
        compose.runOnIdle {
            repository.failLoad = false
            model.onResume()
        }
        compose.onNodeWithText("Aviso de sorteio").assertIsDisplayed()
        compose.onNodeWithText("Não lidas (1)").performClick()
        compose.onNodeWithText("Anterior").assertDoesNotExist()
        compose.onNodeWithText("Todas").performClick()
        compose.onNodeWithText("Anterior").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun logoutOnResumeClearsPrivateRowsAndCancelsThePendingConfirmation() {
        show()
        compose.onNodeWithText("Aviso de sorteio").assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir notificação: Aviso de sorteio").performClick()
        compose.onNodeWithText("Deseja excluir a notificação \"Aviso de sorteio\"?").assertIsDisplayed()
        compose.runOnIdle {
            currentIdentity = null
            model.onResume()
        }
        compose.onNodeWithText("Deseja excluir a notificação \"Aviso de sorteio\"?").assertDoesNotExist()
        compose.onNodeWithText("Aviso de sorteio").assertDoesNotExist()
        compose.onNodeWithText("Anterior").assertDoesNotExist()
        compose.onNodeWithText("Faça login para visualizar suas notificações.").assertIsDisplayed()
        compose.onNodeWithText("Marcar todas como lidas").assertIsNotEnabled()
        compose.onNodeWithText("Excluir todas as notificações").assertIsNotEnabled()
        compose.runOnIdle {
            assertTrue(model.state.value.isAnonymous)
            assertTrue(model.state.value.all.isEmpty())
            assertTrue(model.state.value.unread.isEmpty())
            assertTrue(repository.removed.isEmpty())
            assertEquals(2, repository.rows.size)
        }
    }

    @Test fun failedMutationKeepsAuthoritativeRowsAndDisplaysAnAcknowledgableError() {
        show()
        repository.failMutation = true
        compose.onNodeWithText("Excluir todas as notificações").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText(
            "Não foi possível concluir todas as alterações. Verifique as notificações e tente novamente."
        ).assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Aviso de sorteio").assertIsDisplayed()
        compose.onNodeWithText("Anterior").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, repository.clears)
            assertEquals(2, repository.rows.size)
        }
    }

    private lateinit var model: CentralNotificationsViewModel

    private fun show() {
        model =
            CentralNotificationsViewModel(
                NotificationInboxActions(repository),
                sessions,
                FixedCoroutineDispatchers(Dispatchers.Main.immediate)
            )
        compose.setContent { CaixaDSTheme { CentralNotificationsRoute(model, { backs++ }) } }
    }

    private class InboxRepository : NotificationInboxRepository {
        var rows =
            listOf(
                InboxNotification(UNREAD_NOTIFICATION_ID, "Aviso de sorteio", "Seu concurso será realizado hoje.", false),
                InboxNotification(READ_NOTIFICATION_ID, "Anterior", "Já lida", true)
            )
        var failLoad = false
        var failMutation = false
        var clears = 0
        val marked = mutableListOf<Long>()
        val removed = mutableListOf<Long>()

        override suspend fun load(identity: AuthenticatedSessionIdentity): NotificationInbox {
            if (failLoad) throw IOException("Sem conexão")
            return NotificationInbox(rows, rows.filterNot { it.read })
        }

        override suspend fun markRead(identity: AuthenticatedSessionIdentity, id: Long) {
            checkMutation()
            marked += id
            rows = rows.map { if (it.id == id) it.copy(read = true) else it }
        }

        override suspend fun remove(identity: AuthenticatedSessionIdentity, id: Long) {
            checkMutation()
            removed += id
            rows = rows.filterNot { it.id == id }
        }

        override suspend fun clear(identity: AuthenticatedSessionIdentity) {
            checkMutation()
            clears++
            rows = emptyList()
        }

        private fun checkMutation() {
            if (failMutation) throw IOException("Operação indisponível")
        }
    }
}

private const val UNREAD_NOTIFICATION_ID = 82L
private const val READ_NOTIFICATION_ID = 31L
