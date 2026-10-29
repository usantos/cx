package br.gov.caixa.loterias.feature.notifications

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.presentation.components.NotificationItem
import br.gov.caixa.loterias.feature.notifications.presentation.screen.PreferencesScreen
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NotificationsAccessibilityRegressionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun preferenceSubjectAndToggleHaveOneSemanticOwner() {
        var calls = 0
        compose.setContent {
            CaixaDSTheme {
                PreferencesScreen(
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
        compose.setContent {
            CaixaDSTheme {
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
