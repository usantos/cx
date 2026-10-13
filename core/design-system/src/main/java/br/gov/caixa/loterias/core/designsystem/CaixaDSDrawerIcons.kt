package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

// Path data and viewports preserved from the corresponding legacy drawable resources.

private val DrawerResponsibleVector = ImageVector.Builder(
    name = "CaixaDS.DrawerResponsible", defaultWidth = 25.dp, defaultHeight = 24.dp,
    viewportWidth = 25f, viewportHeight = 24f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M6.2873,10.1671L5,10.1671L5,8.3317L8.6235,8.3317L8.6235,16.6683L6.2873,16.6683L6.2873,10.1671ZM13.7131,9.7801C13.5224,9.5958 13.2919,9.5037 13.0218,9.5037C12.7516,9.5037 12.5192,9.5958 12.3245,9.7801C12.1298,9.9644 12.0324,10.1855 12.0324,10.4435C12.0324,10.7015 12.1298,10.9226 12.3245,11.1069C12.5192,11.2912 12.7516,11.3833 13.0218,11.3833C13.2919,11.3833 13.5244,11.2912 13.719,11.1069C13.9137,10.9226 14.0111,10.7015 14.0111,10.4435C14.0111,10.1855 13.9117,9.9644 13.7131,9.7801ZM13.7727,13.473C13.5661,13.2666 13.3158,13.1634 13.0218,13.1634C12.7277,13.1634 12.4755,13.2647 12.2649,13.4674C12.0543,13.6701 11.949,13.9152 11.949,14.2027C11.949,14.4828 12.0543,14.7224 12.2649,14.9214C12.4755,15.1204 12.7277,15.2199 13.0218,15.2199C13.3158,15.2199 13.5681,15.1204 13.7786,14.9214C13.9892,14.7224 14.0945,14.4828 14.0945,14.2027C14.0945,13.9152 13.9872,13.672 13.7727,13.473ZM10.8643,8.6302C11.4683,8.2101 12.1874,8 13.0218,8C13.8561,8 14.5753,8.2082 15.1792,8.6247C15.7831,9.0412 16.085,9.6253 16.085,10.3772C16.085,10.812 15.9519,11.2045 15.6857,11.5547C15.4195,11.9048 15.0798,12.1425 14.6666,12.2678L14.6666,12.2899C15.2149,12.4595 15.65,12.734 15.9718,13.1136C16.2936,13.4932 16.4545,13.9484 16.4545,14.4791C16.4545,15.0246 16.2718,15.4945 15.9063,15.8888C15.5407,16.2832 15.1057,16.567 14.6011,16.7402C14.0965,16.9134 13.5701,17 13.0218,17C12.4735,17 11.947,16.9134 11.4424,16.7402C10.9378,16.567 10.5028,16.2832 10.1373,15.8888C9.7717,15.4945 9.589,15.0246 9.589,14.4791C9.589,13.9484 9.7499,13.4932 10.0717,13.1136C10.3935,12.734 10.8286,12.4595 11.3769,12.2899L11.3769,12.2678C10.9637,12.1425 10.624,11.9048 10.3578,11.5547C10.0916,11.2045 9.9585,10.812 9.9585,10.3772C9.9585,9.6253 10.2604,9.043 10.8643,8.6302Z").toNodes(),
        fill = SolidColor(Color(0xFF54BBAB)),
        stroke = SolidColor(Color(0x00000000)), strokeLineWidth = 1f,
    )
    addPath(
        pathData = PathParser().parsePathString("M20.8853,16l0,-2.904l-2.8853,0l0,-1.2136l2.8853,0l0,-2.8824l1.2293,0l0,2.8824l2.8853,0l0,1.2136l-2.8853,0l0,2.904z").toNodes(),
        fill = SolidColor(Color(0xFF54BBAB)),
        stroke = SolidColor(Color(0x00000000)), strokeLineWidth = 1f,
    )
    addPath(
        pathData = PathParser().parsePathString("M20.133,16.0178C19.6959,16.9029 19.1181,17.7079 18.4129,18.4131C14.8769,21.9491 9.1233,21.9491 5.5872,18.413C2.0511,14.8769 2.051,9.1233 5.5871,5.5872C9.1232,2.0511 14.8767,2.0513 18.4128,5.5873C19.1181,6.2926 19.696,7.0976 20.1329,7.9827L21.56,6.5556C21.0754,5.7049 20.4787,4.9225 19.7782,4.222C15.4893,-0.0669 8.5107,-0.0669 4.2218,4.222C-0.0671,8.5109 -0.067,15.4895 4.2218,19.7784C8.5107,24.0673 15.4893,24.0673 19.7782,19.7784C20.4786,19.0779 21.0754,18.2955 21.56,17.4448L20.133,16.0178Z").toNodes(),
        fill = SolidColor(Color(0xFF54BBAB)),
        stroke = SolidColor(Color(0xFFFFFFFF)), strokeLineWidth = 1f,
    )
}.build()

val CaixaDSIcons.DrawerResponsible: ImageVector get() = DrawerResponsibleVector

private val DrawerTermsVector = ImageVector.Builder(
    name = "CaixaDS.DrawerTerms", defaultWidth = 13.dp, defaultHeight = 16.dp,
    viewportWidth = 13f, viewportHeight = 16f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M6.215,2.826C7.419,2.826 8.623,2.829 9.826,2.822C9.968,2.822 9.999,2.864 9.991,2.994C9.98,3.199 9.98,3.405 9.991,3.61C9.997,3.728 9.96,3.754 9.846,3.754C8.698,3.749 7.55,3.751 6.403,3.751C5.132,3.751 3.862,3.751 2.591,3.754C2.462,3.754 2.427,3.719 2.433,3.592C2.444,3.387 2.444,3.181 2.433,2.976C2.427,2.852 2.465,2.823 2.586,2.824C3.796,2.828 5.006,2.826 6.215,2.826Z").toNodes(),
        fill = SolidColor(Color(0xFF59B8A9)),
    )
    addPath(
        pathData = PathParser().parsePathString("M0,15.752C0.002,15.691 0.007,15.629 0.007,15.568C0.007,10.443 0.006,5.319 0.004,0.194C0.003,0.028 0.053,0 0.206,0C4.213,0.005 8.221,0.005 12.228,0C12.393,0 12.423,0.048 12.423,0.201C12.418,4.255 12.418,8.309 12.423,12.362C12.425,12.414 12.416,12.467 12.396,12.515C12.376,12.564 12.346,12.607 12.307,12.643C11.269,13.677 10.233,14.714 9.198,15.752H0ZM0.925,14.673C0.925,14.8 0.955,14.832 1.084,14.832C3.457,14.828 5.83,14.828 8.203,14.832C8.318,14.832 8.345,14.805 8.344,14.691C8.34,13.742 8.344,12.794 8.338,11.846C8.338,11.703 8.379,11.682 8.509,11.682C9.447,11.687 10.385,11.682 11.323,11.688C11.467,11.688 11.498,11.647 11.497,11.51C11.494,8.041 11.494,4.571 11.497,1.101C11.497,0.968 11.473,0.923 11.326,0.923C7.918,0.928 4.51,0.928 1.101,0.923C0.965,0.923 0.925,0.952 0.925,1.096C0.93,3.351 0.932,5.606 0.929,7.861C0.929,10.132 0.928,12.403 0.925,14.673ZM9.352,12.605C9.255,12.605 9.263,12.661 9.263,12.724C9.263,13.226 9.263,13.727 9.263,14.229C9.263,14.265 9.241,14.309 9.276,14.334C9.849,13.762 10.419,13.192 11.002,12.607C10.447,12.607 9.9,12.609 9.353,12.605H9.352Z").toNodes(),
        fill = SolidColor(Color(0xFF59B8A9)),
    )
    addPath(
        pathData = PathParser().parsePathString("M6.215,5.117C7.419,5.117 8.623,5.12 9.826,5.114C9.968,5.114 9.999,5.155 9.991,5.285C9.98,5.491 9.98,5.696 9.991,5.902C9.997,6.02 9.96,6.045 9.846,6.045C8.698,6.041 7.55,6.042 6.403,6.042C5.132,6.042 3.862,6.04 2.591,6.045C2.462,6.045 2.427,6.01 2.433,5.884C2.444,5.678 2.444,5.473 2.433,5.267C2.427,5.144 2.465,5.114 2.586,5.115C3.796,5.119 5.006,5.117 6.215,5.117Z").toNodes(),
        fill = SolidColor(Color(0xFF59B8A9)),
    )
    addPath(
        pathData = PathParser().parsePathString("M6.199,8.009C4.995,8.009 3.791,8.007 2.587,8.011C2.468,8.011 2.428,7.984 2.435,7.86C2.445,7.655 2.445,7.449 2.435,7.244C2.428,7.12 2.461,7.079 2.593,7.079C3.171,7.087 3.75,7.083 4.329,7.083C6.158,7.083 7.987,7.082 9.816,7.08C9.957,7.08 10.002,7.113 9.992,7.255C9.981,7.455 9.981,7.655 9.992,7.854C9.998,7.973 9.967,8.009 9.843,8.009C8.741,8.003 7.64,8.006 6.538,8.006L6.199,8.009Z").toNodes(),
        fill = SolidColor(Color(0xFF59B8A9)),
    )
    addPath(
        pathData = PathParser().parsePathString("M6.214,9.054C7.418,9.054 8.622,9.057 9.826,9.051C9.967,9.051 9.999,9.089 9.99,9.222C9.979,9.426 9.979,9.632 9.99,9.836C9.996,9.953 9.96,9.981 9.846,9.98C8.749,9.976 7.653,9.977 6.557,9.977C5.235,9.977 3.913,9.977 2.591,9.98C2.463,9.98 2.427,9.946 2.433,9.819C2.444,9.614 2.444,9.408 2.433,9.202C2.427,9.079 2.464,9.049 2.585,9.049C3.796,9.056 5.005,9.054 6.214,9.054Z").toNodes(),
        fill = SolidColor(Color(0xFF59B8A9)),
    )
}.build()

val CaixaDSIcons.DrawerTerms: ImageVector get() = DrawerTermsVector

private val DrawerQuestionsVector = ImageVector.Builder(
    name = "CaixaDS.DrawerQuestions", defaultWidth = 9.dp, defaultHeight = 12.dp,
    viewportWidth = 9f, viewportHeight = 12f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M4.135,0C2.264,0 1.052,0.766 0.101,2.133C-0.071,2.381 -0.018,2.721 0.223,2.904L1.234,3.671C1.477,3.855 1.823,3.812 2.013,3.573C2.6,2.838 3.036,2.414 3.953,2.414C4.674,2.414 5.565,2.878 5.565,3.578C5.565,4.106 5.129,4.378 4.417,4.777C3.587,5.242 2.488,5.821 2.488,7.271V7.5C2.488,7.811 2.74,8.063 3.051,8.063H4.749C5.06,8.063 5.312,7.811 5.312,7.5V7.365C5.312,6.36 8.248,6.318 8.248,3.6C8.248,1.553 6.124,0 4.135,0ZM3.9,8.753C3.005,8.753 2.277,9.481 2.277,10.377C2.277,11.272 3.005,12 3.9,12C4.795,12 5.524,11.272 5.524,10.377C5.524,9.481 4.795,8.753 3.9,8.753Z").toNodes(),
        fill = SolidColor(Color(0xFF59B8A9)),
    )
}.build()

val CaixaDSIcons.DrawerQuestions: ImageVector get() = DrawerQuestionsVector

private val DrawerLogoutVector = ImageVector.Builder(
    name = "CaixaDS.DrawerLogout", defaultWidth = 29.dp, defaultHeight = 29.dp,
    viewportWidth = 29f, viewportHeight = 29f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M11.226,19.934L13.458,22.167L21.375,14.25L13.458,6.333L11.226,8.566L15.311,12.667H0V15.833H15.311L11.226,19.934ZM25.333,0H3.167C1.409,0 0,1.425 0,3.167V9.5H3.167V3.167H25.333V25.333H3.167V19H0V25.333C0,27.075 1.409,28.5 3.167,28.5H25.333C27.075,28.5 28.5,27.075 28.5,25.333V3.167C28.5,1.425 27.075,0 25.333,0Z").toNodes(),
        fill = SolidColor(Color(0xFFffffff)),
    )
}.build()

val CaixaDSIcons.DrawerLogout: ImageVector get() = DrawerLogoutVector

private val DrawerChevronVector = ImageVector.Builder(
    name = "CaixaDS.DrawerChevron", defaultWidth = 16.dp, defaultHeight = 10.dp,
    viewportWidth = 16f, viewportHeight = 10f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M2.172,0L8,5.729L13.828,0L16,2.135L8,10L0,2.135L2.172,0Z").toNodes(),
        fill = SolidColor(Color(0xFF005CA9)),
    )
}.build()

val CaixaDSIcons.DrawerChevron: ImageVector get() = DrawerChevronVector

private val DrawerLinkVector = ImageVector.Builder(
    name = "CaixaDS.DrawerLink", defaultWidth = 20.dp, defaultHeight = 10.dp,
    viewportWidth = 20f, viewportHeight = 10f,
).apply {
    addPath(
        pathData = PathParser().parsePathString("M15,0H11V2H15C16.65,2 18,3.35 18,5C18,6.65 16.65,8 15,8H11V10H15C17.76,10 20,7.76 20,5C20,2.24 17.76,0 15,0ZM9,8H5C3.35,8 2,6.65 2,5C2,3.35 3.35,2 5,2H9V0H5C2.24,0 0,2.24 0,5C0,7.76 2.24,10 5,10H9V8ZM6,4H14V6H6V4Z").toNodes(),
        fill = SolidColor(Color(0xFF005CA9)),
    )
}.build()

val CaixaDSIcons.DrawerLink: ImageVector get() = DrawerLinkVector
