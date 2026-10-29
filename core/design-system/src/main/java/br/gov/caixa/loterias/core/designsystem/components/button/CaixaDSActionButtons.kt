package br.gov.caixa.loterias.core.designsystem.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

@Composable
fun CaixaDSPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = (modifier.then(if (largeFont) Modifier.heightIn(min = CaixaDS.sizes.button) else Modifier.height(CaixaDS.sizes.button)))
            .uiId("ds.0069"),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CaixaDS.colors.actionHighlight,
            contentColor = Color.White
        )
    ) {
        Text(
            text,
            modifier = if (largeFont) Modifier.fillMaxWidth() else Modifier,
            style = CaixaDS.typography.titleMedium,
            maxLines = if (largeFont) Int.MAX_VALUE else 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CaixaDSFooterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = CaixaDS.colors.primaryButton,
    disabledContainerColor: Color = CaixaDS.colors.disabledButton,
    maxLines: Int = 1,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = (modifier.then(if (largeFont) Modifier.heightIn(min = 48.dp) else Modifier.height(48.dp))).uiId("ds.0070"),
        shape = RoundedCornerShape(2.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
    ) {
        Text(
            text,
            style = CaixaDS.typography.titleMedium.copy(fontSize = fontSize),
            modifier = if (largeFont) Modifier.fillMaxWidth() else Modifier,
            maxLines = if (largeFont) Int.MAX_VALUE else maxLines,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CaixaDSOutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Button(
        onClick = onClick,
        modifier = (modifier.then(if (largeFont) Modifier.heightIn(min = CaixaDS.sizes.button) else Modifier.height(CaixaDS.sizes.button)))
            .uiId("ds.0071"),
        shape = RoundedCornerShape(25.dp),
        border = BorderStroke(1.dp, CaixaDS.colors.brand),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = CaixaDS.colors.brand
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
    ) {
        Text(
            text,
            modifier = if (largeFont) Modifier.fillMaxWidth() else Modifier,
            style = CaixaDS.typography.titleMedium,
            maxLines = if (largeFont) Int.MAX_VALUE else 1,
            textAlign = TextAlign.Center
        )
    }
}
