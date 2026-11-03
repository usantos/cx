package br.gov.caixa.loterias.apostas

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.moduledelivery.di.createModuleInstallationUseCase
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Somente em instalação AAB local-testing nova: não abre jornadas nem efetiva operações de negócio. */
@RunWith(AndroidJUnit4::class)
class AllFeaturesDynamicDeliveryUiTest {
    @Test fun instalaCadaOpcionalPreservandoEssenciaisEModulosJaEntregues() {
        ActivityScenario.launch(HomeDrawerTestActivity::class.java).use { scenario ->
            lateinit var activity: HomeDrawerTestActivity
            scenario.onActivity { activity = it }
            val manager = SplitInstallManagerFactory.create(activity.applicationContext)
            val essentials = AppModule.entries.filter { it.bundled }.mapTo(linkedSetOf()) { it.splitName }
            val optional = AppModule.entries.filterNot { it.bundled }
            assertTrue("Os módulos essenciais devem acompanhar a instalação", manager.installedModules.containsAll(essentials))
            assertTrue(
                "Este cenário exige dados de teste novos, sem opcionais instalados",
                optional.none {
                    it.splitName in
                        manager.installedModules
                }
            )
            val installation = createModuleInstallationUseCase(activity.applicationContext)
            val expected = essentials.toMutableSet()
            AppModule.entries.forEach { module ->
                val state = runBlocking {
                    withTimeout(INSTALL_TIMEOUT_MILLIS) { installation(module).last() }
                }
                assertEquals(module.key, ModuleDeliveryState.Ready, state)
                expected += module.splitName
                assertTrue("A entrega não deve remover módulos existentes: ${module.key}", manager.installedModules.containsAll(expected))
                scenario.onActivity { host ->
                    SplitCompat.installActivity(host)
                    val entry = Class.forName(module.entryClassName, true, host.classLoader)
                        .asSubclass(FeatureUiEntry::class.java).getDeclaredConstructor().newInstance()
                    assertEquals(module.entryClassName, entry.javaClass.name)
                }
            }
            assertEquals(AppModule.entries.mapTo(linkedSetOf()) { it.splitName }, manager.installedModules.intersect(expected))
        }
    }

    private companion object {
        const val INSTALL_TIMEOUT_MILLIS = 60_000L
    }
}
