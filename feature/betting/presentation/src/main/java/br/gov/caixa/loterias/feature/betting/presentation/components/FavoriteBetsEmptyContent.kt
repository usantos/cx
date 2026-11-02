package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR

@Composable
internal fun EmptyFavoriteBets(
    modifier: Modifier,
    modalities: List<Pair<Int, String>>,
    selectedModalityId: Int?,
    onFilter: () -> Unit,
    onCreate: (Int) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier.background(LoteriasCaixaDSColorTokens.White)) {
        if (selectedModalityId != null) {
            FavoriteListHeader(
                modalities.firstOrNull { it.first == selectedModalityId }?.second ?: stringResource(
                    LocalizedR.string.favorite_all_modalities
                ),
                onFilter
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_tem_apostas_favoritas_2dd5d5),
                (Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 5.dp)).uiId("bet.0110"),
                color = LoteriasCaixaDSColorTokens.ComboDark,
                fontSize = 16.sp,
                style = favoriteLegacyTextStyle
            )
            Box(
                Modifier.align(Alignment.CenterHorizontally).width(337.dp).height(44.dp)
                    .background(LoteriasCaixaDSColorTokens.Blue700, RoundedCornerShape(8.dp)).uiId("bet.0111").clickable {
                        onCreate(selectedModalityId)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_incluir_aposta_favorita_417e30),
                    color = LoteriasCaixaDSColorTokens.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = favoriteLegacyTextStyle,
                    modifier = Modifier.uiId("bet.0112")
                )
            }
        } else {
            Text(
                uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_tem_apostas_favoritas_f2bacd),
                (Modifier.padding(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 5.dp)).uiId("bet.0113"),
                color = LoteriasCaixaDSColorTokens.ComboDark,
                fontSize = 18.sp,
                style = favoriteLegacyTextStyle
            )
            Box(
                Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp
                ).fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.DrawerDivider)
            )
            Column(Modifier.uiId("bet.0114").verticalScroll(rememberScrollState()).padding(top = 6.dp)) {
                FavoriteModalityGrid(modalities.filterNot { it.second.equals("Loteca", true) }, null, { id ->
                    if (id !=
                        null
                    ) {
                        onCreate(id)
                    }
                })
            }
        }
    }
}
