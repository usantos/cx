package br.gov.caixa.loterias.core.presentation.delivery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory

/** Adaptador de diálogo da plataforma; não solicita instalação nem executa operações de negócio. */
@Composable
fun FeatureModuleConfirmation(sessionId: Int, onResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    val manager = remember(context) { SplitInstallManagerFactory.create(context) }
    val callback = rememberUpdatedState(onResult)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        callback.value(it.resultCode == android.app.Activity.RESULT_OK)
    }
    DisposableEffect(manager, sessionId) {
        val active = java.util.concurrent.atomic.AtomicBoolean(true)
        manager.getSessionState(sessionId).addOnSuccessListener { session ->
            if (active.get() && !manager.startConfirmationDialogForResult(session, launcher)) callback.value(false)
        }.addOnFailureListener { if (active.get()) callback.value(false) }
        onDispose { active.set(false) }
    }
}
