package br.gov.caixa.loterias.core.designsystem.components.input

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSCheckSwitch(
    enabled: Boolean,
    accent: Color,
    onAccent: Color,
    trackBackground: Color,
    invertColors: Boolean,
    onClick: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.size(48.dp).uiId("ds.0102").toggleable(value = enabled, role = Role.Switch, onValueChange = { onClick() })
            .semantics { contentDescription = uiStrings.getString(LocalizedR.string.ui_aposta_shake_317269) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier.size(width = 48.dp, height = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (invertColors) {
                        LoteriasCaixaDSColorTokens.White
                    } else if (enabled) {
                        accent
                    } else {
                        trackBackground
                    }
                )
                .then(
                    if (enabled &&
                        !invertColors
                    ) {
                        Modifier
                    } else {
                        Modifier.border(
                            1.5.dp,
                            if (invertColors) accent else LoteriasCaixaDSColorTokens.ShakeTrackOutline,
                            RoundedCornerShape(12.dp)
                        )
                    }
                )
        ) {
            Box(
                Modifier.align(if (enabled) Alignment.CenterEnd else Alignment.CenterStart)
                    .size(24.dp).padding(2.dp).clip(CircleShape)
                    .background(
                        if (invertColors) {
                            accent
                        } else {
                            if (enabled) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDSColorTokens.SocialGraphite
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.size(12.dp)) {
                    val path = checkSwitchPath(enabled)
                    drawPath(
                        path,
                        if (invertColors) {
                            onAccent
                        } else {
                            if (enabled) accent else LoteriasCaixaDSColorTokens.White
                        },
                        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

private fun DrawScope.checkSwitchPath(enabled: Boolean): Path {
    val path = Path()
    if (enabled) {
        path.moveTo(
            size.width * CheckSwitchMetrics.CHECK_START_X_FRACTION,
            size.height * CheckSwitchMetrics.CHECK_START_Y_FRACTION
        )
        path.lineTo(
            size.width * CheckSwitchMetrics.CHECK_JOINT_X_FRACTION,
            size.height * CheckSwitchMetrics.CHECK_JOINT_Y_FRACTION
        )
        path.lineTo(
            size.width * CheckSwitchMetrics.CHECK_END_X_FRACTION,
            size.height * CheckSwitchMetrics.CHECK_END_Y_FRACTION
        )
    } else {
        path.moveTo(
            size.width * CheckSwitchMetrics.CROSS_START_FRACTION,
            size.height * CheckSwitchMetrics.CROSS_START_FRACTION
        )
        path.lineTo(size.width * CheckSwitchMetrics.CROSS_END_FRACTION, size.height * CheckSwitchMetrics.CROSS_END_FRACTION)
        path.moveTo(
            size.width * CheckSwitchMetrics.CROSS_END_FRACTION,
            size.height * CheckSwitchMetrics.CROSS_START_FRACTION
        )
        path.lineTo(
            size.width * CheckSwitchMetrics.CROSS_START_FRACTION,
            size.height * CheckSwitchMetrics.CROSS_END_FRACTION
        )
    }
    return path
}

private object CheckSwitchMetrics {
    const val CHECK_START_X_FRACTION = .16f
    const val CHECK_START_Y_FRACTION = .53f
    const val CHECK_JOINT_X_FRACTION = .42f
    const val CHECK_JOINT_Y_FRACTION = .78f
    const val CHECK_END_X_FRACTION = .86f
    const val CHECK_END_Y_FRACTION = .22f
    const val CROSS_START_FRACTION = .24f
    const val CROSS_END_FRACTION = .76f
}
