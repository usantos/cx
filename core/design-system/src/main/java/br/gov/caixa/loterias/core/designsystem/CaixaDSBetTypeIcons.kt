package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Original ic_*_linha vectors, including nested clipping from the Combo asset. */
object CaixaDSBetTypeIcons {
    val Exchange: ImageVector = ImageVector.Builder(
        name = "CaixaDS.BetType.Exchange", defaultWidth = 15.dp, defaultHeight = 18.dp,
        viewportWidth = 15f, viewportHeight = 18f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString("M14.262,2.24L8.877,0.308C8.315,0.107 7.708,0.444 7.524,1.059L7.494,1.157L7.478,1.104C7.294,0.489 6.686,0.152 6.124,0.353L0.739,2.285C0.177,2.486 -0.131,3.151 0.053,3.767L2.875,13.191C3.058,13.806 3.666,14.143 4.228,13.942L7.187,12.88L7.562,12.745L10.771,13.897C11.334,14.098 11.941,13.761 12.125,13.146L14.947,3.721C15.131,3.106 14.823,2.441 14.261,2.24H14.262ZM3.941,12.982C3.862,13.011 3.775,12.962 3.75,12.875L0.929,3.453C0.902,3.366 0.947,3.27 1.026,3.243L6.411,1.312C6.49,1.283 6.577,1.331 6.602,1.418L7.131,3.184L7.615,4.802L9.422,10.841C9.448,10.928 9.404,11.023 9.325,11.05L7.185,11.817L5.706,12.347L3.94,12.98L3.941,12.982ZM14.073,3.407L11.251,12.832C11.225,12.919 11.139,12.967 11.06,12.938L9.042,12.215L9.613,12.01C10.175,11.809 10.483,11.144 10.299,10.528L8.1,3.185L7.978,2.778L8.398,1.375C8.424,1.288 8.51,1.239 8.589,1.268L13.974,3.2C14.053,3.229 14.097,3.322 14.071,3.409L14.073,3.407Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
        addPath(
            pathData = PathParser().parsePathString("M12.498,15.81C11.348,15.414 9.919,14.731 9.029,14L9.551,15.34H5.453L5.919,14.041C5.048,14.796 3.638,15.515 2.5,15.94C3.65,16.336 5.079,17.019 5.969,17.75L5.461,16.443H9.535L9.081,17.709C9.952,16.954 11.362,16.235 12.5,15.81H12.498Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
    }.build()

    val Pool: ImageVector = ImageVector.Builder(
        name = "CaixaDS.BetType.Pool", defaultWidth = 16.dp, defaultHeight = 16.dp,
        viewportWidth = 16f, viewportHeight = 16f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString("M4.824,15.5C3.56,15.5 2.494,15.066 1.626,14.198C0.758,13.33 0.324,12.264 0.324,11C0.324,10.472 0.414,9.958 0.594,9.458C0.775,8.958 1.032,8.507 1.365,8.104L4.324,4.542L2.303,0.5H13.344L11.324,4.542L14.282,8.104C14.615,8.507 14.872,8.958 15.053,9.458C15.233,9.958 15.324,10.472 15.324,11C15.324,12.264 14.886,13.33 14.011,14.198C13.136,15.066 12.074,15.5 10.824,15.5H4.824ZM7.824,11.333C7.365,11.333 6.973,11.17 6.647,10.844C6.32,10.517 6.157,10.125 6.157,9.667C6.157,9.208 6.32,8.816 6.647,8.49C6.973,8.163 7.365,8 7.824,8C8.282,8 8.674,8.163 9.001,8.49C9.327,8.816 9.49,9.208 9.49,9.667C9.49,10.125 9.327,10.517 9.001,10.844C8.674,11.17 8.282,11.333 7.824,11.333ZM5.844,3.833H9.803L10.636,2.167H5.011L5.844,3.833ZM4.824,13.833H10.824C11.615,13.833 12.285,13.559 12.834,13.01C13.383,12.462 13.657,11.792 13.657,11C13.657,10.667 13.598,10.344 13.48,10.031C13.362,9.719 13.199,9.438 12.99,9.188L9.928,5.5H5.74L2.657,9.167C2.449,9.417 2.285,9.701 2.167,10.021C2.049,10.34 1.99,10.667 1.99,11C1.99,11.792 2.265,12.462 2.813,13.01C3.362,13.559 4.032,13.833 4.824,13.833Z").toNodes(),
            fill = SolidColor(Color(0xFF00A651)),
        )
    }.build()

    val Combo: ImageVector = ImageVector.Builder(
        name = "CaixaDS.BetType.Combo", defaultWidth = 13.dp, defaultHeight = 16.dp,
        viewportWidth = 13f, viewportHeight = 16f,
    ).apply {
        addGroup(clipPathData = PathParser().parsePathString("M0,0h13v16h-13z").toNodes())
        addPath(
            pathData = PathParser().parsePathString("M11.664,0H5.227C4.491,0 3.892,0.566 3.892,1.262V1.869H3.256C2.519,1.869 1.921,2.435 1.921,3.131V3.749H1.336C0.599,3.749 0,4.315 0,5.011V14.738C0,15.434 0.599,16 1.336,16H7.772C8.509,16 9.108,15.434 9.108,14.738V14.12H9.693C10.43,14.12 11.029,13.554 11.029,12.858V12.251H11.664C12.401,12.251 13,11.685 13,10.989V1.262C13,0.566 12.401,0 11.664,0H11.664ZM11.72,1.262V10.989C11.72,11.019 11.695,11.042 11.664,11.042H5.227C5.196,11.042 5.171,11.019 5.171,10.989V1.262C5.171,1.233 5.196,1.209 5.227,1.209H11.664C11.695,1.209 11.72,1.233 11.72,1.262ZM3.2,12.858V3.131C3.2,3.102 3.225,3.078 3.256,3.078H3.892V10.989C3.892,11.685 4.491,12.251 5.227,12.251H9.749V12.858C9.749,12.887 9.724,12.911 9.692,12.911H3.256C3.225,12.911 3.2,12.888 3.2,12.858ZM3.256,14.12H7.828V14.737C7.828,14.766 7.803,14.79 7.772,14.79H1.336C1.305,14.79 1.279,14.767 1.279,14.737V5.01C1.279,4.981 1.305,4.957 1.336,4.957H1.921V12.857C1.921,13.554 2.519,14.12 3.256,14.12Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
        addGroup(clipPathData = PathParser().parsePathString("M6,3h5v5h-5z").toNodes())
        addPath(
            pathData = PathParser().parsePathString("M8.381,7.233C8.381,7.65 8.031,8 7.615,8C7.199,8 6.849,7.65 6.849,7.233V7.15H6.767C6.35,7.15 6,6.799 6,6.383C6,5.966 6.351,5.618 6.767,5.618H8.381V7.233Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
        addPath(
            pathData = PathParser().parsePathString("M8.381,7.233C8.381,7.65 8.031,8 7.615,8C7.199,8 6.849,7.65 6.849,7.233V7.15H6.767C6.35,7.15 6,6.799 6,6.383C6,5.966 6.351,5.618 6.767,5.618H8.381V7.233Z").toNodes(),
            fill = SolidColor(Color(0x00000000)),
            stroke = SolidColor(Color(0xFF00984A)), strokeLineWidth = 1f,
        )
        addPath(
            pathData = PathParser().parsePathString("M7.615,3.15C7.948,3.15 8.231,3.433 8.231,3.767V5.232H6.767C6.433,5.232 6.151,4.951 6.15,4.617C6.15,4.283 6.433,4 6.767,4H6.999V3.767C6.999,3.433 7.282,3.15 7.615,3.15Z").toNodes(),
            fill = SolidColor(Color(0x00000000)),
            stroke = SolidColor(Color(0xFF00984A)), strokeLineWidth = 0.3f,
        )
        addPath(
            pathData = PathParser().parsePathString("M10.234,5.768C10.568,5.768 10.85,6.049 10.85,6.383C10.85,6.717 10.567,7 10.234,7H10.001V7.233C10,7.567 9.718,7.85 9.385,7.85C9.052,7.85 8.769,7.567 8.769,7.233V5.768H10.234Z").toNodes(),
            fill = SolidColor(Color(0x00000000)),
            stroke = SolidColor(Color(0xFF00984A)), strokeLineWidth = 0.3f,
        )
        addPath(
            pathData = PathParser().parsePathString("M9.385,3C9.801,3 10.151,3.35 10.151,3.767V3.85H10.234C10.65,3.85 11,4.201 11,4.617C11,5.034 10.65,5.382 10.234,5.382H8.619V3.767C8.619,3.35 8.969,3 9.385,3Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
        addPath(
            pathData = PathParser().parsePathString("M9.385,3C9.801,3 10.151,3.35 10.151,3.767V3.85H10.234C10.65,3.85 11,4.201 11,4.617C11,5.034 10.65,5.382 10.234,5.382H8.619V3.767C8.619,3.35 8.969,3 9.385,3Z").toNodes(),
            fill = SolidColor(Color(0x00000000)),
            stroke = SolidColor(Color(0xFF00984A)), strokeLineWidth = 1f,
        )
        clearGroup()
        clearGroup()
    }.build()

    val Mirror: ImageVector = ImageVector.Builder(
        name = "CaixaDS.BetType.Mirror", defaultWidth = 14.dp, defaultHeight = 14.dp,
        viewportWidth = 14f, viewportHeight = 14f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString("M0.074,0.25V13.75H13.574V0.25H0.074ZM12.074,12.25H1.574V1.75H12.074V12.25ZM6.074,3.25H7.574V4.75H6.074V3.25ZM3.074,3.25H4.574V4.75H3.074V3.25ZM9.074,3.25H10.574V4.75H9.074V3.25ZM3.074,6.25H4.574V7.75H3.074V6.25ZM6.074,6.25H7.574V7.75H6.074V6.25ZM9.074,6.25H10.574V7.75H9.074V6.25Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
    }.build()

    val Surprise: ImageVector = ImageVector.Builder(
        name = "CaixaDS.BetType.Surprise", defaultWidth = 17.dp, defaultHeight = 16.dp,
        viewportWidth = 17f, viewportHeight = 16f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString("M0.746,15.703L11.246,11.953L4.496,5.202L0.746,15.703ZM8.509,11.337L3.221,13.227L5.111,7.94L8.509,11.337Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
        addPath(
            pathData = PathParser().parsePathString("M10.144,8.6L14.336,4.407C14.704,4.04 15.296,4.04 15.664,4.407L16.106,4.85L16.901,4.055L16.459,3.612C15.656,2.81 14.344,2.81 13.541,3.612L9.349,7.805L10.144,8.6Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
        addPath(
            pathData = PathParser().parsePathString("M6.791,4.362L6.349,4.805L7.144,5.6L7.586,5.157C8.389,4.355 8.389,3.042 7.586,2.24L7.144,1.797L6.349,2.6L6.791,3.042C7.151,3.402 7.151,4.002 6.791,4.362Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
        addPath(
            pathData = PathParser().parsePathString("M12.041,8.112L10.849,9.305L11.644,10.1L12.836,8.907C13.204,8.54 13.796,8.54 14.164,8.907L15.371,10.115L16.166,9.32L14.959,8.112C14.149,7.31 12.844,7.31 12.041,8.112Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
        addPath(
            pathData = PathParser().parsePathString("M10.541,3.612L7.849,6.305L8.644,7.1L11.336,4.407C12.139,3.605 12.139,2.292 11.336,1.49L10.144,0.297L9.349,1.092L10.541,2.285C10.901,2.652 10.901,3.252 10.541,3.612Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
    }.build()

    val Recurring: ImageVector = ImageVector.Builder(
        name = "CaixaDS.BetType.Recurring", defaultWidth = 17.dp, defaultHeight = 16.dp,
        viewportWidth = 17f, viewportHeight = 16f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString("M8.954,7.363C7.693,6.945 7.084,6.683 7.084,6.017C7.084,5.294 7.87,5.032 8.366,5.032C9.294,5.032 9.634,5.733 9.712,5.981L10.831,5.507C10.725,5.188 10.25,4.147 9.032,3.92V3.042H7.615V3.934C5.859,4.331 5.852,5.96 5.852,6.031C5.852,7.639 7.445,8.092 8.224,8.375C9.344,8.772 9.839,9.133 9.839,9.813C9.839,10.614 9.096,10.954 8.437,10.954C7.148,10.954 6.779,9.629 6.737,9.473L5.561,9.948C6.007,11.499 7.176,11.917 7.615,12.045V12.958H9.032V12.08C9.315,12.016 11.086,11.662 11.086,9.799C11.086,8.815 10.654,7.95 8.954,7.363ZM1.949,14.375H0.532V10.125H4.782V11.542H3.025C4.166,13.249 6.114,14.375 8.324,14.375C11.844,14.375 14.699,11.52 14.699,8H16.115C16.115,12.307 12.63,15.792 8.324,15.792C5.689,15.792 3.358,14.481 1.949,12.484V14.375ZM0.532,8C0.532,3.693 4.017,0.208 8.324,0.208C10.959,0.208 13.289,1.519 14.699,3.516V1.625H16.115V5.875H11.865V4.458H13.622C12.482,2.751 10.534,1.625 8.324,1.625C4.803,1.625 1.949,4.48 1.949,8H0.532Z").toNodes(),
            fill = SolidColor(Color(0xFF00984A)),
        )
    }.build()

}
