package br.gov.caixa.loterias.feature.home.presentation.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.contract.account.model.AccountNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Geometria e cores de nova_popup_notificacao; sem WebView nem leiaute XML em execução. */
@Composable
fun RenderHomeNotificationPopup(notification: AccountNotification, onClose: () -> Unit, onOpenLink: () -> Unit) {
    val bitmap by produceState<Bitmap?>(null, notification.imageBase64) {
        value = null
        value = withContext(Dispatchers.Default) { decodeNotificationImage(notification.imageBase64) }
    }
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        val window = (LocalView.current.parent as DialogWindowProvider).window
        SideEffect { window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) }
        BoxWithConstraints(
            Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.NotificationScrim)
                .legacyAutomationId("home-notification-overlay"),
            contentAlignment = Alignment.Center
        ) {
            val shape = RoundedCornerShape(10.dp)
            // nova_popup_notificacao reserva 50dp de cada lado, independentemente dos outros diálogos.
            Column(
                Modifier.width((maxWidth - 100.dp).coerceAtLeast(0.dp)).heightIn(max = maxHeight * .7f)
                    .legacyAutomationId("home-notification-panel")
                    .background(LoteriasCaixaDSColorTokens.White, shape).border(.2.dp, LoteriasCaixaDSColorTokens.NotificationBlue, shape)
                    .uiId("home.0038").verticalScroll(rememberScrollState()).padding(vertical = 20.dp)
            ) {
                NotificationPopupContent(notification, bitmap, onClose, onOpenLink)
            }
        }
    }
}

/** Decodifica fora da thread da interface, com limites de bytes e dimensões e redução de amostragem antes da alocação. */
internal fun decodeNotificationImage(encoded: String?): Bitmap? {
    if (encoded.isNullOrEmpty() || encoded.length > MAX_NOTIFICATION_IMAGE_CHARACTERS) return null
    return try {
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth !in 1..MAX_NOTIFICATION_IMAGE_DIMENSION || bounds.outHeight !in 1..MAX_NOTIFICATION_IMAGE_DIMENSION) return null
        val options = BitmapFactory.Options().apply { inSampleSize = 1 }
        while (maxOf(bounds.outWidth, bounds.outHeight) / options.inSampleSize.coerceAtLeast(1) > NOTIFICATION_IMAGE_TARGET_DIMENSION) {
            options.inSampleSize = options.inSampleSize.coerceAtLeast(1) * IMAGE_SAMPLE_MULTIPLIER
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    } catch (_: IllegalArgumentException) {
        null
    }
}

private const val MAX_NOTIFICATION_IMAGE_CHARACTERS = 1_400_000
private const val MAX_NOTIFICATION_IMAGE_DIMENSION = 16_384
private const val NOTIFICATION_IMAGE_TARGET_DIMENSION = 1_600
private const val IMAGE_SAMPLE_MULTIPLIER = 2
