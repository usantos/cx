package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary

@Composable
internal fun PrizePixKeyConfirmation(beneficiary: PixBeneficiary) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).padding(top = 8.dp)) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_confirmar_a_chave_pix_para_o_resgate_52b273),
            (Modifier.padding(start = 16.dp, top = 24.dp, end = 24.dp, bottom = 24.dp)).uiId("retail.0012"),
            color = CaixaDS.colors.brand,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_dados_do_titular_da_chave_724785),
            (Modifier.padding(horizontal = 24.dp)).uiId("retail.0013"),
            color = CaixaDS.colors.brand,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            beneficiary.name,
            (Modifier.padding(start = 24.dp, top = 16.dp)).uiId("retail.0014"),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            beneficiary.maskedCpf,
            (Modifier.padding(start = 24.dp, top = 4.dp)).uiId("retail.0015"),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_chave_1_s_14ef7a, beneficiary.key),
            (Modifier.padding(start = 24.dp, top = 4.dp)).uiId("retail.0016"),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
