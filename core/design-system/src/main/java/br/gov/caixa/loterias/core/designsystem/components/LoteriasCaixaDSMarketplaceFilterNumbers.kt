package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId

/** O GridLayoutManager nativo distribui seis colunas, mas mantém cada filho de 49dp no início da sua coluna. */
@Composable
fun LoteriasCaixaDSMarketplaceFilterBetGrid(options: List<Int>, selected: Set<Int>, modifier: Modifier = Modifier, onClick: (Int) -> Unit) {
    val fontScale = LocalDensity.current.fontScale
    if (fontScale > 1f) {
        val cellWidth = 45.dp * fontScale + 4.dp
        FlowRow(modifier.fillMaxWidth()) {
            options.forEach { value ->
                Box(Modifier.width(cellWidth).padding(2.dp)) {
                    LoteriasCaixaDSMarketplaceFilterNumberCell(value, value in selected, 45.dp, 45.dp) { onClick(value) }
                }
            }
        }
        return
    }
    val rows = ((options.size + GRID_LAST_COLUMN_OFFSET) / GRID_COLUMN_COUNT).coerceAtLeast(1)
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMN_COUNT),
        modifier = modifier.fillMaxWidth().height((rows * BET_GRID_ROW_HEIGHT_DP).dp),
        userScrollEnabled = false
    ) {
        items(options) { value ->
            Box(Modifier.fillMaxWidth().height(53.dp), contentAlignment = Alignment.TopStart) {
                Box(Modifier.width(49.dp).height(53.dp).padding(horizontal = 2.dp, vertical = 4.dp)) {
                    LoteriasCaixaDSMarketplaceFilterNumberCell(value, value in selected, 45.dp, 45.dp) { onClick(value) }
                }
            }
        }
    }
}

/** O mesmo vetor de 41 unidades é escalado pelo ToggleButton nativo para apostas de 45dp e diálogos de 29dp. */
@Composable
fun LoteriasCaixaDSMarketplaceFilterNumberCell(
    value: Int,
    selected: Boolean,
    diameter: Dp = 29.dp,
    rowHeight: Dp = 45.dp,
    onClick: () -> Unit,
) {
    val fontScale = LocalDensity.current.fontScale
    val effectiveDiameter = if (fontScale > 1f) diameter * fontScale else diameter
    val effectiveRowHeight = if (fontScale > 1f) maxOf(rowHeight, effectiveDiameter + 8.dp) else rowHeight
    Box(Modifier.fillMaxWidth().height(effectiveRowHeight), contentAlignment = Alignment.Center) {
        Box(
            Modifier.size(effectiveDiameter).uiId("ds.0030").toggleable(value = selected, role = Role.Checkbox, onValueChange = {
                onClick()
            }),
            contentAlignment = Alignment.Center
        ) {
            MarketplaceFilterNumberBall(value, selected)
        }
    }
}

/** item_dezena_quero_nao_quero e seu contêiner nativo de seleção com recuo de 20dp. */
@Composable
fun LoteriasCaixaDSMarketplaceFilterSelection(values: List<Int>, clearLabel: String, onRemove: (Int) -> Unit, onClear: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val rows = ((values.size + GRID_LAST_COLUMN_OFFSET) / GRID_COLUMN_COUNT).coerceAtLeast(1)
    Column(Modifier.fillMaxWidth().padding(top = 10.dp, end = 20.dp)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMN_COUNT),
            modifier = Modifier.fillMaxWidth().height((rows * SELECTED_GRID_ROW_HEIGHT_DP).dp),
            userScrollEnabled = false
        ) {
            items(values) { value ->
                Box(Modifier.fillMaxWidth().height(45.dp), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier.size(29.dp).uiId("ds.0034").clickable(role = Role.Button) { onRemove(value) }.semantics {
                            contentDescription =
                                uiStrings.getString(LocalizedR.string.ui_remover_1_s_f2ff9f, value.toString().padStart(2, '0'))
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        MarketplaceFilterNumberBall(value, selected = true)
                        Image(
                            painterResource(R.drawable.legacy_marketplace_filter_remove),
                            null,
                            Modifier.align(Alignment.TopEnd).offset(x = 7.dp, y = (-4).dp).size(16.dp)
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
            LoteriasCaixaDSMarketplaceFilterButton(
                text = clearLabel.uppercase(java.util.Locale.forLanguageTag("pt-BR")),
                outlined = true,
                onClick = onClear,
                modifier = (Modifier.width(127.dp)).uiId("ds.0035"),
                height = 34.dp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/** Desenha apenas o visual compartilhado; cada chamador mantém seu tamanho, tratamento de cliques e sobreposições. */
@Composable
private fun MarketplaceFilterNumberBall(value: Int, selected: Boolean) {
    Image(
        painterResource(if (selected) R.drawable.legacy_marketplace_filter_bet_selected else R.drawable.legacy_marketplace_filter_bet),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
    Text(
        value.toString().padStart(2, '0'),
        modifier = if (LocalDensity.current.fontScale > 1f) Modifier.fillMaxWidth() else Modifier,
        textAlign = if (LocalDensity.current.fontScale > 1f) TextAlign.Center else null,
        color = if (selected) Color.White else LoteriasCaixaDSMarketplaceFilterStyle.betText,
        style = loteriasCaixaDSMarketplaceFilterText(NUMBER_LABEL_TEXT_SIZE, FontWeight.Bold)
    )
}

private const val GRID_COLUMN_COUNT = 6
private const val GRID_LAST_COLUMN_OFFSET = GRID_COLUMN_COUNT - 1
private const val BET_GRID_ROW_HEIGHT_DP = 53
private const val SELECTED_GRID_ROW_HEIGHT_DP = 45
private const val NUMBER_LABEL_TEXT_SIZE = 14
