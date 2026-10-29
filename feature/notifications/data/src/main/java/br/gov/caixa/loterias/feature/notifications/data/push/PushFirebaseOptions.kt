package br.gov.caixa.loterias.feature.notifications.data.push

/** Fornecido uma vez pelo APP; credenciais e destinos não são inferidos nem inventados. */
data class PushFirebaseOptions(val applicationId: String, val apiKey: String, val projectId: String, val senderId: String)
