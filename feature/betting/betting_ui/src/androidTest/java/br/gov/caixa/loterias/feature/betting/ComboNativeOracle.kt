package br.gov.caixa.loterias.feature.betting

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

/** XML, fontes e cores somente para leitura do aplicativo legado instalado. */
internal class ComboNativeOracle(target: Context) {
    private val packageName = "br.gov.caixa.loterias.apostas.cxv2validation"
    private val native = target.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)
    val themed = ContextThemeWrapper(native, id("AppTheme.NoActionBar", "style"))
    init {
        // Somente as classes de referência neste processo de teste recebem o aplicativo de teste isolado.
        // O aplicativo legado instalado nunca é iniciado e suas preferências nunca são gravadas.
        nativeClass("br.gov.caixa.loterias.apostas.utils.Aplicacao").getField("application")
            .set(null, target.applicationContext)
    }
    fun nativeClass(name: String): Class<*> = native.classLoader.loadClass(name)
    fun id(name: String, type: String = "id") = native.resources.getIdentifier(name, type, packageName)
    fun string(name: String) = native.getString(id(name, "string")).replace("_", "")
    fun font(name: String) = native.resources.getFont(id(name, "font"))
    fun drawable(name: String) = requireNotNull(native.getDrawable(id(name, "drawable")))
    fun color(name: String) = native.getColor(id(name, "color"))
    fun inflate(name: String, parent: ViewGroup? = null): View = LayoutInflater.from(themed).inflate(id(name, "layout"), parent, false)
    fun measure(view: View, width: Int, height: Int? = null) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            if (height == null) View.MeasureSpec.UNSPECIFIED else View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
    fun text(view: View, name: String): TextView = view.findViewById(id(name))
    fun probe(width: Int, height: Int): String {
        val catalog = inflate("activity_combos")
        measure(catalog, width, height)
        val description = text(catalog, "txt_combos_orientacoes_top")
        val detail = inflate("activity_detalhe_combo")
        text(detail, "txtNomeDetalheCombo").text = "Combo da Sorte"
        text(detail, "txtValorCombo").text = "R$ 20,50"
        text(detail, "txtQtdDeApostas").text = "1"
        measure(detail, width, height)
        val check = detail.findViewById<CheckBox>(id("checkBoxNumerosEscolhidos"))
        val scale = native.resources.displayMetrics.scaledDensity
        return "headerHeight=${description.height}; headerLineHeight=${description.lineHeight / scale}; " +
            "headerExtra=${description.lineSpacingExtra / scale}; checkText=${check.textSize / scale}; " +
            "checkInset=${check.compoundPaddingLeft}; checkPadding=${check.compoundDrawablePadding}; " +
            listOf(
                "txtNomeDetalheCombo",
                "txtHeaderDetalheCombo",
                "txtValorCombo",
                "txtQtdApostas",
                "txtQtdDeApostas"
            ).joinToString { name ->
                val t = text(detail, name)
                "$name=${t.width}x${t.height};font=${t.textSize / scale};color=${t.currentTextColor}"
            }
    }
}
