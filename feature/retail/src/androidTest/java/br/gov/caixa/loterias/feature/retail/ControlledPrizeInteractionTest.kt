package br.gov.caixa.loterias.feature.retail

import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.retail.*
import java.math.BigDecimal
import java.time.Clock
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Synthetic fixture, no HTTP request, credentials, real prize or captured personal data. */
class ControlledPrizeInteractionTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var vm: PrizeRedemptionViewModel
    private val consults = AtomicInteger()
    private val redemptions = AtomicInteger()
    private val myBets = AtomicInteger()
    private var submitted: Triple<Long, PrizeRedemptionChannel, String>? = null

    @Test fun consultationDoesNotRedeemAndExplicitConfirmNavigatesWithoutResubmission() {
        show(initialChannel = PrizeRedemptionChannel.PIX)
        compose.onNodeWithText("Continuar").performScrollTo().performClick()
        compose.onNodeWithText("Chave: fixture@example.test").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, consults.get()); assertEquals(0, redemptions.get()) }
        compose.onNodeWithText("Resgatar").performScrollTo().performClick()
        compose.onNodeWithText("Seu resgate foi solicitado com sucesso!").assertIsDisplayed()
        compose.onNodeWithText("Acesse \nMinhas apostas").performScrollTo().performClick()
        compose.runOnIdle {
            vm.confirmPix(); vm.resumeAfterRedirect()
            assertEquals(1, redemptions.get()); assertEquals(1, myBets.get())
            assertEquals(Triple(9002L, PrizeRedemptionChannel.PIX, "fixture@example.test"), submitted)
        }
    }

    @Test fun noEligibleChannelsCannotSelectPixOrRenderSuccessFromAlias() {
        show(noEligibleChannels = true, previewChannel = PrizeRedemptionChannel.PIX)
        compose.onNodeWithText("Escolha como resgatar seu prêmio").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Seu resgate foi solicitado com sucesso!").assertDoesNotExist()
        compose.runOnIdle {
            vm.select(PrizeRedemptionChannel.PIX)
            assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
            assertNull(vm.state.value.selectedChannel)
            assertEquals(0, consults.get()); assertEquals(0, redemptions.get())
        }
    }

    @Test fun capturePixChoiceAndConfirmation() {
        show(initialChannel = PrizeRedemptionChannel.PIX)
        compose.onNodeWithText("Continuar").performScrollTo().assertIsDisplayed()
        capture("prize-pix-choice")
        compose.onNodeWithText("Continuar").performClick()
        compose.onNodeWithText("Chave: fixture@example.test").performScrollTo().assertIsDisplayed()
        capture("prize-pix-confirmation")
        compose.runOnIdle { assertEquals(0, redemptions.get()) }
    }

    @Test fun captureMercadoPagoWithoutRedeeming() {
        show(initialChannel = PrizeRedemptionChannel.MERCADO_PAGO)
        compose.onNodeWithText("Resgatar prêmio").assertIsDisplayed()
        capture("prize-mercado-pago")
        compose.runOnIdle { assertEquals(0, redemptions.get()) }
    }

    @Test fun controlledMercadoPagoConfirmationShowsSuccessUsingOnlyFakeRepository() {
        show(initialChannel = PrizeRedemptionChannel.MERCADO_PAGO)
        compose.onNodeWithText("Resgatar prêmio").performClick()
        compose.waitUntil(5_000) { vm.state.value.stage == PrizeRedemptionStage.SUCCESS }
        compose.onNodeWithText("Solicitação efetuada com sucesso!").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, redemptions.get())
            assertEquals(Triple(9002L, PrizeRedemptionChannel.MERCADO_PAGO, "fixture@example.test"), submitted)
        }
    }

    @Test fun captureLotteryCodeWithoutRedeeming() {
        show(initialChannel = PrizeRedemptionChannel.LOTTERY)
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("O QR Code é válido", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("O QR Code é válido", substring = true).assertIsDisplayed()
        capture("prize-lottery-top")
        compose.onNodeWithText("Mostre o QR Code ao atendente").performScrollTo()
        capture("prize-lottery-instructions")
        compose.runOnIdle { assertEquals(0, redemptions.get()) }
    }

    @Test fun captureAgencyWithoutDownloadingOrRedeeming() {
        show(initialChannel = PrizeRedemptionChannel.AGENCY)
        compose.onNodeWithText("Gerar PDF").performScrollTo().assertIsDisplayed()
        capture("prize-agency")
        compose.runOnIdle { assertEquals(0, redemptions.get()) }
    }

    @Test fun installedSevenReaisOptionsCaptureUsesOnlyEligibleChannels() {
        show(installedSeven = true)
        compose.onNodeWithText("Escolha como resgatar seu prêmio").assertExists()
        capture("prize-installed-seven-options")
        listOf("Mercado Pago", "Lotérica", "Agência Caixa").forEach { label ->
            compose.onNodeWithText(label, substring = true).assertExists()
        }
        val mark = compose.onNodeWithContentDescription("Mercado Pago", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        val density = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .targetContext.resources.displayMetrics.density
        assertEquals(18f * density, mark.width, 1f)
        assertEquals(12f * density, mark.height, 1f)
        compose.onNodeWithText("PIX").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, consults.get()); assertEquals(0, redemptions.get()) }
    }

    @Test fun installedOptionsFollowAwardWithoutOldContestOrDetailSections() {
        show(installedSeven = true)
        compose.onNodeWithText("Escolha como resgatar seu prêmio").assertExists()
        compose.onNodeWithText("Ver detalhe da aposta").assertDoesNotExist()
        compose.onNodeWithText("Informações do concurso atual").assertDoesNotExist()
    }

    @Test fun installedSevenReaisMercadoPagoFormNeverRequestsRedemption() {
        show(initialChannel = PrizeRedemptionChannel.MERCADO_PAGO, installedSeven = true)
        compose.onNodeWithText("Resgate de Prêmio via…", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Resgatar prêmio").assertIsDisplayed()
        compose.onNodeWithContentDescription("Mercado Pago").assertIsDisplayed()
        compose.onNodeWithText("7,00", substring = true).assertIsDisplayed()
        capture("prize-installed-seven-mercado-form")
        compose.onNodeWithText("fixture@example.test").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Resgatar prêmio").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, consults.get()); assertEquals(0, redemptions.get()) }
    }

    @Test fun installedPrizeHeaderRetainsContestNumber() {
        show(installedSeven = true)
        compose.onNodeWithText("Concurso").assertIsDisplayed()
        compose.onNodeWithText("3797").assertIsDisplayed()
    }

    @Test fun installedSingleAwardUsesDescriptionAndAmountWithoutOldSixColumnTable() {
        show(installedSeven = true)
        compose.onNodeWithText("11 acertos").assertIsDisplayed()
        listOf("Condição", "Faixa", "Qtde", "Sorteio", "Valor").forEach {
            compose.onNodeWithText(it).assertDoesNotExist()
        }
    }

    @Test fun installedOptionTextUsesPackedGreyAndExplicitSizes() {
        show(installedSeven = true)
        val title = textLayout("Mercado Pago").layoutInput.style
        val subtitle = textLayout("Receba na hora").layoutInput.style
        assertEquals("Packed greyText", br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens.LegacySocialGraphite, title.color)
        assertEquals("Packed subtitle greyText", br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens.LegacySocialGraphite, subtitle.color)
        assertEquals("Legacy title16sp", 16.sp, title.fontSize)
        assertEquals("Legacy subtitle14sp", 14.sp, subtitle.fontSize)
        assertEquals(br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily, title.fontFamily)
        assertEquals(br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily, subtitle.fontFamily)
        assertEquals(androidx.compose.ui.text.font.FontWeight.Bold, title.fontWeight)
        assertEquals(androidx.compose.ui.text.font.FontWeight.Normal, subtitle.fontWeight)
    }

    @Test fun installedMercadoPagoActionLabelFitsOneLine() {
        show(initialChannel = PrizeRedemptionChannel.MERCADO_PAGO, installedSeven = true)
        val label = textLayout("Resgatar prêmio")
        assertEquals("Native184dp action keeps its entire label on one line", 1, label.lineCount)
        compose.runOnIdle { assertEquals(0, redemptions.get()) }
    }

    @Test fun installedPrizeSummaryRetainsTheProductionBottomCoordinate() {
        show(installedSeven = true)
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val metrics = instrumentation.targetContext.resources.displayMetrics
        org.junit.Assume.assumeTrue(metrics.densityDpi == 450)
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            org.junit.Assume.assumeTrue(bitmap.width == 1080 && bitmap.height == 2408)
            val purple = android.graphics.Color.rgb(145, 39, 143)
            val firstWhite = (300 until 1100).firstOrNull { y ->
                android.graphics.Color.red(bitmap.getPixel(20, y)) > 250 &&
                    android.graphics.Color.green(bitmap.getPixel(20, y)) > 250 &&
                    android.graphics.Color.blue(bitmap.getPixel(20, y)) > 250
            }
            assertEquals("Production prize summary ends at the installed coordinate", 880, firstWhite)
            assertEquals("The measured summary uses the installed purple background", purple, bitmap.getPixel(20, 879))
        } finally {
            bitmap.recycle()
        }
    }

    @Test fun installedMercadoPagoPrizeAndInstructionsUsePackedTextColour() {
        show(initialChannel = PrizeRedemptionChannel.MERCADO_PAGO, installedSeven = true)
        val expected = androidx.compose.ui.graphics.Color(0xFF48586C)
        val label = textLayout("Prêmio").layoutInput.style
        assertEquals("Caixa DS MP prize label", expected, label.color)
        assertEquals("Caixa DS MP prize textSize", 15.199982f.sp, label.fontSize)
        assertEquals(br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily, label.fontFamily)
        assertEquals("Caixa DS MP instructions", expected,
            textLayout("Para receber seu prêmio,\npor favor confirme seu e-mail:").layoutInput.style.color)
        compose.runOnIdle { assertEquals(0, redemptions.get()) }
    }

    @Test fun installedRecurringRangeIgnoresNonAwardedContestPlaceholder() {
        show(installedSeven = true, includeNonAwarded = true)
        compose.onNodeWithText("3797 - 3797").assertIsDisplayed()
        compose.onNodeWithText("3797 - 3798").assertDoesNotExist()
    }

    @Test fun installedAwardTableDoesNotRenderEmptyNonAwardedRows() {
        show(installedSeven = true, includeNonAwarded = true)
        compose.onNodeWithText("11 acertos").assertIsDisplayed()
        compose.onAllNodesWithText("", useUnmergedTree = true).assertCountEquals(0)
    }

    private fun textLayout(text: String): androidx.compose.ui.text.TextLayoutResult {
        val result = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        compose.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) { it(result) }
        return result.single()
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val folder = java.io.File(requireNotNull(instrumentation.targetContext.externalCacheDir), "codex-financial-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        java.io.File(folder, "$name.png").outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        java.io.File(folder, "$name.txt").writeText("fixture=true\nnetworkSends=0\nredemptions=${redemptions.get()}\ndensityDpi=${instrumentation.targetContext.resources.displayMetrics.densityDpi}\nfontScale=${instrumentation.targetContext.resources.configuration.fontScale}\n")
        android.util.Log.i("CodexParityCapture", java.io.File(folder, "$name.png").absolutePath)
        assertTrue(java.io.File(folder, "$name.png").length() > 0)
    }

    private fun show(initialChannel: PrizeRedemptionChannel? = null,
        noEligibleChannels: Boolean = false, previewChannel: PrizeRedemptionChannel? = null, installedSeven: Boolean = false, includeNonAwarded: Boolean = false) {
        compose.setContent {
            val model = remember {
                PrizeRedemptionViewModel(
                    betId = 9002, initialEmail = "fixture@example.test", initialCpf = "11111111111",
                    getDetails = GetPrizeDetailsUseCase { AppResult.Success(if (installedSeven) PrizeDetails(
                        9002, BigDecimal("7.00"), "CONTROLLED-NSBI", listOf(
                            PrizeRedemptionOption(PrizeRedemptionChannel.MERCADO_PAGO, "Mercado Pago", "Receba na hora"),
                            PrizeRedemptionOption(PrizeRedemptionChannel.LOTTERY, "Lotérica", "Retire em qualquer lotérica"),
                            PrizeRedemptionOption(PrizeRedemptionChannel.AGENCY, "Agência Caixa", "Valores acima da isenção do IR")),
                        modalityId = 8, modalityName = "Lotofácil", initialContest = 3797, targetContest = 3797,
                        recurringDraws = if (includeNonAwarded) 1 else 0,
                        awards = listOf(PrizeAward(3797, "11 acertos", BigDecimal("7.00"), hitCount = 1)) +
                            (if (includeNonAwarded) listOf(PrizeAward(3798, "", null, status = "Não premiada", isAwarded = false)) else emptyList()))
                        else PrizeDetails(9002, BigDecimal.TEN,
                        "CONTROLLED-NSBI", if (noEligibleChannels) emptyList() else PrizeRedemptionChannel.entries.map { channel -> PrizeRedemptionOption(channel, channel.name, "CONTROLLED") })) },
                    generateLotteryCode = GenerateLotteryRedemptionCodeUseCase {
                        check(!installedSeven) { "Installed display pair must not generate a redemption code" }
                        AppResult.Success(LotteryRedemptionCode("CONTROLLED-QR-DO-NOT-REDEEM", "CONTROLLED-CODE", "CONTROLLED-NSB", 15)) },
                    consultPixKey = ConsultPixRedemptionKeyUseCase { _, _ ->
                        consults.incrementAndGet()
                        AppResult.Success(PixBeneficiary("CONTROLLED", "Fixture", "fixture@example.test", "Fixture Bank"))
                    },
                    redeemPrize = RedeemPrizeUseCase { id, channel, key ->
                        check(!installedSeven) { "Installed display pair must not redeem a prize" }
                        redemptions.incrementAndGet(); submitted = Triple(id, channel, key)
                        AppResult.Success(PrizeRedemptionResult.Redeemed())
                    },
                    cancelPix = CancelPixRedemptionUseCase { AppResult.Success(Unit) },
                    downloadBetReceiptUseCase = DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
                    downloadPrizeReceiptUseCase = DownloadPrizeReceiptUseCase { AppResult.Success(byteArrayOf()) },
                    getBettingParameters = GetBettingParametersUseCase {
                        AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, emptyList()))
                    },
                    prepareBetEntry = PrepareBetEntryUseCase { AppResult.Failure(AppError.Unexpected) },
                    localClock = Clock.systemUTC(),
                    networkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                ).also { vm = it }
            }
            val activityResultOwner = requireNotNull(LocalActivityResultRegistryOwner.current)
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides activityResultOwner) {
                CaixaDSTheme(darkTheme = false) {
                    PrizeRedemptionRoute(model, initialChannel = initialChannel, previewSuccessChannel = previewChannel,
                        onBack = {}, onHelp = {}, onHome = {}, onMyBets = { myBets.incrementAndGet() },
                        onRegistration = {}, onRedirect = {}, onBetEntry = {})
                }
            }
        }
    }
}
