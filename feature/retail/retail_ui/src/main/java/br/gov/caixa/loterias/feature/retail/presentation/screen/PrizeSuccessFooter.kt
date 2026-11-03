package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.components.LegacyRedemptionActionButton
import br.gov.caixa.loterias.feature.retail.ui.R as DeliveryR

@Composable
internal fun PrizeSuccessFooter(onHome: () -> Unit, onMyBets: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxWidth().height(208.dp)) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(176.dp)
                .background(LoteriasCaixaDSColorTokens.MercadoPagoFooter)
        )
        LegacyRedemptionActionButton(
            text = uiStrings.getString(LocalizedR.string.ui_voltar_ao_inicio_372787),
            onClick = onHome,
            modifier = (Modifier.align(Alignment.TopCenter)).uiId("retail.0121"),
            color = LoteriasCaixaDS.colors.teal
        )
        LegacyRedemptionActionButton(
            text = uiStrings.getString(DeliveryR.string.ui_resgatar_novo_premio_4ed587),
            onClick = onMyBets,
            modifier = (Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)).uiId("retail.0122"),
            color = LoteriasCaixaDS.colors.teal
        )
    }
}
