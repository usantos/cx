package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CanalEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoPagamentoDTO;
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ResgatePremioLotericaAgenciaActivity extends SettingToolbarActivity {

    public final static String CODIGO_RESGATE_DTO_EXTRA = "codigoResgateDTO";
    public final static String NSBI_EXTRA = "nsbi";
    public final static String IS_LOTERICA_EXTRA = "isLoterica";
    public final static String COMPROVANTE_EXTRA = "comprovante";
    public final static String DETALHES_PREMIO_DTO_EXTRA = "detalhesPremioDTO";

    private TextView categoriaText, informacaoTopicoUmText, informacaoTopicoDoisText, informacaoTopicoTresText, duploResgateText;
    private ImageView qrCodeApostaImage;
    private RelativeLayout duploResgateRelativeLayout, categoriaLayout;
    private Button buttonGerarPDF;
    private CodigoResgateDTO codigoResgateDTO;
    private String nsbi;
    private Boolean isLoterica;
    private ComprovanteApostaDTO comprovante;
    private DetalhesPremioDTO detalhesPremioDTO;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgate_premio_loterica_agencia);
        init();
    }

    protected void init() {
        getExtras();
        setViews();
        atualizaLayout(isLoterica);
        adicionarListeners();
    }

    private void getExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(CODIGO_RESGATE_DTO_EXTRA)) {
                this.codigoResgateDTO = ((CodigoResgateDTO) extras_.getSerializable(CODIGO_RESGATE_DTO_EXTRA));
            }
            if (extras_.containsKey(NSBI_EXTRA)) {
                this.nsbi = extras_.getString(NSBI_EXTRA);
            }
            if (extras_.containsKey(IS_LOTERICA_EXTRA)) {
                this.isLoterica = ((Boolean) extras_.getSerializable(IS_LOTERICA_EXTRA));
            }
            if (extras_.containsKey(COMPROVANTE_EXTRA)) {
                this.comprovante = ((ComprovanteApostaDTO) extras_.getSerializable(COMPROVANTE_EXTRA));
            }
            if (extras_.containsKey(DETALHES_PREMIO_DTO_EXTRA)) {
                this.detalhesPremioDTO = ((DetalhesPremioDTO) extras_.getSerializable(DETALHES_PREMIO_DTO_EXTRA));
            }
        }
    }

    private void setViews() {
        this.categoriaText = findViewById(R.id.categoriaText);
        this.informacaoTopicoUmText = findViewById(R.id.informacaoTopicoUmText);
        this.informacaoTopicoDoisText = findViewById(R.id.informacaoTopicoDoisText);
        this.informacaoTopicoTresText = findViewById(R.id.informacaoTopicoTresText);
        this.duploResgateText = findViewById(R.id.duploResgateText);
        this.qrCodeApostaImage = findViewById(R.id.qrCodeApostaImage);
        this.duploResgateRelativeLayout = findViewById(R.id.duploResgateRelativeLayout);
        this.categoriaLayout = findViewById(R.id.categoriaLayout);
        this.buttonGerarPDF = findViewById(R.id.buttonGerarPDF);
    }

    private void atualizaLayout(Boolean isLoterica) {
        if (isLoterica) {
            Intent it = new Intent(ResgatePremioLotericaAgenciaActivity.this, ResgatePremioLotericaActivity.class);
            it.putExtra("DETALHES", detalhesPremioDTO);
            it.putExtra("COMPROVANTE", comprovante);
            it.putExtra("CODIGO_RESGATE", codigoResgateDTO);
            startActivity(new Intent(it));
        } else {
            setTitle(ViewUtils.textCaixaSTDBold(this, getString(R.string.label_agencia_bold)));
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
            PdfUtils.createComprovanteApostaPdf(comprovante, ResgatePremioLotericaAgenciaActivity.this);
        });

        duploResgateRelativeLayout.setOnClickListener(view -> {
            atualizaLayout(true);
        });
    }
}
