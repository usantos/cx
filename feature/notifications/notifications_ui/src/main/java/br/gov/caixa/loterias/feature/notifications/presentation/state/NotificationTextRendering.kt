package br.gov.caixa.loterias.feature.notifications.presentation.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun NotificationText.resolve(): String = when (this) {
    is NotificationText.Resource -> stringResource(id)
    is NotificationText.Plain -> value
}
