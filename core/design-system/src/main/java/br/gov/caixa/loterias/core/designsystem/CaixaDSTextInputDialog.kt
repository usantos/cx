package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

object CaixaDSTextInputDialogTokens {
    const val WidthFraction = .85f
    val Radius = 8.dp
    val Padding = 24.dp
    val ActionHeight = 55.dp
    val Blue = Color(0xFF0066B3)
    val Divider = Color(0xFF9EB2B8)
    val Text = CaixaDSColorTokens.LegacySocialGraphite
}

/** Compose counterpart of custom_dialog_edit_text.xml; validation belongs to the caller. */
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
    Dialog(onDismissRequest = { if (enabled) onDismiss() }, properties = DialogProperties(
        usePlatformDefaultWidth = false,
        dismissOnBackPress = enabled,
        dismissOnClickOutside = enabled,
    )) {
        Surface(Modifier.fillMaxWidth(tokens.WidthFraction), shape = RoundedCornerShape(tokens.Radius),
            color = CaixaDSColorTokens.White) {
            Column(Modifier.verticalScroll(rememberScrollState())
                .padding(start = tokens.Padding, end = tokens.Padding, top = tokens.Padding)) {
                Text(title, Modifier.semantics { heading() }, color = tokens.Blue,
                    fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                HorizontalDivider(Modifier.padding(top = 17.dp), color = tokens.Divider)
                BasicTextField(value = value, onValueChange = onValueChange, enabled = enabled,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                        .heightIn(min = 48.dp).semantics { contentDescription = hint },
                    textStyle = TextStyle(fontFamily = CaixaDSFontFamily, fontSize = 18.sp, color = tokens.Text),
                    cursorBrush = SolidColor(tokens.Blue), singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { if (enabled) onSave() }),
                    decorationBox = { field ->
                        Column {
                            Box(Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 4.dp),
                                contentAlignment = Alignment.CenterStart) {
                                if (value.isEmpty()) Text(hint, fontFamily = CaixaDSFontFamily,
                                    fontSize = 18.sp, color = tokens.Text)
                                field()
                            }
                            HorizontalDivider(color = tokens.Divider)
                        }
                    })
                errorMessage?.let {
                    Text(it, Modifier.padding(top = 4.dp), color = CaixaDS.colors.error, fontSize = 12.sp)
                }
                Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss, enabled = enabled, modifier = Modifier.height(tokens.ActionHeight)) {
                        Text("Cancelar", color = tokens.Text, fontFamily = CaixaDSFontFamily, fontSize = 16.sp)
                    }
                    TextButton(onClick = onSave, enabled = enabled, modifier = Modifier.height(tokens.ActionHeight)) {
                        Text("Salvar", color = tokens.Blue, fontFamily = CaixaDSFontFamily,
                            fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
