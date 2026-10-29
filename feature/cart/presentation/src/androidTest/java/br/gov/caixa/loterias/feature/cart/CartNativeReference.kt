package br.gov.caixa.loterias.feature.cart

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

/** APK de referência somente para leitura; suas classes recebem apenas o aplicativo de teste deste processo de instrumentação. */
internal class CartNativeReference(target: Context) {
    private val packageName = "br.gov.caixa.loterias.apostas.cxv2validation"
    private val native = try {
        target.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)
    } catch (missing: android.content.pm.PackageManager.NameNotFoundException) {
        org.junit.Assume.assumeNoException("Install reference APK cxv2validation to run native visual comparisons", missing)
        throw missing
    }
    val context = ContextThemeWrapper(native, id("AppTheme.NoActionBar", "style"))
    init {
        type("br.gov.caixa.loterias.apostas.utils.Aplicacao").getField("application").set(
            null,
            object : android.app.Application() {
                override fun getBaseContext(): Context = context
                override fun getResources() = context.resources
                override fun getApplicationContext(): Context = target.applicationContext
            }
        )
    }
    fun type(name: String): Class<*> = native.classLoader.loadClass(name)
    fun id(name: String, kind: String = "id") = native.resources.getIdentifier(name, kind, packageName)
    fun inflate(name: String): View = LayoutInflater.from(context).inflate(id(name, "layout"), null, false)
    fun text(root: View, name: String): TextView = root.findViewById(id(name))
    fun color(name: String) = native.getColor(id(name, "color"))
    fun call(receiver: Any, name: String, vararg args: Any?): Any? = receiver.javaClass.methods.first {
        it.name == name && it.parameterCount == args.size && it.parameterTypes.zip(args).all { (type, value) ->
            value == null || type.isInstance(value) || (type == Integer.TYPE && value is Int) ||
                (type == java.lang.Boolean.TYPE && value is Boolean)
        }
    }.invoke(receiver, *args)
    fun dto(name: String, json: org.json.JSONObject): Any {
        val gson = type("com.google.gson.Gson").getConstructor().newInstance()
        return requireNotNull(call(gson, "fromJson", json.toString(), type("br.gov.caixa.loterias.apostas.model.bo.silce.dto.$name")))
    }
    fun modalityColors(): Map<String, Int> {
        val codes =
            listOf(
                "MEGA_SENA", "QUINA", "LOTOFACIL", "LOTOMANIA", "DUPLA_SENA",
                "SUPER_7", "TIMEMANIA", "DIA_DE_SORTE", "MAIS_MILIONARIA", "LOTECA"
            )
        return codes.associateWith { code ->
            val item = br.gov.caixa.loterias.feature.cart.domain.model.CartItem(
                1,
                br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind.INDIVIDUAL_BET,
                code,
                "",
                java.math.BigDecimal.ONE,
                modalityCode = code
            )
            text(betRow(item), "tvModalidade").currentTextColor
        }
    }

    fun betRow(item: br.gov.caixa.loterias.feature.cart.domain.model.CartItem): View {
        val row = inflate("item_aposta_carrinho")
        val function = type("kotlin.jvm.functions.Function1")
        val unit = type("kotlin.Unit").getField("INSTANCE").get(null)
        val callback = java.lang.reflect.Proxy.newProxyInstance(function.classLoader, arrayOf(function)) { _, _, _ -> unit }
        val holder = type(
            "br.gov.caixa.loterias.apostas.novo.features.carrinho.ApostaViewHolder"
        ).constructors.single().newInstance(row, callback)
        val data = type("br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoItem\$Aposta").constructors.single()
            .newInstance(dto("IdentificaoDeUmaApostaDas8Modalidades", betJson(item)))
        call(holder, "bind", data)
        return row
    }
    fun measure(view: View, width: Int, height: Int? = null) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            if (height == null) View.MeasureSpec.UNSPECIFIED else View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
