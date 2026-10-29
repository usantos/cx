package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText

/** Cópia Compose dos estados de aposta-espelho usados na simulação legada. */
@Composable
internal fun BettingLegacyMirror(selected: Boolean, onToggle: () -> Unit, manual: Boolean) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val foreground = if (manual) {
        CaixaDSColorTokens.LotomaniaMirrorText
    } else {
        CaixaDSColorTokens.SocialGraphite
    }
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (manual) Modifier.background(CaixaDSColorTokens.LotomaniaMirrorBackground) else Modifier)
            .uiId("bet.0268").toggleable(value = selected, role = Role.Checkbox) { onToggle() }
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = if (manual) 12.dp else 20.dp,
                bottom = if (manual) 16.dp else 0.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (manual) Arrangement.Start else Arrangement.Center
    ) {
        androidx.compose.foundation.layout.Box(Modifier.clearAndSetSemantics { }) {
            CaixaDSCheckbox(
                checked = selected,
                onClick = null,
                modifier = (Modifier.size(20.dp)).uiId("bet.0269"),
                color = if (manual) CaixaDSColorTokens.LotomaniaDark else foreground
            )
        }
        Text(
            text = if (manual) {
                mirrorDescription()
            } else {
                buildAnnotatedString {
                    append(
                        uiStrings.getString(LocalizedR.string.ui_aposta_espelho_b7c1f4)
                    )
                }
            },
            modifier = (Modifier.padding(start = if (manual) 12.dp else 16.dp)).uiId("bet.0270"),
            fontFamily = CaixaDSFontFamily,
            fontSize = if (manual) 12.sp else 14.2.sp,
            lineHeight = if (manual) 15.sp else 17.sp,
            color = foreground,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
    }
}

private fun mirrorDescription() = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append(messageText("aumente_suas_chances_de_ganhar_c_e123f3"))
    }
    append(messageText("ao_escolher_esta_opcao_o_sistema_28c883"))
}
