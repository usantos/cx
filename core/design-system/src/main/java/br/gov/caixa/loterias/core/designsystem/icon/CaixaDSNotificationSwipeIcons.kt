package br.gov.caixa.loterias.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** ic_carta_msglidas.xml legado exato; sem drawable XML em execução. */
val CaixaDSNotificationSwipeRead: ImageVector by lazy {
    ImageVector.Builder(name = "CaixaDSNotificationSwipeRead", defaultWidth = 26.dp, defaultHeight = 26.dp,
        viewportWidth = 26f, viewportHeight = 26f).apply {
        addPath(PathParser().parsePathString("M13,13m-13,0a13,13 0,1 1,26 0a13,13 0,1 1,-26 0").toNodes(), fill = SolidColor(Color(0xFF2E2565)))
        addPath(PathParser().parsePathString("M20.493,11.125C20.493,10.585 20.215,10.113 19.788,9.85L13,5.875L6.212,9.85C5.785,10.113 5.5,10.585 5.5,11.125V18.625C5.5,19.45 6.175,20.125 7,20.125H19C19.825,20.125 20.5,19.45 20.5,18.625L20.493,11.125ZM18.993,11.125V11.132L13,14.875L7,11.125L13,7.615L18.993,11.125ZM7,18.625V12.88L13,16.645L18.993,12.903L19,18.625H7Z").toNodes(), fill = SolidColor(Color(0xFFffffff)))
    }.build()
}

/** ic_lixeira_push.xml legado exato; sem drawable XML em execução. */
val CaixaDSNotificationSwipeDelete: ImageVector by lazy {
    ImageVector.Builder(name = "CaixaDSNotificationSwipeDelete", defaultWidth = 29.dp, defaultHeight = 29.dp,
        viewportWidth = 24f, viewportHeight = 24f).apply {
        addPath(PathParser().parsePathString("M12,12m-12,0a12,12 0,1 1,24 0a12,12 0,1 1,-24 0").toNodes(), fill = SolidColor(Color(0xFF900000)))
        addPath(PathParser().parsePathString("M14.769,9.923V16.846H9.231V9.923H14.769ZM13.731,5.769H10.269L9.577,6.461H7.154V7.846H16.846V6.461H14.423L13.731,5.769ZM16.154,8.538H7.846V16.846C7.846,17.608 8.469,18.231 9.231,18.231H14.769C15.531,18.231 16.154,17.608 16.154,16.846V8.538Z").toNodes(), fill = SolidColor(Color(0xFFffffff)))
    }.build()
}
