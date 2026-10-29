package br.gov.caixa.loterias.feature.notifications.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.notifications.R

fun notificationScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "notifications/messages",
        text(R.string.notifications_central_de_notificacoes),
        text(R.string.notifications_veja_suas_notificacoes_e_avisos),
        primaryAction = text(R.string.notifications_marcar_todas_como_lidas)
    ),
    WorkflowSpec(
        "notifications/preferences",
        text(R.string.notifications_preferencias),
        text(R.string.notifications_escolha_quais_comunicacoes_deseja_receber),
        listOf(
            WorkflowField("all", text(R.string.notifications_todas_as_notificacoes), WorkflowFieldKind.CHECKBOX),
            WorkflowField("payments", text(R.string.notifications_pagamentos_e_resgates), WorkflowFieldKind.CHECKBOX),
            WorkflowField("luck", text(R.string.notifications_lembretes_da_sorte), WorkflowFieldKind.CHECKBOX),
            WorkflowField("results", text(R.string.notifications_resultados_disponiveis), WorkflowFieldKind.CHECKBOX),
            WorkflowField("news", text(R.string.notifications_dicas_e_novidades), WorkflowFieldKind.CHECKBOX),
            WorkflowField(
                "security",
                text(R.string.notifications_alertas_de_seguranca_ou_disponibilidade_de_servicos),
                WorkflowFieldKind.CHECKBOX
            )
        ),
        text(R.string.notifications_salvar)
    )
)
