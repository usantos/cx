package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialInstructionDescription
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialPage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
internal fun LottieTutorialPage(page: TutorialPage, index: Int, total: Int, active: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(page.media))
    Box(
        Modifier.fillMaxSize().clearAndSetSemantics {
            if (active) {
                contentDescription = tutorialInstructionDescription(page, index, total)
                role = Role.Image
            }
        }
    ) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier.fillMaxSize(),
            iterations = if (page.loops) LottieConstants.IterateForever else 1,
            speed = page.speed,
            contentScale = ContentScale.Fit
        )
    }
}
