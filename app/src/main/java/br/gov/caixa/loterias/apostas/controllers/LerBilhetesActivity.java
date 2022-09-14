package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LerQrCodeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Crypto;
import br.gov.caixa.loterias.apostas.model.enums.LeituraBilheteEnum;
import br.gov.caixa.loterias.apostas.utils.BilheteUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;


public class LerBilhetesActivity extends AppCompatActivity {

    private static int INTENT_RESULT = 123;

    private boolean isActivityRunning;
    private boolean isScanned;
//    private boolean isRunning10Seg;
    private boolean isPaused;

//    private Handler handler10Sec;

    private DecoratedBarcodeView barcodeScannerView;

    private boolean chamouresultado;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (!isPaused){
                try {
                    isScanned = true;
                    pause(barcodeScannerView);
                    if (result.getText().length() >= 23){
                        String             code, nsbFormatado;
                        LeituraBilheteEnum tipo;
                        try {
                            LerQrCodeDTO lerQrCodeDTO = new Gson().fromJson(result.getText().replace("APOSTA", ""), LerQrCodeDTO.class);

                            if (lerQrCodeDTO.getCf() == null || lerQrCodeDTO.getCf().isEmpty()){
                                lerQrCodeDTO.setCf(lerQrCodeDTO.getCi());
                            }

                            //Cria a string com o formato desejado e depois criptografa
                            String envio = lerQrCodeDTO.getNsb()+"=="+
                                    lerQrCodeDTO.getCi()+"=="+
                                    lerQrCodeDTO.getPro()+"=="+
                                    ModalidadeEnum.fromInteger(Integer.parseInt(lerQrCodeDTO.getPro()))+"=="
                                    +lerQrCodeDTO.getCf();

                            code = Crypto.cripfyBarcode(envio);
                            nsbFormatado = BilheteUtils.getNSBFormatado(lerQrCodeDTO.getNsb());
                            tipo = LeituraBilheteEnum.QR_CODE;
                        }catch (Exception e){
                            nsbFormatado = BilheteUtils.getNSBFormatado(result.getText());
                            code = Crypto.cripfyBarcode(result.getText());
                            tipo = LeituraBilheteEnum.CODIGO_BARRAS;
                        }

                        chamouresultado = true;
//                        if (handler10Sec != null){
//                            handler10Sec.removeCallbacksAndMessages(null);
//                        }
//                        isRunning10Seg = false;
                        isScanned = false;
                        ResultadoLerBilhetesActivity_.intent(LerBilhetesActivity.this)
                                                     .codBarras(code).nsb(nsbFormatado)
                                                     .tipoLeitura(tipo)
                                                     .startForResult(INTENT_RESULT);
                    } else {
                        isScanned = false;
                        resume(barcodeScannerView);
                        chamouresultado = false;
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
            //startBilheteManual();
        });

        barcodeScannerView = findViewById(R.id.DecoratedBarcodeViewBilhetes);
        barcodeScannerView.getViewFinder().setVisibility(View.INVISIBLE);
        barcodeScannerView.getStatusView().setVisibility(View.INVISIBLE);
        barcodeScannerView.getBarcodeView().getCameraSettings().setFocusMode(CameraSettings.FocusMode.CONTINUOUS);
        barcodeScannerView.decodeContinuous(callback);

        buttonDigitarAposta.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_digite_aposta_realizada)));
    }

//    private void startDezSegundos() {
//        if (!isRunning10Seg){
//            handler10Sec = new Handler();
//            handler10Sec.postDelayed((Runnable) () -> {
//                isRunning10Seg = false;
//                if (isActivityRunning) {
//                    pause(barcodeScannerView);
//                    showAlert();
//                } else {
//                    new Handler().postDelayed((Runnable) () -> {
//                        isRunning10Seg = false;
//                        startDezSegundos();
//                    }, 5000);
//                    isRunning10Seg = true;
//                }
//
//            }, 10000);
//            isRunning10Seg = true;
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chamouresultado = false;
    }

//    private void showAlert() {
//        if (isActivityRunning){
//            if (!isScanned && !chamouresultado){
//                try {
//                    ViewUtils.alertTitleButtonYesOrNotListener(LerBilhetesActivity.this,
//                            getString(R.string.nao_foi_possivel_realizar_leitura),
//                            getString(R.string.tentar_novamente),
//                            (dialog, which) -> {
//                                resume(barcodeScannerView);
//                                startDezSegundos();
//                            },
//                            getString(R.string.digitar_codigo),
//                            (dialog, which) -> {
//                                startBilheteManual();
//                            });
//                } catch (Exception e){
//                    Log.e("", e.getLocalizedMessage());
//                }
//            } else {
//                resume(barcodeScannerView);
//                startDezSegundos();
//            }
//        }
//    }

    private void startBilheteManual() {
//        Intent activity = new Intent(LerBilhetesActivity.this, InsercaoManualCodBarrasActivity.class);
//        startActivity(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume(barcodeScannerView);
//        startDezSegundos();
        isScanned = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause(barcodeScannerView);
    }

    public void pause(View view) {
        barcodeScannerView.pause();
        isPaused = true;
    }

    public void resume(View view) {
        barcodeScannerView.resume();
        isPaused = false;
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
