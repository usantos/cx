package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

/** Mensagem compartilhada por termos e FAQ, com o identificador publicado preservado. */
@Composable
fun SupportContentError(message: String) {
    Text(
        text = message,
        color = LoteriasCaixaDS.colors.error,
        modifier = Modifier.padding(SUPPORT_ERROR_PADDING.dp).uiId("help.0025")
    )
}

private const val SUPPORT_ERROR_PADDING = 16
