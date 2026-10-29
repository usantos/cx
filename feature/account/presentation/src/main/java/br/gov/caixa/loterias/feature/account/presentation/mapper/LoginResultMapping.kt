package br.gov.caixa.loterias.feature.account.presentation.mapper

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText

internal fun AppError.loginUserMessage(): String = when (this) {
    AppError.Network -> messageText("nao_foi_possivel_validar_o_cadas_f5be2c")
    is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_validar_o_cadas_8cef5e") }
    AppError.Unexpected -> messageText("ocorreu_um_erro_inesperado_ao_va_2f618d")
}

internal val PostLoginDestination.loginRoute: String
    get() = when (this) {
        PostLoginDestination.HOME -> "home"
        PostLoginDestination.REGISTRATION -> "account/register"
        PostLoginDestination.TERMS -> "support/terms-acceptance"
        PostLoginDestination.SELF_ASSESSMENT -> "responsible/start/required"
    }
