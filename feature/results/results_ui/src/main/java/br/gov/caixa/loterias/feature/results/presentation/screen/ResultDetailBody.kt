package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import br.gov.caixa.loterias.feature.results.ui.R as DeliveryR

@Composable
internal fun ResultDetailBody(
    state: ResultsUiState,
    lotteryStyle: LoteriasCaixaDSLotteryStyle,
    padding: PaddingValues,
    onRetry: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val errorMessage = state.errorMessage
    LazyColumn((Modifier.fillMaxSize().padding(padding).background(lotteryStyle.dark)).uiId("results.0090")) {
        item {
            ResultLotteryHeader(state, lotteryStyle)
        }
        when {
            state.isLoading -> item {
                Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LoteriasCaixaDSColorTokens.White)
                }
            }

            errorMessage != null -> item {
                Column(Modifier.fillMaxWidth().padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        errorMessage,
                        color = LoteriasCaixaDSColorTokens.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .uiId("results.0092")
                    )
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                        (Modifier.padding(top = 20.dp).uiId("results.0093").clickable(onClick = onRetry)).uiId("results.0094"),
                        color = LoteriasCaixaDSColorTokens.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            state.result != null -> item { ResultContent(requireNotNull(state.result), state.modality ?: "megasena") }

            else -> item {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_nenhum_resultado_disponivel_para_est_cec209),
                    (Modifier.fillMaxWidth().padding(30.dp)).uiId("results.0095"),
                    color = LoteriasCaixaDSColorTokens.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
