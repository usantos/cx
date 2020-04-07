package br.gov.caixa.loterias.apostar.interno.controllers;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CanalEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CodigoResgateDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.TipoPagamentoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.PdfUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

@EActivity(R.layout.activity_resgate_premio_loterica_agencia)
public class ResgatePremioLotericaAgenciaActivity extends SettingToolbarActivity {

    @ViewById
    protected TextView categoriaText, informacaoTopicoUmText, informacaoTopicoDoisText, informacaoTopicoTresText, duploResgateText;

    @ViewById
    protected ImageView qrCodeApostaImage;

    @ViewById
    protected RelativeLayout duploResgateRelativeLayout, categoriaLayout;

    @ViewById
    protected Button buttonGerarPDF;

    @Extra
    protected CodigoResgateDTO codigoResgateDTO;

    @Extra
    protected String nsbi;

    @Extra
    protected Boolean isLoterica;

    @Extra
    protected ComprovanteApostaDTO comprovante;

    @Extra
    protected DetalhesPremioDTO detalhesPremioDTO;

    @Bean
    protected PdfUtils pdfUtils;

    @AfterViews
    protected void init() {
        atualizaLayout(isLoterica);

        adicionarListeners();
    }

    private void atualizaLayout(Boolean isLoterica) {
        if (isLoterica) {
            setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));

            categoriaLayout.setVisibility(View.VISIBLE);
            categoriaText.setText(R.string.label_loterica_minusculo);
            buttonGerarPDF.setVisibility(View.GONE);
            duploResgateRelativeLayout.setVisibility(View.GONE);
            informacaoTopicoUmText.setText(R.string.label_diriga_unidade_loterica_preferencia);
            informacaoTopicoDoisText.setText(R.string.label_apresente_qr_code_acima);
            informacaoTopicoTresText.setText(R.string.label_digite_cpf);

            if (StringUtils.isNotEmpty(codigoResgateDTO.getQrCode())) {
                try {
                    generateQrCode();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        } else {
            setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.label_agencia_bold)));

            categoriaLayout.setVisibility(View.GONE);

            for (TipoPagamentoDTO meioPagamento: detalhesPremioDTO.getMeiosResgate()) {
                if (meioPagamento.getCanal() == CanalEnum.SISPL) {
                    duploResgateRelativeLayout.setVisibility(View.VISIBLE);
                    duploResgateText.setText(R.string.label_resgatar_premio_loterica);
                }
            }

            informacaoTopicoUmText.setText(R.string.label_emitir_comprovante);
            informacaoTopicoDoisText.setText(R.string.label_dirigir_para_agencia);
            informacaoTopicoTresText.setText(R.string.label_apresente_documento_identificacao_original);
        }
    }

    private void adicionarListeners() {
        buttonGerarPDF.setOnClickListener(view -> {
            pdfUtils.createComprovanteApostaPdf(comprovante, ResgatePremioLotericaAgenciaActivity.this);
        });

        duploResgateRelativeLayout.setOnClickListener(view -> {
            atualizaLayout(true);
        });
    }

    private void generateQrCode() throws WriterException {
        QRGEncoder qrgEncoder = new QRGEncoder(codigoResgateDTO.getQrCode(), null, QRGContents.Type.TEXT, ViewUtils.getSmallSizeQrCode(this));
        qrCodeApostaImage.setImageBitmap(qrgEncoder.encodeAsBitmap());
        qrCodeApostaImage.setVisibility(View.VISIBLE);
    }
}
