package br.gov.caixa.loterias.core.testing.delivery

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule as createAndroidComposeRuleV2
import androidx.test.platform.app.InstrumentationRegistry

/** Usa o host interno de depuração existente e preserva o agendamento original do Compose. */
fun createFeatureComposeRule() = createAndroidComposeRule(featureTestActivityClass())

/** Usa o mesmo host de depuração com o agendamento da API Compose v2. */
fun createFeatureComposeRuleV2() = createAndroidComposeRuleV2(featureTestActivityClass())

private fun featureTestActivityClass(): Class<out ComponentActivity> =
    InstrumentationRegistry.getInstrumentation().targetContext.classLoader
        .loadClass("br.gov.caixa.loterias.apostas.HomeDrawerTestActivity")
        .asSubclass(ComponentActivity::class.java)
