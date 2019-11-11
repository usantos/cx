package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.Crypto;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;


public class LerBilhetesActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeScannerView;


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            try {
                pause(barcodeScannerView);
                String code = Crypto.cripfyBarcode(result.getText());
                ResultadoLerBilhetesActivity_.intent(LerBilhetesActivity.this).codBarras(code).start();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ler_bilhetes);

        TextView textoLeitorCodBarras = findViewById(R.id.TextoLeitorCodBarras);
        textoLeitorCodBarras.setText("Posicione a linha abaixo sobre o código de barras\ne aguarde a focalização da câmera.");

        Button fecharLeitor = findViewById(R.id.fecharLeitorCodBarras);
        fecharLeitor.setOnClickListener(v -> finish());

        Button buttonDigitarAposta = findViewById(R.id.buttonDigitarAposta);
        buttonDigitarAposta.setOnClickListener(v -> {
            Intent activity = new Intent(LerBilhetesActivity.this, InsercaoManualCodBarrasActivity.class);
            startActivity(activity);
        });

        barcodeScannerView = findViewById(R.id.DecoratedBarcodeViewBilhetes);
        barcodeScannerView.getViewFinder().setVisibility(View.INVISIBLE);
        barcodeScannerView.getStatusView().setVisibility(View.INVISIBLE);
        barcodeScannerView.getBarcodeView().getCameraSettings().setFocusMode(CameraSettings.FocusMode.CONTINUOUS);
        barcodeScannerView.decodeContinuous(callback);

        buttonDigitarAposta.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_digite_aposta_realizada)));
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
