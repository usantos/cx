package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerDefaults
import br.gov.caixa.loterias.core.designsystem.icon.DrawerLogout
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration

@Composable
internal fun DrawerFooter(
    institutionExpanded: Boolean,
    onToggleInstitution: () -> Unit,
    environmentLabel: String?,
    footerLabel: Color,
    onLogout: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(LoteriasCaixaDSDrawerDefaults.footerBackground)) {
        br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerInstitution(
            expanded = institutionExpanded,
            onClick = onToggleInstitution,
            details = uiStrings.getString(LocalizedR.string.ui_cnpj_24_038_490_0001_83_ed_jose_alen_ade8ce)
        )
        Row(
            Modifier.fillMaxWidth().heightIn(min = 50.dp)
                .automationId("simlo.home.sair").clickable(role = Role.Button) { onLogout() }
                .padding(start = 24.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(LoteriasCaixaDSIcons.DrawerLogout, null, (Modifier.size(18.dp)).uiId("home.0029"), tint = footerLabel)
            Text(
                uiStrings.getString(LocalizedR.string.ui_sair_2d023d),
                (Modifier.padding(start = 8.dp)).uiId("home.0030"),
                color = footerLabel,
                style = LoteriasCaixaDS.typography.bodyMedium.copy(fontSize = 18.sp)
            )
            Spacer(Modifier.weight(1f))
            if (environmentLabel != null) {
                Text(
                    environmentLabel,
                    (Modifier.padding(horizontal = 12.dp)).uiId("home.0031"),
                    color = footerLabel,
                    style = LoteriasCaixaDS.typography.bodySmall.copy(fontSize = 14.sp)
                )
            }
            Text(
                LocalHomeRuntimeConfiguration.current.versionName,
                color = footerLabel,
                style = LoteriasCaixaDS.typography.bodySmall.copy(fontSize = 14.sp),
                modifier = Modifier.uiId("home.0032")
            )
        }
    }
}
