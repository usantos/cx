package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.descriptionResource
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationLegacyTextStyle
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.titleResource
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState

@Composable
internal fun PreferencesScreen(
    state: NotificationsUiState,
    onTogglePreference: (NotificationCategory?, Boolean) -> Unit,
    onBack: () -> Unit,
    onHelp: () -> Unit,
) {
    CaixaDSScaffold(
        title = stringResource(R.string.notifications_preferencias_de_notificacoes),
        onBack = onBack,

        topBarAction = {
            Box(
                Modifier.size(45.dp).uiId("inbox.0042").clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onHelp
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    CaixaDSIcons.Help,
                    contentDescription = stringResource(R.string.notifications_termos_de_uso),
                    tint = CaixaDSColorTokens.White,
                    modifier = (Modifier.size(24.dp)).uiId("inbox.0043")
                )
            }
        },
        modifier = Modifier.uiId("inbox.0044")
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.surface)) {
            Text(
                stringResource(R.string.notifications_escolha_os_assuntos_que_deseja_receber_notificacoes_no_seu_app),
                (Modifier.fillMaxWidth().padding(16.dp)).uiId("inbox.0045"),
                color = CaixaDSColorTokens.NotificationIntro,
                style = notificationLegacyTextStyle(18.sp)
            )
            LazyColumn((Modifier.fillMaxSize()).uiId("inbox.0046")) {
                items(state.preferences, key = { it.category?.name ?: "ALL" }) { preference ->
                    PreferenceRow(preference) { enabled ->
                        onTogglePreference(preference.category, enabled)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceRow(preference: NotificationPreference, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .uiId("inbox.0047").toggleable(value = preference.enabled, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(start = 20.dp, end = 10.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(preference.category.titleResource()),
                color = CaixaDSColorTokens.NotificationText,
                style = notificationLegacyTextStyle(14.sp, FontWeight.Bold),
                modifier = Modifier.uiId("inbox.0048")
            )
            preference.category?.let { category ->
                Text(
                    stringResource(category.descriptionResource()),
                    (Modifier.padding(top = 8.dp)).uiId("inbox.0049"),
                    color = CaixaDSColorTokens.NotificationText,
                    style = notificationLegacyTextStyle(14.sp)
                )
            }
        }
        LegacyNotificationSwitch(preference.enabled)
    }
    Box(Modifier.fillMaxWidth().height(0.2.dp).background(CaixaDSColorTokens.NotificationDivider))
}

@Composable
private fun LegacyNotificationSwitch(checked: Boolean) {
    val thumbOffset by animateDpAsState(if (checked) 12.dp else 2.dp, label = "notification-switch-thumb")
    Box(
        Modifier
            .size(width = 34.dp, height = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(if (checked) CaixaDSColorTokens.Orange100 else CaixaDSColorTokens.NotificationSwitchTrack)
        )
        Box(
            Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                .clip(CircleShape)
                .background(if (checked) CaixaDSColorTokens.Orange500 else CaixaDSColorTokens.NotificationSwitchThumb)
        )
    }
}
