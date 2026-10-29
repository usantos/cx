package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** Controles sobre a área Lottie original do Bolão; as métricas vêm de fragment_tutorial_nova_modalidade. */
@Composable
fun CaixaDSPoolTutorialPrimary(label: AnnotatedString, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.width(164.dp)
            .background(CaixaDSColorTokens.PoolTutorialAction, RoundedCornerShape(10.dp))
            .uiId("ds.0052").clickable(role = Role.Button, onClick = onClick).padding(16.dp),
        Alignment.Center
    ) {
        Text(
            label,
            style = TextStyle(
                fontFamily = CaixaDSRegularFontFamily,
                platformStyle = PlatformTextStyle(includeFontPadding = true),
                fontSize = 16.sp,
                color = CaixaDSColorTokens.White,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun CaixaDSTutorialPreference(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    checkboxModifier: Modifier = Modifier,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        modifier.uiId("ds.0053").toggleable(value = checked, role = Role.Checkbox, onValueChange = { onToggle() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(
                if (checked) {
                    R.drawable.legacy_tutorial_checkbox_selected
                } else {
                    R.drawable.legacy_tutorial_checkbox
                }
            ),
            contentDescription = null,
            modifier = checkboxModifier.size(13.dp)
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_nao_exibir_novamente_66f1ec),
            style = TextStyle(
                fontFamily = CaixaDSRegularFontFamily,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            fontSize = 14.sp,
            color = CaixaDSColorTokens.PoolTutorialAction
        )
    }
}
