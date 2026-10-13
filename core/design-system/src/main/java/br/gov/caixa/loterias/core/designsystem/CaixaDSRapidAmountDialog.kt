package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/** Compose equivalent of custom_dialog_valor_aposta, used by the Rapidão amount presets. */
@Composable
fun CaixaDSRapidAmountDialog(
    title: String,
    value: String,
    hint: String,
    limits: String,
    errorMessage: String?,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    var fieldValue by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    LaunchedEffect(value) {
        if (fieldValue.text != value) fieldValue = TextFieldValue(value, TextRange(value.length))
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = CaixaDSColorTokens.White) {
            Column(Modifier.fillMaxWidth()) {
                Box(Modifier.fillMaxWidth().height(56.dp).background(Brush.horizontalGradient(listOf(CaixaDS.colors.legacyTeal, CaixaDS.colors.brand))).padding(start = 17.dp), contentAlignment = Alignment.CenterStart) {
                    Text(title, color = CaixaDSColorTokens.White, fontSize = 15.8.sp, fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold)
                }
                BasicTextField(fieldValue, { edit ->
                    if (edit.text == fieldValue.text) {
                        fieldValue = edit
                    } else {
                        val formatted = caixaDSMoneyInputFormat(edit.text)
                        fieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                        onValueChange(formatted)
                    }
                }, modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 40.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(fontFamily = CaixaDSFontFamily, fontSize = 18.sp, textAlign = TextAlign.Center, color = CaixaDS.colors.legacyText),
                    decorationBox = { field ->
                        Column {
                            Box(Modifier.fillMaxWidth().heightIn(min = 48.dp), contentAlignment = Alignment.Center) {
                                if (value.isBlank()) Text(hint, fontFamily = CaixaDSFontFamily, color = CaixaDS.colors.legacyMutedText, textAlign = TextAlign.Center)
                                field()
                            }
                            HorizontalDivider(color = CaixaDS.colors.legacyLine)
                        }
                    },
                )
                Text(limits, Modifier.fillMaxWidth().padding(10.dp), fontFamily = CaixaDSFontFamily, textAlign = TextAlign.Center, color = CaixaDSColorTokens.LegacyFavoriteCartCard)
                errorMessage?.let { Text(it, Modifier.padding(horizontal = 20.dp), color = CaixaDS.colors.error, fontSize = 12.sp, fontFamily = CaixaDSFontFamily) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onDismiss, Modifier.width(100.dp).height(55.dp)) { Text("Cancelar", color = CaixaDS.colors.legacyGraphite, fontSize = 15.8.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily) }
                    TextButton(onSave, Modifier.height(55.dp)) { Text("Salvar", color = CaixaDS.colors.legacyTeal, fontSize = 15.8.sp, fontWeight = FontWeight.Bold, fontFamily = CaixaDSFontFamily) }
                }
            }
        }
    }
}
