package br.gov.caixa.loterias.feature.home

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import dalvik.system.DexClassLoader
import java.io.File
import java.lang.reflect.Proxy
import java.security.DigestInputStream
import java.security.MessageDigest

/** Executa o HomeViewHolder e os recursos do APK fornecido em um carregador isolado.
 * Não executa Application.onCreate, inicialização de SDKs, autenticação ou chamadas ao backend legado.
 */
internal class NativeHomeOracle(private val activity: Activity) {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val base = instrumentation.targetContext
    private val apk = referenceApk
    private val loader = DexClassLoader(
        apk.absolutePath,
        base.codeCacheDir.absolutePath,
        null,
        ClassLoader.getSystemClassLoader().parent
    )
    private val archivePackageName = requireNotNull(base.packageManager.getPackageArchiveInfo(apk.absolutePath, 0)) {
        "APK de referência inválido: ${apk.absolutePath}"
    }.packageName
    private val classNamespace = "br.gov.caixa.loterias.apostas"
    private val resources: android.content.res.Resources = base.packageManager.getResourcesForApplication(
        ApplicationInfo().apply {
            sourceDir = apk.absolutePath
            publicSourceDir = sourceDir
            packageName = archivePackageName
            uid = android.os.Process.myUid()
        }
    )
    private val theme: android.content.res.Resources.Theme = resources.newTheme().apply { applyStyle(id("style", "AppTheme"), true) }
    private lateinit var application: Application
    private val context = object : ContextWrapper(base) {
        override fun getResources() = this@NativeHomeOracle.resources
        override fun getAssets() = this@NativeHomeOracle.resources.assets
        override fun getTheme() = this@NativeHomeOracle.theme
        override fun getClassLoader() = loader
        override fun getApplicationContext() = application
        override fun getPackageName() = archivePackageName
        override fun getSystemService(name: String): Any? = if (name == Context.LAYOUT_INFLATER_SERVICE) {
            (base.getSystemService(name) as LayoutInflater).cloneInContext(this)
        } else {
            super.getSystemService(name)
        }
    }

    init {
        application = object : Application() {
            init {
                attachBaseContext(context)
            }
        }
        loader.loadClass("$classNamespace.utils.Aplicacao").getField("application").set(null, application)
        // O provedor de inicialização Joda do manifesto original não executa neste carregador isolado.
        val providerInterface = loader.loadClass("org.joda.time.tz.Provider")
        val provider = loader.loadClass("net.danlew.android.joda.ResourceZoneInfoProvider")
            .getConstructor(Context::class.java).newInstance(context)
        loader.loadClass("org.joda.time.DateTimeZone").getMethod("setProvider", providerInterface).invoke(null, provider)
    }

    private fun id(type: String, name: String) = resources.getIdentifier(name, type, archivePackageName).also {
        check(it != 0) { "Legacy resource $type/$name absent" }
    }

    private val centeredIds by lazy {
        val ids = mutableSetOf<Int>()
        val parser = resources.getLayout(id("layout", "card_modalidade"))
        val namespace = "http://schemas.android.com/apk/res/android"
        parser.use {
            while (parser.eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == org.xmlpull.v1.XmlPullParser.START_TAG &&
                    parser.getAttributeIntValue(namespace, "textAlignment", -1) == View.TEXT_ALIGNMENT_CENTER
                ) {
                    ids += parser.getAttributeResourceValue(namespace, "id", View.NO_ID)
                }
                parser.next()
            }
        }
        ids
    }

    fun bindCard(json: String, width: Int, height: Int): View {
        val view = LayoutInflater.from(context).inflate(id("layout", "card_modalidade"), null)
        val listenerClass = loader.loadClass("$classNamespace.view.listener.OnHomeListener")
        val listener = Proxy.newProxyInstance(loader, arrayOf(listenerClass)) { _, _, _ -> null }
        val gsonClass = loader.loadClass("com.google.gson.Gson")
        val gson = gsonClass.getConstructor().newInstance()
        val modalityClass = loader.loadClass("$classNamespace.model.bean.Modalidade")
        val modality = gsonClass.getMethod("fromJson", String::class.java, Class::class.java).invoke(gson, json, modalityClass)
        val holderClass = loader.loadClass("$classNamespace.view.holder.HomeViewHolder")
        val holder = holderClass.getConstructor(View::class.java, listenerClass, Activity::class.java).newInstance(view, listener, activity)
        holderClass.getMethod("bind", modalityClass, Int::class.javaPrimitiveType).invoke(holder, modality, 0)
        fun resolveAlignment(child: View) {
            if (child is TextView && (
                    child.textAlignment == View.TEXT_ALIGNMENT_CENTER || child.id in centeredIds ||
                        (
                            child.id != View.NO_ID && resources.getResourceEntryName(
                                child.id
                            ) in setOf("textVieCardAcumulatedValue", "textViewSpelledValue", "textPremioEstimadoConcurso")
                            )
                    )
            ) {
                // Resolve textAlignment do XML antes do primeiro percurso da hierarquia fora da tela.
                child.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                child.gravity =
                    (child.gravity and android.view.Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK.inv()) or
                    android.view.Gravity.CENTER_HORIZONTAL
            }
            if (child is android.view.ViewGroup) for (i in 0 until child.childCount) resolveAlignment(child.getChildAt(i))
        }
        resolveAlignment(view)
        view.layoutDirection = View.LAYOUT_DIRECTION_LTR
        val host = android.widget.FrameLayout(activity)
        (activity.window.decorView as android.view.ViewGroup).addView(host, android.view.ViewGroup.LayoutParams(width, height))
        host.addView(view)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, width, height)
        return view
    }

    fun snapshot(view: View, width: Int, height: Int): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, width, height)
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
            view.draw(Canvas(it))
            fun describe(child: View) {
                if (child is TextView && child.id != View.NO_ID) {
                    android.util.Log.i(
                        "LegacyHomeMetrics",
                        "${resources.getResourceEntryName(child.id)}: alignment=${child.textAlignment}, " +
                            "gravity=${child.gravity}, width=${child.width}, " +
                            "left=${child.layout?.getLineLeft(0)}, text=${child.text}"
                    )
                }
                if (child is android.view.ViewGroup) for (i in 0 until child.childCount) describe(child.getChildAt(i))
            }
            describe(view)
            (activity.window.decorView as android.view.ViewGroup).removeView(view.parent as View)
        }
    }

    fun themeTextColor(): Int = TextView(context).currentTextColor

    fun export(bitmap: Bitmap, name: String) {
        require(name.matches(Regex("[a-z0-9-]+")))
        val file = File(base.filesDir, "$name.png")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        // Copia primeiro para o armazenamento externo do app, acessível ao shell.
        val external = File(base.getExternalFilesDir(null), "$name.png")
        file.copyTo(external, overwrite = true)
        instrumentation.uiAutomation.executeShellCommand("cp -f ${external.absolutePath} /sdcard/Download/$name.png").use {
            val output = android.os.ParcelFileDescriptor.AutoCloseInputStream(it).readBytes().toString(Charsets.UTF_8)
            check(output.isBlank()) { output }
        }
    }

    private companion object {
        val referenceApk: File by lazy {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val directory = instrumentation.targetContext.codeCacheDir
            val temporary = File.createTempFile("legacy-reference-", ".apk", directory)
            val digest = MessageDigest.getInstance("SHA-256")
            try {
                temporary.outputStream().use { output ->
                    check(temporary.setReadOnly())
                    instrumentation.context.assets.open("legacy-reference.apk").use { input ->
                        DigestInputStream(input, digest).use { it.copyTo(output) }
                    }
                }
                val hash = digest.digest().joinToString("") { "%02x".format(it) }
                val cached = File(directory, "legacy-reference-$hash.apk")
                if (cached.exists()) {
                    check(temporary.delete())
                } else {
                    check(temporary.renameTo(cached))
                }
                cached
            } finally {
                temporary.delete()
            }
        }
    }
}
