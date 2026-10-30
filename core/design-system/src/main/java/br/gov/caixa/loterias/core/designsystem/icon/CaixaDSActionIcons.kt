package br.gov.caixa.loterias.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

// Caminhos vetoriais, área de desenho e traços originais do drawable legado indicado.

/** `ic_lupa.xml`. */
val CaixaDSSearchIcon: ImageVector = ImageVector.Builder(
    name = "CaixaDSSearchIcon", defaultWidth = 18.dp, defaultHeight = 18.dp,
    viewportWidth = 18.0f, viewportHeight = 18.0f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M12.5,11H11.71L11.43,10.73C12.41,9.59 13,8.11 13,6.5C13,2.91 10.09,0 6.5,0C2.91,0 0,2.91 0,6.5C0,10.09 2.91,13 6.5,13C8.11,13 9.59,12.41 10.73,11.43L11,11.71V12.5L16,17.49L17.49,16L12.5,11ZM6.5,11C4.01,11 2,8.99 2,6.5C2,4.01 4.01,2 6.5,2C8.99,2 11,4.01 11,6.5C11,8.99 8.99,11 6.5,11Z").toNodes(),
        fill = SolidColor(Color(0xFF64747A)),
    )
}.build()

/** `ic_estrela.xml`. */
val CaixaDSPurchaseFavoriteIcon: ImageVector = ImageVector.Builder(
    name = "CaixaDSPurchaseFavoriteIcon", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 486.6f, viewportHeight = 486.6f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M470.3,183.3c-1.7,-5.1 -6,-8.8 -11.3,-9.6l-140.3,-20.4L255.9,26.2c-2.4,-4.8 -7.2,-7.8 -12.6,-7.8c-5.3,0 -10.2,3 -12.6,7.8L168,153.3L27.6,173.7c-5.3,0.8 -9.7,4.5 -11.3,9.6c-1.7,5.1 -0.3,10.7 3.6,14.4l101.5,99l-24,139.7c-0.9,5.3 1.3,10.6 5.6,13.7c4.3,3.1 10.1,3.6 14.8,1.1l125.5,-66l125.5,66c2.1,1.1 4.3,1.6 6.5,1.6c0,0 0,0 0,0c0,0 0,0 0,0c7.8,0 14,-6.3 14,-14c0,-1.2 -0.1,-2.3 -0.4,-3.4l-23.8,-138.7l101.5,-99C470.5,194 471.9,188.4 470.3,183.3zM340.3,281.7c-3.3,3.2 -4.8,7.9 -4,12.4l20.4,119l-106.9,-56.2c-4.1,-2.2 -9,-2.2 -13.1,0l-106.9,56.2l20.4,-119c0.8,-4.6 -0.7,-9.2 -4,-12.4l-86.5,-84.3l119.5,-17.4c4.6,-0.7 8.5,-3.5 10.6,-7.7l53.4,-108.3l53.4,108.3c2,4.1 6,7 10.6,7.7l119.5,17.4L340.3,281.7z").toNodes(),
        fill = SolidColor(Color(0xFF929EA0)),
        stroke = SolidColor(Color(0xFF929EA0)), strokeLineWidth = 6.0f,
    )
}.build()

/** `ic_carrinho2.xml`. */
val CaixaDSBetCartIcon: ImageVector = ImageVector.Builder(
    name = "CaixaDSBetCartIcon", defaultWidth = 27.dp, defaultHeight = 24.dp,
    viewportWidth = 27.0f, viewportHeight = 24.0f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M10.283,19.7C11.31,19.7 12.156,20.547 12.156,21.6C12.156,22.652 11.31,23.5 10.283,23.5C9.256,23.5 8.41,22.652 8.41,21.6C8.41,20.547 9.256,19.7 10.283,19.7ZM21.463,19.7C22.49,19.7 23.336,20.547 23.336,21.6C23.336,22.652 22.49,23.5 21.463,23.5C20.436,23.5 19.59,22.652 19.59,21.6C19.59,20.547 20.436,19.7 21.463,19.7ZM10.283,20.3C9.566,20.3 8.992,20.888 8.992,21.6C8.992,22.312 9.566,22.9 10.283,22.9C11.001,22.9 11.574,22.312 11.574,21.6C11.574,20.888 11.001,20.3 10.283,20.3ZM21.463,20.3C20.746,20.3 20.172,20.888 20.172,21.6C20.172,22.312 20.745,22.9 21.463,22.9C22.18,22.9 22.754,22.312 22.754,21.6C22.754,20.888 22.18,20.3 21.463,20.3ZM0.791,0.5H5.537C5.653,0.5 5.76,0.571 5.806,0.684L5.821,0.734L6.387,3.308L6.473,3.7H26.209C26.276,3.7 26.341,3.724 26.393,3.768L26.44,3.817C26.496,3.893 26.514,3.992 26.488,4.084L23.324,15.284C23.287,15.415 23.171,15.5 23.045,15.5H8.698C8.683,15.5 8.67,15.501 8.663,15.502H8.652C7.958,15.528 7.41,16.104 7.41,16.8C7.41,17.512 7.984,18.101 8.701,18.101H23.045C23.2,18.101 23.336,18.229 23.336,18.4C23.336,18.571 23.2,18.7 23.045,18.7H8.701C7.674,18.7 6.828,17.852 6.828,16.8C6.828,16.019 7.296,15.348 7.96,15.056L8.335,14.891L8.247,14.49C8.096,13.804 5.492,1.953 5.391,1.493L5.305,1.1H0.791C0.636,1.1 0.5,0.971 0.5,0.8C0.5,0.629 0.636,0.5 0.791,0.5ZM6.738,4.907L8.848,14.508L8.934,14.9H22.826L22.93,14.536L25.642,4.936L25.821,4.3H6.604L6.738,4.907Z").toNodes(),
        fill = SolidColor(Color(0xFFffffff)),
        stroke = SolidColor(Color(0xFFffffff)), strokeLineWidth = 0.999999f,
    )
}.build()

/** `icone_rapidao.xml`, incluindo o contorno arredondado do raio. */
val CaixaDSRapidIcon: ImageVector = ImageVector.Builder(
    name = "CaixaDSRapidIcon", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 101.3f, viewportHeight = 113f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M42.1,95.7l33,-48l-21.9,0.7l7.4,-31.2l-32.3,47.6l20.9,-0.6z").toNodes(),
        fill = SolidColor(Color(0x00000000)),
        stroke = SolidColor(Color(0xFFFEAC00)), strokeLineWidth = 4f,
        strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = PathParser().parsePathString("M33.4,25.3c0.8,-0.5 1,-1.6 0.5,-2.4L28,14.4c-0.3,-0.4 -0.7,-0.6 -1.1,-0.7s-0.9,0 -1.3,0.3c-0.8,0.5 -1,1.6 -0.5,2.4l5.8,8.6C31.5,25.7 32.6,25.9 33.4,25.3z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M4.2,46.2c-0.2,0.9 0.4,1.8 1.4,2l10.2,2c0.9,0.2 1.8,-0.4 2,-1.4c0.2,-0.9 -0.4,-1.8 -1.4,-2l-10.2,-2C5.2,44.7 4.3,45.3 4.2,46.2z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M23.1,79.3c0.8,-0.5 1,-1.6 0.5,-2.4c-0.3,-0.4 -0.7,-0.6 -1.1,-0.7c-0.4,-0.1 -0.9,0 -1.3,0.3l-8.6,5.8c-0.8,0.5 -1,1.6 -0.5,2.4s1.6,1 2.4,0.5L23.1,79.3z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M22.8,36.2c0.5,-0.8 0.3,-1.9 -0.5,-2.4l-8.8,-5.6c-0.2,-0.1 -0.4,-0.2 -0.6,-0.2c-0.7,-0.1 -1.4,0.2 -1.8,0.8c-0.5,0.8 -0.3,1.9 0.5,2.4l8.8,5.6C21.3,37.3 22.3,37 22.8,36.2z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M4.3,67c0.2,0.9 1.1,1.5 2.1,1.3l10.1,-2.3c0.9,-0.2 1.5,-1.1 1.3,-2.1c-0.2,-0.7 -0.7,-1.2 -1.4,-1.3c-0.2,0 -0.5,0 -0.7,0L5.6,65C4.7,65.2 4.1,66.1 4.3,67z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M26.3,96.7c-0.5,0.8 -0.3,1.9 0.5,2.4c0.8,0.5 1.9,0.3 2.4,-0.5l5.6,-8.7c0.5,-0.8 0.3,-1.9 -0.5,-2.4c-0.2,-0.1 -0.4,-0.2 -0.6,-0.2c-0.7,-0.1 -1.4,0.2 -1.8,0.8L26.3,96.7z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M70.3,24.9l5.8,-8.6c0.5,-0.8 0.3,-1.9 -0.5,-2.4c-0.4,-0.3 -0.9,-0.3 -1.3,-0.3s-0.8,0.3 -1.1,0.7l-5.8,8.6c-0.5,0.8 -0.3,1.9 0.5,2.4C68.7,25.9 69.8,25.7 70.3,24.9z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M95.2,44.8l-10.2,2c-0.9,0.2 -1.5,1.1 -1.4,2c0.2,0.9 1.1,1.5 2,1.4l10.2,-2c0.9,-0.2 1.5,-1.1 1.4,-2C97,45.3 96.1,44.7 95.2,44.8z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M86.8,85.1c0.8,0.5 1.9,0.3 2.4,-0.5s0.3,-1.9 -0.5,-2.4l-8.6,-5.8c-0.4,-0.3 -0.9,-0.3 -1.3,-0.3c-0.4,0.1 -0.8,0.3 -1.1,0.7c-0.5,0.8 -0.3,1.9 0.5,2.4L86.8,85.1z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M80.9,36.8l8.8,-5.6c0.8,-0.5 1,-1.6 0.5,-2.4c-0.4,-0.6 -1.1,-0.9 -1.8,-0.8c-0.2,0 -0.4,0.1 -0.6,0.2l-8.8,5.6c-0.8,0.5 -1,1.6 -0.5,2.4C79,37 80.1,37.3 80.9,36.8z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M95.7,65l-10.1,-2.3c-0.2,-0.1 -0.5,-0.1 -0.7,0c-0.7,0.1 -1.2,0.6 -1.4,1.3c-0.2,0.9 0.4,1.8 1.3,2.1L95,68.3c0.9,0.2 1.9,-0.4 2.1,-1.3C97.3,66.1 96.7,65.2 95.7,65z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
    addPath(
        pathData = PathParser().parsePathString("M69.5,88c-0.4,-0.6 -1.1,-0.9 -1.8,-0.8c-0.2,0 -0.4,0.1 -0.6,0.2c-0.8,0.5 -1,1.6 -0.5,2.4l5.6,8.7c0.5,0.8 1.6,1 2.4,0.5c0.8,-0.5 1,-1.6 0.5,-2.4L69.5,88z").toNodes(),
        fill = SolidColor(Color(0xFFFEAC00)),
    )
}.build()

/** `ic_favoritas_carrinho.xml`. */
val CaixaDSFavoriteBetCartIcon: ImageVector = ImageVector.Builder(
    name = "CaixaDSFavoriteBetCartIcon", defaultWidth = 27.dp, defaultHeight = 24.dp,
    viewportWidth = 27.0f, viewportHeight = 24.0f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M10.08,19.2C11.403,19.2 12.48,20.277 12.481,21.6C12.481,22.923 11.403,24 10.08,24C8.757,24 7.68,22.923 7.68,21.6C7.68,20.277 8.757,19.2 10.08,19.2ZM21.6,19.2C22.923,19.2 24,20.277 24,21.6C24,22.923 22.923,24 21.6,24C20.277,24 19.2,22.923 19.2,21.6C19.2,20.277 20.277,19.2 21.6,19.2ZM10.08,20.8C9.639,20.8 9.28,21.159 9.28,21.6C9.28,22.041 9.639,22.4 10.08,22.4C10.521,22.4 10.88,22.041 10.88,21.6C10.88,21.159 10.521,20.8 10.08,20.8ZM21.6,20.8C21.159,20.8 20.8,21.159 20.8,21.6C20.8,22.041 21.159,22.4 21.6,22.4C22.041,22.4 22.4,22.041 22.4,21.6C22.4,21.159 22.041,20.8 21.6,20.8ZM5.513,0C5.882,0 6.201,0.26 6.281,0.626L6.845,3.2H10.65L10.85,4.8H7.194L9.294,14.4H22.349L25.049,4.8H21.15L21.35,3.2H26.093C26.34,3.2 26.573,3.318 26.722,3.519C26.87,3.719 26.917,3.978 26.85,4.22L23.699,15.42C23.603,15.763 23.294,16 22.942,16H8.66C8.227,16.001 7.875,16.36 7.875,16.8C7.875,17.241 8.228,17.599 8.662,17.6H22.942C23.377,17.6 23.73,17.958 23.73,18.4C23.73,18.842 23.377,19.2 22.942,19.2H8.662C7.36,19.2 6.3,18.123 6.3,16.8C6.3,15.815 6.887,14.967 7.724,14.598C7.574,13.912 4.981,2.056 4.881,1.6H0.787C0.352,1.599 0,1.242 0,0.8C0,0.358 0.352,0 0.787,0H5.513ZM15.5,0.5C15.776,0.5 16,0.724 16,1V8.793L18.328,6.465C18.523,6.27 18.84,6.27 19.035,6.465C19.23,6.66 19.23,6.977 19.035,7.172L15.854,10.354C15.658,10.549 15.342,10.549 15.146,10.354L11.965,7.172C11.77,6.977 11.77,6.66 11.965,6.465C12.16,6.27 12.477,6.27 12.672,6.465L15,8.793V1C15,0.724 15.224,0.5 15.5,0.5Z").toNodes(),
        fill = SolidColor(Color(0xFF00437A)),
    )
}.build()

/** `ic_favoritas_lixeira.xml`. */
val CaixaDSFavoriteBetDeleteIcon: ImageVector = ImageVector.Builder(
    name = "CaixaDSFavoriteBetDeleteIcon", defaultWidth = 18.dp, defaultHeight = 24.dp,
    viewportWidth = 18.0f, viewportHeight = 24.0f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M16.008,7.284C16.051,7.284 16.094,7.294 16.134,7.311C16.174,7.328 16.21,7.353 16.24,7.384C16.27,7.415 16.293,7.452 16.308,7.492C16.323,7.533 16.329,7.576 16.326,7.619L15.418,22.486C15.393,22.895 15.211,23.28 14.91,23.561C14.608,23.842 14.209,23.999 13.795,24H3.715C3.3,23.999 2.901,23.842 2.599,23.561C2.297,23.28 2.115,22.895 2.09,22.486L1.184,7.619C1.181,7.576 1.188,7.533 1.202,7.492C1.217,7.452 1.24,7.415 1.27,7.384C1.3,7.353 1.336,7.328 1.376,7.311C1.416,7.294 1.459,7.284 1.502,7.284H16.008ZM5.545,10.803C5.46,10.803 5.379,10.837 5.319,10.896C5.259,10.955 5.225,11.035 5.225,11.119V20.448C5.225,20.531 5.259,20.612 5.319,20.671C5.379,20.73 5.46,20.764 5.545,20.764C5.629,20.764 5.711,20.73 5.77,20.671C5.83,20.612 5.863,20.531 5.863,20.448V11.119C5.863,11.035 5.83,10.955 5.77,10.896C5.711,10.837 5.629,10.803 5.545,10.803ZM8.755,10.803C8.671,10.803 8.589,10.837 8.529,10.896C8.47,10.955 8.436,11.035 8.436,11.119V20.448C8.436,20.531 8.47,20.612 8.529,20.671C8.589,20.73 8.671,20.764 8.755,20.764C8.839,20.764 8.921,20.73 8.981,20.671C9.04,20.612 9.074,20.531 9.074,20.448V11.119C9.074,11.035 9.04,10.955 8.981,10.896C8.921,10.837 8.839,10.803 8.755,10.803ZM11.966,10.803C11.882,10.803 11.8,10.837 11.74,10.896C11.681,10.955 11.646,11.035 11.646,11.119V20.448C11.646,20.531 11.681,20.612 11.74,20.671C11.8,20.73 11.882,20.764 11.966,20.764C12.05,20.764 12.131,20.73 12.191,20.671C12.25,20.612 12.285,20.531 12.285,20.448V11.119C12.285,11.035 12.25,10.955 12.191,10.896C12.131,10.837 12.05,10.803 11.966,10.803Z").toNodes(),
        fill = SolidColor(Color(0xFF00437A)),
        pathFillType = androidx.compose.ui.graphics.PathFillType.EvenOdd,
    )
    addPath(
        pathData = PathParser().parsePathString("M10.718,0C11.063,0 11.393,0.136 11.637,0.377C11.881,0.618 12.019,0.945 12.019,1.286V2.087H14.973C15.578,2.088 16.158,2.326 16.586,2.749C17.014,3.172 17.255,3.745 17.255,4.343V5.68C17.255,5.722 17.247,5.763 17.231,5.801C17.215,5.839 17.191,5.874 17.162,5.903C17.132,5.933 17.097,5.956 17.058,5.972C17.019,5.988 16.978,5.995 16.936,5.995H0.574C0.49,5.995 0.409,5.962 0.349,5.903C0.289,5.844 0.255,5.764 0.255,5.68V4.343C0.256,3.745 0.497,3.172 0.925,2.749C1.353,2.326 1.932,2.088 2.537,2.087H5.491V1.286C5.491,0.945 5.629,0.618 5.873,0.377C6.116,0.136 6.447,0 6.791,0H10.718Z").toNodes(),
        fill = SolidColor(Color(0xFF00437A)),
    )
}.build()

/** `ic_editar.xml`, o controle de edição do resumo do Rapidão. */
val CaixaDSRapidEditIcon: ImageVector = ImageVector.Builder(
    name = "CaixaDSRapidEditIcon", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 330.05f, viewportHeight = 329f,
).apply {
    addPath(PathParser().parsePathString("M310.07,180.42a10,10 0,0 0,-10 10v88.67a30,30 0,0 1,-29.95 30L49.93,309.09a30,30 0,0 1,-30 -30L19.93,70.85a30,30 0,0 1,30 -30h88.66a10,10 0,1 0,0 -20L49.93,20.85A50,50 0,0 0,0 70.85L0,279.07a50,50 0,0 0,49.93 49.93h220.2a50,50 0,0 0,49.93 -49.93L320.06,190.41A10,10 0,0 0,310.07 180.42Z").toNodes(), fill = SolidColor(Color(0xFF7CBAAA)))
    addPath(PathParser().parsePathString("M92.66,179.37 L232.53,39.51l58,58L150.67,237.38Z").toNodes(), fill = SolidColor(Color(0xFF7CBAAA)))
    addPath(PathParser().parsePathString("M63.31,266.73l64.11,-17.76 -46.35,-46.35Z").toNodes(), fill = SolidColor(Color(0xFF7CBAAA)))
    addPath(PathParser().parsePathString("M306.58,9a30.84,30.84 0,0 0,-43.55 0L250,22.07l58,58L321,67.01A30.82,30.82 0,0 0,321 23.51Z").toNodes(), fill = SolidColor(Color(0xFF7CBAAA)))
}.build()
