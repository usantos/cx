package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.feature.tickets.R

/** Renderiza o LayerDrawable Android original, incluindo os recuos negativos exatos e a rotação de 20 graus. */
@Composable
internal fun rememberResultDrawablePainter(resource: Int): Painter {
    val context = LocalContext.current
    return remember(context, resource) {
        val drawable = requireNotNull(ContextCompat.getDrawable(context, resource))
        object : Painter() {
            override val intrinsicSize: Size = Size.Unspecified
            override fun DrawScope.onDraw() {
                drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                drawIntoCanvas { drawable.draw(it.nativeCanvas) }
            }
        }
    }
}

@Composable
internal fun resultTrapezoid(id: Int): Int =
    loteriasCaixaDSCampaignDrawable(id, role = LoteriasCaixaDSLotteryArtwork.TRAPEZE) ?: when (id) {
        QUINA_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_quina_mkp
        SUPER_SEVEN_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_super_sete_mkp
        LOTOFACIL_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_lotofacil_mkp
        MILIONARIA_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_milionaria_mkp
        LUCKY_DAY_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_dia_sorte_mkp
        LOTOMANIA_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_lotomanial_mkp
        DUPLA_SENA_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_dupla_sena_mkp
        LOTECA_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_loteca_mkp
        TIMEMANIA_MODALITY_ID -> R.drawable.legacy_result_background_trapeze_timemania_mkp
        else -> R.drawable.legacy_result_background_trapeze_megasena_mkp
    }

private const val QUINA_MODALITY_ID = 3
private const val SUPER_SEVEN_MODALITY_ID = 7
private const val LOTOFACIL_MODALITY_ID = 8
private const val MILIONARIA_MODALITY_ID = 9
private const val LUCKY_DAY_MODALITY_ID = 11
private const val LOTOMANIA_MODALITY_ID = 16
private const val DUPLA_SENA_MODALITY_ID = 18
private const val LOTECA_MODALITY_ID = 19
private const val TIMEMANIA_MODALITY_ID = 20
