package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState

@Composable
internal fun AddFavoriteOptionsDialog(
    state: FavoriteBetsUiState,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    onContestTypeChange: (Int) -> Unit,
    onRecurringDrawsChange: (Int) -> Unit,
    onToggleMirror: () -> Unit,
) {
    var showContest by remember { mutableStateOf(false) }
    var showRecurring by remember { mutableStateOf(false) }
    val games = state.games.filter { it.modality.id == state.addCandidate?.modalityId }
    val contestOptions = games.distinctBy { it.contestType }.map {
        (if (it.contestType.equals("ESPECIAL", true)) 2 else 1) to
            (if (it.contestType.equals("ESPECIAL", true)) it.modality.specialName.ifBlank { it.modality.name } else it.modality.name)
    }
    val singleNormalRecurring = contestOptions.size == 1 && contestOptions.first().first == 1 && state.recurringOptions.isNotEmpty()
    var choiceOpened by remember { mutableStateOf(singleNormalRecurring) }
    Dialog(onDismissRequest = onCancel, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier.caixaDSModalWidth().background(
                CaixaDSColorTokens.White,
                RoundedCornerShape(8.dp)
            ).uiId("bet.0084").verticalScroll(rememberScrollState()).padding(start = 22.dp, top = 18.dp, end = 22.dp, bottom = 22.dp)
        ) {
            Text(
                stringResource(R.string.favorite_options_title),
                (Modifier.fillMaxWidth().padding(horizontal = 5.dp)).uiId("bet.0085"),
                color = CaixaDSColorTokens.ComboDark,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                style = favoriteLegacyTextStyle
            )
            if (contestOptions.isNotEmpty() && !singleNormalRecurring) {
                FavoritePickerHeading(stringResource(R.string.favorite_contest_type), {
                    choiceOpened = true
                    showContest = !showContest
                })
                if (showContest && contestOptions.isNotEmpty()) {
                    FavoriteComposePicker(
                        contestOptions.map {
                            it.second
                        },
                        contestOptions.indexOfFirst { it.first == state.contestType }.coerceAtLeast(0),
                        Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp).height(100.dp),
                        label = stringResource(R.string.favorite_contest_type)
                    ) { index ->
                        onContestTypeChange(contestOptions[index].first)
                    }
                }
            }
            if (state.recurringOptions.isNotEmpty() && state.contestType == 1) {
                FavoritePickerHeading(stringResource(R.string.favorite_recurring_draws), { showRecurring = !showRecurring })
                if (showRecurring) {
                    FavoriteComposePicker(
                        state.recurringOptions.map(Int::toString),
                        state.recurringSelectionIndex,
                        Modifier.fillMaxWidth().padding(
                            start = 20.dp,
                            top = 20.dp,
                            end = 20.dp
                        ).height(100.dp),
                        label = stringResource(R.string.favorite_recurring_draws)
                    ) {
                        onRecurringDrawsChange(state.recurringOptions[it])
                    }
                }
            }
            if (state.mirrorAvailable) FavoriteMirrorOption(state.mirror, onToggleMirror)
            FavoriteOptionsActions(choiceOpened, onCancel, onConfirm)
        }
    }
}

@Composable
private fun FavoriteMirrorOption(selected: Boolean, onToggleMirror: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(8.dp).uiId("bet.0086").toggleable(value = selected, role = Role.Checkbox) { onToggleMirror() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(Modifier.clearAndSetSemantics { }) {
            br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckbox(
                selected,
                null,
                (Modifier.size(24.dp)).uiId("bet.0087")
            )
        }
        Text(
            stringResource(R.string.favorite_mirror_description),
            (Modifier.padding(start = 8.dp)).uiId("bet.0088"),
            fontFamily = CaixaDSFuturaFontFamily,
            style = favoriteLegacyTextStyle
        )
    }
}

@Composable
private fun FavoriteOptionsActions(enabled: Boolean, onCancel: () -> Unit, onConfirm: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onCancel,
            modifier = (Modifier.fillMaxWidth().heightIn(min = 60.dp)).uiId("bet.0089"),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, CaixaDSColorTokens.White),
            colors = ButtonDefaults.buttonColors(
                containerColor = CaixaDSColorTokens.Blue700,
                contentColor = CaixaDSColorTokens.White
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
        ) {
            Text(
                stringResource(R.string.favorite_cancel),
                (Modifier.fillMaxWidth()).uiId("bet.0090"),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = favoriteLegacyTextStyle
            )
        }
        Button(
            onClick = onConfirm,
            enabled = enabled,
            modifier = (Modifier.fillMaxWidth().heightIn(min = 60.dp)).uiId("bet.0091"),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CaixaDSColorTokens.TealAction,
                contentColor = CaixaDSColorTokens.White,
                disabledContainerColor = CaixaDSColorTokens.PersonalDataFooter,
                disabledContentColor = CaixaDSColorTokens.White
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
        ) {
            Text(
                stringResource(R.string.favorite_add_to_cart),
                (Modifier.fillMaxWidth()).uiId("bet.0092"),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = favoriteLegacyTextStyle
            )
        }
    }
}
