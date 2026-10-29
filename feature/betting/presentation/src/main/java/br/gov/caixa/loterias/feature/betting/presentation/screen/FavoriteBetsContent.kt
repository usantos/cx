package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.presentation.components.EmptyFavoriteBets
import br.gov.caixa.loterias.feature.betting.presentation.components.FavoriteBetsList
import br.gov.caixa.loterias.feature.betting.presentation.components.FavoriteListHeader
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState

@Composable
internal fun FavoriteBetsContent(
    state: FavoriteBetsUiState,
    onBack: () -> Unit,
    onTerms: () -> Unit,
    onCreateFavorite: (Int) -> Unit,
    privateDataEnabled: Boolean,
    onFilter: () -> Unit,
    onAdd: (FavoriteBet) -> Unit,
    onDelete: (FavoriteBet) -> Unit,
    onLoadMore: () -> Unit = {},
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_apostas_favoritas_f81885),
        onBack = onBack,
        gradientTopBar = false,

        topBarAction = {
            Image(
                painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_duvida2),
                uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
                Modifier.size(25.dp).uiId("bet.0277").clickable(onClick = onTerms)
            )
        },
        modifier = Modifier.uiId("bet.0278")
    ) { padding ->
        when {
            !privateDataEnabled -> Column(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)) {
                FavoriteListHeader(state.selectedModalityName) {}
            }

            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }

            state.favorites.isEmpty() -> EmptyFavoriteBets(
                modifier = (Modifier.fillMaxSize().padding(padding)).uiId("bet.0279"),
                modalities = state.modalities.map { it.id to it.name },
                selectedModalityId = state.selectedModalityId,
                onFilter = onFilter,
                onCreate = onCreateFavorite
            )

            else -> FavoriteBetsList(state, padding, onFilter, onAdd, onDelete, onLoadMore)
        }
    }
}
