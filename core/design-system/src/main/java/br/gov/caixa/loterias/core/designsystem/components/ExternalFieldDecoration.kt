package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.error
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

internal fun externalFieldBorderColor(focused: Boolean, error: Boolean) = when {
    focused && error -> LoteriasCaixaDSColorTokens.AccountError
    focused -> LoteriasCaixaDSColorTokens.AccountOutline
    else -> LoteriasCaixaDSColorTokens.AccountUnfocusedOutline
}

@Composable
internal fun ExternalFieldDecoration(value: String, placeholder: String, inner: @Composable () -> Unit) {
    if (value.isBlank()) {
        Text(
            placeholder,
            color = LoteriasCaixaDSColorTokens.AccountOutline,
            fontSize = ExternalFieldTokens.fontSize,
            style = LoteriasCaixaDSAccountTextStyle
        )
    }
    inner()
}
