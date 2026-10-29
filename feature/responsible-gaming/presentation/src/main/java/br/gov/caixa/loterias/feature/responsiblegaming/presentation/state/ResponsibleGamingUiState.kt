package br.gov.caixa.loterias.feature.responsiblegaming.presentation.state

import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension

data class ResponsibleGamingUiState(
    val assessmentRequired: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val questions: List<SelfAssessmentQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: Map<Int, Boolean> = emptyMap(),
    val assessmentSubmitted: Boolean = false,
    val selectedSuspensionPeriod: String? = null,
    val suspensionAcknowledged: Boolean = false,
    val suspensionConfirmation: Boolean = false,
    val suspension: AccountSuspension? = null,
    val suspensionEndsAt: String = "",
    val socialTransfers: SocialTransfers? = null,
) {
    val currentQuestion: SelfAssessmentQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val currentAnswer: Boolean?
        get() = currentQuestion?.let { answers[it.id] }

    val hasAffirmativeAnswer: Boolean
        get() = answers.values.any { it }
}

sealed interface ResponsibleGamingAction {
    data object Start : ResponsibleGamingAction
    data class Answer(val affirmative: Boolean) : ResponsibleGamingAction
    data object PreviousQuestion : ResponsibleGamingAction
    data object NextQuestion : ResponsibleGamingAction
    data class SelectSuspensionPeriod(val label: String) : ResponsibleGamingAction
    data object ToggleSuspensionAcknowledgement : ResponsibleGamingAction
    data object ConfirmSuspension : ResponsibleGamingAction
    data object SubmitSuspension : ResponsibleGamingAction
    data object Retry : ResponsibleGamingAction
    data object Back : ResponsibleGamingAction
    data object Complete : ResponsibleGamingAction
}

sealed interface ResponsibleGamingEffect {
    data object NavigateBack : ResponsibleGamingEffect
    data object Completed : ResponsibleGamingEffect
}
