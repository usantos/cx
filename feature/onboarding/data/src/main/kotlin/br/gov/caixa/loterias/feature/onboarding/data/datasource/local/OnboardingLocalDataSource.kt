package br.gov.caixa.loterias.feature.onboarding.data.datasource.local

internal interface OnboardingLocalDataSource {
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean
    suspend fun putBoolean(key: String, value: Boolean)
}
