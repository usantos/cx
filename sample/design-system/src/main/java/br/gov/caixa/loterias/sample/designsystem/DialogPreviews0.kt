@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSHtmlDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSRapidAmountDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSSelectionDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSTextInputDialog

internal val dialogPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSHtmlDialog") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSHtmlDialog(
                    title = sample,
                    html = html,
                    onPositive = dismiss,
                    positiveLabel = closeLabel
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSOptionDialog") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSOptionDialog(
                    title = sample,
                    options = listOf(
                        0,
                        2,
                        3
                    ),
                    selected = selection,
                    label = { it.toString() },
                    onSelect = {
                        selection = it
                        show = false
                    },
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSRapidAmountDialog") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSRapidAmountDialog(
                    title = sample,
                    value = text,
                    hint = hint,
                    limits = amount,
                    errorMessage = null,
                    onValueChange = { text = it },
                    onSave = dismiss,
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSDialog(
                    title = sample,
                    message = hint,
                    positiveLabel = closeLabel,
                    onPositive = dismiss,
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSSelectionDialog") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSSelectionDialog(
                    title = sample,
                    message = hint,
                    options = options,
                    selectedIndex = selection,
                    onSelect = { selection = it },
                    onConfirm = dismiss,
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSTextInputDialog") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSTextInputDialog(
                    title = sample,
                    value = text,
                    hint = hint,
                    onValueChange = { text = it },
                    onSave = dismiss,
                    onDismiss = dismiss
                )
            }
        }
    }
)
