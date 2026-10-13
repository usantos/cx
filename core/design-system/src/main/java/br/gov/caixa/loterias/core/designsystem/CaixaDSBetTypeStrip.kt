package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CaixaDSBetTypeItem(val label: String, val icon: ImageVector)

/** Wraps the labels in caller order, as TipoApostaLinhaView with TriangleDirection.NONE. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaixaDSBetTypeStrip(
    items: List<CaixaDSBetTypeItem>,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    FlowRow(modifier.fillMaxWidth().background(backgroundColor).padding(6.dp)) {
        items.forEach { item ->
            Row(
                Modifier.padding(end = 17.dp).semantics(mergeDescendants = true) { heading() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(item.icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(item.label, color = contentColor, style = CaixaDS.typography.bodyLarge,
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
