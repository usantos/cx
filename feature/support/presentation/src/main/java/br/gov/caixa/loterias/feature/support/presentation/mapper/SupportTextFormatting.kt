package br.gov.caixa.loterias.feature.support.presentation.mapper

import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily

internal fun String.legacyCapitalized(): String = lowercase().replaceFirstChar { it.titlecase() }

internal fun supportRegularStyle(size: Int) = TextStyle(fontFamily = CaixaDSRegularFontFamily,
    fontSize = size.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))
