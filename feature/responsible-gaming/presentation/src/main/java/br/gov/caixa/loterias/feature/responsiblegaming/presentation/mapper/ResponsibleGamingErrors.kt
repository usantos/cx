package br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.formatting.localizedMessage as messageText

internal fun AppError.responsibleGamingUserMessage(): String = when (this) {
    AppError.Network -> messageText("nao_foi_possivel_conectar_verifi_d9330e")
    is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_carregar_os_dad_351069") }
    AppError.Unexpected -> "Ocorreu um erro inesperado. Tente novamente."
}
