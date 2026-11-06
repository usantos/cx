package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.icon.DrawerChevron
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSDrawerItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean? = null,
    child: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().background(
                if (child) LoteriasCaixaDSDrawerDefaults.expandedBackground else LoteriasCaixaDSColorTokens.White
            )
                .heightIn(min = if (child) 58.dp else 55.dp)
                .drawBehind {
                    if (!child) {
                        drawLine(
                            LoteriasCaixaDSColorTokens.DrawerDivider,
                            Offset(0f, size.height - .5.dp.toPx()),
                            Offset(size.width, size.height - .5.dp.toPx()),
                            1.dp.toPx()
                        )
                    }
                }
                .semantics { expanded?.let { stateDescription = if (it) "Expandido" else "Recolhido" } }
                .uiId("ds.0109").clickable(role = Role.Button, onClick = onClick)
                .padding(start = if (child) 24.dp else 0.dp, end = if (child) 11.dp else 0.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) { it() }
                Spacer(Modifier.width(20.dp))
            }
            Text(
                label,
                Modifier.weight(1f),
                color = if (child &&
                    LocalLoteriasCaixaDSCampaign.current == LoteriasCaixaDSCampaign.DEFAULT
                ) {
                    LoteriasCaixaDSColorTokens.SocialGraphite
                } else {
                    LoteriasCaixaDSDrawerDefaults.secondaryForeground
                },
                style = LoteriasCaixaDSDrawerDefaults.itemTextStyle
            )
            expanded?.let {
                Icon(
                    LoteriasCaixaDSIcons.DrawerChevron,
                    null,
                    Modifier.padding(start = 2.dp).size(10.dp, 22.dp).rotate(if (it) EXPANDED_CHEVRON_ROTATION else 0f),
                    tint = LoteriasCaixaDSDrawerDefaults.foreground
                )
            }
        }
        if (child) Box(Modifier.padding(horizontal = 15.dp).fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.White))
    }
}

private const val EXPANDED_CHEVRON_ROTATION = 180f
