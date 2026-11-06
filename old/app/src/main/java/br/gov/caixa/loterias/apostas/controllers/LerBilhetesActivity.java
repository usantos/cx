package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LerQrCodeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Crypto;
import br.gov.caixa.loterias.apostas.model.enums.LeituraBilheteEnum;
import br.gov.caixa.loterias.apostas.utils.BarcodeAnalyzer;
import br.gov.caixa.loterias.apostas.utils.BilheteUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;


public class LerBilhetesActivity extends LoteriasBaseAppActivity {

    private boolean isActivityRunning;
    private boolean isScanned;
    private boolean isPaused;
    private boolean chamouresultado;

    private PreviewView previewView;
    private ImageAnalysis imageAnalysis;

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ler_bilhetes);

        previewView = findViewById(R.id.previewView);

        TextView resultadoText = findViewById(R.id.resultadoText);
        resultadoText.setHint(R.string.titulo);

        Button fecharLeitor = findViewById(R.id.fecharLeitorCodBarras);
        fecharLeitor.setOnClickListener(v -> finish());

        Button buttonDigitarAposta = findViewById(R.id.buttonDigitarAposta);
        buttonDigitarAposta.setVisibility(View.GONE);
        buttonDigitarAposta.setOnClickListener(v -> startBilheteManual());

        Button buttonQrCode = findViewById(R.id.buttonQrCode);
        buttonQrCode.setOnClickListener(v -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));

        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> providerFuture =
                ProcessCameraProvider.getInstance(this);

        providerFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = providerFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());


                imageAnalysis = new ImageAnalysis.Builder()
                        .setResolutionSelector(
                                new ResolutionSelector.Builder()
                                        .setResolutionStrategy(
                                                new ResolutionStrategy(
                                                        new Size(1280, 720),
                                                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                                                )
                                        )
                                        .build()
                        )
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();


                imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(this),
                        new BarcodeAnalyzer(this::onBarcodeDetected)
                );

                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this, selector, preview, imageAnalysis
                );

            } catch (Exception e) {
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void onBarcodeDetected(String result) {

        if (isPaused) return;

        try {
            isScanned = true;
            pauseAnalyzer();

            if (result.length() >= 23) {

                String code, nsbFormatado;
                LeituraBilheteEnum tipo;

                try {
                    LerQrCodeDTO lerQrCodeDTO =
                            new Gson().fromJson(result.replace("APOSTA", ""), LerQrCodeDTO.class);

                    if (lerQrCodeDTO.getCf() == null || lerQrCodeDTO.getCf().isEmpty()) {
                        lerQrCodeDTO.setCf(lerQrCodeDTO.getCi());
                    }

                    String envio = lerQrCodeDTO.getNsb() + "==" +
                            lerQrCodeDTO.getCi() + "==" +
                            lerQrCodeDTO.getPro() + "==" +
                            ModalidadeEnum.fromInteger(Integer.parseInt(lerQrCodeDTO.getPro())) +
                            "==" + lerQrCodeDTO.getCf();

                    code = Crypto.cripfyBarcode(envio);
                    nsbFormatado = BilheteUtils.getNSBFormatado(lerQrCodeDTO.getNsb());
                    tipo = LeituraBilheteEnum.QR_CODE;

                } catch (Exception e) {
                    nsbFormatado = BilheteUtils.getNSBFormatado(result);
                    code = Crypto.cripfyBarcode(result);
                    tipo = LeituraBilheteEnum.CODIGO_BARRAS;
                }

                if (verificaQrCodeBarCodeByOrientation(tipo)) {

                    chamouresultado = true;
                    isScanned = false;

                    Intent intent =
                            IntentUtil.getIntentOrigemDestino(this, ResultadoLerBilhetesActivity.class);

                    intent.putExtra(ResultadoLerBilhetesActivity.COD_BARRAS_EXTRA, code);
                    intent.putExtra(ResultadoLerBilhetesActivity.NSB_EXTRA, nsbFormatado);
                    intent.putExtra(ResultadoLerBilhetesActivity.TIPO_LEITURA_EXTRA, tipo);

                    int INTENT_RESULT = 123;
                    startActivityForResult(intent, INTENT_RESULT);
                } else {
                    isScanned = false;
                    resumeAnalyzer();
                    chamouresultado = false;
                }

            } else {
                isScanned = false;
                resumeAnalyzer();
                chamouresultado = false;
            }

        } catch (Exception ignored) {
        }
    }

    private boolean verificaQrCodeBarCodeByOrientation(LeituraBilheteEnum tipo) {
        if ((tipo == LeituraBilheteEnum.QR_CODE &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                ||
                (tipo == LeituraBilheteEnum.CODIGO_BARRAS &&
                        getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            return true;
        }

        isScanned = false;
        resumeAnalyzer();
        chamouresultado = false;

        return false;
    }

    private void startBilheteManual() {
        Intent activity = new Intent(this, InsercaoManualCodBarrasActivity.class);
        startActivity(activity);
    }

    private void pauseAnalyzer() {
        isPaused = true;
    }

    private void resumeAnalyzer() {
        isPaused = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeAnalyzer();
        isScanned = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAnalyzer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }
}
