package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSChoiceRow
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSSelectionDialog(
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
            modifier = Modifier.loteriasCaixaDSModalWidth(),
            shape = RoundedCornerShape(4.dp),
            color = LoteriasCaixaDSColorTokens.White
        ) {
            Column(
                Modifier.fillMaxWidth().uiId("ds.0090").verticalScroll(
                    rememberScrollState()
                ).padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp)
            ) {
                Text(title, color = LoteriasCaixaDS.colors.brand, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider(
                    Modifier.fillMaxWidth().padding(top = 17.dp),
                    thickness = 1.dp,
                    color = LoteriasCaixaDSColorTokens.TutorialDivider
                )
                Text(
                    message,
                    Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
                    color = LoteriasCaixaDSColorTokens.SocialGraphite,
                    fontSize = 16.sp
                )
                Column(Modifier.selectableGroup()) {
                    options.forEachIndexed { index, option ->
                        LoteriasCaixaDSChoiceRow(
                            option,
                            selected = index == selectedIndex,
                            onClick = { onSelect(index) },
                            modifier = Modifier
                                .uiId("ds.0091")
                        )
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                    SelectionDialogAction(dismissLabel, onDismiss, false, modifier = Modifier.uiId("ds.0092"))
                    SelectionDialogAction(confirmLabel, onConfirm, true, modifier = Modifier.uiId("ds.0093"))
                }
            }
        }
    }
}
