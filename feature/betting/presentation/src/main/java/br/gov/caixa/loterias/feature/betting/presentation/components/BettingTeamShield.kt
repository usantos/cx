package br.gov.caixa.loterias.feature.betting.presentation.components

import br.gov.caixa.loterias.feature.betting.R

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam

@Composable
internal fun TeamShield(team: SportsTeam, modifier: Modifier = Modifier, bottomPadding: Dp = 8.dp) {
    val bitmap = remember(team.shieldBase64) {
        team.shieldBase64?.let { encoded ->
            runCatching {
                val bytes = Base64.decode(encoded.substringAfter("base64,", encoded), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }.getOrNull()
        }
    }
    if (bitmap != null) {
        Image(bitmap, contentDescription = null, modifier = modifier.padding(bottom = bottomPadding))
    } else {
        Image(painterResource(R.drawable.installed_betting_time_padrao), null, modifier.padding(bottom = bottomPadding))
    }
}
