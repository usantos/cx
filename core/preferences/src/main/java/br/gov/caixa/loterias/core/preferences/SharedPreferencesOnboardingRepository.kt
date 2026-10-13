package br.gov.caixa.loterias.core.preferences

import android.content.Context
import br.gov.caixa.loterias.core.domain.onboarding.OnboardingRepository
import br.gov.caixa.loterias.core.domain.onboarding.BettingTutorial

internal interface BooleanPreferenceStore {
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}

private class AndroidBooleanPreferenceStore(context: Context) : BooleanPreferenceStore {
    private val context = context.applicationContext

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        context.getSharedPreferences(key, Context.MODE_PRIVATE).getBoolean(key, defaultValue)

    override fun putBoolean(key: String, value: Boolean) {
        context.getSharedPreferences(key, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply()
    }
}

/**
 * Reads the exact preference file/key used by the legacy application, preserving upgrades where
 * the introduction had already been dismissed.
 */
class SharedPreferencesOnboardingRepository internal constructor(
    private val store: BooleanPreferenceStore,
) : OnboardingRepository {
    override fun isIntroductionCompleted(): Boolean = store.getBoolean(INTRODUCTION_KEY, false)

    override fun completeIntroduction() {
        store.putBoolean(INTRODUCTION_KEY, true)
    }

    override fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean) {
        store.putBoolean(tutorial.legacyPreferenceKey, dismissed)
    }

    override fun isTutorialDismissed(tutorial: BettingTutorial): Boolean =
        store.getBoolean(tutorial.legacyPreferenceKey, false)

    companion object {
        private const val INTRODUCTION_KEY = "INTRODUCAO_KEY"

        fun fromContext(context: Context): SharedPreferencesOnboardingRepository =
            SharedPreferencesOnboardingRepository(AndroidBooleanPreferenceStore(context))
    }
}
