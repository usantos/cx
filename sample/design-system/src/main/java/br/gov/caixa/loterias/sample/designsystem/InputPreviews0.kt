@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckSwitch
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSLoginSwitch
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSNumberPicker
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSTutorialCheckbox
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

internal val inputPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSAccountCheckbox") { state ->
        with(state) {
            CaixaDSAccountCheckbox(
                checked = checked,
                onToggle = toggle,
                width = 32.dp
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckSwitch") { state ->
        with(state) {
            CaixaDSCheckSwitch(
                enabled = checked,
                accent = CaixaDS.colors.brand,
                onAccent = CaixaDSColorTokens.White,
                trackBackground = CaixaDS.colors.line,
                invertColors = false,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSLoginSwitch") { state ->
        with(state) {
            CaixaDSLoginSwitch(checked = checked)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSNumberPicker") { state ->
        with(state) {
            CaixaDSNumberPicker(
                label = sample,
                options = options,
                selectedIndex = selection,
                onSelect = { selection = it }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSRadioButton") { state ->
        with(state) {
            CaixaDSRadioButton(
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckbox") { state ->
        with(state) {
            CaixaDSCheckbox(
                checked = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSTutorialCheckbox") { state ->
        with(state) {
            CaixaDSTutorialCheckbox(
                checked = checked,
                onClick = toggle
            )
        }
    }
)
