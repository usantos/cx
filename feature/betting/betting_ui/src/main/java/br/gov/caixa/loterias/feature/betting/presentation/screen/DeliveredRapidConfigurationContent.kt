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
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidAmountPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidModalitiesPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidNumbersPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPageIndicator
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPrizePage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidSummaryPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidWelcomePage
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun RapidConfigurationContentDelivered(
    state: RapidConfigurationUiState,
    pager: PagerState,
    summaryVisited: Boolean,
    onBack: () -> Unit,
    actions: RapidConfigurationActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
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
                        uiStrings.getString(DeliveryR.string.ui_salvar_aef7cd),
                        color = LoteriasCaixaDSColorTokens.White,
                        fontSize = 16.sp,
                        fontFamily = LoteriasCaixaDSFontFamily,
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
                CircularProgressIndicator(color = LoteriasCaixaDS.colors.brand)
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDS.colors.surface)) {
                HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { page ->
                    when (page) {
                        0 -> RapidWelcomePage()

                        1 -> RapidPrizePage(state.minimumPrize, actions.setMinimumPrize)

                        2 -> RapidAmountPage(state, actions.setMinimumAmount, actions.setMaximumAmount, actions.setCustomAmount, {
                            actions.loadAmountLimits()
                        })

                        RAPID_MODALITIES_PAGE -> RapidModalitiesPage(
                            state.availableModalities,
                            state.selectedModalityIds,
                            actions.toggleModality
                        )

                        RAPID_REQUIRED_NUMBERS_PAGE -> RapidNumbersPage(
                            title = uiStrings.getString(DeliveryR.string.ui_numeros_obrigatorios_621e09),
                            description = uiStrings.getString(DeliveryR.string.ui_selecione_os_numeros_que_deverao_apa_0c4419),
                            selected = state.requiredNumbers,
                            onToggle = actions.toggleRequiredNumber,
                            onClear = actions.clearRequiredNumbers
                        )

                        RAPID_EXCLUDED_NUMBERS_PAGE -> RapidNumbersPage(
                            title = uiStrings.getString(DeliveryR.string.ui_numeros_proibidos_09cf4d),
                            description = uiStrings.getString(DeliveryR.string.ui_selecione_os_numeros_que_nao_deverao_30dc1d),
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

private const val RAPID_MODALITIES_PAGE = 3
private const val RAPID_REQUIRED_NUMBERS_PAGE = 4
private const val RAPID_EXCLUDED_NUMBERS_PAGE = 5
