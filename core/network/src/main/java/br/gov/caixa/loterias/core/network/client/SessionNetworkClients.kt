package br.gov.caixa.loterias.core.network.client

import com.google.gson.Gson
import okhttp3.OkHttpClient
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.interceptor.SingleAttemptTransactionInterceptor
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler
import br.gov.caixa.loterias.core.network.api.NotificationInboxApi
import br.gov.caixa.loterias.core.network.api.NotificationReadApi
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionInterceptor
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendApi
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendClient
import br.gov.caixa.loterias.core.network.api.SessionLogoutApi

internal object SessionNetworkClients {
    fun createSessionLogout(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): SessionLogoutApi = createConfiguredRetrofit(
        baseUrl, AuthTokenProvider { null },
        clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .addInterceptor(AccountVerificationSessionInterceptor())
            .addInterceptor(SingleAttemptTransactionInterceptor()),
        gson, publicKeySet, debugOptions, mockOptions, attachBearerToken = false, sessionRefreshHandler = sessionRefreshHandler
    ).create(SessionLogoutApi::class.java)

    fun createNotificationRead(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): NotificationReadApi = createConfiguredRetrofit(
        baseUrl, AuthTokenProvider { null },
        clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .addInterceptor(AccountVerificationSessionInterceptor())
            .addInterceptor(SingleAttemptTransactionInterceptor()),
        gson, publicKeySet, debugOptions, mockOptions, attachBearerToken = false, sessionRefreshHandler = sessionRefreshHandler
    ).create(NotificationReadApi::class.java)

    fun createNotificationInbox(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): NotificationInboxApi = createConfiguredRetrofit(
        baseUrl, AuthTokenProvider { null },
        clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .addInterceptor(AccountVerificationSessionInterceptor())
            .addInterceptor(SingleAttemptTransactionInterceptor()),
        gson, publicKeySet, debugOptions, mockOptions, attachBearerToken = false, sessionRefreshHandler = sessionRefreshHandler
    ).create(NotificationInboxApi::class.java)

    fun createAccountVerification(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): AccountVerificationBackendClient = AccountVerificationBackendClient(
        createConfiguredRetrofit(
            baseUrl, AuthTokenProvider { null },
            clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
                .addInterceptor(AccountVerificationSessionInterceptor())
                .addInterceptor(SingleAttemptTransactionInterceptor()),
            gson, publicKeySet, debugOptions, mockOptions, attachBearerToken = false, sessionRefreshHandler = sessionRefreshHandler
        ).create(AccountVerificationBackendApi::class.java),
        gson
    )
}
