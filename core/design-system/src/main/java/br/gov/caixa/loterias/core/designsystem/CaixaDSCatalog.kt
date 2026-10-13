package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/** Catálogo executável usado pelo Android Studio para inspecionar o CaixaDS. */
@Composable
fun CaixaDSCatalog(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(CaixaDS.spacing.md),
        verticalArrangement = Arrangement.spacedBy(CaixaDS.spacing.md),
    ) {
        CaixaDSSectionTitle("CaixaDS")
        Text("Título", style = CaixaDS.typography.headlineSmall)
        Text("Texto de apoio e conteúdo.", color = CaixaDS.colors.textSecondary)
        CaixaDSCartSummary(java.math.BigDecimal("25.50"), betCount = 4, onClick = {})
        CaixaDSCartActions(onContinueBetting = {}, onPayment = {})
        Image(CaixaDSNotificationPlaceholder, "Ilustração de notificação", Modifier.size(96.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm)) {
            Image(CaixaDSNotificationSwipeRead, "Leitura de mensagem")
            Image(CaixaDSNotificationSwipeDelete, "Exclusão de mensagem")
        }
        CaixaDSButton("Ação principal", onClick = {}, modifier = Modifier.fillMaxWidth())
        CaixaDSButton(
            "Ação secundária",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            variant = CaixaDSButtonVariant.Secondary,
        )
        CaixaDSTextField(value = "", onValueChange = {}, label = "Campo", modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(CaixaDS.spacing.sm)) {
            listOf(3, 12, 27, 41, 56).forEach { CaixaDSNumberBall(it) }
        }
    }
}

@Preview(name = "CaixaDS claro", showBackground = true)
@Composable
private fun CaixaDSCatalogLightPreview() {
    CaixaDSTheme(darkTheme = false) { CaixaDSCatalog() }
}

@Preview(name = "CaixaDS escuro", showBackground = true)
@Composable
private fun CaixaDSCatalogDarkPreview() {
    CaixaDSTheme(darkTheme = true) { CaixaDSCatalog() }
}
