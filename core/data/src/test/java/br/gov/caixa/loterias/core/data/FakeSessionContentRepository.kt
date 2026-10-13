package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContent
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeSessionContentRepository : AuthenticatedSessionContentRepository {
    val content = MutableStateFlow<AuthenticatedSessionContent?>(null)
    var accepts = true
    var publications = 0
    var owner: AuthenticatedSessionIdentity? = null
    override fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean {
        publications++
        if (!accepts) return false
        owner = identity
        this.content.value = content
        return true
    }
    override fun observe() = content
}
