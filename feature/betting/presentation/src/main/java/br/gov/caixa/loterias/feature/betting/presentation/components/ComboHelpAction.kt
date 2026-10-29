package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as DesignR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.betting.R

@Composable
internal fun ComboHelpAction(onHelp: () -> Unit) {
    val resources = LocalResources.current
    Box(Modifier.fillMaxSize().uiId("bet.0078").clickable(role = Role.Button, onClick = onHelp), contentAlignment = Alignment.Center) {
        Image(painterResource(DesignR.drawable.caixads_modality_help), resources.getString(R.string.ui_ajuda_fe0add), Modifier.size(24.dp))
    }
}
