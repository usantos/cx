package br.gov.caixa.loterias.feature.support

internal fun SupportUiState.reduce(action: SupportAction): SupportUiState = when (action) {
    is SupportAction.SelectTab -> copy(
        selectedTab = action.tab,
        htmlContent = "",
        selectedSectionId = null,
        selectedSectionTitle = "",
        questions = emptyList(),
        errorMessage = null,
    )
    is SupportAction.OpenSection -> copy(
        selectedSectionId = action.id,
        selectedSectionTitle = action.title,
        questions = emptyList(),
        expandedQuestionIds = emptySet(),
        errorMessage = null,
    )
    SupportAction.CloseSection -> copy(
        selectedSectionId = null,
        selectedSectionTitle = "",
        questions = emptyList(),
        expandedQuestionIds = emptySet(),
        errorMessage = null,
    )
    is SupportAction.ToggleQuestion -> copy(
        expandedQuestionIds = if (action.id in expandedQuestionIds) {
            expandedQuestionIds - action.id
        } else {
            expandedQuestionIds + action.id
        },
    )
    is SupportAction.ContentLoaded -> copy(
        htmlContent = action.html,
        isLoading = false,
        errorMessage = null,
    )
    is SupportAction.SectionsLoaded -> copy(
        sections = action.sections,
        isLoading = false,
        errorMessage = null,
    )
    is SupportAction.QuestionsLoaded -> copy(
        questions = action.questions,
        isLoading = false,
        errorMessage = null,
    )
    is SupportAction.LoadFailed -> copy(isLoading = false, errorMessage = action.message)
    SupportAction.StartLoading -> copy(isLoading = true, errorMessage = null)
    SupportAction.ToggleTermsAcceptance -> copy(termsAccepted = !termsAccepted)
    SupportAction.AcceptTerms,
    SupportAction.Back -> this
}
