package br.gov.caixa.loterias.feature.tickets.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.tickets.R

fun ticketScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "tickets/my-bets",
        text(R.string.workflow_tickets_minhas_apostas),
        text(R.string.workflow_tickets_acompanhe_apostas_ativas_premiadas_e_encerradas),
        primaryAction = text(R.string.workflow_tickets_ver_detalhes)
    ),
    WorkflowSpec(
        "tickets/bet-detail",
        text(R.string.workflow_tickets_detalhes_da_aposta),
        text(R.string.workflow_tickets_confira_concurso_numeros_jogados_situacao_e_premiacao),
        primaryAction = text(R.string.workflow_tickets_ver_resultado),
        notice = text(R.string.workflow_tickets_apostas_com_teimosinha_podem_gerar_uma_aposta_de_troca_apos_o_res)
    ),
    WorkflowSpec(
        "tickets/purchases",
        text(R.string.workflow_tickets_minhas_compras),
        text(R.string.workflow_tickets_consulte_pagamentos_e_comprovantes_por_periodo),
        primaryAction = text(R.string.workflow_tickets_ver_compra)
    ),
    WorkflowSpec(
        "tickets/purchase-detail",
        text(R.string.workflow_tickets_detalhes_da_compra),
        text(R.string.workflow_tickets_confira_itens_pagamento_data_e_comprovante),
        primaryAction = text(R.string.workflow_tickets_ver_comprovante)
    ),
    WorkflowSpec(
        "tickets/read",
        text(R.string.workflow_tickets_ler_bilhete),
        text(R.string.workflow_tickets_aponte_a_camera_para_o_codigo_do_recibo_ou_faca_a_digitacao_manua),
        primaryAction = text(R.string.workflow_tickets_abrir_camera),
        secondaryAction = text(R.string.workflow_tickets_digitar_codigo)
    ),
    WorkflowSpec(
        "tickets/read-game-choice",
        text(R.string.workflow_tickets_conferir_aposta),
        text(R.string.workflow_tickets_selecione_a_aposta_realizada),
        primaryAction = text(R.string.workflow_tickets_salvar)
    ),
    WorkflowSpec(
        "tickets/manual-code",
        text(R.string.workflow_tickets_digitar_codigo_de_barras),
        text(R.string.workflow_tickets_informe_os_dados_impressos_no_recibo_loterico),
        listOf(
            WorkflowField("barcode", text(R.string.workflow_tickets_nsb), required = true),
            WorkflowField("retailer", text(R.string.workflow_tickets_loterico), required = true)
        ),
        text(R.string.workflow_tickets_continuar)
    ),
    WorkflowSpec(
        "tickets/read-result",
        text(R.string.workflow_tickets_resultado_do_bilhete),
        text(R.string.workflow_tickets_confira_os_jogos_identificados_e_a_situacao_de_cada_concurso),
        primaryAction = text(R.string.workflow_tickets_salvar)
    )
)
