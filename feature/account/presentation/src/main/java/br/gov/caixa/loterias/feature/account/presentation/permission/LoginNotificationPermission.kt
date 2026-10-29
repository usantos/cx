package br.gov.caixa.loterias.feature.account.presentation.permission

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** A CX solicita permissões de execução do Topaz na entrada do login; a recusa nunca impede a autenticação. */
@Composable
fun loginNotificationPermissionReady(simulated: Boolean): Boolean {
    val context = LocalContext.current
    val pending = remember(context) {
        if (simulated) emptyList() else {
            @Suppress("DEPRECATION")
            val declared = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                .requestedPermissions.orEmpty().toSet()
            val granted = declared.filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }.toSet()
            loginRuntimePermissions(Build.VERSION.SDK_INT, declared, granted, simulated = false)
        }
    }
    var ready by rememberSaveable { mutableStateOf(pending.isEmpty()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        // A recusa ainda deve permitir a autenticação; permissões de coleta não são requisitos de acesso.
        ready = true
    }
    LaunchedEffect(Unit) {
        if (!ready) {
            try { launcher.launch(pending.toTypedArray()) }
            catch (_: Exception) { ready = true }
        }
    }
    return ready
}
