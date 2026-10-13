package br.gov.caixa.loterias.core.preferences

import android.content.Context
import br.gov.caixa.loterias.core.domain.betting.ShakeSettingsRepository

class SharedPreferencesShakeSettings(context: Context) : ShakeSettingsRepository {
    private val context = context.applicationContext
    private fun read(key: String) = context.getSharedPreferences(key, Context.MODE_PRIVATE).getBoolean(key, false)
    private fun write(key: String, value: Boolean) {
        check(context.getSharedPreferences(key, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit()) { "Não foi possível salvar a configuração da Aposta Shake." }
    }
    override fun enabled() = read("aposta_shake_ativa")
    override fun setEnabled(enabled: Boolean) = write("aposta_shake_ativa", enabled)
    override fun warningHidden() = read("aposta_shake_ocultar_aviso")
    override fun setWarningHidden(hidden: Boolean) = write("aposta_shake_ocultar_aviso", hidden)
    override fun tutorialCompleted() = read("aposta_shake_tutorial_v1_concluido")
    override fun completeTutorial() { write("aposta_shake_tooltip_pendente", true); write("aposta_shake_tutorial_v1_concluido", true) }
    override fun tooltipPending() = read("aposta_shake_tooltip_pendente")
    override fun consumeTooltip() = write("aposta_shake_tooltip_pendente", false)
}
