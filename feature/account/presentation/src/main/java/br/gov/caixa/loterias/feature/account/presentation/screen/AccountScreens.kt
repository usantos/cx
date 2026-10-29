package br.gov.caixa.loterias.feature.account.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.account.R

fun accountScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "account/login",
        text(R.string.workflow_account_entrar),
        text(R.string.workflow_account_acesse_sua_conta_caixa_para_apostar_e_acompanhar_seus_jogos),
        listOf(
            WorkflowField("cpf", text(R.string.workflow_account_cpf), WorkflowFieldKind.NUMBER, true),
            WorkflowField("password", text(R.string.workflow_account_senha), WorkflowFieldKind.PASSWORD, true)
        ),
        text(R.string.workflow_account_entrar),
        text(R.string.workflow_account_cadastre_se)
    ),
    WorkflowSpec(
        "account/register",
        text(R.string.workflow_account_cadastro),
        text(R.string.workflow_account_o_cadastro_e_bem_rapido_para_comecar_informe_os_dados_abaixo),
        listOf(
            WorkflowField("cpf", text(R.string.workflow_account_cpf), WorkflowFieldKind.NUMBER, true),
            WorkflowField("name", text(R.string.workflow_account_nome), required = true),
            WorkflowField("cep", text(R.string.workflow_account_cep), WorkflowFieldKind.NUMBER, true),
            WorkflowField("uf", text(R.string.workflow_account_uf), required = true),
            WorkflowField("city", text(R.string.workflow_account_municipio), required = true),
            WorkflowField("email", text(R.string.workflow_account_e_mail), required = true)
        ),
        text(R.string.workflow_account_finalizar_cadastro)
    ),
    WorkflowSpec(
        "account/register-confirmation",
        text(R.string.workflow_account_loterias_caixa),
        text(R.string.workflow_account_cadastro_realizado),
        primaryAction = ""
    ),
    WorkflowSpec(
        "account/personal-data",
        text(R.string.workflow_account_dados_pessoais),
        text(R.string.workflow_account_estes_sao_seus_dados_pessoais_por_favor_mantenha_os_sempre_atuali),
        listOf(
            WorkflowField("cpf", text(R.string.workflow_account_cpf), WorkflowFieldKind.NUMBER, true),
            WorkflowField("name", text(R.string.workflow_account_nome), required = true),
            WorkflowField("cep", text(R.string.workflow_account_cep), WorkflowFieldKind.NUMBER, true),
            WorkflowField("uf", text(R.string.workflow_account_uf), required = true),
            WorkflowField("city", text(R.string.workflow_account_municipio), required = true),
            WorkflowField("email", text(R.string.workflow_account_e_mail), required = true),
            WorkflowField("limit", text(R.string.workflow_account_limite_diario_de_apostas), WorkflowFieldKind.CURRENCY, true)
        ),
        text(R.string.workflow_account_atualizar)
    ),
    WorkflowSpec(
        "account/personal-data-confirmation",
        text(R.string.workflow_account_loterias_caixa),
        text(R.string.workflow_account_cadastro_atualizado),
        primaryAction = ""
    ),
    WorkflowSpec(
        "account/change-login",
        text(R.string.workflow_account_alterar_dados_login_caixa),
        text(R.string.workflow_account_pagina_segura_da_conta_caixa),
        primaryAction = ""
    ),
    WorkflowSpec(
        "account/token",
        text(R.string.workflow_account_token),
        text(R.string.workflow_account_tela_tecnica_de_retorno_da_autenticacao),
        primaryAction = ""
    )
)
