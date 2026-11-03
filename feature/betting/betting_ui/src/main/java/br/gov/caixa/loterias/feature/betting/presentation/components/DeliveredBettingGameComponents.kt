package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
/** textSelecionados possui uma margem nativa de 16dp; o contêiner do bilhete já aplica 8dp. */
@Composable
fun legacyPromptInset() = with(androidx.compose.ui.platform.LocalDensity.current) {
    (16.dp.roundToPx() - 8.dp.roundToPx()).toDp()
}

@Composable
fun BettingGameError(message: String, padding: PaddingValues, onRetry: () -> Unit, onBack: () -> Unit, review: BettingCheckpoint? = null) {
    if (review != null) {
        BettingRecoveredDraftReview(review, onBack)
        return
    }
    Column(
        Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = LoteriasCaixaDS.colors.text, textAlign = TextAlign.Center, modifier = Modifier.uiId("bet.0029"))
        TextButton(onClick = onRetry, modifier = Modifier.uiId("bet.0030")) {
            Text(stringResource(DeliveryR.string.betting_recovery_retry), modifier = Modifier.uiId("bet.0031"))
        }
    }
}
