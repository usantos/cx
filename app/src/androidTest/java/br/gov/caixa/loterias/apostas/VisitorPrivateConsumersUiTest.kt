package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import org.junit.Assert.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.FavoriteBetsRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.presentation.route.FavoriteCartsRoute
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.FavoriteCartsViewModel
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentDataRoute
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.route.ResponsibleGamingRoute
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel.ResponsibleGamingViewModel
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Rule
import org.junit.Test

/** Consumidores reais de rotas privadas: o visitante vê o conteúdo público inicial nativo sem chamar dependências privadas. */
class VisitorPrivateConsumersUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun visitorFavoritesRenderInitialIntroAndFilterWithoutPrivateReadOrEmptyClaim() {
        val reads = AtomicInteger()
        val vm = FavoriteBetsViewModel(
            GetFavoriteBetsUseCase { reads.incrementAndGet(); AppResult.Success(emptyList()) },
            GetBettingParametersUseCase { error("Parameters must also be deferred") },
            DeleteFavoriteBetUseCase { error("Unexpected mutation") },
            AddFavoriteBetToCartUseCase { _, _ -> error("Unexpected mutation") }, autoLoad = false,
        )
        compose.setContent { CaixaDSTheme { FavoriteBetsRoute(vm, {}, {}, {}, privateDataEnabled = false) } }
        compose.waitForIdle()
        compose.onNodeWithText("Aqui ficam seus números favoritos, prontos pra você jogar sempre que precisar.").assertExists()
        compose.onNodeWithText("Selecione a modalidade").assertExists()
        compose.onNodeWithText("Todas").assertExists()
        compose.onAllNodesWithText("Você ainda não tem", substring = true).assertCountEquals(0)
        assertEquals(0, reads.get())
    }

    @Test fun visitorFavoriteCartsRenderInitialIntroWithoutPrivateReadOrEmptyClaim() {
        val reads = AtomicInteger()
        val vm = FavoriteCartsViewModel(null,
            GetFavoriteCartsUseCase { reads.incrementAndGet(); AppResult.Success(emptyList()) },
            GetFavoriteCartBetsUseCase { error("Unexpected detail read") },
            DeleteFavoriteCartUseCase { error("Unexpected mutation") },
            DeleteFavoriteCartBetUseCase { _, _ -> error("Unexpected mutation") },
            AddFavoriteCartToCurrentUseCase { _, _ -> error("Unexpected mutation") }, autoLoad = false,
        )
        compose.setContent { CaixaDSTheme { FavoriteCartsRoute(vm, {}, {}, {}, privateDataEnabled = false) } }
        compose.waitForIdle()
        compose.onNodeWithText("Aqui estão todos os seus carrinhos favoritos.").assertExists()
        compose.onNodeWithText("Você pode excluir, detalhar e transformar no seu carrinho atual.").assertExists()
        compose.onAllNodesWithText("Você ainda não possui", substring = true).assertCountEquals(0)
        assertEquals(0, reads.get())
    }

    @Test fun visitorCardsRenderNativeRegisteredCardsHeadingWithoutPrivateReadOrEmptyClaim() {
        val reads = AtomicInteger()
        val vm = PaymentViewModel(PaymentMode.CARDS,
            getMethods = GetPaymentMethodsUseCase { error("Methods must be deferred") },
            getCards = GetSavedCardsUseCase { reads.incrementAndGet(); AppResult.Success(emptyList()) },
            deleteCard = DeleteSavedCardUseCase { _, _ -> error("Unexpected mutation") },
            generatePixCharge = GeneratePixChargeUseCase { error("Unexpected mutation") },
            getPurchaseProgress = GetPurchaseProgressUseCase { error("Unexpected purchase read") },
            validateDam = ValidateDamUseCase { error("Unexpected validation") },
            getCurrentCart = GetCurrentCartUseCase { error("Unexpected private cart read") },
            recordRecentPurchase = RecordRecentPurchaseUseCase { error("Unexpected mutation") },
            clock = java.time.Clock.systemUTC(), autoLoad = false,
        )
        compose.setContent { CaixaDSTheme { PaymentDataRoute(PaymentMode.CARDS, vm,
            onBack = {}, onHelp = {}, onPix = {}, onAddCard = {}, onOpenCard = {}, onApproved = {},
            onHome = {}, onPurchases = {}, onRedirect = {}, onRegistration = {}, privateDataEnabled = false) } }
        compose.waitForIdle()
        compose.onNodeWithText("Cartões cadastrados").assertExists()
        compose.onAllNodesWithText("Você ainda não possui", substring = true).assertCountEquals(0)
        assertEquals(0, reads.get())
    }

    @Test fun visitorAssessmentKeepsInitialPublicFormIntroWithoutQuestionReadOrErrorClaim() {
        val reads = AtomicInteger()
        val vm = ResponsibleGamingViewModel("responsible/form",
            GetSelfAssessmentQuestionsUseCase { reads.incrementAndGet(); AppResult.Success(emptyList()) },
            SubmitSelfAssessmentUseCase { error("Unexpected mutation") },
            GetSocialTransfersUseCase { error("Unexpected social read") },
            SuspendAccountUseCase { error("Unexpected mutation") }, autoLoad = false,
        )
        compose.setContent { CaixaDSTheme { ResponsibleGamingRoute("responsible/form", vm, {}, {}, {}, privateDataEnabled = false) } }
        compose.waitForIdle()
        compose.onNodeWithText("Autoavaliação para o Apostador").assertExists()
        compose.onNodeWithText("NODS*-CLIP Problem Gambling Screen").assertExists()
        compose.onNodeWithText("Próximo").assertExists()
        compose.onAllNodesWithText("Autoavaliação sem perguntas.").assertCountEquals(0)
        assertEquals(0, reads.get())
    }
}
