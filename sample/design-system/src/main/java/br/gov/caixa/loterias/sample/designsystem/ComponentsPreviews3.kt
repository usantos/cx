@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.ui.text.input.KeyboardType
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceBack
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterAccordionHeader
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterBetGrid
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterInput
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterLabel
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterNumberCell
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterSelection
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceListFooter
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMercadoPagoPrizePanel

internal val componentsPreviews3 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceListFooter") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceListFooter(
                loading = false,
                exhausted = true
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceBack") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceBack(onClick = action)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterLabel") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceFilterLabel(text = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterInput") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceFilterInput(
                value = text,
                onValueChange = { text = it },
                placeholder = hint,
                keyboardType = KeyboardType.Text
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterBetGrid") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceFilterBetGrid(
                options = listOf(
                    1,
                    2,
                    3,
                    4
                ),
                selected = setOf(selection + 1),
                onClick = { selection = it - 1 }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterNumberCell") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceFilterNumberCell(
                value = 7,
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterAccordionHeader") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceFilterAccordionHeader(
                title = sample,
                testTag = "accordion",
                onOpen = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterButton") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceFilterButton(
                text = actionLabel,
                outlined = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterSelection") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceFilterSelection(
                values = listOf(
                    1,
                    2,
                    3
                ),
                clearLabel = actionLabel,
                onRemove = { count++ },
                onClear = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMercadoPagoPrizePanel") { state ->
        with(state) {
            LoteriasCaixaDSMercadoPagoPrizePanel(amount = amount)
        }
    }
)
