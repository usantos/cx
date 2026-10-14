package br.gov.caixa.loterias.apostas

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnvironmentSettings @Inject constructor(@ApplicationContext context: Context) {
    // CX stores each key in a preferences file of the same name.
    private val preferences = context.getSharedPreferences(SELECTION_KEY, Context.MODE_PRIVATE)
    internal val selection = EnvironmentSelection(
        flavor = BuildConfig.FLAVOR,
        saved = runCatching { preferences.getString(SELECTION_KEY, null) }.getOrNull(),
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
    ) { value ->
        check(preferences.edit().putString(SELECTION_KEY, value).commit()) {
            "Não foi possível salvar o ambiente."
        }
    }
    val effective: EffectiveEnvironment = selection.effective
    val selected: LegacyTestEnvironment get() = selection.selected
    val selectorVisible: Boolean get() = selection.selectorVisible

    companion object {
        const val SELECTION_KEY = "AMBIENTE_SELECIONADO"
    }
}
