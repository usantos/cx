package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.layout.CaixaDSPrizeMessage
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

internal val layoutPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.layout.CaixaDSPrizeMessage") { state ->
        CaixaDSPrizeMessage(message = state.sample, contentColor = CaixaDS.colors.brand)
    }
)

internal val catalogLayoutEntries = listOf(
    CatalogEntry(
        id = "br.gov.caixa.loterias.core.designsystem.layout.CaixaDSPrizeMessage",
        name = "CaixaDSPrizeMessage",
        category = R.string.category_components,
        description = R.string.prize_message_description,
        signatures = R.string.prize_message_signatures,
        snippet = R.string.prize_message_snippet,
        searchTerms = "message contentColor modifier"
    )
)
