package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.queue.android.FilaBraQueueWebView
import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class FilaBraQueueCoordinationTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun insecureQueueAddressReportsOneErrorThenFinishesAcrossRecomposition() {
        val calls = mutableListOf<String>()
        val broadcasts = mutableListOf<Intent>()
        lateinit var queueActivity: Activity
        compose.runOnUiThread {
            queueActivity = object : Activity() {
                override fun getPackageName() = "br.gov.caixa.queue.test"
                override fun getResources() = compose.activity.resources
                override fun sendBroadcast(intent: Intent) { broadcasts += intent; calls += "broadcast" }
                override fun finish() { calls += "finish" }
            }
        }
        val location = mutableStateOf("https://example.invalid/queue")
        compose.setContent { FilaBraQueueWebView(location.value, queueActivity) }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(listOf("broadcast", "finish"), calls)
            val error = broadcasts.single()
            assertEquals("br.gov.caixa.queue.test.com.filabra.adapter.FILABRA_BROADCAST", error.action)
            assertEquals("br.gov.caixa.queue.test", error.`package`)
            assertEquals("ERROR", error.getStringExtra("result"))
            assertEquals("Endereço inválido para a fila.", error.getStringExtra("message"))
            location.value = "javascript:void(0)"
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(listOf("broadcast", "finish"), calls) }
    }
}
