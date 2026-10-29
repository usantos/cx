package br.gov.caixa.loterias.feature.retail

import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

/** Recriação real com ActivityScenario; distinta da cobertura de metadados após perda do processo. */
class PrizeReceiptActivityRecreationTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun activityRecreationWhilePickerIsOpenRetainsOwnerAndDeliversOneWrite() {
        val bytes = "%PDF-1.7\nactivity recreation receipt".toByteArray()
        val opened = CopyOnWriteArrayList<Intent>()
        val registry = object : ActivityResultRegistry(), ActivityResultRegistryOwner {
            override val activityResultRegistry: ActivityResultRegistry get() = this
            var requestCode = 0
            var launches = 0
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?,
            ) {
                this.requestCode = requestCode
                launches++
                val intent = contract.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, input)
                assertEquals("application/pdf", intent.type)
                assertEquals("comprovante-aposta-9002.pdf", intent.getStringExtra(Intent.EXTRA_TITLE))
            }
        }
        lateinit var manager: PrizeReceiptManager
        lateinit var model: PrizeRedemptionViewModel
        var downloads = 0
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = receiptModel(bytes) {
                downloads++
            } as T
        }
        fun render() = compose.activityRule.scenario.onActivity { activity ->
            model = ViewModelProvider(activity, factory)[PrizeRedemptionViewModel::class.java]
            val context = object : ContextWrapper(activity) {
                override fun startActivity(intent: Intent) {
                    opened += Intent(intent)
                }
            }
            activity.setContent {
                CompositionLocalProvider(LocalContext provides context, LocalActivityResultRegistryOwner provides registry) {
                    val current = rememberPrizeReceiptManager(model)
                    SideEffect { manager = current }
                }
            }
        }
        val file = File.createTempFile("prize-activity-", ".pdf", InstrumentationRegistry.getInstrumentation().targetContext.cacheDir)
        try {
            render()
            compose.waitForIdle()
            val original = model
            compose.runOnIdle { manager.openOrDownload("comprovante-aposta-9002.pdf", model::downloadBetReceipt) }
            awaitReceipt { registry.launches == 1 }
            compose.activityRule.scenario.recreate()
            render()
            compose.waitForIdle()
            assertSame(original, model)
            compose.runOnIdle { registry.dispatchResult(registry.requestCode, Uri.fromFile(file)) }
            awaitReceipt { opened.size == 1 }
            assertArrayEquals(bytes, file.readBytes())
            compose.runOnIdle { registry.dispatchResult(registry.requestCode, Uri.fromFile(file)) }
            compose.waitForIdle()
            assertEquals(1, opened.size)
            compose.runOnIdle { manager.openOrDownload("comprovante-aposta-9002.pdf") { fail("Saved receipt must be reused") } }
            assertEquals(2, opened.size)
            assertEquals(1, downloads)
            assertEquals(1, registry.launches)
            assertEquals(Uri.fromFile(file), opened.last().data)
            assertEquals(Intent.FLAG_GRANT_READ_URI_PERMISSION, opened.last().flags)
        } finally {
            file.delete()
        }
    }

    private fun receiptModel(bytes: ByteArray, onDownload: () -> Unit) = PrizeRedemptionViewModel(
        input = PrizeRedemptionInput(9002, "", ""),
        getDetails = GetPrizeDetailsUseCase { AppResult.Success(PrizeDetails(9002, BigDecimal.TEN, "", emptyList())) },
        generateLotteryCode = GenerateLotteryRedemptionCodeUseCase { error("Unexpected lottery request") },
        consultPixKey = ConsultPixRedemptionKeyUseCase { _, _ -> error("Unexpected Pix request") },
        redeemPrize = RedeemPrizeUseCase { _, _, _ -> error("Unexpected redemption") },
        cancelPix = CancelPixRedemptionUseCase { error("Unexpected cancellation") },
        receiptUseCases = PrizeReceiptUseCases(
            DownloadBetReceiptUseCase {
                onDownload()
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
