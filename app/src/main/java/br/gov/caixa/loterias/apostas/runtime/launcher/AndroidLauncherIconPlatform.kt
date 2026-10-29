package br.gov.caixa.loterias.apostas.runtime.launcher

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ShortcutManager
import android.os.Build
import br.gov.caixa.loterias.apostas.runtime.shortcuts.ResultShortcutDestination
import br.gov.caixa.loterias.apostas.runtime.shortcuts.publishResultLauncherShortcuts
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset

internal fun launcherAlias(context: Context, preset: LauncherIconPreset): ComponentName {
    val name = when (preset) {
        LauncherIconPreset.DEFAULT -> "DefaultLauncher"
        LauncherIconPreset.OUTUBRO_ROSA -> "OutubroRosaLauncher"
        LauncherIconPreset.SETEMBRO_AMARELO -> "SetembroAmareloLauncher"
        LauncherIconPreset.NOVEMBRO_AZUL -> "NovembroAzulLauncher"
        LauncherIconPreset.AGOSTO_LILAS -> "AgostoLilasLauncher"
    }
    return ComponentName(context.packageName, "br.gov.caixa.loterias.apostas.launcher.$name")
}

internal fun activeLauncherAlias(context: Context): ComponentName = launcherAlias(
    context,
    AndroidLauncherIconPlatform(context).enabledPresets().firstOrNull() ?: LauncherIconPreset.DEFAULT
)

internal class AndroidLauncherIconPlatform(private val context: Context) : LauncherIconPlatform {
    private val packageManager get() = context.packageManager
    private val shortcutManager get() = context.getSystemService(ShortcutManager::class.java)

    override fun enabledPresets(): Set<LauncherIconPreset> = LauncherIconPreset.entries.filterTo(linkedSetOf()) { preset ->
        when (packageManager.getComponentEnabledSetting(launcherAlias(context, preset))) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> preset == LauncherIconPreset.DEFAULT
            else -> false
        }
    }

    override fun shortcutsMatch(preset: LauncherIconPreset): Boolean {
        val shortcuts = shortcutManager?.dynamicShortcuts ?: return false
        return shortcuts.map { it.id }.toSet() == ResultShortcutDestination.entries.map { it.id }.toSet() &&
            shortcuts.all { it.activity == launcherAlias(context, preset) }
    }

    override fun canPublishShortcuts(): Boolean = shortcutManager?.let {
        !it.isRateLimitingActive && it.maxShortcutCountPerActivity >= ResultShortcutDestination.entries.size
    } ?: false

    override fun prepare(preset: LauncherIconPreset) {
        packageManager.setComponentEnabledSetting(
            launcherAlias(context, preset),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    override fun activate(preset: LauncherIconPreset) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.setComponentEnabledSettings(
                LauncherIconPreset.entries.map {
                    PackageManager.ComponentEnabledSetting(
                        launcherAlias(context, it),
                        enabledState(it == preset),
                        PackageManager.DONT_KILL_APP
                    )
                }
            )
        } else {
            prepare(preset)
            LauncherIconPreset.entries.filter { it != preset }.forEach {
                packageManager.setComponentEnabledSetting(
                    launcherAlias(context, it),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }

    override fun publishShortcuts(preset: LauncherIconPreset): Boolean =
        publishResultLauncherShortcuts(context, launcherAlias(context, preset))

    private fun enabledState(enabled: Boolean): Int = if (enabled) {
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    } else {
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    }
}
