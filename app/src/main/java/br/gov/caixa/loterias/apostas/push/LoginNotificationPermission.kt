package br.gov.caixa.loterias.apostas.push

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
import br.gov.caixa.loterias.apostas.BuildConfig

/** CX asks Topaz runtime permissions at login entry; denial never prevents authentication. */
@Composable
internal fun loginNotificationPermissionReady(): Boolean {
    val context = LocalContext.current
    val pending = remember(context) {
        if (BuildConfig.ENABLE_MOCK_BACKEND) emptyList() else {
            @Suppress("DEPRECATION")
            val declared = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                .requestedPermissions.orEmpty().toSet()
            val granted = declared.filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }.toSet()
            loginRuntimePermissions(Build.VERSION.SDK_INT, declared, granted, simulated = false)
        }
    }
    var ready by rememberSaveable { mutableStateOf(pending.isEmpty()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        // Refusal must still allow authentication; collection permissions are not access requirements.
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
