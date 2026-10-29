package br.gov.caixa.loterias.feature.home.presentation.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Geometria e cores de nova_popup_notificacao; sem WebView nem leiaute XML em execução. */
@Composable
fun HomeNotificationPopup(notification: AccountNotification, onClose: () -> Unit, onOpenLink: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
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
            Modifier.fillMaxSize().background(CaixaDSColorTokens.NotificationScrim)
                .legacyAutomationId("home-notification-overlay"),
            contentAlignment = Alignment.Center
        ) {
            val shape = RoundedCornerShape(10.dp)
            // nova_popup_notificacao reserva 50dp de cada lado, independentemente dos outros diálogos.
            Column(
                Modifier.width((maxWidth - 100.dp).coerceAtLeast(0.dp)).heightIn(max = maxHeight * .7f)
                    .legacyAutomationId("home-notification-panel")
                    .background(CaixaDSColorTokens.White, shape).border(.2.dp, CaixaDSColorTokens.NotificationBlue, shape)
                    .uiId("home.0038").verticalScroll(rememberScrollState()).padding(vertical = 20.dp)
            ) {
                val imageModifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 20.dp)
                val image = bitmap
                if (image == null) {
                    Image(CaixaDSNotificationPlaceholder, null, imageModifier)
                } else {
                    Image(image.asImageBitmap(), null, imageModifier)
                }
                Text(
                    notification.title.orEmpty(),
                    (Modifier.fillMaxWidth().padding(start = 36.dp, end = 36.dp, top = 8.dp))
                        .uiId("home.0039"),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                        fontFamily = CaixaDSFuturaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = CaixaDSColorTokens.NotificationBlue,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    notification.content.orEmpty(),
                    (Modifier.fillMaxWidth().padding(start = 36.dp, end = 36.dp, top = 8.dp))
                        .uiId("home.0040"),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                        fontFamily = CaixaDSFuturaFontFamily,
                        fontSize = 14.sp,
                        color = CaixaDSColorTokens.NotificationBlue,
                        textAlign = TextAlign.Center
                    )
                )
                val hasLink = !notification.link.isNullOrEmpty()
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (hasLink) Arrangement.SpaceBetween else Arrangement.Center
                ) {
                    NotificationButton("Fechar", onClose)
                    if (hasLink) NotificationButton(uiStrings.getString(LocalizedR.string.ui_abrir_link_9b8abd), onOpenLink)
                }
            }
        }
    }
}

@Composable
private fun NotificationButton(label: String, onClick: () -> Unit) {
    Box(
        Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 48.dp)
            .background(CaixaDSColorTokens.NotificationAction, RoundedCornerShape(5.dp))
            .uiId("home.0041").clickable(role = Role.Button, onClick = onClick).padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = true),
                fontFamily = CaixaDSFuturaFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CaixaDSColorTokens.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .uiId("home.0042")
        )
    }
}

/** Decodifica fora da thread da interface, com limites de bytes e dimensões e redução de amostragem antes da alocação. */
internal fun decodeNotificationImage(encoded: String?): Bitmap? {
    if (encoded.isNullOrEmpty() || encoded.length > 1_400_000) return null
    return try {
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth !in 1..16_384 || bounds.outHeight !in 1..16_384) return null
        val options = BitmapFactory.Options().apply { inSampleSize = 1 }
        while (maxOf(bounds.outWidth, bounds.outHeight) / options.inSampleSize.coerceAtLeast(1) > 1600) {
            options.inSampleSize = options.inSampleSize.coerceAtLeast(1) * 2
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    } catch (_: IllegalArgumentException) {
        null
    }
}
