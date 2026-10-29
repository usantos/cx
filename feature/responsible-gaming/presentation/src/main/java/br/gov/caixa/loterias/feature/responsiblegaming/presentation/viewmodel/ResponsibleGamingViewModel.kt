package br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.coordinator.ResponsibleGamingAssessment
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.coordinator.ResponsibleGamingSocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.coordinator.ResponsibleGamingSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingEffect
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingStateAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.reduceResponsibleGamingState
import kotlinx.coroutines.launch

class ResponsibleGamingViewModel(
    private val route: String,
    private val getQuestions: GetSelfAssessmentQuestionsUseCase,
    private val submitAssessment: SubmitSelfAssessmentUseCase,
    private val getSocialTransfers: GetSocialTransfersUseCase,
    private val suspendAccount: SuspendAccountUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    assessmentRequired: Boolean = false,
    autoLoad: Boolean = true,
    private val isSessionCurrent: () -> Boolean = { true },
    serverTimeSource: br.gov.caixa.loterias.core.common.time.ServerTimeSource = br.gov.caixa.loterias.core.common.time.ServerTimeSource {
        java.time.Instant.now()
    },
) : ViewModel() {
    private val mutableState = reduxStateStore<ResponsibleGamingUiState, ResponsibleGamingStateAction>(
        ResponsibleGamingUiState(assessmentRequired = assessmentRequired),
        ::reduceResponsibleGamingState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val mutableEffects = SingleConsumerEventQueue<ResponsibleGamingEffect>()
    val effects = mutableEffects.events

    private val assessment =
        ResponsibleGamingAssessment(mutableState, viewModelScope, dispatchers, getQuestions, submitAssessment, isSessionCurrent, ::emit)
    private val socialTransfers = ResponsibleGamingSocialTransfers(mutableState, viewModelScope, dispatchers, getSocialTransfers)
    private val suspension = ResponsibleGamingSuspension(mutableState, viewModelScope, dispatchers, suspendAccount, serverTimeSource)

    init {
        if (autoLoad) loadRouteData()
    }

    fun onAction(action: ResponsibleGamingAction) {
        when (action) {
            ResponsibleGamingAction.Start,
            ResponsibleGamingAction.Complete,
            -> emit(ResponsibleGamingEffect.Completed)

            ResponsibleGamingAction.SubmitSuspension -> suspension.submitSuspension()

            ResponsibleGamingAction.Back -> emit(ResponsibleGamingEffect.NavigateBack)

            is ResponsibleGamingAction.Answer -> assessment.answer(action.affirmative)

            ResponsibleGamingAction.PreviousQuestion -> assessment.previousQuestion()

            ResponsibleGamingAction.NextQuestion -> assessment.nextQuestion()

            is ResponsibleGamingAction.SelectSuspensionPeriod -> mutableState.dispatch(
                ResponsibleGamingStateAction.SuspensionPeriodSelected(action.label)
            )

            ResponsibleGamingAction.ToggleSuspensionAcknowledgement -> mutableState.dispatch(
                ResponsibleGamingStateAction.SuspensionAcknowledgementToggled
            )

            ResponsibleGamingAction.ConfirmSuspension -> suspension.confirmSuspension()

            ResponsibleGamingAction.Retry -> if (route == "responsible/suspension" && state.value.suspensionConfirmation) {
                suspension.submitSuspension()
            } else {
                loadRouteData()
            }
        }
    }

    fun loadRouteData() {
        when (route) {
            "responsible/form" -> assessment.loadQuestions()
            "responsible/social" -> socialTransfers.loadSocialTransfers()
        }
    }

    private fun emit(effect: ResponsibleGamingEffect) {
        viewModelScope.launch { mutableEffects.emit(effect) }
    }
}

internal fun suspensionHours(label: String?): Int? = when (label) {
    "24 horas" -> 24
    "48 horas" -> 48
    "7 dias" -> 168
    "15 dias" -> 360
    "30 dias" -> 720
    else -> null
}
