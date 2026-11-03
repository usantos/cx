package br.gov.caixa.loterias.feature.account.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountChoice
import br.gov.caixa.loterias.feature.account.domain.model.Gender

@Composable
internal fun LegacyAccountGenderControl(selected: Gender, onSelected: (Gender) -> Unit) {
    LoteriasCaixaDSAccountChoice(selected, listOf("Masculino" to Gender.MALE, "Feminino" to Gender.FEMALE), onSelected)
}
