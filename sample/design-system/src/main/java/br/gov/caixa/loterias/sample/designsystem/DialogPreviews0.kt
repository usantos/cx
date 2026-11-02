@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSHtmlDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSRapidAmountDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSSelectionDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSTextInputDialog

internal val dialogPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSHtmlDialog") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSHtmlDialog(
                    title = sample,
                    html = html,
                    onPositive = dismiss,
                    positiveLabel = closeLabel
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSOptionDialog") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSOptionDialog(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSRapidAmountDialog") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSRapidAmountDialog(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSDialog(
                    title = sample,
                    message = hint,
                    positiveLabel = closeLabel,
                    onPositive = dismiss,
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSSelectionDialog") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSSelectionDialog(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSTextInputDialog") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSTextInputDialog(
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
