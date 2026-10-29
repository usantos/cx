package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
internal fun TutorialIndicator(
    pageCount: Int,
    selectedPage: Int,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    unselectedColor: Color = CaixaDSColorTokens.TutorialIndicatorOutline,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pageCount) { index ->
            val selected = index == selectedPage
            Box(
                Modifier
                    .size(10.dp)
                    .then(
                        if (selected) Modifier.background(selectedColor, CircleShape)
                        else Modifier.border(1.dp, unselectedColor, CircleShape),
                    ),
            )
        }
    }
}
