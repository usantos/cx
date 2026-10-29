package br.gov.caixa.loterias.feature.retail

import android.content.ActivityNotFoundException
import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
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
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeReceiptUseCases
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionInput
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel
import java.io.File
import java.io.FileNotFoundException
import java.math.BigDecimal
import java.time.Clock
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

/** Exercita a rota pública com um seletor de documentos controlado e IO real de arquivos temporários. */
class PrizeReceiptInteractionTest {
    @get:Rule val compose = createComposeRule()
    private val registry = DocumentRegistry()
    private val restoration by lazy { StateRestorationTester(compose) }
    private val opened = mutableListOf<Intent>()
    private val files = mutableListOf<File>()
    private lateinit var savedStateRegistry: SaveableStateRegistry
    private lateinit var model: PrizeRedemptionViewModel
    private var betDownloads = 0
    private var prizeDownloads = 0
    private var redemptionCalls = 0
    private var prizeDownloadFailure: AppError? = null
    private var restoredInitialChannel: PrizeRedemptionChannel? = null
    private var controlledDocumentResolver: ContentResolver? = null
    private val betBytes = "%PDF-1.7\ncontrolled bet receipt".toByteArray()
    private val prizeBytes = "%PDF-1.7\ncontrolled prize receipt".toByteArray()

    @After fun removeTemporaryFiles() {
        files.forEach { it.delete() }
    }

    @Test fun agencyWritesAndOpensPdfThenReusesTheSavedUri() {
        show()
        downloadAgencyReceipt()
        assertDocument("comprovante-aposta-9002.pdf")
        val file = documentFile()
        respond(Uri.fromFile(file))
        assertArrayEquals(betBytes, file.readBytes())
        assertOpened(Uri.fromFile(file))
        downloadAgencyReceipt()
        compose.runOnIdle {
            assertEquals(1, betDownloads)
            assertEquals(1, registry.launches.size)
            assertEquals(2, opened.size)
        }
    }

    @Test fun cancellingThePickerDoesNotCacheOrOpenAndAllowsAnotherDownload() {
        show()
        downloadAgencyReceipt()
        respond(null)
        compose.runOnIdle { assertTrue(opened.isEmpty()) }
        downloadAgencyReceipt()
        compose.runOnIdle {
            assertEquals(2, betDownloads)
            assertEquals(2, registry.launches.size)
        }
        val file = documentFile()
        respond(Uri.fromFile(file))
        assertArrayEquals(betBytes, file.readBytes())
        assertOpened(Uri.fromFile(file))
    }

    @Test fun aRepeatedFlowReceiptOpensTheCachedUriWithoutAnotherPicker() {
        show()
        downloadAgencyReceipt()
        val file = documentFile()
        respond(Uri.fromFile(file))
        compose.runOnIdle { model.downloadBetReceipt() }
        awaitReceipt { opened.size == 2 }
        compose.runOnIdle {
            assertEquals(2, betDownloads)
            assertEquals(1, registry.launches.size)
        }
        assertArrayEquals(betBytes, file.readBytes())
    }

    @Test fun pickerResultAfterRecreationWritesOriginalBytesExactlyOnceAndKeepsCache() {
        show()
        downloadAgencyReceipt()
        val requestCode = compose.runOnIdle {
            assertContainsNoPdfBytes(savedStateRegistry.performSave())
            registry.launches.single().first
        }
        restoration.emulateSavedInstanceStateRestore()
        val file = documentFile()
        respond(Uri.fromFile(file))
        assertArrayEquals(betBytes, file.readBytes())
        assertOpened(Uri.fromFile(file))
        compose.runOnIdle { registry.dispatchResult(requestCode, Uri.fromFile(file)) }
        compose.waitForIdle()
        restoration.emulateSavedInstanceStateRestore()
        downloadAgencyReceipt()
        compose.runOnIdle {
            assertEquals(1, betDownloads)
            assertEquals(1, registry.launches.size)
            assertEquals(2, opened.size)
        }
    }

    @Test fun restoredPickerMetadataRedownloadsOnlyWhenTheViewModelWasLost() {
        show()
        downloadAgencyReceipt()
        compose.runOnIdle { model = receiptModel() }
        restoration.emulateSavedInstanceStateRestore()
        val file = documentFile()
        respond(Uri.fromFile(file))
        assertArrayEquals(betBytes, file.readBytes())
        compose.runOnIdle {
            assertEquals(2, betDownloads)
            assertEquals(1, registry.launches.size)
            assertEquals(1, opened.size)
        }
    }

    @Test fun failedRestoredPrizeRecoveryCanRetryFromOptionsUsingTheOriginalDestination() = assertRestoredRecovery(false)

    @Test fun cancellingRestoredPrizeRecoveryKeepsOptionsAndStopsRetries() = assertRestoredRecovery(true)

    private fun assertRestoredRecovery(cancel: Boolean) {
        show(channel = PrizeRedemptionChannel.MERCADO_PAGO)
        compose.onNodeWithText("Resgatar prêmio").performClick()
        compose.onNodeWithText("Visualizar comprovante de prêmio").performScrollTo().performClick()
        awaitReceipt { registry.launches.size == 1 }
        assertDocument("comprovante-premio-9002.pdf")
        compose.runOnIdle {
            prizeDownloadFailure = AppError.Network
            restoredInitialChannel = null
            model = receiptModel()
        }
        restoration.emulateSavedInstanceStateRestore()
        val file = documentFile()
        compose.runOnIdle { registry.dispatchResult(registry.launches.single().first, Uri.fromFile(file)) }
        awaitReceipt { prizeDownloads == 2 && !model.state.value.loading }
        compose.runOnIdle {
            assertEquals(PrizeRedemptionStage.OPTIONS, model.state.value.stage)
            assertEquals(1, redemptionCalls)
            assertTrue(opened.isEmpty())
            assertEquals(0L, file.length())
        }
        compose.onNodeWithText("Visualizar comprovante de prêmio").assertDoesNotExist()
        compose.onNodeWithText("Não foi possível recuperar o comprovante. Tente novamente.").assertExists()
        compose.onNodeWithText("Não foi possível acessar o serviço. Verifique sua conexão.").assertDoesNotExist()
        compose.onAllNodesWithText("Atenção").assertCountEquals(1)
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle {
            assertEquals(2, prizeDownloads)
            prizeDownloadFailure = null
        }
        if (cancel) {
            compose.onNodeWithText("Cancelar").performClick()
            restoration.emulateSavedInstanceStateRestore()
            compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
            compose.onNodeWithText("Cancelar").assertDoesNotExist()
            compose.runOnIdle {
                assertEquals(PrizeRedemptionStage.OPTIONS, model.state.value.stage)
                assertEquals(2, prizeDownloads)
                assertEquals(1, registry.launches.size)
                assertEquals(1, redemptionCalls)
                assertTrue(opened.isEmpty())
                assertEquals(0L, file.length())
            }
            return
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        awaitReceipt { opened.size == 1 }
        assertArrayEquals(prizeBytes, file.readBytes())
        assertOpened(Uri.fromFile(file))
        compose.runOnIdle {
            assertEquals(3, prizeDownloads)
            assertEquals(0, betDownloads)
            assertEquals(1, registry.launches.size)
            assertEquals(1, redemptionCalls)
        }
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test fun failedRestoredPrizeWriteRetainsBytesAndDestinationForExplicitRetries() = assertRestoredWrite(false)

    @Test fun cancellingRestoredPrizeWriteKeepsOptionsAndStopsWrites() = assertRestoredWrite(true)

    private fun assertRestoredWrite(cancel: Boolean) {
        show(channel = PrizeRedemptionChannel.MERCADO_PAGO)
        compose.onNodeWithText("Resgatar prêmio").performClick()
        compose.onNodeWithText("Visualizar comprovante de prêmio").performScrollTo().performClick()
        awaitReceipt { registry.launches.size == 1 }
        val file = documentFile()
        val uri = "content://controlled-recovery/prize.pdf".toUri()
        val writes = AtomicInteger()
        compose.runOnIdle {
            controlledDocumentResolver = failingDocumentResolver(file, writes)
            restoredInitialChannel = null
            model = receiptModel()
        }
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle { registry.dispatchResult(registry.launches.single().first, uri) }
        awaitReceipt {
            writes.get() == 1 && model.receiptDelivery.state.value.saveFailure != null &&
                !model.receiptDelivery.state.value.saving
        }
        compose.onNodeWithText("Não foi possível salvar o comprovante. Tente novamente.").assertExists()
        compose.runOnIdle {
            assertEquals(PrizeRedemptionStage.OPTIONS, model.state.value.stage)
            assertEquals(2, prizeDownloads)
            assertEquals(1, redemptionCalls)
            assertTrue(opened.isEmpty())
            assertEquals(0L, file.length())
            assertContainsNoPdfBytes(savedStateRegistry.performSave())
        }
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle {
            assertEquals(1, writes.get())
            assertEquals(2, prizeDownloads)
        }
        if (cancel) {
            compose.onNodeWithText("Cancelar").performClick()
            restoration.emulateSavedInstanceStateRestore()
            compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
            compose.onNodeWithText("Cancelar").assertDoesNotExist()
            compose.runOnIdle {
                assertEquals(PrizeRedemptionStage.OPTIONS, model.state.value.stage)
                assertEquals(1, writes.get())
                assertEquals(2, prizeDownloads)
                assertEquals(1, registry.launches.size)
                assertEquals(1, redemptionCalls)
                assertTrue(opened.isEmpty())
                assertEquals(0L, file.length())
                assertNull(model.receiptDelivery.state.value.pendingFileName)
            }
            return
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        awaitReceipt {
            writes.get() == 2 && model.receiptDelivery.state.value.saveFailure != null &&
                !model.receiptDelivery.state.value.saving
        }
        compose.onNodeWithText("Não foi possível salvar o comprovante. Tente novamente.").assertExists()
        compose.runOnIdle {
            assertTrue(opened.isEmpty())
            assertEquals(0L, file.length())
        }
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle {
            assertEquals(2, writes.get())
            assertEquals(2, prizeDownloads)
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        awaitReceipt { opened.size == 1 }
        assertArrayEquals(prizeBytes, file.readBytes())
        assertOpened(uri)
        compose.runOnIdle {
            assertEquals(3, writes.get())
            assertEquals(2, prizeDownloads)
            assertEquals(0, betDownloads)
            assertEquals(1, registry.launches.size)
            assertEquals(1, redemptionCalls)
        }
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test fun cancellingRestoredPrizeViewerKeepsOptionsAndSavedReceiptWithoutAnotherOpen() {
        show(channel = PrizeRedemptionChannel.MERCADO_PAGO, viewerFailure = ActivityNotFoundException("Controlled missing viewer"))
        compose.onNodeWithText("Resgatar prêmio").performClick()
        compose.onNodeWithText("Visualizar comprovante de prêmio").performScrollTo().performClick()
        awaitReceipt { registry.launches.size == 1 }
        compose.runOnIdle {
            restoredInitialChannel = null
            model = receiptModel()
        }
        restoration.emulateSavedInstanceStateRestore()
        val file = documentFile()
        respond(Uri.fromFile(file))
        compose.onNodeWithText("Não foi possível abrir o comprovante. Tente novamente.").assertExists()
        compose.onNodeWithText("Cancelar").performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
        compose.onNodeWithText("Cancelar").assertDoesNotExist()
        assertArrayEquals(prizeBytes, file.readBytes())
        compose.runOnIdle {
            assertEquals(PrizeRedemptionStage.OPTIONS, model.state.value.stage)
            assertEquals(2, prizeDownloads)
            assertEquals(1, registry.launches.size)
            assertEquals(1, redemptionCalls)
            assertEquals(1, opened.size)
            assertEquals(Uri.fromFile(file).toString(), model.receiptDelivery.state.value.saved["comprovante-premio-9002.pdf"])
        }
    }

    @Test fun missingViewerShowsFailureAndExplicitRetryReusesSavedUri() {
        assertViewerFailure(ActivityNotFoundException("Controlled missing viewer"))
    }

    @Test fun deniedViewerShowsFailureAndExplicitRetryReusesSavedUri() {
        assertViewerFailure(SecurityException("Controlled denied viewer"))
    }

    private fun assertViewerFailure(failure: RuntimeException) {
        show(viewerFailure = failure)
        downloadAgencyReceipt()
        val file = documentFile()
        respond(Uri.fromFile(file))
        compose.onNodeWithText("Não foi possível abrir o comprovante. Tente novamente.").assertExists()
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle { assertEquals(1, opened.size) }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle {
            assertEquals(1, betDownloads)
            assertEquals(1, registry.launches.size)
            assertEquals(2, opened.size)
        }
        assertArrayEquals(betBytes, file.readBytes())
    }

    @Test fun mercadoPagoSuccessDownloadsThePrizeReceiptWithItsOriginalName() {
        show(channel = PrizeRedemptionChannel.MERCADO_PAGO)
        compose.onNodeWithText("Resgatar prêmio").performClick()
        compose.onNodeWithText("Visualizar comprovante de prêmio").performScrollTo().performClick()
        awaitReceipt { registry.launches.isNotEmpty() }
        assertDocument("comprovante-premio-9002.pdf")
        val file = documentFile()
        respond(Uri.fromFile(file))
        assertArrayEquals(prizeBytes, file.readBytes())
        assertOpened(Uri.fromFile(file))
        compose.onNodeWithText("Visualizar comprovante de prêmio").performClick()
        compose.runOnIdle {
            assertEquals(1, prizeDownloads)
            assertEquals(0, betDownloads)
            assertEquals(2, opened.size)
        }
    }

    private fun downloadAgencyReceipt() {
        compose.onNodeWithText("Gerar PDF").performScrollTo().performClick()
        compose.waitForIdle()
    }

    private fun assertDocument(name: String) = compose.runOnIdle {
        val intent = registry.launches.last().second
        assertEquals(Intent.ACTION_CREATE_DOCUMENT, intent.action)
        assertEquals("application/pdf", intent.type)
        assertEquals(name, intent.getStringExtra(Intent.EXTRA_TITLE))
    }

    private fun assertOpened(uri: Uri) = compose.runOnIdle {
        val intent = opened.last()
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("application/pdf", intent.type)
        assertEquals(uri, intent.data)
        assertEquals(Intent.FLAG_GRANT_READ_URI_PERMISSION, intent.flags)
    }

    private fun documentFile(): File = File.createTempFile(
        "prize-refactor-",
        ".pdf",
        InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
    ).also { files += it }

    private fun respond(uri: Uri?) {
        val previousOpens = compose.runOnIdle { opened.size }
        compose.runOnIdle { registry.dispatchResult(registry.launches.last().first, uri) }
        if (uri != null) awaitReceipt { opened.size > previousOpens }
        compose.waitForIdle()
    }

    private fun show(channel: PrizeRedemptionChannel = PrizeRedemptionChannel.AGENCY, viewerFailure: RuntimeException? = null) {
        val context = object : ContextWrapper(InstrumentationRegistry.getInstrumentation().targetContext) {
            override fun getApplicationContext() = this
            override fun getContentResolver() = controlledDocumentResolver ?: super.getContentResolver()
            override fun startActivity(intent: Intent) {
                opened += Intent(intent)
                viewerFailure?.let { throw it }
            }
        }
        model = receiptModel()
        restoredInitialChannel = channel
        restoration.setContent {
            val currentSavedState = LocalSaveableStateRegistry.current!!
            SideEffect { savedStateRegistry = currentSavedState }
            CompositionLocalProvider(LocalContext provides context, LocalActivityResultRegistryOwner provides registry) {
                CaixaDSTheme(darkTheme = false) {
                    PrizeRedemptionRoute(
                        model, initialChannel = restoredInitialChannel, onBack = {}, onHelp = {},
                        onHome = {}, onMyBets = {}, onRegistration = {}, onRedirect = {}, onBetEntry = {}
                    )
                }
            }
        }
        compose.waitForIdle()
    }

    private fun receiptModel() = PrizeRedemptionViewModel(
        input = PrizeRedemptionInput(9002, "fixture@example.test", "11111111111"),
        getDetails = GetPrizeDetailsUseCase {
            AppResult.Success(
                PrizeDetails(
                    9002,
                    BigDecimal.TEN,
                    "CONTROLLED-NSBI",
                    PrizeRedemptionChannel.entries.map { PrizeRedemptionOption(it, it.name, "CONTROLLED") }
                )
            )
        },
        generateLotteryCode = GenerateLotteryRedemptionCodeUseCase {
            AppResult.Success(LotteryRedemptionCode("CONTROLLED-QR", "CONTROLLED-CODE", "CONTROLLED-NSB", 15))
        },
        consultPixKey = ConsultPixRedemptionKeyUseCase { _, _ -> error("Unexpected Pix consultation") },
        redeemPrize = RedeemPrizeUseCase { _, selected, _ ->
            redemptionCalls++
            check(selected == PrizeRedemptionChannel.MERCADO_PAGO)
            AppResult.Success(PrizeRedemptionResult.Redeemed())
        },
        cancelPix = CancelPixRedemptionUseCase { AppResult.Success(Unit) },
        receiptUseCases = PrizeReceiptUseCases(
            DownloadBetReceiptUseCase {
                betDownloads++
                AppResult.Success(betBytes)
            },
            DownloadPrizeReceiptUseCase {
                prizeDownloads++
                prizeDownloadFailure?.let { AppResult.Failure(it) } ?: AppResult.Success(prizeBytes)
            }
        ),
        getBettingParameters = GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, emptyList()))
        },
        prepareBetEntry = PrepareBetEntryUseCase { AppResult.Failure(AppError.Unexpected) },
        localClock = Clock.systemUTC(),
        networkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED },
        dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
    )

    private fun failingDocumentResolver(file: File, writes: AtomicInteger): ContentResolver = ContentResolver.wrap(
        object : ContentProvider() {
            override fun onCreate() = true
            override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
                if (writes.incrementAndGet() <= 2) throw FileNotFoundException("Controlled provider failure")
                return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_WRITE_ONLY or ParcelFileDescriptor.MODE_TRUNCATE)
            }
            override fun getType(uri: Uri) = "application/pdf"
            override fun query(
                uri: Uri,
                projection: Array<out String>?,
                selection: String?,
                selectionArgs: Array<out String>?,
                sortOrder: String?,
            ): Cursor? = null
            override fun insert(uri: Uri, values: ContentValues?): Uri? = null
            override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
            override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
        }
    )

    private fun assertContainsNoPdfBytes(value: Any?) {
        when (value) {
            is ByteArray -> fail("PDF bytes must stay out of saved-state Bundles")
            is State<*> -> assertContainsNoPdfBytes(value.value)
            is Map<*, *> -> value.values.forEach(::assertContainsNoPdfBytes)
            is Iterable<*> -> value.forEach(::assertContainsNoPdfBytes)
            is Array<*> -> value.forEach(::assertContainsNoPdfBytes)
            is Bundle -> value.keySet().forEach { assertContainsNoPdfBytes(value.get(it)) }
        }
    }

    private class DocumentRegistry :
        ActivityResultRegistry(),
        ActivityResultRegistryOwner {
        override val activityResultRegistry: ActivityResultRegistry get() = this
        val launches = mutableListOf<Pair<Int, Intent>>()
        override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
            launches += requestCode to contract.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, input)
        }
    }

    /** A conclusão de IO publica o estado de snapshot do Compose fora do relógio de teste da regra. */
    private fun awaitReceipt(condition: () -> Boolean) {
        compose.waitUntil(5_000) {
            compose.runOnIdle { Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            condition()
        }
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }
}
