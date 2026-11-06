@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package br.gov.caixa.loterias.core.designsystem.automation

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

/** Expõe um identificador técnico sem alterar o texto lido pelo TalkBack. */
fun Modifier.automationId(id: String): Modifier = then(AutomationIdentityElement(id))

/** Usa a chave compacta e estável do catálogo, sem depender do texto da tela. */
fun Modifier.uiId(key: String): Modifier = automationId("simlo.$key")

/** Identidades já publicadas prevalecem sobre os novos fallbacks de wrappers. */
fun Modifier.legacyAutomationId(id: String): Modifier = foldIn<Modifier>(Modifier) { result, element ->
    if (element is AutomationIdentityElement) result else result.then(element)
}.testTag(id).semantics {
    testTagsAsResourceId = true
}

private data class AutomationIdentityElement(val id: String) : ModifierNodeElement<AutomationIdentityNode>() {
    override fun create() = AutomationIdentityNode(id)

    override fun update(node: AutomationIdentityNode) {
        node.id = id
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "automationId"
    }
}

private class AutomationIdentityNode(var id: String) :
    Modifier.Node(),
    SemanticsModifierNode {
    override fun SemanticsPropertyReceiver.applySemantics() {
        this[SemanticsProperties.TestTag] = id
        testTagsAsResourceId = true
    }
}
