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
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButtonVariant
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSNumberBall
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionTitle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartActions
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSNotificationSwipeDelete
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSNotificationSwipeRead
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme

/** Catálogo executável usado pelo Android Studio para inspecionar o CaixaDS. */
@Composable
fun CaixaDSCatalog(modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier = modifier.padding(CaixaDS.spacing.md),
        verticalArrangement = Arrangement.spacedBy(CaixaDS.spacing.md)
    ) {
        CaixaDSSectionTitle("CaixaDS", modifier = Modifier.uiId("ds.0130"))
        Text(uiStrings.getString(LocalizedR.string.ui_titulo_4c08a5), style = CaixaDS.typography.headlineSmall)
        Text(uiStrings.getString(LocalizedR.string.ui_texto_de_apoio_e_conteudo_4a9983), color = CaixaDS.colors.textSecondary)
        CaixaDSCartSummary(java.math.BigDecimal("25.50"), betCount = 4, onClick = {}, modifier = Modifier.uiId("ds.0131"))
        CaixaDSCartActions(onContinueBetting = {}, onPayment = {}, modifier = Modifier.uiId("ds.0132"))
        Image(
            CaixaDSNotificationPlaceholder,
            uiStrings.getString(LocalizedR.string.ui_ilustracao_de_notificacao_4cf671),
            Modifier.size(96.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm)) {
            Image(CaixaDSNotificationSwipeRead, uiStrings.getString(LocalizedR.string.ui_leitura_de_mensagem_d790e5))
            Image(CaixaDSNotificationSwipeDelete, uiStrings.getString(LocalizedR.string.ui_exclusao_de_mensagem_3af531))
        }
        CaixaDSButton(
            uiStrings.getString(LocalizedR.string.ui_acao_principal_3e09e8),
            onClick = {},
            modifier = (Modifier.fillMaxWidth())
                .uiId("ds.0133")
        )
        CaixaDSButton(
            uiStrings.getString(LocalizedR.string.ui_acao_secundaria_7f20f6),
            onClick = {},
            modifier = (Modifier.fillMaxWidth()).uiId("ds.0134"),
            variant = CaixaDSButtonVariant.Secondary
        )
        CaixaDSTextField(
            value = "",
            onValueChange = {},
            label = uiStrings.getString(LocalizedR.string.ui_campo_45789c),
            modifier = (Modifier.fillMaxWidth()).uiId("ds.0135")
        )
        Row(horizontalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm)) {
            listOf(3, 12, 27, 41, 56).forEach { CaixaDSNumberBall(it, modifier = Modifier.uiId("ds.0136")) }
        }
    }
}

@Preview(name = "CaixaDS claro", showBackground = true)
@Composable
private fun CaixaDSCatalogLightPreview() {
    CaixaDSTheme(darkTheme = false) { CaixaDSCatalog(modifier = Modifier.uiId("ds.0137")) }
}

@Preview(name = "CaixaDS escuro", showBackground = true)
@Composable
private fun CaixaDSCatalogDarkPreview() {
    CaixaDSTheme(darkTheme = true) { CaixaDSCatalog(modifier = Modifier.uiId("ds.0138")) }
}
