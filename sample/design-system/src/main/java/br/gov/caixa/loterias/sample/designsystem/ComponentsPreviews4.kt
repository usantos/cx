@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSOctoberRoseCampaignPanel
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSOctoberRoseRibbon
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSPixPanel
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSPoolTutorialPrimary
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTutorialPreference
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSWheel
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText

internal val componentsPreviews4 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSOctoberRoseRibbon") { state ->
        with(state) {
            LoteriasCaixaDSOctoberRoseRibbon()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSOctoberRoseCampaignPanel") { state ->
        with(state) {
            LoteriasCaixaDSOctoberRoseCampaignPanel()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSPixPanel") { state ->
        with(state) {
            LoteriasCaixaDSPixPanel(
                purchaseId = 1234L,
                copyPaste = "PIX-DEMONSTRACAO-SEM-VALOR",
                remainingSeconds = 180L,
                checking = false,
                onCheckPayment = action,
                onCopied = action,
                onPurchases = action,
                formattedAmount = amount
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSPoolTutorialPrimary") { state ->
        with(state) {
            LoteriasCaixaDSPoolTutorialPrimary(
                label = AnnotatedString(actionLabel),
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTutorialPreference") { state ->
        with(state) {
            LoteriasCaixaDSTutorialPreference(
                checked = checked,
                onToggle = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold") { state ->
        with(state) {
            LoteriasCaixaDSScaffold(
                title = sample,
                onBack = action,
                modifier = Modifier.height(220.dp),
                content = { padding ->
                    LoteriasCaixaDSText(
                        hint,
                        Modifier.padding(padding)
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextField") { state ->
        with(state) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextFieldVariant.entries.forEach { variant ->
                    LoteriasCaixaDSTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = variant.name,
                        supportingText = hint,
                        variant = variant
                    )
                }
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSWheel") { state ->
        with(state) {
            LoteriasCaixaDSWheel(
                options = options,
                selectedIndex = selection,
                onSelectedIndex = { selection = it },
                modifier = Modifier.height(160.dp)
            )
        }
    }
)
