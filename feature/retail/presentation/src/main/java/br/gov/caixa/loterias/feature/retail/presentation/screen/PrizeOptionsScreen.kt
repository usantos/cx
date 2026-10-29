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
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeItem
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeStrip
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseHelpIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeOptionRow
import br.gov.caixa.loterias.feature.retail.presentation.mapper.PrizeBetType
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeBetTypes
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

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
    val style = caixaDSLotteryStyle(details?.modalityId ?: 2, details?.modalityName.orEmpty())
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resultado_da_aposta_4e6334),
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0093")) {
                CaixaDSPurchaseHelpIcon((Modifier.size(24.dp)).uiId("retail.0094"))
            }
        },

        containerColor = CaixaDSColorTokens.White,
        modifier = Modifier.uiId("retail.0095")
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).uiId("retail.0096").verticalScroll(rememberScrollState())) {
            PrizeResultHeader(state, style)
            details?.let {
                CaixaDSBetTypeStrip(
                    items = prizeBetTypes(it).map { type ->
                        CaixaDSBetTypeItem(
                            type.label,
                            when (type) {
                                PrizeBetType.EXCHANGE -> CaixaDSBetTypeIcons.Exchange
                                PrizeBetType.POOL -> CaixaDSBetTypeIcons.Pool
                                PrizeBetType.COMBO -> CaixaDSBetTypeIcons.Combo
                                PrizeBetType.MIRROR -> CaixaDSBetTypeIcons.Mirror
                                PrizeBetType.SURPRISE -> CaixaDSBetTypeIcons.Surprise
                                PrizeBetType.RECURRING -> CaixaDSBetTypeIcons.Recurring
                            }
                        )
                    },
                    backgroundColor = style.dark,
                    contentColor = style.darkContent,
                    modifier = (Modifier.padding(bottom = 2.dp)).uiId("retail.0097")
                )
            }
            Text(
                uiStrings.getString(LocalizedR.string.ui_escolha_como_resgatar_seu_premio_15382c),
                (Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("retail.0098"),
                color = CaixaDSColorTokens.NotificationIntro,
                fontFamily = CaixaDSFuturaFontFamily,
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
                    color = CaixaDS.colors.mutedText,
                    style = CaixaDS.typography.bodySmall
                )
            }
        }
    }
}
