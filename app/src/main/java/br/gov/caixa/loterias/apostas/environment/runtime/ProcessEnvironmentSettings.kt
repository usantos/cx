package br.gov.caixa.loterias.apostas.environment.runtime

import br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment
import br.gov.caixa.loterias.core.domain.environment.usecase.EnvironmentSelection
import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import android.content.Context
import br.gov.caixa.loterias.apostas.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import br.gov.caixa.loterias.apostas.environment.persistence.EnvironmentPreferences

@Singleton
class ProcessEnvironmentSettings @Inject constructor(@ApplicationContext context: Context) {
    // O CX armazena cada chave em um arquivo de preferências com o mesmo nome.
    private val preferences = EnvironmentPreferences(context)
    internal val selection = EnvironmentSelection(
        flavor = BuildConfig.FLAVOR,
        saved = preferences.read(),
        defaults = EffectiveEnvironment(
            apiBaseUrl = BuildConfig.API_BASE_URL,
            ssoAuthUrl = BuildConfig.SSO_AUTH_URL,
            ssoTokenUrl = BuildConfig.SSO_TOKEN_URL,
            ssoLogoutUrl = BuildConfig.SSO_LOGOUT_URL,
            ssoAccountUrl = BuildConfig.SSO_ACCOUNT_URL,
            ssoClientId = BuildConfig.SSO_CLIENT_ID,
            ssoRedirectUri = BuildConfig.SSO_REDIRECT_URI,
            mercadoPagoBaseUrl = BuildConfig.MERCADO_PAGO_BASE_URL,
            recargaPayBaseUrl = BuildConfig.RECARGA_PAY_BASE_URL,
            queueAccount = BuildConfig.FILABRA_ACCOUNT_SYSTEM_NAME,
            queueSystem = BuildConfig.FILABRA_QUEUE_SYSTEM_NAME,
        ),
    ) { value -> preferences.persist(value) }
    internal val persistedSelection: String? get() = preferences.confirmedSelection
    val effective: EffectiveEnvironment = selection.effective
    val selected: TestEnvironment get() = selection.selected
    val selectorVisible: Boolean get() = selection.selectorVisible

}
