package br.gov.caixa.loterias.feature.responsiblegaming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSocialTransfersUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.SelfAssessmentAnswer
import br.gov.caixa.loterias.core.domain.responsiblegaming.SelfAssessmentQuestion
import br.gov.caixa.loterias.core.domain.responsiblegaming.SocialTransfers
import br.gov.caixa.loterias.core.domain.responsiblegaming.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.AccountSuspension
import br.gov.caixa.loterias.core.domain.responsiblegaming.SuspendAccountUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
) : ViewModel() {
    private val mutableState = MutableStateFlow(ResponsibleGamingUiState(assessmentRequired = assessmentRequired))
    val state = mutableState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<ResponsibleGamingEffect>()
    val effects = mutableEffects.asSharedFlow()

    init {
        if (autoLoad) loadRouteData()
    }

    fun onAction(action: ResponsibleGamingAction) {
        when (action) {
            ResponsibleGamingAction.Start,
            ResponsibleGamingAction.Complete -> emit(ResponsibleGamingEffect.Completed)
            ResponsibleGamingAction.SubmitSuspension -> submitSuspension()
            ResponsibleGamingAction.Back -> emit(ResponsibleGamingEffect.NavigateBack)
            is ResponsibleGamingAction.Answer -> answer(action.affirmative)
            ResponsibleGamingAction.PreviousQuestion -> previousQuestion()
            ResponsibleGamingAction.NextQuestion -> nextQuestion()
            is ResponsibleGamingAction.SelectSuspensionPeriod -> mutableState.update {
                it.copy(selectedSuspensionPeriod = action.label, errorMessage = null)
            }
            ResponsibleGamingAction.ToggleSuspensionAcknowledgement -> mutableState.update {
                it.copy(suspensionAcknowledged = !it.suspensionAcknowledged, errorMessage = null)
            }
            ResponsibleGamingAction.ConfirmSuspension -> confirmSuspension()
            ResponsibleGamingAction.Retry -> if (route == "responsible/suspension" && state.value.suspensionConfirmation) {
                submitSuspension()
            } else {
                loadRouteData()
            }
        }
    }

    fun loadRouteData() {
        when (route) {
            "responsible/form" -> loadQuestions()
            "responsible/social" -> loadSocialTransfers()
        }
    }

    private fun loadQuestions() = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        val result = getQuestions()
        if (!isSessionCurrent()) return@launch
        when (result) {
            is AppResult.Success -> mutableState.update {
                it.copy(loading = false, questions = result.value, currentQuestionIndex = 0, answers = emptyMap())
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
        }
    }

    private fun loadSocialTransfers() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = getSocialTransfers()) {
            is AppResult.Success -> mutableState.update { it.copy(loading = false, socialTransfers = result.value) }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
        }
    }

    private fun answer(affirmative: Boolean) {
        val question = state.value.currentQuestion ?: return
        mutableState.update { it.copy(answers = it.answers + (question.id to affirmative), errorMessage = null) }
    }

    private fun previousQuestion() {
        if (state.value.currentQuestionIndex == 0) {
            emit(ResponsibleGamingEffect.NavigateBack)
        } else {
            mutableState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex - 1) }
        }
    }

    private fun nextQuestion() {
        val current = state.value
        if (current.currentAnswer == null) return
        if (current.currentQuestionIndex < current.questions.lastIndex) {
            mutableState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex + 1) }
        } else {
            submitAnswers(current)
        }
    }

    private fun submitAnswers(current: ResponsibleGamingUiState) = viewModelScope.launch(dispatchers.io) {
        val answers = current.questions.mapNotNull { question ->
            current.answers[question.id]?.let { affirmative ->
                SelfAssessmentAnswer(
                    questionId = question.id,
                    description = if (affirmative) "Sim" else "Não",
                    affirmative = affirmative,
                )
            }
        }
        if (answers.size != current.questions.size) return@launch
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = submitAssessment(answers)) {
            is AppResult.Success -> mutableState.update { it.copy(loading = false, assessmentSubmitted = true) }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
        }
    }

    private fun confirmSuspension() {
        val current = state.value
        if (canSubmitSuspension(current.selectedSuspensionPeriod, current.suspensionAcknowledged)) {
            mutableState.update { it.copy(suspensionConfirmation = true, errorMessage = null) }
        }
    }

    private fun submitSuspension() {
        val hours = suspensionHours(state.value.selectedSuspensionPeriod) ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, errorMessage = null) }
            when (val result = suspendAccount(hours)) {
                is AppResult.Success -> mutableState.update {
                    it.copy(loading = false, suspension = result.value)
                }
                is AppResult.Failure -> mutableState.update {
                    it.copy(loading = false, errorMessage = result.error.userMessage())
                }
            }
        }
    }

    private fun emit(effect: ResponsibleGamingEffect) {
        viewModelScope.launch { mutableEffects.emit(effect) }
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível conectar. Verifique sua internet e tente novamente."
        is AppError.Validation -> reason.ifBlank { "Não foi possível carregar os dados." }
        AppError.Unexpected -> "Ocorreu um erro inesperado. Tente novamente."
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
