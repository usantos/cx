package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.icon.FingerprintVector
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

enum class CaixaDSButtonVariant { Primary, Secondary, Tertiary, Danger }

@Composable
fun CaixaDSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: CaixaDSButtonVariant = CaixaDSButtonVariant.Primary,
    leadingIcon: ImageVector? = null,
) {
    val largeFont = LocalDensity.current.fontScale > 1f
    val buttonModifier = modifier.then(
        if (largeFont) Modifier.heightIn(min = CaixaDS.sizes.controlHeight) else Modifier.height(CaixaDS.sizes.controlHeight)
    )
    val content: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(CaixaDS.sizes.iconSm),
                strokeWidth = 2.dp
            )
        } else {
            leadingIcon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(CaixaDS.sizes.iconSm))
                Spacer(Modifier.width(CaixaDS.spacing.sm))
            }
            Text(text, modifier = if (largeFont) Modifier.fillMaxWidth() else Modifier, textAlign = TextAlign.Center)
        }
    }
    when (variant) {
        CaixaDSButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = (buttonModifier).uiId("ds.0005"),
            enabled = enabled && !loading,
            content = { content() }
        )

        CaixaDSButtonVariant.Tertiary -> TextButton(
            onClick = onClick,
            modifier = (buttonModifier).uiId("ds.0006"),
            enabled = enabled && !loading,
            content = { content() }
        )

        CaixaDSButtonVariant.Primary, CaixaDSButtonVariant.Danger -> Button(
            onClick = onClick,
            modifier = (buttonModifier).uiId("ds.0007"),
            enabled = enabled && !loading,
            colors = if (variant == CaixaDSButtonVariant.Danger) {
                ButtonDefaults.buttonColors(
                    containerColor = CaixaDS.colors.error,
                    contentColor = CaixaDS.colors.onError
                )
            } else {
                ButtonDefaults.buttonColors()
            },
            content = { content() }
        )
    }
}

@Composable
fun CaixaDSIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(onClick = onClick, modifier = (modifier.size(CaixaDS.sizes.touchTarget)).uiId("ds.0008"), enabled = enabled) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
fun CaixaDSSectionTitle(title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm)) {
        Text(title, style = CaixaDS.typography.titleMedium, color = CaixaDS.colors.brand)
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.divider))
    }
}

@Composable
fun CaixaDSAlertDialog(
    title: String,
    message: AnnotatedString,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm,
) {
    CaixaDSDialog(
        title = title,
        message = message,
        positiveLabel = confirmText,
        onPositive = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun CaixaDSAlertDialog(title: String, message: String, confirmText: String, onConfirm: () -> Unit, onDismiss: () -> Unit = onConfirm) =
    CaixaDSAlertDialog(title, AnnotatedString(message), confirmText, onConfirm, onDismiss)

@Composable
fun CaixaDSLoading(label: String = stringResource(LocalizedR.string.format_loading), modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm)
    ) {
        CircularProgressIndicator()
        Text(label, style = CaixaDS.typography.bodyMedium, color = CaixaDS.colors.textSecondary)
    }
}

/** Marca oficial usada nos fundos institucionais escuros. */
@Composable
fun CaixaDSLoteriasLogo(contentDescription: String, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.caixa_loterias_logo),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

/** Vetor original digital2 transcrito para Compose, sem drawable XML. */
@Composable
fun CaixaDSFingerprintIcon(color: Color, modifier: Modifier = Modifier) {
    Icon(FingerprintVector, contentDescription = null, modifier = modifier, tint = color)
}

@Composable
fun CaixaDSNumberBall(number: Int, modifier: Modifier = Modifier, selected: Boolean = true) {
    val background = if (selected) CaixaDS.colors.brand else CaixaDS.colors.surface
    val foreground = if (selected) CaixaDS.colors.onBrand else CaixaDS.colors.brand
    Box(
        modifier = modifier.size(CaixaDS.sizes.numberBall).background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(number.toString().padStart(2, '0'), color = foreground, style = CaixaDS.typography.labelLarge)
    }
}
