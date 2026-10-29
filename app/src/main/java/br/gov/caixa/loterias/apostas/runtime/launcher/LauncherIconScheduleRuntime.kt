package br.gov.caixa.loterias.apostas.runtime.launcher

import android.content.Context
import android.util.Log
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.feature.onboarding.di.createLauncherIconCalendarConfiguration
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.repository.LauncherIconCalendarConfiguration
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetLauncherIconPresetUseCase
import com.google.firebase.FirebaseOptions
import java.io.IOException
import java.time.Instant
import kotlinx.coroutines.CancellationException

internal class LauncherIconScheduleRuntime(
    private val initialize: suspend () -> Unit,
    private val getPreset: GetLauncherIconPresetUseCase,
    private val nextEvaluationAt: () -> Instant,
    private val applyPreset: (LauncherIconPreset) -> LauncherIconApplyResult,
    private val schedule: (Instant) -> Unit,
) {
    constructor(context: Context) : this(
        context.applicationContext,
        createLauncherIconCalendarConfiguration(context.applicationContext, launcherCalendarFirebaseOptions())
    )

    private constructor(context: Context, repository: LauncherIconCalendarConfiguration) : this(
        { repository.initialize(R.xml.remote_config_defaults) },
        GetLauncherIconPresetUseCase(repository),
        repository::nextEvaluationAt,
        LauncherIconController(context)::apply,
        AndroidLauncherIconScheduler(context)::schedule
    )

    suspend fun refresh(): LauncherIconApplyResult = try {
        initialize()
        applyPreset(getPreset())
    } finally {
        // Mesmo uma aplicação adiada ou uma falha de preparação precisa preservar o próximo dia.
        scheduleNextUpdate()
    }

    fun scheduleNextUpdate() {
        try {
            schedule(nextEvaluationAt())
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: IOException) {
            logFailure(failure)
        } catch (failure: IllegalArgumentException) {
            logFailure(failure)
        } catch (failure: IllegalStateException) {
            logFailure(failure)
        } catch (failure: SecurityException) {
            logFailure(failure)
        }
    }
    private fun logFailure(failure: Exception) {
        Log.w("LauncherIconCalendar", "Não foi possível agendar a próxima avaliação do ícone", failure)
    }
}

private fun launcherCalendarFirebaseOptions(): FirebaseOptions = FirebaseOptions.Builder()
    .setApplicationId(BuildConfig.FIREBASE_APP_ID)
    .setApiKey(BuildConfig.FIREBASE_API_KEY)
    .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
    .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
    .build()
