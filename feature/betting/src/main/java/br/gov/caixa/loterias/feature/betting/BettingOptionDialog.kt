package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
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
    val initialPending = if (description == null) options.indexOf(selected).coerceAtLeast(0) else 0
    var pending by remember(options, selected, description) { mutableIntStateOf(initialPending) }
    val wheelState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(title) { focusRequester.requestFocus() }

    if (description != null) {
        LaunchedEffect(wheelState, options) {
            snapshotFlow {
                val layout = wheelState.layoutInfo
                val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
                val centeredIndex = layout.visibleItemsInfo.minByOrNull { item ->
                    kotlin.math.abs((item.offset + item.size / 2) - center)
                }?.index
                centeredIndex to wheelState.isScrollInProgress
            }.distinctUntilChanged().collect { (centeredIndex, scrolling) ->
                if (!scrolling && centeredIndex != null && centeredIndex in options.indices) {
                    pending = centeredIndex
                }
            }
        }
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
                        .focusRequester(focusRequester)
                        .focusable()
                        .semantics { heading() },
                    color = CaixaDSColorTokens.LegacyComboDark,
                    style = TextStyle(
                        fontFamily = CaixaDSLegacyRegularFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                        color = CaixaDSColorTokens.LegacySocialGraphite,
                        style = TextStyle(
                            fontFamily = CaixaDSLegacyRegularFontFamily,
                            fontSize = 14.sp,
                            platformStyle = PlatformTextStyle(includeFontPadding = true),
                        ),
                    )
                }

                if (description == null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                            .testTag("betting-option-list"),
                        contentPadding = PaddingValues(vertical = 0.dp),
                    ) {
                        itemsIndexed(options, key = { index, _ -> index }) { index, option ->
                            BettingOptionRow(
                                label = label(option),
                                selected = index == pending,
                                tag = "betting-option-item-$index",
                                onClick = { pending = index },
                            )
                        }
                    }
                } else {
                    val verticalCenterPadding = (170.dp - 52.dp) / 2
                    LazyColumn(
                        state = wheelState,
                        modifier = Modifier.fillMaxWidth().height(170.dp)
                            .testTag("betting-option-wheel"),
                        contentPadding = PaddingValues(vertical = verticalCenterPadding),
                        flingBehavior = rememberSnapFlingBehavior(lazyListState = wheelState),
                    ) {
                        itemsIndexed(options, key = { index, _ -> index }) { index, option ->
                            BettingOptionRow(
                                label = label(option),
                                selected = index == pending,
                                tag = "betting-option-item-$index",
                                wheel = true,
                                onClick = {
                                    pending = index
                                    // A tap on a wheel item centers it just as the native snap wheel did.
                                    coroutineScope.launch { wheelState.animateScrollToItem(index) }
                                },
                            )
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
                        modifier = Modifier.testTag("betting-option-cancel"),
                    ) {
                        Text(
                            "Cancelar",
                            color = CaixaDSColorTokens.LegacySocialGraphite,
                            style = CaixaDS.typography.labelLarge.copy(
                                fontFamily = CaixaDSLegacyRegularFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                    TextButton(
                        onClick = {
                            options.getOrNull(pending)?.let(latestSelect)
                            latestDismiss()
                        },
                        modifier = Modifier.testTag("betting-option-confirm"),
                    ) {
                        Text(
                            "Confirmar",
                            color = CaixaDSColorTokens.LegacyComboDark,
                            style = CaixaDS.typography.labelLarge.copy(
                                fontFamily = CaixaDSFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
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
    wheel: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(52.dp)
            .padding(horizontal = if (wheel) 15.dp else 0.dp)
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
            modifier = Modifier.fillMaxWidth(),
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
