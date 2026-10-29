package br.gov.caixa.loterias.feature.account.data.dto

import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartUploadDto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class UserProfileDto(
    @SerializedName("cpf") val cpf: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("dataNascimento") val birthDate: String? = null,
    @SerializedName("telefone") val phone: PhoneDto? = null,
    @SerializedName("cep") val zipCode: String? = null,
    @SerializedName("aceitaReceberNoticias") val acceptsNotifications: Boolean? = null,
    @SerializedName("municipioId") val municipalityId: MunicipalityIdDto? = null,
    @SerializedName("sexo") val gender: String? = null,
    @SerializedName("limiteMinimoDiario") val minimumDailyLimit: BigDecimal? = null,
    @SerializedName("limiteDiario") val dailyLimit: BigDecimal? = null,
    @SerializedName("limiteDiarioAutorizado") val authorizedDailyLimit: BigDecimal? = null,
)

data class PhoneDto(
    @SerializedName("ddd") val areaCode: String? = null,
    @SerializedName("numero") val number: String? = null,
)

data class MunicipalityIdDto(
    @SerializedName("idUF") val stateId: Long? = null,
    @SerializedName("numero") val number: Long? = null,
    @SerializedName("digitoVerificador") val checkDigit: Long? = null,
)

data class FederativeUnitDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("sigla") val code: String? = null,
    @SerializedName("nome") val name: String? = null,
)

data class MunicipalityDto(
    @SerializedName("id") val id: MunicipalityIdDto? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("uf") val state: FederativeUnitDto? = null,
)

data class NeighborhoodDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("municipio") val municipality: MunicipalityDto? = null,
)

data class PersonalDataUpdateDto(
    @SerializedName("cep") val zipCode: String,
    @SerializedName("municipioId") val municipalityId: MunicipalityIdDto,
    @SerializedName("sexo") val gender: String,
    @SerializedName("alterandoLoterica") val changingRetailer: Boolean = false,
    @SerializedName("limiteDiario") val dailyLimit: BigDecimal,
    @SerializedName("aceitaReceberNoticias") val acceptsNotifications: Boolean,
)

data class AccountRegistrationDto(
    @SerializedName("cep") val zipCode: String,
    @SerializedName("aceitaTermosUso") val acceptsTerms: Boolean,
    @SerializedName("aceitaReceberNoticias") val acceptsNotifications: Boolean,
    @SerializedName("municipioId") val municipalityId: MunicipalityIdDto,
    @SerializedName("sexo") val gender: String,
    @SerializedName("apostas") val bets: List<Any> = emptyList(),
)

data class AccountVerificationRequestDto(
    @SerializedName("apostas") val bets: List<CartUploadDto> = emptyList(),
) {
    override fun toString() = "AccountVerificationRequestDto(redacted)"
}

data class BlockedAccountVerificationEnvelopeDto(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
) {
    override fun toString(): String = "BlockedAccountVerificationEnvelopeDto(redacted)"
}

data class AccountSessionDto(
    @SerializedName("nome") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("primeiroNome") val firstName: String? = null,
    @SerializedName("limiteDiario") val dailyLimit: BigDecimal? = null,
    @SerializedName("suspensaoTemporariaApostador") val temporarySuspension: Boolean? = null,
    @SerializedName("responderAutoavaliacao") val requiresSelfAssessment: Boolean? = null,
    @SerializedName("situacaoApostador") val status: AccountStatusDto? = null,
    @SerializedName("carrinho") val cart: CartDto? = null,
    @SerializedName("ultimaNotificacao") val latestNotification: AccountNotificationDto? = null,
    @SerializedName("pagamentoNaoIdentificado") val unidentifiedPayment: Boolean? = null,
) {
    override fun toString() = "AccountSessionDto(redacted)"
}

data class AccountNotificationDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("titulo") val title: String? = null,
    @SerializedName("conteudo") val content: String? = null,
    @SerializedName("imagem") val imageBase64: String? = null,
    @SerializedName("link") val link: String? = null,
) {
    override fun toString() = "AccountNotificationDto(redacted)"
}

data class AccountStatusDto(
    @SerializedName("enum") val kind: String? = null,
)
