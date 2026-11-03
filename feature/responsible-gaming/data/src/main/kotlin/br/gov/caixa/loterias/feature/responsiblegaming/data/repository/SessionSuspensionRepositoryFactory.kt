package br.gov.caixa.loterias.feature.responsiblegaming.data.repository

import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.SessionSuspensionRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.ResponsibleGamingRepository
import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.SuspensionRepositoryFactory

class SessionSuspensionRepositoryFactory(
    private val provider: LoteriasApiProvider,
    private val captureSession: () -> AccountVerificationSession?,
) : SuspensionRepositoryFactory {
    override fun forCurrentSession(): ResponsibleGamingRepository = ResponsibleGamingRepositoryImpl(
        SessionSuspensionRemoteDataSourceImpl(provider, captureSession())
    )
}
