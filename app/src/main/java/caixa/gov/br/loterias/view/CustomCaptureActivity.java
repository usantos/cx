package caixa.gov.br.loterias.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.ViewUtils;

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
        imageFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(FECHAR);
                finish();
            }
        });

        buttomDigitarAposta = findViewById(R.id.buttomDigitarAposta);
        buttomDigitarAposta.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_digite_aposta_realizada)));
        buttomDigitarAposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(DIGITAR_APOSTA);
                finish();
            }
        });
    }
}
