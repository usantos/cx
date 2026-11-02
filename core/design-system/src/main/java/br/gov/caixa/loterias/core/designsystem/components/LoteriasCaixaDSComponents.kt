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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.icon.FingerprintVector
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

enum class LoteriasCaixaDSButtonVariant { Primary, Secondary, Tertiary, Danger }

@Composable
fun LoteriasCaixaDSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: LoteriasCaixaDSButtonVariant = LoteriasCaixaDSButtonVariant.Primary,
    leadingIcon: ImageVector? = null,
) {
    val largeFont = LocalDensity.current.fontScale > 1f
    val buttonModifier = modifier.then(
        if (largeFont) {
            Modifier.heightIn(
                min = LoteriasCaixaDS.sizes.controlHeight
            )
        } else {
            Modifier.height(LoteriasCaixaDS.sizes.controlHeight)
        }
    )
    val content: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(LoteriasCaixaDS.sizes.iconSm),
                strokeWidth = 2.dp
            )
        } else {
            leadingIcon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(LoteriasCaixaDS.sizes.iconSm))
                Spacer(Modifier.width(LoteriasCaixaDS.spacing.sm))
            }
            Text(text, modifier = if (largeFont) Modifier.fillMaxWidth() else Modifier, textAlign = TextAlign.Center)
        }
    }
    when (variant) {
        LoteriasCaixaDSButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = (buttonModifier).uiId("ds.0005"),
            enabled = enabled && !loading,
            content = { content() }
        )

        LoteriasCaixaDSButtonVariant.Tertiary -> TextButton(
            onClick = onClick,
            modifier = (buttonModifier).uiId("ds.0006"),
            enabled = enabled && !loading,
            content = { content() }
        )

        LoteriasCaixaDSButtonVariant.Primary, LoteriasCaixaDSButtonVariant.Danger -> Button(
            onClick = onClick,
            modifier = (buttonModifier).uiId("ds.0007"),
            enabled = enabled && !loading,
            colors = if (variant == LoteriasCaixaDSButtonVariant.Danger) {
                ButtonDefaults.buttonColors(
                    containerColor = LoteriasCaixaDS.colors.error,
                    contentColor = LoteriasCaixaDS.colors.onError
                )
            } else {
                ButtonDefaults.buttonColors()
            },
            content = { content() }
        )
    }
}

@Composable
fun LoteriasCaixaDSIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(onClick = onClick, modifier = (modifier.size(LoteriasCaixaDS.sizes.touchTarget)).uiId("ds.0008"), enabled = enabled) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
fun LoteriasCaixaDSSectionTitle(title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(LoteriasCaixaDS.spacing.sm)) {
        Text(title, style = LoteriasCaixaDS.typography.titleMedium, color = LoteriasCaixaDS.colors.brand)
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDS.colors.divider))
    }
}

@Composable
fun LoteriasCaixaDSAlertDialog(
    title: String,
    message: AnnotatedString,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm,
) {
    LoteriasCaixaDSDialog(
        title = title,
        message = message,
        positiveLabel = confirmText,
        onPositive = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun LoteriasCaixaDSAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm,
) = LoteriasCaixaDSAlertDialog(title, AnnotatedString(message), confirmText, onConfirm, onDismiss)

@Composable
fun LoteriasCaixaDSLoading(modifier: Modifier = Modifier, label: String = stringResource(LocalizedR.string.format_loading)) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LoteriasCaixaDS.spacing.sm)
    ) {
        CircularProgressIndicator()
        Text(label, style = LoteriasCaixaDS.typography.bodyMedium, color = LoteriasCaixaDS.colors.textSecondary)
    }
}

/** Marca oficial usada nos fundos institucionais escuros. */
@Composable
fun LoteriasCaixaDSLoteriasLogo(contentDescription: String, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.caixa_loterias_logo),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

/** Vetor original digital2 transcrito para Compose, sem drawable XML. */
@Composable
fun LoteriasCaixaDSFingerprintIcon(color: Color, modifier: Modifier = Modifier) {
    Icon(FingerprintVector, contentDescription = null, modifier = modifier, tint = color)
}

@Composable
fun LoteriasCaixaDSNumberBall(number: Int, modifier: Modifier = Modifier, selected: Boolean = true) {
    val background = if (selected) LoteriasCaixaDS.colors.brand else LoteriasCaixaDS.colors.surface
    val foreground = if (selected) LoteriasCaixaDS.colors.onBrand else LoteriasCaixaDS.colors.brand
    Box(
        modifier = modifier.size(LoteriasCaixaDS.sizes.numberBall).background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(number.toString().padStart(2, '0'), color = foreground, style = LoteriasCaixaDS.typography.labelLarge)
    }
}
