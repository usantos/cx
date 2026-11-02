package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailFavoriteEditor
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailTeimosinhaDialog
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.teimosinhaNotice
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR

@Composable
fun BetDetailRoute(
    bet: ConfirmedBet,
    onBack: () -> Unit,
    onViewResult: () -> Unit,
    onRedeemPrize: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
    onEdit: (() -> Unit)? = null,
    onCart: () -> Unit = {},
    onAddToCart: (() -> Unit)? = null,
    onDownloadReceipt: (() -> Unit)? = null,
    favoriteExpanded: Boolean = false,
    favoriteName: String = "",
    favoriteSaved: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onFavoriteNameChange: (String) -> Unit = {},
    onSaveFavorite: () -> Unit = {},
    drawResults: List<br.gov.caixa.loterias.feature.results.domain.model.DrawResult> = emptyList(),
    cartTotal: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    cartBetCount: Int = 0,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val notice = teimosinhaNotice(bet)
    var showAlert by androidx.compose.runtime.saveable.rememberSaveable(bet.id, notice) {
        mutableStateOf(notice != null)
    }
    val style = loteriasCaixaDSLotteryStyle(bet.modalityId, bet.modality)
    val canRepeat = !bet.poolShare && bet.modalityId != 19 && !bet.modality.contains("loteca", true)
    androidx.compose.runtime.CompositionLocalProvider(
        br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSLocalTextStyle provides androidx.compose.ui.text.TextStyle(
            fontFamily = LoteriasCaixaDSFontFamily,
            platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = true)
        )
    ) {
        LoteriasCaixaDSScaffold(
            title = bet.modality.replace('-', ' ').replace('_', ' ').lowercase(),
            onBack = onBack, topBarNavigationWidth = 56.dp, topBarTitleSize = 18.sp,
            gradientTopBar = false, topBarStartColor = style.light, topBarEndColor = style.light,
            topBarContentColor = style.content,
            topBarAction = {
                Icon(
                    LoteriasCaixaDSIcons.Help,
                    uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                    (Modifier.size(25.dp).uiId("tickets.0154").clickable(onClick = onHelp)).uiId("tickets.0155"),
                    tint = style.content
                )
            },
            bottomBar = {
                NativeBetDetailFooter(
                    style, canRepeat, onEdit, onCart,
                    onAddToCart, onToggleFavorite, favoriteSaved, cartTotal, cartBetCount
                )
            },
            containerColor = style.dark, modifier = Modifier.uiId("tickets.0156")
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                    .legacyAutomationId("bet-detail-scroll")
            ) {
                NativeBetDetailHeader(bet, style)
                NativeBetTypeBanner(bet, style)
                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    BetDetailResultActions(bet, drawResults, style, onViewResult, onRedeemPrize, onDownloadReceipt)
                }
            }
        }
        BetDetailFavoriteEditor(
            favoriteExpanded,
            favoriteSaved,
            favoriteName,
            onFavoriteNameChange,
            onSaveFavorite,
            onToggleFavorite
        )
    }
    if (showAlert) {
        BetDetailTeimosinhaDialog(notice, onConfirm = { showAlert = false })
    }
}

@Composable
internal fun BetDetailUnavailableScreen(state: BetDetailUiState, onRetry: () -> Unit, onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold(
        title = uiStrings.getString(
            LocalizedR.string.ui_detalhes_da_aposta_82fbc3
        ),
        onBack = onBack,
        modifier = Modifier.uiId("tickets.0159")
    ) { padding ->
        br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState(
            title = uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_consultar_esta_apos_728d12),
            message = state.errorMessage.orEmpty(),
            actionLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onAction = onRetry,
            modifier = (Modifier.padding(padding)).uiId("tickets.0160")
        )
    }
}
