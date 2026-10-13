package br.gov.caixa.loterias.core.designsystem

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
import androidx.compose.ui.unit.dp

/** Decorative native-Switch geometry; the containing login row owns toggle semantics. */
@Composable
fun CaixaDSLoginSwitch(checked: Boolean, modifier: Modifier = Modifier) {
    val offset by animateDpAsState(if (checked) 7.dp else (-7).dp, label = "login-switch-thumb")
    Box(modifier.size(50.dp, 40.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.size(34.dp, 14.dp).background(
            (if (checked) CaixaDSColorTokens.Orange500 else CaixaDSColorTokens.Black).copy(alpha = .3f),
            RoundedCornerShape(7.dp),
        ))
        Box(Modifier.offset(x = offset).size(20.dp).shadow(2.dp, CircleShape)
            .background(if (checked) CaixaDSColorTokens.Orange500 else CaixaDSColorTokens.LegacyLoginSwitchThumb, CircleShape))
    }
}
