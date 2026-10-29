package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.error.toBackendHttpFailure
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.feature.betting.data.dto.AddBetRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddComboRequestDto

class BettingRemoteDataSourceImpl(
    private val apiProvider: LoteriasApiProvider,
    private val legacyApiProvider: BackendApiProvider? = null,
) : BettingRemoteDataSource {
    override suspend fun modalities() = apiProvider.current().featureBettingApi().lotteryModalities().payload

    override suspend fun bettingParameters() = apiProvider.current().featureBettingApi().bettingParameters().payload

    override suspend fun combos() = apiProvider.current().featureBettingApi().betCombos().payload

    override suspend fun addBet(request: AddBetRequestDto) = try {
        if (request.surprise) {
            val body = br.gov.caixa.loterias.feature.betting.data.dto.SurpriseBetRequestDto(
                modality = request.modality,
                contestType = request.contestType,
                surpriseIndicator = request.surpriseIndicator,
                targetContest = request.targetContest,
                numberQuantity = request.numberQuantity,
                recurringDraws = request.recurringDraws,
                surpriseQuantity = request.quantity,
                cloverQuantity = request.cloverQuantity,
                generateMirror = request.generateMirror,
                favoriteTeam = request.favoriteTeam,
            )
            val backend = checkNotNull(legacyApiProvider) { "Backend de Surpresinha não configurado" }
            backend.current().featureBettingApi().addSurpriseBets(com.google.gson.Gson().toJsonTree(body)).payload
        } else apiProvider.current().featureBettingApi().addBetToCart(request).payload
    } catch (failure: retrofit2.HttpException) {
        val parsed = failure.toBackendHttpFailure()
        val message = parsed.metadata?.message?.takeIf(String::isNotBlank)
        if (message != null) throw IllegalArgumentException(message)
        throw failure
    }

    override suspend fun addCombo(request: AddComboRequestDto) = apiProvider.current().featureBettingApi().addComboToCart(request).payload

    override suspend fun sportsTeamShields() = legacyApiProvider?.current()?.featureBettingApi()?.sportsTeams()?.payload.orEmpty()
}
