package br.gov.caixa.loterias.feature.betting.data.repository

import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import kotlinx.coroutines.runBlocking
import java.util.UUID
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class ShakeTutorialPreferencesTest {
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val accounts = listOf(null, "a".repeat(64), "b".repeat(64))
    private val names = listOf(
        "aposta_shake_tutorial_v1_concluido",
        "aposta_shake_tooltip_pendente",
        "aposta_shake_tutorial_v1_concluido_logado",
        "aposta_shake_tooltip_pendente_logado"
    ) +
        accounts.flatMap { owner ->
            val scope = if (owner == null) "visitante" else "usuario_$owner"
            listOf("aposta_shake_tutorial_v1_concluido_$scope", "aposta_shake_tooltip_pendente_$scope")
        }

    @Before @After
    fun clearPreferences() {
        names.forEach { context.getSharedPreferences(it, 0).edit().clear().commit() }
    }

    @Test fun completionAndTooltipPersistSeparatelyForEachAccountAndVisitor() = runBlocking {
        accounts.forEach { first ->
            clearPreferences()
            val factory = isolatedFactory()
            ShakeSettingsRepositoryImpl(context, factory).completeTutorial(first)
            val restored = ShakeSettingsRepositoryImpl(context, factory)
            assertTrue(restored.tutorialCompleted(first))
            assertTrue(restored.tooltipPending(first))
            accounts.filter { it != first }.forEach { other ->
                assertFalse(restored.tutorialCompleted(other))
                assertFalse(restored.tooltipPending(other))
                restored.completeTutorial(other)
                restored.consumeTooltip(other)
            }
            val again = ShakeSettingsRepositoryImpl(context, factory)
            assertTrue(again.tooltipPending(first))
            accounts.forEach { assertTrue(again.tutorialCompleted(it)) }
            accounts.filter { it != first }.forEach { assertFalse(again.tooltipPending(it)) }
        }
    }

    @Test fun oldSharedLoggedCompletionDoesNotSuppressIndividualAccounts() = runBlocking {
        val factory = isolatedFactory()
        for (key in listOf("aposta_shake_tutorial_v1_concluido", "aposta_shake_tutorial_v1_concluido_logado")) {
            context.getSharedPreferences(key, 0).edit().putBoolean(key, true).commit()
        }
        val repository = ShakeSettingsRepositoryImpl(context, factory)
        accounts.forEach { assertFalse(repository.tutorialCompleted(it)) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun rawIdentifierIsNotAcceptedAsAnOwnerKey() = runBlocking {
        val factory = isolatedFactory()
        ShakeSettingsRepositoryImpl(context, factory).completeTutorial("raw-identifier")
    }
    private fun isolatedFactory(): SecureStoreFactory {
        val stores = AndroidSecureStores(context)
        val suffix = UUID.randomUUID().toString().replace("-", "")
        return SecureStoreFactory { stores.open(it.copy(record = it.record + "_" + suffix)) }
    }
}
