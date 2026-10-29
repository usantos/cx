package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
internal fun PrizePixActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    outlined: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = (modifier.height(55.dp).graphicsLayer { alpha = if (enabled) 1f else .3f }).uiId("retail.0010"),
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        border = if (outlined) BorderStroke(1.dp, CaixaDSColorTokens.RedemptionButton) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (outlined) CaixaDSColorTokens.White else CaixaDSColorTokens.RedemptionButton,
            contentColor = if (outlined) CaixaDSColorTokens.RedemptionButton else CaixaDSColorTokens.White,
            disabledContainerColor = CaixaDSColorTokens.RedemptionButton,
            disabledContentColor = CaixaDSColorTokens.White
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.uiId("retail.0011"))
    }
}
