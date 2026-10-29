package br.gov.caixa.loterias.apostas.composition.startup

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSStatusAction
import br.gov.caixa.loterias.core.designsystem.components.state.showCaixaDSMessage
import br.gov.caixa.loterias.feature.onboarding.di.createForceUpdateViewModel
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashAnimationHost
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.state.ForceUpdateState
import com.google.firebase.FirebaseOptions

@Composable
internal fun ForceUpdateGate(activity: Activity, ready: @Composable () -> Unit) {
    val options = remember {
        FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
            .build()
    }
    val model = viewModel(key = "force-update") {
        createForceUpdateViewModel(activity.applicationContext, options, R.xml.remote_config_defaults)
    }
    val state by model.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(model) {
        model.startForegroundUpdates()
        onPauseOrDispose { model.stopForegroundUpdates() }
    }
    ForceUpdateGateContent(state, {
        val id = activity.packageName
        try {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, "market://details?id=$id".toUri())
                    .setPackage("com.android.vending")
            )
        } catch (_: ActivityNotFoundException) {
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$id".toUri()))
            } catch (_: ActivityNotFoundException) {
                showCaixaDSMessage(activity, activity.getString(R.string.force_update_store_unavailable))
            }
        }
    }) { LauncherIconGate(activity, options, ready) }
}

@Composable
internal fun ForceUpdateGateContent(state: ForceUpdateState, onOpenStore: () -> Unit, ready: @Composable () -> Unit) {
    SplashAnimationHost {
        when (state) {
            ForceUpdateState.READY -> ready()

            ForceUpdateState.CHECKING -> {
                BackHandler { }
                SplashScreen(modifier = Modifier.uiId("app.0002"))
            }

            ForceUpdateState.REQUIRED -> {
                br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBarColor(
                    br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens.Blue700,
                    darkIcons = false
                )
                BackHandler { }
                CaixaDSStatusAction(
                    title = stringResource(R.string.force_update_title),
                    message = stringResource(R.string.force_update_message),
                    action = stringResource(R.string.force_update_action),
                    onAction = onOpenStore
                )
            }
        }
    }
}
