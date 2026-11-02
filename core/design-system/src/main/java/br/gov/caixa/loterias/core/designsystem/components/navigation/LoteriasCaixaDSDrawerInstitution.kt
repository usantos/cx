package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.icon.DrawerChevron
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSDrawerInstitution(expanded: Boolean, onClick: () -> Unit, details: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.SocialCanvas).padding(bottom = 5.dp)
            .heightIn(min = 42.dp).semantics { stateDescription = if (expanded) "Expandido" else "Recolhido" }
            .uiId("ds.0110").clickable(role = Role.Button, onClick = onClick)
    ) {
        Column(Modifier.fillMaxWidth().padding(start = 60.dp, top = 10.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_caixa_loterias_567fcc),
                Modifier.fillMaxWidth().padding(end = 50.dp),
                color = if (
                    LocalLoteriasCaixaDSCampaign.current == LoteriasCaixaDSCampaign.DEFAULT
                ) {
                    LoteriasCaixaDSColorTokens.DrawerInstitutionHeading
                } else {
                    LoteriasCaixaDSDrawerDefaults.foreground
                },
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
            if (expanded) {
                Text(
                    details,
                    Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 6.dp),
                    color = LoteriasCaixaDSColorTokens.DrawerInstitutionText,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = LoteriasCaixaDSRegularFontFamily,
                        fontSize = 14.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    )
                )
            }
        }
        Icon(
            LoteriasCaixaDSIcons.DrawerChevron,
            null,
            Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 24.dp)
                .size(10.dp, 22.dp).rotate(if (expanded) EXPANDED_CHEVRON_ROTATION else 0f),
            tint = LoteriasCaixaDSDrawerDefaults.foreground
        )
    }
}

private const val EXPANDED_CHEVRON_ROTATION = 180f
