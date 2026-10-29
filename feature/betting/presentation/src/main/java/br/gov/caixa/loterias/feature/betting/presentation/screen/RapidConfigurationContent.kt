package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidAmountPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidModalitiesPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidNumbersPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPageIndicator
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPrizePage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidSummaryPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidWelcomePage
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import java.math.BigDecimal

@Composable
internal fun RapidConfigurationContent(
    state: RapidConfigurationUiState,
    pager: PagerState,
    summaryVisited: Boolean,
    onBack: () -> Unit,
    actions: RapidConfigurationActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_rapidao_658c41),
        onBack = onBack,
        topBarActionWidth = 80.dp,
        topBarAction = {
            if (summaryVisited) {
                TextButton(
                    onClick = actions.save,
                    enabled = !state.loading && state.canSave,
                    modifier = Modifier
                        .uiId("bet.0287")
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_salvar_aef7cd),
                        color = CaixaDSColorTokens.White,
                        fontSize = 16.sp,
                        fontFamily = CaixaDSFontFamily,
                        modifier = Modifier.uiId("bet.0288")
                    )
                }
            }
        },
        bottomBar = { if (!state.loading) RapidPageIndicator(state.page, actions.setPage) },
        modifier = Modifier.uiId("bet.0289")
    ) { padding ->
        if (state.loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.surface)) {
                HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { page ->
                    when (page) {
                        0 -> RapidWelcomePage()

                        1 -> RapidPrizePage(state.minimumPrize, actions.setMinimumPrize)

                        2 -> RapidAmountPage(state, actions.setMinimumAmount, actions.setMaximumAmount, actions.setCustomAmount, {
                            actions.loadAmountLimits()
                        })

                        3 -> RapidModalitiesPage(state.availableModalities, state.selectedModalityIds, actions.toggleModality)

                        4 -> RapidNumbersPage(
                            title = uiStrings.getString(LocalizedR.string.ui_numeros_obrigatorios_621e09),
                            description = uiStrings.getString(LocalizedR.string.ui_selecione_os_numeros_que_deverao_apa_0c4419),
                            selected = state.requiredNumbers,
                            onToggle = actions.toggleRequiredNumber,
                            onClear = actions.clearRequiredNumbers
                        )

                        5 -> RapidNumbersPage(
                            title = uiStrings.getString(LocalizedR.string.ui_numeros_proibidos_09cf4d),
                            description = uiStrings.getString(LocalizedR.string.ui_selecione_os_numeros_que_nao_deverao_30dc1d),
                            selected = state.excludedNumbers,
                            onToggle = actions.toggleExcludedNumber,
                            onClear = actions.clearExcludedNumbers,
                            danger = true
                        )

                        else -> RapidSummaryPage(state, actions.setPage)
                    }
                }
            }
        }
    }
}

internal data class RapidConfigurationActions(
    val setPage: (Int) -> Unit,
    val setMinimumPrize: (BigDecimal) -> Unit,
    val setMinimumAmount: (BigDecimal) -> Unit,
    val setMaximumAmount: (BigDecimal) -> Unit,
    val setCustomAmount: (BigDecimal?, Boolean) -> Boolean,
    val loadAmountLimits: () -> Unit,
    val toggleModality: (Int) -> Unit,
    val toggleRequiredNumber: (Int) -> Unit,
    val clearRequiredNumbers: () -> Unit,
    val toggleExcludedNumber: (Int) -> Unit,
    val clearExcludedNumbers: () -> Unit,
    val save: () -> Unit,
)
