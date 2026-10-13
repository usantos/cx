package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.auth.ForegroundSessionAccess
import br.gov.caixa.loterias.core.auth.ForegroundSessionCheck

/** Configuration changes rebuild the callback from an opaque marker and the original session guard. */
internal class ForegroundSessionNoticeController(
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
