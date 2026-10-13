package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFavoriteBetCartIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFavoriteBetDeleteIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDivider
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyWheel
import br.gov.caixa.loterias.core.designsystem.caixaDSLegacyLotteryClover
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.betting.FavoriteBet

@Composable
fun FavoriteBetsRoute(
    viewModel: FavoriteBetsViewModel,
    onBack: () -> Unit,
    onTerms: () -> Unit,
    onCreateFavorite: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showFilter = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    CaixaDSLegacyScaffold(
        title = "Apostas favoritas",
        onBack = onBack,
        gradientTopBar = false,
        topBarTitleSize = 20.sp,
        topBarTitleWeight = FontWeight.SemiBold,
        topBarAction = {
            Image(painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_duvida2),
                "Ajuda", Modifier.size(25.dp).clickable(onClick = onTerms))
        },
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
            state.favorites.isEmpty() -> EmptyFavoriteBets(
                modifier = Modifier.fillMaxSize().padding(padding),
                modalities = state.modalities.map { it.id to it.name },
                selectedModalityId = state.selectedModalityId,
                onFilter = { showFilter.value = true },
                onCreate = onCreateFavorite,
            )
            else -> Column(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)) {
                FavoriteListHeader(state.selectedModalityName, { showFilter.value = true })
                LazyColumn(Modifier.weight(1f).padding(top = 6.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                itemsIndexed(state.favorites, key = { _, favorite -> favorite.id }) { index, favorite ->
                    val previous = state.favorites.getOrNull(index - 1)
                    FavoriteBetItem(
                        favorite = favorite,
                        showModalityHeader = previous?.modalityId != favorite.modalityId,
                        onAdd = { viewModel.requestAdd(favorite) },
                        onDelete = { viewModel.requestDelete(favorite) },
                    )
                }
                }
            }
        }
    }

    if (showFilter.value) {
        ModalityFilterDialog(
            selected = state.selectedModalityId,
            modalities = state.modalities.map { it.id to it.name },
            onDismiss = { showFilter.value = false },
            onSelect = {
                showFilter.value = false
                viewModel.selectModality(it)
            },
        )
    }
    state.deleteCandidate?.let { favorite ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Deseja excluir a aposta favorita: ${favorite.name}?",
            positiveLabel = "Sim",
            onPositive = viewModel::confirmDelete,
            negativeLabel = "Não",
            onNegative = viewModel::cancelDelete,
        )
    }
    state.addCandidate?.let {
        AddFavoriteOptionsDialog(state, viewModel)
    }
    if (state.addedToCart) {
        CaixaDSAlertDialog(
            "Apostas favoritas",
            "Aposta favorita inserida com sucesso",
            "Entendi",
            viewModel::consumeAdded,
        )
    }
    state.errorMessage?.let { CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissError) }
}

@Composable
private fun FavoriteBetItem(
    favorite: FavoriteBet,
    showModalityHeader: Boolean,
    onAdd: () -> Unit,
    onDelete: () -> Unit,
) {
    val lotteryStyle = caixaDSLotteryStyle(favorite.modalityId, favorite.modalityName)
    val color = lotteryStyle.light
    Column(Modifier.fillMaxWidth()) {
        if (showModalityHeader) {
            Row(
                Modifier.fillMaxWidth().padding(top = 15.dp).height(55.dp).background(color),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.width(80.dp).height(55.dp)) {
                    FavoriteTrapezoid(favorite.modalityId, Modifier.fillMaxSize())
                    Image(caixaDSLegacyLotteryClover(favorite.modalityId), null, Modifier.padding(start = 18.dp, top = 10.dp).size(28.dp))
                }
                Text(
                    favorite.modalityName.lowercase(),
                    Modifier.padding(horizontal = 12.dp),
                    color = lotteryStyle.content,
                    fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 20.sp, style = favoriteLegacyTextStyle,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(favorite.name, Modifier.padding(start = 20.dp, top = 10.dp), fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold, style = favoriteLegacyTextStyle)
        Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 14.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f).padding(top = 10.dp, end = 12.dp)) {
                if (favorite.isSuperSeven) {
                    FavoriteSuperSevenColumns(favorite.numberColumns, lotteryStyle)
                } else {
                    Text(favorite.numbers.joinToString(" - ") { if (it == 100) "00" else it.toString().padStart(2, '0') },
                        color = CaixaDSColorTokens.LegacyRetailSearchText, fontSize = 16.sp, style = favoriteLegacyTextStyle)
                }
                if (favorite.heartTeam.isNotBlank() || favorite.luckyMonth.isNotBlank() || favorite.clovers.isNotEmpty()) {
                    Row(Modifier.padding(top = 16.dp)) {
                        val detail = when {
                            favorite.heartTeam.isNotBlank() -> "Time do coração: ${favorite.heartTeam}"
                            favorite.luckyMonth.isNotBlank() -> "Mês de sorte: ${favorite.luckyMonth}"
                            else -> "Trevos: ${favorite.clovers.joinToString(" - ")}"
                        }
                        Text(detail, color = CaixaDSColorTokens.LegacySocialGraphite, fontSize = 16.sp, style = favoriteLegacyTextStyle)
                    }
                }
            }
            Row(Modifier.padding(top = 6.dp)) {
                Box(Modifier.size(30.dp).clickable(onClick = onAdd), contentAlignment = Alignment.Center) {
                    Icon(CaixaDSLegacyFavoriteBetCartIcon, "Adicionar aposta", Modifier.width(26.dp).height(24.dp), tint = CaixaDS.colors.brand)
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(30.dp).clickable(onClick = onDelete), contentAlignment = Alignment.Center) {
                    Icon(CaixaDSLegacyFavoriteBetDeleteIcon, "Excluir aposta", Modifier.width(17.dp).height(24.dp), tint = CaixaDS.colors.brand)
                }
            }
        }
        Box(Modifier.padding(top = 8.dp).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.LegacyDrawerDivider))
    }
}

@Composable
private fun EmptyFavoriteBets(
    modifier: Modifier,
    modalities: List<Pair<Int, String>>,
    selectedModalityId: Int?,
    onFilter: () -> Unit,
    onCreate: (Int) -> Unit,
) {
    Column(modifier.background(CaixaDSColorTokens.White)) {
        if (selectedModalityId != null) {
            FavoriteListHeader(modalities.firstOrNull { it.first == selectedModalityId }?.second ?: "Todas", onFilter)
            Text("Você ainda não tem apostas favoritas desta modalidade. Que tal adicionar uma agora?",
                Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 5.dp),
                color = CaixaDSColorTokens.LegacyComboDark, fontSize = 16.sp, style = favoriteLegacyTextStyle)
            Box(Modifier.align(Alignment.CenterHorizontally).width(337.dp).height(44.dp)
                .background(CaixaDSColorTokens.Blue700, RoundedCornerShape(8.dp)).clickable { onCreate(selectedModalityId) }, contentAlignment = Alignment.Center) {
                Text("Incluir aposta favorita", color = CaixaDSColorTokens.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, style = favoriteLegacyTextStyle)
            }
        } else {
            Text("Você ainda não tem apostas favoritas. Que tal adicionar uma agora?",
                Modifier.padding(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 5.dp),
                color = CaixaDSColorTokens.LegacyComboDark, fontSize = 18.sp, style = favoriteLegacyTextStyle)
            Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.LegacyDrawerDivider))
            Column(Modifier.verticalScroll(rememberScrollState()).padding(top = 6.dp)) {
                FavoriteModalityGrid(modalities.filterNot { it.second.equals("Loteca", true) }, null, { id -> if (id != null) onCreate(id) })
            }
        }
    }
}

@Composable
private fun FavoriteListHeader(selectedName: String, onFilter: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text("Aqui ficam seus números favoritos, prontos pra você jogar sempre que precisar.",
            Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 5.dp), fontSize = 18.sp,
            color = CaixaDSColorTokens.LegacySocialGraphite, style = favoriteLegacyTextStyle)
        Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.LegacyDrawerDivider))
        Column(Modifier.fillMaxWidth().clickable(onClick = onFilter)) {
            Text("Selecione a modalidade", Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp), fontSize = 16.sp,
                color = CaixaDSColorTokens.LegacyTicketChoiceMuted, style = favoriteLegacyTextStyle)
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(selectedName, Modifier.weight(1f).padding(end = 16.dp), fontSize = 18.sp, color = CaixaDSColorTokens.LegacyRetailSearchText, style = favoriteLegacyTextStyle)
                Image(painterResource(R.drawable.ic_filtro_circular), "Selecionar modalidade", Modifier.size(25.dp))
            }
        }
        Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.LegacyDrawerDivider))
    }
}

@Composable
private fun ModalityFilterDialog(
    selected: Int?,
    modalities: List<Pair<Int, String>>,
    onDismiss: () -> Unit,
    onSelect: (Int?) -> Unit,
) {
    var pending by remember(selected) { mutableStateOf(selected) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.fillMaxWidth(.9f).background(CaixaDSColorTokens.White, RoundedCornerShape(8.dp))) {
            Row(Modifier.fillMaxWidth().background(CaixaDSColorTokens.LegacyComboDark, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Selecione a modalidade desejada", Modifier.weight(1f), color = CaixaDSColorTokens.White, fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, style = favoriteLegacyTextStyle)
                Icon(CaixaDSIcons.Close, "Fechar", Modifier.size(25.dp).clickable(onClick = onDismiss), tint = CaixaDSColorTokens.White)
            }
            Column(Modifier.padding(10.dp).verticalScroll(rememberScrollState())) {
                FavoriteModalityGrid(listOf(null to "Todas") + modalities.filterNot { it.second.equals("Loteca", true) }, pending, { pending = it })
            }
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                Text("Cancelar", Modifier.padding(8.dp).clickable(onClick = onDismiss), color = CaixaDSColorTokens.LegacyFavoritePickerBlue, fontWeight = FontWeight.Bold,
                    style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)), fontSize = 14.sp)
                Text("Filtrar", Modifier.padding(start = 16.dp).padding(8.dp).clickable { onSelect(pending) }, color = CaixaDSColorTokens.LegacyFavoritePickerBlue, fontWeight = FontWeight.Bold,
                    style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun FavoriteModalityGrid(modalities: List<Pair<Int?, String>>, selected: Int?, onSelect: (Int?) -> Unit) {
    modalities.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth()) {
            row.forEach { (id, name) ->
                val style = caixaDSLotteryStyle(id ?: 0, name)
                Row(Modifier.weight(1f).padding(4.dp).height(62.dp)
                    .background(if (selected == id) if (id == null) CaixaDSColorTokens.LegacyComboDark else style.light else CaixaDSColorTokens.LegacyFavoriteFilterCanvas, RoundedCornerShape(8.dp))
                    .border(1.dp, if (selected == id) if (id == null) CaixaDSColorTokens.LegacyComboDark else style.light else CaixaDSColorTokens.LegacyDrawerDivider, RoundedCornerShape(8.dp))
                    .clickable { onSelect(id) }, verticalAlignment = Alignment.CenterVertically) {
                    if (id != null) Image(caixaDSLegacyLotteryClover(id), null, Modifier.padding(start = 12.dp).size(20.dp))
                    Text(if (id == null) name else name.lowercase(), Modifier.padding(start = 8.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        fontFamily = CaixaDSLegacyFuturaFontFamily, color = if (selected == id) if (id == null) CaixaDSColorTokens.White else style.content else CaixaDSColorTokens.LegacyTicketChoiceMuted,
                        maxLines = 1, style = favoriteLegacyTextStyle)
                }
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

private val favoriteLegacyTextStyle = TextStyle(fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true))

@Composable
private fun FavoriteTrapezoid(id: Int, modifier: Modifier) {
    val resource = when (id) {
        3 -> R.drawable.legacy_favorite_trapeze_quina_mkp
        7 -> R.drawable.legacy_favorite_trapeze_super_sete_mkp
        8 -> R.drawable.legacy_favorite_trapeze_lotofacil_mkp
        9 -> R.drawable.legacy_favorite_trapeze_milionaria_mkp
        11 -> R.drawable.legacy_favorite_trapeze_dia_sorte_mkp
        16 -> R.drawable.legacy_favorite_trapeze_lotomanial_mkp
        18 -> R.drawable.legacy_favorite_trapeze_dupla_sena_mkp
        19 -> R.drawable.legacy_favorite_trapeze_loteca_mkp
        20 -> R.drawable.legacy_favorite_trapeze_timemania_mkp
        else -> R.drawable.legacy_favorite_trapeze_megasena_mkp
    }
    val context = LocalContext.current
    val drawable = remember(resource, context) { requireNotNull(context.getDrawable(resource)) }
    Canvas(modifier) {
        drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
        drawIntoCanvas { drawable.draw(it.nativeCanvas) }
    }
}

@Composable
private fun AddFavoriteOptionsDialog(state: FavoriteBetsUiState, viewModel: FavoriteBetsViewModel) {
    var showContest by remember { mutableStateOf(false) }
    var showRecurring by remember { mutableStateOf(false) }
    val games = state.games.filter { it.modality.id == state.addCandidate?.modalityId }
    val contestOptions = games.distinctBy { it.contestType }.map {
        (if (it.contestType.equals("ESPECIAL", true)) 2 else 1) to
            (if (it.contestType.equals("ESPECIAL", true)) it.modality.specialName.ifBlank { it.modality.name } else it.modality.name)
    }
    val singleNormalRecurring = contestOptions.size == 1 && contestOptions.first().first == 1 && state.recurringOptions.isNotEmpty()
    var choiceOpened by remember { mutableStateOf(singleNormalRecurring) }
    Dialog(onDismissRequest = viewModel::cancelAdd) {
        Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White, RoundedCornerShape(8.dp)).padding(start = 22.dp, top = 18.dp, end = 22.dp, bottom = 22.dp)) {
            Text("Selecione as opções e clique em adicionar", Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                color = CaixaDSColorTokens.LegacyComboDark, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                textAlign = TextAlign.Center, style = favoriteLegacyTextStyle)
            if (contestOptions.isNotEmpty() && !singleNormalRecurring) {
                FavoritePickerHeading("Tipo de Concurso", {
                    choiceOpened = true
                    showContest = !showContest
                })
                if (showContest && contestOptions.isNotEmpty()) {
                    FavoriteComposePicker(contestOptions.map { it.second }, contestOptions.indexOfFirst { it.first == state.contestType }.coerceAtLeast(0),
                        Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp).height(100.dp)) { index -> viewModel.setContestType(contestOptions[index].first) }
                }
            }
            if (state.recurringOptions.isNotEmpty() && state.contestType == 1) {
                FavoritePickerHeading("Quantidade de teimosinhas", { showRecurring = !showRecurring })
                if (showRecurring) {
                    FavoriteComposePicker(state.recurringOptions.map(Int::toString), state.recurringSelectionIndex,
                        Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp).height(minOf(state.recurringOptions.size * 10, 100).dp)) {
                        viewModel.setRecurringDraws(state.recurringOptions[it])
                    }
                }
            }
            if (state.mirrorAvailable) {
                Row(Modifier.fillMaxWidth().padding(8.dp).clickable(onClick = viewModel::toggleMirror), verticalAlignment = Alignment.CenterVertically) {
                    br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCheckbox(state.mirror, viewModel::toggleMirror, Modifier.size(24.dp))
                    Text("Ao selecionar esta opção, o sistema fará a aposta automaticamente, escolhendo os 50 números que não foram registrados na sua aposta original.", Modifier.padding(start = 8.dp), fontFamily = CaixaDSLegacyFuturaFontFamily, style = favoriteLegacyTextStyle)
                }
            }
            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::cancelAdd, modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, CaixaDSColorTokens.White), colors = ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.Blue700, contentColor = CaixaDSColorTokens.White),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)) {
                    Text("Cancelar", fontWeight = FontWeight.Bold, style = favoriteLegacyTextStyle)
                }
                Button(onClick = viewModel::confirmAdd, enabled = choiceOpened, modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.LegacyTealAction, contentColor = CaixaDSColorTokens.White,
                        disabledContainerColor = CaixaDSColorTokens.LegacyPersonalDataFooter, disabledContentColor = CaixaDSColorTokens.White),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)) {
                    Text("Adicionar ao carrinho", fontWeight = FontWeight.Bold, style = favoriteLegacyTextStyle)
                }
            }
        }
    }
}

@Composable
private fun FavoritePickerHeading(label: String, onClick: () -> Unit) {
    OutlinedButton(onClick, Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp).height(50.dp),
        shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, CaixaDSColorTokens.LegacyTicketChoiceMuted), colors = ButtonDefaults.outlinedButtonColors(contentColor = CaixaDSColorTokens.LegacySocialGraphite)) {
        Text(label, fontSize = 16.sp, style = favoriteLegacyTextStyle)
    }
}

@Composable
private fun FavoriteComposePicker(options: List<String>, selected: Int, modifier: Modifier, onChange: (Int) -> Unit) {
    CaixaDSLegacyWheel(
        options = options,
        selectedIndex = selected,
        onSelectedIndex = onChange,
        modifier = modifier,
        itemHeight = 32.dp,
        fontSize = 16.sp,
        color = CaixaDSColorTokens.LegacyFavoritePickerBlue,
    )
}


@Composable
private fun FavoriteSuperSevenColumns(columns: List<List<Int>>, style: br.gov.caixa.loterias.core.designsystem.CaixaDSLotteryStyle) {
    Row {
        columns.forEachIndexed { index, numbers ->
            Column(Modifier.width(41.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.padding(top = 8.dp).size(25.dp).background(style.light, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                    Text("${index + 1}", color = style.content, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSLegacyFuturaFontFamily, style = favoriteLegacyTextStyle)
                }
                Column(Modifier.padding(top = 7.dp)) {
                    numbers.sorted().forEach { number ->
                        Box(Modifier.size(30.dp).background(CaixaDSColorTokens.White, RoundedCornerShape(15.dp)).border(1.dp, style.dark, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                            Text(number.toString(), color = style.content, fontSize = 14.sp, style = favoriteLegacyTextStyle)
                        }
                    }
                }
            }
        }
    }
}
