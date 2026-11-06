package br.gov.caixa.loterias.core.designsystem.components.input

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Geometria decorativa do Switch nativo; a linha de login que o contém controla a semântica de alternância. */
@Composable
fun LoteriasCaixaDSLoginSwitch(checked: Boolean, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val offset by animateDpAsState(
        if (checked) 7.dp else (-LoginSwitchMetrics.THUMB_OFFSET_DP).dp,
        label = uiStrings.getString(LocalizedR.string.ui_login_switch_thumb_ababd4)
    )
    Box(modifier.size(50.dp, 40.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier.size(34.dp, 14.dp).background(
                (if (checked) LoteriasCaixaDSColorTokens.Orange500 else LoteriasCaixaDSColorTokens.Black).copy(alpha = .3f),
                RoundedCornerShape(7.dp)
            )
        )
        Box(
            Modifier.offset { IntOffset(offset.roundToPx(), 0) }.size(20.dp).shadow(2.dp, CircleShape)
                .background(if (checked) LoteriasCaixaDSColorTokens.Orange500 else LoteriasCaixaDSColorTokens.LoginSwitchThumb, CircleShape)
        )
    }
}

private object LoginSwitchMetrics {
    const val THUMB_OFFSET_DP = 7
}
