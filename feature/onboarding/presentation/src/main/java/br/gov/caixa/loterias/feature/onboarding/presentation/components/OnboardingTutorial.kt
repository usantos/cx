package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSTutorialCheckbox
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR

@Composable
internal fun TutorialScreen(checked: Boolean, onToggle: () -> Unit, onStart: () -> Unit, onSkip: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val checkboxInteraction = remember { MutableInteractionSource() }
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
        Box(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .background(CaixaDSColorTokens.TutorialIndicator)
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_chegou_a_milionaria_29108e),
                modifier = (Modifier.align(Alignment.Center).offset(y = (-110).dp)).uiId("start.0024"),
                color = CaixaDSColorTokens.White,
                fontFamily = CaixaDSFuturaFontFamily,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_conheca_o_novo_jogo_milionaria_vamos_9ca438),
                modifier = (Modifier.align(Alignment.Center).offset(y = (-42).dp)).uiId("start.0025"),
                color = CaixaDSColorTokens.White,
                fontFamily = CaixaDSFuturaFontFamily,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                textAlign = TextAlign.Center
            )
            LegacyTutorialButton(
                text = uiStrings.getString(LocalizedR.string.ui_comecar_tutorial_2e42d1),
                color = CaixaDSColorTokens.TutorialGreenButton,
                modifier = Modifier.align(Alignment.Center).offset(y = 151.dp).size(width = 180.dp, height = 84.dp),
                onClick = onStart
            )
            Row(
                modifier = Modifier.align(Alignment.Center).offset(x = 2.dp, y = 221.dp).uiId("start.0026").toggleable(
                    value = checked,
                    role = Role.Checkbox,
                    interactionSource = checkboxInteraction,
                    indication = null,
                    onValueChange = { onToggle() }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CaixaDSTutorialCheckbox(checked = checked, onClick = null, modifier = Modifier.uiId("start.0027"))
                Text(
                    uiStrings.getString(LocalizedR.string.ui_nao_mostrar_mais_esse_tutorial_ae03a7),
                    color = CaixaDSColorTokens.White,
                    fontSize = 14.sp,
                    modifier = Modifier.uiId("start.0028")
                )
            }
            LegacyTutorialButton(
                text = uiStrings.getString(LocalizedR.string.ui_pular_apresentacao_1e552b),
                color = CaixaDSColorTokens.TutorialGray,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp).size(width = 180.dp, height = 64.dp),
                onClick = onSkip
            )
        }
    }
}
