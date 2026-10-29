package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.feature.account.data.dto.AccountRegistrationDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountSessionDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationRequestDto
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.feature.account.data.dto.MunicipalityDto
import br.gov.caixa.loterias.feature.account.data.dto.NeighborhoodDto
import br.gov.caixa.loterias.feature.account.data.dto.PersonalDataUpdateDto
import br.gov.caixa.loterias.feature.account.data.dto.UserProfileDto
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface AccountLoteriasApi {
    @GET("usuarios/recuperar-dados")
    suspend fun currentUserProfile(): ApiEnvelope<UserProfileDto>

    @GET("municipios")
    suspend fun municipalities(@Query("idUF") stateId: Long): ApiEnvelope<List<MunicipalityDto>>

    @GET("bairros")
    suspend fun neighborhoodsByZipCode(@Query("cep") zipCode: String): ApiEnvelope<List<NeighborhoodDto>>

    @PUT("usuarios")
    suspend fun updatePersonalData(@Body request: PersonalDataUpdateDto): ApiEnvelope<Unit>

    @POST("usuarios")
    suspend fun registerAccount(@Body request: AccountRegistrationDto): ApiEnvelope<UserProfileDto>

    @POST("logado/usuarios/verificar-cadastro")
    suspend fun verifyAccount(
        @Body request: AccountVerificationRequestDto = AccountVerificationRequestDto(),
    ): ApiEnvelope<AccountSessionDto>

    @POST("usuarios/recalcular-limite-autorizado")
    suspend fun recalculateAuthorizedLimit(): ApiEnvelope<UserProfileDto>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun LoteriasApi.featureAccountApi(): AccountLoteriasApi = AccountLoteriasApiProjection(this)

private class AccountLoteriasApiProjection(private val transport: LoteriasApi) : AccountLoteriasApi {
    override suspend fun currentUserProfile(): ApiEnvelope<UserProfileDto> = transport.currentUserProfile()

    override suspend fun municipalities(@Query("idUF") stateId: Long): ApiEnvelope<List<MunicipalityDto>> = transport.municipalities(stateId = stateId)

    override suspend fun neighborhoodsByZipCode(@Query("cep") zipCode: String): ApiEnvelope<List<NeighborhoodDto>> = transport.neighborhoodsByZipCode(zipCode = zipCode)

    override suspend fun updatePersonalData(@Body request: PersonalDataUpdateDto): ApiEnvelope<Unit> = transport.updatePersonalData(request = request)

    override suspend fun registerAccount(@Body request: AccountRegistrationDto): ApiEnvelope<UserProfileDto> = transport.registerAccount(request = request)

    override suspend fun verifyAccount(
        @Body request: AccountVerificationRequestDto,
    ): ApiEnvelope<AccountSessionDto> = transport.verifyAccount(request = request)

    override suspend fun recalculateAuthorizedLimit(): ApiEnvelope<UserProfileDto> = transport.recalculateAuthorizedLimit()
}
