package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.notifications.presentation.route.NotificationsRoute
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.NotificationsViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.route.ResponsibleGamingRoute
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel.ResponsibleGamingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class StateCollectionLifecycleUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val models = mutableListOf<ViewModel>()

    @After fun cleanup() {
        compose.runOnIdle { models.forEach { it.viewModelScope.cancel() } }
    }

    @Test fun notificationsStatePausesBelowStartedAndResumesWithLatestQuery() {
        lateinit var owner: Owner
        lateinit var model: NotificationsViewModel
        compose.setContent {
            val lifecycleOwner = remember { Owner().also { owner = it } }
            val viewModel = remember {
                NotificationsViewModel(
                    { AppResult.Success(emptyList()) },
                    { AppResult.Success(Unit) },
                    { AppResult.Success(Unit) },
                    { AppResult.Success(Unit) },
                    { AppResult.Success(Unit) },
                    { AppResult.Success(emptyList()) },
                    { _, _ -> AppResult.Success(Unit) },
                    FixedCoroutineDispatchers(Dispatchers.Main.immediate)
                ).also {
                    model = it
                    models += it
                }
            }
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                CaixaDSTheme { NotificationsRoute("notifications/messages", viewModel, {}, {}) }
            }
        }
        compose.runOnIdle { model.updateQuery("Started") }
        compose.onNode(hasSetTextAction()).assertTextEquals("Started")
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.CREATED }
        compose.runOnIdle {
            model.updateQuery("Stopped first")
            model.updateQuery("Stopped latest")
        }
        compose.runOnIdle { assertEquals("Stopped latest", model.state.value.query) }
        compose.onNode(hasSetTextAction()).assertTextEquals("Started")

        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }

        compose.onNode(hasSetTextAction()).assertTextEquals("Stopped latest")
        compose.runOnIdle { model.updateQuery("Resumed") }
        compose.onNode(hasSetTextAction()).assertTextEquals("Resumed")
    }

    @Test fun responsibleGamingStatePausesBelowStartedAndResumesWithLatestSelection() {
        lateinit var owner: Owner
        lateinit var model: ResponsibleGamingViewModel
        val radio = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
        compose.setContent {
            val lifecycleOwner = remember { Owner().also { owner = it } }
            val viewModel = remember {
                ResponsibleGamingViewModel(
                    "responsible/suspension",
                    GetSelfAssessmentQuestionsUseCase { error("Unexpected questions request") },
                    SubmitSelfAssessmentUseCase { error("Unexpected assessment submission") },
                    GetSocialTransfersUseCase { error("Unexpected social transfers request") },
                    SuspendAccountUseCase { error("Unexpected suspension submission") },
                    FixedCoroutineDispatchers(Dispatchers.Main.immediate)
                ).also {
                    model = it
                    models += it
                }
            }
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                CaixaDSTheme { ResponsibleGamingRoute("responsible/suspension", viewModel, {}, {}, {}) }
            }
        }
        compose.runOnIdle { model.onAction(ResponsibleGamingAction.SelectSuspensionPeriod("24 horas")) }
        compose.onAllNodes(radio).assertCountEquals(SUSPENSION_RADIO_COUNT)
        compose.onAllNodes(radio)[0].assertIsSelected()
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.CREATED }
        compose.runOnIdle {
            model.onAction(ResponsibleGamingAction.SelectSuspensionPeriod("48 horas"))
            model.onAction(ResponsibleGamingAction.SelectSuspensionPeriod("7 dias"))
        }
        compose.runOnIdle { assertEquals("7 dias", model.state.value.selectedSuspensionPeriod) }
        compose.onAllNodes(radio)[0].assertIsSelected()
        compose.onAllNodes(radio)[2].assertIsNotSelected()

        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }

        compose.onAllNodes(radio)[0].assertIsNotSelected()
        compose.onAllNodes(radio)[2].assertIsSelected()
        compose.runOnIdle { model.onAction(ResponsibleGamingAction.SelectSuspensionPeriod("30 dias")) }
        compose.onAllNodes(radio)[4].assertIsSelected()
    }

    private class Owner : LifecycleOwner {
        val registry = LifecycleRegistry(this).apply { currentState = Lifecycle.State.STARTED }
        override val lifecycle: Lifecycle get() = registry
    }
}

private const val SUSPENSION_RADIO_COUNT = 5
