package br.gov.caixa.loterias.apostas

import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.text.TextCatalog
import java.util.Locale
import java.util.ResourceBundle
import java.util.zip.ZipFile
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Verifica os recursos JVM do APK instalado contra textos capturados antes da extração. */
class MessageResourcesPackagingTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val target = instrumentation.targetContext
    private val loader = target.classLoader
    private val portuguese = Locale("pt", "BR")

    @Test
    fun everyBundleAndKeyIsPackagedOnceAndPreservesCapturedText() {
        val fixture = fixture()
        val byBundle = fixture.groupBy(MessageFixture::bundle)
        assertEquals(EXPECTED_BUNDLE_COUNT, byBundle.size)
        assertEquals(EXPECTED_MESSAGE_COUNT, fixture.size)
        val apkPaths = listOf(target.applicationInfo.sourceDir) + target.applicationInfo.splitSourceDirs.orEmpty()
        byBundle.forEach { (bundle, messages) ->
            assertPackagedOnce(bundle, apkPaths)
            val packaged = ResourceBundle.getBundle(bundle, portuguese, loader)
            assertEquals("Chaves do bundle $bundle", messages.map(MessageFixture::key).toSet(), packaged.keySet())
            val catalog = TextCatalog(bundle, loader)
            messages.forEach { message ->
                assertEquals("Texto de $bundle/${message.key}", message.expected, catalog.text(message.key, locale = portuguese))
            }
        }
        assertFixtureExercisesLiteralBoundaries(fixture)
    }

    @Test
    fun formatsInterpolatedMessageAndUsesPortugueseBaseForUntranslatedLocale() {
        val captured = fixture().single { it.key == MISSING_NUMBERS_KEY && it.bundle == BETTING_PRESENTATION_BUNDLE }
        assertEquals("Selecione mais %1\$s números.", captured.expected)
        val catalog = TextCatalog(captured.bundle, loader)
        assertEquals(INTERPOLATED_MESSAGE, catalog.text(captured.key, MISSING_NUMBERS, locale = portuguese))
        // Produção contém somente a base portuguesa; outros idiomas devem usar essa base.
        assertEquals(INTERPOLATED_MESSAGE, catalog.text(captured.key, MISSING_NUMBERS, locale = Locale.ENGLISH))
        assertEquals(captured.expected, catalog.text(captured.key, locale = portuguese))
    }

    private fun assertPackagedOnce(bundle: String, apkPaths: List<String>) {
        val path = bundle.replace('.', '/') + ".properties"
        val owners = apkPaths.filter { apk -> ZipFile(apk).use { it.getEntry(path) != null } }
        assertEquals("O recurso $path deve ter um único dono nos APKs do aplicativo", SINGLE_OWNER_COUNT, owners.size)
        assertTrue("O classloader do aplicativo deve encontrar $path", loader.getResource(path) != null)
    }

    private fun assertFixtureExercisesLiteralBoundaries(messages: List<MessageFixture>) {
        assertTrue("A fixture deve cobrir acentos", messages.any { it.expected.contains('ç') })
        assertTrue("A fixture deve cobrir quebras de linha reais", messages.any { it.expected.contains('\n') })
        assertTrue("A fixture deve cobrir aspas literais", messages.any { it.expected.contains('"') })
        assertTrue("A fixture deve cobrir espaços finais", messages.any { it.expected.endsWith(' ') })
    }

    private fun fixture(): List<MessageFixture> {
        val json = instrumentation.context.assets.open(FIXTURE_ASSET).bufferedReader(Charsets.UTF_8).use { JSONObject(it.readText()) }
        assertEquals(EXPECTED_SOURCE_ENTRY_COUNT, json.getInt("sourceEntryCount"))
        assertEquals(EXPECTED_BUNDLE_COUNT, json.getInt("bundleCount"))
        val messages = json.getJSONArray("messages")
        return List(messages.length()) { index ->
            val message = messages.getJSONObject(index)
            MessageFixture(message.getString("bundle"), message.getString("key"), message.getString("expected"))
        }
    }

    private data class MessageFixture(val bundle: String, val key: String, val expected: String)

    private companion object {
        const val FIXTURE_ASSET = "message-resources-fixture.json"
        const val EXPECTED_SOURCE_ENTRY_COUNT = 533
        const val EXPECTED_BUNDLE_COUNT = 35
        const val EXPECTED_MESSAGE_COUNT = 590
        const val SINGLE_OWNER_COUNT = 1
        const val MISSING_NUMBERS = 3
        const val MISSING_NUMBERS_KEY = "selecione_mais_1_s_numeros_868900"
        const val BETTING_PRESENTATION_BUNDLE = "br.gov.caixa.loterias.feature.betting.presentation.formatting.messages"
        const val INTERPOLATED_MESSAGE = "Selecione mais 3 números."
    }
}
