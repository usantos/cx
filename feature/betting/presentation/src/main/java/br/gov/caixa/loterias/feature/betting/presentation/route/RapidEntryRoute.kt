package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.RapidEntryCoordinator

@Composable
internal fun RapidEntryRoute(
    coordinator: RapidEntryCoordinator,
    onBack: () -> Unit,
    onCompleteAccount: (PostLoginDestination) -> Unit,
    content: @Composable (HomeBettingTarget) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val result by coordinator.state.collectAsStateWithLifecycle()
    DisposableEffect(coordinator) { onDispose { coordinator.close() } }
    LaunchedEffect(coordinator) { coordinator.prepare() }
    val decision = (result as? AppResult.Success)?.value
    if (decision is HomeBettingDecision.Ready) {
        content(decision.target)
    } else {
        CaixaDSScaffold(
            title = uiStrings.getString(LocalizedR.string.ui_rapidao_658c41),
            onBack = onBack,
            modifier = Modifier
                .uiId("bet.0181")
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (result == null) CircularProgressIndicator()
            }
        }
        LaunchedEffect(decision) {
            if (decision is HomeBettingDecision.CompleteAccount) onCompleteAccount(decision.destination)
        }
        (result as? AppResult.Failure)?.error?.let { error ->
            val message = when (error) {
                AppError.Network -> uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_carregar_o_rapidao_0e0802)

                is AppError.Validation -> error.reason.ifBlank {
                    uiStrings.getString(
                        LocalizedR.string.ui_nao_foi_possivel_abrir_o_rapidao_523eef
                    )
                }

                AppError.Unexpected -> uiStrings.getString(LocalizedR.string.ui_ocorreu_um_erro_inesperado_no_rapida_2ee739)
            }
            CaixaDSAlertDialog(
                uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message,
                stringResource(LocalizedR.string.ui_entendi_9f9373),
                onBack
            )
        }
    }
}
