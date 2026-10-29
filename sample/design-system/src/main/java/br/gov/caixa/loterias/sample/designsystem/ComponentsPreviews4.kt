@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseCampaignPanel
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseRibbon
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSPixPanel
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSPoolTutorialPrimary
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTutorialPreference
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSWheel
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText

internal val componentsPreviews4 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseRibbon") { state ->
        with(state) {
            CaixaDSOctoberRoseRibbon()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseCampaignPanel") { state ->
        with(state) {
            CaixaDSOctoberRoseCampaignPanel()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSPixPanel") { state ->
        with(state) {
            CaixaDSPixPanel(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSPoolTutorialPrimary") { state ->
        with(state) {
            CaixaDSPoolTutorialPrimary(
                label = AnnotatedString(actionLabel),
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSTutorialPreference") { state ->
        with(state) {
            CaixaDSTutorialPreference(
                checked = checked,
                onToggle = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold") { state ->
        with(state) {
            CaixaDSScaffold(
                title = sample,
                onBack = action,
                modifier = Modifier.height(220.dp),
                content = { padding ->
                    CaixaDSText(
                        hint,
                        Modifier.padding(padding)
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField") { state ->
        with(state) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextFieldVariant.entries.forEach { variant ->
                    CaixaDSTextField(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSWheel") { state ->
        with(state) {
            CaixaDSWheel(
                options = options,
                selectedIndex = selection,
                onSelectedIndex = { selection = it },
                modifier = Modifier.height(160.dp)
            )
        }
    }
)
