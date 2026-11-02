package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProvider
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardActionButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardHeading
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardPicker
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardProviderHeader
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardSecurityCode
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardTextInput
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardBack
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardContent
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardFront
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSavedCardSection
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSavedCreditCard
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons

private const val CARD_PACKAGE = "br.gov.caixa.loterias.core.designsystem.components."
private val exampleCard =
    LoteriasCaixaDSCreditCardContent("0000 0000 0000 0000", "TITULAR DE TESTE", "01/2030", "Número", "Nome", "Validade")
internal val paymentCardPreviews = mapOf(
    preview(CARD_PACKAGE + "LoteriasCaixaDSCreditCardFront") {
        LoteriasCaixaDSCreditCardFront(exampleCard, brand = { LoteriasCaixaDSPaymentBrandMark("VISA", it) })
    },
    preview(CARD_PACKAGE + "LoteriasCaixaDSCreditCardBack") { state ->
        LoteriasCaixaDSCreditCardBack(
            state.checked,
            brand = { LoteriasCaixaDSPaymentBrandMark("VISA", it) },
            magnifier = rememberVectorPainter(LoteriasCaixaDSIcons.Help),
            magnifierText = "XXX"
        )
    },
    preview(CARD_PACKAGE + "LoteriasCaixaDSCardSecurityCode") { state ->
        LoteriasCaixaDSCardSecurityCode(state.text, 3, { state.text = it })
    },
    preview(CARD_PACKAGE + "LoteriasCaixaDSCardActionButton") { state ->
        LoteriasCaixaDSCardActionButton("Incluir Código de Verificação", state.action)
    },
    preview(CARD_PACKAGE + "LoteriasCaixaDSSavedCreditCard") { state -> SavedCardExample(state) },
    preview(CARD_PACKAGE + "LoteriasCaixaDSSavedCardSection") { state ->
        LoteriasCaixaDSSavedCardSection(
            LoteriasCaixaDSPaymentProvider.MERCADO_PAGO,
            "Cartões de demonstração",
            logo = { LoteriasCaixaDSText("Processador", it) },
            cards = { SavedCardExample(state, it) }
        )
    },
    preview(CARD_PACKAGE + "LoteriasCaixaDSCardProviderHeader") { state ->
        LoteriasCaixaDSCardProviderHeader(
            "Voltar",
            "Alterar",
            logo = { LoteriasCaixaDSText("Processador", it) },
            onChange = state.action
        )
    },
    preview(CARD_PACKAGE + "LoteriasCaixaDSCardTextInput") { state ->
        LoteriasCaixaDSCardTextInput("Nome", state.text, { state.text = it })
    },
    preview(CARD_PACKAGE + "LoteriasCaixaDSCardPicker") { state ->
        LoteriasCaixaDSCardPicker(
            "Mês",
            state.text,
            listOf("01", "02"),
            {
                state.text = it
                state.count++
            },
            "Selecionar mês"
        )
    },
    preview(CARD_PACKAGE + "LoteriasCaixaDSCardHeading") { LoteriasCaixaDSCardHeading("Confirme os dados do cartão escolhido") }
)

@Composable
private fun SavedCardExample(state: PreviewState, modifier: Modifier = Modifier) {
    LoteriasCaixaDSSavedCreditCard(
        "Final0000", "01/2030", rememberVectorPainter(LoteriasCaixaDSIcons.Help), "Arte demonstrativa",
        rememberVectorPainter(LoteriasCaixaDSIcons.Delete), "Remover cartão", state.action, state.action, modifier
    )
}

internal val catalogPaymentCardEntries = listOf(
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSCreditCardFront",
        "LoteriasCaixaDSCreditCardFront",
        R.string.category_cards,
        R.string.card_components_description,
        R.string.card_0_signature,
        R.string.card_0_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSCreditCardBack",
        "LoteriasCaixaDSCreditCardBack",
        R.string.category_cards,
        R.string.card_components_description,
        R.string.card_1_signature,
        R.string.card_1_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSCardSecurityCode",
        "LoteriasCaixaDSCardSecurityCode",
        R.string.category_fields,
        R.string.card_components_description,
        R.string.card_2_signature,
        R.string.card_2_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSCardActionButton",
        "LoteriasCaixaDSCardActionButton",
        R.string.category_buttons,
        R.string.card_components_description,
        R.string.card_3_signature,
        R.string.card_3_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSSavedCreditCard",
        "LoteriasCaixaDSSavedCreditCard",
        R.string.category_cards,
        R.string.card_components_description,
        R.string.card_4_signature,
        R.string.card_4_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSSavedCardSection",
        "LoteriasCaixaDSSavedCardSection",
        R.string.category_cards,
        R.string.card_components_description,
        R.string.card_5_signature,
        R.string.card_5_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSCardProviderHeader",
        "LoteriasCaixaDSCardProviderHeader",
        R.string.category_cards,
        R.string.card_components_description,
        R.string.card_6_signature,
        R.string.card_6_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSCardTextInput",
        "LoteriasCaixaDSCardTextInput",
        R.string.category_fields,
        R.string.card_components_description,
        R.string.card_7_signature,
        R.string.card_7_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSCardPicker",
        "LoteriasCaixaDSCardPicker",
        R.string.category_input,
        R.string.card_components_description,
        R.string.card_8_signature,
        R.string.card_8_snippet,
        "cartão legado cvv pagamento acessibilidade"
    ),
    CatalogEntry(
        CARD_PACKAGE + "LoteriasCaixaDSCardHeading",
        "LoteriasCaixaDSCardHeading",
        R.string.category_typography,
        R.string.card_components_description,
        R.string.card_9_signature,
        R.string.card_9_snippet,
        "cartão legado cvv pagamento acessibilidade"
    )
)
