// IDs de recursos, dimensões da área de desenho e IDs de modalidades são fixtures fixas do contrato das artes.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSLotteryFilterSpecialBackground
import br.gov.caixa.loterias.core.designsystem.artwork.resolveLoteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.artwork.resolveLoteriasCaixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.artwork.resolveLoteriasCaixaDSMarketplaceTrapeze
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LoteriasCaixaDSArtworkSelectionJvmTest {
    private val resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    fun marketplaceAssetsRetainEachLotteryIdentityAcrossSelectedStates() {
        val fixtures = listOf(
            Triple("Mega-Sena", "megasena", "megasena"),
            Triple("Quina", "quina", "quina"),
            Triple("Lotofácil", "lotofacil", "lotofacil"),
            Triple("+Milionária", "milionaria", "milionaria"),
            Triple("Dupla Sena", "dupla_sena", "dupla_sena"),
            Triple("Lotomania", "lotomanial", "lotomania"),
            Triple("Super Sete", "super_sete", "super_sete"),
            Triple("Loteca", "loteca", "loteca"),
            Triple("Timemania", "timemania", "timemania"),
            Triple("Dia de Sorte", "dia_sorte", "dia_de_sorte")
        )
        fixtures.forEach { (name, trapeze, clover) ->
            assertEquals(
                "legacy_marketplace_trapeze_$trapeze",
                resources.getResourceEntryName(requireNotNull(resolveLoteriasCaixaDSMarketplaceTrapeze(0, name)))
            )
            listOf(false to "branco", true to "escuro").forEach { (selected, background) ->
                assertEquals(
                    "legacy_marketplace_filter_trevo_${clover}_fundo_$background",
                    resources.getResourceEntryName(requireNotNull(resolveLoteriasCaixaDSMarketplaceFilterClover(0, name, selected)))
                )
            }
        }
        assertNull(resolveLoteriasCaixaDSMarketplaceTrapeze(0, "Desconhecida"))
        assertNull(resolveLoteriasCaixaDSMarketplaceFilterClover(0, "Desconhecida", true))
        assertNull(resolveLoteriasCaixaDSMarketplaceTrapeze(2, campaign = LoteriasCaixaDSCampaign.OCTOBER_ROSE))
        assertNull(resolveLoteriasCaixaDSMarketplaceFilterClover(2, selected = false, campaign = LoteriasCaixaDSCampaign.SEPTEMBER_BLUE))
    }

    @Test
    fun octoberCampaignSelectsExactLegacyAssetForEveryArtworkRole() {
        val expected = listOf(
            "background_trapeze_megasena_outubro_rosa",
            "mega_sena_trapezio_outubro_rosa",
            "trevo_megasena_outubro_rosa_fundo_branco",
            "trevo_megasena_outubro_rosa_fundo_escuro",
            "trevo_megasena_outubro_rosa_ativado",
            "tarja_trevo_mega_outubro_rosa_combos",
            "trevo_mega_sena_detalhe_combo_outubro_rosa",
            "ic_tag_bolao_outubro_rosa",
            "ic_tag_bolao_rosa_outline",
            "mega_virada_background",
            "mega_virada_novo",
            "mega_virada_simples",
            "mega_virada_dupla",
            "mega_virada_quadrado",
            "cabecalho_mega_da_virada_detalhe_aposta",
            "rodape_mega_da_virada_inferior"
        )
        assertEquals(expected.size, LoteriasCaixaDSLotteryArtwork.entries.size)
        LoteriasCaixaDSLotteryArtwork.entries.zip(expected).forEach { (role, asset) ->
            assertEquals(
                role.name,
                "legacy_campaign_$asset",
                resources.getResourceEntryName(
                    requireNotNull(resolveLoteriasCaixaDSCampaignDrawable(2, "", role, LoteriasCaixaDSCampaign.OCTOBER_ROSE))
                )
            )
            assertNull(resolveLoteriasCaixaDSCampaignDrawable(2, "", role, LoteriasCaixaDSCampaign.SEPTEMBER_BLUE))
        }
    }

    @Test
    fun specialContestArtworkDistinguishesAnniversaryAndFathersDayFromDefaultEvents() {
        listOf(
            Triple("Mega-Sena", "Mega 30", "mega_30_quadrado"),
            Triple("Mega-Sena", "Virada", "mega_virada_quadrado"),
            Triple("Quina", "São João", "quina_sao_joao_quadrado"),
            Triple("Lotofácil", "Independência", "lotofacil_independencia_quadrado"),
            Triple("Dupla Sena", "Páscoa", "dupla_pascoa_quadrado"),
            Triple("Loteca", "Dia dos Pais", "loteca_pais_quadrado_alta"),
            Triple("Loteca", "PAÍS", "loteca_pais_quadrado_alta"),
            Triple("Loteca", "Copa", "loteca_copa_quadrado")
        ).forEach { (name, contest, expected) ->
            assertEquals(
                "legacy_campaign_$expected",
                resources.getResourceEntryName(requireNotNull(loteriasCaixaDSLotteryFilterSpecialBackground(0, name, contest)))
            )
        }
        assertNull(loteriasCaixaDSLotteryFilterSpecialBackground(0, "Timemania", "Normal"))
    }
}
