package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;


public class LerBilhetesActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeScannerView;


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            captchaToken(result.getText());
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    protected void captchaToken(String resultBarcode) {

        //Porq não funciona aqui e funciona no oncreate de outra activity?
        SafetyNet.getClient(this).verifyWithRecaptcha("6LcOHZQUAAAAACNBYY7zprEoihu9ZyFHzYTHL8w9")
                .addOnSuccessListener(this,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                String userResponseToken = response.getTokenResult();
                                System.out.println("Token" + userResponseToken);

                                if (!userResponseToken.isEmpty()) {
                                    ResultadoLerBilhetesActivity_.intent(LerBilhetesActivity.this).codBarras(resultBarcode).start();
                                } else {
                                    System.out.println("Erro não autorizado");
                                }
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            System.out.println("Erro status code: " + statusCode);
                        } else {
                            System.out.println("Erro desconhecido");

                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ler_bilhetes);

        TextView textoLeitorCodBarras = (TextView) findViewById(R.id.TextoLeitorCodBarras);
        textoLeitorCodBarras.setText("Posicione a linha abaixo sobre o código de barras\ne aguarde a focalização da câmera.");

        Button fecharLeitor = (Button) findViewById(R.id.fecharLeitorCodBarras);
        fecharLeitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonDigitarAposta = (Button) findViewById(R.id.buttonDigitarAposta);
        buttonDigitarAposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(LerBilhetesActivity.this, InsercaoManualCodBarrasActivity.class);
                startActivity(activity);
            }
        });


        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.DecoratedBarcodeViewBilhetes);
        barcodeScannerView.getViewFinder().setVisibility(View.INVISIBLE);
        barcodeScannerView.getStatusView().setVisibility(View.INVISIBLE);
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

    public void pause() {
        barcodeScannerView.pause();
    }

    public void resume() {
        barcodeScannerView.resume();
    }

    public void triggerScan() {
        barcodeScannerView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


}
