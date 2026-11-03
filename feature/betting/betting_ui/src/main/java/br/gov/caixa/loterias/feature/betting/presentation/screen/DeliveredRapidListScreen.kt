package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

@Composable
fun RapidListScreenDelivered(
    state: RapidListUiState,
    onBack: () -> Unit,
    onConfiguration: () -> Unit,
    onGenerate: () -> Unit,
    onAdvance: () -> Unit,
    onRemove: (CartItem) -> Unit,
    onDismissError: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_rapidao_658c41),
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onConfiguration, modifier = Modifier.uiId("bet.0182")) {
                Icon(
                    painterResource(DeliveryR.drawable.legacy_rapid_settings),
                    contentDescription = uiStrings.getString(DeliveryR.string.ui_configuracao_8d60e1),
                    tint = LoteriasCaixaDSColorTokens.White,
                    modifier = (Modifier.size(24.dp)).uiId("bet.0183")
                )
            }
        },
        bottomBar = {
            RapidListFooterDelivered(state, onGenerate, onAdvance)
        },
        modifier = Modifier.uiId("bet.0190")
    ) { padding ->
        RapidListContentDelivered(state, padding, onGenerate, onRemove)
    }
    state.errorMessage?.let {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            onDismissError
        )
    }
}
