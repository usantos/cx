package br.gov.caixa.loterias.core.domain.contract.account.model

data class AuthenticatedUser(
    val name: String,
    val cpf: String,
    val email: String,
    val birthDate: String,
)
