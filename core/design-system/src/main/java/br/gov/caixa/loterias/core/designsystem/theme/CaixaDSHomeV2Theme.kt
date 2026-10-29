package br.gov.caixa.loterias.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

/** Métricas Material3 do HomeV2, respeitando a escala de fonte escolhida no sistema. */
@Composable
fun CaixaDSHomeV2Theme(content: @Composable () -> Unit) {
    MaterialTheme(typography = Typography(), content = content)
}
