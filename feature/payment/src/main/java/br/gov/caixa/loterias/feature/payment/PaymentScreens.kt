package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val paymentScreens = listOf(
    WorkflowSpec("payment/method", "Forma de pagamento", "Escolha Pix ou um processador de cartão de crédito."),
    WorkflowSpec("payment/cards", "Meus cartões", "Cartões cadastrados."),
    WorkflowSpec(
        "payment/add-card",
        "Forma de pagamento",
        "Preencha os dados de cartão de crédito de sua titularidade",
        listOf(
            WorkflowField("number", "Número do cartão", WorkflowFieldKind.NUMBER, true),
            WorkflowField("name", "Nome igual ao cartão", required = true),
            WorkflowField("month", "Mês de validade", WorkflowFieldKind.NUMBER, true),
            WorkflowField("year", "Ano de validade", WorkflowFieldKind.NUMBER, true),
            WorkflowField("save", "Salvar cartão", WorkflowFieldKind.CHECKBOX),
        ),
        "Continuar",
    ),
    WorkflowSpec("payment/card-preview", "Forma de pagamento", "Confirme os dados do cartão escolhido", primaryAction = "Incluir Código de Verificação"),
    WorkflowSpec("payment/confirm-card", "Forma de pagamento", "Confirme os dados digitados", primaryAction = "Apostar"),
    WorkflowSpec("payment/cvv", "Forma de pagamento", "Insira o código de segurança do seu cartão de crédito", listOf(WorkflowField("cvv", "CVV", WorkflowFieldKind.PASSWORD, true)), "Confirmar"),
    WorkflowSpec("payment/confirmation", "Confirmação", "Estamos processando sua compra.", primaryAction = "Acompanhe suas compras", secondaryAction = "Voltar ao início"),
    WorkflowSpec("payment/failure", "Pagamento não aprovado", "Não foi possível concluir sua compra.", primaryAction = "Tentar novamente", secondaryAction = "Voltar ao início"),
    WorkflowSpec("payment/pix-copy", "Forma de pagamento", "Copie o código abaixo e utilize o Pix Copia e Cola no aplicativo que você fará o pagamento.", primaryAction = "Verificar Pagamento"),
    WorkflowSpec("payment/pix-expired", "Forma de pagamento", "O prazo para pagamento deste código Pix terminou.", primaryAction = "Gerar novo código"),
    WorkflowSpec("payment/pix-guidance", "Forma de pagamento", "Antes de prosseguir, leia atentamente as instruções abaixo.", listOf(WorkflowField("aware", "Ciência", WorkflowFieldKind.CHECKBOX, true)), "Pagar agora!"),
    WorkflowSpec("payment/success", "Confirmação", "Estamos processando sua compra.", primaryAction = "Acompanhe suas compras", secondaryAction = "Voltar ao início"),
)
