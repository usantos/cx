package br.gov.caixa.loterias.feature.results

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val resultScreens = listOf(
    WorkflowSpec("results/modalities", "Resultados", "Escolha uma modalidade para consultar os últimos sorteios.", primaryAction = "Consultar"),
    WorkflowSpec("results/detail", "Resultado do concurso", "Veja dezenas sorteadas, data, arrecadação e faixas de premiação.", listOf(WorkflowField("contest", "Concurso", WorkflowFieldKind.NUMBER)), "Buscar concurso"),
    WorkflowSpec("results/confirmed-bet", "Resultado da aposta", "Compare os números apostados com o resultado oficial.", primaryAction = "Voltar às apostas"),
)
