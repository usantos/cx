package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import br.gov.caixa.loterias.core.designsystem.layout.LoteriasCaixaDSModalTokens

private const val OPTION_DIALOG_WIDTH_FRACTION = .85f
private const val NATIVE_TEXT_SIZE_SP = 16f

internal fun Modifier.optionDialogWidth(): Modifier = layout { measurable, constraints ->
    // DialogUtilsTheme declara windowMinWidthMajor/Minor como 85%.
    val width = (constraints.maxWidth * OPTION_DIALOG_WIDTH_FRACTION).toInt()
        .coerceIn(0, LoteriasCaixaDSModalTokens.MaxWidth.roundToPx())
    val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
}

internal fun measureOptionDialogMetrics(
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
