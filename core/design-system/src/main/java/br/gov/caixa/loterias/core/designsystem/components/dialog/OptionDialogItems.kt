package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

private const val NUMBER_LIST_HEIGHT_DP = 300
private const val WHEEL_HEIGHT_DP = 170

@Composable
private fun BettingOptionRow(label: String, selected: Boolean, tag: String, onClick: () -> Unit) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        modifier = Modifier.fillMaxWidth()
            .then(if (largeFont) Modifier.heightIn(min = 52.dp).padding(vertical = 12.dp) else Modifier.height(52.dp))
            .background(
                color = if (selected) LoteriasCaixaDSColorTokens.SocialCanvas else androidx.compose.ui.graphics.Color.Transparent,
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
            color = if (selected) LoteriasCaixaDSColorTokens.RetailSearchText else LoteriasCaixaDSColorTokens.TicketChoiceMuted,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSRegularFontFamily,
                fontSize = 15.8.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
    }
}

@Composable
internal fun OptionDialogItems(
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

internal suspend fun centerOptionRow(wheelState: LazyListState, index: Int) {
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
