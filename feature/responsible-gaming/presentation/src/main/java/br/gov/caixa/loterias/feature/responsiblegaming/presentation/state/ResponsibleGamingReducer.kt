package br.gov.caixa.loterias.feature.responsiblegaming.presentation.state

import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers

sealed interface ResponsibleGamingStateAction {
    data object LoadStarted : ResponsibleGamingStateAction
    data class Failed(val message: String) : ResponsibleGamingStateAction
    data class QuestionsLoaded(val questions: List<SelfAssessmentQuestion>) : ResponsibleGamingStateAction
    data class Answered(val questionId: Int, val affirmative: Boolean) : ResponsibleGamingStateAction
    data object PreviousQuestion : ResponsibleGamingStateAction
    data object NextQuestion : ResponsibleGamingStateAction
    data object AssessmentSubmitted : ResponsibleGamingStateAction
    data class SocialTransfersLoaded(val transfers: SocialTransfers) : ResponsibleGamingStateAction
    data class SuspensionPeriodSelected(val label: String) : ResponsibleGamingStateAction
    data object SuspensionAcknowledgementToggled : ResponsibleGamingStateAction
    data object SuspensionConfirmed : ResponsibleGamingStateAction
    data class Suspended(val suspension: AccountSuspension, val endsAt: String) : ResponsibleGamingStateAction
}

fun reduceResponsibleGamingState(state: ResponsibleGamingUiState, action: ResponsibleGamingStateAction): ResponsibleGamingUiState =
    when (action) {
        ResponsibleGamingStateAction.LoadStarted -> state.copy(loading = true, errorMessage = null)

        is ResponsibleGamingStateAction.Failed -> state.copy(loading = false, errorMessage = action.message)

        is ResponsibleGamingStateAction.QuestionsLoaded -> state.copy(
            loading = false,
            questions = action.questions,
            currentQuestionIndex = 0,
            answers = emptyMap()
        )

        is ResponsibleGamingStateAction.Answered -> state.copy(
            answers = state.answers + (action.questionId to action.affirmative),
            errorMessage = null
        )

        ResponsibleGamingStateAction.PreviousQuestion -> {
            val previous = state.currentQuestionIndex - 1
            state.copy(currentQuestionIndex = previous, answers = state.answers - state.questions[previous].id)
        }

        ResponsibleGamingStateAction.NextQuestion -> {
            val next = state.currentQuestionIndex + 1
            state.copy(currentQuestionIndex = next, answers = state.answers - state.questions[next].id)
        }

        ResponsibleGamingStateAction.AssessmentSubmitted -> state.copy(loading = false, assessmentSubmitted = true)

        is ResponsibleGamingStateAction.SocialTransfersLoaded -> state.copy(loading = false, socialTransfers = action.transfers)

        is ResponsibleGamingStateAction.SuspensionPeriodSelected -> state.copy(selectedSuspensionPeriod = action.label, errorMessage = null)

        ResponsibleGamingStateAction.SuspensionAcknowledgementToggled -> state.copy(
            suspensionAcknowledged = !state.suspensionAcknowledged,
            errorMessage = null
        )

        ResponsibleGamingStateAction.SuspensionConfirmed -> state.copy(suspensionConfirmation = true, errorMessage = null)

        is ResponsibleGamingStateAction.Suspended -> state.copy(
            loading = false,
            suspension = action.suspension,
            suspensionEndsAt = action.endsAt
        )
    }
