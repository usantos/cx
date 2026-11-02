package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckSwitch
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSTutorialCheckbox
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LoteriasCaixaDSSelectionRenderingJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun selectionStateChangesThePaintedIndicatorForAllNativeControls() {
        val checked = mutableStateOf(false)
        val invert = mutableStateOf(false)
        compose.setContent {
            LoteriasCaixaDSTheme {
                Column {
                    LoteriasCaixaDSRadioButton(checked.value, {}, Modifier.background(Color.White).testTag("radio"))
                    LoteriasCaixaDSCheckbox(checked.value, {}, Modifier.background(Color.White).testTag("checkbox"))
                    LoteriasCaixaDSTutorialCheckbox(checked.value, {}, Modifier.background(Color.White).testTag("tutorial"))
                    Box(Modifier.size(48.dp).background(Color.White).testTag("switch")) {
                        LoteriasCaixaDSCheckSwitch(checked.value, Color.Blue, Color.White, Color.Gray, invert.value, {})
                    }
                }
            }
        }
        val tags = listOf("radio", "checkbox", "tutorial", "switch")
        val unchecked = tags.associateWith { compose.onNodeWithTag(it).captureToImage().asAndroidBitmap() }
        compose.runOnIdle { checked.value = true }
        tags.forEach { tag ->
            assertFalse(
                "$tag must visually distinguish checked state",
                unchecked.getValue(tag).sameAs(compose.onNodeWithTag(tag).captureToImage().asAndroidBitmap())
            )
        }
        val checkedSwitch = compose.onNodeWithTag("switch").captureToImage().asAndroidBitmap()
        compose.runOnIdle { invert.value = true }
        val invertedChecked = compose.onNodeWithTag("switch").captureToImage().asAndroidBitmap()
        assertFalse("Inverted switches must use their alternate palette", checkedSwitch.sameAs(invertedChecked))
        compose.runOnIdle { checked.value = false }
        assertFalse(
            "Inverted unchecked switch must paint the off symbol",
            invertedChecked.sameAs(compose.onNodeWithTag("switch").captureToImage().asAndroidBitmap())
        )
    }
}
