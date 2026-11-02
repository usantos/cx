package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.text.withLoteriasCaixaDSAccessibleLinks
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.R as LocalizedR

@Composable
internal fun TermsConsent(checked: Boolean, onToggle: () -> Unit, onTerms: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        val consentLabel = uiStrings.getString(LocalizedR.string.ui_li_e_concordo_com_os_3bcaeb) +
            uiStrings.getString(LocalizedR.string.ui_termos_de_uso_3eec48)
        Box(Modifier.semantics(mergeDescendants = true) { contentDescription = consentLabel }) {
            LoteriasCaixaDSAccountCheckbox(checked, onToggle, 32.dp)
        }
        val text = buildAnnotatedString {
            append(uiStrings.getString(LocalizedR.string.ui_li_e_concordo_com_os_3bcaeb))
            pushStringAnnotation("URL", "terms")
            withStyle(SpanStyle(color = LoteriasCaixaDSColorTokens.BetAction, textDecoration = TextDecoration.Underline)) {
                append(uiStrings.getString(LocalizedR.string.ui_termos_de_uso_3eec48))
            }
            pop()
            append(".")
        }
        Text(
            text.withLoteriasCaixaDSAccessibleLinks { onTerms() },
            (Modifier.padding(start = 8.dp).weight(1f)).uiId("account.0034"),
            style = LoteriasCaixaDSAccountTextStyle.copy(color = LoteriasCaixaDSColorTokens.AccountTermsText, fontSize = 16.sp)
        )
    }
}

@Composable
internal fun NotificationConsent(checked: Boolean, onToggle: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp).uiId("account.0035").toggleable(value = checked, role = Role.Checkbox) { onToggle() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.clearAndSetSemantics { }) { LoteriasCaixaDSAccountCheckbox(checked, null, 32.dp) }
        Text(
            uiStrings.getString(LocalizedR.string.ui_aceito_receber_notificacoes_de_loter_474748),
            modifier = (Modifier.padding(start = 8.dp).weight(1f)).uiId("account.0036"),
            color = LoteriasCaixaDSColorTokens.AccountTermsText,
            fontSize = 16.sp,
            style = LoteriasCaixaDSAccountTextStyle
        )
    }
}
