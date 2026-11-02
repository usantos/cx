package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

internal data class PaymentVectorPath(val data: String, val fill: Brush, val fillType: PathFillType)

internal fun paymentVector(
    name: String,
    defaultWidth: Float,
    defaultHeight: Float,
    viewportWidth: Float,
    viewportHeight: Float,
    autoMirror: Boolean,
    paths: List<PaymentVectorPath>,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = defaultWidth.dp,
    defaultHeight = defaultHeight.dp,
    viewportWidth = viewportWidth,
    viewportHeight = viewportHeight,
    autoMirror = autoMirror
).apply {
    paths.forEach { vectorPath ->
        addPath(
            pathData = PathParser().parsePathString(vectorPath.data).toNodes(),
            pathFillType = vectorPath.fillType,
            fill = vectorPath.fill
        )
    }
}.build()
