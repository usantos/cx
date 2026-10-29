package br.gov.caixa.loterias.feature.results.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.results.R

fun resultScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "results/modalities",
        text(R.string.workflow_results_resultados),
        text(R.string.workflow_results_escolha_uma_modalidade_para_consultar_os_ultimos_sorteios),
        primaryAction = text(R.string.workflow_results_consultar)
    ),
    WorkflowSpec(
        "results/detail",
        text(R.string.workflow_results_resultado_do_concurso),
        text(R.string.workflow_results_veja_dezenas_sorteadas_data_arrecadacao_e_faixas_de_premiacao),
        listOf(WorkflowField("contest", text(R.string.workflow_results_concurso), WorkflowFieldKind.NUMBER)),
        text(R.string.workflow_results_buscar_concurso)
    ),
    WorkflowSpec(
        "results/confirmed-bet",
        text(R.string.workflow_results_resultado_da_aposta),
        text(R.string.workflow_results_compare_os_numeros_apostados_com_o_resultado_oficial),
        primaryAction = text(R.string.workflow_results_voltar_as_apostas)
    )
)
