package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.layout.CaixaDSModalTokens
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSSemiboldFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val OPTION_DIALOG_WIDTH_FRACTION = .85f
private const val NUMBER_LIST_HEIGHT_DP = 300
private const val WHEEL_HEIGHT_DP = 170
private const val NATIVE_TEXT_SIZE_SP = 16f
private const val ENLARGED_DIALOG_HEIGHT_FRACTION = .9f

/** Versão Compose/CAIXA DS da lista de quantidades e do seletor giratório de recorrência legados. */
@Composable
fun CaixaDSOptionDialog(
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
            color = CaixaDSColorTokens.White,
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

@Composable
private fun BettingOptionRow(label: String, selected: Boolean, tag: String, onClick: () -> Unit) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        modifier = Modifier.fillMaxWidth()
            .then(if (largeFont) Modifier.heightIn(min = 52.dp).padding(vertical = 12.dp) else Modifier.height(52.dp))
            .background(
                color = if (selected) CaixaDSColorTokens.SocialCanvas else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(31.dp)
            )
            .clickable(onClick = onClick)
            .legacyAutomationId(tag)
            .semantics {
                contentDescription = label
                this.selected = selected
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            color = if (selected) CaixaDSColorTokens.RetailSearchText else CaixaDSColorTokens.TicketChoiceMuted,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = CaixaDSRegularFontFamily,
                fontSize = 15.8.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
    }
}

private fun Modifier.optionDialogWidth(): Modifier = layout { measurable, constraints ->
    // DialogUtilsTheme declara windowMinWidthMajor/Minor como 85%.
    val width = (constraints.maxWidth * OPTION_DIALOG_WIDTH_FRACTION).toInt()
        .coerceIn(0, CaixaDSModalTokens.MaxWidth.roundToPx())
    val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
}

private fun measureOptionDialogMetrics(
    context: android.content.Context,
    title: String,
    cancelLabel: String,
    confirmLabel: String,
): Triple<Int, Pair<Int, Int>, Pair<Int, Int>> {
    val themed = android.view.ContextThemeWrapper(context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
    fun measureText(font: Int): Int = android.widget.TextView(themed).apply {
        text = title
        textSize = NATIVE_TEXT_SIZE_SP
        typeface = androidx.core.content.res.ResourcesCompat.getFont(context, font)
        measure(0, 0)
    }.measuredHeight
    fun measureButton(label: String, font: Int): Pair<Int, Int> =
        android.widget.Button(themed, null, android.R.attr.buttonBarButtonStyle).apply {
            text = label
            textSize = NATIVE_TEXT_SIZE_SP
            isAllCaps = false
            typeface = androidx.core.content.res.ResourcesCompat.getFont(context, font)
            measure(0, 0)
        }.let { it.measuredWidth to it.measuredHeight }
    return Triple(
        measureText(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_bold),
        measureButton(cancelLabel, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular),
        measureButton(confirmLabel, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold)
    )
}

@Composable
private fun OptionDialogHeader(
    title: String,
    description: String?,
    subtitle: String?,
    nativeTitleHeight: Int,
    focusRequester: FocusRequester,
) {
    val density = LocalDensity.current
    val boldFamily = remember { FontFamily(Font(R.font.caixa_std_bold)) }
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = with(density) { nativeTitleHeight.toDp() })
            .focusRequester(focusRequester)
            .focusable()
            .semantics { heading() },
        color = CaixaDSColorTokens.ComboDark,
        style = TextStyle(
            fontFamily = boldFamily,
            fontSize = 16.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
    Box(
        Modifier.fillMaxWidth().padding(top = 17.dp).height(1.dp)
            .background(CaixaDSColorTokens.TutorialDivider)
    )
    description?.let {
        Text(
            text = it,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            color = CaixaDSColorTokens.SocialGraphite,
            style = TextStyle(
                fontFamily = CaixaDSRegularFontFamily,
                fontSize = 16.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
    }
    subtitle?.let {
        Text(
            text = it,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            color = CaixaDSColorTokens.SocialGraphite,
            style = TextStyle(
                fontFamily = CaixaDSRegularFontFamily,
                fontSize = 14.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
    }
    if (description == null && subtitle == null) {
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun OptionDialogItems(
    options: List<Int>,
    label: (Int) -> String,
    pending: Int,
    description: String?,
    wheelState: LazyListState,
    modifier: Modifier = Modifier,
    onChoose: (Int) -> Unit,
) {
    val height = if (description == null) NUMBER_LIST_HEIGHT_DP.dp else WHEEL_HEIGHT_DP.dp
    val largeFont = LocalDensity.current.fontScale > 1f
    LazyColumn(
        state = wheelState,
        modifier = (
            modifier.fillMaxWidth()
                .then(if (largeFont) Modifier.heightIn(max = height) else Modifier.height(height))
                .legacyAutomationId(if (description == null) "betting-option-list" else "betting-option-wheel")
            ).uiId("ds.0079"),
        contentPadding = PaddingValues(0.dp),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = wheelState)
    ) {
        itemsIndexed(options, key = { index, _ -> index }) { index, option ->
            BettingOptionRow(label(option), index == pending, "betting-option-item-$index") { onChoose(index) }
        }
    }
}

private suspend fun centerOptionRow(wheelState: LazyListState, index: Int) {
    val layout = wheelState.layoutInfo
    val item = layout.visibleItemsInfo.firstOrNull { it.index == index }
    if (item != null) {
        wheelState.animateScrollBy(
            (
                item.offset + item.size / 2 -
                    (layout.viewportStartOffset + layout.viewportEndOffset) / 2
                ).toFloat()
        )
    }
}

@Composable
private fun OptionDialogActions(
    nativeMetrics: Triple<Int, Pair<Int, Int>, Pair<Int, Int>>,
    enabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val density = LocalDensity.current
    val cancelLabel = stringResource(LocalizedR.string.ui_cancelar_bb9dbb)
    val confirmLabel = stringResource(LocalizedR.string.ui_confirmar_717bed)
    if (density.fontScale > 1f) {
        OptionDialogLargeFontActions(cancelLabel, confirmLabel, enabled, onDismiss, onConfirm)
        return
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            // Mantém as dimensões externas nativas sem o recuo extra de texto do Material.
            contentPadding = PaddingValues(0.dp),
            onClick = onDismiss,
            modifier = (
                Modifier.width(with(density) { nativeMetrics.second.first.toDp() })
                    .height(with(density) { nativeMetrics.second.second.toDp() }).legacyAutomationId("betting-option-cancel")
                ).uiId("ds.0080")
        ) {
            Text(
                cancelLabel,
                color = CaixaDSColorTokens.SocialGraphite,
                style = TextStyle(
                    fontFamily = CaixaDSRegularFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
        }
        TextButton(
            // Mantém as dimensões externas nativas sem o recuo extra de texto do Material.
            contentPadding = PaddingValues(0.dp),
            onClick = onConfirm,
            enabled = enabled,
            modifier = (
                Modifier.width(with(density) { nativeMetrics.third.first.toDp() })
                    .height(with(density) { nativeMetrics.third.second.toDp() }).legacyAutomationId("betting-option-confirm")
                ).uiId("ds.0081")
        ) {
            Text(
                confirmLabel,
                color = if (enabled) {
                    CaixaDSColorTokens.ComboDark
                } else {
                    CaixaDSColorTokens.AccountHelper.copy(alpha = .6f)
                },
                style = TextStyle(
                    fontFamily = CaixaDSSemiboldFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
        }
    }
}
