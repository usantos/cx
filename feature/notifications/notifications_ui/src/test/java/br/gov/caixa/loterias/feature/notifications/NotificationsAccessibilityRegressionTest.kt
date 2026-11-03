package br.gov.caixa.loterias.feature.notifications

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.presentation.components.NotificationItem
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import br.gov.caixa.loterias.feature.notifications.ui.NotificationsUiEntryImpl
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(sdk = [36], application = android.app.Application::class)
class NotificationsAccessibilityRegressionTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun preferenceSubjectAndToggleHaveOneSemanticOwner() {
        var calls = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                NotificationsUiEntryImpl().PreferencesScreen(
                    NotificationsUiState(
                        loading = false,
                        preferences = listOf(NotificationPreference(null, "", "", false))
                    ),
                    { _, _ -> calls++ },
                    {},
                    {}
                )
            }
        }
        val subject = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
            .getString(R.string.notifications_todas_as_notificacoes)
        compose.onNode(hasText(subject, substring = true) and isToggleable()).assertIsOff().performClick()
        compose.runOnIdle { assertEquals(1, calls) }
    }

    @Test fun expandedMessagePublishesExpandedState() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                NotificationItem(
                    AppNotification(1, NotificationCategory.TIPS_NEWS, "Mensagem acessível", "Conteúdo", "Hoje", true),
                    true,
                    {},
                    {},
                    {}
                )
            }
        }
        compose.onNodeWithText("Mensagem acessível").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expandido"))
    }
}
