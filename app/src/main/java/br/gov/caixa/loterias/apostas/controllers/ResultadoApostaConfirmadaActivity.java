package br.gov.caixa.loterias.apostas.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
//import br.gov.caixa.loterias.apostas.ResgatePixActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoPagamentoDTO;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtil;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FaixaPremiacaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FaixaPremiacaoBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ContentResultadoPremiado;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class ResultadoApostaConfirmadaActivity extends SettingToolbarActivity {

    public final static String DETALHES_PREMIO_DTO_EXTRA = "detalhesPremioDTO";
    public final static String COMPROVANTE_EXTRA = "comprovante";

    public static String DETALHES = "DETALHES";
    public static String COMPROVANTE = "COMPROVANTE";
    public static String CODIGO_RESGATE = "CODIGO_RESGATE";
    public static int RESGATOU_MERCADO_PAGO = 200;
    private Toolbar toolbar;
    private ContentResultadoPremiado contentResultadoPremiado;
    private RelativeLayout lotericaLayout, agenciaCaixaLayout, mercadoPagoLayout, pixLayout;
    private TextView mercadoPagoText, agenciaCaixaText, lotericaText;
    private ConstraintLayout constraintLayoutPix, constraintAvisoResgate;
    private DetalhesPremioDTO detalhesPremioDTO;

    private Button botaoAposteAgora;

    private AlertDialog alertDialog;

    ModalidadeEnum modalidadeEnum;

    protected ComprovanteApostaDTO comprovante;
    private EstiloModalidade estiloModalidade;
    private EstiloModalidadeMKP estiloModalidadeMKP;

    private TextView concursoTextView, situacaoTextView, faixaPremiacaoTextView, quantidadePremiacaoTextView, sorteioTextView, premiacaoTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_aposta_confirmada);
        init();
    }

    protected void init() {
        getExtras();
        setViews();

        createToolBar();

        modalidadeEnum = detalhesPremioDTO.getAposta().getModalidade();
        estiloModalidadeMKP = new EstiloModalidadeMKP(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);

        contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));
        contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidadeMKP.getTrapezio()));
        contentResultadoPremiado.getImgTrevoResultado().setImageResource(estiloModalidadeMKP.getTrevoFundoEscuro());

        if (isBolao(detalhesPremioDTO.getAposta())) {
            contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));
            contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidadeMKP.getTrapezio()));
            contentResultadoPremiado.getImgTrevoResultado().setImageDrawable(VectorUtils.getShape(R.drawable.ic_money_bolao, estiloModalidadeMKP.getCorFonteFundoEscuro()));
            contentResultadoPremiado.getImgTagBolao().setVisibility(View.VISIBLE);
            contentResultadoPremiado.getImgTagBolao().setImageDrawable(getDrawable(estiloModalidadeMKP.getImagemTrianguloOutline()));
            contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));
        }

        //cores
        contentResultadoPremiado.getTituloModalidadeTextView().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
        contentResultadoPremiado.getPodeComemorar().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
        contentResultadoPremiado.getValorPremioTituloTextView().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
        contentResultadoPremiado.getTopoResultadoPremioLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));
        contentResultadoPremiado.getPremioGanhoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));

        //contentResultadoPremiado.getPremioInfoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contentResultadoPremiado.getPremioInfoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorEscura()));

        contentResultadoPremiado.getTituloModalidadeTextView().setText(ViewUtils.getNomeModalidadePorAposta(detalhesPremioDTO.getAposta()).toLowerCase());

        contentResultadoPremiado.getButtonVerDetalheAposta().setOnClickListener(v -> finish());

        if (detalhesPremioDTO.getPremio().getValorLiquido() != null && !(BigDecimal.ZERO.compareTo(detalhesPremioDTO.getPremio().getValorLiquido()) == 0)) {
            contentResultadoPremiado.getValorPremioTituloTextView().setText( getResources().getString(R.string.label_voce_foi_premiado).replace(getResources().getString(R.string.chave_valor_premio_chave), ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido())));
        }
        else {
            contentResultadoPremiado.getValorPremioTituloTextView().setText(R.string.label_voce_foi_premiado_exclamacao);
        }

        contentResultadoPremiado.getBotaoAposteAgora().setText(ViewUtils.textFuturaAndFuturaBold(this ,getString(R.string.botao_aposte_agora)));

        if (parametroSimulacao != null) {

            for (ParametroSimulacao parametro : sessaoUsuario.getParametrosSimulacao().getParametros()) {

                if (parametro.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum && parametro.getProximoConcurso() != null) {

                    parametroSimulacao.setProximoConcurso(parametro.getProximoConcurso());
                }
            }

            if (parametroSimulacao.getProximoConcurso() != null) {

                contentResultadoPremiado.getProximoSorteioTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataFechamento()));

                contentResultadoPremiado.getValorEstimativaConcursoAtualTextView().setText(ViewUtils.getMoedaFormat(parametroSimulacao.getParametroJogo().getConcurso().getEstimativa() ));
            }

        }

        if (isBolao(detalhesPremioDTO.getAposta())){
            contentResultadoPremiado.setCabecalhoBolao();
            contentResultadoPremiado.getTvFaixaPremiacao().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
            contentResultadoPremiado.getTvValorPremioCota().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
            contentResultadoPremiado.getTvValorPremioLiquido().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
        }

        createListPremiacao();
        trataFormasDeResgate();
        RateUtils.avaliarAplicativo(this, true);
        setResult(RESULT_FIRST_USER);
    }

    private void getExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(DETALHES_PREMIO_DTO_EXTRA)) {
                this.detalhesPremioDTO = ((DetalhesPremioDTO) extras_.getSerializable(DETALHES_PREMIO_DTO_EXTRA));
            }
            if (extras_.containsKey(COMPROVANTE_EXTRA)) {
                this.comprovante = ((ComprovanteApostaDTO) extras_.getSerializable(COMPROVANTE_EXTRA));
            }
        }
    }

    private void setViews() {
        this.toolbar = findViewById(R.id.toolbar);
        this.contentResultadoPremiado = findViewById(R.id.contentResultadoPremiado);
        this.lotericaLayout = findViewById(R.id.lotericaLayout);
        this.agenciaCaixaLayout = findViewById(R.id.agenciaCaixaLayout);
        this.mercadoPagoLayout = findViewById(R.id.mercadoPagoLayout);
        this.pixLayout = findViewById(R.id.pixLayout);
        this.mercadoPagoText = findViewById(R.id.mercadoPagoText);
        this.agenciaCaixaText = findViewById(R.id.agenciaCaixaText);
        this.lotericaText = findViewById(R.id.lotericaText);
        this.constraintLayoutPix = findViewById(R.id.constraintLayoutPix);
        this.constraintAvisoResgate = findViewById(R.id.constraintAvisoResgate);
        this.botaoAposteAgora = findViewById(R.id.botaoAposteAgora);

        this.concursoTextView = findViewById(R.id.concursoTextView);
        this.situacaoTextView = findViewById(R.id.situacaoTextView);
        this.faixaPremiacaoTextView = findViewById(R.id.faixaPremiacaoTextView);
        this.quantidadePremiacaoTextView = findViewById(R.id.quantidadePremiacaoTextView);
        this.sorteioTextView = findViewById(R.id.sorteioTextView);
        this.premiacaoTextView = findViewById(R.id.premiacaoTextView);

        lotericaLayout.setOnClickListener(view -> clickLotericaLayout());

        botaoAposteAgora.setOnClickListener(view -> clickBotaoAposteAgora());

        agenciaCaixaLayout.setOnClickListener(view -> clickAgenciaCaixaLayout());

        pixLayout.setOnClickListener(view -> clickPixLayout());

        mercadoPagoLayout.setOnClickListener(view -> clickMercadoPagoLayout());
    }

    protected void clickLotericaLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lotericaText.setBackgroundColor(getColor(R.color.azul_botao_pix));
        }
        apresentaOrientacoesResgate(true);
    }

    protected void apresentaOrientacoesResgate(Boolean isLoterica) {
        alertDialog = LoadingViewLoterias.show(getContext());
        ApostaBO.getInstance().getApostaGerarCodigoResgate(detalhesPremioDTO.getAposta().getId(), new RequestListener<CodigoResgateDTOResponse>() {
            @Override
            public void onResponse(CodigoResgateDTOResponse result) {
                alertDialog.dismiss();
                if(isLoterica){
                    Intent it = new Intent(ResultadoApostaConfirmadaActivity.this, ResgatePremioLotericaActivity.class);
                    it.putExtra(DETALHES, detalhesPremioDTO);
                    it.putExtra(COMPROVANTE, comprovante);
                    it.putExtra(CODIGO_RESGATE, result.getPayload());
                    startActivity(new Intent(it));
                }else {
                    //ResgatePremioLotericaAgenciaActivity_.intent(getContext()).codigoResgateDTO(result.getPayload()).isLoterica(isLoterica).detalhesPremioDTO(detalhesPremioDTO).comprovante(comprovante).start();
                    Intent intent = IntentUtil.getIntentOrigemDestino((Activity) getContext(), ResgatePremioLotericaAgenciaActivity.class);
                    intent.putExtra(ResgatePremioLotericaAgenciaActivity.CODIGO_RESGATE_DTO_EXTRA, ((Serializable) result.getPayload()));
                    intent.putExtra(ResgatePremioLotericaAgenciaActivity.IS_LOTERICA_EXTRA, ((Serializable) isLoterica));
                    intent.putExtra(ResgatePremioLotericaAgenciaActivity.DETALHES_PREMIO_DTO_EXTRA, ((Serializable) detalhesPremioDTO));
                    intent.putExtra(ResgatePremioLotericaAgenciaActivity.COMPROVANTE_EXTRA, ((Serializable) comprovante));
                    ((Activity) getContext()).startActivity(intent);
                }

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                String msgError = MensagensNetwork.setMensagem(error, ResultadoApostaConfirmadaActivity.this);
                ViewUtils.alertTitleButton(getContext(), R.string.label_atencao, msgError, getContext().getResources().getString( R.string.ok ));
            }
        });
    }

    private void apresentaInfoResgateIR(){
        constraintAvisoResgate.setVisibility(View.VISIBLE);
    }

    protected void clickBotaoAposteAgora() {

        Intent it = new Intent(ResultadoApostaConfirmadaActivity.this, PrincipalActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        it.putExtra(getResources().getString(R.string.extra_modalidade_apostar_agora), modalidadeEnum);
        startActivity(it);
    }

    protected void clickAgenciaCaixaLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            agenciaCaixaText.setBackgroundColor(getColor(R.color.azul_botao_pix));
        }
        apresentaOrientacoesResgate(false);
    }

    protected void clickPixLayout() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            constraintLayoutPix.setBackgroundColor(getColor(R.color.azul_botao_pix));
//        }
//        Intent intent = new Intent( ResultadoApostaConfirmadaActivity.this, ResgatePixActivity.class);
//        startActivity(intent);
    }

    protected void clickMercadoPagoLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mercadoPagoText.setBackgroundColor(getColor(R.color.azul_botao_pix));
        }
        Intent intent = new Intent( ResultadoApostaConfirmadaActivity.this, JogosConfirmadosPremioMercadoPagoActivity.class);
        intent.putExtra( getResources().getString(R.string.extra_valor_premio), ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()) );
        intent.putExtra( getResources().getString(R.string.extra_id_aposta), detalhesPremioDTO.getAposta().getId() );
        startActivityForResult(intent,RESGATOU_MERCADO_PAGO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESGATOU_MERCADO_PAGO && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    private void trataFormasDeResgate() {
        for (TipoPagamentoDTO meioPagamento: detalhesPremioDTO.getMeiosResgate()) {
            switch (meioPagamento.getCanal()) {
                case SILCE:
                    if (meioPagamento.getId().intValue() == MeioPagamentoUtil.MERCADO_PAGO.intValue())
                        mercadoPagoLayout.setVisibility(View.VISIBLE);
                    //Bloqueado para garantir que não chame o resgate PIX
                    //if (meioPagamento.getId().intValue() == MeioPagamentoUtil.PIX.intValue())
                    //    pixLayout.setVisibility(View.VISIBLE);
                    break;
                case SISPL:
                    lotericaLayout.setVisibility(View.VISIBLE);
                    break;
                case SIGEL:
                    agenciaCaixaLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
        if (detalhesPremioDTO.getMensagemInformativaIR() != null && !detalhesPremioDTO.getMensagemInformativaIR().isEmpty()){
            apresentaInfoResgateIR();
        }
    }

    private boolean isBolao(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta != null && aposta.getIndicadorCotaBolao() != null && aposta.getIndicadorCotaBolao();
    }

    private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contentResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

        if (isBolao(detalhesPremioDTO.getAposta())){
            listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoBolaoAdapter(ViewUtils.getFaixaPremiacaoDTO(detalhesPremioDTO.getPremio()), ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()), ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()), detalhesPremioDTO.getPremio().getValorLiquido()));
        } else {

            concursoTextView.setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
            situacaoTextView.setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
            faixaPremiacaoTextView.setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
            quantidadePremiacaoTextView.setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
            sorteioTextView.setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
            premiacaoTextView.setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));

            //listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(detalhesPremioDTO.getPremio()), ContextCompat.getColor(this, estiloModalidade.getCorEscura()), ContextCompat.getColor(this, estiloModalidade.getCorEscura())));
            //listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(detalhesPremioDTO.getPremio()), ContextCompat.getColor(this, estiloModalidade.getCorEscura()), ContextCompat.getColor(this, estiloModalidadeMKP.getCorEscura())));
            listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(detalhesPremioDTO.getPremio()), ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()), ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro())));
        }
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createToolBar() {
        setTitle(ViewUtils.textCaixaSTDBold(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
    }

    private Context getContext() {
        return ResultadoApostaConfirmadaActivity.this;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            constraintLayoutPix.setBackgroundColor(getColor(R.color.verdeazul));
            mercadoPagoText.setBackgroundColor(getColor(R.color.verdeazul));
            agenciaCaixaText.setBackgroundColor(getColor(R.color.verdeazul));
            lotericaText.setBackgroundColor(getColor(R.color.verdeazul));
        }

    }

}
