package br.gov.caixa.loterias.feature.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.support.AcceptTermsUseCase
import br.gov.caixa.loterias.core.domain.support.GetSupportSectionsUseCase
import br.gov.caixa.loterias.core.domain.support.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.core.domain.support.GetTermsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupportViewModel(
    private val getSections: GetSupportSectionsUseCase,
    private val getQuestions: GetSupportQuestionsUseCase,
    private val getTerms: GetTermsUseCase,
    private val acceptTermsUseCase: AcceptTermsUseCase,
    private val privacyHtml: String,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    initialState: SupportUiState = SupportUiState(),
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        if (initialState.mode == SupportMode.TERMS_READ_ONLY) initialState.copy(
            selectedTab = SupportTab.TERMS, showTermsTabs = false, acceptanceRequired = false,
            termsAccepted = false, selectedSectionId = null,
        ) else initialState,
    )
    val state = mutableState.asStateFlow()

    private val mutableTermsAccepted = MutableSharedFlow<Unit>()
    val termsAccepted = mutableTermsAccepted.asSharedFlow()

    init {
        loadSelectedContent()
    }

    fun onAction(action: SupportAction) {
        when (action) {
            is SupportAction.SelectTab -> {
                mutableState.update { it.reduce(action) }
                loadSelectedContent()
            }
            is SupportAction.OpenSection -> {
                mutableState.update { it.reduce(action) }
                loadQuestions(action.id)
            }
            SupportAction.CloseSection -> mutableState.update { it.reduce(action) }
            SupportAction.AcceptTerms -> acceptTerms()
            SupportAction.Back -> Unit
            else -> mutableState.update { it.reduce(action) }
        }
    }

    private fun loadQuestions(sectionId: Long) {
        mutableState.update { it.reduce(SupportAction.StartLoading) }
        viewModelScope.launch(dispatchers.io) {
            when (val outcome = getQuestions(sectionId)) {
                is AppResult.Success -> mutableState.update {
                    it.reduce(SupportAction.QuestionsLoaded(outcome.value))
                }
                is AppResult.Failure -> showLoadFailure()
            }
        }
    }

    fun loadSelectedContent() {
        mutableState.update { it.reduce(SupportAction.StartLoading) }
        viewModelScope.launch(dispatchers.io) {
            when (state.value.selectedTab) {
                SupportTab.FAQ -> when (val outcome = getSections()) {
                    is AppResult.Success -> mutableState.update {
                        it.reduce(SupportAction.SectionsLoaded(outcome.value))
                    }
                    is AppResult.Failure -> showLoadFailure()
                }
                SupportTab.TERMS -> when (val outcome = getTerms()) {
                    is AppResult.Success -> mutableState.update {
                        it.reduce(SupportAction.ContentLoaded(outcome.value))
                    }
                    is AppResult.Failure -> showLoadFailure()
                }
                SupportTab.PRIVACY -> mutableState.update {
                    it.reduce(SupportAction.ContentLoaded(privacyHtml))
                }
            }
        }
    }

    private fun showLoadFailure() {
        mutableState.update {
            it.reduce(SupportAction.LoadFailed("Não foi possível carregar o conteúdo"))
        }
    }

    private fun acceptTerms() {
        if (state.value.mode == SupportMode.TERMS_READ_ONLY || !state.value.termsAccepted) return
        viewModelScope.launch(dispatchers.io) {
            when (acceptTermsUseCase()) {
                is AppResult.Success<*> -> mutableTermsAccepted.emit(Unit)
                is AppResult.Failure -> mutableState.update {
                    it.copy(errorMessage = "Não foi possível aceitar os termos")
                }
            }
        }
    }
}
