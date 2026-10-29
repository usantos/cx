package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

@Composable
fun TicketGameChoiceRoute(viewModel: TicketsViewModel, onBack: () -> Unit, onHelp: () -> Unit, onSave: (Set<Int>) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.loadTicketModalities() }

    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_conferir_aposta_c2b3fa),
        onBack = onBack,
        topBarAction = {
            Icon(
                CaixaDSIcons.Help,
                contentDescription = uiStrings.getString(LocalizedR.string.ui_termos_de_uso_e259be),
                modifier = (Modifier.fillMaxSize().uiId("tickets.0141").clickable(onClick = onHelp).padding(12.dp)).uiId("tickets.0142"),
                tint = CaixaDSColorTokens.White
            )
        },
        bottomBar = {
            TicketChoiceSave(state.selectedTicketModalityIds, onSave)
        },
        modifier = Modifier.uiId("tickets.0145")
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.teal)
            }

            else -> Column(Modifier.fillMaxSize().padding(padding)) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_conferir_aposta_c2b3fa),
                    (Modifier.fillMaxWidth().padding(top = 40.dp)).uiId("tickets.0146"),
                    color = CaixaDS.colors.teal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_selecione_a_aposta_realizada_a33b3d),
                    (Modifier.width(130.dp).padding(top = 24.dp).align(Alignment.CenterHorizontally)).uiId("tickets.0147"),
                    color = CaixaDS.colors.graphite,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(top = 19.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(17.dp)
                ) {
                    items(state.ticketModalities, key = LotteryModality::id) { modality ->
                        TicketModalityChoice(
                            modality = modality,
                            selected = modality.id in state.selectedTicketModalityIds,
                            onClick = { viewModel.toggleTicketModality(modality.id) }
                        )
                    }
                }
            }
        }
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = it,
            confirmText = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onConfirm = {
                viewModel.dismissTicketError()
                viewModel.loadTicketModalities()
            }
        )
    }
}

@Composable
private fun TicketModalityChoice(modality: LotteryModality, selected: Boolean, onClick: () -> Unit) {
    val color = ticketModalityColor(modality.code)
    Row(
        Modifier.fillMaxWidth().padding(4.dp).height(56.dp)
            .background(if (selected) color else CaixaDS.colors.surface)
            .then(if (selected) Modifier else Modifier.border(1.dp, CaixaDSColorTokens.TicketChoiceBorder))
            .uiId("tickets.0148").clickable(onClick = onClick)
            .padding(horizontal = if (selected) 12.dp else 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            caixaDSDrawablePainter(
                caixaDSCampaignDrawable(
                    modality.id,
                    modality.name,
                    if (selected) CaixaDSLotteryArtwork.CLOVER_ON_DARK else CaixaDSLotteryArtwork.CLOVER_ON_LIGHT
                )
                    ?: R.drawable.legacy_ticket_trevo
            ),
            contentDescription = null,
            modifier = (Modifier.size(18.dp)).uiId("tickets.0149"),
            tint = Color.Unspecified
        )
        Text(
            modality.name,
            (Modifier.padding(start = 8.dp)).uiId("tickets.0150"),
            color = if (selected) ticketModalityContent(modality) else CaixaDSColorTokens.TicketChoiceMuted,
            fontFamily = CaixaDSFuturaFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun ticketModalityColor(code: String): Color {
    val style = caixaDSLotteryStyle(0, code)
    if (style != resolveCaixaDSLotteryStyle(0, code, CaixaDSCampaign.DEFAULT)) return style.light
    return when (code.uppercase().filter(Char::isLetterOrDigit)) {
        "MEGASENA" -> CaixaDSColorTokens.MegaSenaLight
        "LOTOFACIL" -> CaixaDSColorTokens.LotofacilLight
        "QUINA" -> CaixaDSColorTokens.QuinaLight
        "LOTOMANIA" -> CaixaDSColorTokens.LotomaniaLight
        "TIMEMANIA" -> CaixaDSColorTokens.TimemaniaLight
        "DUPLASENA" -> CaixaDSColorTokens.DuplaSenaLight
        "LOTECA" -> CaixaDSColorTokens.Loteca
        "LOTOGOL" -> CaixaDSColorTokens.Lotogol
        else -> CaixaDS.colors.brand
    }
}

@Composable
private fun ticketModalityContent(modality: LotteryModality): Color {
    val style = caixaDSLotteryStyle(modality.id, modality.name)
    return if (style != resolveCaixaDSLotteryStyle(modality.id, modality.name, CaixaDSCampaign.DEFAULT)) {
        style.content
    } else {
        CaixaDSColorTokens.White
    }
}

@Composable
private fun TicketChoiceSave(selectedTicketModalityIds: Set<Int>, onSave: (Set<Int>) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.fillMaxWidth().height(135.dp).background(CaixaDS.colors.surface),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            Modifier.fillMaxWidth().height(95.dp).align(Alignment.BottomCenter)
                .background(CaixaDS.colors.line)
        )
        Button(
            onClick = { onSave(selectedTicketModalityIds) },
            enabled = selectedTicketModalityIds.isNotEmpty(),
            modifier = (Modifier.width(184.dp).height(84.dp)).uiId("tickets.0143"),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CaixaDS.colors.teal,
                contentColor = CaixaDSColorTokens.White,
                disabledContainerColor = CaixaDS.colors.disabledButton,
                disabledContentColor = CaixaDSColorTokens.White
            )
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_salvar_aef7cd),
                fontSize = 17.7.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("tickets.0144")
            )
        }
    }
}
