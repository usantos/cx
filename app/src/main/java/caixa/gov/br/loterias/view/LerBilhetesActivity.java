package caixa.gov.br.loterias.view;

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

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.ViewUtils;


public class LerBilhetesActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeScannerView;


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            ResultadoLerBilhetesActivity_.intent(LerBilhetesActivity.this).codBarras(result.getText()).start();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

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
