package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSChoiceRow
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

private val nativeDialogTextStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
fun CaixaDSDialog(
    title: String,
    message: String,
    positiveLabel: String,
    onPositive: () -> Unit,
    negativeLabel: String? = null,
    onNegative: () -> Unit = {},
    neutralLabel: String? = null,
    onNeutral: () -> Unit = {},
    onDismiss: () -> Unit = onNegative,
    extraContent: (@Composable () -> Unit)? = null,
    visitorPrompt: Boolean = false,
) = CaixaDSDialog(
    title = title,
    message = AnnotatedString(message),
    positiveLabel = positiveLabel,
    onPositive = onPositive,
    negativeLabel = negativeLabel,
    onNegative = onNegative,
    neutralLabel = neutralLabel,
    onNeutral = onNeutral,
    onDismiss = onDismiss,
    extraContent = extraContent,
    visitorPrompt = visitorPrompt
)

@Composable
fun CaixaDSDialog(
    title: String,
    message: AnnotatedString,
    positiveLabel: String,
    onPositive: () -> Unit,
    negativeLabel: String? = null,
    onNegative: () -> Unit = {},
    neutralLabel: String? = null,
    onNeutral: () -> Unit = {},
    onDismiss: () -> Unit = onNegative,
    extraContent: (@Composable () -> Unit)? = null,
    visitorPrompt: Boolean = false,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.caixaDSModalWidth(),
            shape = RoundedCornerShape(4.dp),
            color = CaixaDSColorTokens.White
        ) {
            Column(
                Modifier.fillMaxWidth().uiId("ds.0086").verticalScroll(
                    rememberScrollState()
                ).padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp)
            ) {
                DialogTitle(title)
                Text(
                    message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (title.isNotBlank()) Modifier.padding(top = 16.dp) else Modifier),
                    color = CaixaDSColorTokens.SocialGraphite,
                    fontSize = 16.sp,
                    style = nativeDialogTextStyle
                )
                extraContent?.invoke()
                if (neutralLabel != null) {
                    Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                        DialogAction(
                            label = positiveLabel,
                            onClick = onPositive,
                            emphasized = true,
                            modifier = (Modifier.fillMaxWidth()).automationId("simlo.dialog.positivo"),
                            visitorPrompt = visitorPrompt
                        )
                        negativeLabel?.let { label ->
                            DialogAction(label, onNegative, false, (Modifier.fillMaxWidth()).uiId("ds.0087"), visitorPrompt)
                        }
                        DialogAction(neutralLabel, onNeutral, false, (Modifier.fillMaxWidth()).uiId("ds.0088"), visitorPrompt)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        negativeLabel?.let { label ->
                            DialogAction(label, onNegative, false, visitorPrompt = visitorPrompt, modifier = Modifier.uiId("ds.0089"))
                        }
                        DialogAction(
                            positiveLabel,
                            onPositive,
                            true,
                            visitorPrompt = visitorPrompt,
                            modifier = Modifier
                                .automationId("simlo.dialog.positivo")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogTitle(title: String) {
    if (title.isNotBlank()) {
        Text(
            title,
            modifier = Modifier.semantics { heading() },
            color = CaixaDS.colors.brand,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            style = nativeDialogTextStyle
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(top = 17.dp),
            thickness = 1.dp,
            color = CaixaDSColorTokens.TutorialDivider
        )
    }
}

@Composable
fun CaixaDSSelectionDialog(
    title: String,
    message: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String = stringResource(LocalizedR.string.format_confirm),
    dismissLabel: String = stringResource(LocalizedR.string.format_cancel),
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.caixaDSModalWidth(),
            shape = RoundedCornerShape(4.dp),
            color = CaixaDSColorTokens.White
        ) {
            Column(
                Modifier.fillMaxWidth().uiId("ds.0090").verticalScroll(
                    rememberScrollState()
                ).padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp)
            ) {
                Text(title, color = CaixaDS.colors.brand, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider(
                    Modifier.fillMaxWidth().padding(top = 17.dp),
                    thickness = 1.dp,
                    color = CaixaDSColorTokens.TutorialDivider
                )
                Text(
                    message,
                    Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
                    color = CaixaDSColorTokens.SocialGraphite,
                    fontSize = 16.sp
                )
                Column(Modifier.selectableGroup()) {
                    options.forEachIndexed { index, option ->
                        CaixaDSChoiceRow(
                            option,
                            selected = index == selectedIndex,
                            onClick = { onSelect(index) },
                            modifier = Modifier
                                .uiId("ds.0091")
                        )
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                    DialogAction(dismissLabel, onDismiss, false, modifier = Modifier.uiId("ds.0092"))
                    DialogAction(confirmLabel, onConfirm, true, modifier = Modifier.uiId("ds.0093"))
                }
            }
        }
    }
}

@Composable
private fun DialogAction(
    label: String,
    onClick: () -> Unit,
    emphasized: Boolean,
    modifier: Modifier = Modifier,
    visitorPrompt: Boolean = false,
) {
    // Activities privadas AppCompat usam 54dp/64dp; Theme.SimulaMaterial usa 48dp/88dp.
    Box(
        modifier.widthIn(min = if (visitorPrompt) 64.dp else 88.dp)
            .heightIn(min = if (visitorPrompt) 54.dp else 48.dp).uiId("ds.0094").clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = if (emphasized) CaixaDS.colors.brand else CaixaDSColorTokens.SocialGraphite,
            style = nativeDialogTextStyle.copy(fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal),
            textAlign = TextAlign.Center
        )
    }
}
