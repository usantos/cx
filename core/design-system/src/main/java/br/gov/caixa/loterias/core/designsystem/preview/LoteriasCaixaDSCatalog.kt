package br.gov.caixa.loterias.core.designsystem.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButtonVariant
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSNumberBall
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionTitle
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartActions
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSNotificationSwipeDelete
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSNotificationSwipeRead
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme

/** Catálogo executável usado pelo Android Studio para inspecionar o LoteriasCaixaDS. */
@Composable
fun LoteriasCaixaDSCatalog(modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier = modifier.padding(LoteriasCaixaDS.spacing.md),
        verticalArrangement = Arrangement.spacedBy(LoteriasCaixaDS.spacing.md)
    ) {
        LoteriasCaixaDSSectionTitle("LoteriasCaixaDS", modifier = Modifier.uiId("ds.0130"))
        Text(uiStrings.getString(LocalizedR.string.ui_titulo_4c08a5), style = LoteriasCaixaDS.typography.headlineSmall)
        Text(uiStrings.getString(LocalizedR.string.ui_texto_de_apoio_e_conteudo_4a9983), color = LoteriasCaixaDS.colors.textSecondary)
        LoteriasCaixaDSCartSummary(java.math.BigDecimal("25.50"), betCount = 4, onClick = {}, modifier = Modifier.uiId("ds.0131"))
        LoteriasCaixaDSCartActions(onContinueBetting = {}, onPayment = {}, modifier = Modifier.uiId("ds.0132"))
        Image(
            LoteriasCaixaDSNotificationPlaceholder,
            uiStrings.getString(LocalizedR.string.ui_ilustracao_de_notificacao_4cf671),
            Modifier.size(96.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(LoteriasCaixaDS.spacing.sm)) {
            Image(LoteriasCaixaDSNotificationSwipeRead, uiStrings.getString(LocalizedR.string.ui_leitura_de_mensagem_d790e5))
            Image(LoteriasCaixaDSNotificationSwipeDelete, uiStrings.getString(LocalizedR.string.ui_exclusao_de_mensagem_3af531))
        }
        LoteriasCaixaDSButton(
            uiStrings.getString(LocalizedR.string.ui_acao_principal_3e09e8),
            onClick = {},
            modifier = (Modifier.fillMaxWidth())
                .uiId("ds.0133")
        )
        LoteriasCaixaDSButton(
            uiStrings.getString(LocalizedR.string.ui_acao_secundaria_7f20f6),
            onClick = {},
            modifier = (Modifier.fillMaxWidth()).uiId("ds.0134"),
            variant = LoteriasCaixaDSButtonVariant.Secondary
        )
        LoteriasCaixaDSTextField(
            value = "",
            onValueChange = {},
            label = uiStrings.getString(LocalizedR.string.ui_campo_45789c),
            modifier = (Modifier.fillMaxWidth()).uiId("ds.0135")
        )
        Row(horizontalArrangement = Arrangement.spacedBy(LoteriasCaixaDS.spacing.sm)) {
            val numberModifier = Modifier.uiId("ds.0136")
            LoteriasCaixaDSNumberBall(CatalogMetrics.FIRST_SAMPLE_BALL, modifier = numberModifier)
            LoteriasCaixaDSNumberBall(CatalogMetrics.SECOND_SAMPLE_BALL, modifier = numberModifier)
            LoteriasCaixaDSNumberBall(CatalogMetrics.THIRD_SAMPLE_BALL, modifier = numberModifier)
            LoteriasCaixaDSNumberBall(CatalogMetrics.FOURTH_SAMPLE_BALL, modifier = numberModifier)
            LoteriasCaixaDSNumberBall(CatalogMetrics.FIFTH_SAMPLE_BALL, modifier = numberModifier)
        }
    }
}

@Preview(name = "LoteriasCaixaDS claro", showBackground = true)
@Composable
private fun LoteriasCaixaDSCatalogLightPreview() {
    LoteriasCaixaDSTheme(darkTheme = false) { LoteriasCaixaDSCatalog(modifier = Modifier.uiId("ds.0137")) }
}

@Preview(name = "LoteriasCaixaDS escuro", showBackground = true)
@Composable
private fun LoteriasCaixaDSCatalogDarkPreview() {
    LoteriasCaixaDSTheme(darkTheme = true) { LoteriasCaixaDSCatalog(modifier = Modifier.uiId("ds.0138")) }
}

private object CatalogMetrics {
    const val FIRST_SAMPLE_BALL = 3
    const val SECOND_SAMPLE_BALL = 12
    const val THIRD_SAMPLE_BALL = 27
    const val FOURTH_SAMPLE_BALL = 41
    const val FIFTH_SAMPLE_BALL = 56
}
