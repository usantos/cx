package br.gov.caixa.loterias.feature.home.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.contract.account.model.AccountNotification
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

/** Geometria e cores de nova_popup_notificacao; sem WebView nem leiaute XML em execução. */
@Composable
internal fun NotificationPopupContent(notification: AccountNotification, bitmap: Bitmap?, onClose: () -> Unit, onOpenLink: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val imageModifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 20.dp)
    val image = bitmap
    if (image == null) {
        Image(LoteriasCaixaDSNotificationPlaceholder, null, imageModifier)
    } else {
        Image(image.asImageBitmap(), null, imageModifier)
    }
    Text(
        notification.title.orEmpty(),
        (Modifier.fillMaxWidth().padding(start = 36.dp, end = 36.dp, top = 8.dp))
            .uiId("home.0039"),
        style = TextStyle(
            platformStyle = PlatformTextStyle(includeFontPadding = true),
            fontFamily = LoteriasCaixaDSFuturaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = LoteriasCaixaDSColorTokens.NotificationBlue,
            textAlign = TextAlign.Center
        )
    )
    Text(
        notification.content.orEmpty(),
        (Modifier.fillMaxWidth().padding(start = 36.dp, end = 36.dp, top = 8.dp))
            .uiId("home.0040"),
        style = TextStyle(
            platformStyle = PlatformTextStyle(includeFontPadding = true),
            fontFamily = LoteriasCaixaDSFuturaFontFamily,
            fontSize = 14.sp,
            color = LoteriasCaixaDSColorTokens.NotificationBlue,
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
        if (hasLink) NotificationButton(uiStrings.getString(DeliveryR.string.ui_abrir_link_9b8abd), onOpenLink)
    }
}

@Composable
internal fun NotificationButton(label: String, onClick: () -> Unit) {
    Box(
        Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 48.dp)
            .background(LoteriasCaixaDSColorTokens.NotificationAction, RoundedCornerShape(5.dp))
            .uiId("home.0041").clickable(role = Role.Button, onClick = onClick).padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = true),
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LoteriasCaixaDSColorTokens.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .uiId("home.0042")
        )
    }
}
