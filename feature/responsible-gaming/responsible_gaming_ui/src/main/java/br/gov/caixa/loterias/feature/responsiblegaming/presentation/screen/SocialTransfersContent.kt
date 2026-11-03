package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.currentSocialYear
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.formatCurrency
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.socialTransferredAmount
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import br.gov.caixa.loterias.feature.responsiblegaming.ui.R as DeliveryR
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun SocialTransfersContent(
    state: ResponsibleGamingUiState,
    padding: androidx.compose.foundation.layout.PaddingValues,
    onBack: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val transfers = state.socialTransfers
    var transferredAmount by remember(transfers) { mutableStateOf(socialTransferredAmount(transfers)) }
    LaunchedEffect(transfers) {
        while (isActive) {
            transferredAmount = socialTransferredAmount(transfers)
            delay(SOCIAL_TRANSFER_REFRESH_INTERVAL_MILLIS)
        }
    }
    Column(
        Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)
            .uiId("rg.0050").verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(listOf(LoteriasCaixaDS.colors.headerStart, LoteriasCaixaDS.colors.headerEnd))
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_em_1_s_as_loterias_caixa_ja_destinar_fce192, currentSocialYear().toString()),
                modifier = (Modifier.padding(top = 40.dp)).uiId("rg.0051"),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                formatCurrency(transferredAmount),
                (Modifier.padding(top = 15.dp)).uiId("rg.0052"),
                color = LoteriasCaixaDSColorTokens.SocialLime,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                uiStrings.getString(DeliveryR.string.ui_para_melhorar_o_pais_85a746),
                (Modifier.padding(top = 18.dp, bottom = 40.dp)).uiId("rg.0053"),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        SocialTransfersAllocationIntro()
        SocialTransfersDistribution(transfers)
        SocialTransfersFooter(onBack)
    }
}

private const val SOCIAL_TRANSFER_REFRESH_INTERVAL_MILLIS = 1_000L
