package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSModalNavigationDrawer as ModalNavigationDrawer
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.feature.home.presentation.components.HomeDrawer

internal data class HomeDrawerIdentity(val userName: String? = null, val environmentLabel: String? = null)

@Composable
internal fun HomeNavigationDrawer(
    drawerState: androidx.compose.material3.DrawerState,
    navigationBarInset: Float,
    preferenceState: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState?,
    preferencesViewModel: br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel?,
    homeState: br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState,
    identity: HomeDrawerIdentity,
    onClose: () -> Unit,
    onExternalFailure: () -> Unit,
    onOpen: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit,
) {
    BackHandler(enabled = drawerState.isOpen, onBack = onClose)
    ModalNavigationDrawer(
        modifier = (
            Modifier.drawWithContent {
                drawContent()
                if (drawerState.isOpen) {
                    // Preserva o recuo branco da navegação; o hospedeiro do aplicativo pinta a barra de status azul.
                    drawRect(
                        color = CaixaDSColorTokens.White,
                        topLeft = Offset(0f, size.height - navigationBarInset),
                        size = Size(size.width, navigationBarInset)
                    )
                }
            }
            ).uiId("home.0110"),
        drawerState = drawerState,
        scrimColor = CaixaDSColorTokens.HomeDrawerScrim,
        drawerContent = {
            HomeDrawer(
                preferenceState = preferenceState,
                onToggleShake = { preferencesViewModel?.toggleShake(it) },
                onConsumeShakeTooltip = { preferencesViewModel?.consumeShakeTooltip() },
                drawerOpen = drawerState.isOpen,
                features = homeState.features,
                userName = identity.userName,
                environmentLabel = identity.environmentLabel,
                campaign = homeState.features.campaigns.active(CampaignArea.DRAWER),
                onClose = onClose,
                onExternalFailure = onExternalFailure,
                onOpen = onOpen,
                onLogout = onLogout
            )
        },
        content = content
    )
}
