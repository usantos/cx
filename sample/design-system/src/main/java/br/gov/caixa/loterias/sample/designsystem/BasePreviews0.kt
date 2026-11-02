@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

internal val basePreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSAlertDialog") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSAlertDialog(
                    onDismissRequest = dismiss,
                    confirmButton = { LoteriasCaixaDSTextButton(onClick = dismiss) { LoteriasCaixaDSText(closeLabel) } },
                    title = { LoteriasCaixaDSText(sample) },
                    text = { LoteriasCaixaDSText(hint) }
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField") { state ->
        with(state) {
            LoteriasCaixaDSBasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                textStyle = LoteriasCaixaDS.typography.bodyLarge
            )
            LoteriasCaixaDSBasicTextField(
                value = richText,
                onValueChange = { richText = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                textStyle = LoteriasCaixaDS.typography.bodyLarge
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton") { state ->
        with(state) {
            LoteriasCaixaDSButton(
                onClick = action,
                content = { LoteriasCaixaDSText(actionLabel) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCard") { state ->
        with(state) {
            LoteriasCaixaDSCard(
                onClick = action,
                content = {
                    LoteriasCaixaDSText(
                        sample,
                        Modifier.padding(20.dp)
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCheckbox") { state ->
        with(state) {
            LoteriasCaixaDSCheckbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator") { state ->
        with(state) {
            LoteriasCaixaDSCircularProgressIndicator(progress = { 0.65f })
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenu") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSDropdownMenu(
                    expanded = show,
                    onDismissRequest = dismiss,
                    content = {
                        LoteriasCaixaDSDropdownMenuItem(
                            text = { LoteriasCaixaDSText(sample) },
                            onClick = dismiss
                        )
                    }
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenuItem") { state ->
        with(state) {
            LoteriasCaixaDSDropdownMenuItem(
                text = { LoteriasCaixaDSText(sample) },
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider") { state ->
        with(state) {
            LoteriasCaixaDSHorizontalDivider()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon") { state ->
        with(state) {
            LoteriasCaixaDSIcon(
                imageVector = LoteriasCaixaDSNotificationPlaceholder,
                contentDescription = sample
            )
        }
    }
)
