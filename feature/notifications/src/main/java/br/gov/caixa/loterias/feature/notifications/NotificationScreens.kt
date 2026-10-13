package br.gov.caixa.loterias.feature.notifications

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val notificationScreens = listOf(
    WorkflowSpec("notifications/messages", "Notificações", "Veja avisos de resultados, pagamentos e novidades.", primaryAction = "Marcar como lidas"),
    WorkflowSpec(
        "notifications/preferences",
        "Preferências",
        "Escolha quais comunicações deseja receber.",
        listOf(
            WorkflowField("all", "Todas as notificações", WorkflowFieldKind.CHECKBOX),
            WorkflowField("payments", "Pagamentos e Resgates", WorkflowFieldKind.CHECKBOX),
            WorkflowField("luck", "Lembretes da Sorte", WorkflowFieldKind.CHECKBOX),
            WorkflowField("results", "Resultados Disponíveis", WorkflowFieldKind.CHECKBOX),
            WorkflowField("news", "Dicas e Novidades", WorkflowFieldKind.CHECKBOX),
            WorkflowField("security", "Alertas de Segurança ou Disponibilidade de Serviços", WorkflowFieldKind.CHECKBOX),
        ),
        "Salvar",
    ),
)
