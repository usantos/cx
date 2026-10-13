package br.gov.caixa.loterias.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Ícones semânticos. A feature depende do significado, não do nome Material. */
object CaixaDSIcons {
    /** `ic_lupa_detalhe_aposta`: original 23 × 24 dp paths, including the plus sign. */
    val PrizeBetDetail: ImageVector = ImageVector.Builder(
        name = "CaixaDS.PrizeBetDetail", defaultWidth = 23.dp, defaultHeight = 24.dp,
        viewportWidth = 23f, viewportHeight = 24f,
    ).apply {
        val stroke = SolidColor(CaixaDSColorTokens.LegacyPrizeDetailIcon)
        addPath(
            pathData = PathParser().parsePathString("M8.9803,8.9803m-7.9803,0a7.9803,7.9803 0,1 1,15.9605 0a7.9803,7.9803 0,1 1,-15.9605 0").toNodes(),
            stroke = stroke, strokeLineWidth = 2f,
        )
        addPath(
            pathData = PathParser().parsePathString("M15.3359,15.6198L21.9759,22.0321").toNodes(),
            stroke = stroke, strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round,
        )
        addPath(
            pathData = PathParser().parsePathString("M8.9805,5.2832V12.6787").toNodes(),
            stroke = stroke, strokeLineWidth = 1f,
        )
        addPath(
            pathData = PathParser().parsePathString("M12.6777,8.9805H5.2822").toNodes(),
            stroke = stroke, strokeLineWidth = 1f,
        )
    }.build()

    /** Chevron de retorno `seta_esquerda` do tema legado, preservado em 12 x 18 dp. */
    val Back: ImageVector = ImageVector.Builder(
        name = "CaixaDS.Back",
        defaultWidth = 12.dp,
        defaultHeight = 18.dp,
        viewportWidth = 12f,
        viewportHeight = 18f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(9.5f, 0f)
            lineTo(12f, 2.5f)
            lineTo(5.5f, 9f)
            lineTo(12f, 15.5f)
            lineTo(9.5f, 18f)
            lineTo(.5f, 9f)
            close()
        }
    }.build()
    val Forward: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    val Close: ImageVector = Icons.Filled.Close
    val Confirm: ImageVector = Icons.Filled.Check
    val Add: ImageVector = Icons.Filled.Add
    val Remove: ImageVector = ImageVector.Builder(
        name = "CaixaDS.Remove",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19f, 13f)
            horizontalLineTo(5f)
            verticalLineTo(11f)
            horizontalLineTo(19f)
            close()
        }
    }.build()
    val Search: ImageVector = CaixaDSLegacySearchIcon
    val Download: ImageVector = legacyVector(
        "CaixaDS.Download",
        24f,
        24f,
        LegacyPath("M19,9H15V3H9V9H5L12,16L19,9ZM5,18V20H19V18H5Z", 0xFFFFFFFF),
    )
    val Filter: ImageVector = legacyVector(
        "CaixaDS.Filter",
        24f,
        24f,
        LegacyPath("M3,5C3,4.45 3.45,4 4,4H20C20.55,4 21,4.45 21,5C21,5.24 20.91,5.47 20.75,5.65L14,13.1V19C14,19.38 13.79,19.72 13.45,19.89L9.45,21.89C8.78,22.22 8,21.74 8,21V13.1L3.25,5.65C3.09,5.47 3,5.24 3,5Z", 0xFFFFFFFF),
    )
    val Information: ImageVector = Icons.Filled.Info
    val Help: ImageVector = legacyVector(
        "CaixaDS.Help",
        24f,
        24f,
        LegacyPath("M11,18H13V16H11V18ZM12,2C6.48,2 2,6.48 2,12C2,17.52 6.48,22 12,22C17.52,22 22,17.52 22,12C22,6.48 17.52,2 12,2ZM12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20ZM12,6C9.79,6 8,7.79 8,10H10C10,8.9 10.9,8 12,8C13.1,8 14,8.9 14,10C14,12 11,11.75 11,15H13C13,12.75 16,12.5 16,10C16,7.79 14.21,6 12,6Z", 0xFFFFFFFF),
    )
    val Warning: ImageVector = Icons.Filled.Warning
    val Time: ImageVector = ImageVector.Builder(
        name = "CaixaDS.Time",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
            curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
            curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
            close()
            moveTo(12f, 20f)
            curveTo(7.58f, 20f, 4f, 16.42f, 4f, 12f)
            curveTo(4f, 7.58f, 7.58f, 4f, 12f, 4f)
            curveTo(16.42f, 4f, 20f, 7.58f, 20f, 12f)
            curveTo(20f, 16.42f, 16.42f, 20f, 12f, 20f)
            close()
            moveTo(12.5f, 7f)
            horizontalLineTo(11f)
            verticalLineTo(13f)
            lineTo(16.25f, 16.15f)
            lineTo(17f, 14.92f)
            lineTo(12.5f, 12.25f)
            close()
        }
    }.build()
    val ChevronDown: ImageVector = Icons.Filled.KeyboardArrowDown
    val Favorite: ImageVector = Icons.Filled.Favorite
    val FavoriteBorder: ImageVector = Icons.Outlined.FavoriteBorder
    val Email: ImageVector = Icons.Filled.Email
    val Edit: ImageVector = Icons.Filled.Edit
    val Delete: ImageVector = Icons.Filled.Delete
    val Location: ImageVector = Icons.Filled.LocationOn
    val Star: ImageVector = Icons.Filled.Star
    val More: ImageVector = Icons.Filled.MoreVert

    /** Ampulheta circular original da confirmação de compra, convertida do vector XML legado. */
    val PaymentProcessing = legacyVector(
        "CaixaDS.PaymentProcessing",
        72f,
        72f,
        LegacyPath("M24.632,17.053V28.421H24.65L24.632,28.44L32.21,36L24.632,43.579L24.65,43.598H24.632V54.947H47.368V43.598H47.349L47.368,43.579L39.79,36L47.368,28.44L47.349,28.421H47.368V17.053H24.632ZM43.579,44.526V51.158H28.421V44.526L36,36.947L43.579,44.526ZM36,35.053L28.421,27.474V20.842H43.579V27.474L36,35.053Z", 0xFFD87B00),
        LegacyPath("M72,28L66.316,5.684L61.44,10.56C54.96,4.04 45.96,0 36,0C16.12,0 0,16.12 0,36C0,55.88 16.12,72 36,72C55.88,72 72,55.88 72,36H64C64,51.44 51.44,64 36,64C20.56,64 8,51.44 8,36C8,20.56 20.56,8 36,8C43.72,8 50.72,11.16 55.8,16.2L49.684,22.316L72,28Z", 0xFFD87B00),
    )

    /** Símbolo CAIXA branco/laranja usado na opção de resgate em agência. */
    val Caixa = legacyVector(
        "CaixaDS.Caixa",
        19f,
        14f,
        LegacyPath("M13.114,0.003L10.56,2.618L12.547,6.609L19,0.003H13.114Z", 0xFFF39200, PathFillType.EvenOdd),
        LegacyPath("M3.371,0.003L9.256,0L12.547,6.609H6.658L3.371,0.003Z", 0xFFFFFFFF, PathFillType.EvenOdd),
        LegacyPath("M0,13.886H5.889L12.342,7.28H6.453L0,13.886Z", 0xFFF39200, PathFillType.EvenOdd),
        LegacyPath("M8.439,11.272L12.341,7.28L15.629,13.886H9.74L8.439,11.272Z", 0xFFFFFFFF, PathFillType.EvenOdd),
    )

    val NotificationRead = legacyVector(
        "CaixaDS.NotificationRead",
        26f,
        26f,
        LegacyPath("M13,13m-13,0a13,13 0,1 1,26 0a13,13 0,1 1,-26 0", 0xFF0163AF),
        LegacyPath("M20.493,11.125C20.493,10.585 20.215,10.113 19.788,9.85L13,5.875L6.212,9.85C5.785,10.113 5.5,10.585 5.5,11.125V18.625C5.5,19.45 6.175,20.125 7,20.125H19C19.825,20.125 20.5,19.45 20.5,18.625L20.493,11.125ZM18.993,11.125V11.132L13,14.875L7,11.125L13,7.615L18.993,11.125ZM7,18.625V12.88L13,16.645L18.993,12.903L19,18.625H7Z", 0xFFFFFFFF),
    )
    val NotificationDelete = legacyVector(
        "CaixaDS.NotificationDelete",
        24f,
        24f,
        LegacyPath("M12,12m-12,0a12,12 0,1 1,24 0a12,12 0,1 1,-24 0", 0xFF0163AF),
        LegacyPath("M14.769,9.923V16.846H9.231V9.923H14.769ZM13.731,5.769H10.269L9.577,6.461H7.154V7.846H16.846V6.461H14.423L13.731,5.769ZM16.154,8.538H7.846V16.846C7.846,17.608 8.469,18.231 9.231,18.231H14.769C15.531,18.231 16.154,17.608 16.154,16.846V8.538Z", 0xFFFFFFFF),
    )
    val NotificationShare = legacyVector(
        "CaixaDS.NotificationShare",
        20f,
        20f,
        LegacyPath("M16.663,0C18.648,0.018 20.174,1.702 19.984,3.668C19.834,5.219 18.629,6.446 17.08,6.636C15.904,6.78 14.922,6.413 14.144,5.522C14.04,5.402 13.969,5.44 13.864,5.493C12.286,6.283 10.705,7.071 9.126,7.859C8.327,8.258 7.529,8.659 6.728,9.052C6.592,9.119 6.555,9.189 6.59,9.34C6.694,9.784 6.692,10.234 6.589,10.679C6.557,10.817 6.584,10.887 6.715,10.954C9.108,12.144 11.498,13.338 13.887,14.536C14.003,14.594 14.06,14.576 14.146,14.482C15.094,13.455 16.254,13.101 17.597,13.481C18.917,13.855 19.702,14.761 19.929,16.105C20.229,17.881 19.155,19.659 17.101,19.966C15.209,20.25 13.183,18.719 13.331,16.406C13.339,16.276 13.35,16.14 13.39,16.017C13.445,15.847 13.384,15.777 13.234,15.703C10.866,14.526 8.499,13.344 6.136,12.158C5.992,12.085 5.923,12.1 5.811,12.221C4.88,13.238 3.726,13.573 2.414,13.188C1.093,12.8 0.245,11.916 0.041,10.528C-0.234,8.661 0.885,7.093 2.745,6.74C3.961,6.51 5.006,6.878 5.831,7.814C5.922,7.917 5.987,7.93 6.108,7.87C8.491,6.675 10.876,5.484 13.263,4.298C13.404,4.228 13.425,4.153 13.392,4.006C12.945,1.961 14.564,-0.019 16.663,0Z", 0xFF6E7E97),
    )
    val NotificationMoney = notificationCategoryIcon("CaixaDS.NotificationMoney", "M18,8C12.48,8 8,12.48 8,18C8,23.52 12.48,28 18,28C23.52,28 28,23.52 28,18C28,12.48 23.52,8 18,8ZM18,26C13.59,26 10,22.41 10,18C10,13.59 13.59,10 18,10C22.41,10 26,13.59 26,18C26,22.41 22.41,26 18,26ZM18.31,17.14C16.54,16.69 15.97,16.2 15.97,15.47C15.97,14.63 16.76,14.04 18.07,14.04C19.45,14.04 19.97,14.7 20.01,15.68H21.72C21.67,14.34 20.85,13.11 19.23,12.71V11H16.9V12.69C15.39,13.01 14.18,13.99 14.18,15.5C14.18,17.29 15.67,18.19 17.84,18.71C19.79,19.17 20.18,19.86 20.18,20.58C20.18,21.11 19.79,21.97 18.08,21.97C16.48,21.97 15.85,21.25 15.76,20.33H14.04C14.14,22.03 15.4,22.99 16.9,23.3V25H19.24V23.33C20.76,23.04 21.96,22.17 21.97,20.56C21.96,18.36 20.07,17.6 18.31,17.14Z")
    val NotificationPayments = notificationCategoryIcon("CaixaDS.NotificationPayments", "M25.333,10.667H10.667C9.649,10.667 8.842,11.483 8.842,12.5L8.833,23.5C8.833,24.517 9.649,25.333 10.667,25.333H25.333C26.351,25.333 27.167,24.517 27.167,23.5V12.5C27.167,11.483 26.351,10.667 25.333,10.667ZM25.333,23.5H10.667V18H25.333V23.5ZM25.333,14.333H10.667V12.5H25.333V14.333Z")
    val NotificationSecurity = notificationCategoryIcon("CaixaDS.NotificationSecurity", "M23.5,14.792H22.583V12.958C22.583,10.428 20.53,8.375 18,8.375C15.47,8.375 13.417,10.428 13.417,12.958V14.792H12.5C11.492,14.792 10.667,15.617 10.667,16.625V25.792C10.667,26.8 11.492,27.625 12.5,27.625H23.5C24.508,27.625 25.333,26.8 25.333,25.792V16.625C25.333,15.617 24.508,14.792 23.5,14.792ZM15.25,12.958C15.25,11.437 16.478,10.208 18,10.208C19.522,10.208 20.75,11.437 20.75,12.958V14.792H15.25V12.958ZM23.5,25.792H12.5V16.625H23.5V25.792ZM18,23.042C19.008,23.042 19.833,22.217 19.833,21.208C19.833,20.2 19.008,19.375 18,19.375C16.992,19.375 16.167,20.2 16.167,21.208C16.167,22.217 16.992,23.042 18,23.042Z")
    val NotificationNews = notificationCategoryIcon("CaixaDS.NotificationNews", "M28.083,17.995L25.847,15.447L26.158,12.074L22.849,11.322L21.117,8.407L18,9.745L14.883,8.407L13.151,11.322L9.842,12.065L10.153,15.438L7.917,17.995L10.153,20.544L9.842,23.926L13.151,24.678L14.883,27.593L18,26.245L21.117,27.584L22.849,24.669L26.158,23.917L25.847,20.544L28.083,17.995ZM23.949,19.93L24.188,22.487L21.676,23.055L20.365,25.265L18,24.247L15.635,25.265L14.324,23.055L11.813,22.487L12.051,19.92L10.355,17.995L12.051,16.052L11.813,13.504L14.324,12.945L15.635,10.735L18,11.744L20.365,10.726L21.676,12.935L24.188,13.504L23.949,16.061L25.645,17.995L23.949,19.93ZM17.083,20.745H18.917V22.579H17.083V20.745ZM17.083,13.412H18.917V18.912H17.083V13.412Z")
    val NotificationResults = notificationCategoryIcon("CaixaDS.NotificationResults", "M18,8.833C12.94,8.833 8.833,12.94 8.833,18C8.833,23.06 12.94,27.167 18,27.167C23.06,27.167 27.167,23.06 27.167,18C27.167,12.94 23.06,8.833 18,8.833ZM18,25.333C13.958,25.333 10.667,22.042 10.667,18C10.667,13.958 13.958,10.667 18,10.667C22.042,10.667 25.333,13.958 25.333,18C25.333,22.042 22.042,25.333 18,25.333ZM22.208,13.948L16.167,19.989L13.792,17.624L12.5,18.917L16.167,22.583L23.5,15.25L22.208,13.948Z")

    /** Carrinho original do somador inferior, convertido do vector XML legado. */
    val Cart: ImageVector = ImageVector.Builder(
        name = "CaixaDS.Cart",
        defaultWidth = 28.dp,
        defaultHeight = 28.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(7.7325f, 15.5159f)
            horizontalLineTo(7.7336f)
            curveTo(7.7346f, 15.5159f, 7.7355f, 15.5157f, 7.7364f, 15.5157f)
            horizontalLineTo(20.4844f)
            curveTo(20.7982f, 15.5159f, 21.0742f, 15.3076f, 21.1604f, 15.0058f)
            lineTo(23.9729f, 5.162f)
            curveTo(24.0335f, 4.9498f, 23.991f, 4.7217f, 23.8583f, 4.5455f)
            curveTo(23.7253f, 4.3694f, 23.5175f, 4.2657f, 23.2969f, 4.2657f)
            horizontalLineTo(6.111f)
            lineTo(5.6083f, 2.0038f)
            curveTo(5.5367f, 1.6821f, 5.2515f, 1.4533f, 4.9219f, 1.4533f)
            horizontalLineTo(0.7031f)
            curveTo(0.3148f, 1.4533f, 0f, 1.768f, 0f, 2.1564f)
            curveTo(0f, 2.5447f, 0.3148f, 2.8595f, 0.7031f, 2.8595f)
            horizontalLineTo(4.3579f)
            curveTo(4.4469f, 3.2603f, 6.7632f, 13.6838f, 6.8965f, 14.2834f)
            curveTo(6.1492f, 14.6083f, 5.625f, 15.3533f, 5.625f, 16.2189f)
            curveTo(5.625f, 17.382f, 6.5713f, 18.3282f, 7.7344f, 18.3282f)
            horizontalLineTo(20.4844f)
            curveTo(20.8727f, 18.3282f, 21.1875f, 18.0135f, 21.1875f, 17.6251f)
            curveTo(21.1875f, 17.2368f, 20.8727f, 16.922f, 20.4844f, 16.922f)
            horizontalLineTo(7.7344f)
            curveTo(7.3467f, 16.922f, 7.0313f, 16.6065f, 7.0313f, 16.2189f)
            curveTo(7.0313f, 15.8318f, 7.3456f, 15.5168f, 7.7325f, 15.5159f)
            close()
            moveTo(22.3647f, 5.672f)
            lineTo(19.9539f, 14.1095f)
            horizontalLineTo(8.2983f)
            lineTo(6.4233f, 5.672f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7.0312f, 20.4374f)
            curveTo(7.0312f, 21.6005f, 7.9775f, 22.5468f, 9.1406f, 22.5468f)
            curveTo(10.3037f, 22.5468f, 11.25f, 21.6005f, 11.25f, 20.4374f)
            curveTo(11.25f, 19.2744f, 10.3037f, 18.3281f, 9.1406f, 18.3281f)
            curveTo(7.9775f, 18.3281f, 7.0312f, 19.2744f, 7.0312f, 20.4374f)
            close()
            moveTo(9.1406f, 19.7343f)
            curveTo(9.5282f, 19.7343f, 9.8437f, 20.0498f, 9.8437f, 20.4374f)
            curveTo(9.8437f, 20.8251f, 9.5282f, 21.1406f, 9.1406f, 21.1406f)
            curveTo(8.753f, 21.1406f, 8.4375f, 20.8251f, 8.4375f, 20.4374f)
            curveTo(8.4375f, 20.0498f, 8.753f, 19.7343f, 9.1406f, 19.7343f)
            close()
            moveTo(16.9687f, 20.4374f)
            curveTo(16.9687f, 21.6005f, 17.915f, 22.5468f, 19.0781f, 22.5468f)
            curveTo(20.2412f, 22.5468f, 21.1875f, 21.6005f, 21.1875f, 20.4374f)
            curveTo(21.1875f, 19.2744f, 20.2412f, 18.3281f, 19.0781f, 18.3281f)
            curveTo(17.915f, 18.3281f, 16.9687f, 19.2744f, 16.9687f, 20.4374f)
            close()
            moveTo(19.0781f, 19.7343f)
            curveTo(19.4657f, 19.7343f, 19.7812f, 20.0498f, 19.7812f, 20.4374f)
            curveTo(19.7812f, 20.8251f, 19.4657f, 21.1406f, 19.0781f, 21.1406f)
            curveTo(18.6905f, 21.1406f, 18.375f, 20.8251f, 18.375f, 20.4374f)
            curveTo(18.375f, 20.0498f, 18.6905f, 19.7343f, 19.0781f, 19.7343f)
            close()
        }
    }.build()

    /** Ícone de telefone usado pela introdução legada. */
    val IntroPhone: ImageVector = ImageVector.Builder(
        name = "CaixaDS.IntroPhone",
        defaultWidth = 30.dp,
        defaultHeight = 50.dp,
        viewportWidth = 30f,
        viewportHeight = 50f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(24f, .25f)
            horizontalLineTo(6f)
            curveTo(2.895f, .25f, .375f, 2.77f, .375f, 5.875f)
            verticalLineTo(44.125f)
            curveTo(.375f, 47.23f, 2.895f, 49.75f, 6f, 49.75f)
            horizontalLineTo(24f)
            curveTo(27.105f, 49.75f, 29.625f, 47.23f, 29.625f, 44.125f)
            verticalLineTo(5.875f)
            curveTo(29.625f, 2.77f, 27.105f, .25f, 24f, .25f)
            close()
            moveTo(15f, 47.5f)
            curveTo(13.132f, 47.5f, 11.625f, 45.993f, 11.625f, 44.125f)
            curveTo(11.625f, 42.257f, 13.132f, 40.75f, 15f, 40.75f)
            curveTo(16.868f, 40.75f, 18.375f, 42.257f, 18.375f, 44.125f)
            curveTo(18.375f, 45.993f, 16.868f, 47.5f, 15f, 47.5f)
            close()
            moveTo(25.125f, 38.5f)
            horizontalLineTo(4.875f)
            verticalLineTo(7f)
            horizontalLineTo(25.125f)
            close()
        }
    }.build()

    /** Estrela vazada usada pela segunda página da introdução legada. */
    val IntroStar: ImageVector = ImageVector.Builder(
        name = "CaixaDS.IntroStar",
        defaultWidth = 50.dp,
        defaultHeight = 49.dp,
        viewportWidth = 50f,
        viewportHeight = 49f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(50f, 18.85f)
            lineTo(32.025f, 17.3f)
            lineTo(25f, .75f)
            lineTo(17.975f, 17.325f)
            lineTo(0f, 18.85f)
            lineTo(13.65f, 30.675f)
            lineTo(9.55f, 48.25f)
            lineTo(25f, 38.925f)
            lineTo(40.45f, 48.25f)
            lineTo(36.375f, 30.675f)
            close()
            moveTo(25f, 34.25f)
            lineTo(15.6f, 39.925f)
            lineTo(18.1f, 29.225f)
            lineTo(9.8f, 22.025f)
            lineTo(20.75f, 21.075f)
            lineTo(25f, 11f)
            lineTo(29.275f, 21.1f)
            lineTo(40.225f, 22.05f)
            lineTo(31.925f, 29.25f)
            lineTo(34.425f, 39.95f)
            close()
        }
    }.build()

    /** Lista com lupa usada pela terceira página da introdução legada. */
    val IntroManageSearch: ImageVector = ImageVector.Builder(
        name = "CaixaDS.IntroManageSearch",
        defaultWidth = 59.dp,
        defaultHeight = 60.dp,
        viewportWidth = 59f,
        viewportHeight = 60f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(17.208f, 21.396f)
            horizontalLineTo(4.917f)
            verticalLineTo(16.479f)
            horizontalLineTo(17.208f)
            close()
            moveTo(17.208f, 28.771f)
            horizontalLineTo(4.917f)
            verticalLineTo(33.688f)
            horizontalLineTo(17.208f)
            close()
            moveTo(50.617f, 45.979f)
            lineTo(41.201f, 36.564f)
            curveTo(39.235f, 37.842f, 36.924f, 38.604f, 34.417f, 38.604f)
            curveTo(27.632f, 38.604f, 22.125f, 33.097f, 22.125f, 26.313f)
            curveTo(22.125f, 19.528f, 27.632f, 14.021f, 34.417f, 14.021f)
            curveTo(41.201f, 14.021f, 46.708f, 19.528f, 46.708f, 26.313f)
            curveTo(46.708f, 28.82f, 45.946f, 31.131f, 44.668f, 33.073f)
            lineTo(54.083f, 42.513f)
            close()
            moveTo(41.792f, 26.313f)
            curveTo(41.792f, 22.256f, 38.473f, 18.938f, 34.417f, 18.938f)
            curveTo(30.36f, 18.938f, 27.042f, 22.256f, 27.042f, 26.313f)
            curveTo(27.042f, 30.369f, 30.36f, 33.688f, 34.417f, 33.688f)
            curveTo(38.473f, 33.688f, 41.792f, 30.369f, 41.792f, 26.313f)
            close()
            moveTo(4.917f, 45.979f)
            horizontalLineTo(29.5f)
            verticalLineTo(41.063f)
            horizontalLineTo(4.917f)
            close()
        }
    }.build()
}

private data class LegacyPath(
    val data: String,
    val color: Long,
    val fillType: PathFillType = PathFillType.NonZero,
)

private fun legacyVector(name: String, viewportWidth: Float, viewportHeight: Float, vararg paths: LegacyPath) =
    ImageVector.Builder(
        name = name,
        defaultWidth = viewportWidth.dp,
        defaultHeight = viewportHeight.dp,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
    ).apply {
        paths.forEach { spec ->
            addPath(
                pathData = PathParser().parsePathString(spec.data).toNodes(),
                pathFillType = spec.fillType,
                fill = SolidColor(Color(spec.color)),
            )
        }
    }.build()

private fun notificationCategoryIcon(name: String, foreground: String) = legacyVector(
    name,
    36f,
    36f,
    LegacyPath("M18,0L18,0A18,18 0,0 1,36 18L36,18A18,18 0,0 1,18 36L18,36A18,18 0,0 1,0 18L0,18A18,18 0,0 1,18 0z", 0xFF54BBAB),
    LegacyPath(foreground, 0xFFFFFFFF),
)
