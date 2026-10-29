package br.gov.caixa.loterias.feature.betting

import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.TypefaceSpan
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetComboEntry
import java.text.NumberFormat
import java.util.Locale

private const val NATIVE_TITLE_SIZE = 16

/** O XML nativo instalado recebe dados controlados e as ligações dos holders legados. Nenhuma atividade ou serviço é iniciado. */
@Composable
internal fun ComboReferencePreview(combo: BetCombo, combos: List<BetCombo>, detail: Boolean) {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.Blue700).statusBarsPadding()) {
        Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White).navigationBarsPadding()) {
            AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                val oracle = ComboNativeOracle(context)
                val root = oracle.inflate(if (detail) "activity_detalhe_combo" else "activity_combos")
                root.fitsSystemWindows = false
                val toolbar = root.findViewById<View>(oracle.id("toolbar"))
                toolbar.background = oracle.drawable("navigation_gradient")
                call(toolbar, "setNavigationIcon", oracle.drawable("seta_esquerda"))
                call(
                    toolbar,
                    "setTitle",
                    SpannableString(oracle.string("titulo_combos")).apply {
                        setSpan(TypefaceSpan(oracle.font("caixa_std_bold")), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(AbsoluteSizeSpan(NATIVE_TITLE_SIZE, true), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                )
                call(toolbar, "inflateMenu", oracle.id("settings", "menu"))
                if (detail) bindDetail(root, oracle, combo) else bindCatalog(root, oracle, combos)
                root
            })
        }
    }
}

private fun money(value: java.math.BigDecimal) = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)

private fun bindDetail(root: View, oracle: ComboNativeOracle, combo: BetCombo) {
    oracle.text(root, "txtNomeDetalheCombo").text = combo.name
    oracle.text(root, "txtHeaderDetalheCombo").text = combo.description
    oracle.text(root, "txtValorCombo").text = money(combo.amount)
    oracle.text(root, "txtQtdDeApostas").text = "1"
    oracle.text(root, "btnAdicionarAoCarrinho").text = SpannableString("Adicionar\nao carrinho de apostas").apply {
        setSpan(TypefaceSpan(oracle.font("caixa_std_bold")), 0, "Adicionar".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    val recycler = root.findViewById<View>(oracle.id("rcvDetalheCombo"))
    val activity = oracle.nativeClass("br.gov.caixa.loterias.apostas.controllers.DetalheComboActivity")
    val grid = activity.getDeclaredMethod("getGridLayoutManager", activity, Integer::class.java).apply { isAccessible = true }
        .invoke(null, null, combo.entries.size)
    call(recycler, "setLayoutManager", grid)
    call(recycler, "setNestedScrollingEnabled", false)
    val adapter = oracle.nativeClass(
        "br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheComboAdapter"
    ).getConstructor().newInstance()
    call(recycler, "setAdapter", adapter)
    call(adapter, "submitList", combo.entries.map { nativeEntry(oracle, it) })
}

private fun call(receiver: Any, name: String, vararg args: Any?) = receiver.javaClass.methods.first {
    it.name == name && it.parameterCount == args.size && it.parameterTypes.zip(args).all { (type, value) ->
        value == null || type.isInstance(value) || (type == Integer.TYPE && value is Int) ||
            (type == java.lang.Boolean.TYPE && value is Boolean)
    }
}.invoke(receiver, *args)

private fun nativeDto(oracle: ComboNativeOracle, name: String, json: org.json.JSONObject): Any {
    val gson = oracle.nativeClass("com.google.gson.Gson").getConstructor().newInstance()
    return requireNotNull(
        call(gson, "fromJson", json.toString(), oracle.nativeClass("br.gov.caixa.loterias.apostas.model.bo.silce.dto.$name"))
    )
}

private fun entryJson(entry: BetComboEntry) = org.json.JSONObject().apply {
    put(
        "modalidade",
        org.json.JSONObject().apply {
            put("valor", entry.modality.id)
            put("descricao", entry.modality.name)
            put("descricaoGestor", entry.modality.name)
            put("descricaoEspecial", entry.modality.specialName)
        }
    )
    put("tipoConcurso", entry.contestType)
    put("quantidadeApostas", entry.betQuantity)
    put("quantidadeTeimosinhas", entry.recurringDraws)
    put("quantidadeDezenas", entry.numberQuantity)
    put("quantidadeDezenasTrevos", 0)
    put("numero", entry.contestNumber)
}

private fun nativeEntry(oracle: ComboNativeOracle, entry: BetComboEntry) = nativeDto(oracle, "ModalidadeComboDTO", entryJson(entry))

private fun bindCatalog(root: View, oracle: ComboNativeOracle, combos: List<BetCombo>) {
    val recycler = root.findViewById<View>(oracle.id("rcv_lista_combos"))
    val grid = oracle.nativeClass("androidx.recyclerview.widget.GridLayoutManager")
        .getConstructor(android.content.Context::class.java, Integer.TYPE).newInstance(oracle.themed, 2)
    call(recycler, "setLayoutManager", grid)
    val referenceActivity = object : android.app.Activity() {
        override fun getResources() = oracle.themed.resources
        override fun getTheme() = oracle.themed.theme
    }
    val values = combos.map { combo ->
        nativeDto(
            oracle,
            "CombosDTO",
            org.json.JSONObject().apply {
                put("tipoCombo", org.json.JSONObject().put("codigo", combo.id).put("nome", combo.name))
                put("valor", combo.amount)
                put("modalidadesCombo", org.json.JSONArray(combo.entries.map(::entryJson)))
            }
        )
    }
    val adapter = oracle.nativeClass("br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaComboAdapter").constructors.single()
        .newInstance(values, referenceActivity, null)
    call(recycler, "setAdapter", adapter)
}
