package br.gov.caixa.loterias.core.domain.onboarding

/** Preference contract kept in the domain so startup policy does not depend on Android APIs. */
interface OnboardingRepository {
    fun isIntroductionCompleted(): Boolean
    fun completeIntroduction()
    fun isTutorialDismissed(tutorial: BettingTutorial): Boolean
    fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean)
}

enum class BettingTutorial(val legacyPreferenceKey: String) {
    POOL("BOLAO_TUTORIAL"),
    MAIS_MILIONARIA("MAIS_MILIONARIA_TUTORIAL"),
    SUPER_SETE("SUPER_SETE_TUTORIAL"),
    RAPID("RAPIDAO_KEY"),
}

fun interface ShouldShowIntroductionUseCase {
    operator fun invoke(): Boolean
}

class ShouldShowIntroduction(
    private val repository: OnboardingRepository,
) : ShouldShowIntroductionUseCase {
    override fun invoke(): Boolean = !repository.isIntroductionCompleted()
}

fun interface CompleteIntroductionUseCase {
    operator fun invoke()
}

class CompleteIntroduction(
    private val repository: OnboardingRepository,
) : CompleteIntroductionUseCase {
    override fun invoke() = repository.completeIntroduction()
}

fun interface SetBettingTutorialDismissedUseCase {
    operator fun invoke(tutorial: BettingTutorial, dismissed: Boolean)
}

class SetBettingTutorialDismissed(
    private val repository: OnboardingRepository,
) : SetBettingTutorialDismissedUseCase {
    override fun invoke(tutorial: BettingTutorial, dismissed: Boolean) =
        repository.setTutorialDismissed(tutorial, dismissed)
}
