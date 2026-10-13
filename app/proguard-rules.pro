# Keep generic type information and annotations consumed by Retrofit/Gson/AppAuth.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault,Exceptions

# Retrofit creates implementations of these interfaces with java.lang.reflect.Proxy.
-keep interface br.gov.caixa.loterias.core.network.LoteriasApi { *; }
-keep interface br.gov.caixa.loterias.core.network.LegacyBackendApi { *; }
-keep interface br.gov.caixa.loterias.core.network.BetsHistoryApi { *; }
-keep interface br.gov.caixa.loterias.core.network.MercadoPagoApi { *; }
-keep interface br.gov.caixa.loterias.core.network.RecargaPayApi { *; }
-keep interface br.gov.caixa.loterias.core.network.PurchaseBackendApi { *; }
-keep interface br.gov.caixa.loterias.core.network.AccountVerificationBackendApi { *; }
-keep interface br.gov.caixa.loterias.core.network.NotificationReadApi { *; }
-keepclasseswithmembers,allowshrinking,allowoptimization,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Gson deserializes API payloads reflectively. Names may be obfuscated because wire names are
# fixed by @SerializedName, but fields and constructors must remain available.
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowoptimization,allowobfuscation class br.gov.caixa.loterias.core.network.ApiEnvelope { *; }
-keep,allowoptimization,allowobfuscation class br.gov.caixa.loterias.core.network.**Dto { *; }
# Shared HTTP error metadata is also read reflectively and intentionally is not a UI/domain model.
-keep,allowoptimization,allowobfuscation class br.gov.caixa.loterias.core.network.BackendErrorMetadata { *; }

# Hilt/KSP normally ships consumer rules; retain DI entry points and generated components as a
# defensive rule for release builds that use R8 full mode.
-keep @dagger.hilt.EntryPoint class * { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep class br.gov.caixa.loterias.apostas.Hilt_* { *; }

# Browser authentication state is persisted/deserialized by AppAuth.
-keep class net.openid.appauth.** { *; }
-dontwarn net.openid.appauth.**

# Firebase supplies consumer rules, but Remote Config/Crash reporting are release-critical.
-keep class com.google.firebase.remoteconfig.** { *; }
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.**

# The homologated FilaBRA binary has no consumer-rules.pro inside the AAR and uses reflection.
-keep class br.com.caixa.filabra.** { *; }
-keep class br.gov.caixa.filabra.** { *; }
-keep class com.caixa.filabra.** { *; }
-keep class com.filabra.adapter.** { *; }
-dontwarn br.com.caixa.filabra.**
-dontwarn br.gov.caixa.filabra.**
-dontwarn com.caixa.filabra.**
-dontwarn com.filabra.adapter.**

# Preserve actionable production stack traces while keeping source file paths private.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Android WebView JavaScript bridges, if a receipt flow exposes one.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
