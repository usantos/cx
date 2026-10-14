package br.gov.caixa.loterias.apostas

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LoteriasApplication : Application() {
    @javax.inject.Inject lateinit var pushRuntime: br.gov.caixa.loterias.apostas.push.PushRuntime
    override fun onCreate() {
        br.gov.caixa.loterias.apostas.authentication.isolateTopazMockComponents(this, BuildConfig.ENABLE_MOCK_BACKEND)
        super.onCreate()
        pushRuntime.start(this)
    }
}
