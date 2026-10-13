package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val retailScreens = listOf(
    WorkflowSpec("retail/search", "Casas lotéricas", "Encontre a unidade mais próxima para atendimento e resgate.", listOf(WorkflowField("location", "Cidade, bairro ou CEP", required = true)), "Buscar"),
    WorkflowSpec("retail/location", "Localização", "Permita o acesso à localização para mostrar unidades próximas.", listOf(WorkflowField("permission", "Permitir localização", WorkflowFieldKind.CHECKBOX, true)), "Continuar"),
    WorkflowSpec("retail/prize", "Resgatar prêmio", "Consulte orientações e escolha a forma disponível para o resgate.", primaryAction = "Continuar"),
    WorkflowSpec("retail/prize-guidance", "Lotérica", "Consulte as etapas para resgatar o prêmio em uma unidade lotérica.", primaryAction = "Gerar QR Code"),
    WorkflowSpec("retail/prize-lottery", "Resgate na lotérica", "Apresente o QR Code válido ao atendente da lotérica.", primaryAction = "Voltar"),
    WorkflowSpec("retail/prize-agency", "Resgate em agência", "Consulte documentos e dados necessários para receber em uma agência CAIXA.", primaryAction = "Localizar agência"),
    WorkflowSpec("retail/prize-pix", "Resgate via Pix", "Escolha e confirme a chave Pix para receber o prêmio elegível.", listOf(WorkflowField("pix", "Chave Pix", required = true), WorkflowField("confirm", "Confirmo que a chave pertence ao titular", WorkflowFieldKind.CHECKBOX, true)), "Confirmar chave"),
    WorkflowSpec("retail/prize-confirmation", "Resgate solicitado", "A solicitação foi registrada. Acompanhe o crédito pelo aplicativo.", primaryAction = "Concluir"),
    WorkflowSpec("retail/prize-mercado-pago", "Resgate Mercado Pago", "Confirme seu e-mail para receber o prêmio pelo Mercado Pago.", listOf(WorkflowField("email", "E-mail", required = true)), "Resgatar prêmio"),
    WorkflowSpec("retail/prize-mercado-pago-success", "Resgate Mercado Pago", "A solicitação do prêmio foi efetuada com sucesso.", primaryAction = "Voltar ao início"),
)
