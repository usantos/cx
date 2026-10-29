package br.gov.caixa.loterias.feature.retail.presentation.route

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import br.gov.caixa.loterias.feature.retail.presentation.screen.LocationGateScreen
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateEffect
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.LocationGateViewModel

@Composable
fun LocationGateRoute(
    viewModel: LocationGateViewModel,
    versionName: String,
    @DrawableRes lotteryIconResourceId: Int = 0,
    mockInsideBrazil: Boolean = false,
    onAllowed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var permissionRequested by rememberSaveable { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.retry()
        } else {
            val permanentlyDenied = permissionRequested && activity != null &&
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) &&
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            viewModel.permissionRequired(permanentlyDenied)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { if (it == LocationGateEffect.Allowed) onAllowed() }
    }
    LaunchedEffect(state.status, state.checkAttempt, mockInsideBrazil) {
        if (state.status != LocationGateStatus.CHECKING) return@LaunchedEffect
        if (mockInsideBrazil) {
            viewModel.checkLocation(mockInsideBrazil = true)
            return@LaunchedEffect
        }
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.hasEnabledLocationProvider()) {
            viewModel.providerDisabled()
        } else if (!context.hasLocationPermission()) {
            viewModel.permissionRequired()
        } else {
            viewModel.checkLocation()
        }
    }
    LaunchedEffect(state.status) {
        if (state.status == LocationGateStatus.PERMISSION_REQUIRED && !permissionRequested) {
            permissionRequested = true
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && state.status != LocationGateStatus.PERMISSION_REQUIRED) {
                viewModel.retry()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LocationGateScreen(
        state = state,
        versionName = versionName,
        lotteryIconResourceId = lotteryIconResourceId,
        onAction = {
            when (state.status) {
                LocationGateStatus.PERMISSION_REQUIRED -> permissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                )
                LocationGateStatus.PERMISSION_PERMANENTLY_DENIED -> context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
                )
                LocationGateStatus.PROVIDER_DISABLED -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                LocationGateStatus.ERROR -> scope.launch { viewModel.retry() }
                else -> Unit
            }
        },
    )
}

