package br.gov.caixa.loterias.feature.onboarding.data.config

import android.content.Context
import android.content.SharedPreferences

/** Armazena somente o documento público validado; não contém identidade ou credenciais. */
internal interface AppModulesConfigurationCache {
    fun read(): String?
    fun write(value: String): Boolean
}

internal class PreferencesAppModulesConfigurationCache(private val preferences: SharedPreferences) : AppModulesConfigurationCache {
    constructor(context: Context) : this(context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE))

    override fun read(): String? = preferences.getString(LAST_VALID_DOCUMENT, null)

    override fun write(value: String): Boolean = preferences.edit().putString(LAST_VALID_DOCUMENT, value).commit()

    private companion object {
        const val FILE_NAME = "app_modules_last_valid"
        const val LAST_VALID_DOCUMENT = "document"
    }
}
