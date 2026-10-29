package br.gov.caixa.loterias.core.designsystem.layout

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/** Preserva a renderização nativa de LayerDrawable e RotateDrawable dos recursos originais. */
@Composable
fun caixaDSDrawablePainter(@DrawableRes resource: Int): Painter {
    val context = LocalContext.current
    val resources = LocalResources.current
    val configuration = LocalConfiguration.current
    return remember(resources, configuration, context.theme, resource) {
        val drawable = requireNotNull(resources.getDrawable(resource, context.theme)).mutate()
        object : Painter() {
            override val intrinsicSize = Size.Unspecified
            override fun DrawScope.onDraw() {
                drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                drawIntoCanvas { drawable.draw(it.nativeCanvas) }
            }
        }
    }
}
