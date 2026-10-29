package br.gov.caixa.loterias.apostas

import android.app.Application
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import br.gov.caixa.loterias.apostas.runtime.shortcuts.RESULT_SHORTCUT_ACTION
import br.gov.caixa.loterias.apostas.runtime.shortcuts.RESULT_SHORTCUT_ID_EXTRA
import br.gov.caixa.loterias.apostas.runtime.shortcuts.ResultShortcutDestination
import br.gov.caixa.loterias.apostas.runtime.shortcuts.ResultShortcutRequests
import br.gov.caixa.loterias.apostas.runtime.shortcuts.publishResultLauncherShortcuts
import br.gov.caixa.loterias.apostas.runtime.shortcuts.reportResultLauncherShortcutUsage
import br.gov.caixa.loterias.apostas.runtime.shortcuts.resultShortcutDestination
import br.gov.caixa.loterias.apostas.runtime.shortcuts.resultShortcutIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ResultLauncherShortcutsTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Test fun publishesTheFourSelectedPublicResultActionsForTheActualPackage() {
        val manager = context.getSystemService(ShortcutManager::class.java)
        shadowOf(manager).setMaxShortcutCountPerActivity(ResultShortcutDestination.entries.size)
        assertTrue(publishResultLauncherShortcuts(context))
        val shortcuts = manager.dynamicShortcuts.sortedBy { it.rank }
        assertEquals(ResultShortcutDestination.entries.map { it.id }, shortcuts.map { it.id })
        assertEquals(
            listOf("Resultado Mega-Sena", "Resultado Lotofácil", "Resultado Loteca", "Todos os resultados"),
            shortcuts.map { it.shortLabel.toString() }
        )
        for ((shortcut, destination) in shortcuts.zip(ResultShortcutDestination.entries)) {
            assertEquals(context.packageName, shortcut.activity?.packageName)
            assertEquals(MainActivity::class.java.name, shortcut.intent?.component?.className)
            assertEquals(context.packageName, shortcut.intent?.component?.packageName)
            assertEquals(destination, resultShortcutDestination(shortcut.intent))
        }
    }

    @Test fun publishesEachModalitiesOwnColoredIcon() {
        assertTrue(publishResultLauncherShortcuts(context))
        val shortcuts = context.getSystemService(ShortcutManager::class.java).dynamicShortcuts
        val expected = mapOf(
            "result_megasena" to R.drawable.trevo_megasena,
            "result_lotofacil" to R.drawable.trevo_lotofacil,
            "result_loteca" to R.drawable.trevo_loteca,
            "results_all" to R.drawable.menu3_resultados
        )
        for (shortcut in shortcuts) {
            val icon = ReflectionHelpers.callInstanceMethod<Icon>(shortcut, "getIcon")
            assertEquals(expected.getValue(shortcut.id), icon.resId)
        }
    }

    @Test fun everyPublishedIconIsVisibleOnTheWhiteLauncherMenu() {
        assertTrue(publishResultLauncherShortcuts(context))
        val shortcuts = context.getSystemService(ShortcutManager::class.java).dynamicShortcuts
        for (shortcut in shortcuts) {
            val icon = ReflectionHelpers.callInstanceMethod<Icon>(shortcut, "getIcon")
            val drawable = requireNotNull(icon.loadDrawable(context))
            val bitmap = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            drawable.setBounds(0, 0, ICON_SIZE, ICON_SIZE)
            drawable.draw(canvas)
            val pixels = IntArray(ICON_SIZE * ICON_SIZE)
            bitmap.getPixels(pixels, 0, ICON_SIZE, 0, 0, ICON_SIZE, ICON_SIZE)
            val colored = pixels.count { pixel ->
                val channels = listOf(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
                channels.max() - channels.min() > MIN_COLOR_DIFFERENCE
            }
            assertTrue(shortcut.id + " should have a visible colored icon", colored > pixels.size / MIN_COLORED_FRACTION)
        }
    }

    @Test fun smallerLauncherCapacityKeepsAnEntryToAllModalities() {
        val manager = context.getSystemService(ShortcutManager::class.java)
        shadowOf(manager).setMaxShortcutCountPerActivity(SMALL_CAPACITY)
        assertTrue(publishResultLauncherShortcuts(context))
        assertEquals(listOf("result_megasena", "results_all"), manager.dynamicShortcuts.sortedBy { it.rank }.map { it.id })
    }

    @Test fun publishingAgainKeepsStableIdsWithoutDuplicates() {
        assertTrue(publishResultLauncherShortcuts(context))
        assertTrue(publishResultLauncherShortcuts(context))
        val ids = context.getSystemService(ShortcutManager::class.java).dynamicShortcuts.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
        assertEquals(ResultShortcutDestination.entries.size, ids.size)
    }

    @Test fun rejectsUnknownActionsAndPrivateOrMalformedDestinations() {
        assertNull(resultShortcutDestination(null))
        assertNull(resultShortcutDestination(Intent(Intent.ACTION_MAIN).putExtra(RESULT_SHORTCUT_ID_EXTRA, "result_megasena")))
        for (id in listOf("", "payment/cards", "results/home/megasena", "result_unknown")) {
            assertNull(resultShortcutDestination(Intent(RESULT_SHORTCUT_ACTION).putExtra(RESULT_SHORTCUT_ID_EXTRA, id)))
        }
        assertEquals(ResultShortcutDestination.MEGA_SENA, resultShortcutDestination(intent(ResultShortcutDestination.MEGA_SENA)))
    }

    @Test fun restoresAnUnconsumedColdRequestAfterStartupRecreation() {
        val requests = ResultShortcutRequests()
        requests.restore(intent(ResultShortcutDestination.LOTECA), null)
        val saved = Bundle()
        requests.save(saved)
        val recreated = ResultShortcutRequests()
        recreated.restore(Intent(Intent.ACTION_MAIN), saved)
        assertEquals(ResultShortcutDestination.LOTECA, recreated.pending?.destination)
        assertTrue(requireNotNull(recreated.pending).coldStart)
    }

    @Test fun consumedOriginalIntentIsNotReplayedAfterRecreation() {
        val original = intent(ResultShortcutDestination.LOTOFACIL)
        val requests = ResultShortcutRequests()
        requests.restore(original, null)
        requests.consume(requireNotNull(requests.pending))
        val saved = Bundle()
        requests.save(saved)
        val recreated = ResultShortcutRequests()
        recreated.restore(original, saved)
        assertNull(recreated.pending)
    }

    @Test fun aLateConsumerCannotDiscardTheNewerWarmShortcut() {
        val requests = ResultShortcutRequests()
        requests.restore(intent(ResultShortcutDestination.MEGA_SENA), null)
        val previous = requireNotNull(requests.pending)
        requests.receive(intent(ResultShortcutDestination.ALL))
        val latest = requireNotNull(requests.pending)
        assertTrue(latest.coldStart)
        requests.consume(previous)
        assertSame(latest, requests.pending)
        requests.receive(Intent(Intent.ACTION_MAIN))
        assertSame(latest, requests.pending)
        requests.consume(latest)
        assertNull(requests.pending)
    }

    @Test fun aSecondShortcutDuringStartupKeepsThePublicHomeBackStack() {
        val requests = ResultShortcutRequests()
        requests.restore(intent(ResultShortcutDestination.MEGA_SENA), null)
        requests.receive(intent(ResultShortcutDestination.LOTECA))
        assertEquals(ResultShortcutDestination.LOTECA, requests.pending?.destination)
        assertTrue(requireNotNull(requests.pending).coldStart)
    }

    @Test fun repeatedWarmActionsCreateDistinctConsumableRequests() {
        val requests = ResultShortcutRequests()
        requests.receive(intent(ResultShortcutDestination.MEGA_SENA))
        val first = requireNotNull(requests.pending)
        requests.consume(first)
        requests.receive(intent(ResultShortcutDestination.MEGA_SENA))
        assertNotNull(requests.pending)
        assertFalse(first === requests.pending)
    }

    @Test fun reportsColdShortcutOnlyAfterItsPendingRequestIsConsumed() {
        val manager = context.getSystemService(ShortcutManager::class.java)
        val requests = ResultShortcutRequests { reportResultLauncherShortcutUsage(context, it) }
        requests.restore(intent(ResultShortcutDestination.LOTECA), null)
        val request = requireNotNull(requests.pending)
        assertTrue(publishResultLauncherShortcuts(context))
        assertTrue(shadowOf(manager).reportedShortcutsUsed.isEmpty())

        requests.consume(request)
        requests.consume(request)

        assertEquals(listOf("result_loteca"), shadowOf(manager).reportedShortcutsUsed)
        assertNull(requests.pending)
    }

    @Test fun reportsOnlyTheCurrentWarmRequestAndCountsRepeatedActivations() {
        val manager = context.getSystemService(ShortcutManager::class.java)
        val requests = ResultShortcutRequests { reportResultLauncherShortcutUsage(context, it) }
        requests.receive(intent(ResultShortcutDestination.MEGA_SENA))
        val stale = requireNotNull(requests.pending)
        requests.receive(intent(ResultShortcutDestination.ALL))
        requests.consume(stale)
        assertTrue(shadowOf(manager).reportedShortcutsUsed.isEmpty())

        requests.consume(requireNotNull(requests.pending))
        requests.receive(intent(ResultShortcutDestination.ALL))
        requests.consume(requireNotNull(requests.pending))

        assertEquals(listOf("results_all", "results_all"), shadowOf(manager).reportedShortcutsUsed)
    }

    @Test fun unavailableShortcutUsageServiceDoesNotInterruptConsumption() {
        for (failure in listOf(null, IllegalStateException(), SecurityException())) {
            val unavailable = object : ContextWrapper(context) {
                override fun getSystemService(name: String): Any? {
                    if (failure != null) throw failure
                    return null
                }
            }
            val requests = ResultShortcutRequests { reportResultLauncherShortcutUsage(unavailable, it) }
            requests.receive(intent(ResultShortcutDestination.LOTOFACIL))

            requests.consume(requireNotNull(requests.pending))

            assertNull(requests.pending)
        }
    }

    private fun intent(destination: ResultShortcutDestination) = resultShortcutIntent(context, destination)
}

private const val SMALL_CAPACITY = 2

private const val ICON_SIZE = 96
private const val MIN_COLOR_DIFFERENCE = 32
private const val MIN_COLORED_FRACTION = 10
