package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.network.toBackendHttpFailure
import retrofit2.HttpException
import br.gov.caixa.loterias.core.network.AppParametersMapDto
import br.gov.caixa.loterias.core.network.DrawResultDto
import br.gov.caixa.loterias.core.network.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import br.gov.caixa.loterias.core.network.MarketplacePageDto
import br.gov.caixa.loterias.core.network.MarketplacePoolDetailDto
import br.gov.caixa.loterias.core.network.SupportSectionDto
import br.gov.caixa.loterias.core.network.TermsDto
import br.gov.caixa.loterias.core.network.SelfAssessmentAnswerDto
import br.gov.caixa.loterias.core.network.SelfAssessmentPayloadDto
import br.gov.caixa.loterias.core.network.SocialTransfersDto
import br.gov.caixa.loterias.core.network.AccountSuspensionDto
import br.gov.caixa.loterias.core.network.SuspendAccountRequestDto
import br.gov.caixa.loterias.core.network.UserProfileDto
import br.gov.caixa.loterias.core.network.MunicipalityDto
import br.gov.caixa.loterias.core.network.NeighborhoodDto
import br.gov.caixa.loterias.core.network.PersonalDataUpdateDto
import br.gov.caixa.loterias.core.network.AccountRegistrationDto
import br.gov.caixa.loterias.core.network.AccountSessionDto
import br.gov.caixa.loterias.core.network.AccountVerificationRequestDto
import br.gov.caixa.loterias.core.network.CartDto
import br.gov.caixa.loterias.core.network.FavoriteCartDto
import br.gov.caixa.loterias.core.network.FavoriteCartBetDto
import br.gov.caixa.loterias.core.network.AddFavoriteCartRequestDto
import br.gov.caixa.loterias.core.network.SaveFavoriteCartRequestDto
import br.gov.caixa.loterias.core.network.LotteryModalityDto
import br.gov.caixa.loterias.core.network.BettingParametersDto
import br.gov.caixa.loterias.core.network.BetComboDto
import br.gov.caixa.loterias.core.network.AddBetRequestDto
import br.gov.caixa.loterias.core.network.AddComboRequestDto
import br.gov.caixa.loterias.core.network.AddPoolShareRequestDto
import br.gov.caixa.loterias.core.network.SportsTeamShieldDto

interface AppConfigurationRemoteDataSource {
    suspend fun isVersionSupported(versionName: String): Boolean?
    suspend fun appParameters(): AppParametersMapDto?
}

interface MarketplaceRemoteDataSource {
    suspend fun pools(query: Map<String, String>): MarketplacePageDto?
    suspend fun poolDetail(poolCode: String): MarketplacePoolDetailDto?
    suspend fun addPoolShare(request: AddPoolShareRequestDto): CartDto?
}

interface ResultsRemoteDataSource {
    suspend fun latestResult(modality: String): DrawResultDto?
    suspend fun resultByContest(modality: String, contest: Int): DrawResultDto?
    suspend fun resultsForContests(code: String, contests: List<Int>): List<DrawResultDto> =
        contests.mapNotNull { resultByContest(code, it) }
}

interface SupportRemoteDataSource {
    suspend fun sections(): List<SupportSectionDto>?
    suspend fun questions(sectionId: Long): List<SupportSectionDto>?
    suspend fun terms(): TermsDto?
    suspend fun acceptTerms()
}

interface ResponsibleGamingRemoteDataSource {
    suspend fun assessmentQuestions(): SelfAssessmentPayloadDto?
    suspend fun submitAssessment(answers: List<SelfAssessmentAnswerDto>)
    suspend fun socialTransfers(): SocialTransfersDto?
    suspend fun currentUserProfile(): UserProfileDto?
    suspend fun suspendAccount(request: SuspendAccountRequestDto): AccountSuspensionDto?
}

interface AccountRemoteDataSource {
    suspend fun currentUserProfile(): UserProfileDto?
    suspend fun municipalities(stateId: Long): List<MunicipalityDto>?
    suspend fun neighborhoodsByZipCode(zipCode: String): List<NeighborhoodDto>?
    suspend fun updatePersonalData(request: PersonalDataUpdateDto)
    suspend fun registerAccount(request: AccountRegistrationDto)
    suspend fun verifyAccount(): AccountVerificationRemoteData
    suspend fun recalculateAuthorizedLimit(): UserProfileDto?
}

data class AccountVerificationRemoteData(
    val session: AccountSessionDto?,
    val redirect: String?,
)

interface CartRemoteDataSource {
    suspend fun currentCart(): CartDto?
    suspend fun removeCartBet(id: Long): CartDto?
    suspend fun removeCartCombo(id: Long): CartDto?
    suspend fun clearCart(): CartDto?
    suspend fun validateCart()
    suspend fun validateFavoriteCartName(name: String)
    suspend fun saveCurrentCartFavorite(name: String)
}

interface FavoriteCartRemoteDataSource {
    suspend fun favoriteCarts(): List<FavoriteCartDto>?
    suspend fun favoriteCartBets(cartId: Long): List<FavoriteCartBetDto>?
    suspend fun deleteFavoriteCart(cartId: Long): List<FavoriteCartDto>?
    suspend fun deleteFavoriteCartBet(cartId: Long, betId: Long): List<FavoriteCartBetDto>?
    suspend fun addFavoriteCartToCurrent(cartId: Long, specialSelection: Int): CartDto?
}

interface BettingRemoteDataSource {
    suspend fun modalities(): List<LotteryModalityDto>?
    suspend fun bettingParameters(): BettingParametersDto?
    suspend fun combos(): List<BetComboDto>?
    suspend fun addBet(request: AddBetRequestDto): CartDto?
    suspend fun addCombo(request: AddComboRequestDto): CartDto?
    suspend fun sportsTeamShields(): List<SportsTeamShieldDto>?
}

/** Retrofit is confined to this outer data-source adapter. */
class RetrofitLoteriasRemoteDataSource(
    private val apiProvider: LoteriasApiProvider,
    private val legacyApiProvider: LegacyBackendApiProvider? = null,
) : AppConfigurationRemoteDataSource,
    MarketplaceRemoteDataSource,
    ResultsRemoteDataSource,
    SupportRemoteDataSource,
    ResponsibleGamingRemoteDataSource,
    AccountRemoteDataSource,
    CartRemoteDataSource,
    FavoriteCartRemoteDataSource,
    BettingRemoteDataSource {
    override suspend fun isVersionSupported(versionName: String) = apiProvider.current().isVersionSupported(versionName).payload
    override suspend fun appParameters() = apiProvider.current().appParameters().payload.also {
        it?.values?.let(apiProvider::updateParameters)
    }
    override suspend fun pools(query: Map<String, String>) = apiProvider.current().marketplacePools(query).payload
    override suspend fun poolDetail(poolCode: String) = apiProvider.current().marketplacePoolDetail(poolCode).payload
    override suspend fun addPoolShare(request: AddPoolShareRequestDto) =
        apiProvider.current().addPoolShareToCart(request).payload
    override suspend fun latestResult(modality: String) = apiProvider.current().latestResult(modality).payload
    override suspend fun resultByContest(modality: String, contest: Int) = apiProvider.current().resultByContest(modality, contest).payload
    override suspend fun resultsForContests(code: String, contests: List<Int>): List<DrawResultDto> =
        requireNotNull(requireNotNull(legacyApiProvider).current().resultsForContests(code, contests).checkedPayload()) {
            "Resultados sem payload"
        }
    override suspend fun sections() = apiProvider.current().supportSections().payload
    override suspend fun questions(sectionId: Long) = apiProvider.current().supportQuestions(sectionId).payload
    override suspend fun terms() = apiProvider.current().currentTerms().payload
    override suspend fun acceptTerms() {
        apiProvider.current().acceptCurrentTerms()
    }
    override suspend fun assessmentQuestions() = apiProvider.current().selfAssessmentQuestions().payload
    override suspend fun submitAssessment(answers: List<SelfAssessmentAnswerDto>) {
        apiProvider.current().submitSelfAssessment(answers)
    }
    override suspend fun socialTransfers() = apiProvider.current().socialTransfers().payload
    override suspend fun currentUserProfile() = apiProvider.current().currentUserProfile().payload
    override suspend fun municipalities(stateId: Long) = apiProvider.current().municipalities(stateId).payload
    override suspend fun neighborhoodsByZipCode(zipCode: String) =
        apiProvider.current().neighborhoodsByZipCode(zipCode).payload
    override suspend fun updatePersonalData(request: PersonalDataUpdateDto) {
        apiProvider.current().updatePersonalData(request)
    }
    override suspend fun registerAccount(request: AccountRegistrationDto) {
        apiProvider.current().registerAccount(request)
    }
    override suspend fun verifyAccount(): AccountVerificationRemoteData {
        val response = apiProvider.current().verifyAccount(AccountVerificationRequestDto())
        return AccountVerificationRemoteData(response.payload, response.redirect)
    }
    override suspend fun recalculateAuthorizedLimit() =
        apiProvider.current().recalculateAuthorizedLimit().payload
    override suspend fun currentCart() = apiProvider.current().currentCart().payload
    override suspend fun removeCartBet(id: Long) = apiProvider.current().removeCartBet(id).payload
    override suspend fun removeCartCombo(id: Long) = apiProvider.current().removeCartCombo(id).payload
    override suspend fun clearCart() = apiProvider.current().clearCart().payload
    override suspend fun validateCart() {
        try {
            apiProvider.current().validateCart()
        } catch (failure: HttpException) {
            throw failure.toBackendHttpFailure()
        }
    }
    override suspend fun validateFavoriteCartName(name: String) {
        apiProvider.current().validateFavoriteCartName(SaveFavoriteCartRequestDto(name))
    }
    override suspend fun saveCurrentCartFavorite(name: String) {
        apiProvider.current().saveCurrentCartFavorite(SaveFavoriteCartRequestDto(name))
    }
    override suspend fun favoriteCarts() = apiProvider.current().favoriteCarts().payload
    override suspend fun favoriteCartBets(cartId: Long) = apiProvider.current().favoriteCartBets(cartId).payload
    override suspend fun deleteFavoriteCart(cartId: Long) = apiProvider.current().deleteFavoriteCart(cartId).payload
    override suspend fun deleteFavoriteCartBet(cartId: Long, betId: Long) =
        apiProvider.current().deleteFavoriteCartBet(cartId, betId).payload
    override suspend fun addFavoriteCartToCurrent(cartId: Long, specialSelection: Int) =
        apiProvider.current().addFavoriteCartToCurrent(cartId, AddFavoriteCartRequestDto(specialSelection)).payload
    override suspend fun modalities() = apiProvider.current().lotteryModalities().payload
    override suspend fun bettingParameters() = apiProvider.current().bettingParameters().payload
    override suspend fun combos() = apiProvider.current().betCombos().payload
    override suspend fun addBet(request: AddBetRequestDto) = apiProvider.current().addBetToCart(request).payload
    override suspend fun addCombo(request: AddComboRequestDto) = apiProvider.current().addComboToCart(request).payload
    override suspend fun sportsTeamShields() = legacyApiProvider?.current()?.sportsTeams()?.payload.orEmpty()
    override suspend fun suspendAccount(request: SuspendAccountRequestDto) =
        apiProvider.current().suspendAccount(request).payload
}
