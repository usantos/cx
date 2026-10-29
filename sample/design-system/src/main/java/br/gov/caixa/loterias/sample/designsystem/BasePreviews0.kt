@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

internal val basePreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSAlertDialog") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSAlertDialog(
                    onDismissRequest = dismiss,
                    confirmButton = { CaixaDSTextButton(onClick = dismiss) { CaixaDSText(closeLabel) } },
                    title = { CaixaDSText(sample) },
                    text = { CaixaDSText(hint) }
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField") { state ->
        with(state) {
            CaixaDSBasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                textStyle = CaixaDS.typography.bodyLarge
            )
            CaixaDSBasicTextField(
                value = richText,
                onValueChange = { richText = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                textStyle = CaixaDS.typography.bodyLarge
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton") { state ->
        with(state) {
            CaixaDSButton(
                onClick = action,
                content = { CaixaDSText(actionLabel) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard") { state ->
        with(state) {
            CaixaDSCard(
                onClick = action,
                content = {
                    CaixaDSText(
                        sample,
                        Modifier.padding(20.dp)
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox") { state ->
        with(state) {
            CaixaDSCheckbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator") { state ->
        with(state) {
            CaixaDSCircularProgressIndicator(progress = { 0.65f })
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenu") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSDropdownMenu(
                    expanded = show,
                    onDismissRequest = dismiss,
                    content = {
                        CaixaDSDropdownMenuItem(
                            text = { CaixaDSText(sample) },
                            onClick = dismiss
                        )
                    }
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenuItem") { state ->
        with(state) {
            CaixaDSDropdownMenuItem(
                text = { CaixaDSText(sample) },
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider") { state ->
        with(state) {
            CaixaDSHorizontalDivider()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon") { state ->
        with(state) {
            CaixaDSIcon(
                imageVector = CaixaDSNotificationPlaceholder,
                contentDescription = sample
            )
        }
    }
)
