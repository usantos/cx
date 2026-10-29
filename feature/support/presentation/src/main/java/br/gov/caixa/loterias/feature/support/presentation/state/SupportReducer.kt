package br.gov.caixa.loterias.feature.support.presentation.state

internal fun reduceSupportState(state: SupportUiState, mutation: SupportMutation): SupportUiState = when (mutation) {
    is SupportMutation.LoadStarted -> state.copy(activeRequestId = mutation.requestId, isLoading = true, errorMessage = null)

    is SupportMutation.LoadCompleted -> if (state.activeRequestId == mutation.requestId) {
        reduceSupportAction(state, mutation.action).copy(activeRequestId = null)
    } else {
        state
    }

    is SupportMutation.LoadFinished -> if (state.activeRequestId == mutation.requestId) {
        state.copy(activeRequestId = null, isLoading = false)
    } else {
        state
    }

    is SupportMutation.ParametersReceived -> if (state.activeRequestId == mutation.requestId) {
        reduceSupportState(state, mutation.value).copy(activeRequestId = null, isLoading = false)
    } else {
        state
    }

    is SupportMutation.Action -> reduceSupportAction(state, mutation.value)

    is SupportMutation.GameHelpLoaded -> state.copy(
        htmlContent = mutation.html,
        isLoading = false,
        errorMessage = null,
        gameHelpContestNumber = null,
        gameHelpDrawDate = "",
        gameHelpClosingDateTime = ""
    )

    is SupportMutation.GameParametersLoaded -> state.copy(
        gameHelpContestNumber = mutation.contestNumber,
        gameHelpDrawDate = mutation.drawDate,
        gameHelpClosingDateTime = mutation.closingDateTime
    )

    is SupportMutation.AcceptanceFailed -> state.copy(errorMessage = mutation.message)
}

private fun reduceSupportAction(state: SupportUiState, action: SupportAction): SupportUiState = when (action) {
    is SupportAction.SelectTab -> state.copy(
        selectedTab = action.tab,
        activeRequestId = null,
        isLoading = false,
        htmlContent = "",
        selectedSectionId = null,
        selectedSectionTitle = "",
        questions = emptyList(),
        errorMessage = null
    )

    is SupportAction.OpenSection -> state.copy(
        selectedSectionId = action.id,
        activeRequestId = null,
        isLoading = false,
        selectedSectionTitle = action.title,
        questions = emptyList(),
        expandedQuestionIds = emptySet(),
        errorMessage = null
    )

    SupportAction.CloseSection -> state.copy(
        activeRequestId = null,
        isLoading = false,
        selectedSectionId = null,
        selectedSectionTitle = "",
        questions = emptyList(),
        expandedQuestionIds = emptySet(),
        errorMessage = null
    )

    is SupportAction.ToggleQuestion -> state.copy(
        expandedQuestionIds = if (action.id in state.expandedQuestionIds) {
            state.expandedQuestionIds - action.id
        } else {
            state.expandedQuestionIds + action.id
        }
    )

    is SupportAction.ContentLoaded -> state.copy(htmlContent = action.html, isLoading = false, errorMessage = null)

    is SupportAction.SectionsLoaded -> state.copy(sections = action.sections, isLoading = false, errorMessage = null)

    is SupportAction.QuestionsLoaded -> state.copy(questions = action.questions, isLoading = false, errorMessage = null)

    is SupportAction.LoadFailed -> state.copy(isLoading = false, errorMessage = action.message)

    SupportAction.StartLoading -> state.copy(isLoading = true, errorMessage = null)

    SupportAction.ToggleTermsAcceptance -> state.copy(termsAccepted = !state.termsAccepted)

    SupportAction.AcceptTerms, SupportAction.Back -> state
}
