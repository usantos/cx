package br.gov.caixa.loterias.core.security.topaz

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager

/** Nenhum ponto de entrada do SDK é chamado; as preferências contêm apenas nomes de componentes e estados originais do PM. */
fun isolateTopazMockComponents(context: Context, simulated: Boolean) {
    val manager = context.packageManager
    val controller = object : ComponentOverrideController {
        override fun state(name: String) = manager.getComponentEnabledSetting(ComponentName(context.packageName, name))
        override fun setState(name: String, state: Int) {
            manager.setComponentEnabledSetting(ComponentName(context.packageName, name), state, PackageManager.DONT_KILL_APP)
        }
    }
    val preferences = context.getSharedPreferences("topaz_mock_component_overrides", Context.MODE_PRIVATE)
    TopazMockComponentPolicy(topazMockComponents, controller, PreferencesComponentOverrideBackup(preferences)).apply(simulated)
}

private class PreferencesComponentOverrideBackup(private val preferences: SharedPreferences) : ComponentOverrideBackup {
    override fun read(): Map<String, Int>? {
        if (!preferences.getBoolean("saved", false)) return null
        return preferences.all.filterKeys { it.startsWith("state:") }.map { (key, value) ->
            val state = value as? Int ?: error("Registro de componentes de simulação inválido")
            check(state in 0..4) { "Estado de componente de simulação inválido" }
            key.removePrefix("state:") to state
        }.toMap()
    }

    override fun save(states: Map<String, Int>): Boolean {
        val editor = preferences.edit().clear().putBoolean("saved", true)
        states.forEach { (name, state) -> editor.putInt("state:$name", state) }
        return editor.commit()
    }

    override fun clear() = preferences.edit().clear().commit()
}
