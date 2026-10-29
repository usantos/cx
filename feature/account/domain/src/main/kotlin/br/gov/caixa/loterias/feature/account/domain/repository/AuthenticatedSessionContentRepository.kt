package br.gov.caixa.loterias.feature.account.domain.repository

import kotlinx.coroutines.flow.Flow
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

interface AuthenticatedSessionContentRepository {
    /** A publicação e a verificação da identidade devem ser atômicas em relação ao login, logout e substituição. */
    fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean
    fun observe(): Flow<AuthenticatedSessionContent?>
}
