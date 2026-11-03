package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.onboarding.ui.R as DeliveryR

@Composable
internal fun LargeFontIntroductionFooter(currentPage: Int, lastPage: Boolean, onComplete: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(Modifier.legacyAutomationId("introduction-page-indicators"), horizontalArrangement = Arrangement.Center) {
            introPages.indices.forEach { index ->
                Box(
                    Modifier.padding(horizontal = 3.dp).size(12.dp).border(1.dp, LoteriasCaixaDSColorTokens.White, CircleShape)
                        .background(
                            if (index ==
                                currentPage
                            ) {
                                LoteriasCaixaDSColorTokens.White
                            } else {
                                LoteriasCaixaDSColorTokens.Transparent
                            },
                            CircleShape
                        )
                )
            }
        }
        Box(
            Modifier.fillMaxWidth().heightIn(min = 44.dp).uiId("start.0020").clickable(role = Role.Button, onClick = onComplete)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(
                    if (lastPage) DeliveryR.string.ui_acessar_o_aplicativo_58076a else DeliveryR.string.ui_pular_introducao_24a12b
                ),
                (Modifier.fillMaxWidth()).uiId("start.0021"),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontFamily = LoteriasCaixaDSFontFamily,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun LegacyIntroductionFooter(currentPage: Int, lastPage: Boolean, onComplete: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        modifier = modifier.fillMaxWidth().navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp).height(44.dp)
    ) {
        Row(
            Modifier.align(Alignment.Center).legacyAutomationId("introduction-page-indicators"),
            horizontalArrangement = Arrangement.Center
        ) {
            introPages.indices.forEach { index ->
                Box(
                    Modifier.padding(horizontal = 3.dp).size(12.dp).border(
                        width = 1.dp,
                        color = LoteriasCaixaDSColorTokens.White,
                        shape = CircleShape
                    ).background(
                        if (index == currentPage) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDSColorTokens.Transparent,
                        CircleShape
                    )
                )
            }
        }
        Box(
            Modifier.align(Alignment.CenterEnd).padding(end = 3.dp)
                .size(width = 140.dp, height = 44.dp)
                .uiId("start.0022").clickable(role = Role.Button, onClick = onComplete),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (lastPage) {
                    uiStrings.getString(
                        DeliveryR.string.ui_acessar_o_aplicativo_58076a
                    )
                } else {
                    uiStrings.getString(DeliveryR.string.ui_pular_introducao_24a12b)
                },
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontFamily = LoteriasCaixaDSFontFamily,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                maxLines = 2, modifier = Modifier.uiId("start.0023")
            )
        }
    }
}
