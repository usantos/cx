package br.gov.caixa.loterias.feature.betting

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySemiboldFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder

@Composable
fun ShakeTutorialRoute(onComplete: () -> Unit) {
    var page by rememberSaveable { mutableStateOf(0) }
    val titles = listOf("Conheça a Aposta Shake!", "Abra o volante de apostas", "Agite o celular\npara completar", "Aposta completada\nautomaticamente!", "Ative ou desative\nno menu lateral", "Vamos testar?")
    val descriptions = listOf("Nova funcionalidade: Aposta Shake. Complete apostas automaticamente chacoalhando seu celular!", "Volante da Mega-Sena aberto, ainda sem números selecionados.", "Ilustração de um celular sendo agitado para completar a aposta.", "Volante da Mega-Sena com seis números preenchidos automaticamente.", "Menu lateral com a opção Aposta Shake e o switch para ativar ou desativar.", "Agora é só ativar a funcionalidade, abrir um volante e agitar o celular para completar sua aposta automaticamente!")
    val images = listOf(R.raw.aposta_shake_apresentacao, R.raw.aposta_shake_abrir_volante, R.raw.aposta_shake_agitar, R.raw.aposta_shake_preencher, R.raw.aposta_shake_menu, R.raw.aposta_shake_final)
    val context = LocalContext.current
    val loader = remember(context) { ImageLoader.Builder(context).components {
        if (Build.VERSION.SDK_INT >= 28) add(AnimatedImageDecoder.Factory()) else add(GifDecoder.Factory())
    }.build() }
    DisposableEffect(loader) { onDispose { loader.shutdown() } }
    fun previous() { if (page > 0) page-- }
    fun next() { if (page == 5) onComplete() else page++ }
    BackHandler { if (page > 0) previous() else onComplete() }
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(CaixaDSColorTokens.White, CaixaDSColorTokens.LegacyShakeTutorialBottom)))
        .statusBarsPadding().navigationBarsPadding().padding(top = 12.dp)) {
        val title = buildAnnotatedString {
            val highlights = listOf("Aposta Shake!", "Abra o volante", "Agite o celular", "Aposta completada", "menu lateral", "")
            append(titles[page])
            val text = highlights[page]
            val start = titles[page].indexOf(text)
            if (text.isNotEmpty() && start >= 0) addStyle(SpanStyle(color = CaixaDSColorTokens.LegacyCaixaBlue, fontWeight = FontWeight.Bold), start, start + text.length)
        }
        Box(Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
            Text(title, Modifier.fillMaxWidth().semantics { heading() },
                fontFamily = CaixaDSLegacyRegularFontFamily, fontWeight = FontWeight.Normal, fontSize = 20.sp, color = CaixaDSColorTokens.LegacySocialGraphite, textAlign = TextAlign.Center)
        }
        Box(Modifier.fillMaxWidth().weight(1f).pointerInput(page) {
            var drag = 0f
            detectHorizontalDragGestures(onDragStart = { drag = 0f }, onDragEnd = { if (drag < -48.dp.toPx()) next() else if (drag > 48.dp.toPx()) previous() }) { _, amount -> drag += amount }
        }.pointerInput(page) {
            detectTapGestures {
                val arrowHitWidth = 48.dp.toPx()
                if (it.x <= arrowHitWidth || it.x >= size.width - arrowHitWidth) return@detectTapGestures
                if (it.x >= size.width / 2f) next() else previous()
            }
        }) {
            val painter = rememberAsyncImagePainter(model = images[page], imageLoader = loader)
            Canvas(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 8.dp).semantics {
                contentDescription = descriptions[page]
            }) {
                val sourceSize = painter.intrinsicSize
                if (sourceSize.width.isFinite() && sourceSize.height.isFinite() &&
                    sourceSize.width > 0f && sourceSize.height > 0f
                ) {
                    val frame = ShakeTutorialLegacyFrameTransform.fromSourceSize(sourceSize)
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
                                top = frameTop + frame.destinationTop * fit - frame.sourceTop * imageScale,
                            )
                            scale(imageScale, imageScale, pivot = Offset.Zero)
                        }) {
                            with(painter) { draw(sourceSize) }
                        }
                    }
                }
            }
            if (page > 0) ShakeTutorialArrow(
                onClick = { previous() }, modifier = Modifier.align(Alignment.CenterStart),
                icon = R.drawable.shake_tutorial_chevron_left, description = "Etapa anterior",
            )
            ShakeTutorialArrow(
                onClick = { next() }, modifier = Modifier.align(Alignment.CenterEnd),
                icon = R.drawable.shake_tutorial_chevron_right,
                description = if (page == 5) "Fechar tutorial" else "Próxima etapa",
            )
        }
        Row(Modifier.fillMaxWidth().height(24.dp).semantics { contentDescription = "Etapa ${page + 1} de 6" }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            repeat(6) { index -> Box(Modifier.padding(horizontal = 3.dp).width(if (page == index) 24.dp else 7.dp).height(7.dp).background(if (page == index) CaixaDSColorTokens.LegacyCaixaBlue else CaixaDSColorTokens.White, RoundedCornerShape(8.dp)).clickable { page = index }) }
        }
        TextButton(onClick = onComplete, Modifier.fillMaxWidth().padding(bottom = 12.dp).height(56.dp)) {
            Text(if (page == 5) "Fechar tutorial" else "Pular tutorial", fontFamily = CaixaDSLegacySemiboldFontFamily, fontSize = 16.sp, color = CaixaDSColorTokens.LegacySocialGraphite, fontWeight = FontWeight.Normal, textDecoration = TextDecoration.Underline)
        }
    }
}

/** Geometry applied to every decoded GIF frame, matching the legacy Glide bitmap transform. */
internal data class ShakeTutorialLegacyFrameTransform(
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
        fun fromSourceSize(source: Size): ShakeTutorialLegacyFrameTransform {
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
            return ShakeTutorialLegacyFrameTransform(
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
                destinationHeight = destinationHeight,
            )
        }
    }
}

@Composable
private fun ShakeTutorialArrow(onClick: () -> Unit, modifier: Modifier, icon: Int, description: String) {
    Box(modifier.width(48.dp).height(64.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Box(Modifier.width(24.dp).height(48.dp).shadow(2.dp, RoundedCornerShape(12.dp))
            .background(CaixaDSColorTokens.LegacyShakeArrowBackground, RoundedCornerShape(12.dp))
            .border(1.dp, CaixaDSColorTokens.LegacySocialCanvas, RoundedCornerShape(12.dp)))
        Image(painterResource(icon), description, Modifier.width(8.dp).height(14.dp))
    }
}
