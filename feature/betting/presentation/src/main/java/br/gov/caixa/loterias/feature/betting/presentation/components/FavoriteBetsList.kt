package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSFavoriteBetCartIcon
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSFavoriteBetDeleteIcon
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun FavoriteBetsList(
    state: FavoriteBetsUiState,
    padding: PaddingValues,
    onFilter: () -> Unit,
    onAdd: (FavoriteBet) -> Unit,
    onDelete: (FavoriteBet) -> Unit,
    onLoadMore: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState, state.selectedModalityId, state.favorites.size, state.canLoadMore) {
        if (state.canLoadMore) {
            snapshotFlow {
                val info = listState.layoutInfo
                info.totalItemsCount > 0 && (info.visibleItemsInfo.lastOrNull()?.index ?: -1) >= info.totalItemsCount - 1
            }.distinctUntilChanged().collect { reachedEnd -> if (reachedEnd) onLoadMore() }
        }
    }
    Column(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)) {
        FavoriteListHeader(state.selectedModalityName, onFilter)
        LazyColumn(
            state = listState,
            modifier = (Modifier.weight(1f).padding(top = 6.dp))
                .uiId("bet.0101"),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            itemsIndexed(state.favorites, key = { _, favorite -> favorite.id }) { index, favorite ->
                val previous = state.favorites.getOrNull(index - 1)
                FavoriteBetItem(
                    favorite = favorite,
                    showModalityHeader = previous?.modalityId != favorite.modalityId,
                    onAdd = { onAdd(favorite) },
                    onDelete = { onDelete(favorite) }
                )
            }
            if (state.loadingMore) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CaixaDS.colors.brand)
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteBetItem(favorite: FavoriteBet, showModalityHeader: Boolean, onAdd: () -> Unit, onDelete: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val lotteryStyle = caixaDSLotteryStyle(favorite.modalityId, favorite.modalityName)
    Column(Modifier.fillMaxWidth()) {
        if (showModalityHeader) {
            FavoriteBetModalityHeader(favorite)
        }
        Text(
            favorite.name,
            (Modifier.padding(start = 20.dp, top = 10.dp)).uiId("bet.0103"),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            style = favoriteLegacyTextStyle
        )
        Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 14.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f).padding(top = 10.dp, end = 12.dp)) {
                if (favorite.isSuperSeven) {
                    FavoriteSuperSevenColumns(favorite.numberColumns, lotteryStyle)
                } else {
                    Text(
                        favorite.numbers.joinToString(" - ") { if (it == 100) "00" else it.toString().padStart(2, '0') },
                        color = CaixaDSColorTokens.RetailSearchText,
                        fontSize = 16.sp,
                        style = favoriteLegacyTextStyle,
                        modifier = Modifier.uiId("bet.0104")
                    )
                }
                if (favorite.heartTeam.isNotBlank() || favorite.luckyMonth.isNotBlank() || favorite.clovers.isNotEmpty()) {
                    Row(Modifier.padding(top = 16.dp)) {
                        val detail = when {
                            favorite.heartTeam.isNotBlank() -> uiStrings.getString(
                                LocalizedR.string.ui_time_do_coracao_1_s_7e407c,
                                favorite.heartTeam
                            )

                            favorite.luckyMonth.isNotBlank() -> uiStrings.getString(
                                LocalizedR.string.ui_mes_de_sorte_1_s_42ca39,
                                favorite.luckyMonth
                            )

                            else -> "Trevos: ${favorite.clovers.joinToString(" - ")}"
                        }
                        Text(
                            detail,
                            color = CaixaDSColorTokens.SocialGraphite,
                            fontSize = 16.sp,
                            style = favoriteLegacyTextStyle,
                            modifier = Modifier.uiId("bet.0105")
                        )
                    }
                }
            }
            Row(Modifier.padding(top = 6.dp)) {
                Box(Modifier.size(30.dp).uiId("bet.0106").clickable(onClick = onAdd), contentAlignment = Alignment.Center) {
                    Icon(
                        CaixaDSFavoriteBetCartIcon,
                        uiStrings.getString(LocalizedR.string.ui_adicionar_aposta_86ec9a),
                        (Modifier.width(26.dp).height(24.dp)).uiId("bet.0107"),
                        tint = CaixaDS.colors.brand
                    )
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(30.dp).uiId("bet.0108").clickable(onClick = onDelete), contentAlignment = Alignment.Center) {
                    Icon(
                        CaixaDSFavoriteBetDeleteIcon,
                        uiStrings.getString(LocalizedR.string.ui_excluir_aposta_86e1ac),
                        (Modifier.width(17.dp).height(24.dp)).uiId("bet.0109"),
                        tint = CaixaDS.colors.brand
                    )
                }
            }
        }
        Box(Modifier.padding(top = 8.dp).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.DrawerDivider))
    }
}

@Composable
internal fun EmptyFavoriteBets(
    modifier: Modifier,
    modalities: List<Pair<Int, String>>,
    selectedModalityId: Int?,
    onFilter: () -> Unit,
    onCreate: (Int) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier.background(CaixaDSColorTokens.White)) {
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
                color = CaixaDSColorTokens.ComboDark,
                fontSize = 16.sp,
                style = favoriteLegacyTextStyle
            )
            Box(
                Modifier.align(Alignment.CenterHorizontally).width(337.dp).height(44.dp)
                    .background(CaixaDSColorTokens.Blue700, RoundedCornerShape(8.dp)).uiId("bet.0111").clickable {
                        onCreate(selectedModalityId)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_incluir_aposta_favorita_417e30),
                    color = CaixaDSColorTokens.White,
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
                color = CaixaDSColorTokens.ComboDark,
                fontSize = 18.sp,
                style = favoriteLegacyTextStyle
            )
            Box(
                Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp
                ).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.DrawerDivider)
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

@Composable
internal fun FavoriteListHeader(selectedName: String, onFilter: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_aqui_ficam_seus_numeros_favoritos_pr_8b2b23),
            (Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 5.dp)).uiId("bet.0115"),
            fontSize = 18.sp,
            color = CaixaDSColorTokens.SocialGraphite,
            style = favoriteLegacyTextStyle
        )
        Box(
            Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp
            ).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.DrawerDivider)
        )
        Column(Modifier.fillMaxWidth().uiId("bet.0116").clickable(onClick = onFilter)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_selecione_a_modalidade_1927fb),
                (Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp)).uiId("bet.0117"),
                fontSize = 16.sp,
                color = CaixaDSColorTokens.TicketChoiceMuted,
                style = favoriteLegacyTextStyle
            )
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    selectedName,
                    (Modifier.weight(1f).padding(end = 16.dp)).uiId("bet.0118"),
                    fontSize = 18.sp,
                    color = CaixaDSColorTokens.RetailSearchText,
                    style = favoriteLegacyTextStyle
                )
                Image(
                    painterResource(R.drawable.ic_filtro_circular),
                    uiStrings.getString(LocalizedR.string.ui_selecionar_modalidade_60a513),
                    Modifier.size(25.dp)
                )
            }
        }
        Box(
            Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp
            ).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.DrawerDivider)
        )
    }
}

@Composable
private fun FavoriteBetModalityHeader(favorite: FavoriteBet) {
    val lotteryStyle = caixaDSLotteryStyle(favorite.modalityId, favorite.modalityName)
    val color = lotteryStyle.light
    Row(
        Modifier.fillMaxWidth().padding(top = 15.dp).height(55.dp).background(color),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(80.dp).height(55.dp)) {
            FavoriteTrapezoid(favorite.modalityId, Modifier.fillMaxSize())
            Image(caixaDSLotteryClover(favorite.modalityId), null, Modifier.padding(start = 18.dp, top = 10.dp).size(28.dp))
        }
        Text(
            favorite.modalityName.lowercase(),
            (Modifier.padding(horizontal = 12.dp)).uiId("bet.0102"),
            color = lotteryStyle.content,
            fontFamily = CaixaDSFuturaFontFamily,
            fontSize = 20.sp,
            style = favoriteLegacyTextStyle,
            fontWeight = FontWeight.Bold
        )
    }
}
