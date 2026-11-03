package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.presentation.mapper.icon
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable
fun HomeV2Presentation.BottomNavigation(modifier: Modifier, density: Density, onHeightChanged: (Dp) -> Unit, onHome: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        modifier.fillMaxWidth()
            .onGloballyPositioned { onHeightChanged(with(density) { it.size.height.toDp() }) }
            .shadow(6.dp)
            .background(Brush.verticalGradient(listOf(CxHomeV2Colors.White.copy(alpha = .98f), CxHomeV2Colors.White.copy(alpha = .96f))))
            .border(1.dp, CxHomeV2Colors.White.copy(alpha = .65f))
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Nav(
            uiStrings.getString(DeliveryR.string.ui_inicio_325dfc),
            DeliveryR.drawable.trevo_megasena_fundo_branco,
            true,
            (Modifier.weight(1f)).automationId("simlo.home.apostar")
        ) { onHome() }
        Nav(
            "Minhas\nApostas",
            DeliveryR.drawable.ic_home_v2_bilhete,
            false,
            (Modifier.weight(1f)).uiId("home.0096"),
            action = actions::games
        )
        Nav(
            "Carrinho",
            DeliveryR.drawable.ic_carrinho2,
            false,
            (Modifier.weight(1f))
                .automationId("simlo.home.carrinho"),
            emphasized = true,
            action = actions::cart
        )
        Nav(
            "Resultados",
            DeliveryR.drawable.ic_home_v2_resultados,
            false,
            (Modifier.weight(1f))
                .automationId("simlo.home.resultados"),
            action = actions::results
        )
        Nav(
            "Apostas\nFavoritas",
            DeliveryR.drawable.ic_favoritar2,
            false,
            (Modifier.weight(1f)).uiId("home.0099"),
            action = actions::favorites
        )
    }
}

@Composable private fun HomeV2Presentation.Nav(
    title: String,
    icon: Int,
    selected: Boolean,
    modifier: Modifier,
    emphasized: Boolean = false,
    originalColors: Boolean = false,
    action: () -> Unit,
) {
    val ink = if (selected || emphasized) blue else CxHomeV2Colors.CFF30445A
    Column(
        modifier.uiId("home.0100").selectable(selected = selected, role = Role.Tab, onClick = action).padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(42.dp).then(if (emphasized) Modifier.background(CxHomeV2Colors.CFFE9F0FC, CircleShape) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            if (originalColors) {
                Image(painterResource(icon), null, Modifier.size(28.dp), contentScale = ContentScale.Fit)
            } else {
                Icon(
                    painterResource(icon),
                    null,
                    tint = ink,
                    modifier = (Modifier.size(28.dp)).uiId("home.0101")
                )
            }
        }
        Text(
            title,
            color = ink,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontFamily = regular,
            textAlign = TextAlign.Center,
            modifier = (Modifier.heightIn(min = 28.dp)).uiId("home.0102")
        )
    }
}
