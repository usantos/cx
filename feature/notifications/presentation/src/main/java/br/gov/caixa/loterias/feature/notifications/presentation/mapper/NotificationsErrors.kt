package br.gov.caixa.loterias.feature.notifications.presentation.mapper

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationText

internal fun AppError.notificationUserMessage(): NotificationText = when (this) {
    AppError.Network -> NotificationText.Resource(R.string.notifications_nao_foi_possivel_atualizar_as_notificacoes_verifique_sua_internet)

    is AppError.Validation -> if (reason.isNotBlank()) {
        NotificationText.Plain(reason)
    } else {
        NotificationText.Resource(R.string.notifications_nao_foi_possivel_atualizar_as_notificacoes)
    }

    AppError.Unexpected -> NotificationText.Resource(R.string.notifications_ocorreu_um_erro_inesperado_ao_atualizar_as_notificacoes)
}
