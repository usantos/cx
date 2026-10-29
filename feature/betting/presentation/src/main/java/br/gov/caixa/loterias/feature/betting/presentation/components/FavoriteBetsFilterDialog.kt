package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R

@Composable
internal fun ModalityFilterDialog(selected: Int?, modalities: List<Pair<Int, String>>, onDismiss: () -> Unit, onSelect: (Int?) -> Unit) {
    var pending by remember(selected) { mutableStateOf(selected) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.caixaDSModalWidth().background(CaixaDSColorTokens.White, RoundedCornerShape(8.dp))) {
            Row(
                Modifier.fillMaxWidth().background(
                    CaixaDSColorTokens.ComboDark,
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.favorite_filter_title),
                    (Modifier.weight(1f)).uiId("bet.0093"),
                    color = CaixaDSColorTokens.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = favoriteLegacyTextStyle
                )
                Icon(
                    CaixaDSIcons.Close,
                    stringResource(R.string.favorite_close),
                    (Modifier.size(25.dp).uiId("bet.0094").clickable(onClick = onDismiss)).uiId("bet.0095"),
                    tint = CaixaDSColorTokens.White
                )
            }
            Column(Modifier.weight(1f, fill = false).padding(10.dp).uiId("bet.0096").verticalScroll(rememberScrollState())) {
                FavoriteModalityGrid(
                    listOf(null to stringResource(R.string.favorite_all_modalities)) +
                        modalities.filterNot { it.second.equals("Loteca", true) },
                    pending,
                    { pending = it }
                )
            }
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                Text(
                    stringResource(R.string.favorite_cancel),
                    (Modifier.padding(8.dp).uiId("bet.0097").clickable(onClick = onDismiss)).uiId("bet.0098"),
                    color = CaixaDSColorTokens.FavoritePickerBlue,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontFamily = CaixaDSFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    fontSize = 14.sp
                )
                Text(
                    stringResource(R.string.favorite_filter),
                    (
                        Modifier.padding(start = 16.dp).padding(8.dp).uiId("bet.0099").clickable {
                            onSelect(pending)
                        }
                        ).uiId("bet.0100"),
                    color = CaixaDSColorTokens.FavoritePickerBlue,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontFamily = CaixaDSFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    fontSize = 14.sp
                )
            }
        }
    }
}
