package br.gov.caixa.loterias.feature.notifications

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.presentation.route.NotificationsRoute
import br.gov.caixa.loterias.feature.notifications.presentation.screen.notificationScreens
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.NotificationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotificationsParityTest {
    @get:Rule val compose = createComposeRule()
    private var viewModel: NotificationsViewModel? = null

    @After fun cleanup() {
        viewModel?.viewModelScope?.cancel()
    }

    @Test fun expandingUnreadKeepsPreviewAndFullBodyUntilFilterRefresh() {
        val vm = show("notifications/messages")
        compose.onNodeWithText("Não Lidas").performClick()
        compose.onNodeWithText("Lembrete").performClick()
        compose.onNodeWithText("Apostar agora").assertIsDisplayed()
        compose.onAllNodesWithText("Conteúdo do lembrete").assertCountEquals(2)
        compose.runOnIdle { assertTrue(vm.state.value.messages.first().isRead) }
        compose.onNodeWithText("Todas").performClick()
        compose.onNodeWithText("Não Lidas").performClick()
        compose.onNodeWithText("Lembrete").assertDoesNotExist()
        compose.onNodeWithText("Você não possui mensagens não lidas").assertIsDisplayed()
    }

    @Test fun bulkActionsKeepLegacyConfirmationsEvenWhenTheListIsEmpty() {
        show("notifications/messages")
        compose.onNodeWithContentDescription("Excluir todas as mensagens").performClick()
        compose.onNodeWithText("Deseja excluir todas a mensagens ?").assertIsDisplayed()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Você não possui mensagens").assertIsDisplayed()
        compose.onNodeWithContentDescription("Marcar todas as mensagens como lidas").performClick()
        compose.onNodeWithText("Deseja marcar todas a mensagens como lidas ?").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Você não possui mensagens").assertIsDisplayed()
    }

    @Test fun preferencesStayIndependentAndHelpKeepsItsLegacyDestinationAction() {
        var help = 0
        val vm = show("notifications/preferences") { help++ }
        compose.onAllNodes(isToggleable())[0].performClick()
        compose.runOnIdle {
            assertTrue(vm.state.value.preferences.first().enabled)
            assertFalse(vm.state.value.preferences.last().enabled)
        }
        compose.onNodeWithContentDescription("Termos de Uso").performClick()
        compose.runOnIdle { assertEquals(1, help) }
    }

    @Test fun individualSwipeActionsRequireConsentAndCategoryActionKeepsLegacyNotice() {
        show("notifications/messages")
        compose.onNodeWithContentDescription("Marcar mensagem como lida").performScrollTo().performClick()
        compose.onNodeWithText("Deseja marcar esta mensagem como lida ?").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithContentDescription("Marcar mensagem como lida").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Lembrete").performScrollTo().performClick()
        compose.onNodeWithText("Lembrete").performClick()
        compose.onNodeWithText("Apostar agora").performClick()
        compose.onNodeWithText("Realiza ação da categoria").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithContentDescription("Excluir mensagem").performScrollTo().performClick()
        compose.onNodeWithText("Deseja excluir esta mensagem ?").assertIsDisplayed()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Você não possui mensagens").assertIsDisplayed()
    }

    @Test fun backendFailureIsShownAndAcknowledgedWithoutDeletingMessages() {
        show("notifications/messages", deleteFails = true)
        compose.onNodeWithContentDescription("Excluir todas as mensagens").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Serviço indisponível").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Lembrete").assertIsDisplayed()
    }

    private fun show(route: String, deleteFails: Boolean = false, onHelp: () -> Unit = {}): NotificationsViewModel {
        var messages = listOf(AppNotification(1, NotificationCategory.LUCK_REMINDERS, "Lembrete", "Conteúdo do lembrete", "hoje", false))
        var preferences = listOf(
            NotificationPreference(null, "Todas as notificações", "", false),
            NotificationPreference(NotificationCategory.TIPS_NEWS, "Dicas e Novidades", "Novos produtos", false)
        )
        val vm = NotificationsViewModel(
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
                if (deleteFails) {
                    AppResult.Failure(br.gov.caixa.loterias.core.common.result.AppError.Validation("Serviço indisponível"))
                } else {
                    messages =
                        emptyList()
                    AppResult.Success(Unit)
                }
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
            FixedCoroutineDispatchers(Dispatchers.Main)
        )
        viewModel = vm
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                NotificationsRoute(
                    notificationScreens(androidx.compose.ui.platform.LocalContext.current::getString).single {
                        it.route ==
                            route
                    }.route,
                    vm,
                    {},
                    onHelp
                )
            }
        }
        compose.waitUntil { !vm.state.value.loading }
        return vm
    }
}
