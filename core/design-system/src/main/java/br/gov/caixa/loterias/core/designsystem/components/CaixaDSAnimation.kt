package br.gov.caixa.loterias.core.designsystem.components

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import androidx.core.graphics.createBitmap
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Substitui apenas a arte; a animação mantém suas transformações e opacidade originais. */
data class CaixaDSAnimationImage(
    val assetId: String,
    val layer: String,
    @param:DrawableRes val resource: Int,
    val sourceRegion: Rect = Rect(0f, 0f, 1f, 1f),
)

private data class AnimationImageKey(
    val resources: Resources,
    val configuration: Configuration,
    val composition: LottieComposition?,
    val images: List<CaixaDSAnimationImage>,
)

private val LocalAnimationImages = staticCompositionLocalOf<MutableMap<AnimationImageKey, List<Pair<String, Bitmap>>>?> { null }

/** Mantém a arte preparada entre telas da mesma animação; o cache termina com o fluxo que o hospeda. */
@Composable
fun CaixaDSAnimationImageHost(content: @Composable () -> Unit) {
    val images = remember { mutableMapOf<AnimationImageKey, List<Pair<String, Bitmap>>>() }
    CompositionLocalProvider(LocalAnimationImages provides images, content = content)
}

@Composable
fun CaixaDSAnimation(
    composition: LottieComposition?,
    progress: () -> Float,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    images: List<CaixaDSAnimationImage> = emptyList(),
) {
    val resources = LocalResources.current
    val configuration = LocalConfiguration.current
    val imageCache = LocalAnimationImages.current
    val imageKey = AnimationImageKey(resources, Configuration(configuration), composition, images)
    val cached = imageCache?.get(imageKey)?.let { composition to it }
    val prepared by produceState<Pair<LottieComposition?, List<Pair<String, Bitmap>>>?>(
        cached,
        imageKey
    ) {
        value = cached ?: (
            composition to prepareAnimationImages(composition, images) {
                BitmapFactory.decodeResource(resources, it)
            }
            ).also { result ->
            imageCache?.put(imageKey, result.second)
        }
    }
    // Não exibe momentaneamente a arte incorporada enquanto as substituições são preparadas.
    val ready = prepared?.takeIf { it.first === composition }
    val replacements = ready?.second.orEmpty()
    val properties = replacements.map { (layer, bitmap) ->
        rememberLottieDynamicProperty(LottieProperty.IMAGE, bitmap, layer)
    }
    val dynamicProperties = rememberLottieDynamicProperties(*properties.toTypedArray())
    LottieAnimation(
        composition = if (images.isEmpty() || ready != null) composition else null,
        progress = progress,
        modifier = modifier,
        contentScale = contentScale,
        dynamicProperties = dynamicProperties
    )
}

internal suspend fun prepareAnimationImages(
    composition: LottieComposition?,
    images: List<CaixaDSAnimationImage>,
    loadBitmap: (Int) -> Bitmap,
): List<Pair<String, Bitmap>> = withContext(Dispatchers.Default) {
    val decoded = mutableMapOf<Int, Bitmap>()
    images.mapNotNull { image ->
        val asset = composition?.images?.get(image.assetId) ?: return@mapNotNull null
        val source = decoded.getOrPut(image.resource) { loadBitmap(image.resource) }
        image.layer to animationImage(source, image.sourceRegion, asset.width, asset.height)
    }
}

/** Ajusta a arte não transparente à área original do recurso sem distorção. */
internal fun animationImage(source: Bitmap, region: Rect, width: Int, height: Int): Bitmap {
    require(region.left >= 0f && region.top >= 0f && region.right <= 1f && region.bottom <= 1f)
    require(region.width > 0f && region.height > 0f && width > 0 && height > 0)
    val sourceWidth = source.width
    val sourceHeight = source.height
    val left = (region.left * sourceWidth).toInt()
    val top = (region.top * sourceHeight).toInt()
    val right = (region.right * sourceWidth).toInt()
    val bottom = (region.bottom * sourceHeight).toInt()
    val regionWidth = right - left
    val pixels = IntArray(regionWidth * (bottom - top))
    source.getPixels(pixels, 0, regionWidth, left, top, regionWidth, bottom - top)
    var minX = right
    var minY = bottom
    var maxX = left - 1
    var maxY = top - 1
    for (y in top until bottom) {
        for (x in left until right) {
            if ((pixels[(y - top) * regionWidth + x - left] ushr ALPHA_SHIFT) != 0) {
                minX = minOf(minX, x)
                minY = minOf(minY, y)
                maxX = maxOf(maxX, x)
                maxY = maxOf(maxY, y)
            }
        }
    }
    val result = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    if (maxX < minX || maxY < minY) return result
    val scale = minOf(width.toFloat() / (maxX - minX + 1), height.toFloat() / (maxY - minY + 1))
    val targetWidth = (maxX - minX + 1) * scale
    val targetHeight = (maxY - minY + 1) * scale
    Canvas(result).drawBitmap(
        source,
        android.graphics.Rect(minX, minY, maxX + 1, maxY + 1),
        RectF(
            (width - targetWidth) / 2f,
            (height - targetHeight) / 2f,
            (width + targetWidth) / 2f,
            (height + targetHeight) / 2f
        ),
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    )
    return result
}

private const val ALPHA_SHIFT = 24
