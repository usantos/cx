package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

@Composable
internal fun PrizeAgencyDataScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onLottery: () -> Unit,
    onDownload: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        uiStrings.getString(LocalizedR.string.ui_agencia_62aa47),
        onBack,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0054")) {
                Icon(
                    CaixaDSIcons.Help,
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
                    tint = CaixaDSColorTokens.White,
                    modifier = (Modifier.size(24.dp)).uiId("retail.0055")
                )
            }
        },

        containerColor = CaixaDSColorTokens.White,
        modifier = Modifier.uiId("retail.0056")
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).uiId("retail.0057").verticalScroll(rememberScrollState())) {
            if (state.details?.options.orEmpty().any { it.channel == PrizeRedemptionChannel.LOTTERY }) {
                // A ActionBar do legado sobrepunha o bloco de 70 dp e deixava somente
                // esta faixa clicável visível abaixo da toolbar.
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(CaixaDS.colors.graphite)
                        .uiId("retail.0058").clickable(onClick = onLottery)
                )
            }
            PrizeAgencyInstruction(
                number = 1,
                text = uiStrings.getString(LocalizedR.string.ui_emita_o_seu_comprovante_da_aposta_3638fb),
                modifier = (Modifier.padding(top = 39.dp)).uiId("retail.0059")
            )
            PrizeAgencyInstruction(
                number = 2,
                text = uiStrings.getString(LocalizedR.string.ui_dirija_se_a_uma_agencia_de_sua_prefe_93e205),
                modifier = (Modifier.padding(top = 35.dp)).uiId("retail.0060")
            )
            PrizeAgencyInstruction(
                number = 3,
                text = uiStrings.getString(LocalizedR.string.ui_apresente_seus_documentos_pessoais_r_83357e),
                modifier = (Modifier.padding(top = 39.dp, bottom = 16.dp)).uiId("retail.0061")
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, top = 36.dp, end = 50.dp)
                    .height(56.dp)
                    .background(CaixaDSColorTokens.White, RectangleShape)
                    .border(1.dp, CaixaDS.colors.teal, RectangleShape)
                    .uiId("retail.0062").clickable(onClick = onDownload),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_gerar_pdf_9cdd93),
                    color = CaixaDS.colors.teal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("retail.0063")
                )
            }
        }
    }
}

@Composable
private fun PrizeAgencyInstruction(number: Int, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(CaixaDSColorTokens.White, CircleShape)
                .border(1.dp, CaixaDS.colors.line, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                number.toString(),
                color = CaixaDS.colors.brand,
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("retail.0064")
            )
        }
        Text(
            text,
            modifier = (Modifier.weight(1f).padding(start = 8.dp)).uiId("retail.0065"),
            color = CaixaDS.colors.graphite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
