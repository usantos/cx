package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Trevo do legado: geometria de Mega-Sena e cores das dez modalidades. */
fun caixaDSLegacyLotteryClover(modality: String): ImageVector =
    legacyLotteryClovers[modality] ?: legacyLotteryClovers.getValue("megasena")

/** Backend ModalidadeEnum IDs shared by catalog, favorites and Rapidão. */
fun caixaDSLegacyLotteryClover(modalityId: Int): ImageVector = caixaDSLegacyLotteryClover(
    when (modalityId) {
        2 -> "megasena"
        3 -> "quina"
        7 -> "supersete"
        8 -> "lotofacil"
        9 -> "maismilionaria"
        11 -> "diadesorte"
        16 -> "lotomania"
        18 -> "duplasena"
        19 -> "loteca"
        20 -> "timemania"
        else -> "megasena"
    },
)

private fun lotteryClover(accent: Color, foreground: Color = Color.White): ImageVector =
    ImageVector.Builder(
        name = "LegacyLotteryClover", defaultWidth = 57.dp, defaultHeight = 57.dp,
        viewportWidth = 57f, viewportHeight = 57f,
    ).apply {
        addPath(PathParser().parsePathString("M27.146,48.257C27.146,53.007 23.157,57 18.412,57C13.667,57 9.679,53.007 9.679,48.257V47.311H8.734C3.989,47.311 0,43.317 0,38.567C0,33.817 3.989,29.845 8.734,29.845H27.146V48.257Z").toNodes(), fill = SolidColor(accent))
        addPath(PathParser().parsePathString("M27.146,8.743C27.146,3.993 23.157,0 18.412,0C13.667,0 9.679,3.993 9.679,8.743V9.689H8.734C3.989,9.689 0,13.682 0,18.433C0,23.183 3.989,27.155 8.734,27.155H27.146V8.743Z").toNodes(), fill = SolidColor(foreground))
        addPath(PathParser().parsePathString("M29.854,48.257C29.854,53.007 33.843,57 38.588,57C43.332,57 47.321,53.007 47.321,48.257V47.311H48.266C53.011,47.311 57,43.317 57,38.567C57,33.817 53.011,29.845 48.266,29.845H29.854V48.257Z").toNodes(), fill = SolidColor(foreground))
        addPath(PathParser().parsePathString("M29.854,8.743C29.854,3.993 33.843,0 38.588,0C43.332,0 47.321,3.993 47.321,8.743V9.689H48.266C53.011,9.689 57,13.682 57,18.433C57,23.183 53.011,27.155 48.266,27.155H29.854V8.743Z").toNodes(), fill = SolidColor(accent))
    }.build()

private val legacyLotteryClovers = mapOf(
    "megasena" to lotteryClover(Color(0xFF81CA9C)),
    "quina" to lotteryClover(Color(0xFF8781BD)),
    "duplasena" to lotteryClover(Color(0xFFCC8D88)),
    "lotofacil" to lotteryClover(Color(0xFFBC8CBF)),
    "lotomania" to lotteryClover(Color(0xFFFDC689)),
    "maismilionaria" to lotteryClover(Color(0xFF8A81AF)),
    "supersete" to lotteryClover(Color(0xFFDDE89A)),
    "loteca" to lotteryClover(Color(0xFFF69679)),
    "timemania" to lotteryClover(Color(0xFF80CBA4), Color(0xFF038141)),
    "diadesorte" to lotteryClover(Color(0xFFF3DC93)),
)

/** Seta de app_bar_custom.xml. */
val CaixaDSLegacyCustomBack: ImageVector = ImageVector.Builder(
    name = "LegacyCustomBack", defaultWidth = 15.dp, defaultHeight = 23.dp,
    viewportWidth = 15f, viewportHeight = 23f,
).apply {
    addPath(
        PathParser().parsePathString("M14.05,3L6,11.051L14.05,19.101L11.05,22.101L0,11.051L11.05,0L14.05,3Z").toNodes(),
        fill = SolidColor(Color.White),
    )
}.build()
