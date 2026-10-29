package br.gov.caixa.loterias.core.designsystem.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import kotlin.math.cos

/** Valores de LoadingViewLoterias/dialog_loading, incluindo a curva padrão do animador Android. */
internal object CaixaDSLoadingMotion {
    const val DELAY_MILLIS = 500
    const val FADE_MILLIS = 200
    const val ROTATION_MILLIS = 1_200
    val easing = Easing { fraction ->
        (cos((fraction + 1) * Math.PI) / 2.0).toFloat() + 0.5f
    }
    val fadeSpec = tween<Float>(FADE_MILLIS, DELAY_MILLIS, easing)
    val rotationSpec = infiniteRepeatable(tween<Float>(ROTATION_MILLIS, easing = easing))
}

/** Apenas visual: usa [CaixaDSAuthenticationLoadingDialog] para bloquear a interação durante uma operação. */
@Composable
fun CaixaDSAuthenticationLoading(modifier: Modifier = Modifier, label: String = stringResource(LocalizedR.string.format_loading_ellipsis)) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = CaixaDSLoadingMotion.fadeSpec,
        label = uiStrings.getString(LocalizedR.string.ui_authentication_loading_fade_16ad30)
    )
    val transition = rememberInfiniteTransition(label = uiStrings.getString(LocalizedR.string.ui_authentication_loading_rotation_7122cf))
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = CaixaDSLoadingMotion.rotationSpec,
        label = uiStrings.getString(LocalizedR.string.ui_authentication_loading_icon_47a9aa)
    )
    Box(
        // O alfa também deve envolver o fundo: operações curtas não devem exibir um retângulo azul momentâneo.
        modifier = modifier.fillMaxSize().alpha(alpha).background(CaixaDS.colors.loadingOverlay),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.loterias_loading_icon),
                contentDescription = null,
                modifier = Modifier.size(44.dp).rotate(rotation)
            )
            Text(
                text = label,
                color = CaixaDS.colors.onBrand,
                // Não herda a altura de linha nem o espaçamento de letras do bodyLarge Material do hospedeiro.
                style = TextStyle(
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
        }
    }
}

/** Modal não cancelável, sem a camada extra de escurecimento ausente no DialogFullScreenTheme. */
@Composable
fun CaixaDSAuthenticationLoadingDialog(label: String = stringResource(LocalizedR.string.format_loading_ellipsis)) {
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
        CaixaDSAuthenticationLoading(label = label, modifier = Modifier.uiId("ds.0004"))
    }
}
