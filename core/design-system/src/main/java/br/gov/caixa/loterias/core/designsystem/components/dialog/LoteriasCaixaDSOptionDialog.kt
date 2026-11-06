package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val ENLARGED_DIALOG_HEIGHT_FRACTION = .9f

/** Versão Compose/CAIXA DS da lista de quantidades e do seletor giratório de recorrência legados. */
@Composable
fun LoteriasCaixaDSOptionDialog(
    title: String,
    options: List<Int>,
    selected: Int,
    label: (Int) -> String,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    description: String? = null,
    subtitle: String? = null,
) {
    var pending by remember(options, selected, description) { mutableIntStateOf(-1) }
    var userDragging by remember { androidx.compose.runtime.mutableStateOf(false) }
    val wheelState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(title) { focusRequester.requestFocus() }

    // Layout e encaixe automático nunca representam uma escolha. O WheelController nativo
    // só publica o encaixe após um arrasto real, ou o item exato que recebeu o toque.
    LaunchedEffect(wheelState) {
        wheelState.interactionSource.interactions.collect { interaction ->
            if (interaction is DragInteraction.Start) userDragging = true
        }
    }
    LaunchedEffect(wheelState, options) {
        snapshotFlow { wheelState.isScrollInProgress }.distinctUntilChanged().collect { scrolling ->
            if (!scrolling && userDragging) {
                val layout = wheelState.layoutInfo
                val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
                layout.visibleItemsInfo.minByOrNull { kotlin.math.abs(it.offset + it.size / 2 - center) }
                    ?.index?.takeIf { it in options.indices }?.let { pending = it }
                userDragging = false
            }
        }
    }
    val context = LocalContext.current
    val density = LocalDensity.current
    val largeFont = density.fontScale > 1f
    val windowHeight = LocalWindowInfo.current.containerSize.height
    val enlargedMaxHeight = with(LocalDensity.current) { windowHeight.toDp() } * ENLARGED_DIALOG_HEIGHT_FRACTION
    val cancelLabel = stringResource(LocalizedR.string.ui_cancelar_bb9dbb)
    val confirmLabel = stringResource(LocalizedR.string.ui_confirmar_717bed)
    val nativeMetrics = remember(context, density, title, cancelLabel, confirmLabel) {
        measureOptionDialogMetrics(context, title, cancelLabel, confirmLabel)
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.optionDialogWidth(),
            shape = RoundedCornerShape(8.dp),
            color = LoteriasCaixaDSColorTokens.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .then(if (largeFont) Modifier.heightIn(max = enlargedMaxHeight) else Modifier)
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp)
            ) {
                OptionDialogHeader(title, description, subtitle, nativeMetrics.first, focusRequester)

                OptionDialogItems(
                    options,
                    label,
                    pending,
                    description,
                    wheelState,
                    modifier = if (largeFont) Modifier.weight(1f, fill = false) else Modifier
                ) { index ->
                    pending = index
                    coroutineScope.launch { centerOptionRow(wheelState, index) }
                }

                OptionDialogActions(nativeMetrics, pending in options.indices, onDismiss) {
                    options.getOrNull(pending)?.let(onSelect)
                    onDismiss()
                }
            }
        }
    }
}
