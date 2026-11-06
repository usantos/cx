package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

@Composable
fun LoteriasCaixaDSCardProviderHeader(
    backDescription: String,
    changeLabel: String,
    logo: @Composable (Modifier) -> Unit,
    onChange: (() -> Unit)? = null,
) {
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp).height(57.dp)
            .background(LoteriasCaixaDS.colors.paymentCard),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(5.dp).fillMaxSize().background(LoteriasCaixaDS.colors.savedCardAccent))
        Icon(
            LoteriasCaixaDSIcons.Back,
            backDescription,
            tint = LoteriasCaixaDS.colors.cartDark,
            modifier = (
                Modifier.padding(start = 16.dp)
                    .then(if (onChange != null) Modifier.uiId("pay.0001").clickable(onClick = onChange) else Modifier)
                ).uiId("pay.0002")
        )
        Box(Modifier.padding(start = 16.dp).weight(1f)) {
            logo(Modifier.uiId("pay.0003"))
        }
        Text(
            changeLabel,
            (
                Modifier.padding(end = 28.dp)
                    .then(if (onChange != null) Modifier.uiId("pay.0004").clickable(onClick = onChange) else Modifier)
                ).uiId("pay.0005"),
            color = LoteriasCaixaDS.colors.cartDark
        )
    }
}

@Composable
fun LoteriasCaixaDSCardTextInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 14.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = (Modifier.fillMaxWidth().heightIn(min = 40.dp)).uiId("pay.0006"),
            singleLine = true,
            textStyle = LoteriasCaixaDS.typography.bodyLarge.copy(color = LoteriasCaixaDS.colors.text),
            decorationBox = { inner ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    if (value.isBlank()) {
                        Text(label, color = LoteriasCaixaDS.colors.primaryButton, fontSize = 16.sp, modifier = Modifier.uiId("pay.0007"))
                    }
                    inner()
                }
            }
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDS.colors.line))
    }
}

@Composable
fun LoteriasCaixaDSCardPicker(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    selectionDescription: String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier.padding(top = 14.dp)) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 41.dp).uiId("pay.0020").clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                value.ifBlank { label },
                (Modifier.weight(1f)).uiId("pay.0021"),
                color = if (value.isBlank()) LoteriasCaixaDS.colors.primaryButton else LoteriasCaixaDS.colors.text,
                fontSize = 16.sp
            )
            Icon(
                LoteriasCaixaDSIcons.ChevronDown,
                contentDescription = selectionDescription,
                tint = LoteriasCaixaDS.colors.brand,
                modifier = Modifier.uiId("pay.0022")
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).align(Alignment.BottomCenter).background(LoteriasCaixaDS.colors.line))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.uiId("pay.0023")) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, modifier = Modifier.uiId("pay.0024")) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.uiId("pay.0025")
                )
            }
        }
    }
}

@Composable
fun LoteriasCaixaDSCardHeading(text: String) {
    Text(
        text,
        (Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp)).uiId("pay.0019"),
        color = LoteriasCaixaDS.colors.cartDark,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        style = LoteriasCaixaDSCardTextStyle
    )
}
