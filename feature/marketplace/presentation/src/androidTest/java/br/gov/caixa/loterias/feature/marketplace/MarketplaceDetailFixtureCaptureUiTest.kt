package br.gov.caixa.loterias.feature.marketplace

import android.graphics.Bitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.*
import br.gov.caixa.loterias.feature.betting.domain.model.*
import br.gov.caixa.loterias.feature.marketplace.domain.model.*
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.io.File
import java.math.BigDecimal
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Rule
import org.junit.Test

/** Capturas sem conexão correspondentes aos conteúdos de LegacyDetailInstrumentation exclusivos dos testes. */
class MarketplaceDetailFixtureCaptureUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun captureRequestedOfflineDetailFixture() {
        val args = InstrumentationRegistry.getArguments()
        val modality = args.getString("modality", "MEGA_SENA")!!
        val contestType = args.getString("contesttype", "1")!!.toInt()
        val october = args.getString("october", "false").toBoolean()
        val specialName = args.getString("specialName")
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val fixture = instrumentation.context.assets.open("pool-detail-capture-fixture.json")
            .bufferedReader().use { JSONObject(it.readText()).getJSONObject(modality) }
        val detail = fixture.detail(modality, contestType, args.getString("contest", "3070")!!.toInt())
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                val campaign = if (october) CaixaDSCampaign.OCTOBER_ROSE else CaixaDSCampaign.DEFAULT
                CaixaDSCampaignTheme(campaign, campaign) {
                    MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail, isLoading = false,
                        favoriteLottery = false), {}, {}, {}, {}, {}, cartTotal = BigDecimal("35"),
                        cartBetCount = 1, onCart = {}, specialName = specialName)
                }
            }
        }
        val prefix = "compose-detail-${modality.lowercase()}-$contestType-${if (october) "october" else "normal"}"
        capture("$prefix-initial")
        repeat(6) {
            compose.onAllNodes(hasScrollAction()).onFirst().performTouchInput { swipeUp(durationMillis = 300) }
            compose.waitForIdle()
        }
        capture("$prefix-games")
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        android.os.SystemClock.sleep(400)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val output = File(instrumentation.context.filesDir, "pool-detail-captures").apply { mkdirs() }
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(output, "$name.png").outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        val nodes = compose.onAllNodes(SemanticsMatcher("all nodes") { true }, useUnmergedTree = true).fetchSemanticsNodes()
        val values = JSONArray()
        compose.runOnIdle {
            nodes.forEach { node ->
                val config = node.config
                val bounds = node.boundsInRoot
                val value = JSONObject().put("boundsInRoot", JSONArray(listOf(bounds.left, bounds.top, bounds.right, bounds.bottom)))
                config.getOrNull(SemanticsProperties.Text)?.let { value.put("text", it.joinToString(" ")) }
                config.getOrNull(SemanticsProperties.ContentDescription)?.let { value.put("description", it.joinToString(" ")) }
                config.getOrNull(SemanticsProperties.TestTag)?.let { value.put("tag", it) }
                val layouts = mutableListOf<TextLayoutResult>()
                config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(layouts)
                layouts.firstOrNull()?.layoutInput?.style?.let {
                    value.put("fontSizeSp", it.fontSize.value).put("fontWeight", it.fontWeight?.weight)
                        .put("color", it.color.toArgb())
                }
                values.put(value)
            }
        }
        File(output, "$name.json").writeText(JSONObject().put("density", compose.density.density)
            .put("nodes", values).toString(2))
    }

    private fun JSONObject.detail(modality: String, contestType: Int, contest: Int): MarketplacePoolDetail {
        val first = getJSONArray("apostas").getJSONObject(0)
        val rawNumbers = first.getJSONArray("dezenas")
        val columns = if (modality == "SUPER_7") (0 until rawNumbers.length()).map { rawNumbers.getJSONArray(it).ints() } else emptyList()
        val selections = first.optJSONArray("partidasLoteca")?.let { matches -> (0 until matches.length()).map {
            val match = matches.getJSONObject(it)
            val home = match.getJSONObject("equipe1")
            val away = match.getJSONObject("equipe2")
            LotecaMatchSelection(SportsMatch(match.getInt("numero"), home.getJSONObject("parametroEquipe").team(),
                away.getJSONObject("parametroEquipe").team(), match.optString("descricaoLegenda"), match.optString("letraLegenda")),
                home.optBoolean("vitoria"), match.optBoolean("empate"), away.optBoolean("vitoria"))
        } }.orEmpty()
        val bet = MarketplaceBet(if (columns.isEmpty()) rawNumbers.ints() else emptyList(), first.getJSONArray("trevos").ints(),
            false, columns, first.optJSONObject("timeDoCoracao")?.team(), first.optJSONObject("mesDeSorte")?.let {
                LuckyMonth(it.getInt("numero"), it.getString("nome"), it.optString("abreviacao"))
            }, selections)
        return MarketplacePoolDetail("OFFLINE-VISUAL-B1", modality, contest, getLong("loterica"), getString("nomeFantasia"),
            getJSONObject("lotericaFormatada").getString("numeroFormatado"), "São Paulo - SP", getString("dataSorteio"),
            get("vrPremioEstimado").toString(), getInt("qtdCotaDisponivel"), getInt("qtdCotaTotal"), getInt("qtdApostas"),
            getInt("qtdNumeros"), get("vrUltimaCotaSemTarifa").toString(), get("vrTarifaServicoUltimaCota").toString(),
            get("vrUltimaCotaComTarifa").toString(), listOf(bet), contestType, getString("dataSorteio"),
            lastSharePriceWithFeeValue = BigDecimal("10.50"), regularSharePriceWithFee = BigDecimal("10.50"))
    }

    private fun JSONArray.ints() = (0 until length()).map { getInt(it) }
    private fun JSONObject.team() = SportsTeam(optInt("numero"), optString("nome"), optString("uf"),
        country = optString("pais"), countryCode = optString("siglaPais"), selectionIndicator = optBoolean("indicadorSelecao"))
}
