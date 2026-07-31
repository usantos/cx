package br.gov.caixa.loterias.apostas.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.ResgatePixActivity;
import br.gov.caixa.loterias.apostas.model.bean.OpcaoResgatePremio;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtil;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FaixaPremiacaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FaixaPremiacaoBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.OpcaoResgatePremioAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ContentResultadoPremiado;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;

public class ResultadoApostaConfirmadaActivity extends SettingToolbarActivity {
    public final static String DETALHES_PREMIO_DTO_EXTRA = "detalhesPremioDTO";
    public final static String COMPROVANTE_EXTRA = "comprovante";

    public static String DETALHES = "DETALHES";
    public static String COMPROVANTE = "COMPROVANTE";
    public static String CODIGO_RESGATE = "CODIGO_RESGATE";
    public static int RESGATOU_MERCADO_PAGO = 200;
    private Toolbar toolbar;
    private ContentResultadoPremiado contentResultadoPremiado;
    private ConstraintLayout constraintAvisoResgate;
    private RecyclerView listaOpcaoResgate;
    private DetalhesPremioDTO detalhesPremioDTO;

    private Button botaoAposteAgora;

    private AlertDialog alertDialog;

    ModalidadeEnum modalidadeEnum;

    protected ComprovanteApostaDTO comprovante;
    private EstiloModalidadeMKP estiloModalidadeMKP;

    private TextView concursoTextView, situacaoTextView, faixaPremiacaoTextView, quantidadePremiacaoTextView, sorteioTextView, premiacaoTextView;

    private boolean isEspeial;

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

        montaTipoApostaLinha(detalhesPremioDTO.getAposta(), estiloModalidadeMKP.getCorEscura(),
                estiloModalidadeMKP.getCorFonteFundoEscuro(), estiloModalidadeMKP.getCorFonteFundoEscuro());

        if (isEspeial() && estiloModalidadeMKP.getImagemEspecialSimples() > 0) {
            contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidadeMKP.getImagemEspecialSimples()));
        } else {
            contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));
        }

        contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidadeMKP.getTrapezio()));
        contentResultadoPremiado.getImgTrevoResultado().setImageResource(estiloModalidadeMKP.getTrevoFundoEscuro());

        if (isBolao(detalhesPremioDTO.getAposta())) {
            //contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));
            contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidadeMKP.getTrapezio()));
            contentResultadoPremiado.getImgTrevoResultado().setImageResource(estiloModalidadeMKP.getTrevoFundoEscuro());
            //contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));
        }

        //cores
        contentResultadoPremiado.getTituloModalidadeTextView().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
        contentResultadoPremiado.getPodeComemorar().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
        contentResultadoPremiado.getValorPremioTituloTextView().setTextColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorFonteFundoClaro()));
        contentResultadoPremiado.getTopoResultadoPremioLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));
        contentResultadoPremiado.getPremioGanhoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorClara()));

        //contentResultadoPremiado.getPremioInfoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contentResultadoPremiado.getPremioInfoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidadeMKP.getCorEscura()));
        String descricaoModalidade = "";
        if (EspecialUtils.isMega30(detalhesPremioDTO.getAposta().getModalidade(), detalhesPremioDTO.getAposta().getConcursoAlvo(), detalhesPremioDTO.getAposta().getTipoConcurso())) {
            descricaoModalidade = SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_LINHA, "");
            contentResultadoPremiado.getTituloModalidadeTextView().setTextSize(20);
        } else if (EspecialUtils.isLotecaPais(detalhesPremioDTO.getAposta().getModalidade(), detalhesPremioDTO.getAposta().getConcursoAlvo(), detalhesPremioDTO.getAposta().getTipoConcurso())) {
            descricaoModalidade = SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_LINHA, "");
            contentResultadoPremiado.getTituloModalidadeTextView().setTextSize(20);
        } else {
            descricaoModalidade = ViewUtils.getNomeModalidadePorAposta(detalhesPremioDTO.getAposta()).toLowerCase();
        }
        contentResultadoPremiado.getTituloModalidadeTextView().setText(descricaoModalidade);
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
        //RateUtils.avaliarAplicativo(this, true);
        setResult(RESULT_FIRST_USER);
    }

    private boolean isEspeial() {
        return (detalhesPremioDTO.getAposta().getTipoConcurso().getValor().equalsIgnoreCase("2"));
    }

    private void montaTipoApostaLinha(IdentificaoDeUmaApostaDas8Modalidades aposta,
                                      int backGroundColorRes, int textColorRes,
                                      int iconColorRes) {

        List<TipoApostaLinhaEnum> listTipoApostaLinha = new ArrayList<>();

        if (aposta.getTroca()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.TROCA);
        }
        if (aposta.getIndicadorCotaBolao()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.BOLAO);
        }
        if (aposta.getCombo()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.COMBO);
        }
        if (aposta.getEspelho()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.ESPELHO);
        }
        if (aposta.getSurpresinha()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.SURPRESINHA);
        }
        if (aposta.getQuantidadeTeimosinhas() > 0) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.TEIMOSINHA);
        }

        if (listTipoApostaLinha.size() > 0) {
            contentResultadoPremiado.getTipoApostaLinhaView().setVisibility(View.VISIBLE);
            contentResultadoPremiado.getTipoApostaLinhaView().setupView(listTipoApostaLinha,
                    backGroundColorRes, textColorRes, iconColorRes,
                    TipoApostaLinhaView.TriangleDirection.NONE);
        } else {
            contentResultadoPremiado.getTipoApostaLinhaView().setVisibility(View.GONE);
        }
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
        this.constraintAvisoResgate = findViewById(R.id.constraintAvisoResgate);
        this.botaoAposteAgora = findViewById(R.id.botaoAposteAgora);
        this.concursoTextView = findViewById(R.id.concursoTextView);
        this.situacaoTextView = findViewById(R.id.situacaoTextView);
        this.faixaPremiacaoTextView = findViewById(R.id.faixaPremiacaoTextView);
        this.quantidadePremiacaoTextView = findViewById(R.id.quantidadePremiacaoTextView);
        this.sorteioTextView = findViewById(R.id.sorteioTextView);
        this.premiacaoTextView = findViewById(R.id.premiacaoTextView);
        this.listaOpcaoResgate = findViewById(R.id.listaOpcaoResgate);

        botaoAposteAgora.setOnClickListener(view -> clickBotaoAposteAgora());
    }

    protected void apresentaOrientacoesResgate(Boolean isLoterica) {
        alertDialog = LoadingViewLoterias.show(getContext());
        ApostaSilceBO.getInstance().getApostaGerarCodigoResgate(detalhesPremioDTO.getAposta().getId(), new RequestListener<CodigoResgateDTOResponse>() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESGATOU_MERCADO_PAGO && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    private void trataFormasDeResgate() {
        ArrayList<OpcaoResgatePremio> list = new ArrayList<>();
        for (int i = 0; i < detalhesPremioDTO.getMeiosResgate().size(); i++){
            TipoPagamentoDTO meioPagamento = detalhesPremioDTO.getMeiosResgate().get(i);
            if (meioPagamento.getId().equals(MeioPagamentoUtil.PIX)) {
                list.add(new OpcaoResgatePremio(getDrawable(R.drawable.icone_do_pix), getString(R.string.label_pix),
                        getString(R.string.receba_na_hora), v -> {
                    Intent intent = new Intent( ResultadoApostaConfirmadaActivity.this, ResgatePixActivity.class);
                    intent.putExtra(ResgatePixActivity.ARG_VALOR, detalhesPremioDTO.getPremio().getValorLiquido());
                    intent.putExtra(ResgatePixActivity.ARG_ID_APOSTA, detalhesPremioDTO.getAposta().getId());
                    startActivity(intent);
                }));
            } else if (meioPagamento.getId().equals(MeioPagamentoUtil.MERCADO_PAGO)) {
                list.add(new OpcaoResgatePremio(null, getString(R.string.label_mercado_pago),
                        getString(R.string.receba_na_hora),v -> {
                    Intent intent = new Intent( ResultadoApostaConfirmadaActivity.this, JogosConfirmadosPremioMercadoPagoActivity.class);
                    intent.putExtra( getResources().getString(R.string.extra_valor_premio), ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()) );
                    intent.putExtra( getResources().getString(R.string.extra_id_aposta), detalhesPremioDTO.getAposta().getId() );
                    startActivityForResult(intent,RESGATOU_MERCADO_PAGO);
                }));
            } else if (meioPagamento.getId().equals(MeioPagamentoUtil.AGENCIA)) {
                list.add(new OpcaoResgatePremio(null, getString(R.string.label_agencia_maiuscula),
                        getString(R.string.valores_isencao_ir),v -> {
                    apresentaOrientacoesResgate(false);
                }));
            } else if (meioPagamento.getId().equals(MeioPagamentoUtil.LOTERICA)) {
                list.add(new OpcaoResgatePremio(null, getString(R.string.label_loterica),
                        getString(R.string.retire_qualquer_loterica),v -> {
                    apresentaOrientacoesResgate(true);
                }));
            }
        }

        listaOpcaoResgate.setAdapter(new OpcaoResgatePremioAdapter(list));
        listaOpcaoResgate.setLayoutManager(new LinearLayoutManager(this));

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

}
