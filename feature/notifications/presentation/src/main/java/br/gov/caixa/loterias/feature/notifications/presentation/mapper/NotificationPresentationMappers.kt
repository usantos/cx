package br.gov.caixa.loterias.feature.notifications.presentation.mapper

import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory

internal fun notificationIcon(category: NotificationCategory): ImageVector = when (category) {
    NotificationCategory.PAYMENTS_REDEMPTIONS -> CaixaDSIcons.NotificationPayments
    NotificationCategory.LUCK_REMINDERS -> CaixaDSIcons.NotificationMoney
    NotificationCategory.RESULTS_AVAILABLE -> CaixaDSIcons.NotificationResults
    NotificationCategory.TIPS_NEWS -> CaixaDSIcons.NotificationNews
    NotificationCategory.SECURITY_ALERTS -> CaixaDSIcons.NotificationSecurity
}

internal fun notificationLegacyTextStyle(size: TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFuturaFontFamily,
    fontSize = size,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)
