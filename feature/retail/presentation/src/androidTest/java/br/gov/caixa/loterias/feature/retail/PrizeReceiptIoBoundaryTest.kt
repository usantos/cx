package br.gov.caixa.loterias.feature.retail

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Looper
import android.os.ParcelFileDescriptor
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.presentation.route.PrizeReceiptManager
import br.gov.caixa.loterias.feature.retail.presentation.route.rememberPrizeReceiptManager
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeReceiptUseCases
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionInput
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel
import java.io.File
import java.math.BigDecimal
import java.time.Clock
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Exercita a fronteira da plataforma; apenas despachar uma operação sem relação não permite passar no teste. */
class PrizeReceiptIoBoundaryTest {
    @get:Rule val compose = createComposeRule()

    @Test fun documentProviderAndViewerUseTheirRespectiveThreadsAndKeepPdfBytes() = verifyWrite(false)

    @Test fun recreationDuringBlockingWriteFinishesOnceWithoutPublishingPartialFile() = verifyWrite(true)

    @Test fun disposingCompositionDuringBlockingWriteDoesNotLaunchViewer() = verifyWrite(true, true)

    private fun verifyWrite(recreateDuringWrite: Boolean, disposeDuringWrite: Boolean = false) {
        val visible = mutableStateOf(true)
        val restoration = StateRestorationTester(compose)
        val entered = CountDownLatch(1)
        val release = CountDownLatch(if (recreateDuringWrite) 1 else 0)
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File.createTempFile("prize-io-", ".pdf", base.cacheDir)
        val providerThreads = CopyOnWriteArrayList<Thread>()
        val viewerThreads = CopyOnWriteArrayList<Thread>()
        val opened = CopyOnWriteArrayList<Intent>()
        val bytes = "%PDF-1.7\nprize IO boundary".toByteArray()
        val uri = "content://controlled-prize/receipt.pdf".toUri()
        val provider = object : ContentProvider() {
            override fun onCreate() = true
            override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
                providerThreads += Thread.currentThread()
                entered.countDown()
                check(release.await(5, TimeUnit.SECONDS))
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
        val resolver = ContentResolver.wrap(provider)
        val context = object : ContextWrapper(base) {
            override fun getApplicationContext() = this
            override fun getContentResolver() = resolver
            override fun startActivity(intent: Intent) {
                viewerThreads += Thread.currentThread()
                opened += Intent(intent)
            }
        }
        val registry = object : ActivityResultRegistry(), ActivityResultRegistryOwner {
            override val activityResultRegistry: ActivityResultRegistry get() = this
            var requestCode = 0
            var title: String? = null
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?,
            ) {
                this.requestCode = requestCode
                title = contract.createIntent(base, input).getStringExtra(Intent.EXTRA_TITLE)
            }
        }
        val model = receiptModel(bytes)
        lateinit var manager: PrizeReceiptManager
        try {
            restoration.setContent {
                CompositionLocalProvider(LocalContext provides context, LocalActivityResultRegistryOwner provides registry) {
                    if (visible.value) {
                        val current = rememberPrizeReceiptManager(model)
                        SideEffect { manager = current }
                    }
                }
            }
            compose.runOnIdle { manager.openOrDownload("comprovante-aposta-9002.pdf", model::downloadBetReceipt) }
            awaitReceipt { registry.requestCode != 0 }
            compose.runOnIdle { registry.dispatchResult(registry.requestCode, uri) }
            if (recreateDuringWrite) {
                awaitReceipt { entered.count == 0L }
                if (disposeDuringWrite) {
                    compose.runOnIdle { visible.value = false }
                    compose.waitForIdle()
                } else {
                    restoration.emulateSavedInstanceStateRestore()
                }
                compose.runOnIdle {
                    assertTrue(opened.isEmpty())
                    assertEquals(0L, file.length())
                }
                release.countDown()
            }
            if (disposeDuringWrite) {
                awaitReceipt { file.length() == bytes.size.toLong() }
                compose.waitForIdle()
                assertTrue(opened.isEmpty())
                assertArrayEquals(bytes, file.readBytes())
                assertEquals(1, providerThreads.size)
                return
            }
            awaitReceipt { opened.size == 1 }
            assertEquals("comprovante-aposta-9002.pdf", registry.title)
            assertArrayEquals(bytes, file.readBytes())
            assertNotEquals(Looper.getMainLooper().thread, providerThreads.single())
            assertEquals(Looper.getMainLooper().thread, viewerThreads.single())
            assertEquals(uri, opened.single().data)
            assertEquals("application/pdf", opened.single().type)
            compose.runOnIdle { manager.openOrDownload("comprovante-aposta-9002.pdf") { error("Saved receipt must be reused") } }
            assertEquals(2, opened.size)
            assertEquals(1, providerThreads.size)
        } finally {
            release.countDown()
            file.delete()
        }
    }

    private fun receiptModel(bytes: ByteArray) = PrizeRedemptionViewModel(
        input = PrizeRedemptionInput(9002, "", ""),
        getDetails = GetPrizeDetailsUseCase { AppResult.Success(PrizeDetails(9002, BigDecimal.TEN, "", emptyList())) },
        generateLotteryCode = GenerateLotteryRedemptionCodeUseCase { error("Unexpected lottery request") },
        consultPixKey = ConsultPixRedemptionKeyUseCase { _, _ -> error("Unexpected Pix request") },
        redeemPrize = RedeemPrizeUseCase { _, _, _ -> error("Unexpected redemption") },
        cancelPix = CancelPixRedemptionUseCase { error("Unexpected cancellation") },
        receiptUseCases = PrizeReceiptUseCases(
            DownloadBetReceiptUseCase {
                AppResult.Success(bytes)
            },
            DownloadPrizeReceiptUseCase { error("Unexpected prize download") }
        ),
        getBettingParameters = GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, emptyList()))
        },
        prepareBetEntry = PrepareBetEntryUseCase { AppResult.Failure(AppError.Unexpected) },
        localClock = Clock.systemUTC(),
        networkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED },
        dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
    )

    /** A conclusão de IO publica o estado de snapshot do Compose fora do relógio de teste da regra. */
    private fun awaitReceipt(condition: () -> Boolean) {
        compose.waitUntil(5_000) {
            compose.runOnIdle { Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            condition()
        }
    }
}
