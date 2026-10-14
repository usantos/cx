package br.gov.caixa.loterias.feature.betting

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/** Read-only installed CX resources/controller; avoids rebuilding legacy or contacting services. */
internal class LotecaNativeOracle(target: Context) {
    private val packageName = "br.gov.caixa.loterias.apostas.cxv2validation"
    private val native = target.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)
    private val context = ContextThemeWrapper(native, id("Theme.SimulaMaterial", "style"))
    private fun id(name: String, type: String = "id") = native.resources.getIdentifier(name, type, packageName)
    private fun inflate(name: String): View = LayoutInflater.from(context).inflate(id(name,"layout"),null)
    private fun measure(view: View, width: Int) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),0)
        view.layout(0,0,view.measuredWidth,view.measuredHeight)
    }
    private fun absoluteTop(view: View): Int = view.top + ((view.parent as? View)?.let(::absoluteTop) ?: 0)

    fun counterOffsetsFromTitleBottom(width: Int): List<Int> {
        val toolbar = inflate("loteca_toolbar")
        measure(toolbar,width)
        val title = toolbar.findViewById<TextView>(id("txtSuasApostas"))
        return listOf("grupoPartidas","grupoSimples","grupoDuplas","grupoTriplas").mapIndexed { index, name ->
            val group = toolbar.findViewById<ViewGroup>(id(name))
            absoluteTop(group.getChildAt(if(index == 0) 0 else 1)) - title.bottom
        }
    }

    inner class Row(initialHomeSelected: Boolean = false, awayIndicator: Boolean = false) {
        val view = inflate("item_loteca_novo")
        private val loader = native.classLoader
        private val teamClass = loader.loadClass("br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe")
        private val matchClass = loader.loadClass("br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida")
        private val holderClass = loader.loadClass("br.gov.caixa.loterias.apostas.view.holder.LotecaHolder")
        private fun team(name: String, uf: String, indicator: Boolean): Any = teamClass.getConstructor().newInstance().apply {
            teamClass.getMethod("setNome",String::class.java).invoke(this,name)
            teamClass.getMethod("setUf",String::class.java).invoke(this,uf)
            teamClass.getMethod("setSiglaPais",String::class.java).invoke(this,"BRA")
            teamClass.getMethod("setIndicadorSelecao",java.lang.Boolean::class.java).invoke(this,indicator)
        }
        private val home = team("BAHIA","BA",false)
        private val away = team("FLAMENGO","RJ",awayIndicator)
        private val match = matchClass.getConstructor().newInstance().apply {
            matchClass.getMethod("setEquipe1",teamClass).invoke(this,home)
            matchClass.getMethod("setEquipe2",teamClass).invoke(this,away)
        }
        private val holder = holderClass.getConstructor(View::class.java).newInstance(view)
        init { select(initialHomeSelected,false); bindDescriptions() }
        fun select(homeSelected: Boolean, awaySelected: Boolean) {
            teamClass.getMethod("setSelecionado",Boolean::class.javaPrimitiveType).invoke(home,homeSelected)
            teamClass.getMethod("setSelecionado",Boolean::class.javaPrimitiveType).invoke(away,awaySelected)
            holderClass.getMethod("bindEstadoVisual",Context::class.java,matchClass).invoke(holder,context,match)
        }
        fun bindDescriptions() {
            // Actual bindTextos is the description part of bind; no shield/network work is needed.
            holderClass.getDeclaredMethod("bindTextos",matchClass).apply { isAccessible=true }.invoke(holder,match)
        }
        fun height(width: Int): Int { measure(view,width); return view.findViewById<View>(id("time_um")).height }
        fun description(home: Boolean) = view.findViewById<TextView>(id(if(home) "desc_time_um" else "desc_time_dois"))
    }
}
