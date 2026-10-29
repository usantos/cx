package br.gov.caixa.loterias.feature.betting.presentation.components

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSSurface as Surface
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR

/** Renderização Compose/DS de layout_dialog_favoritar.xml. */
@Composable
internal fun BettingFavoriteDialog(name: String, onNameChange: (String) -> Unit, onConfirm: () -> Unit, onCancel: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.caixaDSModalWidth(),
            shape = RoundedCornerShape(8.dp),
            color = CaixaDSColorTokens.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp)
            ) {
                FavoriteDialogHeading(focusRequester)
                FavoriteDialogNameField(name, onNameChange, onConfirm)
                FavoriteDialogActions(onConfirm, onCancel)
            }
        }
    }
}

@Composable
private fun FavoriteDialogHeading(focusRequester: FocusRequester) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        text = uiStrings.getString(LocalizedR.string.ui_favoritar_aposta_a34b1e),
        modifier = (
            Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable()
                .semantics { heading() }
            ).uiId("bet.0021"),
        color = CaixaDSColorTokens.ComboDark,
        style = CaixaDS.typography.bodyLarge.copy(
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = TextStyle.Default.lineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
    Box(
        Modifier.fillMaxWidth().padding(top = 17.dp).height(1.dp)
            .background(CaixaDSColorTokens.SimulationMuted)
    )
    Text(
        text = uiStrings.getString(LocalizedR.string.ui_informe_um_nome_para_salvar_sua_apos_2c27af),
        modifier = (Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("bet.0022"),
        color = CaixaDSColorTokens.SocialGraphite,
        style = CaixaDS.typography.bodyLarge.copy(
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 16.sp,
            lineHeight = TextStyle.Default.lineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
}

@Composable
private fun FavoriteDialogNameField(name: String, onNameChange: (String) -> Unit, onConfirm: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val keyboard = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            .height(48.dp)
            .border(1.dp, CaixaDSColorTokens.SimulationMuted, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (name.isEmpty()) {
            Text(
                text = uiStrings.getString(LocalizedR.string.ui_ex_aposta_do_joao_9f84d5),
                color = CaixaDS.colors.mutedText,
                style = CaixaDS.typography.bodyLarge.copy(
                    fontFamily = CaixaDSRegularFontFamily,
                    fontSize = 16.sp,
                    lineHeight = TextStyle.Default.lineHeight,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                ),
                modifier = Modifier.uiId("bet.0023")
            )
        }
        BasicTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = (Modifier.fillMaxWidth().legacyAutomationId("favorite-name-input")).uiId("bet.0024"),
            singleLine = true,
            textStyle = CaixaDS.typography.bodyLarge.copy(
                color = CaixaDSColorTokens.SocialGraphite,
                fontFamily = CaixaDSRegularFontFamily,
                fontSize = 16.sp,
                lineHeight = TextStyle.Default.lineHeight,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboard?.hide()
                onConfirm()
            })
        )
    }
}

@Composable
private fun FavoriteDialogActions(onConfirm: () -> Unit, onCancel: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val keyboard = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = {
                keyboard?.hide()
                onCancel()
            },
            modifier = (Modifier.defaultMinSize(minHeight = 48.dp)).uiId("bet.0025")
        ) {
            Text(
                text = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
                color = CaixaDSColorTokens.SocialGraphite,
                style = CaixaDS.typography.labelLarge.copy(
                    fontFamily = CaixaDSRegularFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.uiId("bet.0026")
            )
        }
        TextButton(
            onClick = {
                keyboard?.hide()
                onConfirm()
            },
            modifier = (Modifier.defaultMinSize(minHeight = 48.dp)).uiId("bet.0027")
        ) {
            Text(
                text = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
                color = CaixaDSColorTokens.ComboDark,
                style = CaixaDS.typography.labelLarge.copy(
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.uiId("bet.0028")
            )
        }
    }
}
