package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R

@Composable
internal fun HomeQuickAccessItem(
    title: String,
    modifier: Modifier = Modifier,
    description: AnnotatedString,
    color: Color,
    descriptionSize: TextUnit = 13.sp,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.width(294.dp).heightIn(min = 60.dp).uiId("home.0053").clickable(onClick = onClick),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(199.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = title,
                color = color,
                style = homeLegacyTextStyle(14.sp, FontWeight.Bold),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("home.0054")
            )
            Text(
                text = description,
                color = LoteriasCaixaDSColorTokens.BetAction,
                style = homeLegacyTextStyle(descriptionSize),
                textAlign = TextAlign.End,
                modifier = Modifier.uiId("home.0055")
            )
        }
        Box(
            modifier = Modifier.width(95.dp).height(60.dp).padding(start = 20.dp),
            contentAlignment = Alignment.TopStart
        ) {
            icon()
        }
    }
}

@Composable
internal fun AgeRestrictionIcon() {
    Image(painterResource(br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_ic_jr_menu_trevo), null, Modifier.size(40.dp))
}

@Composable
internal fun WomenSupportIcon() {
    Image(painterResource(br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_ic_disque_180), null, Modifier.size(40.dp))
}
