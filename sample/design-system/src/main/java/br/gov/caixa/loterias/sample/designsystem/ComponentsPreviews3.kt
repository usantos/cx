@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.ui.text.input.KeyboardType
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceBack
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterAccordionHeader
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterBetGrid
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterInput
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterLabel
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterNumberCell
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterSelection
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceListFooter
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMercadoPagoPrizePanel

internal val componentsPreviews3 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceListFooter") { state ->
        with(state) {
            CaixaDSMarketplaceListFooter(
                loading = false,
                exhausted = true
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceBack") { state ->
        with(state) {
            CaixaDSMarketplaceBack(onClick = action)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterLabel") { state ->
        with(state) {
            CaixaDSMarketplaceFilterLabel(text = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterInput") { state ->
        with(state) {
            CaixaDSMarketplaceFilterInput(
                value = text,
                onValueChange = { text = it },
                placeholder = hint,
                keyboardType = KeyboardType.Text
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterBetGrid") { state ->
        with(state) {
            CaixaDSMarketplaceFilterBetGrid(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterNumberCell") { state ->
        with(state) {
            CaixaDSMarketplaceFilterNumberCell(
                value = 7,
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterAccordionHeader") { state ->
        with(state) {
            CaixaDSMarketplaceFilterAccordionHeader(
                title = sample,
                testTag = "accordion",
                onOpen = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterButton") { state ->
        with(state) {
            CaixaDSMarketplaceFilterButton(
                text = actionLabel,
                outlined = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterSelection") { state ->
        with(state) {
            CaixaDSMarketplaceFilterSelection(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMercadoPagoPrizePanel") { state ->
        with(state) {
            CaixaDSMercadoPagoPrizePanel(amount = amount)
        }
    }
)
