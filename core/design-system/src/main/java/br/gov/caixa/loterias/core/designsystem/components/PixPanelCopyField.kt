package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixCopyIcon
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun PixPanelCopyField(copyPaste: String, expired: Boolean, copiedRecently: Boolean, expiredLabel: String, onCopy: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp).height(40.dp)
            .background(if (expired) LoteriasCaixaDS.colors.disabledControl else LoteriasCaixaDSColorTokens.White, RoundedCornerShape(4.dp))
            .border(
                1.2.dp,
                if (expired) LoteriasCaixaDS.colors.disabledControl else LoteriasCaixaDSColorTokens.PixOutline,
                RoundedCornerShape(4.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (expired) {
                expiredLabel
            } else {
                copyPaste.take(COPY_CODE_PREVIEW_LENGTH) +
                    if (copyPaste.length > COPY_CODE_PREVIEW_LENGTH) uiStrings.getString(LocalizedR.string.ui_texto_ab5df6) else ""
            },
            Modifier.weight(1f).padding(start = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (expired) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDS.colors.graphite,
            style = pixPanelStyle
        )
        if (!expired) {
            Box(
                Modifier.width(60.dp).fillMaxSize()
                    .background(if (copiedRecently) LoteriasCaixaDS.colors.pix else LoteriasCaixaDS.colors.pixCopyButton)
                    .uiId("ds.0045").clickable(enabled = !copiedRecently) {
                        onCopy()
                    },
                contentAlignment = Alignment.Center
            ) {
                LoteriasCaixaDSPixCopyIcon(copiedRecently, (Modifier.size(if (copiedRecently) 24.dp else 15.dp)).uiId("ds.0046"))
            }
        }
    }
}

private const val COPY_CODE_PREVIEW_LENGTH = 19
