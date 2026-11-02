package br.gov.caixa.loterias.feature.betting.presentation.route

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import br.gov.caixa.loterias.feature.betting.presentation.screen.ShakeTutorialScreen
import coil3.ImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder

@Composable
fun ShakeTutorialRoute(onComplete: () -> Unit) {
    var page by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current
    val loader = remember(context) {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= 28) add(AnimatedImageDecoder.Factory()) else add(GifDecoder.Factory())
        }.build()
    }
    DisposableEffect(loader) { onDispose { loader.shutdown() } }
    fun previous() {
        if (page > 0) page--
    }
    fun next() {
        if (page == 5) onComplete() else page++
    }
    BackHandler { if (page > 0) previous() else onComplete() }
    ShakeTutorialScreen(
        page = page,
        loader = loader,
        onPrevious = { previous() },
        onNext = { next() },
        onPage = { page = it },
        onComplete = onComplete
    )
}
