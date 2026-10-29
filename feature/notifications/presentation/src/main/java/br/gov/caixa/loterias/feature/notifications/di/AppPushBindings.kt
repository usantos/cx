package br.gov.caixa.loterias.feature.notifications.di

import android.content.Context
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.data.repository.PushRegistrationRepositoryImpl
import br.gov.caixa.loterias.core.domain.persistence.secureEnvironmentKey
import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.datasource.remote.PushRegistrationRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.notifications.data.repository.PushRegistrationLocalRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppPushBindings {
    @Provides @Singleton
    fun registerPush(
        @ApplicationContext context: Context,
        sessions: SessionTokenStore,
        user: CurrentUserIdentityProvider,
        provider: NotificationReadApiProvider,
        binding: AccountVerificationSessionBinding,
        configuration: HostFeatureConfiguration,
    ): RegisterPushDevice = RegisterPushDevice(
        sessions,
        { identity ->
            if (sessions.isCurrent(identity)) {
                user.currentUser()?.cpf
                    ?.takeIf { sessions.isCurrent(identity) }
            } else {
                null
            }
        },
        PushRegistrationRepositoryImpl(PushRegistrationRemoteDataSourceImpl(provider, binding)),
        PushRegistrationLocalRepositoryImpl(
            context,
            { secureEnvironmentKey(configuration.backendSessionNamespace, configuration.backendApiBaseUrl) },
            sessions::isCurrent
        )
    )
}
