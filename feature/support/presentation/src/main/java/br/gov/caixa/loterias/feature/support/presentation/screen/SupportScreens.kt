package br.gov.caixa.loterias.feature.support.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.support.R

/** Destinos que substituem as Activities de ajuda, dúvidas e termos do aplicativo legado. */
fun supportScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "support",
        text(R.string.workflow_support_duvidas),
        text(R.string.workflow_support_consulte_as_perguntas_e_respostas_das_loterias_caixa),
        primaryAction = text(R.string.workflow_support_continuar)
    ),
    WorkflowSpec(
        "support/terms-view",
        text(R.string.workflow_support_termo_de_uso),
        text(R.string.workflow_support_leia_o_termo_de_uso_das_loterias_caixa),
        primaryAction = text(R.string.workflow_support_continuar)
    ),
    WorkflowSpec(
        "support/terms",
        text(R.string.workflow_support_termos_de_uso),
        text(R.string.workflow_support_consulte_os_termos_de_uso_das_loterias_caixa),
        primaryAction = text(R.string.workflow_support_continuar)
    ),
    WorkflowSpec(
        "support/terms-acceptance",
        text(R.string.workflow_support_termos_de_uso),
        text(R.string.workflow_support_leia_e_aceite_os_termos_de_uso_para_continuar),
        primaryAction = text(R.string.workflow_support_continuar)
    ),
    WorkflowSpec(
        "support/game-help",
        text(R.string.workflow_support_ajuda),
        text(R.string.workflow_support_consulte_concurso_datas_e_regras_da_modalidade),
        primaryAction = text(R.string.workflow_support_continuar)
    )
)
