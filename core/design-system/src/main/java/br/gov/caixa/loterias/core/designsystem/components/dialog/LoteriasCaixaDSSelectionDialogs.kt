package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSDialog(
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
) = LoteriasCaixaDSDialog(
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
fun LoteriasCaixaDSDialog(
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
            modifier = Modifier.loteriasCaixaDSModalWidth(),
            shape = RoundedCornerShape(4.dp),
            color = LoteriasCaixaDSColorTokens.White
        ) {
            Column(
                Modifier.fillMaxWidth().uiId("ds.0086").verticalScroll(
                    rememberScrollState()
                ).padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp)
            ) {
                SelectionDialogTitle(title)
                Text(
                    message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (title.isNotBlank()) Modifier.padding(top = 16.dp) else Modifier),
                    color = LoteriasCaixaDSColorTokens.SocialGraphite,
                    fontSize = 16.sp,
                    style = nativeDialogTextStyle
                )
                extraContent?.invoke()
                if (neutralLabel != null) {
                    Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                        SelectionDialogAction(
                            label = positiveLabel,
                            onClick = onPositive,
                            emphasized = true,
                            modifier = (Modifier.fillMaxWidth()).automationId("simlo.dialog.positivo"),
                            visitorPrompt = visitorPrompt
                        )
                        negativeLabel?.let { label ->
                            SelectionDialogAction(label, onNegative, false, (Modifier.fillMaxWidth()).uiId("ds.0087"), visitorPrompt)
                        }
                        SelectionDialogAction(neutralLabel, onNeutral, false, (Modifier.fillMaxWidth()).uiId("ds.0088"), visitorPrompt)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        negativeLabel?.let { label ->
                            SelectionDialogAction(
                                label,
                                onNegative,
                                false,
                                visitorPrompt = visitorPrompt,
                                modifier = Modifier.uiId("ds.0089")
                            )
                        }
                        SelectionDialogAction(
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
