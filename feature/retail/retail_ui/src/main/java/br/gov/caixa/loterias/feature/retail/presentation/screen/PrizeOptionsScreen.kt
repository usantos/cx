package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSBetTypeItem
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSBetTypeStrip
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseHelpIcon
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeOptionRow
import br.gov.caixa.loterias.feature.retail.presentation.mapper.PrizeBetType
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeBetTypes
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.ui.R as DeliveryR

@Composable
internal fun PrizeOptionsScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onSelect: (PrizeRedemptionChannel) -> Unit,
    onBetNow: () -> Unit,
    onRetryContest: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val details = state.details
    val style = loteriasCaixaDSLotteryStyle(details?.modalityId ?: 2, details?.modalityName.orEmpty())
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_resultado_da_aposta_4e6334),
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0093")) {
                LoteriasCaixaDSPurchaseHelpIcon((Modifier.size(24.dp)).uiId("retail.0094"))
            }
        },

        containerColor = LoteriasCaixaDSColorTokens.White,
        modifier = Modifier.uiId("retail.0095")
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).uiId("retail.0096").verticalScroll(rememberScrollState())) {
            PrizeResultHeader(state, style)
            details?.let {
                LoteriasCaixaDSBetTypeStrip(
                    items = prizeBetTypes(it).map { type ->
                        LoteriasCaixaDSBetTypeItem(
                            type.label,
                            when (type) {
                                PrizeBetType.EXCHANGE -> LoteriasCaixaDSBetTypeIcons.Exchange
                                PrizeBetType.POOL -> LoteriasCaixaDSBetTypeIcons.Pool
                                PrizeBetType.COMBO -> LoteriasCaixaDSBetTypeIcons.Combo
                                PrizeBetType.MIRROR -> LoteriasCaixaDSBetTypeIcons.Mirror
                                PrizeBetType.SURPRISE -> LoteriasCaixaDSBetTypeIcons.Surprise
                                PrizeBetType.RECURRING -> LoteriasCaixaDSBetTypeIcons.Recurring
                            }
                        )
                    },
                    backgroundColor = style.dark,
                    contentColor = style.darkContent,
                    modifier = (Modifier.padding(bottom = 2.dp)).uiId("retail.0097")
                )
            }
            Text(
                uiStrings.getString(DeliveryR.string.ui_escolha_como_resgatar_seu_premio_15382c),
                (Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("retail.0098"),
                color = LoteriasCaixaDSColorTokens.NotificationIntro,
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            state.details?.options.orEmpty().forEach { option ->
                PrizeOptionRow(option, onSelect)
            }
            state.details?.incomeTaxMessage?.takeIf(String::isNotBlank)?.let {
                Text(
                    it,
                    (Modifier.padding(24.dp)).uiId("retail.0099"),
                    color = LoteriasCaixaDS.colors.mutedText,
                    style = LoteriasCaixaDS.typography.bodySmall
                )
            }
        }
    }
}
