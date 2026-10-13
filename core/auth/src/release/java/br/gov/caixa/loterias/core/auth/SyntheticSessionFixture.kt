package br.gov.caixa.loterias.core.auth

/** Release artifacts never install a visual capture session. */
object SyntheticSessionFixture {
    @Suppress("UNUSED_PARAMETER")
    fun install(store: SessionTokenStore): Boolean = false
}
