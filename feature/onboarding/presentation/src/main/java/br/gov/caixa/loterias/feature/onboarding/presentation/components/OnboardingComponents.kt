package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
internal fun LegacyFlatAction(text: String, modifier: Modifier, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = (modifier).uiId("start.0010"),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = CaixaDSColorTokens.White),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.uiId("start.0011"))
    }
}

@Composable
internal fun LegacyTutorialButton(text: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier, onClick: () -> Unit) {
    val lines = text.split('\n', limit = 2)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier.background(color).uiId("start.0012").clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                lines.first(),
                color = CaixaDSColorTokens.White,
                fontFamily = CaixaDSFuturaFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp,
                modifier = Modifier.uiId("start.0013")
            )
            if (lines.size > 1) {
                Text(
                    lines[1],
                    color = CaixaDSColorTokens.White,
                    fontFamily = CaixaDSFuturaFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp,
                    modifier = Modifier.uiId("start.0014")
                )
            }
        }
    }
}
