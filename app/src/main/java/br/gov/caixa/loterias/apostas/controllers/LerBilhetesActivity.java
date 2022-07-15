package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Crypto;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;


public class LerBilhetesActivity extends AppCompatActivity {

    private boolean isActivityRunning;
    private DecoratedBarcodeView barcodeScannerView;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            try {
                pause(barcodeScannerView);
                String code = Crypto.cripfyBarcode(result.getText());
                if (code.length() >= 23){
                    ResultadoLerBilhetesActivity_.intent(LerBilhetesActivity.this).codBarras(code).start();
                } else {
                    resume(barcodeScannerView);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ler_bilhetes);

        TextView textoLeitorCodBarras = findViewById(R.id.TextoLeitorCodBarras);
        textoLeitorCodBarras.setText(getResources().getString(R.string.posicione_linha_codigo_barras));

        Button fecharLeitor = findViewById(R.id.fecharLeitorCodBarras);
        fecharLeitor.setOnClickListener(v -> finish());

        Button buttonDigitarAposta = findViewById(R.id.buttonDigitarAposta);
        buttonDigitarAposta.setOnClickListener(v -> {
            startBilheteManual();
        });

        barcodeScannerView = findViewById(R.id.DecoratedBarcodeViewBilhetes);
        barcodeScannerView.getViewFinder().setVisibility(View.INVISIBLE);
        barcodeScannerView.getStatusView().setVisibility(View.INVISIBLE);
        barcodeScannerView.getBarcodeView().getCameraSettings().setFocusMode(CameraSettings.FocusMode.CONTINUOUS);
        barcodeScannerView.decodeContinuous(callback);

        buttonDigitarAposta.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_digite_aposta_realizada)));
    }

    private void startDezSegundos() {
        new Handler().postDelayed((Runnable) () -> {
            if(isActivityRunning){
                pause(barcodeScannerView);
                showAlert();
            } else {
                new Handler().postDelayed((Runnable) () -> {
                    startDezSegundos();
                }, 5000);
            }

        }, 10000);
    }

    private void showAlert() {
        ViewUtils.alertTitleButtonYesOrNotListener(LerBilhetesActivity.this,
                                                   getString(R.string.nao_foi_possivel_realizar_leitura),
                                                   getString(R.string.tentar_novamente),
                                                   (dialog, which) -> {
                                                       resume(barcodeScannerView);
                                                       startDezSegundos();
                                                   },
                                                   getString(R.string.digitar_codigo),
                                                   (dialog, which) -> {
                                                       startBilheteManual();
                                                   });
    }

    private void startBilheteManual() {
        Intent activity = new Intent(LerBilhetesActivity.this, InsercaoManualCodBarrasActivity.class);
        startActivity(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDezSegundos();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    public void pause(View view) {
        barcodeScannerView.pause();
    }

    public void resume(View view) {
        barcodeScannerView.resume();
    }

    public void triggerScan(View view) {
        barcodeScannerView.decodeSingle(callback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
