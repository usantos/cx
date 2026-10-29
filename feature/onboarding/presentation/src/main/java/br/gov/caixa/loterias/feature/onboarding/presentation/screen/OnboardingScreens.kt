package br.gov.caixa.loterias.feature.onboarding.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.onboarding.R

fun onboardingScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "onboarding/splash",
        text(R.string.workflow_onboarding_loterias_caixa),
        text(R.string.workflow_onboarding_carregando_configuracoes_e_verificando_a_versao_do_aplicativo),
        primaryAction = text(R.string.workflow_onboarding_entrar)
    ),
    WorkflowSpec(
        "onboarding/introduction",
        text(R.string.workflow_onboarding_bem_vindo),
        text(R.string.workflow_onboarding_conheca_as_apostas_resultados_favoritos_e_formas_de_pagamento_do),
        primaryAction = text(R.string.workflow_onboarding_comecar)
    ),
    WorkflowSpec(
        "onboarding/tutorial",
        text(R.string.workflow_onboarding_como_apostar),
        text(R.string.workflow_onboarding_escolha_uma_modalidade_marque_seus_numeros_e_confira_a_aposta_ant),
        primaryAction = text(R.string.workflow_onboarding_avancar),
        secondaryAction = text(R.string.workflow_onboarding_pular)
    ),
    WorkflowSpec(
        "onboarding/age",
        text(R.string.workflow_onboarding_jogo_responsavel),
        text(R.string.workflow_onboarding_este_aplicativo_e_destinado_exclusivamente_a_maiores_de_18_anos),
        listOf(
            WorkflowField("adult", text(R.string.workflow_onboarding_confirmo_que_tenho_18_anos_ou_mais), WorkflowFieldKind.CHECKBOX, true)
        ),
        text(R.string.workflow_onboarding_confirmar)
    ),
    WorkflowSpec(
        "onboarding/update",
        text(R.string.workflow_onboarding_atualizacao_disponivel),
        text(R.string.workflow_onboarding_uma_nova_versao_do_aplicativo_esta_disponivel_para_manter_seguran),
        primaryAction = text(R.string.workflow_onboarding_atualizar)
    ),
    WorkflowSpec(
        "onboarding/unavailable",
        text(R.string.workflow_onboarding_aplicativo_indisponivel),
        text(R.string.workflow_onboarding_o_servico_esta_temporariamente_indisponivel_tente_novamente_em_al),
        primaryAction = text(R.string.workflow_onboarding_tentar_novamente)
    )
)
