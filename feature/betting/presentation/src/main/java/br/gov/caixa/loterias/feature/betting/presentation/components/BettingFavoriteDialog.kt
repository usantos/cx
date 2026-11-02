package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSSurface as Surface
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

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
            modifier = Modifier.loteriasCaixaDSModalWidth(),
            shape = RoundedCornerShape(8.dp),
            color = LoteriasCaixaDSColorTokens.White,
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
