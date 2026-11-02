@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckSwitch
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSLoginSwitch
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSNumberPicker
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSTutorialCheckbox
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

internal val inputPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSAccountCheckbox") { state ->
        with(state) {
            LoteriasCaixaDSAccountCheckbox(
                checked = checked,
                onToggle = toggle,
                width = 32.dp
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckSwitch") { state ->
        with(state) {
            LoteriasCaixaDSCheckSwitch(
                enabled = checked,
                accent = LoteriasCaixaDS.colors.brand,
                onAccent = LoteriasCaixaDSColorTokens.White,
                trackBackground = LoteriasCaixaDS.colors.line,
                invertColors = false,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSLoginSwitch") { state ->
        with(state) {
            LoteriasCaixaDSLoginSwitch(checked = checked)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSNumberPicker") { state ->
        with(state) {
            LoteriasCaixaDSNumberPicker(
                label = sample,
                options = options,
                selectedIndex = selection,
                onSelect = { selection = it }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSRadioButton") { state ->
        with(state) {
            LoteriasCaixaDSRadioButton(
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckbox") { state ->
        with(state) {
            LoteriasCaixaDSCheckbox(
                checked = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSTutorialCheckbox") { state ->
        with(state) {
            LoteriasCaixaDSTutorialCheckbox(
                checked = checked,
                onClick = toggle
            )
        }
    }
)
