package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySemiboldFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/** Compose/CAIXA DS rendition of the legacy quantity list and recurrence wheel. */
@Composable
internal fun BettingOptionDialog(
    title: String,
    options: List<Int>,
    selected: Int,
    label: (Int) -> String,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    description: String? = null,
    subtitle: String? = null,
) {
    val latestSelect by rememberUpdatedState(onSelect)
    val latestDismiss by rememberUpdatedState(onDismiss)
    var pending by remember(options, selected, description) { mutableIntStateOf(-1) }
    var userDragging by remember { androidx.compose.runtime.mutableStateOf(false) }
    val wheelState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(title) { focusRequester.requestFocus() }

    // Layout and automatic settling never constitute a choice. Native WheelController
    // only publishes the snap after a real drag, or the exact item that was tapped.
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
    val boldFamily = remember { FontFamily(Font(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_bold)) }
    val nativeMetrics = remember(context, density, title) {
        val themed = android.view.ContextThemeWrapper(context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
        fun measureText(font: Int): Int = android.widget.TextView(themed).apply {
            text = title; textSize = 16f
            typeface = androidx.core.content.res.ResourcesCompat.getFont(context, font)
            measure(0, 0)
        }.measuredHeight
        fun measureButton(label: String, font: Int): Pair<Int, Int> = android.widget.Button(themed, null, android.R.attr.buttonBarButtonStyle).apply {
            text = label; textSize = 16f; isAllCaps = false
            typeface = androidx.core.content.res.ResourcesCompat.getFont(context, font)
            measure(0, 0)
        }.let { it.measuredWidth to it.measuredHeight }
        Triple(measureText(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_bold),
            measureButton("Cancelar", br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular),
            measureButton("Confirmar", br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold))
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(.85f),
            shape = RoundedCornerShape(8.dp),
            color = CaixaDSColorTokens.White,
            shadowElevation = 16.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp),
            ) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(min = with(density) { nativeMetrics.first.toDp() })
                        .focusRequester(focusRequester)
                        .focusable()
                        .semantics { heading() },
                    color = CaixaDSColorTokens.LegacyComboDark,
                    style = TextStyle(
                        fontFamily = boldFamily,
                        fontSize = 16.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                    ),
                )
                Box(
                    Modifier.fillMaxWidth().padding(top = 17.dp).height(1.dp)
                        .background(CaixaDSColorTokens.LegacyTutorialDivider),
                )
                description?.let {
                    Text(
                        text = it,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        color = CaixaDSColorTokens.LegacySocialGraphite,
                        style = TextStyle(
                            fontFamily = CaixaDSLegacyRegularFontFamily,
                            fontSize = 16.sp,
                            platformStyle = PlatformTextStyle(includeFontPadding = true),
                        ),
                    )
                }
                subtitle?.let {
                    Text(
                        text = it,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        color = CaixaDSColorTokens.LegacySocialGraphite,
                        style = TextStyle(
                            fontFamily = CaixaDSLegacyRegularFontFamily,
                            fontSize = 14.sp,
                            platformStyle = PlatformTextStyle(includeFontPadding = true),
                        ),
                    )
                }

                LazyColumn(
                    state = wheelState,
                    modifier = Modifier.fillMaxWidth().height(170.dp)
                        .testTag(if (description == null) "betting-option-list" else "betting-option-wheel"),
                    contentPadding = PaddingValues(0.dp),
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = wheelState),
                ) {
                    itemsIndexed(options, key = { index, _ -> index }) { index, option ->
                        BettingOptionRow(label(option), index == pending, "betting-option-item-$index") {
                            pending = index
                            coroutineScope.launch {
                                val layout = wheelState.layoutInfo
                                val item = layout.visibleItemsInfo.firstOrNull { it.index == index }
                                if (item != null) {
                                    wheelState.animateScrollBy((item.offset + item.size / 2 -
                                        (layout.viewportStartOffset + layout.viewportEndOffset) / 2).toFloat())
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = { latestDismiss() },
                        modifier = Modifier.width(with(density) { nativeMetrics.second.first.toDp() })
                            .height(with(density) { nativeMetrics.second.second.toDp() }).testTag("betting-option-cancel"),
                    ) {
                        Text(
                            "Cancelar",
                            color = CaixaDSColorTokens.LegacySocialGraphite,
                            style = TextStyle(
                                fontFamily = CaixaDSLegacyRegularFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                platformStyle = PlatformTextStyle(includeFontPadding = true),
                            ),
                        )
                    }
                    TextButton(
                        onClick = {
                            options.getOrNull(pending)?.let(latestSelect)
                            latestDismiss()
                        },
                        enabled = pending in options.indices,
                        modifier = Modifier.width(with(density) { nativeMetrics.third.first.toDp() })
                            .height(with(density) { nativeMetrics.third.second.toDp() }).testTag("betting-option-confirm"),
                    ) {
                        Text(
                            "Confirmar",
                            color = if (pending in options.indices) CaixaDSColorTokens.LegacyComboDark else androidx.compose.ui.graphics.Color(0xFFA5AAB6).copy(alpha = .6f),
                            style = TextStyle(
                                fontFamily = CaixaDSLegacySemiboldFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                platformStyle = PlatformTextStyle(includeFontPadding = true),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BettingOptionRow(
    label: String,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(52.dp)
            .background(
                color = if (selected) CaixaDSColorTokens.LegacySocialCanvas else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(31.dp),
            )
            .clickable(onClick = onClick)
            .testTag(tag)
            .semantics {
                contentDescription = label
                this.selected = selected
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            color = if (selected) CaixaDSColorTokens.LegacyRetailSearchText else CaixaDSColorTokens.LegacyTicketChoiceMuted,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = CaixaDSLegacyRegularFontFamily,
                fontSize = 15.8.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true),
            ),
        )
    }
}
