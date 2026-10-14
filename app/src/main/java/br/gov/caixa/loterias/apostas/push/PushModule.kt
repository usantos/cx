package br.gov.caixa.loterias.apostas.push

import android.content.Context
import br.gov.caixa.loterias.core.auth.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.auth.SessionTokenStore
import br.gov.caixa.loterias.core.data.AccountVerificationSessionBinding
import br.gov.caixa.loterias.core.data.RemotePushRegistrationRepository
import br.gov.caixa.loterias.core.data.RetrofitPushRegistrationRemoteDataSource
import br.gov.caixa.loterias.core.domain.push.PushRegistrationStore
import br.gov.caixa.loterias.core.domain.push.RegisterPushDevice
import br.gov.caixa.loterias.core.network.NotificationReadApiProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PushModule {
    @Provides @Singleton
    fun registerPush(@ApplicationContext context: Context, sessions: SessionTokenStore,
        user: CurrentUserIdentityProvider, provider: NotificationReadApiProvider,
        binding: AccountVerificationSessionBinding): RegisterPushDevice = RegisterPushDevice(
        sessions,
        { identity -> if (sessions.isCurrent(identity)) user.currentUser()?.cpf
            ?.takeIf { sessions.isCurrent(identity) } else null },
        RemotePushRegistrationRepository(RetrofitPushRegistrationRemoteDataSource(provider, binding)),
        PreferencesPushRegistrationStore(context),
    )
}

/** Stores only the confirmed full pair digest, never identity documents or credentials. */
class PreferencesPushRegistrationStore(context: Context) : PushRegistrationStore {
    private val preferences = context.getSharedPreferences("push_registration", Context.MODE_PRIVATE)
    override fun contains(marker: String) = preferences.getString("confirmed", null) == marker
    override fun remember(marker: String) { preferences.edit().putString("confirmed", marker).apply() }
}
