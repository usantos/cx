package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Original CX vector paths expressed as Compose ImageVectors. */
val CaixaDSCurrentCartIcon: ImageVector = ImageVector.Builder("CaixaDSCurrentCartIcon", 36.dp, 32.dp, 36f, 32f).apply {
    addPath(PathParser().parsePathString("M11.599,21.334H11.601C11.602,21.334 11.603,21.333 11.605,21.333H30.727C31.197,21.333 31.611,21.018 31.741,20.56L35.959,5.626C36.05,5.304 35.987,4.958 35.787,4.691C35.588,4.424 35.276,4.267 34.945,4.267H9.166L8.413,0.835C8.305,0.347 7.877,0 7.383,0H1.055C0.472,0 0,0.478 0,1.067C0,1.656 0.472,2.133 1.055,2.133H6.537C6.67,2.741 10.145,18.554 10.345,19.464C9.224,19.957 8.438,21.087 8.438,22.4C8.438,24.165 9.857,25.6 11.602,25.6H30.727C31.309,25.6 31.781,25.123 31.781,24.534C31.781,23.944 31.309,23.467 30.727,23.467H11.602C11.02,23.467 10.547,22.988 10.547,22.4C10.547,21.813 11.019,21.335 11.599,21.334ZM33.547,6.4L29.931,19.2H12.448L9.635,6.4H33.547Z").toNodes(), fill = SolidColor(Color(0xFF404B52)))
    addPath(PathParser().parsePathString("M10.547,28.8C10.547,30.564 11.966,32 13.711,32C15.456,32 16.875,30.564 16.875,28.8C16.875,27.035 15.456,25.6 13.711,25.6C11.966,25.6 10.547,27.035 10.547,28.8ZM13.711,27.733C14.292,27.733 14.766,28.212 14.766,28.8C14.766,29.388 14.292,29.867 13.711,29.867C13.13,29.867 12.656,29.388 12.656,28.8C12.656,28.212 13.13,27.733 13.711,27.733Z").toNodes(), fill = SolidColor(Color(0xFF404B52)))
    addPath(PathParser().parsePathString("M25.453,28.8C25.453,30.564 26.873,32 28.617,32C30.362,32 31.781,30.564 31.781,28.8C31.781,27.035 30.362,25.6 28.617,25.6C26.873,25.6 25.453,27.035 25.453,28.8ZM28.617,27.733C29.199,27.733 29.672,28.212 29.672,28.8C29.672,29.388 29.199,29.867 28.617,29.867C28.036,29.867 27.563,29.388 27.563,28.8C27.563,28.212 28.036,27.733 28.617,27.733Z").toNodes(), fill = SolidColor(Color(0xFF404B52)))
}.build()

val CaixaDSCurrentCartDeleteIcon: ImageVector = ImageVector.Builder("CaixaDSCurrentCartDeleteIcon", 20.dp, 20.dp, 20f, 20f).apply {
    addPath(PathParser().parsePathString("M13.333,7.5V15.833H6.667V7.5H13.333ZM12.083,2.5H7.917L7.083,3.333H4.167V5H15.833V3.333H12.917L12.083,2.5ZM15,5.833H5V15.833C5,16.75 5.75,17.5 6.667,17.5H13.333C14.25,17.5 15,16.75 15,15.833V5.833Z").toNodes(), fill = SolidColor(Color(0xFF0066B3)))
}.build()
