package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR

@Composable
internal fun FavoriteDialogActions(onConfirm: () -> Unit, onCancel: () -> Unit) {
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
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                style = LoteriasCaixaDS.typography.labelLarge.copy(
                    fontFamily = LoteriasCaixaDSRegularFontFamily,
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
                color = LoteriasCaixaDSColorTokens.ComboDark,
                style = LoteriasCaixaDS.typography.labelLarge.copy(
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.uiId("bet.0028")
            )
        }
    }
}
