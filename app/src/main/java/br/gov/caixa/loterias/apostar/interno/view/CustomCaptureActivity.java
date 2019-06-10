package br.gov.caixa.loterias.apostar.interno.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class CustomCaptureActivity extends CaptureActivity{

    private ImageView imageFechar;
    private Button buttomDigitarAposta;
    public static final int FECHAR = 0;
    public static final int DIGITAR_APOSTA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initButtons();
    }

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_custom_capture);
        return (DecoratedBarcodeView)findViewById(R.id.decoratedBarcodeView);
    }

    private void initButtons(){
        imageFechar = findViewById(R.id.imageFechar);
        imageFechar.setOnClickListener(view -> {
            setResult(FECHAR);
            finish();
        });

        buttomDigitarAposta = findViewById(R.id.buttomDigitarAposta);
        buttomDigitarAposta.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_digite_aposta_realizada)));
        buttomDigitarAposta.setOnClickListener(view -> {
            setResult(DIGITAR_APOSTA);
            finish();
        });
    }
}
