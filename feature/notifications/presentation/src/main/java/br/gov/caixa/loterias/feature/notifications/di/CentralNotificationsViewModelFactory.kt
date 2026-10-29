package br.gov.caixa.loterias.feature.notifications.di

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationInboxRepository
import br.gov.caixa.loterias.feature.notifications.domain.usecase.NotificationInboxActions
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.CentralNotificationsViewModel
import javax.inject.Inject

class CentralNotificationsViewModelFactory @Inject constructor(
    private val repository: NotificationInboxRepository,
    private val identities: AuthenticatedSessionIdentityProvider,
    private val dispatchers: CoroutineDispatchers,
) {
    fun create() = CentralNotificationsViewModel(NotificationInboxActions(repository), identities, dispatchers)
}
