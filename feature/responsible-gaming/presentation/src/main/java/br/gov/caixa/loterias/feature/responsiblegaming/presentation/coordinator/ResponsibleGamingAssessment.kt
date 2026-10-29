package br.gov.caixa.loterias.feature.responsiblegaming.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentAnswer
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.responsibleGamingUserMessage
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingEffect
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingStateAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class ResponsibleGamingAssessment(
    private val mutableState: ReduxStateStore<ResponsibleGamingUiState, ResponsibleGamingStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getQuestions: GetSelfAssessmentQuestionsUseCase,
    private val submitAssessment: SubmitSelfAssessmentUseCase,
    private val isSessionCurrent: () -> Boolean,
    private val emit: (ResponsibleGamingEffect) -> Unit,
) {

    private var questionsJob: Job? = null
    private var submissionJob: Job? = null
    private var questionsGeneration = 0L

    fun loadQuestions() {
        val task = synchronized(mutableState) {
            if (!isSessionCurrent() || questionsJob?.isCompleted == false || submissionJob?.isCompleted == false) return
            val generation = ++questionsGeneration
            mutableState.dispatch(ResponsibleGamingStateAction.LoadStarted)
            scope.launch(dispatchers.io, start = CoroutineStart.LAZY) {
                if (!isSessionCurrent()) return@launch
                val result = getQuestions()
                synchronized(mutableState) {
                    if (!isSessionCurrent() || generation != questionsGeneration || submissionJob?.isCompleted == false) return@launch
                    when (result) {
                        is AppResult.Success -> mutableState.dispatch(ResponsibleGamingStateAction.QuestionsLoaded(result.value))

                        is AppResult.Failure -> mutableState.dispatch(
                            ResponsibleGamingStateAction.Failed(result.error.responsibleGamingUserMessage())
                        )
                    }
                }
            }.also { questionsJob = it }
        }
        task.start()
    }

    fun answer(affirmative: Boolean) {
        val question = mutableState.value.currentQuestion ?: return
        mutableState.dispatch(ResponsibleGamingStateAction.Answered(question.id, affirmative))
    }

    fun previousQuestion() {
        if (mutableState.value.currentQuestionIndex == 0) {
            emit(ResponsibleGamingEffect.NavigateBack)
        } else {
            mutableState.dispatch(ResponsibleGamingStateAction.PreviousQuestion)
        }
    }

    fun nextQuestion() {
        synchronized(mutableState) {
            if (submissionJob?.isCompleted == false || mutableState.value.loading || mutableState.value.assessmentSubmitted) return
            val current = mutableState.value
            if (current.currentAnswer == null) return
            if (current.currentQuestionIndex < current.questions.lastIndex) {
                mutableState.dispatch(ResponsibleGamingStateAction.NextQuestion)
            } else {
                submitAnswers(current)
            }
        }
    }

    private fun submitAnswers(current: ResponsibleGamingUiState) {
        val answers = current.questions.mapNotNull { question ->
            current.answers[question.id]?.let { affirmative ->
                SelfAssessmentAnswer(
                    questionId = question.id,
                    description = "",
                    affirmative = affirmative
                )
            }
        }
        if (answers.size != current.questions.size) return
        // O envio invalida qualquer leitura anterior; seu Job continua proprietário até a conclusão do POST.
        questionsGeneration++
        questionsJob?.cancel()
        mutableState.dispatch(ResponsibleGamingStateAction.LoadStarted)
        val task = scope.launch(dispatchers.io, start = CoroutineStart.LAZY) {
            if (!isSessionCurrent()) return@launch
            val result = submitAssessment(answers)
            if (!isSessionCurrent()) return@launch
            when (result) {
                is AppResult.Success -> mutableState.dispatch(ResponsibleGamingStateAction.AssessmentSubmitted)

                is AppResult.Failure -> mutableState.dispatch(
                    ResponsibleGamingStateAction.Failed(result.error.responsibleGamingUserMessage())
                )
            }
        }
        submissionJob = task
        task.start()
    }
}
