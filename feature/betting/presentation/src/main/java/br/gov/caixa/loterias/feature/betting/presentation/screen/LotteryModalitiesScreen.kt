package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LoteriasCaixaDSR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSContentState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSDivider
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.icon.loteriasCaixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import br.gov.caixa.loterias.feature.betting.presentation.style.modalityColor

@Composable
internal fun LotteryModalitiesScreen(state: BettingCatalogUiState, onBack: () -> Unit, onSelect: (Int) -> Unit, onRetry: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_modalidades_6b4baa),
        onBack = onBack,
        modifier = Modifier
            .uiId("bet.0280")
    ) { padding ->
        LoteriasCaixaDSContentState(state.loading, state.errorMessage, padding, onRetry) {
            LazyColumn((Modifier.fillMaxSize().padding(padding)).uiId("bet.0281")) {
                item {
                    LoteriasCaixaDSSectionHeader(
                        uiStrings.getString(LocalizedR.string.ui_escolha_uma_modalidade_3ef3c2),
                        subtitle = uiStrings.getString(LocalizedR.string.ui_consulte_o_proximo_concurso_e_faca_s_348c0a),
                        modifier = (Modifier.padding(16.dp)).uiId("bet.0282")
                    )
                }
                items(state.modalities, key = LotteryModality::id) { modality ->
                    val game = state.parameters?.games?.firstOrNull { it.modality.id == modality.id }
                    Row(
                        Modifier.fillMaxWidth().uiId("bet.0283").clickable(enabled = game?.open != false) { onSelect(modality.id) }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (modality.id == 14) {
                            Image(painterResource(LoteriasCaixaDSR.drawable.legacy_trevo_lotogol), null, Modifier.size(44.dp))
                        } else {
                            Image(
                                loteriasCaixaDSLotteryClover(modality.id, onDark = false),
                                contentDescription = null,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                            Text(
                                modality.name,
                                color = modalityColor(modality.id),
                                style = LoteriasCaixaDS.typography.titleMedium,
                                modifier = Modifier.uiId("bet.0284")
                            )
                            Text(
                                if (game == null) {
                                    uiStrings.getString(
                                        LocalizedR.string.ui_consulte_a_disponibilidade_0149a3
                                    )
                                } else {
                                    uiStrings.getString(
                                        LocalizedR.string.ui_concurso_1_s_2_s_144dfc,
                                        game.contestNumber.toString(),
                                        game.drawDate
                                    )
                                },
                                color = LoteriasCaixaDS.colors.mutedText,
                                style = LoteriasCaixaDS.typography.bodySmall,
                                modifier = Modifier.uiId("bet.0285")
                            )
                        }
                        Icon(
                            LoteriasCaixaDSIcons.Forward,
                            contentDescription = null,
                            modifier = (Modifier.size(20.dp)).uiId("bet.0286"),
                            tint = LoteriasCaixaDS.colors.brand
                        )
                    }
                    LoteriasCaixaDSDivider()
                }
            }
        }
    }
}
