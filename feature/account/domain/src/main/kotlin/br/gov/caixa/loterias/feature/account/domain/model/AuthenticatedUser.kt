package br.gov.caixa.loterias.feature.account.domain.model

data class AuthenticatedUser(
    val name: String,
    val cpf: String,
    val email: String,
    val birthDate: String,
)
