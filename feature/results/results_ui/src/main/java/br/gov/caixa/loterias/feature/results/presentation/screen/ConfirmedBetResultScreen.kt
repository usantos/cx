package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.presentation.state.ConfirmedBetResultsState
import br.gov.caixa.loterias.feature.results.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

@Composable
internal fun DeliveredConfirmedBetResultScreen(
    bet: ConfirmedBet,
    state: ConfirmedBetResultsState,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onRetry: () -> Unit,
    onRedeemPrize: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val lottery = loteriasCaixaDSLotteryStyle(bet.modalityId, bet.modality)
    val campaignStyle = lottery.takeIf {
        it !=
            resolveLoteriasCaixaDSLotteryStyle(bet.modalityId, bet.modality, LoteriasCaixaDSCampaign.DEFAULT)
    }
    val resultText = campaignStyle?.darkContent ?: LoteriasCaixaDS.colors.text
    val serviceMessage = confirmedResultServiceMessage(state.serviceFailure)
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_resultado_da_aposta_4e6334),
        onBack = onBack,
        gradientTopBar = false,
        topBarStartColor = campaignStyle?.light,
        topBarEndColor = campaignStyle?.light,
        topBarContentColor = campaignStyle?.content ?: LoteriasCaixaDSColorTokens.White,
        containerColor = campaignStyle?.dark ?: LoteriasCaixaDS.colors.canvas,
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(LoteriasCaixaDS.colors.surface).padding(16.dp)) {
                LoteriasCaixaDSPrimaryButton(
                    uiStrings.getString(DeliveryR.string.final_return_to_bets),
                    onComplete,
                    (Modifier.fillMaxWidth())
                        .uiId("results.0051")
                )
            }
        }, modifier = Modifier.uiId("results.0052")
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LoteriasCaixaDS.colors.brand)
            }

            state.errorMessage != null || serviceMessage != null -> LoteriasCaixaDSEmptyState(
                title = uiStrings.getString(DeliveryR.string.ui_nao_foi_possivel_consultar_o_resulta_e18575),
                message = state.errorMessage ?: serviceMessage.orEmpty(),
                actionLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                onAction = onRetry,
                modifier = (Modifier.padding(padding)).uiId("results.0053")
            )

            state.results.isNotEmpty() -> {
                ConfirmedBetResultList(bet, state.results, padding, campaignStyle, resultText, onRedeemPrize)
            }

            else -> LoteriasCaixaDSEmptyState(
                title = uiStrings.getString(DeliveryR.string.ui_resultado_ainda_nao_disponivel_1f07c7),
                message = uiStrings.getString(DeliveryR.string.ui_os_concursos_desta_aposta_ainda_nao_71da09),
                actionLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                onAction = onRetry,
                modifier = (Modifier.padding(padding)).uiId("results.0069")
            )
        }
    }
}

@Composable
private fun confirmedResultServiceMessage(failure: br.gov.caixa.loterias.core.domain.service.model.ServiceFailure?): String? {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    return when (failure) {
        is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired -> failure.notice.message ?: uiStrings.getString(
            DeliveryR.string.ui_nao_foi_possivel_consultar_o_resulta_2b3533
        )

        is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.ConnectionFailed -> uiStrings.getString(
            LocalizedR.string.ui_nao_foi_possivel_acessar_o_servico_v_856170
        )

        is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RegistrationRequired -> failure.message

        else -> null
    }
}

@Composable
private fun ConfirmedBetResultList(
    bet: ConfirmedBet,
    results: List<br.gov.caixa.loterias.feature.results.domain.model.DrawResult>,
    padding: androidx.compose.foundation.layout.PaddingValues,
    campaignStyle: br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle?,
    resultText: androidx.compose.ui.graphics.Color,
    onRedeemPrize: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().padding(padding).padding(16.dp).uiId("results.0054").verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        results.forEach { result ->
            ConfirmedBetDrawResult(bet, result, campaignStyle, resultText)
        }
        if (onRedeemPrize != null && bet.prize.signum() > 0 && !bet.redeemedPrize) {
            LoteriasCaixaDSPrimaryButton(
                uiStrings.getString(DeliveryR.string.ui_resgatar_premio_e68bcd),
                onRedeemPrize,
                (Modifier.fillMaxWidth()).uiId("results.0068")
            )
        }
    }
}
