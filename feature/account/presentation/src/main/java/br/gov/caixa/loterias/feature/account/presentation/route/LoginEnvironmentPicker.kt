package br.gov.caixa.loterias.feature.account.presentation.route

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment

/** A composição fornece a operação; o formulário não possui serviço nem corrotina. */
@Composable
fun LoginEnvironmentPicker(
    selected: TestEnvironment,
    enabled: Boolean,
    onChange: suspend (TestEnvironment) -> Boolean,
    onClose: () -> Unit,
) {
    val latestChange by androidx.compose.runtime.rememberUpdatedState(onChange)
    val model = androidx.lifecycle.viewmodel.compose.viewModel {
        br.gov.caixa.loterias.feature.account.presentation.viewmodel.EnvironmentViewModel(
            createSavedStateHandle(), selected, br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment { latestChange(it) })
    }
    LaunchedEffect(selected) {
        model.bind(selected, br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment { latestChange(it) })
    }
    androidx.compose.runtime.DisposableEffect(model) { onDispose { model.unbind() } }
    val state by model.state.collectAsStateWithLifecycle()
    br.gov.caixa.loterias.feature.account.presentation.components.EnvironmentPicker(
        selected, state, enabled, model::select, model::retry, onClose)
}

