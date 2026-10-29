package br.gov.caixa.loterias.feature.account.domain.model

import br.gov.caixa.loterias.feature.account.domain.model.localizedMessage as messageText

/** Falha esperada do serviço; deliberadamente não contém conteúdo de notificação ou credenciais. */
class UnreadNotificationCountUnavailableException : Exception(messageText("contagem_de_notificacoes_indispo_e4b355"))
