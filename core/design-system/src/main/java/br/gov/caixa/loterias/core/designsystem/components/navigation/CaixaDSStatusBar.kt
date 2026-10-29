package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

private class StatusBarRequest(color: Color, darkIcons: Boolean) {
    var color by mutableStateOf(color)
    var darkIcons by mutableStateOf(darkIcons)
}

private val LocalStatusBarRequests = staticCompositionLocalOf<SnapshotStateList<StatusBarRequest>?> { null }

/** Pintura pertencente à rota para o recuo da barra de status imposto pelo Android no modo de ponta a ponta. */
@Composable
fun CaixaDSStatusBarColor(color: Color, darkIcons: Boolean? = null) {
    val requests = LocalStatusBarRequests.current
    val view = LocalView.current
    val useDarkIcons = darkIcons ?: (color.luminance() > .5f)
    val request = remember(requests) { StatusBarRequest(color, useDarkIcons) }
    SideEffect {
        request.color = color
        request.darkIcons = useDarkIcons
    }
    DisposableEffect(requests, request) {
        requests?.add(request)
        onDispose { requests?.remove(request) }
    }
    // Prévias independentes de features e hospedeiros de teste podem renderizar sem o invólucro do aplicativo.
    if (requests == null) DisposableEffect(view, useDarkIcons) {
        val controller = view.context.statusBarActivity()?.window?.let {
            androidx.core.view.WindowCompat.getInsetsController(it, view)
        }
        val previous = controller?.isAppearanceLightStatusBars
        controller?.isAppearanceLightStatusBars = useDarkIcons
        onDispose { if (previous != null) controller?.isAppearanceLightStatusBars = previous }
    }
}

private tailrec fun android.content.Context.statusBarActivity(): android.app.Activity? = when (this) {
    is android.app.Activity -> this
    is android.content.ContextWrapper -> baseContext.statusBarActivity()
    else -> null
}

/** Pinta os recuos do sistema de forma consistente, inclusive em janelas de ponta a ponta impostas pelo Android. */
@Suppress("DEPRECATION")
@Composable
fun CaixaDSStatusBar(content: @Composable () -> Unit) {
    val requests = remember { mutableStateListOf<StatusBarRequest>() }
    // Uma tela que sai remove apenas sua própria solicitação, preservando a tela que entra.
    val active = requests.lastOrNull()
    val color = active?.color ?: CaixaDSColorTokens.Blue700
    val darkIcons = active?.darkIcons ?: false
    val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current).toFloat()
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current).toFloat()
    val view = LocalView.current
    val location = remember { IntArray(2) }
    SideEffect {
        view.context.statusBarActivity()?.window?.let {
            androidx.core.view.WindowCompat.getInsetsController(it, view).apply {
                isAppearanceLightStatusBars = darkIcons
                isAppearanceLightNavigationBars = true
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                it.isNavigationBarContrastEnforced = false
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                it.navigationBarDividerColor = android.graphics.Color.TRANSPARENT
            }
        }
    }
    Box(Modifier.fillMaxSize().drawWithContent {
        drawContent()
        view.getLocationInWindow(location)
        val visibleInset = (statusBarHeight - location[1]).coerceAtLeast(0f)
        if (visibleInset > 0f) {
            drawRect(color, size = Size(size.width, visibleInset))
        }
        val bottomOutsideContent = (view.rootView.height - location[1] - size.height).coerceAtLeast(0f)
        val visibleNavigationInset = (navigationBarHeight - bottomOutsideContent).coerceIn(0f, size.height)
        if (visibleNavigationInset > 0f) {
            drawRect(CaixaDSColorTokens.White, topLeft = Offset(0f, size.height - visibleNavigationInset),
                size = Size(size.width, visibleNavigationInset))
        }
    }) { CompositionLocalProvider(LocalStatusBarRequests provides requests) { content() } }
}
