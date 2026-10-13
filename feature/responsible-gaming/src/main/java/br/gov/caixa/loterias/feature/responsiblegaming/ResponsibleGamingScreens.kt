package br.gov.caixa.loterias.feature.responsiblegaming

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val responsibleGamingScreens = listOf(
    WorkflowSpec("responsible/start", "Autoavaliação", "Responda algumas perguntas sobre seus hábitos de jogo.", primaryAction = "Iniciar"),
    WorkflowSpec("responsible/form", "Autoavaliação", "Selecione as respostas que melhor representam sua experiência.", listOf(WorkflowField("aware", "Li e responderei com sinceridade", WorkflowFieldKind.CHECKBOX, true)), "Ver resultado"),
    WorkflowSpec("responsible/suspension", "Autoexclusão", "Defina um período de suspensão para apostas digitais.", listOf(WorkflowField("period", "Período de suspensão", required = true), WorkflowField("confirm", "Estou ciente de que a suspensão não pode ser desfeita", WorkflowFieldKind.CHECKBOX, true)), "Confirmar suspensão"),
    WorkflowSpec("responsible/social", "Repasses sociais", "Conheça a destinação social dos recursos arrecadados pelas Loterias CAIXA.", primaryAction = "Entendi"),
)
