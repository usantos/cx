package br.gov.caixa.loterias.core.auth.foreground

import br.gov.caixa.loterias.core.auth.session.ForegroundSessionAccess
import br.gov.caixa.loterias.core.auth.session.ForegroundSessionCheck

/** Mudanças de configuração reconstroem a função de retorno a partir de um marcador opaco e da proteção original da sessão. */
class ForegroundSessionNoticeController(
    private val sessions: ForegroundSessionAccess,
    private val nowSeconds: () -> Long,
    private val onLogin: () -> Unit,
) {
    fun show(check: ForegroundSessionCheck): String? = sessions.rememberForegroundNotice(check)

    fun restore(marker: String): (() -> Unit)? {
        val check = sessions.restoreForegroundNotice(marker, nowSeconds()) ?: return null
        return {
            val cleared = sessions.clearForegroundSession(check)
            sessions.dismissForegroundNotice(marker)
            if (cleared) onLogin()
        }
    }

    fun dismiss(marker: String) = sessions.dismissForegroundNotice(marker)
}
