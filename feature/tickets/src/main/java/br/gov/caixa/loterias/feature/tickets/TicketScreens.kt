package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val ticketScreens = listOf(
    WorkflowSpec("tickets/my-bets", "Minhas apostas", "Acompanhe apostas ativas, premiadas e encerradas.", primaryAction = "Ver detalhes"),
    WorkflowSpec("tickets/bet-detail", "Detalhes da aposta", "Confira concurso, números jogados, situação e premiação.", primaryAction = "Ver resultado", notice = "Apostas com Teimosinha podem gerar uma aposta de troca após o resgate."),
    WorkflowSpec("tickets/purchases", "Minhas compras", "Consulte pagamentos e comprovantes por período.", primaryAction = "Ver compra"),
    WorkflowSpec("tickets/purchase-detail", "Detalhes da compra", "Confira itens, pagamento, data e comprovante.", primaryAction = "Ver comprovante"),
    WorkflowSpec("tickets/read", "Ler bilhete", "Aponte a câmera para o código do recibo ou faça a digitação manual.", primaryAction = "Abrir câmera", secondaryAction = "Digitar código"),
    WorkflowSpec("tickets/read-game-choice", "Conferir aposta", "Selecione a aposta realizada.", primaryAction = "Salvar"),
    WorkflowSpec(
        "tickets/manual-code",
        "Digitar código de barras",
        "Informe os dados impressos no recibo lotérico.",
        listOf(
            WorkflowField("barcode", "NSB", required = true),
            WorkflowField("retailer", "Lotérico", required = true),
        ),
        "Continuar",
    ),
    WorkflowSpec("tickets/read-result", "Resultado do bilhete", "Confira os jogos identificados e a situação de cada concurso.", primaryAction = "Salvar"),
)
