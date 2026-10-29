package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.responsiblegaming.R

fun responsibleGamingScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "responsible/start",
        text(R.string.workflow_responsible_gaming_autoavaliacao),
        text(R.string.workflow_responsible_gaming_responda_algumas_perguntas_sobre_seus_habitos_de_jogo),
        primaryAction = text(R.string.workflow_responsible_gaming_iniciar)
    ),
    WorkflowSpec(
        "responsible/form",
        text(R.string.workflow_responsible_gaming_autoavaliacao),
        text(R.string.workflow_responsible_gaming_selecione_as_respostas_que_melhor_representam_sua_experiencia),
        listOf(
            WorkflowField(
                "aware",
                text(R.string.workflow_responsible_gaming_li_e_responderei_com_sinceridade),
                WorkflowFieldKind.CHECKBOX,
                true
            )
        ),
        text(R.string.workflow_responsible_gaming_ver_resultado)
    ),
    WorkflowSpec(
        "responsible/suspension",
        text(R.string.workflow_responsible_gaming_autoexclusao),
        text(R.string.workflow_responsible_gaming_defina_um_periodo_de_suspensao_para_apostas_digitais),
        listOf(
            WorkflowField("period", text(R.string.workflow_responsible_gaming_periodo_de_suspensao), required = true),
            WorkflowField(
                "confirm",
                text(R.string.workflow_responsible_gaming_estou_ciente_de_que_a_suspensao_nao_pode_ser_desfeita),
                WorkflowFieldKind.CHECKBOX,
                true
            )
        ),
        text(R.string.workflow_responsible_gaming_confirmar_suspensao)
    ),
    WorkflowSpec(
        "responsible/social",
        text(R.string.workflow_responsible_gaming_repasses_sociais),
        text(R.string.workflow_responsible_gaming_conheca_a_destinacao_social_dos_recursos_arrecadados_pelas_loteri),
        primaryAction = text(R.string.workflow_responsible_gaming_entendi)
    )
)
