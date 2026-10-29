package br.gov.caixa.loterias.apostas.runtime.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import br.gov.caixa.loterias.apostas.MainActivity

internal class ForegroundSessionStartupRestarter(
    private val startFreshStartup: () -> Unit,
    private val finishPreviousHost: () -> Unit,
) {
    fun restart() {
        startFreshStartup()
        finishPreviousHost()
    }
}

/** Uma nova inicialização não leva redirecionamento da fila, dados de teste visual nem navegação salva. */
internal fun foregroundSessionStartupIntent(context: Context): Intent =
    Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

internal fun restartForegroundSession(activity: Activity) {
    ForegroundSessionStartupRestarter(
        { activity.startActivity(foregroundSessionStartupIntent(activity)) },
        activity::finish,
    ).restart()
}
