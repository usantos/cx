package br.gov.caixa.loterias.apostas.di

import android.content.Context
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import br.gov.caixa.loterias.core.moduledelivery.di.createModuleInstallationRepository
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleUiDependencies
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntryLoader
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeatureModulesModule {
    @Provides
    @Singleton
    fun installationRepository(@ApplicationContext context: Context): ModuleInstallationRepository =
        createModuleInstallationRepository(context)

    @Provides
    @Singleton
    fun installation(repository: ModuleInstallationRepository): InstallAppModuleUseCase = InstallAppModuleUseCase(repository)

    @Provides
    @Singleton
    fun featureModules(installation: InstallAppModuleUseCase, configuration: AppModulesConfigurationUseCase): FeatureModuleUiDependencies =
        FeatureModuleUiDependencies(
            configuration,
            installation,
            object : FeatureUiEntryLoader {
                override fun <T : FeatureUiEntry> load(module: AppModule, type: Class<T>, context: Context): T? = runCatching {
                    SplitCompat.installActivity(context)
                    Class.forName(module.entryClassName, true, context.classLoader).asSubclass(type).getDeclaredConstructor().newInstance()
                }.getOrNull()
            }
        )
}
