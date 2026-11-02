package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.payment.R as LocalizedR

@Composable
internal fun ProviderCardActions(methodId: Long, expandable: Boolean, expanded: Boolean, add: (Long) -> Unit, toggle: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = LocalDensity.current.fontScale > 1f
    val actionModifier = if (largeFont) Modifier.fillMaxWidth() else Modifier
    val actions: @Composable () -> Unit = {
        ProviderActionLabel(
            uiStrings.getString(LocalizedR.string.ui_adicionar_cartao_dd9fbe),
            (actionModifier)
                .uiId("pay.0043")
        ) { add(methodId) }
        if (expandable) {
            val label = if (expanded) LocalizedR.string.ui_listar_menos_cff0a0 else LocalizedR.string.ui_listar_mais_99b0d5
            ProviderActionLabel(uiStrings.getString(label), (actionModifier).uiId("pay.0044"), toggle)
        }
    }
    if (largeFont) {
        Column(Modifier.fillMaxWidth()) { actions() }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { actions() }
    }
}

@Composable
private fun ProviderActionLabel(label: String, modifier: Modifier, onClick: () -> Unit) {
    Text(
        label,
        (modifier.uiId("pay.0045").clickable(role = Role.Button, onClick = onClick).padding(horizontal = 16.dp, vertical = 8.dp))
            .uiId("pay.0046"),
        color = LoteriasCaixaDS.colors.brand,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        textDecoration = TextDecoration.Underline
    )
}
