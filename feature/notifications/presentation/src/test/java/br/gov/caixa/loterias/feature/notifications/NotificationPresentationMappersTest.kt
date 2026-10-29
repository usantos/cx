package br.gov.caixa.loterias.feature.notifications

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationIcon
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationLegacyTextStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class NotificationPresentationMappersTest {
    @Test fun categoriesSelectTheirOwnNotificationIcon() {
        assertSame(CaixaDSIcons.NotificationPayments, notificationIcon(NotificationCategory.PAYMENTS_REDEMPTIONS))
        assertSame(CaixaDSIcons.NotificationMoney, notificationIcon(NotificationCategory.LUCK_REMINDERS))
        assertSame(CaixaDSIcons.NotificationResults, notificationIcon(NotificationCategory.RESULTS_AVAILABLE))
        assertSame(CaixaDSIcons.NotificationNews, notificationIcon(NotificationCategory.TIPS_NEWS))
        assertSame(CaixaDSIcons.NotificationSecurity, notificationIcon(NotificationCategory.SECURITY_ALERTS))
    }

    @Test fun legacyTextUsesFuturaFontPaddingAndRequestedSizeAndWeight() {
        val normal = notificationLegacyTextStyle(18.sp)
        assertEquals(CaixaDSFuturaFontFamily, normal.fontFamily)
        assertEquals(18.sp, normal.fontSize)
        assertEquals(FontWeight.Normal, normal.fontWeight)
        assertEquals(true, normal.platformStyle?.paragraphStyle?.includeFontPadding)
        val bold = notificationLegacyTextStyle(14.sp, FontWeight.Bold)
        assertEquals(14.sp, bold.fontSize)
        assertEquals(FontWeight.Bold, bold.fontWeight)
        assertEquals(true, bold.platformStyle?.paragraphStyle?.includeFontPadding)
    }
}
