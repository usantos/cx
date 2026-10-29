package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSResources
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterButton as ActionButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterNumberCell as NumberCell
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
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
            modifier = Modifier.fillMaxSize().background(CaixaDSMarketplaceFilterStyle.canvas)
                .statusBarsPadding().navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(39.dp)
                    .background(CaixaDSMarketplaceFilterStyle.accent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    modifier = (Modifier.padding(start = 24.dp).weight(1f)).uiId("filter.0024"),
                    color = CaixaDS.colors.onBrand,
                    style = CaixaDSMarketplaceFilterStyle.dialogHeading
                )
                Row(
                    modifier = Modifier.fillMaxHeight().uiId("filter.0025").clickable(onClick = onDismiss).padding(end = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(CaixaDSResources.drawable.legacy_marketplace_filter_close),
                        contentDescription = null,
                        tint = CaixaDS.colors.onBrand,
                        modifier = (Modifier.size(10.dp)).uiId("filter.0026")
                    )
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
                        modifier = (Modifier.padding(start = 7.dp)).uiId("filter.0027"),
                        color = CaixaDS.colors.onBrand,
                        style = CaixaDSMarketplaceFilterStyle.dialogHeading
                    )
                }
            }
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
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_nao_e_possivel_selecionar_um_numero_628dac),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { conflictVisible = false }
        )
    }
}

@Composable
private fun LegacyDialogButton(text: String, outlined: Boolean, onClick: () -> Unit, modifier: Modifier) {
    ActionButton(
        text = text,
        outlined = outlined,
        onClick = onClick,
        modifier = (modifier).uiId("filter.0028"),
        height = 31.dp,
        textSize = 12,
        lineHeight = 16.2.sp,
        contentColor = if (outlined) CaixaDSMarketplaceFilterStyle.heading else CaixaDSColorTokens.White
    )
}

@Composable
private fun NumberDialogActions(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 16.dp)) {
        LegacyDialogButton(
            stringResource(LocalizedR.string.format_cancelar),
            true,
            onDismiss,
            (Modifier.weight(1f).padding(end = 5.dp)).uiId("filter.0029")
        )
        LegacyDialogButton(
            stringResource(LocalizedR.string.format_confirmar),
            false,
            onConfirm,
            (Modifier.weight(1f).padding(start = 5.dp)).uiId("filter.0030")
        )
    }
}
