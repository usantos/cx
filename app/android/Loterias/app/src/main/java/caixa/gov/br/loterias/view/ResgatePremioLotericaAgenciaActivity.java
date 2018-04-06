package caixa.gov.br.loterias.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.CodigoResgateDTO;
import caixa.gov.br.loterias.utils.ViewUtils;

@EActivity(R.layout.activity_resgate_premio_loterica_agencia)
public class ResgatePremioLotericaAgenciaActivity extends SettingToolbarActivity {

    @ViewById
    protected TextView categoriaText, informacaoTopicoUmText, informacaoTopicoDoisText, informacaoTopicoTresText;

    @ViewById
    protected ImageView qrCodeApostaImage;

    @Extra
    protected CodigoResgateDTO codigoResgateDTO;

    @AfterViews
    protected void init(){
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_resultado_ler_bilhetes)));

        if(codigoResgateDTO != null){
            categoriaText.setText(R.string.label_loterica_minusculo);
            informacaoTopicoUmText.setText(R.string.label_diriga_unidade_loterica_preferencia);
            informacaoTopicoDoisText.setText(R.string.label_apresente_qr_code_acima);
            informacaoTopicoTresText.setText(R.string.label_digite_cpf);

            if(StringUtils.isNotEmpty(codigoResgateDTO.getCodigo())){
                try {
                    generateQrCode();
                } catch (WriterException e) {
                    Log.e("TAG_RESGATE", e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateQrCode() throws WriterException {
        codigoResgateDTO.getCodigo();
        QRGEncoder qrgEncoder = new QRGEncoder(codigoResgateDTO.getCodigo(), null, QRGContents.Type.TEXT, ViewUtils.getSmallSizeQeCode(this));
        qrCodeApostaImage.setImageBitmap(qrgEncoder.encodeAsBitmap());
        qrCodeApostaImage.setVisibility(View.VISIBLE);
    }
}
