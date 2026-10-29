package br.gov.caixa.loterias.feature.notifications.presentation.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface NotificationText {
    data class Resource(val id: Int) : NotificationText
    data class Plain(val value: String) : NotificationText
}

@Composable
internal fun NotificationText.resolve(): String = when (this) {
    is NotificationText.Resource -> stringResource(id)
    is NotificationText.Plain -> value
}
