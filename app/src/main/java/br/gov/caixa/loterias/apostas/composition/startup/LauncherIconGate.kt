package br.gov.caixa.loterias.apostas.composition.startup

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.runtime.launcher.LauncherIconApplyResult
import br.gov.caixa.loterias.apostas.runtime.launcher.LauncherIconCalendarObserver
import br.gov.caixa.loterias.apostas.runtime.launcher.LauncherIconController
import br.gov.caixa.loterias.apostas.runtime.launcher.LauncherIconScheduleRuntime
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.onboarding.di.createLauncherIconViewModel
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.state.LauncherIconApplyOutcome
import com.google.firebase.FirebaseOptions

@Composable
internal fun LauncherIconGate(activity: Activity, options: FirebaseOptions, ready: @Composable () -> Unit) {
    val model = viewModel(key = "launcher-icon") { createLauncherIconViewModel(activity.applicationContext, options) }
    val runtime = remember(activity.applicationContext) { LauncherIconScheduleRuntime(activity.applicationContext) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = lifecycleOwner.lifecycleScope
    val state by model.state.collectAsStateWithLifecycle()
    LaunchedEffect(model, state.revision) {
        model.load(launcherIconPreviewValue(activity.intent, BuildConfig.DEBUG))
        val selection = model.state.value
        val preset = requireNotNull(selection.preset)
        if (selection.completedRevision != selection.revision) {
            val result = LauncherIconController(activity.applicationContext).apply(preset)
            model.complete(
                when (result) {
                    LauncherIconApplyResult.APPLIED -> LauncherIconApplyOutcome.APPLIED
                    LauncherIconApplyResult.UNCHANGED -> LauncherIconApplyOutcome.UNCHANGED
                    LauncherIconApplyResult.DEFERRED -> LauncherIconApplyOutcome.DEFERRED
                    LauncherIconApplyResult.FAILED -> LauncherIconApplyOutcome.FAILED
                },
                selection.revision
            )
            if (BuildConfig.DEBUG) Log.d("LauncherIcon", "preset=${preset.configValue} outcome=$result")
            runtime.scheduleNextUpdate()
        }
    }
    if (state.outcome == null) {
        SplashScreen(modifier = Modifier.uiId("app.0003"))
    } else {
        if (launcherIconPreviewValue(activity.intent, BuildConfig.DEBUG) == null) {
            DisposableEffect(lifecycleOwner, runtime) {
                val observer = LauncherIconCalendarObserver(scope) { runtime.refresh() }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }
        }
        ready()
    }
}

internal fun launcherIconPreviewValue(intent: Intent, debug: Boolean): String? =
    if (debug) intent.getStringExtra("launcher_icon_preview") else null
