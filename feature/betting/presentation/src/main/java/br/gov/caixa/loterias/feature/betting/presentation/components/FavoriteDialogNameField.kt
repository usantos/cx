package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR

@Composable
internal fun FavoriteDialogNameField(name: String, onNameChange: (String) -> Unit, onConfirm: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val keyboard = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            .height(48.dp)
            .border(1.dp, LoteriasCaixaDSColorTokens.SimulationMuted, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (name.isEmpty()) {
            Text(
                text = uiStrings.getString(LocalizedR.string.ui_ex_aposta_do_joao_9f84d5),
                color = LoteriasCaixaDS.colors.mutedText,
                style = LoteriasCaixaDS.typography.bodyLarge.copy(
                    fontFamily = LoteriasCaixaDSRegularFontFamily,
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
            textStyle = LoteriasCaixaDS.typography.bodyLarge.copy(
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                fontFamily = LoteriasCaixaDSRegularFontFamily,
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
