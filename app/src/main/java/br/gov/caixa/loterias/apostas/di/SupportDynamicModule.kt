package br.gov.caixa.loterias.apostas.di

import android.content.Context
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.feature.onboarding.di.createAppModulesConfigurationUseCase
import br.gov.caixa.loterias.feature.support.di.createSupportModuleDelivery
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportModuleDelivery
import br.gov.caixa.loterias.feature.support.navigation.SupportModuleNavigationDependencies
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntry
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntryLoader
import com.google.firebase.FirebaseOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object SupportDynamicModule {
    @Provides
    @Singleton
    fun modules(@ApplicationContext context: Context): AppModulesConfigurationUseCase = createAppModulesConfigurationUseCase(
        context,
        FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
            .build(),
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    )

    @Provides
    @Singleton
    fun delivery(installation: br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository): SupportModuleDelivery =
        createSupportModuleDelivery(installation)

    @Provides
    fun dependencies(
        modules: AppModulesConfigurationUseCase,
        delivery: SupportModuleDelivery,
        @ApplicationContext context: Context,
    ): SupportModuleNavigationDependencies = object : SupportModuleNavigationDependencies {
        override val modules = modules
        override val delivery = delivery
        override val entryLoader = SupportFaqEntryLoader { activityContext ->
            runCatching {
                com.google.android.play.core.splitcompat.SplitCompat.installActivity(activityContext)
                Class.forName("br.gov.caixa.loterias.feature.support.faq.SupportFaqEntryImpl", true, context.classLoader)
                    .asSubclass(SupportFaqEntry::class.java).getDeclaredConstructor().newInstance()
            }.getOrNull()
        }
    }
}
