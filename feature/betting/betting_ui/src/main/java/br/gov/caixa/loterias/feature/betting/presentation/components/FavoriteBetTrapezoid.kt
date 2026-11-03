package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun FavoriteTrapezoid(id: Int, modifier: Modifier) {
    val resource = loteriasCaixaDSCampaignDrawable(id, role = LoteriasCaixaDSLotteryArtwork.TRAPEZE) ?: when (id) {
        QUINA_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_quina_mkp
        SUPER_SEVEN_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_super_sete_mkp
        LOTOFACIL_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_lotofacil_mkp
        MAIS_MILIONARIA_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_milionaria_mkp
        DIA_DE_SORTE_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_dia_sorte_mkp
        LOTOMANIA_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_lotomanial_mkp
        DUPLA_SENA_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_dupla_sena_mkp
        LOTECA_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_loteca_mkp
        TIMEMANIA_MODALITY_ID -> DeliveryR.drawable.legacy_favorite_trapeze_timemania_mkp
        else -> DeliveryR.drawable.legacy_favorite_trapeze_megasena_mkp
    }
    val context = LocalContext.current
    val resources = LocalResources.current
    val configuration = LocalConfiguration.current
    val drawable =
        remember(resource, resources, configuration, context.theme) {
            requireNotNull(androidx.core.content.res.ResourcesCompat.getDrawable(resources, resource, context.theme))
        }
    Canvas(modifier.clipToBounds()) {
        drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
        drawIntoCanvas { drawable.draw(it.nativeCanvas) }
    }
}

private const val QUINA_MODALITY_ID = 3

private const val SUPER_SEVEN_MODALITY_ID = 7

private const val LOTOFACIL_MODALITY_ID = 8

private const val MAIS_MILIONARIA_MODALITY_ID = 9

private const val DIA_DE_SORTE_MODALITY_ID = 11

private const val LOTOMANIA_MODALITY_ID = 16

private const val DUPLA_SENA_MODALITY_ID = 18

private const val LOTECA_MODALITY_ID = 19

private const val TIMEMANIA_MODALITY_ID = 20
