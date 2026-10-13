package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily

/** Compose/DS rendering of layout_dialog_favoritar.xml. */
@Composable
internal fun BettingFavoriteDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val change = rememberUpdatedState(onNameChange)
    val confirm = rememberUpdatedState(onConfirm)
    val cancel = rememberUpdatedState(onCancel)
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(.85f),
            shape = RoundedCornerShape(8.dp),
            color = CaixaDSColorTokens.White,
            shadowElevation = 16.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp),
            ) {
                Text(
                    text = "Favoritar aposta",
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(focusRequester)
                        .focusable()
                        .semantics { heading() },
                    color = CaixaDSColorTokens.LegacyComboDark,
                    style = CaixaDS.typography.bodyLarge.copy(
                        fontFamily = CaixaDSLegacyRegularFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = TextStyle.Default.lineHeight,
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                    ),
                )
                Box(
                    Modifier.fillMaxWidth().padding(top = 17.dp).height(1.dp)
                        .background(CaixaDSColorTokens.LegacySimulationMuted),
                )
                Text(
                    text = "Informe um nome para salvar sua aposta favorita:",
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    color = CaixaDSColorTokens.LegacySocialGraphite,
                    style = CaixaDS.typography.bodyLarge.copy(
                        fontFamily = CaixaDSLegacyRegularFontFamily,
                        fontSize = 16.sp,
                        lineHeight = TextStyle.Default.lineHeight,
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                    ),
                )
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                        .height(48.dp)
                        .border(1.dp, CaixaDSColorTokens.LegacySimulationMuted, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (name.isEmpty()) {
                        Text(
                            text = "Ex: Aposta do João",
                            color = CaixaDS.colors.legacyMutedText,
                            style = CaixaDS.typography.bodyLarge.copy(
                                fontFamily = CaixaDSLegacyRegularFontFamily,
                                fontSize = 16.sp,
                                lineHeight = TextStyle.Default.lineHeight,
                                platformStyle = PlatformTextStyle(includeFontPadding = true),
                            ),
                        )
                    }
                    BasicTextField(
                        value = name,
                        onValueChange = { change.value(it) },
                        modifier = Modifier.fillMaxWidth().testTag("favorite-name-input"),
                        singleLine = true,
                        textStyle = CaixaDS.typography.bodyLarge.copy(
                            color = CaixaDSColorTokens.LegacySocialGraphite,
                            fontFamily = CaixaDSLegacyRegularFontFamily,
                            fontSize = 16.sp,
                            lineHeight = TextStyle.Default.lineHeight,
                            platformStyle = PlatformTextStyle(includeFontPadding = true),
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboard?.hide()
                            confirm.value()
                        }),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = { keyboard?.hide(); cancel.value() },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                    ) {
                        Text(
                            text = "Cancelar",
                            color = CaixaDSColorTokens.LegacySocialGraphite,
                            style = CaixaDS.typography.labelLarge.copy(
                                fontFamily = CaixaDSLegacyRegularFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                    TextButton(
                        onClick = { keyboard?.hide(); confirm.value() },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                    ) {
                        Text(
                            text = "Confirmar",
                            color = CaixaDSColorTokens.LegacyComboDark,
                            style = CaixaDS.typography.labelLarge.copy(
                                fontFamily = CaixaDSFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }
            }
        }
    }
}
