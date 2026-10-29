package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.support.R
import br.gov.caixa.loterias.feature.support.R as LocalizedR
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.presentation.mapper.supportRegularStyle
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Composable
internal fun FaqContent(state: SupportUiState, onAction: (SupportAction) -> Unit) {
    if (state.selectedSectionId == null) {
        FaqSections(state, onAction)
    } else {
        FaqQuestions(state, onAction)
    }
}

@Composable
private fun FaqSections(state: SupportUiState, onAction: (SupportAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = androidx.compose.ui.platform.LocalDensity.current.fontScale > 1f
    LazyColumn(
        (Modifier.fillMaxSize().padding(top = 22.dp)).uiId("help.0026")
    ) {
        items(state.sections, key = { it.id }) { section ->
            val divider = CaixaDS.colors.line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (largeFont) Modifier.heightIn(min = 51.dp).padding(vertical = 8.dp) else Modifier.height(51.dp))
                    .uiId("help.0027").clickable { onAction(SupportAction.OpenSection(section.id, section.title)) }
                    .padding(horizontal = 16.dp)
                    .drawBehind {
                        if (section != state.sections.firstOrNull()) {
                            drawLine(
                                divider,
                                Offset.Zero,
                                Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = section.title,
                    color = CaixaDS.colors.graphite,
                    style = supportRegularStyle(14),
                    modifier = (Modifier.weight(.8f)).uiId("help.0028")
                )
                Box(Modifier.weight(.2f), contentAlignment = Alignment.CenterEnd) {
                    Image(
                        painterResource(R.drawable.legacy_support_shape_23_copy_3),
                        uiStrings.getString(LocalizedR.string.ui_abrir_1_s_638c29, section.title),
                        Modifier.size(width = 10.dp, height = 16.dp).rotate(90f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FaqQuestions(state: SupportUiState, onAction: (SupportAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LazyColumn((Modifier.fillMaxSize()).uiId("help.0029")) {
        items(state.questions, key = SupportQuestion::id) { question ->
            val expanded = question.id in state.expandedQuestionIds
            val divider = CaixaDS.colors.line
            Column(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(horizontal = 16.dp)
                    .drawBehind {
                        if (question != state.questions.firstOrNull()) {
                            drawLine(
                                divider,
                                Offset.Zero,
                                Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().uiId("help.0030").clickable {
                        onAction(SupportAction.ToggleQuestion(question.id))
                    }.padding(vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = question.title,
                        color = if (expanded) CaixaDSColorTokens.Blue900 else CaixaDS.colors.graphite,
                        style = supportRegularStyle(if (expanded) 16 else 14),
                        modifier = (Modifier.weight(.8f)).uiId("help.0031")
                    )
                    Box(Modifier.weight(.2f), contentAlignment = Alignment.CenterEnd) {
                        Image(
                            painterResource(
                                if (expanded) R.drawable.legacy_support_shape_23_copy_3 else R.drawable.legacy_support_shape_23_copy_2
                            ),
                            if (expanded) {
                                uiStrings.getString(
                                    LocalizedR.string.ui_recolher_resposta_dea24c
                                )
                            } else {
                                uiStrings.getString(LocalizedR.string.ui_expandir_resposta_930d08)
                            },
                            Modifier.size(width = 10.dp, height = 16.dp)
                        )
                    }
                }
                if (expanded) {
                    FaqAnswer(question.answer, Modifier.padding(start = 19.dp, end = 3.dp, bottom = 18.dp))
                }
            }
        }
    }
}
