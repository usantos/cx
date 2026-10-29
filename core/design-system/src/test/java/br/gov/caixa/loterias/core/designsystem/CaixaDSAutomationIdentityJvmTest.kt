package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSAutomationIdentityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun callerIdentitySurvivesNestedComponentsAndKeepsAccessibilityLabel() {
        compose.setContent {
            Box(
                Modifier.testTag("caller_id").automationId("component_fallback")
                    .semantics { contentDescription = "Continuar" }
            )
        }
        compose.onNodeWithTag("caller_id").assertExists()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Continuar")))
        compose.onNodeWithTag("component_fallback").assertDoesNotExist()
    }

    @Test fun compactCatalogKeyMatchesTheRobotResourceIdentity() {
        compose.setContent { Box(Modifier.uiId("home.0001")) }
        compose.onNodeWithTag("simlo.home.0001").assertExists()
            .assert(
                SemanticsMatcher("Identificador exportado ao UiAutomator") { node ->
                    node.config.any { it.key.name == "TestTagsAsResourceId" && it.value == true }
                }
            )
    }

    @Test fun publishedLegacyIdentitySurvivesFeatureWrapperFallback() {
        compose.setContent { Box(Modifier.automationId("feature_fallback").legacyAutomationId("published_id")) }
        compose.onNodeWithTag("published_id").assertExists()
        compose.onNodeWithTag("feature_fallback").assertDoesNotExist()
    }

    @Test fun explicitCallerTagStillWinsOverPublishedComponentIdentity() {
        compose.setContent {
            Box(Modifier.testTag("caller_id").automationId("fallback").legacyAutomationId("published_id"))
        }
        compose.onNodeWithTag("caller_id").assertExists()
        compose.onNodeWithTag("published_id").assertDoesNotExist()
    }

    @Test fun nestedFallbackDoesNotReplaceFirstIdentity() {
        compose.setContent { Box(Modifier.automationId("feature_action").automationId("ds_fallback")) }
        compose.onNodeWithTag("feature_action").assertExists()
        compose.onNodeWithTag("ds_fallback").assertDoesNotExist()
    }
}
