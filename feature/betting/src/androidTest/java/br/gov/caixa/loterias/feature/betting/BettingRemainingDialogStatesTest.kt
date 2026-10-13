package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.view.inspector.WindowInspector
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyHtmlDialog
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.io.File
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Validation/save callbacks are local fixtures; no favorites or cart requests leave the device. */
class BettingRemainingDialogStatesTest {
    @get:Rule val compose=createComposeRule()

    @Test fun duplicateDialogCancelAndConfirmUseOnlyLocalCallbacks() {
        val visible=mutableStateOf(true)
        var accepted=0;var declined=0
        compose.setContent {CaixaDSTheme(darkTheme=false) {
            if(visible.value) CaixaDSLegacyHtmlDialog(
                title="Atenção",html=br.gov.caixa.loterias.core.domain.cart.VISITOR_DUPLICATE_CONFIRMATION,
                positiveLabel="Sim",negativeLabel="Não",parseHtml=false,
                onPositive={accepted++;visible.value=false},onNegative={declined++;visible.value=false},
            )
        }}
        capture("duplicate-confirmation","callbacks=local-only\n")
        compose.onNodeWithText("Não").performClick()
        compose.waitForIdle()
        assertEquals(0,accepted);assertEquals(1,declined)
        compose.runOnIdle {visible.value=true}
        compose.onNodeWithText("Sim").performClick()
        compose.waitForIdle()
        assertEquals(1,accepted);assertEquals(1,declined)
    }

    @Test fun favoriteKeyboardFullInputPendingOverlayOverwriteCancelAndBlankName() {
        val validation=CompletableDeferred<AppResult<Unit>>()
        val validations=AtomicInteger(0);val saves=AtomicInteger(0);val cartSends=AtomicInteger(0)
        var submitted:FavoriteBetDraft?=null
        val game=LotteryGameParameters(LotteryModality(2,"Mega-Sena","MEGA_SENA"),3067,"06/10/2026",60,6,6,100,listOf(0,2,3,4,6,8,9,12),listOf(BetPrice(6,0,BigDecimal("6"))))
        val vm=BettingGameViewModel(2,GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game)))
        },object:AddBetToCartUseCase {
            override suspend fun invoke(draft:BetDraft):AppResult<ShoppingCart> { cartSends.incrementAndGet();error("No cart submission permitted") }
        },validateFavoriteBet=ValidateFavoriteBetUseCase { draft ->
            submitted=draft;validations.incrementAndGet();validation.await()
        },saveFavoriteBet=SaveFavoriteBetUseCase { saves.incrementAndGet();AppResult.Success(Unit) })
        compose.setContent { CaixaDSTheme(darkTheme=false) { BettingGameRoute(vm,{}, {}) } }
        compose.waitUntil(10_000) { vm.state.value.game!=null }
        compose.runOnIdle { (1..6).forEach(vm::toggleNumber);vm.requestFavorite() }
        capture("favorite-open")
        val instrumentation=InstrumentationRegistry.getInstrumentation()
        compose.onNodeWithTag("favorite-name-input").performClick()
        compose.waitUntil(10_000) {
            var visible=false
            instrumentation.runOnMainSync {
                visible=views().any { root ->
                    ViewCompat.getRootWindowInsets(root)?.isVisible(WindowInsetsCompat.Type.ime())==true
                }
            }
            visible
        }
        val typed="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        instrumentation.sendStringSync(typed)
        compose.waitUntil(10_000) { vm.state.value.favoriteName==typed }
        capture("favorite-keyboard", "typedFixture=$typed\ninputFilter=false\n")
        compose.onNodeWithText("Confirmar").performClick()
        compose.waitUntil(10_000) { validations.get()==1 && vm.state.value.loading }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        assertEquals(typed.take(25),submitted?.name)
        capture("favorite-pending", "fakeValidations=${validations.get()}\nsavedFavorites=${saves.get()}\n")
        validation.complete(AppResult.Failure(AppError.Validation("Já existe uma aposta favorita com esse nome. Deseja substituí-la?")))
        compose.waitUntil(10_000) { vm.state.value.favoriteOverwriteConfirmation }
        capture("favorite-overwrite", "messageSource=fake-server-validation\n")
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(0,saves.get());vm.requestFavorite() }
        compose.onNodeWithText("Confirmar").performClick()
        compose.waitUntil(10_000) { vm.state.value.errorMessage!=null }
        compose.onNodeWithText("Favor inserir um nome para sua aposta favorita").assertIsDisplayed()
        capture("favorite-blank-name")
        compose.onNodeWithText("Entendi").performClick()
        assertEquals(1,validations.get());assertEquals(0,saves.get());assertEquals(0,cartSends.get())
    }

    private fun capture(state:String,extra:String="") {
        compose.waitForIdle()
        val instrumentation=InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync();android.os.SystemClock.sleep(700);instrumentation.waitForIdleSync()
        val dir=File(requireNotNull(instrumentation.targetContext.externalCacheDir),"codex-parity").apply { mkdirs() }
        val name="compose-mega-$state";val bitmap=requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(dir,"$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) };bitmap.recycle()
        val resources=instrumentation.targetContext.resources
        File(dir,"$name.txt").writeText("fixture=true\nnetworkSends=0\ncontest=3067\ndrawDate=06/10/2026\nstate=$state\ndensityDpi=${resources.displayMetrics.densityDpi}\nfontScale=${resources.configuration.fontScale}\n$extra")
        assertTrue(File(dir,"$name.png").length()>0)
    }
    private fun views()=WindowInspector.getGlobalWindowViews().flatMap(::descendants)
    private fun descendants(view:View):List<View> = listOf(view)+if(view is ViewGroup)(0 until view.childCount).flatMap { descendants(view.getChildAt(it)) } else emptyList()
}
