package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam

@Composable
internal fun HomeResultTeam(team: DrawSportsTeam, selected: Boolean, color: Color, description: String, lotogol: Boolean) {
    if (lotogol) {
        Column(Modifier.width(105.dp).height(150.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            HomeResultShield(team.shieldBase64, (Modifier.padding(top = 24.dp, bottom = 10.dp).size(40.dp)).uiId("home.0139"))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(team.name) }
                    append(" / " + if (team.nationalTeam == true) team.countryCode else team.state)
                },
                color = color,
                textAlign = TextAlign.Center,
                style = homeResultTextStyle.copy(fontSize = 12.sp),
                modifier = Modifier
                    .uiId("home.0140")
            )
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(40.dp).background(LoteriasCaixaDSColorTokens.White, CircleShape), contentAlignment = Alignment.Center) {
                Text(
                    description,
                    color = LoteriasCaixaDSColorTokens.Lotogol,
                    style = homeResultTextStyle.copy(fontSize = 14.1.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.uiId("home.0141")
                )
            }
        }
        return
    }
    Column(
        Modifier.width(
            80.dp
        ).heightIn(min = 115.dp).background(if (selected) LoteriasCaixaDSColorTokens.SportsResultSelection else Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeResultShield(team.shieldBase64, (Modifier.padding(top = 16.dp, bottom = 10.dp).size(40.dp)).uiId("home.0142"))
        Text(
            team.name,
            color = color,
            textAlign = TextAlign.Center,
            style = homeResultTextStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.uiId("home.0143")
        )
        Text(
            description,
            color = color,
            textAlign = TextAlign.Center,
            style = homeResultTextStyle.copy(fontSize = 13.sp),
            modifier = Modifier.uiId("home.0144")
        )
    }
}

@Composable
internal fun HomeResultShield(base64: String?, modifier: Modifier = Modifier) {
    val bitmap = androidx.compose.runtime.remember(base64) {
        base64?.let { encoded ->
            try {
                val bytes = android.util.Base64.decode(encoded.substringAfter("base64,", encoded), android.util.Base64.DEFAULT)
                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
    val bottomPadding = with(LocalDensity.current) { 12.toDp() }
    if (bitmap !=
        null
    ) {
        Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = modifier.padding(bottom = bottomPadding))
    } else {
        Image(painterResource(DeliveryR.drawable.legacy_home_result_time_padrao), null, modifier.padding(bottom = bottomPadding))
    }
}
