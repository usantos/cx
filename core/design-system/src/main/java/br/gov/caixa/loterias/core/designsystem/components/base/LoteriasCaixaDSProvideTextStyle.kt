@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.core.designsystem.components.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

/** Contrato básico de ProvideTextStyle; a renderização e os padrões pertencem exclusivamente ao LoteriasCaixaDS. */
@Composable
fun LoteriasCaixaDSProvideTextStyle(value: TextStyle, content: @Composable () -> Unit) {
    androidx.compose.material3.ProvideTextStyle(
        value = value,
        content = content
    )
}
