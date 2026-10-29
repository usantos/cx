package br.gov.caixa.loterias.feature.cart

import android.view.View
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.lang.reflect.Proxy
import org.json.JSONArray
import org.json.JSONObject

private const val REFERENCE_CALLBACK_COUNT = 6
private val referenceHeaderColor = Color(0xFF005CA9)

@Composable
internal fun CartReferencePreview(cart: ShoppingCart) {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize().background(referenceHeaderColor).statusBarsPadding()) {
        Box(Modifier.fillMaxSize().background(Color.White).navigationBarsPadding()) {
            AndroidView(modifier = Modifier.fillMaxSize(), factory = { createCartReference(CartNativeReference(context), cart) })
        }
    }
}

internal fun betJson(item: CartItem) = JSONObject().apply {
    put("id", item.id)
    put("modalidade", item.modalityCode)
    put("concursoAlvo", item.contestNumber)
    put("valor", item.amount)
    put(
        "numerosSelecionados",
        if (item.modalityCode ==
            "SUPER_7"
        ) {
            JSONArray(item.selectedNumbers.map(::JSONArray))
        } else {
            JSONArray(item.selectedNumbers.flatten())
        }
    )
    put("quantidadeTeimosinhas", 0)
    put("quantidadeNumeros", item.numberQuantity)
    put("surpresinha", false)
    put("espelho", false)
}

internal fun createCartReference(reference: CartNativeReference, cart: ShoppingCart): View {
    val root = reference.inflate("activity_carrinho")
    root.fitsSystemWindows = false
    reference.text(root, "textCustom").text = "Carrinho de apostas"
    reference.text(root, "tvValorTotalCarrinho").text =
        java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR")).format(cart.total)
    reference.text(root, "tvQuantidadeCarrinho").text = cart.betCount.toString()
    root.findViewById<View>(reference.id("btn_avancar_pagamento")).isEnabled = true
    val cartDto = reference.dto(
        "CarrinhoDTO",
        JSONObject().apply {
            put("id", cart.id)
            put("valorTotal", cart.total)
            put("apostasIndividuais", JSONArray(cart.items.filter { it.kind == CartItemKind.INDIVIDUAL_BET }.map(::betJson)))
            put(
                "combos",
                JSONArray(
                    cart.items.filter { it.kind == CartItemKind.COMBO }.map { combo ->
                        JSONObject().apply {
                            put("id", combo.id)
                            put("tipoCombo", JSONObject().put("codigo", combo.id).put("nome", combo.title))
                            put(
                                "apostas",
                                JSONArray(
                                    List(combo.comboBetCount) {
                                        JSONObject().put(
                                            "id",
                                            it
                                        ).put("valor", if (it == 0) combo.amount else java.math.BigDecimal.ZERO)
                                    }
                                )
                            )
                        }
                    }
                )
            )
        }
    )
    val mapperType = reference.type("br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoMapper")
    val items = reference.call(mapperType.getField("INSTANCE").get(null), "map", cartDto)
    val function = reference.type("kotlin.jvm.functions.Function1")
    val unit = reference.type("kotlin.Unit").getField("INSTANCE").get(null)
    val callback = Proxy.newProxyInstance(function.classLoader, arrayOf(function)) { _, method, _ ->
        when (method.name) {
            "invoke" -> unit
            "hashCode" -> 0
            "equals" -> false
            else -> "Read-only reference callback"
        }
    }
    val adapterType = reference.type("br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoAdapter")
    val adapter = adapterType.constructors.single().newInstance(*Array(REFERENCE_CALLBACK_COUNT) { callback })
    val recycler = root.findViewById<View>(reference.id("listaCarrinho"))
    val manager = reference.type(
        "androidx.recyclerview.widget.LinearLayoutManager"
    ).getConstructor(android.content.Context::class.java).newInstance(reference.context)
    reference.call(recycler, "setLayoutManager", manager)
    reference.call(recycler, "setAdapter", adapter)
    reference.call(adapter, "submitList", items)
    root.post {
        android.util.Log.i(
            "CartReferenceMetrics",
            "addElevation=${root.findViewById<View>(
                reference.id("btn_nova_aposta")
            ).elevation}; payElevation=${root.findViewById<View>(reference.id("btn_avancar_pagamento")).elevation}"
        )
    }
    return root
}
