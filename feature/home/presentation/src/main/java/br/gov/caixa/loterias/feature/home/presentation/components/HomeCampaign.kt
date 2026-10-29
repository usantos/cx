package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos

@Composable
internal fun BoxScope.HomeCampaign(
    octoberRosePanelVisible: Boolean,
    onContentHeightChanged: (Dp) -> Unit = {},
) {
    val density = LocalDensity.current
    val legacyEasing = Easing { ((cos((it + 1f) * PI) / 2.0) + .5).toFloat() }
    AnimatedVisibility(
        visible = octoberRosePanelVisible,
        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
        enter = EnterTransition.None,
        // A marca-d'água permanece até a descrição desaparecer (500ms) e o conteúdo retornar (500ms).
        exit = fadeOut(tween(0, delayMillis = 1_000)),
    ) {
        br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseCampaignPanel(
            // Mede a descrição, excluindo a marca-d'água decorativa, e soma o recuo superior do painel.
            descriptionModifier = Modifier.onSizeChanged {
                onContentHeightChanged(with(density) { it.height.toDp() } + 10.dp)
            }.animateEnterExit(
                enter = fadeIn(tween(200, delayMillis = 500, easing = legacyEasing)),
                exit = fadeOut(tween(500, easing = legacyEasing)),
            ),
        )
    }
}
