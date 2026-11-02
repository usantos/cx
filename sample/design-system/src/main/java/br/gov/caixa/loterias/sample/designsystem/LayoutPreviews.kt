package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.layout.LoteriasCaixaDSPrizeMessage
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

internal val layoutPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.layout.LoteriasCaixaDSPrizeMessage") { state ->
        LoteriasCaixaDSPrizeMessage(message = state.sample, contentColor = LoteriasCaixaDS.colors.brand)
    }
)

internal val catalogLayoutEntries = listOf(
    CatalogEntry(
        id = "br.gov.caixa.loterias.core.designsystem.layout.LoteriasCaixaDSPrizeMessage",
        name = "LoteriasCaixaDSPrizeMessage",
        category = R.string.category_state,
        description = R.string.prize_message_description,
        signatures = R.string.prize_message_signatures,
        snippet = R.string.prize_message_snippet,
        searchTerms = "message contentColor modifier"
    )
)
