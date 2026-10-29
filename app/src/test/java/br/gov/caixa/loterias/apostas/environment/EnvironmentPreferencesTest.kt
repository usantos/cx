package br.gov.caixa.loterias.apostas.environment

import android.content.SharedPreferences
import java.lang.reflect.Proxy
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.apostas.environment.persistence.EnvironmentPreferences

class EnvironmentPreferencesTest {
    @Test fun `failed commit memory value cannot be mistaken for a durable acknowledgement`() {
        var memory: String? = "EXTERNO_ESTEIRA"; var success = false; var commits = 0
        // commit do Android pode atualizar o mapa em memória mesmo quando a gravação em disco retorna false.
        lateinit var editor: SharedPreferences.Editor
        editor = Proxy.newProxyInstance(SharedPreferences.Editor::class.java.classLoader,
            arrayOf(SharedPreferences.Editor::class.java)) { _, method, args -> when (method.name) {
                "putString" -> { assertEquals("AMBIENTE_SELECIONADO", args[0]); memory = args[1] as String?; editor }
                "commit" -> { commits++; success }
                else -> error("Unexpected editor operation: "+method.name)
            } } as SharedPreferences.Editor
        val preferences = Proxy.newProxyInstance(SharedPreferences::class.java.classLoader,
            arrayOf(SharedPreferences::class.java)) { _, method, args -> when (method.name) {
                "getString" -> { assertEquals("AMBIENTE_SELECIONADO", args[0]); memory }
                "edit" -> editor
                else -> error("Unexpected preferences operation: "+method.name)
            } } as SharedPreferences
        val storage = EnvironmentPreferences(preferences)
        assertEquals("EXTERNO_ESTEIRA", storage.confirmedSelection)
        try { storage.persist("PILOTO"); fail("Failed disk commit must propagate") }
        catch (_: IllegalStateException) { }
        assertEquals("PILOTO", storage.read())
        assertEquals("Failed memory update must not acknowledge durability", "EXTERNO_ESTEIRA", storage.confirmedSelection)
        success = true
        storage.persist("PILOTO")
        assertEquals("Retry must perform the previously failed durable write", 2, commits)
        assertEquals("PILOTO", storage.confirmedSelection)
    }
}
