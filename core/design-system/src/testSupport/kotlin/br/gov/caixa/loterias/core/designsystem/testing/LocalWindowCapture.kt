@file:OptIn(androidx.compose.ui.InternalComposeUiApi::class)

package br.gov.caixa.loterias.core.designsystem.testing

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt
import org.robolectric.shadows.ShadowLooper

/**
 * Captura pixels reais sem aguardar o FrameCommitCallback não implementado pelo Robolectric.
 * Equivale à correção AndroidX bcef06da086866f7389fdb8e8d5a80cff2ea2808; somente testes JVM.
 * As fontes androidTest continuam usando a captura da plataforma em dispositivos.
 */
@RequiresApi(PIXEL_COPY_WINDOW_API)
internal fun SemanticsNodeInteraction.captureToImage(): ImageBitmap {
    val node = fetchSemanticsNode("O nó precisa existir antes da captura de pixels")
    val view = (checkNotNull(node.root) as ViewRootForTest).view
    check(view.isAttachedToWindow) { "A captura exige uma janela anexada" }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    if (generateSequence(node) { it.parent }.any { it.config.contains(SemanticsProperties.IsPopup) }) {
        return capturePopup(node, view).asImageBitmap()
    }
    val location = IntArray(2)
    view.getLocationInWindow(location)
    val bounds = node.boundsInRoot
    val area = Rect(
        bounds.left.roundToInt() + location[0],
        bounds.top.roundToInt() + location[1],
        bounds.right.roundToInt() + location[0],
        bounds.bottom.roundToInt() + location[1]
    )
    return captureWindow(view.captureWindow(), area).asImageBitmap()
}

private fun View.captureWindow(): Window {
    generateSequence(this) { it.parent as? View }
        .filterIsInstance<androidx.compose.ui.window.DialogWindowProvider>()
        .firstOrNull()?.let { return it.window }
    var candidate: Context = context
    while (candidate is ContextWrapper) {
        if (candidate is Activity) return candidate.window
        candidate = candidate.baseContext
    }
    error("A captura exige contexto de Activity ou janela de diálogo")
}

@RequiresApi(PIXEL_COPY_WINDOW_API)
private fun captureWindow(window: Window, area: Rect): Bitmap {
    check(area.width() > 0 && area.height() > 0) { "A região capturada deve ser visível" }
    repeat(CAPTURE_ATTEMPTS) {
        val bitmap = Bitmap.createBitmap(area.width(), area.height(), Bitmap.Config.ARGB_8888)
        val completed = CountDownLatch(1)
        val result = AtomicInteger(PixelCopy.ERROR_UNKNOWN)
        PixelCopy.request(window, area, bitmap, { status ->
            result.set(status)
            completed.countDown()
        }, Handler(Looper.getMainLooper()))
        ShadowLooper.idleMainLooper()
        check(completed.await(CAPTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) { "PixelCopy não concluiu a captura" }
        if (result.get() == PixelCopy.SUCCESS) return bitmap
        bitmap.recycle()
        check(result.get() == PixelCopy.ERROR_SOURCE_NO_DATA) { "PixelCopy falhou: ${result.get()}" }
    }
    error("PixelCopy não recebeu pixels após $CAPTURE_ATTEMPTS tentativas")
}

private fun capturePopup(node: SemanticsNode, view: View): Bitmap {
    val screenshot = checkNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot())
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val bounds = node.boundsInRoot
    return Bitmap.createBitmap(
        screenshot,
        location[0] + bounds.left.roundToInt(),
        location[1] + bounds.top.roundToInt(),
        bounds.width.roundToInt(),
        bounds.height.roundToInt()
    )
}

private const val PIXEL_COPY_WINDOW_API = 26
private const val CAPTURE_ATTEMPTS = 3
private const val CAPTURE_TIMEOUT_SECONDS = 1L
