package br.gov.caixa.loterias.feature.tickets.presentation.mapper

import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText

import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseDialogAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseStatusDialog

internal fun Purchase.statusDialog(hasPoolShare: Boolean): PurchaseStatusDialog? {
    if (hasPoolShare) return null
    val pix = paymentMethodId == 6L || paymentMethod.contains("pix", ignoreCase = true)
    val normalizedStatus = status.uppercase().filter(Char::isLetterOrDigit)
    val id = statusId.takeIf { it > 0 } ?: when {
        "CANCEL" in normalizedStatus && "PAGAMENTONAOIDENTIFICADO" in normalizedStatus -> 12
        "CANCEL" in normalizedStatus -> 5
        "ESTORN" in normalizedStatus -> 4
        "NAOAUTORIZ" in normalizedStatus -> 3
        else -> 0
    }
    val cancelledActions = listOf(
        PurchaseDialogAction.SAVE_FAVORITE to "Salvar Carrinho",
        PurchaseDialogAction.HELP to messageText("ajuda_fe0add"),
        PurchaseDialogAction.DISMISS to messageText("cancelar_bb9dbb")
    )
    return when (id) {
        3L -> if (pix) {
            PurchaseStatusDialog(
                messageText("pagamento_nao_realizado_1e6ddf"),
                messageText("nao_foi_possivel_concluir_sua_co_777acc"),
                listOf(
                    PurchaseDialogAction.NEW_PIX to messageText("gerar_novo_codigo_03d852"),
                    PurchaseDialogAction.CHANGE_PAYMENT to "Alterar Pagamento",
                    PurchaseDialogAction.HOME to messageText("voltar_ao_inicio_372787")
                )
            )
        } else {
            PurchaseStatusDialog(
                messageText("atencao_b0f8a4"),
                messageText("nao_foi_possivel_concluir_essa_c_eb0ad1"),
                cancelledActions
            )
        }

        4L -> PurchaseStatusDialog(
            messageText("atencao_b0f8a4"),
            messageText("nao_foi_possivel_completar_a_sua_a93aac"),
            cancelledActions
        )

        5L -> PurchaseStatusDialog(
            messageText("atencao_b0f8a4"),
            if (pix) {
                messageText("nao_recebemos_seu_pix_dentro_do_088627")
            } else {
                messageText("nao_identificamos_o_seu_pagament_ab94c8")
            },
            cancelledActions
        )

        12L -> PurchaseStatusDialog(
            messageText("atencao_b0f8a4"),
            messageText("nao_identificamos_o_seu_pagament_ab94c8"),
            cancelledActions
        )

        else -> null
    }
}

/** Expõe a ajuda somente quando o hospedeiro fornece um destino real compatível. */
internal fun PurchaseStatusDialog.withAvailableHelp(available: Boolean): PurchaseStatusDialog = copy(
    actions = actions.filter { (action, _) -> action != PurchaseDialogAction.HELP || available }
)
