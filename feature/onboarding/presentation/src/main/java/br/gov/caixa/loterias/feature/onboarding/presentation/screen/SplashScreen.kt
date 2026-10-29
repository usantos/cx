package br.gov.caixa.loterias.feature.onboarding.presentation.screen

import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import br.gov.caixa.loterias.core.designsystem.R as DesignSystemR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnimation
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnimationImage
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnimationImageHost
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBarColor
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.onboarding.R
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition

internal val LocalSplashAnimation = staticCompositionLocalOf<LottieAnimatable?> { null }

/** Mantém a reprodução durante a recuperação de sessão, a preparação de segurança e as verificações da fila. */
@Composable
fun SplashAnimationHost(content: @Composable () -> Unit) {
    val animation = rememberLottieAnimatable()
    CaixaDSAnimationImageHost {
        CompositionLocalProvider(LocalSplashAnimation provides animation, content = content)
    }
}

/** Splash institucional original, agora renderizado diretamente pelo Compose. */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    CaixaDSStatusBarColor(Color.Transparent, darkIcons = false)
    val animation = LocalSplashAnimation.current ?: rememberLottieAnimatable()
    val composition = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_ja_pensou)
    ).value

    val view = LocalView.current
    // API pública usada pela consulta de escala do Lottie em todos os SDKs compatíveis (minSdk 29).
    // A escala zero produz velocidade infinita, que LottieAnimatable ajusta ao quadro final.
    val speed = 1.4f / Settings.Global.getFloat(
        view.context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1.0f
    )
    LaunchedEffect(composition, animation, speed) {
        if (composition != null) {
            animation.animate(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                // O movimento termina no quadro 110; o arquivo exportado mantém o logotipo até o quadro 900.
                clipSpec = LottieClipSpec.Frame(max = 120),
                speed = speed,
                initialProgress = animation.progress
            )
        }
    }

    val description = stringResource(R.string.splash_loterias_logo)
    Box(
        modifier.fillMaxSize().clipToBounds().background(CaixaDSColorTokens.Blue700)
            .semantics { contentDescription = description }
    ) {
        // Exibe imediatamente o fundo original enquanto Lottie e suas imagens carregam.
        // O recurso de 1080 x 1920 ultrapassa a área de animação de 1000 x 1778.
        Image(
            painter = painterResource(R.drawable.splash_animation_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().scale(BACKGROUND_WIDTH / ANIMATION_CANVAS_WIDTH, BACKGROUND_HEIGHT / ANIMATION_CANVAS_HEIGHT),
            contentScale = ContentScale.Crop
        )
        CaixaDSAnimation(
            composition = animation.composition,
            progress = { animation.progress },
            modifier = (Modifier.fillMaxSize()).uiId("start.0034"),
            contentScale = ContentScale.Crop,
            images = splashBrandImages
        )
    }
}

// Preserva a arte do login, com os quadros-chave legados de entrada do trevo e da marca.
private val splashBrandImages = listOf(
    CaixaDSAnimationImage("image_0", "caixa", DesignSystemR.drawable.caixa_loterias_logo, Rect(0f, 0.75f, 1f, 1f)),
    CaixaDSAnimationImage("image_1", "loterias", DesignSystemR.drawable.caixa_loterias_logo, Rect(0f, 0.49f, 1f, 0.75f)),
    CaixaDSAnimationImage("image_2", "trevo", DesignSystemR.drawable.caixa_loterias_logo, Rect(0f, 0f, 1f, 0.49f))
)

private const val BACKGROUND_WIDTH = 1080f
private const val BACKGROUND_HEIGHT = 1920f
private const val ANIMATION_CANVAS_WIDTH = 1000f
private const val ANIMATION_CANVAS_HEIGHT = 1778f
