package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

/** Splash institucional original, agora renderizado diretamente pelo Compose. */
@Composable
fun LegacySplashScreen(modifier: Modifier = Modifier) {
    val composition = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_ja_pensou),
    ).value

    LottieAnimation(
        composition = composition,
        modifier = modifier.fillMaxSize(),
        iterations = LottieConstants.IterateForever,
        speed = 1.4f,
        contentScale = ContentScale.Crop,
    )
}
