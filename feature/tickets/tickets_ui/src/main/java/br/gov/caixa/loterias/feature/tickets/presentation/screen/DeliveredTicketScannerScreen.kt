package br.gov.caixa.loterias.feature.tickets.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun TicketScannerScreenDelivered(
    permissionGranted: Boolean,
    onBack: () -> Unit,
    onRequestCameraPermission: () -> Unit,
    cameraPreview: @Composable (Modifier) -> Unit,
    onManual: (() -> Unit)? = null,
) {
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Box(Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.CameraCanvas).navigationBarsPadding()) {
        if (permissionGranted) {
            cameraPreview(Modifier.fillMaxSize())
        } else {
            Column(
                Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    LoteriasCaixaDSIcons.Information,
                    null,
                    (Modifier.size(56.dp)).uiId("tickets.0244"),
                    tint = LoteriasCaixaDSColorTokens.White
                )
                Text(
                    stringResource(DeliveryR.string.tickets_scanner_permission_message),
                    (Modifier.padding(vertical = 20.dp)).uiId("tickets.0245"),
                    color = LoteriasCaixaDSColorTokens.White
                )
                LoteriasCaixaDSPrimaryButton(stringResource(DeliveryR.string.tickets_scanner_permission_action), {
                    onRequestCameraPermission()
                }, (Modifier.fillMaxWidth()).uiId("tickets.0246"))
            }
        }
        if (permissionGranted) {
            TicketScannerMask(landscape)
            Image(
                painter = painterResource(DeliveryR.drawable.logo_cinza),
                contentDescription = null,
                modifier = Modifier.statusBarsPadding().offset(x = 68.dp, y = 56.dp).size(22.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                stringResource(DeliveryR.string.tickets_scanner_title),
                (Modifier.statusBarsPadding().offset(x = 100.dp, y = 58.dp)).uiId("tickets.0247"),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = if (landscape) 16.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(DeliveryR.drawable.close_circle_outline),
                contentDescription = stringResource(DeliveryR.string.tickets_scanner_close),
                modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().offset(x = (-20).dp, y = 56.dp).size(30.dp)
                    .uiId("tickets.0248").clickable(onClick = onBack),
                contentScale = ContentScale.Fit
            )
            Text(
                stringResource(DeliveryR.string.tickets_scanner_qr_instruction),
                (Modifier.statusBarsPadding().offset(y = 88.dp).fillMaxWidth().padding(horizontal = 68.dp)).uiId("tickets.0249"),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 14.2.sp,
                lineHeight = 20.sp
            )
        }
        onManual?.let { openManual ->
            LoteriasCaixaDSPrimaryButton(
                stringResource(R.string.workflow_tickets_digitar_codigo),
                openManual,
                (Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)).uiId("tickets.0250")
            )
        }
    }
}

@Composable
private fun TicketScannerMask(landscape: Boolean) {
    Canvas(Modifier.fillMaxSize()) {
        val frameWidth = size.width * if (landscape) LANDSCAPE_FRAME_WIDTH else PORTRAIT_FRAME_WIDTH
        val frameHeight = if (landscape) size.height * LANDSCAPE_FRAME_HEIGHT else frameWidth
        val left = (size.width - frameWidth) / 2
        val top = (size.height - frameHeight) / 2
        val window = Path().apply {
            addRect(androidx.compose.ui.geometry.Rect(left, top, left + frameWidth, top + frameHeight))
        }
        clipPath(window, ClipOp.Difference) {
            drawRect(LoteriasCaixaDSColorTokens.Blue500.copy(alpha = .8f))
        }
        val corner = 24.dp.toPx()
        val stroke = 3.dp.toPx()
        listOf(
            Triple(Offset(left, top), 1f, 1f),
            Triple(Offset(left + frameWidth, top), -1f, 1f),
            Triple(Offset(left, top + frameHeight), 1f, -1f),
            Triple(Offset(left + frameWidth, top + frameHeight), -1f, -1f)
        ).forEach { (origin, horizontal, vertical) ->
            drawLine(LoteriasCaixaDSColorTokens.White, origin, origin + Offset(corner * horizontal, 0f), stroke)
            drawLine(LoteriasCaixaDSColorTokens.White, origin, origin + Offset(0f, corner * vertical), stroke)
        }
    }
}

private const val LANDSCAPE_FRAME_WIDTH = .7f
private const val PORTRAIT_FRAME_WIDTH = .76f
private const val LANDSCAPE_FRAME_HEIGHT = .42f
