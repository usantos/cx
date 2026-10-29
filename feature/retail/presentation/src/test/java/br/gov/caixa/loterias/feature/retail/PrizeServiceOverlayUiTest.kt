// Literais numéricos descrevem dados sintéticos, níveis de API da plataforma e limites esperados dos dados de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.retail

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionOption
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.presentation.route.PrizeRedemptionRoute
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeReceiptUseCases
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionInput
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel
import java.math.BigDecimal
import java.time.Clock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Todos os resultados do serviço são valores sintéticos no processo; nenhum serviço financeiro é contatado. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PrizeServiceOverlayUiTest {
    @get:Rule val compose = createComposeRule()
    private val redirects = mutableListOf<ServiceRedirect>()
    private val registrations = mutableListOf<String>()
    private var submissions = 0
    private var backRequests = 0
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }
    private fun show(detailsGate: CompletableDeferred<AppResult<PrizeDetails>>? = null, outcome: () -> PrizeRedemptionResult) {
        val model = PrizeRedemptionViewModel(
            PrizeRedemptionInput(9, "fixture@example.test", "synthetic-cpf"),
            GetPrizeDetailsUseCase {
                detailsGate?.await()
                    ?: AppResult.Success(
                        PrizeDetails(
                            9,
                            BigDecimal.TEN,
                            "synthetic-nsbi",
                            listOf(PrizeRedemptionOption(PrizeRedemptionChannel.MERCADO_PAGO, "Mercado Pago", ""))
                        )
                    )
            },
            GenerateLotteryRedemptionCodeUseCase {
                error("No lottery request")
            },
            ConsultPixRedemptionKeyUseCase { _, _ -> error("No Pix request") },
            RedeemPrizeUseCase { id, channel, email ->
                assertEquals(9L, id)
                assertEquals(PrizeRedemptionChannel.MERCADO_PAGO, channel)
                assertEquals("fixture@example.test", email)
                submissions++
                AppResult.Success(outcome())
            },
            CancelPixRedemptionUseCase {
                error("No Pix cancellation")
            },
            PrizeReceiptUseCases(DownloadBetReceiptUseCase { error("No document") }, DownloadPrizeReceiptUseCase { error("No document") }),
            GetBettingParametersUseCase { AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 100, emptyList())) },
            PrepareBetEntryUseCase {
                AppResult.Failure(AppError.Unexpected)
            },
            Clock.systemUTC(),
            NetworkAvailability {
                ConnectionAvailability.CONNECTED
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        )
        compose.setContent {
            CaixaDSTheme {
                PrizeRedemptionRoute(
                    model, initialChannel = PrizeRedemptionChannel.MERCADO_PAGO,
                    onBack = {
                        backRequests++
                    }, onHelp = {}, onHome = {}, onMyBets = {}, onRegistration = { registrations += it }, onRedirect = {
                        redirects +=
                            it
                    }, onBetEntry = { error("No bet entry") }
                )
            }
        }
        flush()
    }
    private fun submit() {
        compose.onNodeWithText("Resgatar prêmio").performClick()
        flush()
    }

    @Test fun `initial details loading stays on loading surface until controlled payload arrives`() {
        val gate = CompletableDeferred<AppResult<PrizeDetails>>()
        show(gate) { error("No submission allowed") }
        compose.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, backRequests)
            assertEquals(0, submissions)
            gate.complete(
                AppResult.Success(
                    PrizeDetails(
                        9,
                        BigDecimal.TEN,
                        "synthetic-nsbi",
                        listOf(PrizeRedemptionOption(PrizeRedemptionChannel.MERCADO_PAGO, "Mercado Pago", ""))
                    )
                )
            )
        }
        flush()
        compose.onNodeWithText("Resgate de Prêmio via Mercado Pago").assertExists()
        compose.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertDoesNotExist()
        assertEquals(0, submissions)
    }

    @Test fun `terms redirect waits for explicit acknowledgment and never repeats synthetic submission`() {
        show { PrizeRedemptionResult.RedirectRequired(ServiceRedirect.ACCEPT_TERMS) }
        submit()
        compose.runOnIdle {
            assertTrue(redirects.isEmpty())
            assertEquals(1, submissions)
        }
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(listOf(ServiceRedirect.ACCEPT_TERMS), redirects)
            assertEquals(1, submissions)
        }
    }

    @Test fun `registration rejection and acceptance keep the original email and require separate decisions`() {
        show { PrizeRedemptionResult.RegistrationRequired("Cadastro de teste necessário") }
        submit()
        compose.onNodeWithText("Cadastro de teste necessário").assertExists()
        compose.onNodeWithText("Não").performClick()
        flush()
        compose.runOnIdle {
            assertTrue(registrations.isEmpty())
            assertEquals(1, submissions)
        }
        submit()
        compose.onNodeWithText("Sim").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(listOf("fixture@example.test"), registrations)
            assertEquals(2, submissions)
            assertTrue(redirects.isEmpty())
        }
    }

    @Test fun `ordinary notice dismisses locally and more bets actions distinguish cancel from navigation`() {
        var outcome: PrizeRedemptionResult = PrizeRedemptionResult.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))
        show { outcome }
        submit()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertTrue(redirects.isEmpty())
            outcome =
                PrizeRedemptionResult.NoticeRequired(ServiceNotice(ServiceNoticeReason.MORE_BETS_REQUIRED))
        }
        submit()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertTrue(redirects.isEmpty())
            assertEquals(2, submissions)
        }
        submit()
        compose.onNodeWithText("Apostar mais").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(listOf(ServiceRedirect.HOME), redirects)
            assertEquals(3, submissions)
        }
    }

    @Test fun `successful recurring prize notice is acknowledged without requesting a receipt or new submission`() {
        show { PrizeRedemptionResult.Redeemed("MI053") }
        submit()
        compose.onNodeWithText("Prêmio resgatado com sucesso!", substring = true).assertExists()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.onNodeWithText("Prêmio resgatado com sucesso!", substring = true).assertDoesNotExist()
        compose.onNodeWithText("Voltar ao início").assertExists()
        compose.runOnIdle {
            assertEquals(1, submissions)
            assertTrue(registrations.isEmpty())
            assertTrue(redirects.isEmpty())
        }
    }
}
