package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import kotlinx.coroutines.delay

private const val LOADING_PREVIEW_DURATION_MS = 2_000L

/** Este diálogo do DS bloqueia o fechamento intencionalmente; o responsável pelo exemplo o fecha após dois segundos. */
@Composable
internal fun TimedLoadingPreview(state: PreviewState) {
    CaixaDSText(stringResource(R.string.timed_loading_note))
    OpenPreview(state) {
        LaunchedEffect(state) {
            delay(LOADING_PREVIEW_DURATION_MS)
            state.dismiss()
        }
        CaixaDSAuthenticationLoadingDialog(label = state.sample)
    }
}
