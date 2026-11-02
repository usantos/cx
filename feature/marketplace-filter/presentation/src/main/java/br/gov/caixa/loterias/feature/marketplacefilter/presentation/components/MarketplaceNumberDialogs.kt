package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterNumberCell as NumberCell
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterStyle
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.marketplacefilter.R as LocalizedR

internal enum class NumberDialog { Quantity, Wanted, Unwanted }

/** Modal de tela cheia equivalente à antiga `QueroNaoQueroActivity`. */
@Composable
internal fun LegacyNumberSelectionDialog(
    title: String,
    options: List<Int>,
    selected: List<Int>,
    unavailable: Set<Int>,
    singleSelection: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (List<Int>) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale
    var current by remember(title, selected) { mutableStateOf(selected) }
    var conflictVisible by remember(title) { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(LoteriasCaixaDSMarketplaceFilterStyle.canvas)
                .statusBarsPadding().navigationBarsPadding()
        ) {
            NumberDialogHeader(title, onDismiss)
            LazyVerticalGrid(
                columns = if (fontScale > 1f) GridCells.Adaptive(29.dp * fontScale + 8.dp) else GridCells.Fixed(6),
                modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 10.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(options) { value ->
                    NumberCell(value, value in current) {
                        if (value in unavailable) {
                            conflictVisible = true
                        } else {
                            current = if (singleSelection) {
                                listOf(value)
                            } else if (value in current) {
                                current - value
                            } else {
                                current + value
                            }
                        }
                    }
                }
            }
            NumberDialogActions(onDismiss) { onConfirm(current.sorted()) }
        }
    }
    if (conflictVisible) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_nao_e_possivel_selecionar_um_numero_628dac),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { conflictVisible = false }
        )
    }
}
