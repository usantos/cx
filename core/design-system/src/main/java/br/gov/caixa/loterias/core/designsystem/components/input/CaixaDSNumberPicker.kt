package br.gov.caixa.loterias.core.designsystem.components.input

import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId

/** Seletor giratório nativo usado pelos filtros legados, com tipografia CAIXA e sem edição de texto. */
@Composable
fun CaixaDSNumberPicker(label: String, options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    if (options.isEmpty()) return
    val index = selectedIndex.coerceIn(options.indices)
    AndroidView(
        modifier = (
            modifier.fillMaxWidth().semantics {
                contentDescription = label
                stateDescription = options[index]
                customActions = options.mapIndexed { optionIndex, option ->
                    CustomAccessibilityAction(option) {
                        onSelect(optionIndex)
                        true
                    }
                }
            }
            ).uiId("ds.0103"),
        factory = { context ->
            NumberPicker(context).apply {
                descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                wrapSelectorWheel = false
                val font = ResourcesCompat.getFont(context, R.font.caixa_std_regular)
                for (childIndex in 0 until childCount) {
                    (getChildAt(childIndex) as? TextView)?.typeface = font
                }
            }
        },
        update = { picker ->
            picker.setOnValueChangedListener(null)
            picker.displayedValues = null
            picker.minValue = 0
            picker.maxValue = options.lastIndex
            picker.displayedValues = options.toTypedArray()
            picker.value = index
            picker.setOnValueChangedListener { _, _, value -> onSelect(value) }
        }
    )
}
