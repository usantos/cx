package br.gov.caixa.loterias.core.auth

import com.google.gson.Gson

data class AuthenticatedUser(
    val name: String,
    val cpf: String,
    val email: String,
    val birthDate: String,
)

fun interface CurrentUserIdentityProvider {
    fun currentUser(): AuthenticatedUser?
}

class JwtCurrentUserIdentityProvider(
    private val tokenStore: SessionTokenStore,
    private val gson: Gson = Gson(),
) : CurrentUserIdentityProvider {
    override fun currentUser(): AuthenticatedUser? = runCatching {
        val payload = requireNotNull(tokenStore.token()).split('.').getOrNull(1)
            ?: error("JWT inválido")
        val json = payload.decodeBase64Url().toString(Charsets.UTF_8)
        val claims = gson.fromJson(json, IdentityClaims::class.java)
        AuthenticatedUser(
            name = claims.name.orEmpty(),
            cpf = claims.cpf.orEmpty(),
            email = claims.email.orEmpty(),
            birthDate = claims.birthDate.orEmpty(),
        ).takeIf { it.cpf.isNotBlank() && it.name.isNotBlank() }
    }.getOrNull()

    private data class IdentityClaims(
        val name: String? = null,
        val cpf: String? = null,
        val email: String? = null,
        val dataNascimento: String? = null,
    ) {
        val birthDate: String? get() = dataNascimento
    }
}

internal fun String.decodeBase64Url(): ByteArray {
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
    val output = ByteArray(length * 6 / 8)
    var buffer = 0
    var bits = 0
    var outputIndex = 0
    for (character in this) {
        if (character == '=') break
        val value = alphabet.indexOf(character)
        require(value >= 0) { "Base64 URL inválido" }
        buffer = (buffer shl 6) or value
        bits += 6
        if (bits >= 8) {
            bits -= 8
            output[outputIndex++] = (buffer shr bits).toByte()
            buffer = if (bits == 0) 0 else buffer and ((1 shl bits) - 1)
        }
    }
    return output.copyOf(outputIndex)
}
