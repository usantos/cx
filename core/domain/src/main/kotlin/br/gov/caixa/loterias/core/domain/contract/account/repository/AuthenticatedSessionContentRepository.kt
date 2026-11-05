package br.gov.caixa.loterias.core.domain.contract.account.repository

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import kotlinx.coroutines.flow.Flow

interface AuthenticatedSessionContentRepository {
    /** A publicação e a verificação da identidade devem ser atômicas em relação ao login, logout e substituição. */
    fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean
    fun observe(): Flow<AuthenticatedSessionContent?>
}
