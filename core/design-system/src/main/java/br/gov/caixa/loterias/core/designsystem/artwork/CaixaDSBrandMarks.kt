package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.foundation.Canvas
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

@Composable
fun CaixaDSAttentionIcon(modifier: Modifier = Modifier) {
    val color = CaixaDS.colors.primaryButton
    Canvas(modifier) {
        // ImageView usava FIT_CENTER: o viewport 75x111 não era esticado para os 72x89dp.
        val scale = minOf(size.width / 75f, size.height / 111f)
        val offsetX = (size.width - 75f * scale) / 2f
        val offsetY = (size.height - 111f * scale) / 2f
        fun point(x: Float, y: Float) = Offset(offsetX + x * scale, offsetY + y * scale)
        drawOval(
            color = color,
            topLeft = point(3.5f, 1.06f),
            size = Size(64.97f * scale, 67.44f * scale),
            style = Stroke(width = scale),
        )
        val triangle = Path().apply {
            val top = point(35.95f, 15.89f)
            val right = point(55.8f, 49f)
            val left = point(16.1f, 49f)
            moveTo(top.x, top.y)
            lineTo(right.x, right.y)
            lineTo(left.x, left.y)
            close()
        }
        drawPath(triangle, color = color, style = Stroke(width = 2f * scale, cap = StrokeCap.Round))
        drawLine(
            color = color,
            start = point(35.5f, 24.9f),
            end = point(35.5f, 39.33f),
            strokeWidth = 1.97f * scale,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = color,
            radius = 1.35f * scale,
            center = point(35.5f, 41.94f),
        )
    }
}

@Composable
fun CaixaDSSelfAssessmentIcon(modifier: Modifier = Modifier) {
    val blue = CaixaDS.colors.primaryButton
    val paleBlue = Color(0xFFCEE8FA)
    val paths = remember {
        listOf(
            PathParser().parsePathString("M65.467,34.781C65.467,53.421 50.907,68.5 32.984,68.5C15.061,68.5 0.5,53.421 0.5,34.781C0.5,16.141 15.061,1.062 32.984,1.062C50.907,1.062 65.467,16.141 65.467,34.781Z").toPath(),
            PathParser().parsePathString("M23.783,57.396C22.872,57.396 21.54,56.933 18.583,53.499C17.08,51.754 15.848,50.037 15.796,49.965C15.761,49.916 15.73,49.865 15.701,49.812C15.139,48.762 14.842,47.572 14.842,46.37V37.638C14.842,36.876 15.437,36.259 16.171,36.259H26.92C28.459,36.259 29.912,36.876 31.009,37.996L36.513,43.615C37.289,44.42 37.712,45.481 37.712,46.609C37.712,47.737 37.289,48.797 36.52,49.595C34.924,51.25 32.342,51.25 30.755,49.604L25.257,43.9C24.711,43.895 23.824,43.891 23.115,43.888L22.113,43.883C21.379,43.879 20.787,43.258 20.791,42.497C20.795,41.738 21.389,41.125 22.12,41.125L25.583,41.145C25.97,41.15 26.37,41.154 26.756,41.555L32.635,47.654C33.185,48.225 34.082,48.225 34.632,47.654C34.908,47.368 35.055,47 35.055,46.609C35.055,46.217 34.908,45.849 34.641,45.572L29.145,39.961C28.548,39.352 27.757,39.016 26.92,39.016H17.499V46.37C17.499,47.075 17.667,47.773 17.984,48.396C18.226,48.729 19.242,50.112 20.435,51.511C22.795,54.278 23.696,54.605 23.816,54.639L38.184,54.636C39.163,54.636 40.305,54.104 41.39,53.559C42.306,53.099 49.107,48.313 53.179,45.404C54.006,44.884 54.252,43.857 53.795,43.069C53.573,42.687 53.222,42.418 52.805,42.311C52.396,42.206 51.972,42.268 51.609,42.488L41.683,49.317C41.071,49.738 40.247,49.564 39.841,48.93C39.436,48.295 39.603,47.44 40.214,47.019L50.217,40.139C52.232,38.88 54.858,39.556 56.071,41.647C57.277,43.724 56.641,46.428 54.656,47.697C53.425,48.576 43.935,55.344 42.548,56.041C41.185,56.725 39.725,57.393 38.184,57.393Z").toPath(),
            PathParser().parsePathString("M18.389,35.944H10.597V54.159H18.389Z").toPath(),
            PathParser().parsePathString("M18.389,55.537H10.597C9.863,55.537 9.268,54.92 9.268,54.158V35.944C9.268,35.183 9.863,34.566 10.597,34.566H18.389C19.122,34.566 19.717,35.183 19.717,35.944V54.158C19.717,54.919 19.122,55.537 18.389,55.537ZM11.925,52.779H17.06V37.323H11.925Z").toPath(),
            PathParser().parsePathString("M34.553,33.256C29.084,33.256 24.634,28.64 24.634,22.966C24.634,17.292 29.084,12.676 34.553,12.676C40.022,12.676 44.471,17.292 44.471,22.966C44.471,28.64 40.022,33.256 34.553,33.256ZM34.553,15.434C30.549,15.434 27.292,18.813 27.292,22.966C27.292,27.12 30.549,30.499 34.553,30.499C38.556,30.499 41.813,27.12 41.813,22.966C41.813,18.813 38.556,15.434 34.553,15.434Z").toPath(),
            PathParser().parsePathString("M41.58,38.747C46.324,38.747 50.17,34.757 50.17,29.836C50.17,24.914 46.324,20.924 41.58,20.924C36.836,20.924 32.991,24.914 32.991,29.836C32.991,34.757 36.836,38.747 41.58,38.747Z").toPath(),
            PathParser().parsePathString("M41.581,40.125C36.112,40.125 31.663,35.509 31.663,29.835C31.663,24.162 36.112,19.546 41.581,19.546C47.05,19.546 51.499,24.162 51.499,29.836C51.499,35.509 47.05,40.125 41.581,40.125ZM41.581,22.303C37.577,22.303 34.32,25.682 34.32,29.835C34.32,33.989 37.577,37.368 41.581,37.368C45.584,37.368 48.841,33.989 48.841,29.835C48.841,25.682 45.584,22.303 41.581,22.303Z").toPath(),
            PathParser().parsePathString("M41.252,34.282C39.815,34.232 38.642,33.462 38.642,32.667C38.642,32.245 39.001,31.624 39.456,31.624C39.959,31.624 40.366,32.357 41.252,32.518V30.506C40.15,30.072 38.857,29.538 38.857,27.948C38.857,26.371 39.983,25.613 41.252,25.427V25.079C41.252,24.905 41.443,24.744 41.707,24.744C41.934,24.744 42.162,24.905 42.162,25.079V25.389C42.904,25.414 44.305,25.613 44.305,26.47C44.305,26.805 44.089,27.488 43.562,27.488C43.167,27.488 42.939,27.091 42.161,27.029V28.842C43.251,29.264 44.52,29.848 44.52,31.525C44.52,33.065 43.562,33.996 42.161,34.232V34.593C42.161,34.766 41.934,34.928 41.707,34.928C41.443,34.928 41.252,34.767 41.252,34.593V34.282ZM41.371,28.544V27.066C40.833,27.178 40.605,27.463 40.605,27.761C40.605,28.122 40.917,28.345 41.371,28.544ZM42.042,30.841V32.506C42.449,32.406 42.772,32.17 42.772,31.723C42.772,31.313 42.473,31.053 42.042,30.841Z").toPath(),
        )
    }
    Canvas(modifier) {
        val factor = minOf(size.width / 66f, size.height / 69f)
        withTransform({ scale(factor, factor, Offset.Zero) }) {
            drawPath(paths[0], color = blue, style = Stroke(width = 1f))
            drawPath(paths[1], color = blue)
            drawPath(paths[2], color = paleBlue)
            drawPath(paths[3], color = blue)
            drawPath(paths[4], color = blue)
            drawPath(paths[5], color = paleBlue)
            drawPath(paths[6], color = blue)
            drawPath(paths[7], color = blue)
        }
    }
}

@Composable
fun CaixaDSLotteryMark(
    modifier: Modifier = Modifier,
    lightColor: Color = Color.White,
    accentColor: Color = CaixaDS.colors.accent,
) {
    Canvas(modifier) {
        val side = size.minDimension
        val offset = Offset((size.width - side) / 2f, (size.height - side) / 2f)
        val leaf = Path().apply {
            moveTo(offset.x + side * .49f, offset.y + side * .49f)
            lineTo(offset.x + side * .49f, offset.y + side * .27f)
            cubicTo(
                offset.x + side * .49f,
                offset.y + side * .11f,
                offset.x + side * .39f,
                offset.y,
                offset.x + side * .25f,
                offset.y,
            )
            cubicTo(
                offset.x + side * .11f,
                offset.y,
                offset.x,
                offset.y + side * .11f,
                offset.x,
                offset.y + side * .25f,
            )
            cubicTo(
                offset.x,
                offset.y + side * .39f,
                offset.x + side * .11f,
                offset.y + side * .49f,
                offset.x + side * .27f,
                offset.y + side * .49f,
            )
            close()
        }
        val pivot = Offset(offset.x + side / 2f, offset.y + side / 2f)
        drawPath(leaf, lightColor)
        withTransform({ rotate(90f, pivot) }) { drawPath(leaf, accentColor) }
        withTransform({ rotate(180f, pivot) }) { drawPath(leaf, lightColor) }
        withTransform({ rotate(270f, pivot) }) { drawPath(leaf, accentColor) }
    }
}

@Composable
fun CaixaDSPixMark(
    modifier: Modifier = Modifier,
    color: Color = CaixaDS.colors.pix,
) {
    Canvas(modifier) {
        val inset = size.minDimension * .12f
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val half = (size.minDimension - inset * 2f) / 2f
        val path = Path().apply {
            moveTo(centerX, inset)
            lineTo(centerX + half, centerY)
            lineTo(centerX, size.height - inset)
            lineTo(centerX - half, centerY)
            close()
        }
        drawPath(path, color, style = Stroke(width = size.minDimension * .105f))
        drawLine(
            color = color,
            start = Offset(centerX - half * .40f, centerY),
            end = Offset(centerX + half * .40f, centerY),
            strokeWidth = size.minDimension * .105f,
        )
    }
}
