package br.gov.caixa.loterias.core.designsystem.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily

object CaixaDSBettingButtonDefaults {
    /** mtrl_btn_inset do Widget.MaterialComponents.Button, mantido pelo XML nativo. */
    val NativeVerticalInset: Dp
        @Composable get() = with(androidx.compose.ui.platform.LocalDensity.current) {
            // MaterialButton lê estes valores usando getDimensionPixelOffset, que
            // trunca em vez de arredondar em densidades fracionárias de tela.
            6.dp.toPx().toInt().toDp()
        }
}

/** Geometria nativa de apostas: [verticalInset] altera a pintura, preservando toda a área de toque. */
@Composable
fun CaixaDSBettingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    color: Color,
    content: Color,
    stroke: Color = color,
    enabled: Boolean = true,
    textSize: Float = 13f,
    corner: Float = 4f,
    icon: Int? = null,
    semiBold: Boolean = false,
    textSizeInDp: Boolean = false,
    iconAtStart: Boolean = false,
    retainNativeAnimator: Boolean = false,
    verticalInset: Dp = 0.dp,
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val fontSize = with(density) {
        val pixels = if (textSizeInDp && fontScale == 1f) textSize.dp.roundToPx() else textSize.sp.roundToPx()
        (pixels.toFloat() / this.density / this.fontScale).sp
    }
    Box(
        modifier = modifier
            .legacyAutomationId("betting-button-$text")
            .drawWithCache {
                val inset = verticalInset.roundToPx().toFloat()
                val outline = Path().apply {
                    addRoundRect(
                        RoundRect(
                            0f,
                            inset,
                            size.width,
                            size.height - inset,
                            CornerRadius(corner.dp.toPx())
                        )
                    )
                }
                onDrawWithContent {
                    // Recorta a indicação nas coordenadas de toda a área de toque para
                    // seu ponto de origem permanecer na posição real do ponteiro.
                    clipPath(outline) { this@onDrawWithContent.drawContent() }
                }
            }
            .uiId("ds.0072").clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = text }
            .padding(vertical = verticalInset)
            .clip(RoundedCornerShape(corner.dp))
            .background(color)
            .border(1.dp, stroke, RoundedCornerShape(corner.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = if (icon != null && !iconAtStart) 4.dp else 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null && iconAtStart) {
                Icon(painterResource(icon), null, Modifier.size(24.dp), tint = Color.Unspecified)
                Spacer(Modifier.width(2.dp))
            }
            Text(
                text = text,
                modifier = Modifier.weight(1f, fill = false),
                color = content,
                fontFamily = CaixaDSFontFamily,
                fontWeight = if (semiBold) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
            if (icon != null && !iconAtStart) {
                Spacer(Modifier.width(8.dp))
                Icon(painterResource(icon), null, Modifier.size(7.dp, 4.dp).legacyAutomationId("betting-dropdown-icon"), tint = content)
            }
        }
    }
}
