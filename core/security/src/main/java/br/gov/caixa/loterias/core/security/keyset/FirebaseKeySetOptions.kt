package br.gov.caixa.loterias.core.security.keyset

/** A configuração de compilação e variante é fornecida pela raiz de composição do aplicativo. */
data class FirebaseKeySetOptions(val applicationId: String, val apiKey: String, val projectId: String, val senderId: String)
