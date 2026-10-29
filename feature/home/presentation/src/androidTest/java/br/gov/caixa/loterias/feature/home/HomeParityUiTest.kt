package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.components.*
import br.gov.caixa.loterias.feature.home.presentation.state.*
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Referências de card_modalidade.xml e ScaleTransformer(0.8) de PrincipalActivity. */
class HomeParityUiTest {
    @get:Rule val compose = createComposeRule()
    private val lottery = HomeLottery(
        2, "NORMAL", "megasena", "Mega-Sena", 3073,
        BigDecimal("200000000"), BigDecimal("6"), "22/10/2026 21:00:00", "", true,
        HomeLotteryKind.LOTTERY, Color(0xFF00A651), Color(0xFF00984A), Color.White,
        R.drawable.legacy_home_trevo_megasena_fundo_escuro,
        R.drawable.icon_data_megasena, R.drawable.icon_horario_megasena
    )

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { Box(Modifier.size(280.dp, 600.dp).background(Color.Magenta)) { content() } } }
    }

    @Test fun homeHeaderRetainsLegacyWhiteBackgroundAndCampaignControls() {
        val actions = br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions(
            retry = {}, openMenu = {}, openNotifications = {}, toggleCampaign = {}, toggleQuickAccess = {},
            readTicket = {}, responsibleGaming = {}, socialTransfers = {}, womenSupport = {}, toggleResult = {},
            pool = {}, bet = {}, openCart = {}
        )
        compose.setContent {
            CaixaDSTheme {
                HomeTopBar(
                    0,
                    br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign.OUTUBRO_ROSA,
                    false,
                    90f,
                    actions
                )
            }
        }
        val menu = compose.onNodeWithContentDescription("Abrir menu").fetchSemanticsNode().boundsInRoot
        val image = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(Color.White, image[image.width / 2, menu.center.y.toInt()])
        compose.onNodeWithContentDescription("Notificações").assertIsDisplayed()
        compose.onNodeWithContentDescription("Outubro Rosa").assertIsDisplayed()
        compose.onNodeWithContentDescription("Apresentar atalhos").assertIsDisplayed()
    }

    @Test fun homeActionsWaitForInitialModalitiesAndStayVisibleDuringRefresh() {
        val loading = androidx.compose.runtime.mutableStateOf(true)
        val actions = br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions(
            retry = {}, openMenu = {}, openNotifications = {}, toggleCampaign = {}, toggleQuickAccess = {},
            readTicket = {}, responsibleGaming = {}, socialTransfers = {}, womenSupport = {}, toggleResult = {},
            pool = {}, bet = {}, openCart = {}
        )
        compose.setContent {
            CaixaDSTheme {
                br.gov.caixa.loterias.feature.home.presentation.screen.HomeContent(
                    HomeUiState(loading = loading.value),
                    br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign.OUTUBRO_ROSA,
                    listOf(lottery),
                    ResultsUiState(),
                    null,
                    br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation(false, null, false, 0.dp, 90f),
                    actions
                )
            }
        }
        compose.onNodeWithContentDescription("Abrir menu").assertDoesNotExist()
        compose.onNodeWithContentDescription("Notificações").assertDoesNotExist()
        compose.onNodeWithContentDescription("Outubro Rosa").assertDoesNotExist()
        compose.onNodeWithContentDescription("Apresentar atalhos").assertDoesNotExist()
        compose.runOnIdle { loading.value = false }
        compose.onNodeWithContentDescription("Abrir menu").assertIsDisplayed()
        compose.onNodeWithContentDescription("Notificações").assertIsDisplayed()
        compose.onNodeWithContentDescription("Outubro Rosa").assertIsDisplayed()
        compose.onNodeWithContentDescription("Apresentar atalhos").assertIsDisplayed()
        compose.runOnIdle { loading.value = true }
        compose.onNodeWithContentDescription("Abrir menu").assertIsDisplayed()
        compose.onNodeWithContentDescription("Notificações").assertIsDisplayed()
        compose.onNodeWithContentDescription("Outubro Rosa").assertIsDisplayed()
        compose.onNodeWithContentDescription("Apresentar atalhos").assertIsDisplayed()
        compose.runOnIdle { loading.value = false }
        compose.onNodeWithContentDescription("Notificações").assertIsDisplayed()
    }

    @Test fun resultLabelIsCenteredInIts240DpHeader() {
        show { LotteryCard(lottery, ResultsUiState(), false, {}) }
        val label = compose.onNodeWithText("Último resultado").getUnclippedBoundsInRoot()
        assertEquals(140f, (label.left.value + label.right.value) / 2f, GEOMETRY_TOLERANCE)
    }

    @Test fun resultRevealKeepsTextStableAndReturnsBannerAfterClosing() {
        compose.mainClock.autoAdvance = false
        val expanded = androidx.compose.runtime.mutableStateOf(false)
        val result = br.gov.caixa.loterias.feature.results.domain.model.DrawResult(
            "megasena", RESULT_CONTEST_NUMBER, "21/10/2026", "", RESULT_DRAWN_NUMBERS,
            emptyList(), emptyList(), false, "", "0", "", emptyList()
        )
        show { LotteryCard(lottery, ResultsUiState(result = result), expanded.value, { expanded.value = !expanded.value }) }
        compose.runOnIdle {
            expanded.value = true
            androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
        }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(100)
        compose.onNodeWithContentDescription("Fechar resultado").assertExists()
        val moving = compose.onNodeWithTag("home-result-scroll").getUnclippedBoundsInRoot()
        assertEquals(280f, (moving.right - moving.left).value, GEOMETRY_TOLERANCE)
        compose.mainClock.advanceTimeBy(RESULT_OPEN_DELAY_MILLIS)
        compose.onNodeWithText("01").assertIsDisplayed()
        compose.onNodeWithText("Último resultado").performClick()
        compose.mainClock.advanceTimeBy(RESULT_ANIMATION_SETTLE_MILLIS)
        assertEquals(
            COLLAPSED_BANNER_TOP,
            compose.onNodeWithTag("home-normal-banner").getUnclippedBoundsInRoot().top.value,
            GEOMETRY_TOLERANCE
        )
    }

    @Test fun titleStartsImmediatelyAfter85DpTrapezoid() {
        show { LotteryBanner(lottery) }
        assertEquals(85f, compose.onNodeWithText("mega-sena").getUnclippedBoundsInRoot().left.value, GEOMETRY_TOLERANCE)
    }

    @Test fun adjacentCardShrinksOnBothAxes() {
        show { HomeLegacyPagerCard(lottery, ResultsUiState(), false, {}, { 1f }) }
        val pixels = compose.onRoot().captureToImage().toPixelMap()
        val density = pixels.width / 280f
        assertEquals(Color.Magenta, pixels[(10 * density).toInt(), (110 * density).toInt()])
        assertEquals(lottery.color, pixels[(180 * density).toInt(), (110 * density).toInt()])
    }

    @Test fun captionWidthMatchesNativeXmlTextViewAtDeviceDensity() {
        show { CollapsedLotteryContent(lottery) }
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val caption = "Prêmio estimado do concurso 3073"
        var expectedWidth = 0
        instrumentation.runOnMainSync {
            val native = android.widget.TextView(context)
            val size = kotlin.math.round(
                android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_SP, 13.8f, context.resources.displayMetrics)
            )
            native.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, size)
            val text = android.text.SpannableString(caption)
            text.setSpan(
                android.text.style.TypefaceSpan(
                    context.resources.getFont(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular)
                ),
                0,
                27,
                33
            )
            text.setSpan(
                android.text.style.TypefaceSpan(context.resources.getFont(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_bold)),
                27,
                caption.length,
                33
            )
            native.text = text
            native.measure(android.view.View.MeasureSpec.makeMeasureSpec(0, 0), android.view.View.MeasureSpec.makeMeasureSpec(0, 0))
            expectedWidth = native.measuredWidth
        }
        val bounds = compose.onNodeWithText(caption).getUnclippedBoundsInRoot()
        val actual = (bounds.right.value - bounds.left.value) * context.resources.displayMetrics.density
        assertEquals(expectedWidth.toFloat(), actual, 1f)
    }

    @Test fun quickAccessLocksToggleAndDelaysReturnUntilFadeChainCompletes() {
        compose.mainClock.autoAdvance = false
        val expanded = androidx.compose.runtime.mutableStateOf(false)
        var motion: br.gov.caixa.loterias.feature.home.presentation.route.HomePanelAnimations? = null
        compose.setContent {
            motion = br.gov.caixa.loterias.feature.home.presentation.route.homePanelAnimations(expanded.value, false, 500.dp)
            androidx.compose.material3.Text("Animação")
        }
        compose.runOnIdle { expanded.value = true }
        compose.mainClock.advanceTimeByFrame()
        compose.runOnIdle { assertEquals(false, motion!!.quickAccessToggleEnabled) }
        compose.mainClock.advanceTimeBy(1600)
        compose.runOnIdle { assertEquals(true, motion!!.quickAccessToggleEnabled) }
        compose.runOnIdle { expanded.value = false }
        compose.mainClock.advanceTimeBy(2000)
        compose.runOnIdle {
            assertEquals(500f, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(false, motion!!.quickAccessToggleEnabled)
        }
        compose.mainClock.advanceTimeBy(1200)
        compose.runOnIdle {
            assertEquals(0f, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(true, motion!!.quickAccessToggleEnabled)
        }
    }

    @Test fun campaignUsesContentHeightWithoutMaskingCarouselFooter() {
        compose.mainClock.autoAdvance = false
        val campaign = androidx.compose.runtime.mutableStateOf(false)
        var motion: br.gov.caixa.loterias.feature.home.presentation.route.HomePanelAnimations? = null
        compose.setContent {
            motion =
                br.gov.caixa.loterias.feature.home.presentation.route.homePanelAnimations(
                    false,
                    campaign.value,
                    quickAccessOffset = 600.dp,
                    campaignContentHeight = 314.dp
                )
            androidx.compose.material3.Text("Campanha")
        }
        compose.runOnIdle { campaign.value = true }
        compose.mainClock.advanceTimeBy(800)
        compose.runOnIdle {
            assertEquals(330f, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(360f, motion!!.campaignRotation, GEOMETRY_TOLERANCE)
            assertEquals(0f, motion!!.footerScrimAlpha, .01f)
            assertEquals(true, motion!!.quickAccessToggleEnabled)
            campaign.value = false
        }
        compose.mainClock.advanceTimeBy(450)
        compose.runOnIdle {
            assertEquals(330f, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(false, motion!!.quickAccessToggleEnabled)
        }
        compose.mainClock.advanceTimeBy(700)
        compose.runOnIdle {
            assertEquals(0f, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(0f, motion!!.campaignRotation, GEOMETRY_TOLERANCE)
            assertEquals(0f, motion!!.footerScrimAlpha, .01f)
            assertEquals(true, motion!!.quickAccessToggleEnabled)
        }
    }

    @Test fun campaignNeverDrawsTheQuickAccessWhiteFooterOverModalities() {
        val alpha = androidx.compose.runtime.mutableStateOf(.8f)
        val actions = br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions(
            retry = {}, openMenu = {}, openNotifications = {}, toggleCampaign = {}, toggleQuickAccess = {},
            readTicket = {}, responsibleGaming = {}, socialTransfers = {}, womenSupport = {}, toggleResult = {},
            pool = {}, bet = {}, openCart = {}
        )
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.size(320.dp, 700.dp).background(Color.Magenta)) {
                    HomeCarousel(
                        HomeUiState(), listOf(lottery), ResultsUiState(), lottery,
                        androidx.compose.foundation.pager.rememberPagerState(pageCount = { 1 }),
                        br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation(
                            false,
                            null,
                            true,
                            314.dp,
                            90f,
                            footerScrimAlpha = alpha.value
                        ),
                        280.dp, 20.dp, true, false, actions
                    )
                }
            }
        }
        val campaign = compose.onRoot().captureToImage().asAndroidBitmap()
        compose.runOnIdle { alpha.value = 0f }
        val unobstructed = compose.onRoot().captureToImage().asAndroidBitmap()
        org.junit.Assert.assertTrue("Campaign must not add the quick-access footer mask", campaign.sameAs(unobstructed))
    }

    @Test fun zeroEstimateUsesLegacySingleMultilineText() {
        show { CollapsedLotteryContent(lottery.copy(estimatedPrize = BigDecimal.ZERO)) }
        compose.onNodeWithText("Próximo Concurso\n\n3073\n\nAguardando estimativa de prêmio").assertIsDisplayed()
    }

    @Test fun lotecaUsesClosingDateAndEncerramentoLabel() {
        show {
            CollapsedLotteryContent(
                lottery.copy(resultCode = "loteca", drawDate = "23/10/2026 21:00:00", closingDateTime = "22/10/2026 15:00:00")
            )
        }
        compose.onNodeWithText("Encerramento").assertIsDisplayed()
        compose.onNodeWithText("Quinta-feira,\n22/10/2026 às 15:00:00").assertIsDisplayed()
    }

    @Test fun expiredSalesShowApostasEncerradas() {
        show { CollapsedLotteryContent(lottery.copy(closingDateTime = "01/01/2020 00:00:00")) }
        compose.onNodeWithText("Apostas", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Encerradas").assertIsDisplayed()
    }

    @Test fun captureSameCardAndViewportAsLegacyReference() {
        val closing = java.time.LocalDateTime.now().plusHours(25).plusMinutes(5)
            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        val mega = lottery.copy(
            estimatedPrize = BigDecimal("92000000"),
            contestNumber = 3067,
            drawDate = "06/10/2026 21:00:00",
            closingDateTime = closing
        )
        val lotofacil = mega.copy(
            modalityId = 8,
            name = "Lotofácil",
            resultCode = "lotofacil",
            color = Color(0xFF92278F),
            darkColor = Color(0xFF802081),
            cloverRes = R.drawable.legacy_home_trevo_lotofacil_fundo_escuro
        )
        val timemania = mega.copy(
            modalityId = 20,
            name = "Timemania",
            resultCode = "timemania",
            color = Color(0xFFCADB2D),
            darkColor = Color(0xFF91A439),
            headerContentColor = Color(0xFF00A651),
            cloverRes = R.drawable.legacy_home_trevo_timemania_fundo_escuro
        )
        val parameters = br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters(
            BigDecimal.TEN,
            BigDecimal.TEN,
            10,
            emptyList(),
            listOf(br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability(2, "MEGA_SENA", "NORMAL"))
        )
        val actions = br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions(
            retry = {}, openMenu = {}, openNotifications = {}, toggleCampaign = {}, toggleQuickAccess = {},
            readTicket = {}, responsibleGaming = {}, socialTransfers = {}, womenSupport = {}, toggleResult = {},
            pool = {}, bet = {}, openCart = {}
        )
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                br.gov.caixa.loterias.feature.home.presentation.screen.HomeContent(
                    HomeUiState(
                        loading = false,
                        parameters = parameters,
                        unreadNotificationCount = 1,
                        features = br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration(poolSalesEnabled = true)
                    ),
                    null,
                    listOf(mega, lotofacil, timemania),
                    ResultsUiState(),
                    null,
                    br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation(false, null, false, 0.dp, 90f),
                    actions
                )
            }
        }
        compose.onNodeWithText("mega-sena").assertIsDisplayed()
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val output = java.io.File(instrumentation.context.getExternalFilesDir(null), "home-parity-reference.png")
        output.outputStream().use {
            instrumentation.uiAutomation.takeScreenshot().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        instrumentation.uiAutomation.executeShellCommand(
            "cp ${output.absolutePath} /sdcard/Download/simlo-home-parity-reference.png"
        ).use { android.os.ParcelFileDescriptor.AutoCloseInputStream(it).readBytes() }
    }
}

private const val RESULT_CONTEST_NUMBER = 3073
private val RESULT_DRAWN_NUMBERS = listOf(1, 5, 18, 22, 42, 57)
private const val RESULT_ANIMATION_SETTLE_MILLIS = 400L
private const val COLLAPSED_BANNER_TOP = 32f
private const val GEOMETRY_TOLERANCE = .5f
private const val RESULT_OPEN_DELAY_MILLIS = 300L
