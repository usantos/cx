package br.gov.caixa.loterias.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.home.presentation.components.LotteryCard
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class HomeNativeOracleUiTest(private val fixture: Fixture) {
    data class Fixture(
        val code: String,
        val name: String,
        val type: String = "NORMAL",
        val estimate: String = "200000000",
        val open: Boolean = true,
    ) {
        override fun toString() = name
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun fixtures() = listOf(
            Fixture("MEGA_SENA", "mega"), Fixture("QUINA", "quina"),
            Fixture("LOTOFACIL", "lotofacil"), Fixture("LOTOMANIA", "lotomania"),
            Fixture("TIMEMANIA", "timemania"), Fixture("DUPLA_SENA", "dupla"),
            Fixture("DIA_DE_SORTE", "dia"), Fixture("SUPER_7", "super"),
            Fixture("MAIS_MILIONARIA", "milionaria"), Fixture("LOTECA", "loteca"),
            Fixture("LOTOGOL", "lotogol"), Fixture("INSTANTANEA", "instant"), Fixture("COMBO", "combo"),
            Fixture("MEGA_SENA", "mega-closed", open = false),
            Fixture("MEGA_SENA", "mega-no-estimate", estimate = "0"),
            Fixture("MEGA_SENA", "mega-special", type = "ESPECIAL"),
            Fixture("QUINA", "quina-special", type = "ESPECIAL"),
            Fixture("DUPLA_SENA", "dupla-special", type = "ESPECIAL"),
            Fixture("LOTOFACIL", "lotofacil-special", type = "ESPECIAL"),
            Fixture("LOTECA", "loteca-special", type = "ESPECIAL")
        ).map { arrayOf(it) }
    }

    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun originalApkHomeHolderRendersWithoutRunningLegacyApplication() {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().context
        assertTrue(
            "Forneça -PlegacyHomeReferenceAssets com legacy-reference.apk para executar a referência nativa",
            context.assets.list("").orEmpty().contains("legacy-reference.apk")
        )
        val closing = closingDateTime()
        val state = mutableStateOf(fixtureLottery(closing))
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.size(280.dp, 600.dp).testTag("card")) {
                    LotteryCard(state.value, ResultsUiState(), false, {})
                }
            }
        }
        if (fixture.type == "NORMAL") {
            assertEquals(32f, compose.onNodeWithTag("home-normal-banner").getUnclippedBoundsInRoot().top.value, .5f)
        }
        val composeBitmap = compose.onNodeWithTag("card").captureToImage().asAndroidBitmap()
        var oracle: NativeHomeOracle? = null
        val density = compose.activity.resources.displayMetrics.density
        var nativeView: android.view.View? = null
        compose.runOnIdle {
            oracle = NativeHomeOracle(compose.activity)
            val json = """{"nome":"${fixture.code}","tipoModalidade":"${fixture.code}","resultadosAberto":false,""" +
                """"concurso":{"modalidade":"${fixture.code}","tipoConcurso":"${fixture.type}",""" +
                """"modalidadeDetalhada":{"descricaoEspecial":"${fixture.name}"},"numero":3073,""" +
                """"estimativa":${fixture.estimate},"valorApostaMinima":6,"dataHoraSorteio":"22/10/2026 21:00:00",""" +
                """"dataFechamento":"$closing","aberto":${fixture.open}}}"""
            nativeView = oracle!!.bindCard(json, kotlin.math.round(280 * density).toInt(), kotlin.math.round(600 * density).toInt())
        }
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        instrumentation.runOnMainSync {
            val bitmap = oracle!!.snapshot(nativeView!!, kotlin.math.round(280 * density).toInt(), kotlin.math.round(600 * density).toInt())
            // O ViewHolder original é a referência para a paleta de cada modalidade.
            assertEquals(
                "${fixture.name} result header background",
                state.value.darkColor.toArgb(),
                bitmap.getPixel(kotlin.math.round(22 * density).toInt(), kotlin.math.round(2 * density).toInt())
            )
            if (state.value.kind == br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind.LOTTERY) {
                val header = IntArray(bitmap.width * kotlin.math.round(32 * density).toInt())
                bitmap.getPixels(header, 0, bitmap.width, 0, 0, bitmap.width, header.size / bitmap.width)
                assertTrue("${fixture.name} result controls foreground", header.count { it == state.value.darkContent.toArgb() } > 20)
                if (fixture.estimate != "0") {
                    val y = kotlin.math.round(128 * density).toInt()
                    val rows = kotlin.math.round(53 * density).toInt()
                    val prize = IntArray(bitmap.width * rows)
                    bitmap.getPixels(prize, 0, bitmap.width, 0, y, bitmap.width, rows)
                    val dominant = prize.filter { pixel ->
                        val r = android.graphics.Color.red(pixel)
                        val g = android.graphics.Color.green(pixel)
                        val b = android.graphics.Color.blue(pixel)
                        maxOf(r, g, b) - minOf(r, g, b) > 35
                    }.groupingBy { it }.eachCount().maxByOrNull { it.value }!!.key
                    assertEquals("${fixture.name} prize foreground", dominant, state.value.listText.toArgb())
                }
            }
            assertEquals(composeBitmap.width, bitmap.width)
            assertEquals(composeBitmap.height, bitmap.height)
            android.util.Log.i("LegacyHomeOracle", "themeTextColor=" + oracle!!.themeTextColor().toUInt().toString(16))
            oracle!!.export(bitmap, "simlo-native-${fixture.name}")
            oracle!!.export(composeBitmap, "simlo-compose-${fixture.name}")
        }
    }
    private fun closingDateTime(): String = java.time.LocalDateTime.now().plusHours(
        if (fixture.open) 25 else -25
    ).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
    private fun fixtureLottery(closing: String): br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery = BettingParameters(
        BigDecimal.TEN,
        BigDecimal.TEN,
        10,
        listOf(
            LotteryGameParameters(
                LotteryModality(2, fixture.code.replace("_", "-").lowercase(), fixture.code, specialName = fixture.name), 3073,
                "22/10/2026 21:00:00", 60, 6, 20, 10, emptyList(), emptyList(),
                estimatedPrize = BigDecimal(
                    fixture.estimate
                ),
                closingDateTime = closing, open = fixture.open, contestType = fixture.type
            )
        )
    ).toHomeLotteries(false).single()
}
