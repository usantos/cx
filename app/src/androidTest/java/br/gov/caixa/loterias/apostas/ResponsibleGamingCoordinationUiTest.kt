package br.gov.caixa.loterias.apostas

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentAnswer
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.navigation.responsibleGamingGraph
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.route.ResponsibleGamingRoute
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel.ResponsibleGamingViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Caracteriza a rota pública com casos de uso locais; nenhuma avaliação ou suspensão real é enviada. */
class ResponsibleGamingCoordinationUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val radio = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
    private val checkbox = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)

    @Test fun lastAssessmentQuestionUsesLegacyConcluidoAction() {
        show("responsible/form", Fixture())
        chooseAnswer(1)
        compose.onNodeWithText("Próximo").performClick()
        compose.onNodeWithText("Concluido").assertIsNotEnabled()
        chooseAnswer(0)
        compose.onNodeWithText("Concluido").assertIsEnabled()
    }

    @Test fun affirmativeAssessmentKeepsExactNativeWarning() {
        val fixture = Fixture().apply {
            questionResult = AppResult.Success(listOf(SelfAssessmentQuestion(1, "Questão única", 1)))
        }
        show("responsible/form", fixture)
        chooseAnswer(0)
        compose.onAllNodes(hasText("Concluido"))[0].performClick()
        compose.onNodeWithText(
            "Muita atenção!\nSe você respondeu \"sim\" para um ou mais dessas questões, recomendamos que você procure avaliação adicional."
        )
            .assertIsDisplayed()
    }

    @Test fun submittedAssessmentKeepsNativeIntroductionAndBottomActions() {
        val fixture = Fixture().apply {
            questionResult = AppResult.Success(listOf(SelfAssessmentQuestion(1, "Questão única", 1)))
        }
        show("responsible/form", fixture)
        chooseAnswer(0)
        compose.onNodeWithText("Concluido").performClick()
        compose.onNodeWithText("NODS*-CLIP Problem Gambling Screen").assertExists()
        val close = compose.onNodeWithText("Fechar").fetchSemanticsNode().boundsInRoot
        val help = compose.onNodeWithText("Ajuda").fetchSemanticsNode().boundsInRoot
        assertEquals(close.top, help.top, 1f)
    }

    @Test fun affirmativeAssessmentOffersLegacyHelpSheetAndReturnsToResultOnBack() {
        val fixture = Fixture().apply {
            questionResult = AppResult.Success(listOf(SelfAssessmentQuestion(1, "Questão única", 1)))
        }
        var completions = 0
        show("responsible/form", fixture, onComplete = { completions++ })
        chooseAnswer(0)
        // Envia pela máquina de estados real, para que a asserção da ajuda também
        // caracterize a resposta de avaliação bem-sucedida, em vez de uma prévia.
        compose.onAllNodes(hasText("Concluido"))[0].performClick()
        compose.onNodeWithText("Ajuda").performClick()
        compose.onNodeWithText("ONDE PROCURAR AJUDA?").assertIsDisplayed()
        compose.onNodeWithText("Centros de Atenção Psicossocial do Ministério da Saúde - CAPS", substring = true)
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Jogadores Anônimos", substring = true).performScrollTo().assertIsDisplayed()
        // Envia Voltar à janela focada do painel, como faz a ação Voltar do sistema.
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        compose.waitForIdle()
        compose.onNodeWithText("ONDE PROCURAR AJUDA?").assertDoesNotExist()
        compose.onNodeWithText("Muita atenção!", substring = true).assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, completions) }
    }

    @Test fun optionalWelcomeKeepsCloseAndStartEffectsWithoutLoadingQuestions() {
        val fixture = Fixture()
        var backs = 0
        var completions = 0
        show("responsible/start", fixture, onBack = { backs++ }, onComplete = { completions++ })
        compose.onNodeWithText("Fechar").performClick()
        compose.onNodeWithText("Seguir para autoavaliação").performClick()
        compose.runOnIdle {
            assertEquals(1, backs)
            assertEquals(1, completions)
            assertEquals(0, fixture.questionReads)
        }
    }

    @Test fun welcomeConsumesSystemBackButOptionalCloseStillExits() {
        var systemBacks = 0
        var closes = 0
        compose.setContent {
            androidx.activity.compose.BackHandler { systemBacks++ }
            val model = remember { Fixture().model("responsible/start") }
            CaixaDSTheme { ResponsibleGamingRoute("responsible/start", model, { closes++ }, {}, {}) }
        }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Seguir para autoavaliação").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, systemBacks)
            assertEquals(0, closes)
        }
        compose.onNodeWithText("Fechar").performClick()
        compose.runOnIdle { assertEquals(1, closes) }
    }

    @OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
    @Test
    fun socialHelpGraphOpensQuestionsBeforeTermsForVisitor() {
        val fixture = Fixture()
        val dependencies = object : br.gov.caixa.loterias.feature.responsiblegaming.navigation.ResponsibleGamingNavigationDependencies {
            override val currentUserIdentity = br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider { null }
            override val dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
            override val selfAssessmentQuestions = GetSelfAssessmentQuestionsUseCase { error("Private questions must not load") }
            override val socialTransfers = GetSocialTransfersUseCase {
                fixture.socialReads++
                fixture.socialResult
            }
            override val submitSelfAssessment = SubmitSelfAssessmentUseCase { error("Private answers must not submit") }
            override val suspendAccount = SuspendAccountUseCase { error("Private suspension must not submit") }
        }
        val session = object : br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions {
            override val state = br.gov.caixa.loterias.core.navigation.state.AppSessionState()
            override val identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity? = null
            override val privateEntryKey = "visitor"
            override val isPrivateSessionCurrent: () -> Boolean = { false }
            override val onLogout: () -> Unit = { error("Unexpected logout") }
            override val requestVisitorLogin: (String) -> Unit = { error("Help is public") }
            override val recheckAccountCompletion: () -> Unit = { error("Unexpected account check") }
            override val onAuthenticated: (String) -> Unit = {}
            override val onTryNow: () -> Unit = {}
        }
        lateinit var nav: androidx.navigation.NavHostController
        compose.setContent {
            nav = androidx.navigation.compose.rememberNavController()
            CaixaDSTheme {
                androidx.navigation.compose.NavHost(nav, startDestination = "responsible/social") {
                    responsibleGamingGraph(dependencies, nav, session)
                    composable(
                        "support/terms?tab={tab}",
                        arguments = listOf(
                            androidx.navigation.navArgument("tab") { defaultValue = "TERMS" }
                        )
                    ) { entry -> androidx.compose.material3.Text("Ajuda: ${entry.arguments?.getString("tab")}") }
                }
            }
        }
        // NavHost instala o destino inicial de forma assíncrona; aguarde a ação dessa tela.
        compose.waitUntilAtLeastOneExists(hasContentDescription("Dúvidas"), timeoutMillis = 5_000)
        compose.runOnIdle { assertEquals("responsible/social", nav.currentDestination?.route) }
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.waitUntilAtLeastOneExists(hasText("Ajuda: FAQ"), timeoutMillis = 5_000)
        compose.onNodeWithText("Ajuda: FAQ").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, fixture.socialReads) }
    }

    @Test fun requiredWelcomeHidesCloseAndKeepsStartEffect() {
        var completions = 0
        show("responsible/start", Fixture(), required = true, onComplete = { completions++ })
        compose.onNodeWithText("Fechar").assertDoesNotExist()
        compose.onNodeWithText("Seguir para autoavaliação").performClick()
        compose.runOnIdle { assertEquals(1, completions) }
    }

    @Test fun assessmentClearsRadioSelectionWhenReturningToQuestionsAsInLegacy() {
        val fixture = Fixture()
        var completions = 0
        show("responsible/form", fixture, onComplete = { completions++ })
        compose.onNodeWithText("Anterior").assertIsNotEnabled()
        compose.onNodeWithText("Próximo").assertIsNotEnabled()
        chooseAnswer(0)
        compose.onNodeWithText("Próximo").performClick()
        compose.onNodeWithText("Questão dois").assertExists()
        chooseAnswer(1)
        compose.onNodeWithText("Anterior").performClick()
        compose.onNodeWithText("Questão um").assertExists()
        compose.onAllNodes(radio).assertCountEquals(2)
        compose.onAllNodes(radio)[0].assertIsNotSelected()
        compose.onAllNodes(radio)[1].assertIsNotSelected()
        compose.onNodeWithText("Próximo").assertIsNotEnabled()
        chooseAnswer(0)
        compose.onNodeWithText("Próximo").performClick()
        compose.onAllNodes(radio)[1].assertIsNotSelected()
        chooseAnswer(1)
        compose.onAllNodes(hasText("Concluido"))[0].performClick()
        compose.onNodeWithText("Muita atenção!", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Fechar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(listOf(SelfAssessmentAnswer(1, "", true), SelfAssessmentAnswer(2, "", false))), fixture.submissions)
            assertEquals(1, fixture.questionReads)
            assertEquals(1, completions)
        }
    }

    @Test fun assessmentWithoutAffirmativeAnswersKeepsPositiveResultAndCompletion() {
        val fixture = Fixture().apply {
            questionResult = AppResult.Success(listOf(SelfAssessmentQuestion(1, "Questão única", 1)))
        }
        var completions = 0
        show("responsible/form", fixture, required = true, onComplete = { completions++ })
        compose.onNodeWithText("Fechar").assertDoesNotExist()
        compose.onNodeWithText("Concluido").assertIsNotEnabled()
        chooseAnswer(1)
        compose.onNodeWithText("Concluido").performClick()
        compose.onNodeWithText("Parabéns!", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Fechar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(listOf(SelfAssessmentAnswer(1, "", false))), fixture.submissions)
            assertEquals(1, completions)
        }
    }

    @Test fun assessmentRetryAndPrivateFlagKeepEmptyQuestionBehaviorWithoutExtraLoads() {
        val fixture = Fixture().apply { questionResult = AppResult.Failure(AppError.Network) }
        val privateData = mutableStateOf(true)
        compose.setContent {
            val model = remember { fixture.model("responsible/form") }
            CaixaDSTheme { ResponsibleGamingRoute("responsible/form", model, {}, {}, {}, privateDataEnabled = privateData.value) }
        }
        compose.onNodeWithText("Não foi possível conectar. Verifique sua internet e tente novamente.").assertExists()
        compose.runOnIdle { fixture.questionResult = AppResult.Success(emptyList()) }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Autoavaliação sem perguntas.").assertExists()
        compose.runOnIdle { privateData.value = false }
        compose.onNodeWithText("Autoavaliação sem perguntas.").assertDoesNotExist()
        compose.onNodeWithText("NODS*-CLIP Problem Gambling Screen").assertExists()
        compose.onNodeWithText("Próximo").assertIsNotEnabled()
        compose.runOnIdle { privateData.value = true }
        compose.onNodeWithText("Autoavaliação sem perguntas.").assertExists()
        compose.runOnIdle { assertEquals(2, fixture.questionReads) }
    }

    @Test fun suspensionRequiresPeriodAndAcknowledgementAndUsesProvidedSubmitCallback() {
        val fixture = Fixture()
        var submitted = 0
        var backs = 0
        show("responsible/suspension", fixture, onBack = { backs++ }, onSubmitSuspension = { submitted++ })
        compose.onNodeWithText("Confirmar").assertIsNotEnabled()
        compose.onAllNodes(radio)[0].performScrollTo().performClick()
        compose.onNodeWithText("Confirmar").assertIsNotEnabled()
        compose.onNode(checkbox).performScrollTo().performClick()
        compose.onNodeWithText("Confirmar").performScrollTo().assertIsEnabled().performClick()
        compose.onNodeWithText(
            "A suspensão temporária de compras é irreversível durante o período selecionado.\n\nVocê confirma a suspensão por 24 horas ?"
        ).assertExists()
        compose.runOnIdle { assertTrue(fixture.suspensions.isEmpty()) }
        compose.onNodeWithText("Confirmar Suspensão").performClick()
        compose.onNodeWithText("Desistir").performClick()
        compose.runOnIdle {
            assertEquals(1, submitted)
            assertTrue(fixture.suspensions.isEmpty())
            assertEquals(1, backs)
        }
    }

    @Test fun suspensionDefaultSubmitUsesSelectedHoursAndCompletesAfterAcknowledgement() {
        val fixture = Fixture()
        var completions = 0
        show("responsible/suspension", fixture, onComplete = { completions++ })
        compose.onAllNodes(radio)[2].performScrollTo().performClick()
        compose.onNode(checkbox).performScrollTo().performClick()
        compose.onNodeWithText("Confirmar").performScrollTo().performClick()
        compose.runOnIdle { assertTrue(fixture.suspensions.isEmpty()) }
        compose.onNodeWithText("Confirmar Suspensão").performClick()
        compose.onNodeWithText(
            "Você foi deslogado e a suspensão temporária efetivada pelo período escolhido.",
            substring = true
        ).assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(listOf(168), fixture.suspensions)
            assertEquals(1, completions)
        }
    }

    @Test fun suspensionSuccessShowsNativeDialogWithEndDateBeforeLogout() {
        var completions = 0
        show("responsible/suspension", Fixture(), onComplete = { completions++ })
        compose.onAllNodes(radio)[2].performScrollTo().performClick()
        compose.onNode(checkbox).performScrollTo().performClick()
        compose.onNodeWithText("Confirmar").performScrollTo().performClick()
        compose.onNodeWithText("Confirmar Suspensão").performClick()
        val prefix = "Você foi deslogado e a suspensão temporária efetivada pelo período escolhido." +
            " Só será possível fazer novas compras após a suspensão."
        compose.onNodeWithText(prefix, substring = true).assertIsDisplayed()
        val endDate = java.time.LocalDate.now().plusDays(7).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        compose.onNodeWithText("$endDate, às ", substring = true).assertExists()
        compose.runOnIdle { assertEquals(0, completions) }
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(1, completions) }
    }

    @Test fun socialTransfersRetryKeepsAmountsHelpAndBothBackActions() {
        val fixture = Fixture().apply { socialResult = AppResult.Failure(AppError.Network) }
        var help = 0
        var backs = 0
        show("responsible/social", fixture, onBack = { backs++ }, onOpenTerms = { help++ })
        compose.runOnIdle { fixture.socialResult = AppResult.Success(transfers) }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("R$ 480,00").assertExists()
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        val betAction = compose.onNodeWithText("Aposte e ajude ainda mais", useUnmergedTree = true).performScrollTo()
        val textLayouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        betAction.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(textLayouts) }
        assertFalse("Social transfer action text must fit without clipping", textLayouts.single().hasVisualOverflow)
        assertEquals(androidx.compose.ui.text.font.FontWeight.Normal, textLayouts.single().layoutInput.style.fontWeight)
        betAction.performClick()
        compose.runOnIdle {
            assertEquals(2, fixture.socialReads)
            assertEquals(1, help)
            assertEquals(2, backs)
        }
    }

    @Test fun socialTransfersDescriptionsFitBeforeFootnoteAtDefaultFontScale() {
        assertSocialDescriptionsFit(1f)
    }

    @Test fun socialTransfersDescriptionsFitBeforeFootnoteAtLargerFontScale() {
        assertSocialDescriptionsFit(1.4f)
    }

    private fun assertSocialDescriptionsFit(fontScale: Float) {
        val fixture = Fixture()
        compose.setContent {
            val density = androidx.compose.ui.platform.LocalDensity.current
            val model = remember { fixture.model("responsible/social") }
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(density.density, fontScale)
            ) {
                CaixaDSTheme {
                    androidx.compose.foundation.layout.Box(
                        androidx.compose.ui.Modifier.width(360.dp)
                    ) {
                        ResponsibleGamingRoute("responsible/social", model, {}, {}, {})
                    }
                }
            }
        }
        val description = compose.onNodeWithText("apostados nas Loterias\nCAIXA")
        description.performScrollTo().assertIsDisplayed()
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        description.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertFalse("The CAIXA line must not be clipped", layouts.single().didOverflowHeight)
        val bottom = description.getUnclippedBoundsInRoot().bottom.value
        val footnoteTop = compose.onNodeWithText("*O valor inclui a parcela correspondente ao imposto de renda sobre os prêmios.")
            .getUnclippedBoundsInRoot().top.value
        assertTrue("Footnote must follow the entire description plus legacy bottom margin", footnoteTop >= bottom + 9.5f)
    }

    @Test fun replacingViewModelMovesStateAndEffectCollectionToTheNewInstance() {
        val old = Fixture().model("responsible/start")
        val replacement = Fixture().model("responsible/start", required = true)
        val current = mutableStateOf(old)
        var backs = 0
        var completions = 0
        compose.setContent {
            CaixaDSTheme { ResponsibleGamingRoute("responsible/start", current.value, { backs++ }, { completions++ }, {}) }
        }
        compose.runOnIdle { old.onAction(ResponsibleGamingAction.Back) }
        compose.runOnIdle {
            assertEquals(1, backs)
            current.value = replacement
        }
        compose.onNodeWithText("Fechar").assertDoesNotExist()
        compose.runOnIdle { old.onAction(ResponsibleGamingAction.Complete) }
        compose.runOnIdle {
            assertEquals(0, completions)
            replacement.onAction(ResponsibleGamingAction.Complete)
        }
        compose.runOnIdle { assertEquals(1, completions) }
    }

    private fun chooseAnswer(index: Int) {
        compose.onAllNodes(radio)[index].performScrollTo().performClick()
    }

    private fun show(
        route: String,
        fixture: Fixture,
        required: Boolean = false,
        onBack: () -> Unit = {},
        onComplete: () -> Unit = {},
        onOpenTerms: () -> Unit = {},
        onSubmitSuspension: (() -> Unit)? = null,
    ) {
        compose.setContent {
            val model = remember { fixture.model(route, required) }
            CaixaDSTheme {
                ResponsibleGamingRoute(route, model, onBack, onComplete, onOpenTerms, onSubmitSuspension)
            }
        }
    }

    private class Fixture {
        var questionReads = 0
        var socialReads = 0
        var questionResult: AppResult<List<SelfAssessmentQuestion>> = AppResult.Success(
            listOf(SelfAssessmentQuestion(1, "Questão um", 1), SelfAssessmentQuestion(2, "Questão dois", 2))
        )
        var socialResult: AppResult<SocialTransfers> = AppResult.Success(transfers)
        val submissions = mutableListOf<List<SelfAssessmentAnswer>>()
        val suspensions = mutableListOf<Int>()

        fun model(route: String, required: Boolean = false) = ResponsibleGamingViewModel(
            route,
            GetSelfAssessmentQuestionsUseCase {
                questionReads++
                questionResult
            },
            SubmitSelfAssessmentUseCase {
                submissions.add(it)
                AppResult.Success(Unit)
            },
            GetSocialTransfersUseCase {
                socialReads++
                socialResult
            },
            SuspendAccountUseCase {
                suspensions.add(it)
                AppResult.Success(AccountSuspension(true, "7 dias"))
            },
            dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            assessmentRequired = required
        )
    }

    companion object {
        private val transfers = SocialTransfers(
            accumulatedRevenue = BigDecimal("1000"), lastWeekRevenue = BigDecimal.ZERO, baseDate = "2026-01-01",
            socialProgramsPercentage = BigDecimal("48"), educationPercentage = BigDecimal("10"),
            culturePercentage = BigDecimal("5"), sportPercentage = BigDecimal("5"), securityPercentage = BigDecimal("8"),
            socialSecurityPercentage = BigDecimal("10"), otherPercentage = BigDecimal("10")
        )
    }
}
