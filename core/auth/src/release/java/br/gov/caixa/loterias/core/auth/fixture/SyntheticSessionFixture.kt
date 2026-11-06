package br.gov.caixa.loterias.core.auth.fixture

import br.gov.caixa.loterias.core.auth.session.SessionTokenStore

/** Artefatos de produção nunca instalam uma sessão de captura visual. */
object SyntheticSessionFixture {
    @Suppress("UNUSED_PARAMETER")
    fun install(store: SessionTokenStore): Boolean = false
}
