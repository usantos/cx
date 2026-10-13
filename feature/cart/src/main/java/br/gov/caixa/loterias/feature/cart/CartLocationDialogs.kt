package br.gov.caixa.loterias.feature.cart

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.domain.cart.CheckoutLocationStatus

@Composable
internal fun CartLocationDialogs(state: CartUiState, viewModel: CartViewModel, onBlockedHome: () -> Unit) {
    val context = LocalContext.current
    val activity = context.locationActivity()
    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = permissions.all { result[it] == true }
        val permanentlyDenied = result.isNotEmpty() && activity != null && permissions.any {
            result[it] != true && !ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
        viewModel.locationPermissionResult(granted, permanentlyDenied)
    }
    val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.locationSettingsReturned()
    }
    LaunchedEffect(state.locationAction) {
        val action = state.locationAction ?: return@LaunchedEffect
        if (!viewModel.takeLocationAction(action)) return@LaunchedEffect
        try {
            when (action) {
                CartLocationAction.REQUEST_PERMISSION -> permissionLauncher.launch(permissions)
                CartLocationAction.PERMISSION_SETTINGS -> settingsLauncher.launch(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
                )
                CartLocationAction.PROVIDER_SETTINGS -> settingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } catch (failure: RuntimeException) {
            if (failure is kotlinx.coroutines.CancellationException) throw failure
            viewModel.locationLaunchFailed()
        }
    }
    when (state.locationPrompt) {
        CheckoutLocationStatus.PERMISSION_REQUIRED,
        CheckoutLocationStatus.PERMISSION_SETTINGS_REQUIRED,
        CheckoutLocationStatus.PROVIDER_DISABLED -> CaixaDSLegacyDialog(
            title = "Atenção",
            message = CART_LOCATION_CONSENT,
            positiveLabel = "Sim",
            onPositive = viewModel::acceptLocationConsent,
            negativeLabel = "Não",
            onNegative = viewModel::declineLocationConsent,
            onDismiss = {},
        )
        CheckoutLocationStatus.OUTSIDE_BRAZIL, CheckoutLocationStatus.UNAVAILABLE -> CaixaDSLegacyDialog(
            title = "Atenção",
            message = CART_LOCATION_BLOCKED,
            positiveLabel = "Entendi",
            onPositive = { if (viewModel.dismissLocationBlock()) onBlockedHome() },
            onDismiss = {},
        )
        else -> Unit
    }
}

private fun Context.locationActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.locationActivity()
    else -> null
}
