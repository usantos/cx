package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import java.text.Normalizer

@Composable
internal fun HomeResultContent(state: ResultsUiState, lottery: HomeLottery, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val errorMessage = state.errorMessage
    val result = state.result
    Box(modifier, contentAlignment = Alignment.Center) {
        when {
            state.isLoading -> br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator(
                color = homeResultContentColor(lottery.resultCode),
                modifier = Modifier.legacyAutomationId("home-result-loading").size(32.dp)
            )

            errorMessage != null -> Text(
                errorMessage,
                (Modifier.padding(24.dp)).uiId("home.0134"),
                color = homeResultContentColor(lottery.resultCode),
                textAlign = TextAlign.Center
            )

            result != null && normalizeModality(result.modality) == normalizeModality(lottery.resultCode) ->
                HomeDrawResult(result, lottery, Modifier.fillMaxSize())

            else -> Text(
                uiStrings.getString(LocalizedR.string.ui_consultando_ultimo_resultado_ff60d8),
                color = homeResultContentColor(lottery.resultCode),
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("home.0135")
            )
        }
    }
}

internal fun normalizeModality(value: String): String {
    val normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "").filter(Char::isLetterOrDigit).lowercase()
    return when (normalized) {
        "super7" -> "supersete"
        "milionaria" -> "maismilionaria"
        else -> normalized
    }
}
