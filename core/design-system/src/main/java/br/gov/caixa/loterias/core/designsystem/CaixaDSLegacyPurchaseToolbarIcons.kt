package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R

/** Original packed ic_funil.xml; the center remains transparent. */
val CaixaDSLegacyPurchaseFilterIcon: ImageVector = ImageVector.Builder(
    name = "CaixaDSLegacyPurchaseFilterIcon", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 24f, viewportHeight = 24f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M15,19.9c0,0.3 -0.1,0.6 -0.3,0.8c-0.4,0.4 -1,0.4 -1.4,0l-4,-4C9.1,16.5 9,16.2 9,15.9v-5.1L4.2,4.6C3.9,4.2 4,3.6 4.4,3.2C4.6,3.1 4.8,3 5,3l0,0h14l0,0c0.2,0 0.4,0.1 0.6,0.2c0.4,0.3 0.5,1 0.2,1.4L15,10.8V19.9M7,5l4,5.1v5.5l2,2v-7.5L17,5H7z").toNodes(),
        fill = SolidColor(Color.White),
    )
}.build()

/** Original density-specific bitmap used inside duvidas_toolbar_icon. */
@Composable
fun CaixaDSLegacyPurchaseHelpIcon(modifier: Modifier = Modifier, contentDescription: String = "Ajuda") {
    Image(painterResource(R.drawable.icon_duvida2), contentDescription, modifier)
}
