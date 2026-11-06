package br.gov.caixa.loterias.core.network.client

import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendApi
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendClient
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionInterceptor
import br.gov.caixa.loterias.core.network.api.NotificationInboxApi
import br.gov.caixa.loterias.core.network.api.NotificationReadApi
import br.gov.caixa.loterias.core.network.api.SessionLogoutApi
import retrofit2.Retrofit

internal object SessionNetworkClients {
    private fun createRetrofit(options: NetworkClientOptions): Retrofit {
        options.clientBuilder
            .addInterceptor(AccountVerificationSessionInterceptor())
            .singleAttemptRequests()
        return createConfiguredRetrofit(options)
    }

    fun createSessionLogout(options: NetworkClientOptions): SessionLogoutApi =
        createRetrofit(options).create(SessionLogoutApi::class.java)

    fun createNotificationRead(options: NetworkClientOptions): NotificationReadApi =
        createRetrofit(options).create(NotificationReadApi::class.java)

    fun createNotificationInbox(options: NetworkClientOptions): NotificationInboxApi =
        createRetrofit(options).create(NotificationInboxApi::class.java)

    fun createAccountVerification(options: NetworkClientOptions): AccountVerificationBackendClient =
        AccountVerificationBackendClient(
            createRetrofit(options).create(AccountVerificationBackendApi::class.java),
            options.gson,
        )
}
