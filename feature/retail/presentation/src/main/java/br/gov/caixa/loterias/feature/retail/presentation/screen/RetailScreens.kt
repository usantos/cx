package br.gov.caixa.loterias.feature.retail.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.retail.R

fun retailScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "retail/search",
        text(R.string.workflow_retail_casas_lotericas),
        text(R.string.workflow_retail_encontre_a_unidade_mais_proxima_para_atendimento_e_resgate),
        listOf(WorkflowField("location", text(R.string.workflow_retail_cidade_bairro_ou_cep), required = true)),
        text(R.string.workflow_retail_buscar)
    ),
    WorkflowSpec(
        "retail/location",
        text(R.string.workflow_retail_localizacao),
        text(R.string.workflow_retail_permita_o_acesso_a_localizacao_para_mostrar_unidades_proximas),
        listOf(WorkflowField("permission", text(R.string.workflow_retail_permitir_localizacao), WorkflowFieldKind.CHECKBOX, true)),
        text(R.string.workflow_retail_continuar)
    ),
    WorkflowSpec(
        "retail/prize",
        text(R.string.workflow_retail_resgatar_premio),
        text(R.string.workflow_retail_consulte_orientacoes_e_escolha_a_forma_disponivel_para_o_resgate),
        primaryAction = text(R.string.workflow_retail_continuar)
    ),
    WorkflowSpec(
        "retail/prize-guidance",
        text(R.string.workflow_retail_loterica),
        text(R.string.workflow_retail_consulte_as_etapas_para_resgatar_o_premio_em_uma_unidade_loterica),
        primaryAction = text(R.string.workflow_retail_gerar_qr_code)
    ),
    WorkflowSpec(
        "retail/prize-lottery",
        text(R.string.workflow_retail_resgate_na_loterica),
        text(R.string.workflow_retail_apresente_o_qr_code_valido_ao_atendente_da_loterica),
        primaryAction = text(R.string.workflow_retail_voltar)
    ),
    WorkflowSpec(
        "retail/prize-agency",
        text(R.string.workflow_retail_resgate_em_agencia),
        text(R.string.workflow_retail_consulte_documentos_e_dados_necessarios_para_receber_em_uma_agenc),
        primaryAction = text(R.string.workflow_retail_localizar_agencia)
    ),
    WorkflowSpec(
        "retail/prize-pix",
        text(R.string.workflow_retail_resgate_via_pix),
        text(R.string.workflow_retail_escolha_e_confirme_a_chave_pix_para_receber_o_premio_elegivel),
        listOf(
            WorkflowField("pix", text(R.string.workflow_retail_chave_pix), required = true),
            WorkflowField(
                "confirm",
                text(R.string.workflow_retail_confirmo_que_a_chave_pertence_ao_titular),
                WorkflowFieldKind.CHECKBOX,
                true
            )
        ),
        text(R.string.workflow_retail_confirmar_chave)
    ),
    WorkflowSpec(
        "retail/prize-confirmation",
        text(R.string.workflow_retail_resgate_solicitado),
        text(R.string.workflow_retail_a_solicitacao_foi_registrada_acompanhe_o_credito_pelo_aplicativo),
        primaryAction = text(R.string.workflow_retail_concluir)
    ),
    WorkflowSpec(
        "retail/prize-mercado-pago",
        text(R.string.workflow_retail_resgate_mercado_pago),
        text(R.string.workflow_retail_confirme_seu_e_mail_para_receber_o_premio_pelo_mercado_pago),
        listOf(WorkflowField("email", text(R.string.workflow_retail_e_mail), required = true)),
        text(R.string.workflow_retail_resgatar_premio)
    ),
    WorkflowSpec(
        "retail/prize-mercado-pago-success",
        text(R.string.workflow_retail_resgate_mercado_pago),
        text(R.string.workflow_retail_a_solicitacao_do_premio_foi_efetuada_com_sucesso),
        primaryAction = text(R.string.workflow_retail_voltar_ao_inicio)
    )
)
