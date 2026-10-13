package br.gov.caixa.loterias.feature.tickets

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun TicketScannerRoute(
    viewModel: TicketsViewModel,
    onBack: () -> Unit,
    onManual: () -> Unit,
    onResult: (List<ConfirmedBet>) -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var permissionGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionGranted = it
    }
    LaunchedEffect(state.loaded, state.bets, state.ticketResult) {
        if (state.loaded && state.errorMessage == null && (state.ticketResult != null || state.bets.isNotEmpty())) onResult(state.bets)
    }
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.CameraCanvas).navigationBarsPadding()) {
        if (permissionGranted) {
            TicketCameraPreview(
                onCode = { code ->
                    viewModel.submitScannedTicketCode(code)
                },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Column(
                Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(CaixaDSIcons.Information, null, Modifier.size(56.dp), tint = CaixaDSColorTokens.White)
                Text(
                    "Permita o acesso à câmera para ler o código do bilhete.",
                    Modifier.padding(vertical = 20.dp),
                    color = CaixaDSColorTokens.White,
                )
                CaixaDSLegacyPrimaryButton("Permitir câmera", { permissionLauncher.launch(Manifest.permission.CAMERA) }, Modifier.fillMaxWidth())
            }
        }
        if (permissionGranted) {
            Image(
                painter = painterResource(R.drawable.mascara_loteria),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
            Image(
                painter = painterResource(R.drawable.logo_cinza),
                contentDescription = null,
                modifier = Modifier.offset(x = 68.dp, y = 32.dp).size(22.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                "Resultado",
                Modifier.offset(x = 100.dp, y = 34.dp),
                color = CaixaDSColorTokens.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Image(
                painter = painterResource(R.drawable.close_circle_outline),
                contentDescription = "Fechar",
                modifier = Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 32.dp).size(30.dp)
                    .clickable(onClick = onBack),
                contentScale = ContentScale.Fit,
            )
            Text(
                "Posicione a câmera sobre o QR Code e\naguarde. A leitura é automática.",
                Modifier.offset(x = 68.dp, y = 64.dp).width(290.dp),
                color = CaixaDSColorTokens.White,
                fontSize = 14.2.sp,
                lineHeight = 20.sp,
            )
            Button(
                onClick = {},
                modifier = Modifier.align(Alignment.BottomCenter).offset(x = 16.dp, y = (-86).dp).height(55.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CaixaDSColorTokens.LegacyBurntYellow,
                    contentColor = CaixaDSColorTokens.White,
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_qrcode_branco),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Text("QR Code", Modifier.padding(start = 8.dp), fontSize = 14.2.sp)
            }
        }
    }
    state.errorMessage?.let { CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissTicketError) }
}

@Composable
private fun TicketCameraPreview(onCode: (String) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_CODE_128, Barcode.FORMAT_ITF)
                .build(),
        )
    }
    val latestOnCode = rememberUpdatedState(onCode)
    val analyzer = remember(scanner) { TicketBarcodeAnalyzer(scanner) { latestOnCode.value(it) } }
    val providerFuture = remember { ProcessCameraProvider.getInstance(context) }
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    val preview = remember { Preview.Builder().build() }
    val analysis = remember(analyzer) {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also { it.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer) }
    }
    DisposableEffect(preview, analysis, providerFuture, lifecycleOwner) {
        preview.setSurfaceProvider(ContextCompat.getMainExecutor(context)) { request -> surfaceRequest = request }
        providerFuture.addListener({
            val provider = providerFuture.get()
            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(context))
        onDispose {
            runCatching { providerFuture.get().unbindAll() }
            preview.setSurfaceProvider(null)
            scanner.close()
        }
    }
    surfaceRequest?.let { request ->
        CameraXViewfinder(surfaceRequest = request, modifier = modifier)
    }
}

private class TicketBarcodeAnalyzer(
    private val scanner: BarcodeScanner,
    private val onCode: (String) -> Unit,
) : ImageAnalysis.Analyzer {
    private val processing = AtomicBoolean(false)
    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || !processing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }
        scanner.process(InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees))
            .addOnSuccessListener { codes -> codes.firstNotNullOfOrNull { it.rawValue?.trim()?.takeIf(String::isNotBlank) }?.let(onCode) }
            .addOnCompleteListener {
                processing.set(false)
                imageProxy.close()
            }
    }
}
