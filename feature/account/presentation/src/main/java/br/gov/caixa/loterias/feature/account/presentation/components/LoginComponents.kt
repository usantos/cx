package br.gov.caixa.loterias.feature.account.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

@Composable
internal fun LegacyLoginButton(
    text: String,
    onClick: () -> Unit,
    outlined: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    val background = if (outlined) CaixaDS.colors.authenticationBackground else CaixaDS.colors.actionHighlight
    val foreground = CaixaDS.colors.onBrand
    val largeFont = androidx.compose.ui.platform.LocalDensity.current.fontScale > 1f
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = (
            modifier.fillMaxWidth()
                .then(if (largeFont) Modifier.heightIn(min = 48.dp) else Modifier.height(48.dp))
            ).uiId("account.0006"),
        shape = RoundedCornerShape(CaixaDS.radii.sm),
        border = if (outlined) BorderStroke(1.dp, foreground) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = foreground,
            disabledContainerColor = background.copy(alpha = 0.55f),
            disabledContentColor = foreground.copy(alpha = 0.75f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = CaixaDS.elevation.none)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(CaixaDS.sizes.iconMd),
                color = foreground,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                modifier = (modifier.fillMaxWidth()).uiId("account.0007"),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily(
                    androidx.compose.ui.text.font.Font(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold)
                ),
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
        }
    }
}
