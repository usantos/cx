package br.gov.caixa.loterias.core.designsystem

import android.view.WindowManager
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import kotlin.math.cos

/** Values from LoadingViewLoterias/dialog_loading, including Android's default animator easing. */
internal object CaixaDSLoadingMotion {
    const val delayMillis = 500
    const val fadeMillis = 200
    const val rotationMillis = 1_200
    val easing = Easing { fraction ->
        (cos((fraction + 1) * Math.PI) / 2.0).toFloat() + 0.5f
    }
    val fadeSpec = tween<Float>(fadeMillis, delayMillis, easing)
    val rotationSpec = infiniteRepeatable(tween<Float>(rotationMillis, easing = easing))
}

/** Visual only: use [CaixaDSAuthenticationLoadingDialog] to block interaction during an operation. */
@Composable
fun CaixaDSAuthenticationLoading(
    modifier: Modifier = Modifier,
    label: String = "Carregando…",
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = CaixaDSLoadingMotion.fadeSpec,
        label = "authentication-loading-fade",
    )
    val transition = rememberInfiniteTransition(label = "authentication-loading-rotation")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = CaixaDSLoadingMotion.rotationSpec,
        label = "authentication-loading-icon",
    )
    Box(
        // Alpha must wrap the background too: short operations must not flash a blue rectangle.
        modifier = modifier.fillMaxSize().alpha(alpha).background(CaixaDS.colors.loadingOverlay),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.loterias_loading_icon),
                contentDescription = null,
                modifier = Modifier.size(44.dp).rotate(rotation),
            )
            Text(
                text = label,
                color = CaixaDS.colors.onBrand,
                // Do not inherit Material bodyLarge line height/letter spacing from the host.
                style = TextStyle(
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    platformStyle = PlatformTextStyle(includeFontPadding = true),
                ),
            )
        }
    }
}

/** Non-cancelable modal, without the extra dim layer absent from DialogFullScreenTheme. */
@Composable
fun CaixaDSAuthenticationLoadingDialog(label: String = "Carregando…") {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        val window = (LocalView.current.parent as DialogWindowProvider).window
        SideEffect { window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) }
        CaixaDSAuthenticationLoading(label = label)
    }
}
