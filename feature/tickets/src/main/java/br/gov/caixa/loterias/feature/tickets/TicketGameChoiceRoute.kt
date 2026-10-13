package br.gov.caixa.loterias.feature.tickets

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import androidx.compose.ui.res.painterResource
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.domain.betting.LotteryModality

@Composable
fun TicketGameChoiceRoute(
    viewModel: TicketsViewModel,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onSave: (Set<Int>) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.loadTicketModalities() }

    CaixaDSLegacyScaffold(
        title = "Conferir aposta",
        onBack = onBack,
        topBarAction = {
            Icon(
                CaixaDSIcons.Help,
                contentDescription = "Termos de Uso",
                modifier = Modifier.fillMaxSize().clickable(onClick = onHelp).padding(12.dp),
                tint = CaixaDSColorTokens.White,
            )
        },
        bottomBar = {
            Box(
                Modifier.fillMaxWidth().height(135.dp).background(CaixaDS.colors.surface),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    Modifier.fillMaxWidth().height(95.dp).align(Alignment.BottomCenter)
                        .background(CaixaDS.colors.legacyLine),
                )
                Button(
                    onClick = { onSave(state.selectedTicketModalityIds) },
                    enabled = state.selectedTicketModalityIds.isNotEmpty(),
                    modifier = Modifier.width(184.dp).height(84.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CaixaDS.colors.legacyTeal,
                        contentColor = CaixaDSColorTokens.White,
                        disabledContainerColor = CaixaDS.colors.legacyDisabledButton,
                        disabledContentColor = CaixaDSColorTokens.White,
                    ),
                ) { Text("Salvar", fontSize = 17.7.sp, fontWeight = FontWeight.Bold) }
            }
        },
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.legacyTeal)
            }
            else -> Column(Modifier.fillMaxSize().padding(padding)) {
                Text(
                    "Conferir aposta",
                    Modifier.fillMaxWidth().padding(top = 40.dp),
                    color = CaixaDS.colors.legacyTeal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    "Selecione a aposta realizada",
                    Modifier.width(130.dp).padding(top = 24.dp).align(Alignment.CenterHorizontally),
                    color = CaixaDS.colors.legacyGraphite,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(top = 19.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(17.dp),
                ) {
                    items(state.ticketModalities, key = LotteryModality::id) { modality ->
                        TicketModalityChoice(
                            modality = modality,
                            selected = modality.id in state.selectedTicketModalityIds,
                            onClick = { viewModel.toggleTicketModality(modality.id) },
                        )
                    }
                }
            }
        }
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            title = "Atenção",
            message = it,
            confirmText = "Tentar novamente",
            onConfirm = {
                viewModel.dismissTicketError()
                viewModel.loadTicketModalities()
            },
        )
    }
}

@Composable
private fun TicketModalityChoice(modality: LotteryModality, selected: Boolean, onClick: () -> Unit) {
    val color = ticketModalityColor(modality.code)
    Row(
        Modifier.fillMaxWidth().padding(4.dp).height(56.dp)
            .background(if (selected) color else CaixaDS.colors.surface)
            .then(if (selected) Modifier else Modifier.border(1.dp, CaixaDSColorTokens.LegacyTicketChoiceBorder))
            .clickable(onClick = onClick)
            .padding(horizontal = if (selected) 12.dp else 22.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(R.drawable.legacy_ticket_trevo),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Unspecified,
        )
        Text(
            modality.name,
            Modifier.padding(start = 8.dp),
            color = if (selected) CaixaDSColorTokens.White else CaixaDSColorTokens.LegacyTicketChoiceMuted,
            fontFamily = CaixaDSLegacyFuturaFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ticketModalityColor(code: String): Color = when (code.uppercase().filter(Char::isLetterOrDigit)) {
    "MEGASENA" -> CaixaDSColorTokens.LegacyMegaSenaLight
    "LOTOFACIL" -> CaixaDSColorTokens.LegacyLotofacilLight
    "QUINA" -> CaixaDSColorTokens.LegacyQuinaLight
    "LOTOMANIA" -> CaixaDSColorTokens.LegacyLotomaniaLight
    "TIMEMANIA" -> CaixaDSColorTokens.LegacyTimemaniaLight
    "DUPLASENA" -> CaixaDSColorTokens.LegacyDuplaSenaLight
    "LOTECA" -> CaixaDSColorTokens.Loteca
    "LOTOGOL" -> CaixaDSColorTokens.Lotogol
    else -> CaixaDS.colors.brand
}
