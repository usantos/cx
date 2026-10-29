package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions

@Composable
internal fun BoxScope.HomeQuickAccess(
    quickAccessExpanded: Boolean,
    homeState: HomeUiState,
    actions: HomeActions,
    onContentHeightChanged: (Dp) -> Unit = {},
) {
    AnimatedVisibility(
        visible = quickAccessExpanded,
        modifier = Modifier.align(Alignment.TopEnd),
        enter = EnterTransition.None,
        exit = ExitTransition.None,
    ) {
        HomeQuickAccessPanel(
            onContentHeightChanged = onContentHeightChanged,
            socialTransfers = homeState.socialTransfers,
            loadingSocialTransfers = homeState.loadingSocialTransfers,
            onReadTicket = actions.readTicket,
            onResponsibleGaming = actions.responsibleGaming,
            onSocialTransfers = actions.socialTransfers,
            onWomenSupport = actions.womenSupport,
            itemModifier = { index ->
                // A versão 1.24 desloca por 500ms e depois revela por opacidade as quatro linhas visíveis em ordem.
                Modifier.animateEnterExit(
                    enter = fadeIn(tween(200, delayMillis = 500 + index * 200, easing = LegacyHomeEasing)),
                    // AnimacaoUtils.fadeIn na verdade reduz a opacidade, com duração fixa de 500ms.
                    exit = fadeOut(tween(500, delayMillis = (3 - index) * 500, easing = LegacyHomeEasing)),
                )
            },
        )
    }
}
