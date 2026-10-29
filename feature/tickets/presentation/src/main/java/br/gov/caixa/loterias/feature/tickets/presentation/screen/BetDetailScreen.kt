package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailFavoriteEditor
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailTeimosinhaDialog
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.teimosinhaNotice
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel

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
    val style = caixaDSLotteryStyle(bet.modalityId, bet.modality)
    val canRepeat = !bet.poolShare && bet.modalityId != 19 && !bet.modality.contains("loteca", true)
    androidx.compose.runtime.CompositionLocalProvider(
        br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSLocalTextStyle provides androidx.compose.ui.text.TextStyle(
            fontFamily = CaixaDSFontFamily,
            platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = true)
        )
    ) {
        CaixaDSScaffold(
            title = bet.modality.replace('-', ' ').replace('_', ' ').lowercase(),
            onBack = onBack, topBarNavigationWidth = 56.dp, topBarTitleSize = 18.sp,
            gradientTopBar = false, topBarStartColor = style.light, topBarEndColor = style.light,
            topBarContentColor = style.content,
            topBarAction = {
                Icon(
                    CaixaDSIcons.Help,
                    uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                    (Modifier.size(25.dp).uiId("tickets.0154").clickable(onClick = onHelp)).uiId("tickets.0155"),
                    tint = style.content
                )
            },
            bottomBar = {
                NativeBetDetailFooter(
                    bet, style, canRepeat, onEdit, onCart,
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
internal fun BetDetailUnavailableScreen(state: BetDetailUiState, viewModel: BetDetailViewModel, onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold(
        title = uiStrings.getString(
            LocalizedR.string.ui_detalhes_da_aposta_82fbc3
        ),
        onBack = onBack,
        modifier = Modifier.uiId("tickets.0159")
    ) { padding ->
        br.gov.caixa.loterias.core.designsystem.components.CaixaDSEmptyState(
            title = uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_consultar_esta_apos_728d12),
            message = state.errorMessage.orEmpty(),
            actionLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onAction = viewModel::loadDetails,
            modifier = (Modifier.padding(padding)).uiId("tickets.0160")
        )
    }
}

@Composable
private fun BetDetailResultActions(
    bet: ConfirmedBet,
    drawResults: List<br.gov.caixa.loterias.feature.results.domain.model.DrawResult>,
    style: br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle,
    onViewResult: () -> Unit,
    onRedeemPrize: (() -> Unit)?,
    onDownloadReceipt: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    NativeBetDetailSelections(bet, drawResults, style)
    if (drawResults.isEmpty() && bet.statusId !in setOf(4L, 101L)) {
        NativeDetailButton(
            uiStrings.getString(LocalizedR.string.ui_ver_resultado_2011cb),
            style,
            onViewResult,
            Modifier.padding(top = 12.dp)
        )
    }
    if (bet.prize.signum() > 0 && !bet.redeemedPrize) {
        Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(CaixaDSIcons.Time, null, (Modifier.size(25.dp)).uiId("tickets.0157"), tint = style.darkContent)
            Text(
                uiStrings.getString(LocalizedR.string.ui_prazo_para_recebimento_de_premio_90_14a8d5),
                (Modifier.padding(start = 8.dp)).uiId("tickets.0158"),
                color = style.darkContent,
                fontSize = 18.sp
            )
        }
        NativeDetailButton(
            uiStrings.getString(LocalizedR.string.ui_resgatar_premio_e68bcd),
            style,
            onRedeemPrize,
            Modifier.padding(top = 12.dp)
        )
    }
    NativeDetailButton(
        uiStrings.getString(LocalizedR.string.ui_gerar_comprovante_a92bea),
        style,
        onDownloadReceipt,
        Modifier.padding(top = 12.dp),
        download = true
    )
    Spacer(Modifier.height(100.dp))
}
