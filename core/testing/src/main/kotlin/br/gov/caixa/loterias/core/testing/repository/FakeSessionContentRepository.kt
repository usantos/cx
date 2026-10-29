package br.gov.caixa.loterias.core.testing.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSessionContentRepository : AuthenticatedSessionContentRepository {
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
