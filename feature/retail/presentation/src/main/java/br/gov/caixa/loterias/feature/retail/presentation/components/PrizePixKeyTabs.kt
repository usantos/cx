package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun PrizePixTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxHeight().background(
            if (selected) LoteriasCaixaDS.colors.teal else LoteriasCaixaDSColorTokens.TicketDisabledBackground
        )
            .uiId("retail.0026").selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDS.colors.teal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("retail.0027")
        )
        if (selected) {
            Box(
                Modifier.align(Alignment.BottomCenter).size(30.dp, 12.dp).background(
                    LoteriasCaixaDSColorTokens.White,
                    GenericShape { size, _ ->
                        moveTo(0f, size.height)
                        lineTo(size.width / 2f, 0f)
                        lineTo(size.width, size.height)
                        close()
                    }
                )
            )
        }
    }
}
