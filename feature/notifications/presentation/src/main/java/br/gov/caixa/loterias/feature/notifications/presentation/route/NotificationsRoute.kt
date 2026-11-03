package br.gov.caixa.loterias.feature.notifications.presentation.route

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.presentation.delivery.NotificationsUiEntry
import br.gov.caixa.loterias.feature.notifications.presentation.screen.NotificationMessageActions
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationEffect
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.NotificationsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationsRoute(route: String, viewModel: NotificationsViewModel, onBack: () -> Unit, onHelp: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resources = LocalResources.current
    LaunchedEffect(viewModel, context, resources) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is NotificationEffect.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.notifications_share_content, effect.title, effect.content))
                    }
                    context.startActivity(Intent.createChooser(intent, resources.getString(R.string.notifications_compartilhar_mensagem)))
                }
            }
        }
    }

    FeatureModuleContent(
        module = AppModule.NOTIFICATIONS,
        entryType = NotificationsUiEntry::class.java,
        onBack = onBack
    ) { entry ->
        when (route) {
            "notifications/messages" -> entry.MessagesScreen(
                state = state,
                actions = NotificationMessageActions(
                    updateQuery = viewModel::updateQuery,
                    requestMarkAllRead = viewModel::requestMarkAllRead,
                    requestDeleteAll = viewModel::requestDeleteAll,
                    selectFilter = viewModel::selectFilter,
                    toggleExpanded = viewModel::toggleExpanded,
                    requestMarkRead = viewModel::requestMarkRead,
                    requestDelete = viewModel::requestDelete,
                    share = viewModel::share,
                    performCategoryAction = viewModel::performCategoryAction
                ),
                onBack = onBack
            )

            "notifications/preferences" -> entry.PreferencesScreen(state, viewModel::togglePreference, onBack, onHelp)

            else -> error("Rota de notificações sem layout Compose: $route")
        }

        entry.NotificationDialogs(state, viewModel::confirmDialog, viewModel::dismissDialog)
    }
}
