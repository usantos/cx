package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

object CaixaDSTextInputDialogTokens {
    val Radius = 8.dp
    val Padding = 24.dp
    val ActionHeight = 55.dp
    val Blue = CaixaDSColorTokens.CaixaBlue
    val Divider = CaixaDSColorTokens.SimulationMuted
    val Text = CaixaDSColorTokens.SocialGraphite
}

/** Equivalente Compose de custom_dialog_edit_text.xml; a validação pertence ao chamador. */
@Composable
fun CaixaDSTextInputDialog(
    title: String,
    value: String,
    hint: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    enabled: Boolean = true,
    errorMessage: String? = null,
) {
    val tokens = CaixaDSTextInputDialogTokens
    Dialog(
        onDismissRequest = { if (enabled) onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = enabled,
            dismissOnClickOutside = enabled
        )
    ) {
        Surface(
            Modifier.caixaDSModalWidth(),
            shape = RoundedCornerShape(tokens.Radius),
            color = CaixaDSColorTokens.White
        ) {
            Column(
                Modifier.uiId("ds.0095").verticalScroll(rememberScrollState())
                    .padding(start = tokens.Padding, end = tokens.Padding, top = tokens.Padding)
            ) {
                Text(
                    title,
                    Modifier.semantics { heading() },
                    color = tokens.Blue,
                    fontFamily = CaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                HorizontalDivider(Modifier.padding(top = 17.dp), color = tokens.Divider)
                BasicTextField(
                    value = value, onValueChange = onValueChange, enabled = enabled,
                    modifier = (
                        Modifier.fillMaxWidth().padding(top = 16.dp)
                            .heightIn(min = 48.dp).semantics {
                                contentDescription = hint
                                if (!errorMessage.isNullOrBlank()) error(errorMessage)
                            }
                        ).uiId("ds.0096"),
                    textStyle = TextStyle(fontFamily = CaixaDSFontFamily, fontSize = 18.sp, color = tokens.Text),
                    cursorBrush = SolidColor(tokens.Blue), singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { if (enabled) onSave() }),
                    decorationBox = { field ->
                        Column {
                            Box(
                                Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 4.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (value.isEmpty()) {
                                    Text(
                                        hint,
                                        fontFamily = CaixaDSFontFamily,
                                        fontSize = 18.sp,
                                        color = tokens.Text
                                    )
                                }
                                field()
                            }
                            HorizontalDivider(color = tokens.Divider)
                        }
                    }
                )
                errorMessage?.let {
                    Text(
                        it,
                        Modifier.padding(top = 4.dp).semantics {
                            liveRegion = LiveRegionMode.Polite
                        },
                        color = CaixaDS.colors.error,
                        fontSize = 12.sp
                    )
                }
                TextInputDialogActions(enabled, onSave, onDismiss)
            }
        }
    }
}

@Composable
private fun TextInputDialogActions(enabled: Boolean, onSave: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val tokens = CaixaDSTextInputDialogTokens
    Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onDismiss, enabled = enabled, modifier = (Modifier.height(tokens.ActionHeight)).uiId("ds.0097")) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
                color = tokens.Text,
                fontFamily = CaixaDSFontFamily,
                fontSize = 16.sp
            )
        }
        TextButton(onClick = onSave, enabled = enabled, modifier = (Modifier.height(tokens.ActionHeight)).uiId("ds.0098")) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_salvar_aef7cd),
                color = tokens.Blue,
                fontFamily = CaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
