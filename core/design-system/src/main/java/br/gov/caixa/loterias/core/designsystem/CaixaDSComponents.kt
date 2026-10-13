package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    val content: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(CaixaDS.sizes.iconSm),
                strokeWidth = 2.dp,
            )
        } else {
            leadingIcon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(CaixaDS.sizes.iconSm))
                Spacer(Modifier.width(CaixaDS.spacing.sm))
            }
            Text(text)
        }
    }
    when (variant) {
        CaixaDSButtonVariant.Primary -> Button(
            onClick = onClick,
            modifier = modifier.height(CaixaDS.sizes.controlHeight),
            enabled = enabled && !loading,
            content = { content() },
        )
        CaixaDSButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(CaixaDS.sizes.controlHeight),
            enabled = enabled && !loading,
            content = { content() },
        )
        CaixaDSButtonVariant.Tertiary -> TextButton(
            onClick = onClick,
            modifier = modifier.height(CaixaDS.sizes.controlHeight),
            enabled = enabled && !loading,
            content = { content() },
        )
        CaixaDSButtonVariant.Danger -> Button(
            onClick = onClick,
            modifier = modifier.height(CaixaDS.sizes.controlHeight),
            enabled = enabled && !loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = CaixaDS.colors.error,
                contentColor = CaixaDS.colors.onError,
            ),
            content = { content() },
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
    IconButton(onClick = onClick, modifier = modifier.size(CaixaDS.sizes.touchTarget), enabled = enabled) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
fun CaixaDSTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        singleLine = singleLine,
        isError = isError,
        supportingText = supportingText?.let { message -> { Text(message) } },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
    )
}

@Composable
fun CaixaDSCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = CaixaDS.elevation.low),
        content = { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) { content() } },
    )
}

@Composable
fun CaixaDSSectionTitle(title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm)) {
        Text(title, style = CaixaDS.typography.titleMedium, color = CaixaDS.colors.brand)
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.divider))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaixaDSTopBar(title: String, onBack: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(title, style = CaixaDS.typography.titleMedium) },
        navigationIcon = {
            onBack?.let {
                CaixaDSIconButton(
                    icon = CaixaDSIcons.Back,
                    contentDescription = "Voltar",
                    onClick = it,
                )
            }
        },
    )
}

@Composable
fun CaixaDSAlertDialog(
    title: String,
    message: AnnotatedString,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm,
) {
    CaixaDSLegacyDialog(
        title = title,
        message = message,
        positiveLabel = confirmText,
        onPositive = onConfirm,
        onDismiss = onDismiss,
    )
}

@Composable
fun CaixaDSAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm,
) = CaixaDSAlertDialog(title, AnnotatedString(message), confirmText, onConfirm, onDismiss)

@Composable
fun CaixaDSLoading(label: String = "Carregando", modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm),
    ) {
        CircularProgressIndicator()
        Text(label, style = CaixaDS.typography.bodyMedium, color = CaixaDS.colors.textSecondary)
    }
}

/** Marca oficial usada nos fundos institucionais escuros. */
@Composable
fun CaixaDSLoteriasLogo(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.caixa_loterias_logo),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

/** Vetor original digital2 transcrito para Compose, sem drawable XML. */
@Composable
fun CaixaDSFingerprintIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Icon(LegacyFingerprintVector, contentDescription = null, modifier = modifier, tint = color)
}

@Composable
fun CaixaDSMessageState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm),
    ) {
        Text(title, style = CaixaDS.typography.titleMedium)
        Text(message, style = CaixaDS.typography.bodyMedium, color = CaixaDS.colors.textSecondary)
        if (actionText != null && onAction != null) {
            CaixaDSButton(actionText, onAction, variant = CaixaDSButtonVariant.Secondary)
        }
    }
}

@Composable
fun CaixaDSNumberBall(number: Int, modifier: Modifier = Modifier, selected: Boolean = true) {
    val background = if (selected) CaixaDS.colors.brand else CaixaDS.colors.surface
    val foreground = if (selected) CaixaDS.colors.onBrand else CaixaDS.colors.brand
    Box(
        modifier = modifier.size(CaixaDS.sizes.numberBall).background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(number.toString().padStart(2, '0'), color = foreground, style = CaixaDS.typography.labelLarge)
    }
}

@Composable
fun CaixaSectionTitle(title: String, modifier: Modifier = Modifier) = CaixaDSSectionTitle(title, modifier)

@Composable
fun CaixaTopBar(title: String, onBack: (() -> Unit)? = null) = CaixaDSTopBar(title, onBack)
