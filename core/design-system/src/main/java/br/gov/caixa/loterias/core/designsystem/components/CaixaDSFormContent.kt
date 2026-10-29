package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

@Composable
fun CaixaDSLabelledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    CaixaDSTextField(
        value,
        onValueChange,
        label,
        modifier.uiId("ds.0013"),
        enabled = enabled,
        placeholder = placeholder,
        visualTransformation = visualTransformation,
        variant = CaixaDSTextFieldVariant.LABELLED
    )
}

@Composable
fun CaixaDSSectionHeader(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = CaixaDS.colors.brand, style = CaixaDS.typography.titleLarge)
        subtitle?.let {
            Text(it, color = CaixaDS.colors.mutedText, style = CaixaDS.typography.bodyMedium)
        }
    }
}

@Composable
fun CaixaDSCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val clickable = if (onClick == null) modifier else modifier.uiId("ds.0014").clickable(onClick = onClick)
    Card(
        modifier = clickable.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(Modifier.fillMaxWidth().padding(16.dp)) { content() }
    }
}

@Composable
fun CaixaDSChoiceRow(
    title: String,
    subtitle: String? = null,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    multiple: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth().then(
            if (multiple) {
                Modifier.uiId("ds.0015").toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() })
            } else {
                Modifier.uiId("ds.0016").selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            }
        ).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (multiple) {
            Checkbox(checked = selected, onCheckedChange = null, modifier = Modifier.uiId("ds.0017"))
        } else {
            RadioButton(selected = selected, onClick = null, modifier = Modifier.uiId("ds.0018"))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = CaixaDS.colors.text, style = CaixaDS.typography.bodyLarge)
            subtitle?.let {
                Text(it, color = CaixaDS.colors.mutedText, style = CaixaDS.typography.bodySmall)
            }
        }
    }
}

@Composable
fun CaixaDSDivider() {
    HorizontalDivider(color = CaixaDS.colors.line, thickness = 1.dp)
}

@Composable
fun CaixaDSEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(title, color = CaixaDS.colors.text, style = CaixaDS.typography.titleMedium)
        Text(
            message,
            color = CaixaDS.colors.mutedText,
            style = CaixaDS.typography.bodyLarge,
            textAlign = TextAlign.Start
        )
        if (actionLabel != null && onAction != null) {
            CaixaDSOutlinedButton(actionLabel, onAction, (Modifier.padding(top = 8.dp)).uiId("ds.0019"))
        }
    }
}
