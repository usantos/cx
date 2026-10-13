package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val accountScreens = listOf(
    WorkflowSpec("account/login", "Entrar", "Acesse sua conta CAIXA para apostar e acompanhar seus jogos.", listOf(WorkflowField("cpf", "CPF", WorkflowFieldKind.NUMBER, true), WorkflowField("password", "Senha", WorkflowFieldKind.PASSWORD, true)), "Entrar", "Cadastre-se"),
    WorkflowSpec(
        "account/register",
        "Cadastro",
        "O cadastro é bem rápido. Para começar, informe os dados abaixo:",
        listOf(
            WorkflowField("cpf", "CPF", WorkflowFieldKind.NUMBER, true),
            WorkflowField("name", "Nome", required = true),
            WorkflowField("cep", "CEP", WorkflowFieldKind.NUMBER, true),
            WorkflowField("uf", "UF", required = true),
            WorkflowField("city", "Município", required = true),
            WorkflowField("email", "E-mail", required = true),
        ),
        "Finalizar cadastro",
    ),
    WorkflowSpec("account/register-confirmation", "Loterias CAIXA", "Cadastro realizado.", primaryAction = ""),
    WorkflowSpec(
        "account/personal-data",
        "Dados pessoais",
        "Estes são seus dados pessoais. Por favor, mantenha-os sempre atualizados.",
        listOf(
            WorkflowField("cpf", "CPF", WorkflowFieldKind.NUMBER, true),
            WorkflowField("name", "Nome", required = true),
            WorkflowField("cep", "CEP", WorkflowFieldKind.NUMBER, true),
            WorkflowField("uf", "UF", required = true),
            WorkflowField("city", "Município", required = true),
            WorkflowField("email", "E-mail", required = true),
            WorkflowField("limit", "Limite Diário de Apostas", WorkflowFieldKind.CURRENCY, true),
        ),
        "Atualizar",
    ),
    WorkflowSpec("account/personal-data-confirmation", "Loterias CAIXA", "Cadastro atualizado.", primaryAction = ""),
    WorkflowSpec("account/change-login", "Alterar dados Login CAIXA", "Página segura da Conta CAIXA.", primaryAction = ""),
    WorkflowSpec("account/token", "Token", "Tela técnica de retorno da autenticação.", primaryAction = ""),
)
