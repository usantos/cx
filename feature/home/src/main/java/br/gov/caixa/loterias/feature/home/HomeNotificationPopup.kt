package br.gov.caixa.loterias.feature.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.domain.account.AccountNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Geometry/colors from nova_popup_notificacao; no WebView or runtime layout XML. */
@Composable
fun HomeNotificationPopup(notification: AccountNotification, onClose: () -> Unit, onOpenLink: () -> Unit) {
    val bitmap by produceState<Bitmap?>(null, notification.imageBase64) {
        value = null
        value = withContext(Dispatchers.Default) { decodeNotificationImage(notification.imageBase64) }
    }
    Dialog(onDismissRequest = {}, properties = DialogProperties(
        dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false,
    )) {
        val window = (LocalView.current.parent as DialogWindowProvider).window
        SideEffect { window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) }
        BoxWithConstraints(Modifier.fillMaxSize().background(CaixaDSColorTokens.LegacyNotificationScrim), contentAlignment = Alignment.Center) {
            val shape = RoundedCornerShape(10.dp)
            Column(Modifier.padding(horizontal = 50.dp).fillMaxWidth().heightIn(max = maxHeight * .7f)
                .background(CaixaDSColorTokens.White, shape).border(.2.dp, CaixaDSColorTokens.LegacyNotificationBlue, shape)
                .verticalScroll(rememberScrollState()).padding(vertical = 20.dp)) {
                val imageModifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 20.dp)
                val image = bitmap
                if (image == null) Image(CaixaDSNotificationPlaceholder, null, imageModifier)
                else Image(image.asImageBitmap(), null, imageModifier)
                Text(notification.title.orEmpty(), Modifier.fillMaxWidth().padding(start = 36.dp, end = 36.dp, top = 8.dp),
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true), fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = CaixaDSColorTokens.LegacyNotificationBlue, textAlign = TextAlign.Center))
                Text(notification.content.orEmpty(), Modifier.fillMaxWidth().padding(start = 36.dp, end = 36.dp, top = 8.dp),
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true), fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 14.sp,
                        color = CaixaDSColorTokens.LegacyNotificationBlue, textAlign = TextAlign.Center))
                val hasLink = !notification.link.isNullOrEmpty()
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (hasLink) Arrangement.SpaceBetween else Arrangement.Center) {
                    NotificationButton("Fechar", onClose)
                    if (hasLink) NotificationButton("Abrir Link", onOpenLink)
                }
            }
        }
    }
}

@Composable
private fun NotificationButton(label: String, onClick: () -> Unit) {
    Box(Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 48.dp)
        .background(CaixaDSColorTokens.LegacyNotificationAction, RoundedCornerShape(5.dp))
        .clickable(role = Role.Button, onClick = onClick).padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
        Text(label, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true), fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 14.sp,
            fontWeight = FontWeight.Bold, color = CaixaDSColorTokens.White, textAlign = TextAlign.Center))
    }
}

/** Decode off the UI thread, with byte/dimension limits and downsampling before allocation. */
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
    } catch (_: IllegalArgumentException) { null }
}
