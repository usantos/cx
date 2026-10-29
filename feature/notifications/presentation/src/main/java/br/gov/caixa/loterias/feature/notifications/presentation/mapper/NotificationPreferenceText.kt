package br.gov.caixa.loterias.feature.notifications.presentation.mapper

import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory

internal fun NotificationCategory?.titleResource(): Int = when (this) {
    null -> R.string.notifications_todas_as_notificacoes
    NotificationCategory.PAYMENTS_REDEMPTIONS -> R.string.notifications_pagamentos_e_resgates
    NotificationCategory.LUCK_REMINDERS -> R.string.notifications_lembretes_da_sorte
    NotificationCategory.RESULTS_AVAILABLE -> R.string.notifications_resultados_disponiveis
    NotificationCategory.TIPS_NEWS -> R.string.notifications_dicas_e_novidades
    NotificationCategory.SECURITY_ALERTS -> R.string.notifications_alertas_de_seguranca_ou_disponibilidade_de_servicos
}

internal fun NotificationCategory.descriptionResource(): Int = when (this) {
    NotificationCategory.PAYMENTS_REDEMPTIONS -> R.string.notifications_confirmacao_e_pendencias_de_pagamentos_e_confirmacao_de_resgates_
    NotificationCategory.LUCK_REMINDERS -> R.string.notifications_avisos_quando_houver_oportunidades_de_ganhar_mais_concurso_especi
    NotificationCategory.RESULTS_AVAILABLE -> R.string.notifications_avisos_quando_resultados_das_suas_apostas_estiverem_disponiveis_p
    NotificationCategory.TIPS_NEWS -> R.string.notifications_novos_produtos_ou_servicos_no_app
    NotificationCategory.SECURITY_ALERTS -> R.string.notifications_avisos_para_proteger_voce_e_melhorar_sua_experiencia
}
