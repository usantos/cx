package br.gov.caixa.loterias.apostas.runtime.launcher

import android.content.Context
import android.util.Log
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset

internal enum class LauncherIconApplyResult { APPLIED, UNCHANGED, DEFERRED, FAILED }

internal interface LauncherIconPlatform {
    fun enabledPresets(): Set<LauncherIconPreset>
    fun shortcutsMatch(preset: LauncherIconPreset): Boolean
    fun canPublishShortcuts(): Boolean
    fun prepare(preset: LauncherIconPreset)
    fun activate(preset: LauncherIconPreset)
    fun publishShortcuts(preset: LauncherIconPreset): Boolean
}

internal class LauncherIconController(private val platform: LauncherIconPlatform) {
    constructor(context: Context) : this(AndroidLauncherIconPlatform(context.applicationContext))

    fun apply(preset: LauncherIconPreset): LauncherIconApplyResult {
        val previous = platform.enabledPresets()
        if (previous == setOf(preset) && platform.shortcutsMatch(preset)) return LauncherIconApplyResult.UNCHANGED
        if (!platform.canPublishShortcuts()) return LauncherIconApplyResult.DEFERRED
        val fallback = previous.firstOrNull() ?: LauncherIconPreset.DEFAULT
        var publishedOwner: LauncherIconPreset? = null
        return try {
            // Mantém o proprietário anterior habilitado até ShortcutManager aceitar o novo proprietário.
            platform.prepare(preset)
            if (platform.publishShortcuts(preset)) {
                publishedOwner = preset
                platform.activate(preset)
                LauncherIconApplyResult.APPLIED
            } else {
                restore(fallback, publishedOwner)
            }
        } catch (failure: IllegalStateException) {
            failed(fallback, publishedOwner, failure)
        } catch (failure: IllegalArgumentException) {
            failed(fallback, publishedOwner, failure)
        } catch (failure: SecurityException) {
            failed(fallback, publishedOwner, failure)
        }
    }

    private fun failed(previous: LauncherIconPreset, publishedOwner: LauncherIconPreset?, failure: Exception): LauncherIconApplyResult {
        Log.w(TAG, "Launcher update failed; restoring previous alias", failure)
        return restore(previous, publishedOwner)
    }

    private fun restore(previous: LauncherIconPreset, publishedOwner: LauncherIconPreset?): LauncherIconApplyResult {
        try {
            platform.prepare(previous)
            // Reassocia antes de desabilitar o proprietário aceito; preserva-o se a restauração sofrer limitação de frequência.
            val restored = platform.shortcutsMatch(previous) || platform.publishShortcuts(previous)
            platform.activate(if (restored) previous else publishedOwner ?: previous)
        } catch (failure: IllegalStateException) {
            Log.w(TAG, "Launcher restoration interrupted", failure)
        } catch (failure: IllegalArgumentException) {
            Log.w(TAG, "Launcher restoration rejected", failure)
        } catch (failure: SecurityException) {
            Log.w(TAG, "Launcher restoration denied", failure)
        }
        return LauncherIconApplyResult.FAILED
    }
}

private const val TAG = "LauncherIconController"
