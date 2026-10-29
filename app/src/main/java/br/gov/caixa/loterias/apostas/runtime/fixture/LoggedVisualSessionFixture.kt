package br.gov.caixa.loterias.apostas.runtime.fixture

import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.auth.fixture.SyntheticSessionFixture
import br.gov.caixa.loterias.core.navigation.state.shouldUseLoggedVisualFixture

internal class LoggedVisualSessionFixture @javax.inject.Inject constructor(private val tokens: SessionTokenStore) {
    fun installIfRequested(requested: Boolean) {
        if (!shouldUseLoggedVisualFixture(BuildConfig.DEBUG, BuildConfig.ENABLE_MOCK_BACKEND, requested)) return
        if (tokens.captureIdentity() != null) return
        SyntheticSessionFixture.install(tokens)
    }
}
