package br.gov.caixa.loterias.apostas.runtime.shortcuts

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import br.gov.caixa.loterias.apostas.MainActivity
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.runtime.launcher.activeLauncherAlias

internal const val RESULT_SHORTCUT_ACTION = "br.gov.caixa.loterias.apostas.OPEN_RESULTS"
internal const val RESULT_SHORTCUT_ID_EXTRA = "result_shortcut_id"

internal enum class ResultShortcutDestination(val id: String, val route: String) {
    MEGA_SENA("result_megasena", "results/home/megasena"),
    LOTOFACIL("result_lotofacil", "results/home/lotofacil"),
    LOTECA("result_loteca", "results/home/loteca"),
    ALL("results_all", "results/modalities"),
}

internal fun resultShortcutDestination(intent: Intent?): ResultShortcutDestination? =
    intent?.takeIf { it.action == RESULT_SHORTCUT_ACTION }?.getStringExtra(RESULT_SHORTCUT_ID_EXTRA)?.let { id ->
        ResultShortcutDestination.entries.firstOrNull { it.id == id }
    }

internal fun resultShortcutIntent(context: Context, destination: ResultShortcutDestination): Intent =
    Intent(context, MainActivity::class.java).apply {
        action = RESULT_SHORTCUT_ACTION
        putExtra(RESULT_SHORTCUT_ID_EXTRA, destination.id)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }

internal fun reportResultLauncherShortcutUsage(context: Context, destination: ResultShortcutDestination) {
    try {
        context.getSystemService(ShortcutManager::class.java)?.reportShortcutUsed(destination.id)
    } catch (_: IllegalStateException) {
        // O launcher pode ficar indisponível após a navegação concluir.
    } catch (_: SecurityException) {
        // A indisponibilidade do registro não deve impedir o acesso aos resultados.
    }
}

internal fun publishResultLauncherShortcuts(context: Context, owner: ComponentName = activeLauncherAlias(context)): Boolean = try {
    val manager = context.getSystemService(ShortcutManager::class.java)
    if (manager == null || manager.isRateLimitingActive) {
        false
    } else {
        val capacity = manager.maxShortcutCountPerActivity
        val destinations = if (capacity >= ResultShortcutDestination.entries.size) {
            ResultShortcutDestination.entries
        } else {
            ResultShortcutDestination.entries.dropLast(1).take((capacity - 1).coerceAtLeast(0)) +
                listOfNotNull(ResultShortcutDestination.ALL.takeIf { capacity > 0 })
        }
        manager.setDynamicShortcuts(
            destinations.mapIndexed { rank, destination ->
                ShortcutInfo.Builder(context, destination.id)
                    .setShortLabel(context.getString(destination.labelResource()))
                    .setLongLabel(context.getString(destination.labelResource()))
                    .setIcon(Icon.createWithResource(context, destination.iconResource()))
                    .setActivity(owner)
                    .setRank(rank)
                    .setIntent(resultShortcutIntent(context, destination))
                    .build()
            }
        )
    }
} catch (_: IllegalStateException) {
    false
} catch (_: SecurityException) {
    false
}

private fun ResultShortcutDestination.labelResource(): Int = when (this) {
    ResultShortcutDestination.MEGA_SENA -> R.string.shortcut_result_megasena
    ResultShortcutDestination.LOTOFACIL -> R.string.shortcut_result_lotofacil
    ResultShortcutDestination.LOTECA -> R.string.shortcut_result_loteca
    ResultShortcutDestination.ALL -> R.string.shortcut_result_all
}

private fun ResultShortcutDestination.iconResource(): Int = when (this) {
    ResultShortcutDestination.MEGA_SENA -> R.drawable.trevo_megasena
    ResultShortcutDestination.LOTOFACIL -> R.drawable.trevo_lotofacil
    ResultShortcutDestination.LOTECA -> R.drawable.trevo_loteca
    ResultShortcutDestination.ALL -> R.drawable.menu3_resultados
}
