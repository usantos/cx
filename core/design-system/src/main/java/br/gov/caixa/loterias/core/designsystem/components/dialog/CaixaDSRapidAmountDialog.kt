package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.input.caixaDSMoneyInputFormat
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** Equivalente Compose de custom_dialog_valor_aposta, usado pelos valores predefinidos do Rapidão. */
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
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.caixaDSModalWidth(), color = CaixaDSColorTokens.White) {
            Column(Modifier.fillMaxWidth().uiId("ds.0082").verticalScroll(rememberScrollState())) {
                RapidAmountHeader(title)
                BasicTextField(
                    fieldValue,
                    { edit ->
                        if (edit.text == fieldValue.text) {
                            fieldValue = edit
                        } else {
                            val formatted = caixaDSMoneyInputFormat(edit.text)
                            fieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                            onValueChange(formatted)
                        }
                    },
                    modifier = (
                        Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 40.dp).semantics {
                            contentDescription = hint
                            if (!errorMessage.isNullOrBlank()) error(errorMessage)
                        }
                        ).uiId("ds.0083"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(
                        fontFamily = CaixaDSFontFamily,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = CaixaDS.colors.text
                    ),
                    decorationBox = { field ->
                        Column {
                            Box(Modifier.fillMaxWidth().heightIn(min = 48.dp), contentAlignment = Alignment.Center) {
                                if (value.isBlank()) {
                                    Text(
                                        hint,
                                        fontFamily = CaixaDSFontFamily,
                                        color = CaixaDS.colors.mutedText,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                field()
                            }
                            HorizontalDivider(color = CaixaDS.colors.line)
                        }
                    }
                )
                Text(
                    limits,
                    Modifier.fillMaxWidth().padding(10.dp),
                    fontFamily = CaixaDSFontFamily,
                    textAlign = TextAlign.Center,
                    color = CaixaDSColorTokens.TutorialGreenButton
                )
                errorMessage?.let {
                    Text(
                        it,
                        Modifier.padding(horizontal = 20.dp).semantics { liveRegion = LiveRegionMode.Polite },
                        color = CaixaDS.colors.error,
                        fontSize = 12.sp,
                        fontFamily = CaixaDSFontFamily
                    )
                }
                RapidAmountActions(onDismiss, onSave)
            }
        }
    }
}

@Composable
private fun RapidAmountActions(onDismiss: () -> Unit, onSave: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val enlarged = LocalDensity.current.fontScale > 1f
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        val cancelModifier = if (enlarged) Modifier.weight(1f).heightIn(min = 55.dp) else Modifier.width(100.dp).height(55.dp)
        val saveModifier = if (enlarged) Modifier.weight(1f).heightIn(min = 55.dp) else Modifier.height(55.dp)
        TextButton(
            onDismiss,
            (cancelModifier).uiId("ds.0084")
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
                modifier = if (enlarged) Modifier.fillMaxWidth() else Modifier,
                textAlign = if (enlarged) TextAlign.Center else null,
                color = CaixaDS.colors.graphite,
                fontSize = 15.8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CaixaDSFontFamily
            )
        }
        TextButton(
            onSave,
            (saveModifier).uiId("ds.0085")
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_salvar_aef7cd),
                modifier = if (enlarged) Modifier.fillMaxWidth() else Modifier,
                textAlign = if (enlarged) TextAlign.Center else null,
                color = CaixaDSColorTokens.TutorialGreenButton,
                fontSize = 15.8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CaixaDSFontFamily
            )
        }
    }
}

@Composable
private fun RapidAmountHeader(title: String) {
    Box(
        Modifier.fillMaxWidth().height(56.dp).background(CaixaDSColorTokens.Blue700).padding(start = 17.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            title,
            color = CaixaDSColorTokens.White,
            fontSize = 15.8.sp,
            fontFamily = CaixaDSFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}
