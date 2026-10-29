package br.gov.caixa.loterias.feature.support.presentation.state

import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection

enum class SupportTab { TERMS, PRIVACY, FAQ }

enum class SupportMode { CONTENT, GAME_HELP, TERMS_READ_ONLY }

data class SupportUiState(
    val mode: SupportMode = SupportMode.CONTENT,
    val gameHelpModalityId: Int = 2,
    val gameHelpModalityName: String = "Mega-Sena",
    val gameHelpContestNumber: Int? = null,
    val gameHelpDrawDate: String = "",
    val gameHelpClosingDateTime: String = "",
    val selectedTab: SupportTab = SupportTab.FAQ,
    val showTermsTabs: Boolean = false,
    val sections: List<SupportSection> = emptyList(),
    val selectedSectionId: Long? = null,
    val selectedSectionTitle: String = "",
    val questions: List<SupportQuestion> = emptyList(),
    val htmlContent: String = "",
    val expandedQuestionIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val activeRequestId: Long? = null,
    val errorMessage: String? = null,
    val acceptanceRequired: Boolean = false,
    val termsAccepted: Boolean = false,
)

sealed interface SupportAction {
    data class SelectTab(val tab: SupportTab) : SupportAction
    data class OpenSection(val id: Long, val title: String) : SupportAction
    data object CloseSection : SupportAction
    data class ToggleQuestion(val id: Long) : SupportAction
    data class ContentLoaded(val html: String) : SupportAction
    data class SectionsLoaded(val sections: List<SupportSection>) : SupportAction
    data class QuestionsLoaded(val questions: List<SupportQuestion>) : SupportAction
    data class LoadFailed(val message: String) : SupportAction
    data object StartLoading : SupportAction
    data object ToggleTermsAcceptance : SupportAction
    data object AcceptTerms : SupportAction
    data object Back : SupportAction
}
