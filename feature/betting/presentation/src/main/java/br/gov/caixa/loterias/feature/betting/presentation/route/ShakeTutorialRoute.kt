package br.gov.caixa.loterias.feature.betting.presentation.route

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSSemiboldFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder

@Composable
fun ShakeTutorialRoute(onComplete: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var page by rememberSaveable { mutableStateOf(0) }
    val titles =
        listOf(
            uiStrings.getString(LocalizedR.string.ui_conheca_a_aposta_shake_e9534b),
            uiStrings.getString(LocalizedR.string.ui_abra_o_volante_de_apostas_f199e2),
            uiStrings.getString(LocalizedR.string.ui_agite_o_celular_para_completar_fc362b),
            uiStrings.getString(LocalizedR.string.ui_aposta_completada_automaticamente_32ab90),
            uiStrings.getString(LocalizedR.string.ui_ative_ou_desative_no_menu_lateral_9558e3),
            uiStrings.getString(LocalizedR.string.ui_vamos_testar_364c25)
        )
    val descriptions =
        listOf(
            uiStrings.getString(LocalizedR.string.ui_nova_funcionalidade_aposta_shake_com_2bdadc),
            uiStrings.getString(LocalizedR.string.ui_volante_da_mega_sena_aberto_ainda_se_19046e),
            uiStrings.getString(LocalizedR.string.ui_ilustracao_de_um_celular_sendo_agita_56d513),
            uiStrings.getString(LocalizedR.string.ui_volante_da_mega_sena_com_seis_numero_0d92f4),
            uiStrings.getString(LocalizedR.string.ui_menu_lateral_com_a_opcao_aposta_shak_a3fa2a),
            uiStrings.getString(LocalizedR.string.ui_agora_e_so_ativar_a_funcionalidade_a_df881f)
        )
    val images =
        listOf(
            R.raw.aposta_shake_apresentacao,
            R.raw.aposta_shake_abrir_volante,
            R.raw.aposta_shake_agitar,
            R.raw.aposta_shake_preencher,
            R.raw.aposta_shake_menu,
            R.raw.aposta_shake_final
        )
    val context = LocalContext.current
    val loader = remember(context) {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= 28) add(AnimatedImageDecoder.Factory()) else add(GifDecoder.Factory())
        }.build()
    }
    DisposableEffect(loader) { onDispose { loader.shutdown() } }
    fun previous() {
        if (page > 0) page--
    }
    fun next() {
        if (page == 5) onComplete() else page++
    }
    BackHandler { if (page > 0) previous() else onComplete() }
    Column(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(CaixaDSColorTokens.White, CaixaDSColorTokens.ShakeTutorialBottom)))
            .statusBarsPadding().navigationBarsPadding().padding(top = 12.dp)
    ) {
        val title = buildAnnotatedString {
            val highlights = listOf(
                uiStrings.getString(LocalizedR.string.ui_aposta_shake_b51943),
                "Abra o volante",
                "Agite o celular",
                uiStrings.getString(LocalizedR.string.ui_aposta_completada_cc22e0),
                uiStrings.getString(LocalizedR.string.ui_menu_lateral_d42910),
                ""
            )
            append(titles[page])
            val text = highlights[page]
            val start = titles[page].indexOf(text)
            if (text.isNotEmpty() &&
                start >= 0
            ) {
                addStyle(
                    SpanStyle(color = CaixaDSColorTokens.CaixaBlue, fontWeight = FontWeight.Bold),
                    start,
                    start + text.length
                )
            }
        }
        Box(Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
            Text(
                title,
                (Modifier.fillMaxWidth().semantics { heading() }).uiId("bet.0212"),
                fontFamily = CaixaDSRegularFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = CaixaDSColorTokens.SocialGraphite,
                textAlign = TextAlign.Center
            )
        }
        Box(
            Modifier.fillMaxWidth().weight(1f).uiId("bet.0213").pointerInput(page) {
                var drag = 0f
                detectHorizontalDragGestures(onDragStart = { drag = 0f }, onDragEnd = {
                    if (drag <
                        -48.dp.toPx()
                    ) {
                        next()
                    } else if (drag > 48.dp.toPx()) {
                        previous()
                    }
                }) { _, amount -> drag += amount }
            }.uiId("bet.0214").pointerInput(page) {
                detectTapGestures {
                    val arrowHitWidth = 48.dp.toPx()
                    if (it.x <= arrowHitWidth || it.x >= size.width - arrowHitWidth) return@detectTapGestures
                    if (it.x >= size.width / 2f) next() else previous()
                }
            }
        ) {
            // Cada página possui seu próprio painter para que a troca de GIFs não reinicie uma requisição durante a composição.
            val painter = key(images[page]) {
                rememberAsyncImagePainter(model = images[page], imageLoader = loader)
            }
            Canvas(
                Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 8.dp).semantics {
                    contentDescription = descriptions[page]
                }
            ) {
                val sourceSize = painter.intrinsicSize
                if (sourceSize.width.isFinite() && sourceSize.height.isFinite() &&
                    sourceSize.width > 0f && sourceSize.height > 0f
                ) {
                    val frame = ShakeTutorialFrameTransform.fromSourceSize(sourceSize)
                    val fit = minOf(size.width / frame.outputWidth, size.height / frame.outputHeight)
                    val frameWidth = frame.outputWidth * fit
                    val frameHeight = frame.outputHeight * fit
                    val frameLeft = (size.width - frameWidth) / 2f
                    val frameTop = (size.height - frameHeight) / 2f
                    val imageScale = fit * frame.bitmapScale
                    clipRect(frameLeft, frameTop, frameLeft + frameWidth, frameTop + frameHeight) {
                        withTransform({
                            translate(
                                left = frameLeft + frame.destinationLeft * fit - frame.sourceLeft * imageScale,
                                top = frameTop + frame.destinationTop * fit - frame.sourceTop * imageScale
                            )
                            scale(imageScale, imageScale, pivot = Offset.Zero)
                        }) {
                            with(painter) { draw(sourceSize) }
                        }
                    }
                }
            }
            if (page > 0) {
                ShakeTutorialArrow(
                    onClick = { previous() },
                    modifier = (Modifier.align(Alignment.CenterStart)).uiId("bet.0215"),
                    icon = R.drawable.shake_tutorial_chevron_left,
                    description = uiStrings.getString(LocalizedR.string.ui_etapa_anterior_14ca7e)
                )
            }
            ShakeTutorialArrow(
                onClick = { next() },
                modifier = (Modifier.align(Alignment.CenterEnd)).uiId("bet.0216"),
                icon = R.drawable.shake_tutorial_chevron_right,
                description = if (page == 5) {
                    uiStrings.getString(
                        LocalizedR.string.ui_fechar_tutorial_e8e309
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_proxima_etapa_e801e4)
                }
            )
        }
        Row(
            Modifier.fillMaxWidth().height(24.dp).semantics {
                contentDescription = uiStrings.getString(LocalizedR.string.format_step_description, page + 1, 6)
            },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                Box(
                    Modifier.padding(horizontal = 3.dp).width(
                        if (page ==
                            index
                        ) {
                            24.dp
                        } else {
                            7.dp
                        }
                    ).height(7.dp).background(
                        if (page ==
                            index
                        ) {
                            CaixaDSColorTokens.CaixaBlue
                        } else {
                            CaixaDSColorTokens.White
                        },
                        RoundedCornerShape(8.dp)
                    ).uiId("bet.0217").clickable { page = index }
                )
            }
        }
        TextButton(onClick = onComplete, (Modifier.fillMaxWidth().padding(bottom = 12.dp).height(56.dp)).uiId("bet.0218")) {
            Text(
                if (page ==
                    5
                ) {
                    uiStrings.getString(LocalizedR.string.ui_fechar_tutorial_e8e309)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_pular_tutorial_4ba00f)
                },
                fontFamily = CaixaDSSemiboldFontFamily,
                fontSize = 16.sp,
                color = CaixaDSColorTokens.SocialGraphite,
                fontWeight = FontWeight.Normal,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.uiId("bet.0219")
            )
        }
    }
}

/** Geometria aplicada a cada quadro GIF decodificado, seguindo a transformação de bitmap do Glide legado. */
internal data class ShakeTutorialFrameTransform(
    val outputWidth: Float,
    val outputHeight: Float,
    val sourceLeft: Float,
    val sourceTop: Float,
    val sourceWidth: Float,
    val sourceHeight: Float,
    val bitmapScale: Float,
    val destinationLeft: Float,
    val destinationTop: Float,
    val destinationWidth: Float,
    val destinationHeight: Float,
) {
    companion object {
        fun fromSourceSize(source: Size): ShakeTutorialFrameTransform {
            val outputWidth = 306f
            val outputHeight = 580f
            val hasLegacyMargins = source.width == 412f && source.height == 804f
            val sourceLeft = if (hasLegacyMargins) 53f else 0f
            val sourceTop = if (hasLegacyMargins) 119f else 0f
            val sourceWidth = if (hasLegacyMargins) 306f else source.width
            val sourceHeight = if (hasLegacyMargins) 580f else source.height
            val bitmapScale = minOf(outputWidth / sourceWidth, outputHeight / sourceHeight)
            val destinationWidth = kotlin.math.round(sourceWidth * bitmapScale)
            val destinationHeight = kotlin.math.round(sourceHeight * bitmapScale)
            return ShakeTutorialFrameTransform(
                outputWidth = outputWidth,
                outputHeight = outputHeight,
                sourceLeft = sourceLeft,
                sourceTop = sourceTop,
                sourceWidth = sourceWidth,
                sourceHeight = sourceHeight,
                bitmapScale = bitmapScale,
                destinationLeft = (outputWidth - destinationWidth) / 2f,
                destinationTop = (outputHeight - destinationHeight) / 2f,
                destinationWidth = destinationWidth,
                destinationHeight = destinationHeight
            )
        }
    }
}

@Composable
private fun ShakeTutorialArrow(onClick: () -> Unit, modifier: Modifier, icon: Int, description: String) {
    Box(modifier.width(48.dp).height(64.dp).uiId("bet.0220").clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Box(
            Modifier.width(24.dp).height(48.dp).shadow(2.dp, RoundedCornerShape(12.dp))
                .background(CaixaDSColorTokens.ShakeArrowBackground, RoundedCornerShape(12.dp))
                .border(1.dp, CaixaDSColorTokens.SocialCanvas, RoundedCornerShape(12.dp))
        )
        Image(painterResource(icon), description, Modifier.width(8.dp).height(14.dp))
    }
}
