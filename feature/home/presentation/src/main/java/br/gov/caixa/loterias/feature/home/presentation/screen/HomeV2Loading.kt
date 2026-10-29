package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.R as LocalizedR

@Composable internal fun HomeV2Presentation.HomeShimmer() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val transition = rememberInfiniteTransition(label = uiStrings.getString(LocalizedR.string.ui_homeloading_d4d822))
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = uiStrings.getString(LocalizedR.string.ui_shimmerprogress_350cb6)
    )
    Column(Modifier.fillMaxWidth().semantics { contentDescription = "Carregando a home" }) {
        Box(Modifier.padding(12.dp)) {
            ShimmerBlock((Modifier.fillMaxWidth().height(220.dp)).uiId("home.0156"), progress, 12)
        }
        Column(Modifier.padding(horizontal = 13.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ShimmerBlock((Modifier.fillMaxWidth().height(64.dp)).uiId("home.0157"), progress, 13)
            ShimmerBlock((Modifier.width(210.dp).height(24.dp)).uiId("home.0158"), progress)
            repeat(4) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(2) { ShimmerBlock((Modifier.weight(1f).height(85.dp)).uiId("home.0159"), progress, 11) }
                }
            }
            Spacer(Modifier.height(5.dp))
            ShimmerBlock((Modifier.width(165.dp).height(24.dp)).uiId("home.0160"), progress)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) { ShimmerBlock((Modifier.weight(1f).height(95.dp)).uiId("home.0161"), progress, 11) }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable private fun HomeV2Presentation.ShimmerBlock(modifier: Modifier, progress: Float, radius: Int = 6) {
    Canvas(modifier.clip(RoundedCornerShape(radius.dp))) {
        val start = size.width * progress
        drawRect(
            Brush.linearGradient(
                listOf(CxHomeV2Colors.CFFDCE2EC, CxHomeV2Colors.CFFF4F6FA, CxHomeV2Colors.CFFDCE2EC),
                start = Offset(start, 0f),
                end = Offset(start + size.width, size.height)
            )
        )
    }
}
