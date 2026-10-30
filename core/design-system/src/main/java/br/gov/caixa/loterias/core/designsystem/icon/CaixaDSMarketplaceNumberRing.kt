package br.gov.caixa.loterias.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Caminho literal de ic_item_dezena do CX; sua área de desenho descentralizada e seu traço também se aplicam em 35dp. */
val CaixaDSMarketplaceNumberRing: ImageVector by lazy {
    ImageVector.Builder("MarketplaceNumberRing", 45.dp, 45.dp, 41f, 41f).apply {
        addPath(
            PathParser().parsePathString("M20.0584,20.0584m-19.5584,0a19.5584,19.5584 0,1 1,39.1168 0a19.5584,19.5584 0,1 1,-39.1168 0").toNodes(),
            fill = SolidColor(Color.Transparent), stroke = SolidColor(Color(0xFF404B52)), strokeLineWidth = 1f,
        )
    }.build()
}
