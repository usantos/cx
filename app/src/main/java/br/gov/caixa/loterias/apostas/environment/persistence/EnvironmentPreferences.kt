package br.gov.caixa.loterias.apostas.environment.persistence

import android.content.Context
import android.content.SharedPreferences
import br.gov.caixa.loterias.apostas.environment.localizedMessage as messageText

private const val ENVIRONMENT_SELECTION_KEY = "AMBIENTE_SELECIONADO"

internal class EnvironmentPreferences(private val preferences: SharedPreferences) {
    constructor(context: Context) : this(context.getSharedPreferences(ENVIRONMENT_SELECTION_KEY, Context.MODE_PRIVATE))

    @Volatile private var confirmed: String? = read()
    val confirmedSelection: String? get() = confirmed
    fun read(): String? = runCatching { preferences.getString(ENVIRONMENT_SELECTION_KEY, null) }.getOrNull()
    fun persist(value: String) {
        check(preferences.edit().putString(ENVIRONMENT_SELECTION_KEY, value).commit()) {
            messageText("nao_foi_possivel_salvar_o_ambien_d4a3bf")
        }
        // commit(false) ainda pode atualizar o mapa em memória; somente o sucesso confirma a persistência.
        confirmed = value
    }
}
