package br.gov.caixa.loterias.feature.payment.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.R

fun paymentScreens(text: (Int) -> String): List<WorkflowSpec> = cardPaymentScreens(text) + terminalPaymentScreens(text)

private fun cardPaymentScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "payment/method",
        text(R.string.workflow_payment_forma_de_pagamento),
        text(R.string.workflow_payment_escolha_pix_ou_um_processador_de_cartao_de_credito),
        primaryAction = text(R.string.workflow_payment_continuar)
    ),
    WorkflowSpec(
        "payment/cards",
        text(R.string.workflow_payment_meus_cartoes),
        text(R.string.workflow_payment_cartoes_cadastrados),
        primaryAction = text(R.string.workflow_payment_continuar)
    ),
    WorkflowSpec(
        "payment/add-card",
        text(R.string.workflow_payment_forma_de_pagamento),
        text(R.string.workflow_payment_preencha_os_dados_de_cartao_de_credito_de_sua_titularidade),
        listOf(
            WorkflowField("number", text(R.string.workflow_payment_numero_do_cartao), WorkflowFieldKind.NUMBER, true),
            WorkflowField("name", text(R.string.workflow_payment_nome_igual_ao_cartao), required = true),
            WorkflowField("month", text(R.string.workflow_payment_mes_de_validade), WorkflowFieldKind.NUMBER, true),
            WorkflowField("year", text(R.string.workflow_payment_ano_de_validade), WorkflowFieldKind.NUMBER, true),
            WorkflowField("save", text(R.string.workflow_payment_salvar_cartao), WorkflowFieldKind.CHECKBOX)
        ),
        text(R.string.workflow_payment_continuar)
    ),
    WorkflowSpec(
        "payment/card-preview",
        text(R.string.workflow_payment_forma_de_pagamento),
        text(R.string.workflow_payment_confirme_os_dados_do_cartao_escolhido),
        primaryAction = text(R.string.workflow_payment_incluir_codigo_de_verificacao)
    ),
    WorkflowSpec(
        "payment/confirm-card",
        text(R.string.workflow_payment_forma_de_pagamento),
        text(R.string.workflow_payment_confirme_os_dados_digitados),
        primaryAction = text(R.string.workflow_payment_apostar)
    ),
    WorkflowSpec(
        "payment/cvv",
        text(R.string.workflow_payment_forma_de_pagamento),
        text(R.string.workflow_payment_insira_o_codigo_de_seguranca_do_seu_cartao_de_credito),
        listOf(WorkflowField("cvv", text(R.string.workflow_payment_cvv), WorkflowFieldKind.PASSWORD, true)),
        text(R.string.workflow_payment_confirmar)
    )
)

private fun terminalPaymentScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "payment/confirmation",
        text(R.string.workflow_payment_confirmacao),
        text(R.string.workflow_payment_estamos_processando_sua_compra),
        primaryAction = text(R.string.workflow_payment_acompanhe_suas_compras),
        secondaryAction = text(R.string.workflow_payment_voltar_ao_inicio)
    ),
    WorkflowSpec(
        "payment/failure",
        text(R.string.workflow_payment_pagamento_nao_aprovado),
        text(R.string.workflow_payment_nao_foi_possivel_concluir_sua_compra),
        primaryAction = text(R.string.workflow_payment_tentar_novamente),
        secondaryAction = text(R.string.workflow_payment_voltar_ao_inicio)
    ),
    WorkflowSpec(
        "payment/pix-copy",
        text(R.string.workflow_payment_forma_de_pagamento),
        text(R.string.workflow_payment_copie_o_codigo_abaixo_e_utilize_o_pix_copia_e_cola_no_aplicativo),
        primaryAction = text(R.string.workflow_payment_verificar_pagamento)
    ),
    WorkflowSpec(
        "payment/pix-expired",
        text(R.string.workflow_payment_forma_de_pagamento),
        text(R.string.workflow_payment_o_prazo_para_pagamento_deste_codigo_pix_terminou),
        primaryAction = text(R.string.workflow_payment_gerar_novo_codigo)
    ),
    WorkflowSpec(
        "payment/pix-guidance",
        text(R.string.workflow_payment_forma_de_pagamento),
        text(R.string.workflow_payment_antes_de_prosseguir_leia_atentamente_as_instrucoes_abaixo),
        listOf(WorkflowField("aware", text(R.string.workflow_payment_ciencia), WorkflowFieldKind.CHECKBOX, true)),
        text(R.string.workflow_payment_pagar_agora)
    ),
    WorkflowSpec(
        "payment/success",
        text(R.string.workflow_payment_confirmacao),
        text(R.string.workflow_payment_estamos_processando_sua_compra),
        primaryAction = text(R.string.workflow_payment_acompanhe_suas_compras),
        secondaryAction = text(R.string.workflow_payment_voltar_ao_inicio)
    )
)
