# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/igorvilar/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# AppAuth
-keep class net.openid.appauth.** { *; }
-dontwarn net.openid.appauth.**

# Gson
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Glide
-keep class com.bumptech.glide.** { *; }
-keep class com.bumptech.glide.load.** { *; }
-dontwarn com.bumptech.glide.**

# ZXing (scanner de código de barras)
-keep class com.journeyapps.barcodescanner.** { *; }
-dontwarn com.journeyapps.barcodescanner.**

# AppCenter
-keep class com.microsoft.appcenter.** { *; }
-dontwarn com.microsoft.appcenter.**

# Joda-Time
-keep class org.joda.time.** { *; }
-dontwarn org.joda.time.**

# Volley
-keep class com.android.volley.** { *; }
-dontwarn com.android.volley.**

# Lottie
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# RootBeer
-keep class com.scottyab.rootbeer.** { *; }
-dontwarn com.scottyab.rootbeer.**

# MercadoLibre SDK
-keep class com.mercadolibre.android.device.** { *; }
-dontwarn com.mercadolibre.android.device.**

# Topaz OFD
-keep class br.com.topaz.** { *; }
-dontwarn br.com.topaz.**

# Swagger
-keep class io.swagger.annotations.** { *; }
-dontwarn io.swagger.annotations.**

# Apache Commons
-keep class org.apache.commons.** { *; }
-dontwarn org.apache.commons.**

# MaskedEditText
-keep class com.github.pinball83.** { *; }
-dontwarn com.github.pinball83.**

# CurrencyEditText
-keep class com.github.BlacKCaT27.** { *; }
-dontwarn com.github.BlacKCaT27.**

# CircleIndicator
-keep class me.relex.circleindicator.** { *; }
-dontwarn me.relex.circleindicator.**

# QR Generator
-keep class androidmads.library.qrgenearator.** { *; }
-dontwarn androidmads.library.qrgenearator.**

# Flexbox (se estiver usando a versão AAR)
-keep class com.google.android.flexbox.** { *; }
-dontwarn com.google.android.flexbox.**

# Reflection (WebView JS interface)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserva nomes de arquivos e linhas para stacktraces
-keepattributes SourceFile,LineNumberTable

# Preserva todos os campos da classe BuildConfig
-keep class **.BuildConfig {
    <fields>;
}

# Contratos de servico: Gson usa reflexao e parte dos campos nao tem @SerializedName.
# Preserva classes, nomes, campos e construtores; nao estende o bypass aos BOs,
# DAOs, repositories, ViewModels, Activities ou a todo o pacote model.
-keep class br.gov.caixa.loterias.apostas.model.bean.** { *; }
-keep class br.gov.caixa.loterias.apostas.model.bo.silce.dto.** { *; }
-keep class br.gov.caixa.loterias.apostas.model.bo.silce.data.** { *; }
-keep class br.gov.caixa.loterias.apostas.model.bo.silce.queryparam.** { *; }

# Resposta Mercado Pago localizada fora dos pacotes de DTOs.
-keep class br.gov.caixa.loterias.apostas.model.bo.CardTokenResponse { *; }

# Enums sao usados em JSON, inclusive por Enum.name()/valueOf e @JsonAdapter.
-keep enum br.gov.caixa.loterias.apostas.** { *; }

# Preserva tipos genericos (List<DTO>, respostas e TypeToken) e anotacoes Gson.
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# Campos explicitamente mapeados fora dos pacotes de contrato tambem sao reflexivos.
-keepclassmembers class br.gov.caixa.loterias.apostas.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Adaptadores instanciados por @JsonAdapter conservam seus construtores e metodos.
# Seus nomes podem mudar: R8 atualiza as referencias nas anotacoes.
-keep,allowobfuscation class br.gov.caixa.loterias.apostas.** extends com.google.gson.TypeAdapter { *; }
-keep,allowobfuscation class br.gov.caixa.loterias.apostas.** implements com.google.gson.TypeAdapterFactory { *; }
