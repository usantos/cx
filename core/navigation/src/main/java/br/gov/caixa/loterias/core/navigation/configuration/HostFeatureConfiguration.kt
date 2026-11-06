package br.gov.caixa.loterias.core.navigation.configuration

/** Valores imutáveis do hospedeiro exigidos pelos pontos de entrada das features. */
data class HostFeatureConfiguration(
    val appVersionName: String,
    val accountReferrerUri: String,
    val ssoAccountUrl: String,
    val ssoClientId: String,
    val environmentSelectorVisible: Boolean,
    val homeNavigationEnvironmentLabel: String?,
    val mockBackendEnabled: Boolean,
    val firebaseAppId: String,
    val firebaseApiKey: String,
    val firebaseProjectId: String,
    val firebaseSenderId: String,
    val backendApiBaseUrl: String,
    val backendSessionNamespace: String,
    val ssoAuthUrl: String,
    val ssoTokenUrl: String,
    val ssoLogoutUrl: String,
    val ssoRedirectUri: String,
    val productionBuild: Boolean,
)
