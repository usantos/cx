package br.gov.caixa.loterias.feature.notifications.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.CentralNotificationsViewModel

@Composable
fun CentralNotificationsRoute(viewModel: CentralNotificationsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent(
        module = br.gov.caixa.loterias.core.domain.configuration.model.AppModule.NOTIFICATIONS,
        entryType = br.gov.caixa.loterias.feature.notifications.presentation.delivery.NotificationsUiEntry::class.java,
        onBack = onBack
    ) { entry ->
        LifecycleResumeEffect(viewModel) {
            viewModel.onResume()
            onPauseOrDispose {}
        }
        entry.CentralNotificationsScreen(
            state,
            onBack,
            viewModel::selectUnread,
            viewModel::requestMarkAll,
            viewModel::requestClear,
            viewModel::requestRemove,
            viewModel::load
        )
        entry.CentralNotificationsDialogs(state, viewModel::confirm, viewModel::dismissDialog)
    }
}
