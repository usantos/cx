package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.core.presentation.WorkflowSpec

/** Destinos que substituem as Activities de ajuda, dúvidas e termos do aplicativo legado. */
val supportScreens = listOf(
    WorkflowSpec("support", "Dúvidas", "Consulte as perguntas e respostas das Loterias CAIXA."),
    WorkflowSpec("support/terms-view", "Termo de uso", "Leia o Termo de uso das Loterias CAIXA."),
    WorkflowSpec("support/terms", "Termos de Uso", "Consulte os Termos de Uso das Loterias CAIXA."),
    WorkflowSpec("support/terms-acceptance", "Termos de Uso", "Leia e aceite os Termos de Uso para continuar."),
    WorkflowSpec("support/game-help", "Ajuda", "Consulte concurso, datas e regras da modalidade."),
)
