package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR

@Composable
internal fun CatalogCounterButton(increase: Boolean, onClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.size(48.dp).uiId("bet.0020").clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Image(
            painterResource(if (increase) R.drawable.botao_mais_detalhe_combo else R.drawable.botao_menos_detalhe_combo),
            if (increase) {
                uiStrings.getString(
                    LocalizedR.string.ui_adicionar_apostas_3a6adb
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_remover_apostas_9bdaac)
            },
            Modifier.width(30.dp).height(29.dp)
        )
    }
}
