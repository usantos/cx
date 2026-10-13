package br.gov.caixa.loterias.feature.retail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    LaunchedEffect(state.status, mockInsideBrazil) {
        if (state.status != LocationGateStatus.CHECKING) return@LaunchedEffect
        if (mockInsideBrazil) {
            viewModel.allowed()
            return@LaunchedEffect
        }
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.hasEnabledLocationProvider()) {
            viewModel.providerDisabled()
        } else if (!context.hasLocationPermission()) {
            viewModel.permissionRequired()
        } else {
            when (resolveCountryCode(context, manager)) {
                "BR" -> viewModel.allowed()
                null -> viewModel.failed()
                else -> viewModel.outsideBrazil()
            }
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

@Composable
private fun LocationGateScreen(
    state: LocationGateUiState,
    versionName: String,
    @DrawableRes lotteryIconResourceId: Int,
    onAction: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(CaixaDS.colors.surface).statusBarsPadding().navigationBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(listOf(CaixaDSColorTokens.LegacyTealAction, CaixaDSColorTokens.Blue700)),
            ).height(56.dp).padding(start = 32.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Loterias CAIXA", Modifier.weight(1f), color = CaixaDSColorTokens.White, fontSize = 16.sp, fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold)
            if (lotteryIconResourceId != 0) {
                Image(
                    painterResource(lotteryIconResourceId),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
        BoxWithConstraints(Modifier.fillMaxSize()) {
            Icon(
                painterResource(R.drawable.legacy_location),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center).offset(y = (-54).dp).size(72.dp),
                tint = Color.Unspecified,
            )
            Box(
                Modifier.align(Alignment.Center).width(142.dp).height(1.dp)
                    .background(CaixaDS.colors.legacyLine),
            )
            when (state.status) {
                LocationGateStatus.CHECKING -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center).offset(y = 80.dp),
                    color = CaixaDS.colors.legacyTeal,
                )
                else -> Text(
                    locationMessage(state.status),
                    Modifier.align(Alignment.TopCenter).offset(y = maxHeight / 2 + 18.dp).padding(horizontal = 16.dp),
                    color = CaixaDSColorTokens.LegacyTealAction,
                    fontFamily = CaixaDSLegacyFuturaFontFamily,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    minLines = 4,
                    maxLines = 4,
                )
            }
            Text(
                "v$versionName",
                Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
                color = CaixaDSColorTokens.LegacyStatusVersion,
                fontFamily = CaixaDSLegacyFuturaFontFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            locationActionLabel(state.status)?.let { label ->
                Button(
                    onClick = onAction,
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CaixaDSColorTokens.YellowAction,
                        contentColor = CaixaDSColorTokens.White,
                    ),
                    shape = RoundedCornerShape(0.dp),
                ) { Text(label, fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

private fun LocationManager.hasEnabledLocationProvider(): Boolean =
    isProviderEnabled(LocationManager.GPS_PROVIDER) || isProviderEnabled(LocationManager.NETWORK_PROVIDER)

@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
private suspend fun resolveCountryCode(context: Context, manager: LocationManager): String? = withContext(Dispatchers.IO) {
    val location = manager.getProviders(true).mapNotNull { provider ->
        runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
    }.minByOrNull { it.accuracy } ?: return@withContext null
    runCatching {
        Geocoder(context, Locale.getDefault())
            .getFromLocation(location.latitude, location.longitude, 1)
            ?.firstOrNull()?.countryCode?.uppercase(Locale.ROOT)
    }.getOrNull()
}
