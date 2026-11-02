package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter

@Composable
internal fun ShakeTutorialPage(
    page: Int,
    image: Int,
    description: String,
    loader: ImageLoader,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        modifier.uiId("bet.0213").pointerInput(page) {
            var drag = 0f
            detectHorizontalDragGestures(onDragStart = { drag = 0f }, onDragEnd = {
                if (drag <
                    -48.dp.toPx()
                ) {
                    onNext()
                } else if (drag > 48.dp.toPx()) {
                    onPrevious()
                }
            }) { _, amount -> drag += amount }
        }.uiId("bet.0214").pointerInput(page) {
            detectTapGestures {
                val arrowHitWidth = 48.dp.toPx()
                if (it.x <= arrowHitWidth || it.x >= size.width - arrowHitWidth) return@detectTapGestures
                if (it.x >= size.width / 2f) onNext() else onPrevious()
            }
        }
    ) {
        // Cada página possui seu próprio painter para que a troca de GIFs não reinicie uma requisição durante a composição.
        val painter = key(image) {
            rememberAsyncImagePainter(model = image, imageLoader = loader)
        }
        ShakeTutorialArtwork(painter, description)
        if (page > 0) {
            ShakeTutorialArrow(
                onClick = { onPrevious() },
                modifier = (Modifier.align(Alignment.CenterStart)).uiId("bet.0215"),
                icon = R.drawable.shake_tutorial_chevron_left,
                description = uiStrings.getString(LocalizedR.string.ui_etapa_anterior_14ca7e)
            )
        }
        ShakeTutorialArrow(
            onClick = { onNext() },
            modifier = (Modifier.align(Alignment.CenterEnd)).uiId("bet.0216"),
            icon = R.drawable.shake_tutorial_chevron_right,
            description = if (page == 5) {
                uiStrings.getString(
                    LocalizedR.string.ui_fechar_tutorial_e8e309
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_proxima_etapa_e801e4)
            }
        )
    }
}

@Composable
private fun ShakeTutorialArrow(onClick: () -> Unit, modifier: Modifier, icon: Int, description: String) {
    Box(modifier.width(48.dp).height(64.dp).uiId("bet.0220").clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Box(
            Modifier.width(24.dp).height(48.dp).shadow(2.dp, RoundedCornerShape(12.dp))
                .background(LoteriasCaixaDSColorTokens.ShakeArrowBackground, RoundedCornerShape(12.dp))
                .border(1.dp, LoteriasCaixaDSColorTokens.SocialCanvas, RoundedCornerShape(12.dp))
        )
        Image(painterResource(icon), description, Modifier.width(8.dp).height(14.dp))
    }
}
