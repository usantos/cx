package br.gov.caixa.loterias.feature.notifications.presentation.mapper

import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory

internal fun notificationIcon(category: NotificationCategory): ImageVector = when (category) {
    NotificationCategory.PAYMENTS_REDEMPTIONS -> LoteriasCaixaDSIcons.NotificationPayments
    NotificationCategory.LUCK_REMINDERS -> LoteriasCaixaDSIcons.NotificationMoney
    NotificationCategory.RESULTS_AVAILABLE -> LoteriasCaixaDSIcons.NotificationResults
    NotificationCategory.TIPS_NEWS -> LoteriasCaixaDSIcons.NotificationNews
    NotificationCategory.SECURITY_ALERTS -> LoteriasCaixaDSIcons.NotificationSecurity
}

internal fun notificationLegacyTextStyle(size: TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = LoteriasCaixaDSFuturaFontFamily,
    fontSize = size,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)
