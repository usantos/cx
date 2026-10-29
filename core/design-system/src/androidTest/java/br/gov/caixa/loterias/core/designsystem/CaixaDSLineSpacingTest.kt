package br.gov.caixa.loterias.core.designsystem

import android.widget.TextView
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSLineHeight
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily

class CaixaDSLineSpacingTest {
    @get:Rule val compose = createComposeRule()

    @Test fun filterHelpBaselineSpacingMatchesNativeTextViewExtraFourDp() {
        lateinit var native: TextView
        lateinit var layout: TextLayoutResult
        compose.setContent {
            val context = LocalContext.current
            native = TextView(context).apply {
                text = "Primeira linha\nSegunda linha\nTerceira linha"
                typeface = ResourcesCompat.getFont(context, R.font.caixa_std_regular)
                textSize = 12f
                setLineSpacing(4f * resources.displayMetrics.density, 1f)
                measure(android.view.View.MeasureSpec.makeMeasureSpec(800, android.view.View.MeasureSpec.EXACTLY),
                    android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED))
                layout(0,0,measuredWidth,measuredHeight)
            }
            Text("Primeira linha\nSegunda linha\nTerceira linha", fontFamily = CaixaDSFontFamily,
                fontSize = 12.sp, style = TextStyle(lineHeight = caixaDSLineHeight(12.sp, 4.dp),
                    platformStyle = PlatformTextStyle(includeFontPadding = true)),
                onTextLayout = { layout = it })
        }
        compose.runOnIdle {
            val expected = native.layout.getLineBaseline(1) - native.layout.getLineBaseline(0)
            assertEquals(expected.toFloat(), layout.getLineBaseline(1) - layout.getLineBaseline(0), 1f)
        }
    }
}
