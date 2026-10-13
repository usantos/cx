package br.gov.caixa.loterias.feature.notifications

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.domain.notifications.AppNotification
import br.gov.caixa.loterias.core.domain.notifications.NotificationCategory
import br.gov.caixa.loterias.core.domain.notifications.NotificationPreference
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationsRoute(
    route: String,
    viewModel: NotificationsViewModel,
    onBack: () -> Unit,
    onHelp: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is NotificationEffect.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${effect.title} <br>${effect.content}")
                    }
                    context.startActivity(Intent.createChooser(intent, "Compartilhar mensagem"))
                }
            }
        }
    }

    when (route) {
        "notifications/messages" -> MessagesScreen(state, viewModel, onBack)
        "notifications/preferences" -> PreferencesScreen(state, viewModel, onBack, onHelp)
        else -> error("Rota de notificações sem layout Compose: $route")
    }

    NotificationDialogs(state, viewModel)
}

@Composable
private fun MessagesScreen(
    state: NotificationsUiState,
    viewModel: NotificationsViewModel,
    onBack: () -> Unit,
) {
    CaixaDSLegacyScaffold(
        title = "Notificações",
        onBack = onBack,
        topBarTitleSize = 16.sp,
        topBarTitleWeight = FontWeight.Normal,
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.LegacyNotificationList)) {
            Row(
                Modifier.fillMaxWidth().background(CaixaDSColorTokens.LegacyNotificationSearch)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.width(20.dp))
                BasicTextField(
                    value = state.query,
                    onValueChange = viewModel::updateQuery,
                    modifier = Modifier.weight(1f).height(40.dp),
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, letterSpacing = 0.56.sp, color = CaixaDSColorTokens.LegacyNotificationText, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    decorationBox = { field ->
                        Row(
                            Modifier.fillMaxSize().border(1.dp, CaixaDS.colors.legacyMutedText, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(CaixaDSIcons.Search, contentDescription = null, tint = CaixaDSColorTokens.LegacyNotificationText, modifier = Modifier.size(20.dp))
                            Box(Modifier.padding(start = 8.dp).weight(1f)) {
                                if (state.query.isBlank()) Text("Pesquisar", color = CaixaDSColorTokens.LegacyNotificationText, fontFamily = FontFamily.SansSerif, fontSize = 14.sp, letterSpacing = 0.56.sp)
                                field()
                            }
                        }
                    },
                )
                Spacer(Modifier.width(32.dp))
                IconButton(
                    onClick = viewModel::requestMarkAllRead,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        CaixaDSIcons.NotificationRead,
                        "Marcar todas as mensagens como lidas",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(36.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(
                    onClick = viewModel::requestDeleteAll,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        CaixaDSIcons.NotificationDelete,
                        "Excluir todas as mensagens",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(36.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
            }

            Row(Modifier.fillMaxWidth().height(56.dp).background(CaixaDSColorTokens.LegacyNotificationSearch)) {
                NotificationTab(
                    label = "Todas",
                    selected = state.filter == NotificationFilter.ALL,
                    onClick = { viewModel.selectFilter(NotificationFilter.ALL) },
                    modifier = Modifier.weight(1f),
                )
                NotificationTab(
                    label = "Não Lidas",
                    selected = state.filter == NotificationFilter.UNREAD,
                    onClick = { viewModel.selectFilter(NotificationFilter.UNREAD) },
                    modifier = Modifier.weight(1f),
                )
            }

            if (!state.loading && state.visibleMessages.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        state.emptyMessage,
                        color = CaixaDS.colors.legacyText,
                        style = notificationLegacyTextStyle(16.sp, FontWeight.Bold),
                        fontWeight = FontWeight.Bold,
                    )
                }
            } else {
                LazyColumn(Modifier.fillMaxSize().background(CaixaDSColorTokens.LegacyNotificationList)) {
                    items(state.visibleMessages, key = AppNotification::id) { message ->
                        SwipeableNotificationItem(
                            message = message,
                            expanded = state.expandedMessageId == message.id,
                            onToggle = { viewModel.toggleExpanded(message) },
                            onRead = { viewModel.requestMarkRead(message.id) },
                            onDelete = { viewModel.requestDelete(message.id) },
                            onShare = { viewModel.share(message) },
                            onCategoryAction = { viewModel.performCategoryAction(message) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxHeight().clickable(onClick = onClick)
            .background(if (selected) CaixaDS.colors.surface else CaixaDSColorTokens.LegacyNotificationSearch),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = CaixaDS.colors.legacyText, style = notificationLegacyTextStyle(14.sp))
    }
}

@Composable
private fun SwipeableNotificationItem(
    message: AppNotification,
    expanded: Boolean,
    onToggle: () -> Unit,
    onRead: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onCategoryAction: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val itemWidth = maxWidth
        val scrollState = rememberScrollState()
        val scope = rememberCoroutineScope()
        Row(Modifier.horizontalScroll(scrollState)) {
            NotificationItem(
                message = message,
                expanded = expanded,
                onToggle = {
                    if (scrollState.value > 0) scope.launch { scrollState.animateScrollTo(0) }
                    else onToggle()
                },
                onShare = onShare,
                onCategoryAction = onCategoryAction,
                modifier = Modifier.width(itemWidth),
            )
            SwipeAction(
                icon = br.gov.caixa.loterias.core.designsystem.CaixaDSNotificationSwipeRead,
                description = "Marcar mensagem como lida",
                background = CaixaDSColorTokens.LegacyNotificationReadAction,
                onClick = onRead,
            )
            SwipeAction(
                icon = br.gov.caixa.loterias.core.designsystem.CaixaDSNotificationSwipeDelete,
                description = "Excluir mensagem",
                background = CaixaDSColorTokens.LegacyNotificationDeleteAction,
                onClick = onDelete,
            )
        }
    }
}

@Composable
private fun SwipeAction(
    icon: ImageVector,
    description: String,
    background: Color,
    onClick: () -> Unit,
) {
    Box(
        Modifier.width(60.dp).fillMaxHeight().background(background).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, tint = Color.Unspecified, modifier = Modifier.size(icon.defaultWidth, icon.defaultHeight))
    }
}

@Composable
private fun NotificationItem(
    message: AppNotification,
    expanded: Boolean,
    onToggle: () -> Unit,
    onShare: () -> Unit,
    onCategoryAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (message.isRead || expanded) {
        CaixaDS.colors.surface
    } else {
        CaixaDSColorTokens.LegacyNotificationSearch
    }
    Column(modifier.background(background).padding(8.dp)) {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                notificationIcon(message.category),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp).align(Alignment.CenterVertically),
            )
            Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(
                    message.title,
                    color = if (message.isRead || expanded) CaixaDSColorTokens.Black else CaixaDSColorTokens.LegacyNotificationUnreadTitle,
                    style = notificationLegacyTextStyle(16.sp, FontWeight.Bold),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // bind() in the legacy holder retains the preview while the full body is expanded.
                    Text(
                        message.content,
                        color = CaixaDSColorTokens.LegacyNotificationText,
                        style = notificationLegacyTextStyle(16.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
            }
            Text(message.dateLabel, color = CaixaDSColorTokens.LegacyNotificationText, style = notificationLegacyTextStyle(14.sp))
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                Modifier.fillMaxWidth().background(CaixaDS.colors.surface)
                    .padding(bottom = 8.dp),
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(
                        message.content,
                        modifier = Modifier.padding(start = 40.dp).width(250.dp).padding(12.dp),
                        color = CaixaDS.colors.legacyText,
                        style = CaixaDS.typography.bodySmall.copy(fontFamily = CaixaDSLegacyFuturaFontFamily),
                        fontWeight = FontWeight.Bold,
                    )
                    if (message.category.canShare()) {
                        IconButton(onClick = onShare, modifier = Modifier.padding(start = 20.dp).size(28.dp)) {
                            Icon(
                                CaixaDSIcons.NotificationShare,
                                "Compartilhar mensagem",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                }
                message.category.primaryAction()?.let { action ->
                    Button(
                        onClick = onCategoryAction,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (message.category == NotificationCategory.RESULTS_AVAILABLE) {
                                CaixaDSColorTokens.LegacyNotificationResultsAction
                            } else {
                                CaixaDS.colors.brand
                            },
                        ),
                    ) { Text(action, fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun PreferencesScreen(
    state: NotificationsUiState,
    viewModel: NotificationsViewModel,
    onBack: () -> Unit,
    onHelp: () -> Unit,
) {
    CaixaDSLegacyScaffold(
        title = "Preferências de Notificações",
        onBack = onBack,
        topBarTitleSize = 18.sp,
        topBarTitleWeight = FontWeight.Normal,
        topBarAnnotatedTitle = buildAnnotatedString {
            append("Preferências de ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Notificações") }
        },
        topBarAction = {
            Box(
                Modifier.size(45.dp).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onHelp,
                ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    CaixaDSIcons.Help,
                    contentDescription = "Termos de Uso",
                    tint = CaixaDSColorTokens.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.surface)) {
            Text(
                "Escolha os assuntos que deseja receber notificações no seu App:",
                Modifier.fillMaxWidth().padding(16.dp),
                color = CaixaDSColorTokens.LegacyNotificationIntro,
                style = notificationLegacyTextStyle(18.sp),
            )
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.preferences, key = { it.category?.name ?: "ALL" }) { preference ->
                    PreferenceRow(preference) { enabled ->
                        viewModel.togglePreference(preference.category, enabled)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceRow(preference: NotificationPreference, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 20.dp, end = 10.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                preference.title,
                color = CaixaDSColorTokens.LegacyNotificationText,
                style = notificationLegacyTextStyle(14.sp, FontWeight.Bold),
            )
            if (preference.description.isNotBlank()) {
                Text(
                    preference.description,
                    Modifier.padding(top = 8.dp),
                    color = CaixaDSColorTokens.LegacyNotificationText,
                    style = notificationLegacyTextStyle(14.sp),
                )
            }
        }
        LegacyNotificationSwitch(preference.enabled, onCheckedChange)
    }
    Box(Modifier.fillMaxWidth().height(0.2.dp).background(CaixaDSColorTokens.LegacyNotificationDivider))
}

@Composable
private fun LegacyNotificationSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val thumbOffset by animateDpAsState(if (checked) 12.dp else 2.dp, label = "notification-switch-thumb")
    Box(
        Modifier
            .size(width = 34.dp, height = 24.dp)
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(if (checked) CaixaDSColorTokens.Orange100 else CaixaDSColorTokens.LegacyNotificationSwitchTrack),
        )
        Box(
            Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                .clip(CircleShape)
                .background(if (checked) CaixaDSColorTokens.Orange500 else CaixaDSColorTokens.LegacyNotificationSwitchThumb),
        )
    }
}

@Composable
private fun NotificationDialogs(state: NotificationsUiState, viewModel: NotificationsViewModel) {
    state.pendingConfirmation?.let { confirmation ->
        val message = when (confirmation) {
            NotificationConfirmation.DeleteAll -> "Deseja excluir todas a mensagens ?"
            NotificationConfirmation.MarkAllRead -> "Deseja marcar todas a mensagens como lidas ?"
            is NotificationConfirmation.DeleteOne -> "Deseja excluir esta mensagem ?"
            is NotificationConfirmation.MarkOneRead -> "Deseja marcar esta mensagem como lida ?"
        }
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = message,
            positiveLabel = "Sim",
            onPositive = viewModel::confirmDialog,
            negativeLabel = "Não",
            onNegative = viewModel::dismissDialog,
        )
    }
    state.categoryActionMessage?.let { message ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = message,
            positiveLabel = "Entendi",
            onPositive = viewModel::dismissDialog,
        )
    }
    state.errorMessage?.let { message ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = message,
            positiveLabel = "Entendi",
            onPositive = viewModel::dismissDialog,
        )
    }
}

private fun notificationIcon(category: NotificationCategory): ImageVector = when (category) {
    NotificationCategory.PAYMENTS_REDEMPTIONS -> CaixaDSIcons.NotificationPayments
    NotificationCategory.LUCK_REMINDERS -> CaixaDSIcons.NotificationMoney
    NotificationCategory.RESULTS_AVAILABLE -> CaixaDSIcons.NotificationResults
    NotificationCategory.TIPS_NEWS -> CaixaDSIcons.NotificationNews
    NotificationCategory.SECURITY_ALERTS -> CaixaDSIcons.NotificationSecurity
}

private fun notificationLegacyTextStyle(size: TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = size, fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)
