package br.gov.caixa.loterias.apostas

import android.app.Activity
import android.content.Context
import android.content.Intent

internal class ForegroundSessionStartupRestarter(
    private val startFreshStartup: () -> Unit,
    private val finishPreviousHost: () -> Unit,
) {
    fun restart() {
        startFreshStartup()
        finishPreviousHost()
    }
}

/** Fresh launch deliberately carries no queue redirect, visual fixture, or saved navigation. */
internal fun foregroundSessionStartupIntent(context: Context): Intent =
    Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

internal fun restartForegroundSession(activity: Activity) {
    ForegroundSessionStartupRestarter(
        { activity.startActivity(foregroundSessionStartupIntent(activity)) },
        activity::finish,
    ).restart()
}
