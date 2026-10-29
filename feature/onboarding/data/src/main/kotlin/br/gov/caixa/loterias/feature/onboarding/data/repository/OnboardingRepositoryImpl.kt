package br.gov.caixa.loterias.feature.onboarding.data.repository

import android.content.Context
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.data.datasource.local.OnboardingLocalDataSource
import br.gov.caixa.loterias.feature.onboarding.data.datasource.local.OnboardingLocalDataSourceImpl

/**
 * Persiste preferências de tutorial pertencentes à instalação após uma migração criptografada verificada.
 */
class OnboardingRepositoryImpl internal constructor(private val store: OnboardingLocalDataSource) : OnboardingRepository {
    override suspend fun isIntroductionCompleted(): Boolean = store.getBoolean(INTRODUCTION_KEY, false)

    override suspend fun completeIntroduction() {
        store.putBoolean(INTRODUCTION_KEY, true)
    }

    override suspend fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean) {
        store.putBoolean(tutorial.legacyPreferenceKey, dismissed)
    }

    override suspend fun isTutorialDismissed(tutorial: BettingTutorial): Boolean = store.getBoolean(tutorial.legacyPreferenceKey, false)

    companion object {
        private const val INTRODUCTION_KEY = "INTRODUCAO_KEY"

        fun fromContext(context: Context, factory: SecureStoreFactory = AndroidSecureStores(context)): OnboardingRepositoryImpl =
            OnboardingRepositoryImpl(OnboardingLocalDataSourceImpl(context, factory))
    }
}
