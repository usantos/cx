package br.gov.caixa.loterias.core.designsystem.components.input

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
fun CaixaDSRadioButton(selected: Boolean, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    val outline = Color(0xFF666666)
    val selectedColor = CaixaDS.colors.primaryButton
    Canvas(
        modifier
            .size(24.dp)
            .clip(CircleShape)
            .then(
                if (onClick ==
                    null
                ) {
                    Modifier
                } else {
                    Modifier.uiId("ds.0104").toggleable(value = selected, role = Role.RadioButton, onValueChange = { onClick() })
                }
            )
    ) {
        val stroke = 2.dp.toPx()
        drawCircle(outline, radius = 9.dp.toPx(), style = Stroke(stroke))
        if (selected) drawCircle(selectedColor, radius = 5.dp.toPx())
    }
}

@Composable
fun CaixaDSCheckbox(
    checked: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    color: Color = CaixaDS.colors.primaryButton,
    uncheckedColor: Color = color,
) {
    val outline = color
    Canvas(
        modifier
            .size(24.dp)
            .then(
                if (onClick ==
                    null
                ) {
                    Modifier
                } else {
                    Modifier.uiId("ds.0105").toggleable(value = checked, role = Role.Checkbox, onValueChange = { onClick() })
                }
            )
    ) {
        val stroke = 2.dp.toPx()
        val side = 18.dp.toPx()
        val origin = Offset((size.width - side) / 2f, (size.height - side) / 2f)
        drawRoundRect(
            color = if (checked) color else Color.Transparent,
            topLeft = origin,
            size = Size(side, side),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
        drawRoundRect(
            color = if (checked) outline else uncheckedColor,
            topLeft = origin,
            size = Size(side, side),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(stroke)
        )
        if (checked) {
            val check = Path().apply {
                moveTo(origin.x + 3.5.dp.toPx(), origin.y + 9.dp.toPx())
                lineTo(origin.x + 7.5.dp.toPx(), origin.y + 13.dp.toPx())
                lineTo(origin.x + 15.dp.toPx(), origin.y + 5.dp.toPx())
            }
            drawPath(check, Color.White, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}

@Composable
fun CaixaDSTutorialCheckbox(checked: Boolean, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    Canvas(
        modifier
            .size(28.dp)
            .then(
                if (onClick ==
                    null
                ) {
                    Modifier
                } else {
                    Modifier.uiId("ds.0106").toggleable(value = checked, role = Role.Checkbox, onValueChange = { onClick() })
                }
            )
    ) {
        val side = 13.dp.toPx()
        val stroke = 1.dp.toPx()
        val origin = Offset((size.width - side) / 2f, (size.height - side) / 2f)
        val fill = if (checked) CaixaDSColorTokens.TutorialCheckbox else CaixaDSColorTokens.White
        val outline = if (checked) CaixaDSColorTokens.TutorialCheckbox else CaixaDSColorTokens.TutorialCheckboxOutline
        drawRoundRect(
            color = fill,
            topLeft = origin,
            size = Size(side, side),
            cornerRadius = CornerRadius(1.5.dp.toPx())
        )
        drawRoundRect(
            color = outline,
            topLeft = origin,
            size = Size(side, side),
            cornerRadius = CornerRadius(1.5.dp.toPx()),
            style = Stroke(stroke)
        )
        if (checked) {
            val check = Path().apply {
                moveTo(origin.x + 3.dp.toPx(), origin.y + 7.dp.toPx())
                lineTo(origin.x + 5.dp.toPx(), origin.y + 9.dp.toPx())
                lineTo(origin.x + 10.5.dp.toPx(), origin.y + 3.dp.toPx())
            }
            drawPath(check, color = CaixaDSColorTokens.White, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}
