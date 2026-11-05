package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepositoryFactory

class SessionAccountRepositoryFactory(private val provider: LoteriasApiProvider, private val sessions: AccountVerificationSessionBinding) :
    AccountRepositoryFactory {
    override fun forSession(identity: AuthenticatedSessionIdentity?): AccountRepository = AccountRepositoryImpl(
        AccountRemoteDataSourceImpl(provider, identity?.let(sessions::bind))
    )
}
