package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSWheel
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSOutlinedButton as OutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
internal fun FavoritePickerHeading(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick,
        (Modifier.fillMaxWidth().padding(top = 20.dp).heightIn(min = 50.dp)).uiId("bet.0123"),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, CaixaDSColorTokens.TicketChoiceMuted),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CaixaDSColorTokens.SocialGraphite)
    ) {
        Text(
            label,
            (Modifier.fillMaxWidth()).uiId("bet.0124"),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            style = favoriteLegacyTextStyle
        )
    }
}

@Composable
internal fun FavoriteComposePicker(options: List<String>, selected: Int, modifier: Modifier, label: String, onChange: (Int) -> Unit) {
    CaixaDSWheel(
        options = options,
        selectedIndex = selected,
        onSelectedIndex = onChange,
        modifier = (modifier).uiId("bet.0125"),
        itemHeight = 32.dp,
        fontSize = 16.sp,
        color = CaixaDSColorTokens.FavoritePickerBlue,
        accessibilityLabel = label
    )
}
