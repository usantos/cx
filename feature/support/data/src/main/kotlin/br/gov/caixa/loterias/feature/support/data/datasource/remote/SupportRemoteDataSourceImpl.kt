package br.gov.caixa.loterias.feature.support.data.datasource.remote

import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider

class SupportRemoteDataSourceImpl(
    private val apiProvider: LoteriasApiProvider,
) : SupportRemoteDataSource {
    override suspend fun sections() = apiProvider.current().featureSupportApi().supportSections().payload

    override suspend fun questions(sectionId: Long) = apiProvider.current().featureSupportApi().supportQuestions(sectionId).payload

    override suspend fun terms() = apiProvider.current().featureSupportApi().currentTerms().payload

    override suspend fun acceptTerms() {
        apiProvider.current().featureSupportApi().acceptCurrentTerms()
    }
}
